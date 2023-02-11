package com.example.elchino;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.elchino.Clases_comunes.*;
import com.example.elchino.Util.*;
import com.google.android.material.textfield.TextInputLayout;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Registrar_cliente_nuevoActivity extends AppCompatActivity {

    TextInputLayout nombre_cliente,telefono2_cliente,telefono1_cliente,apellido1_cliente,apellido2_cliente,apodo_cliente,notas_cliente,direccion_cliente,monto_disponible,ID_cliente;
    private String u0,u1,u2,u3,u4,u6,u9,u10,u12,u13;//u0: cedula, u1: nombre, u2: apellido, u4: Apodo
    private boolean flag_u0 = false;
    private boolean flag_u1 = false;
    private boolean flag_u2 = false;
    private boolean flag_u3 = false;
    private boolean flag_u4 = false;
    private boolean flag_u6 = false;
    private boolean flag_u9 = false;
    private boolean flag_u10 = false;
    private boolean flag_u12 = false;
    private boolean flag_u13 = false;
    private String nombre_clienteS = "";
    private String telefono1_clienteS = "";
    private String telefono2_clienteS = "";
    private String apellido1_clienteS = "";
    private String apellido2_clienteS = "";
    private String apodo_clienteS = "";
    private String notas_clienteS = "";
    private String direccion_clienteS = "";
    private String monto_disponibleS = "";
    private String ID_clienteS = "";
    private String file_content = "";
    private EditText et_ID;
    private TextView tv_esperar;
    private String hora;
    private String minuto;
    private String anio;
    private String mes;
    private String dia;
    private Button confirmar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cliente_nuevo);
        ID_cliente = (TextInputLayout) findViewById(R.id.ID_cliente);
        nombre_cliente = (TextInputLayout) findViewById(R.id.nombre_cliente);
        apellido1_cliente = (TextInputLayout) findViewById(R.id.apellido1_cliente);
        apellido2_cliente = (TextInputLayout) findViewById(R.id.apellido2_cliente);
        telefono1_cliente = (TextInputLayout) findViewById(R.id.telefono1_cliente);
        telefono2_cliente = (TextInputLayout) findViewById(R.id.telefono2_cliente);
        apodo_cliente = (TextInputLayout) findViewById(R.id.apodo_cliente);
        notas_cliente = (TextInputLayout) findViewById(R.id.notas_cliente);
        direccion_cliente = (TextInputLayout) findViewById(R.id.direccion_cliente);
        monto_disponible = (TextInputLayout) findViewById(R.id.monto_disponible);
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        confirmar = (Button) findViewById(R.id.bt_confirmar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        et_ID.setVisibility(View.INVISIBLE);
        separarFecha();
    }

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(null);
        hora = datosFecha.getHora();
        minuto = datosFecha.getMinuto();
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        dia = datosFecha.getDia();
    }

    public void confirm(View view) {
        confirmar.setClickable(false);
        confirmar.setEnabled(false);
        boolean asdf0 = ID_cliente();
        boolean asdf1 = nombre_cliente();
        boolean asdf2 = apellido1_cliente();
        boolean asdf3 = apellido2_cliente();
        boolean asdf4 = apodo_cliente();
        boolean asdf5 = notas_cliente();
        boolean asdf6 = direccion_cliente();
        boolean asdf10 = monto_disponible();
        boolean asdf12 = telefono1_cliente();
        boolean asdf13 = telefono2_cliente();
        Log.v("confirm_2", "Registrar_cliente_nuevo.\n\n" + asdf0 + ", " +
                asdf1 + ", " + asdf2 + ", " + asdf3 + ", " + asdf4 + ", " +
                asdf5 + ", " + asdf6 + ", " + asdf10 + ", " + ", " +
                asdf12 + ", " + asdf13 + "\n\n.");
        if (!asdf0 | !asdf1 | !asdf2 | !asdf3 | !asdf4 |
                !asdf5 | !asdf6 | !asdf10 | !asdf12 | !asdf13) {
            Log.v("confirm_1", "Registrar_cliente_nuevo.\n\n" + u0 + ", " +
                    u1 + ", " + u2 + ", " + u3 + ", " + u4 + ", " +
                    ", " + u6 + ", " + u9 + ", " + u10 + ", " +
                    ", " + u12 + ", " + u13 + ".\n\n.");
            Toast.makeText(this, "Debe revisar todos los campos! ", Toast.LENGTH_LONG).show();
            confirmar.setClickable(true);
            confirmar.setEnabled(true);
            //return;
        }
        else {
            String file = u0 + "_C_.txt";
            String archivoCreado = new CrearArchivo(file, getApplicationContext()).getFile();
            Log.v("Confirm_2", "Registrar_cliente_nuevo.\n\nResultado de la creacin del archivo:\n" + archivoCreado + "\n\n.");
            Cliente cliente = new Cliente(u0, u1, u2, u3, u4, u12, u13, u9, u10, Integer.parseInt(u6), "9", "abajo");
            if (new GuardarArchivo(cliente, file, "abajo", getApplicationContext()).guardarCliente()) {
                Toast.makeText(this, "Cliente " + u1 + " " + u2 + " se ha registrado correctamente!!!", Toast.LENGTH_SHORT).show();
                Log.v("Confirm_3", "Registrar_cliente_nuevo.\n\nContenido del archivo:\n\n" + imprimir_archivo(file) + "\n\n.");
                esperar(u1 + " " + u2 + " se ha registrado correctamente.");
            } else {
                Toast.makeText(this, "*** ERROR ***", Toast.LENGTH_LONG).show();
                esperar("Ha ocurrido un ERROR al guardar el archivo!!!\nIntente nuevamente...");
            }
            Log.v("Archivo", ".\n\nContenido del archivo:\n\n" + imprimir_archivo(file) + "\n\n.");
        }
    }

    private String imprimir_archivo(String file_name){

        String archivos[] = fileList();
        String contenido = "";//Aqui se lee el contenido del archivo guardado.
        if (archivo_existe(archivos, file_name)) {//Archivo nombre_archivo es el archivo que vamos a imprimir
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));//Se abre archivo
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();//Se lee archivo
                while (linea != null) {
                    contenido = contenido + linea + "\n";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
            }
        }
        return contenido;
    }

    private boolean archivo_existe (String[] archivos, String file_name){
        for (int i = 0; i < archivos.length; i++) {
            if (file_name.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

    private boolean idExiste (String iD) {
        boolean flag = false;
        String archivos[] = fileList();
        for (int i = 0; i < archivos.length; i++) {
            if (archivos[i].contains(iD + "_C_.txt")) {
                flag = true;
                break;
            }
        }
        return flag;
    }

    public boolean ID_cliente () {//Se debe llenar automaticamente
        if (ID_cliente.getEditText()!=null){
            u0 = ID_cliente.getEditText().getText().toString().trim();
        }
        if (u0.isEmpty()) {
            ID_cliente.setError(getText(R.string.cantempty_cedula));
            return false;
        } else if (idExiste(u0)) {
            ID_cliente.setError(getText(R.string.cedula_exists));
            return false;
        } else if (u0.length() > 15) {
            ID_cliente.setError(getText(R.string.toolong_cedula));
            return false;
        } else if (u0.length() < 8) {
            ID_cliente.setError(getText(R.string.toolow_cedula));
            return false;
        } else {
            ID_cliente.setError(null);
            Log.v("ID_cliente0", "Registrar_cliente_nuevo.\n\nID_cliente: " + u0 + "\nID_clienteS: " + ID_clienteS + "\n\n.");
            u0 = u0.replaceAll("[^\\w+]", "");
            Log.v("ID_cliente1", "Registrar_cliente_nuevo.\n\nID_cliente: " + u0 + "\nID_clienteS: " + ID_clienteS + "\n\n.");
            if (flag_u0) {
                if (u0.equals(ID_clienteS)) {
                    //Do nothing.
                } else {
                    Log.v("ID_cliente_sep_1", ".\n\nID_cliente: " + u0 + "\nID_clienteS: " + ID_clienteS + "\n\nfile_content:\n\n" + file_content + "\n\n.");
                    u0 = u0.replaceAll("[^\\w+]", "");
                    file_content.replace("ID_cliente_separador_" + ID_clienteS, "ID_cliente_separador_" + u0);
                    Log.v("ID_cliente_sep_2", ".\n\nID_cliente: " + u0 + "\nID_clienteS: " + ID_clienteS + "\n\nfile_content:\n\n" + file_content + "\n\n.");
                }
                ID_clienteS = u0;
                return true;
            } else {
                String linea = "ID_cliente_separador_" + u0;
                file_content = file_content + linea + "\n";
                flag_u0 = true;
                return true;
            }
        }
    }

    public boolean nombre_cliente () {
        if (nombre_cliente.getEditText()!=null){
            u1 = nombre_cliente.getEditText().getText().toString().trim();
        }

        if (u1.isEmpty()) {

            nombre_cliente.setError(getText(R.string.cantempty_nombre));
            return false;

        }

        else if (u1.length() > 12) {

            nombre_cliente.setError(getText(R.string.toolong));
            return false;
        }

        else {
            nombre_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u1) {
                if (u1.equals(nombre_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("nombre_cliente_separador_" + nombre_clienteS, "nombre_cliente_separador_" + u1);
                }
                nombre_clienteS = u1;
                return true;
            } else {
                String linea = "nombre_cliente_separador_" + u1;
                file_content = file_content + linea + "\n";
                flag_u1 = true;
                return true;
            }
        }

    }

    public boolean apellido1_cliente () {
        if (apellido1_cliente.getEditText()!=null){
            u2 = apellido1_cliente.getEditText().getText().toString().trim();
        }

        if (u2.isEmpty()) {

            apellido1_cliente.setError(getText(R.string.cantempty_apellido1));
            return false;

        }

        else if (u2.length() > 15) {

            apellido1_cliente.setError(getText(R.string.toolong_apellido1));
            return false;
        }

        else {
            apellido1_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u2) {
                if (u2.equals(apellido1_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("apellido1_cliente_separador_" + apellido1_clienteS, "apellido1_cliente_separador_" + u2);
                }
                apellido1_clienteS = u2;
                return true;
            } else {
                String linea = "apellido1_cliente_separador_" + u2;
                file_content = file_content + linea + "\n";
                flag_u2 = true;
                return true;
            }
        }

    }

    public boolean apellido2_cliente () {
        if (apellido2_cliente.getEditText()!=null){
            u3 = apellido2_cliente.getEditText().getText().toString().trim();
        }

        if (u3.isEmpty()) {

            apellido2_cliente.setError(getText(R.string.cantempty_apellido1));
            return false;

        }

        else if (u3.length() > 15) {

            apellido2_cliente.setError(getText(R.string.toolong_apellido1));
            return false;
        }

        else {
            apellido2_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u3) {
                if (u3.equals(apellido2_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("apellido2_cliente_separador_" + apellido2_clienteS, "apellido2_cliente_separador_" + u3);
                }
                apellido2_clienteS = u3;
                return true;
            } else {
                String linea = "apellido2_cliente_separador_" + u3;
                file_content = file_content + linea + "\n";
                flag_u3 = true;
                return true;
            }
        }

    }

    public boolean apodo_cliente () {
        if (apodo_cliente.getEditText()!=null){
            u4 = apodo_cliente.getEditText().getText().toString().trim();
        }

        if (u4.isEmpty()) {

            apodo_cliente.setError(getText(R.string.cantempty_apodo));
            return false;

        }

        else if (u4.length() > 10) {

            apodo_cliente.setError(getText(R.string.toolong_apodo));
            return false;
        }

        else {
            apodo_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u4) {
                if (u4.equals(apodo_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("apodo_cliente_separador_" + apodo_clienteS, "apodo_cliente_separador_" + u4);
                }
                apodo_clienteS = u4;
                return true;
            } else {
                String linea = "apodo_cliente_separador_" + u4;
                file_content = file_content + linea + "\n";
                flag_u4 = true;
                return true;
            }
        }

    }

    public boolean telefono1_cliente () {
        if (telefono1_cliente.getEditText()!=null){
            u12 = telefono1_cliente.getEditText().getText().toString().trim();
        }

        if (u12.isEmpty()) {

            telefono1_cliente.setError(getText(R.string.cantempty_telefono));
            return false;

        }

        else if (u12.length() > 8) {

            telefono1_cliente.setError(getText(R.string.toolong_telefono));
            return false;
        }

        else if (u12.length() < 8) {

            telefono1_cliente.setError(getText(R.string.toolow_telefono));
            return false;
        }

        else {
            telefono1_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u12) {
                if (u12.equals(telefono1_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("telefono1_cliente_separador_" + telefono1_clienteS, "telefono1_cliente_separador_" + u12);
                }
                telefono1_clienteS = u12;
                return true;
            } else {
                String linea = "telefono1_cliente_separador_" + u12;
                file_content = file_content + linea + "\n";
                flag_u12 = true;
                return true;
            }
        }
    }

    public boolean telefono2_cliente () {
        if (telefono2_cliente.getEditText()!=null){
            u13 = telefono2_cliente.getEditText().getText().toString().trim();
        }

        if (u13.isEmpty()) {

            telefono2_cliente.setError(getText(R.string.cantempty_telefono));
            return false;

        }

        else if (u13.length() > 8) {

            telefono2_cliente.setError(getText(R.string.toolong_telefono));
            return false;
        }

        else if (u13.length() < 8) {

            telefono2_cliente.setError(getText(R.string.toolow_telefono));
            return false;
        }

        else {
            telefono2_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u13) {
                if (u13.equals(telefono2_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("telefono2_cliente_separador_" + telefono2_clienteS, "telefono2_cliente_separador_" + u13);
                }
                telefono2_clienteS = u13;
                return true;
            } else {
                String linea = "telefono2_cliente_separador_" + u13;
                file_content = file_content + linea + "\n";
                flag_u13 = true;
                return true;
            }
        }

    }

    public boolean notas_cliente () {//Se llena con un onClick listener!!!
        if (notas_cliente.getEditText()!=null){
            u9 = notas_cliente.getEditText().getText().toString().trim();
        }

        if (u9.isEmpty()) {

            u9 = "Sin notas...";

        }

        if (u9.length() > 100) {
            notas_cliente.setError(getText(R.string.toolong_notas));
            return false;
        } else {
            notas_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u9) {
                if (u9.equals(notas_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("notas_cliente_separador_" + notas_clienteS, "notas_cliente_separador_" + u9);
                }
                notas_clienteS = u9;
                return true;
            } else {
                String linea = "notas_cliente_separador_" + u9;
                file_content = file_content + linea + "\n";
                flag_u9 = true;
                return true;
            }
        }

    }

    public boolean direccion_cliente () {//Se llena con un onClick listener!!!
        if (direccion_cliente.getEditText()!=null){
            u10 = direccion_cliente.getEditText().getText().toString().trim();
        }

        if (u10.isEmpty()) {

            direccion_cliente.setError(getText(R.string.cantempty_direccion));
            return false;

        }

        else if (u10.length() > 150) {

            direccion_cliente.setError(getText(R.string.toolong_direccion));
            return false;
        }

        else if (u10.length() < 8) {

            direccion_cliente.setError(getText(R.string.toolow_direccion));
            return false;
        }

        else {
            direccion_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u10) {
                if (u10.equals(direccion_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("direccion_cliente_separador_" + direccion_clienteS, "direccion_cliente_separador_" + u10);
                }
                direccion_clienteS = u10;
                return true;
            } else {
                String linea = "direccion_cliente_separador_" + u10;
                file_content = file_content + linea + "\n";
                flag_u10 = true;
                return true;
            }
        }

    }

    public boolean monto_disponible () {
        if (monto_disponible.getEditText()!=null){
            u6 = monto_disponible.getEditText().getText().toString().trim();
        }

        if (u6.isEmpty()) {

            monto_disponible.setError(getText(R.string.cantempty_monto));
            return false;

        }

        else if (u6.length() > 10000000) {

            monto_disponible.setError(getText(R.string.toolong_monto));
            return false;
        }

        else if (Integer.parseInt(u6) < 10000) {
            monto_disponible.setError(getText(R.string.toolow_monto));
            return false;
        }

        else {
            monto_disponible.setError(null);

            //Aqui se programa las acciones que se van a ejecutar con este parametro.
            //Se crea una linea de texto que se va a agregar al archivo nuevo que se va a crear.
            //String linea = "Comision_vendedor  " + u7 + "\n";
            //nuevo_archivo = nuevo_archivo + linea;
            if (flag_u6) {
                if (u6.equals(monto_disponibleS)) {
                    //Do nothing.
                } else {
                    file_content.replace("monto_disponible_separador_" + monto_disponibleS, "monto_disponible_separador_" + u6);
                }
                monto_disponibleS = u6;
                return true;
            } else {
                flag_u6 = true;
                return true;
            }
        }
    }

    //Metodos comunes//

    private void esperar (String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
        tv_esperar.setText(s);
        for (int i = 0; i > 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        salir(s);
    }

    private void salir(String s) {
        boton_atras(s);
    }

    @Override
    public void onBackPressed(){
        boton_atras("");
    }

    private void boton_atras(String s) {
        Intent menu_principal = new Intent(this, MenuPrincipal.class);
        menu_principal.putExtra("mensaje", s);
        startActivity(menu_principal);
        finish();
        System.exit(0);
    }

}