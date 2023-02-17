package com.example.elchino;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

public class BancaActivity extends AppCompatActivity {

    private EditText et_ID;
    private TextView tv_esperar;
    private String monto_nuevo_end = "";
    private String mes;
    private String anio;
    private String fecha;
    private String hora;
    private String dia;
    private String minuto;
    private Button bt_entregar;
    private Button bt_recibir;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private String monto_disponible = "";
    private boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private String caja = "caja.txt";
    private TextView tv_caja;
    private String credit_ID = "";
    private Date hoy_LD = Calendar.getInstance().getTime();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_banca);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        if (mensaje_recibido.equals("")) {
            //Do nothing.
        } else {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        bt_entregar = (Button) findViewById(R.id.bt_entregar);
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir = (Button) findViewById(R.id.bt_recibir);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("ABONAR/RECIBIR FONDOS DE BANCA");
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");
        mostrar_caja();
        separarFecha();
        try {
            corregir_archivos();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (cliente_recibido.equals("")) {
            //Do nothing.
        } else {
            flag_client_reciv = true;
            cliente_ID = cliente_recibido;
        }
        text_listener();
    }

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(hoy_LD);
        hora = datosFecha.getHora();
        minuto = datosFecha.getMinuto();
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        dia = datosFecha.getDia();
        fecha = dia;
    }

    private void corregir_archivos () throws IOException {

        //////// ARCHIVO cierre  ////////////////////////////////////////////////////////////

        String archivos[] = fileList();
        boolean flag_borrar = false;
        if (archivo_existe(archivos, "cierre.txt")) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("cierre.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String[] split = linea.split(" ");
                int fecha_file = Integer.parseInt(split[1]);
                int hoy_fecha = Integer.parseInt(fecha);
                Log.v("corregir_archivos0", "Nuevo_credito.\n\nfecha_file: " + fecha_file + "\nfecha_hoy: " + hoy_fecha + "\n\n");
                if (fecha_file != hoy_fecha) {
                    flag_borrar = true;
                } else {
                    //Do nothing.
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
            }
        } else {
            new AgregarLinea("fecha " + fecha, "cierre.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }
        if (flag_borrar) {
            new BorrarArchivo("cierre.txt", getApplicationContext());
            new AgregarLinea("fecha " + fecha, "cierre.txt", getApplicationContext());
            new BorrarArchivo("cierre_cierre_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }

        /////////////////////////////////////////////////////////////////////////////////////

    }

    private void mostrar_caja () {
        tv_caja.setText(imprimir_archivo(caja));
    }

    private void actualizar_disponible (String operador) throws IOException {
        int monto_abono = Integer.parseInt(et_ID.getText().toString());
        int monto_caja_mostrar = 0;
        long monto_nuevo = 0;
        String contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            if (operador.equals("sumar")) {
                monto_nuevo = Integer.parseInt(split[1]) + monto_abono;
                monto_caja_mostrar = monto_abono;
            } else if (operador.equals("restar")) {
                monto_nuevo = Integer.parseInt(split[1]) - monto_abono;
                monto_caja_mostrar = monto_abono * -1;
            } else {
                //Do nothing.
            }
            linea = linea.replace(split[1], String.valueOf(monto_nuevo));
            contenido = linea;
            br.close();
            archivo.close();
            new BorrarArchivo(caja, getApplicationContext());
            if (new GuardarArchivo(caja, contenido, getApplicationContext()).guardarFile()) {
                Log.v("actualizar_disponible_0", "Banca.\n\nContenido del archivo:\n\n" + imprimir_archivo(caja) + "\n\n.");
            } else {
                Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        actualizar_cierre(monto_caja_mostrar, obtener_caja(), credit_ID);
        contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("cajax_caja_.txt"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null && !linea.equals("")) {
                String[] split = linea.split("_separador_");
                if (split[0].equals("caja")) {
                    linea = linea.replace(split[1], String.valueOf(monto_nuevo));
                } else if (split[0].equals("estado_archivo")) {
                    if (split[1].equals("abajo")) {
                        //Do nothing. Let the line same.
                    } else {
                        linea = linea.replace("arriba", "abajo");
                    }
                } else {
                    //Do nothing. Let the line same.
                }
                contenido = contenido + linea + "\n";
                Log.v("actualizar_disponible_1", "Banca.\n\nLinea:\n\n" + linea + "\n\n.");
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (new GuardarArchivo("cajax_caja_.txt", contenido, getApplicationContext()).guardarFile()) {
            Log.v("actualizar_disponible_2", "Banca.\n\nContenido del archivo:\n\n" + imprimir_archivo("cajax_caja_.txt") + "\n\n.");
        } else {
            Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
        }
    }

    private void actualizar_cierre (Integer monto_abono, Integer saldo_caja, String credit_ID) {
        String linea_cierre = "banca " + String.valueOf(monto_abono) + " " + saldo_caja + " " + credit_ID;
        new AgregarLinea(linea_cierre, "cierre.txt", getApplicationContext());
        String lineaCierre = "banca_separador_" + String.valueOf(monto_abono) + "_separador_" + saldo_caja + "_separador_" + credit_ID;
        new AgregarLinea(lineaCierre, "cierre_cierre_.txt", getApplicationContext(), "cierre");
    }

    private Integer obtener_caja() {
        int monto_caja = 0;
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            monto_caja = Integer.parseInt(split[1]);
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return monto_caja;
    }

    public void abonar (View view) throws IOException {
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        actualizar_disponible("restar");
    }

    public void recibir (View view) throws IOException {
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        actualizar_disponible("sumar");
    }

    private void text_listener () {

        //Implementacion de un text listener
        et_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    corregir_archivos();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (tv_esperar.getText().toString().equals("Digite el monto...")) {
                    et_ID.setVisibility(View.VISIBLE);
                    et_ID.setEnabled(true);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();
                    bt_entregar.setClickable(false);
                    bt_entregar.setEnabled(false);
                    bt_recibir.setClickable(false);
                    bt_recibir.setEnabled(false);
                    if (String.valueOf(s).equals("")) {
                        //Do nothing.
                    } else {
                        bt_recibir.setEnabled(true);
                        bt_recibir.setClickable(true);
                        String monto_en_caja = "0";
                        try {
                            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            String[] split = linea.split(" ");
                            long monto = Integer.parseInt(split[1]);
                            monto_en_caja = String.valueOf(monto);
                            br.close();
                            archivo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (Integer.parseInt(monto_en_caja) < Integer.parseInt(et_ID.getText().toString())) {
                            bt_entregar.setClickable(false);
                            bt_entregar.setEnabled(false);
                        } else {
                            bt_entregar.setEnabled(true);
                            bt_entregar.setClickable(true);
                        }
                    }
                } else {
                    //TODO: No se sabe que hacer aqui!!!
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    //Metodos comunes//

    private boolean archivo_existe (String[] archivos, String file_name){
        for (int i = 0; i < archivos.length; i++) {
            if (file_name.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void onBackPressed(){
        boton_atras();
    }

    private void boton_atras() {
        Intent menu_principal = new Intent(this, MenuPrincipal.class);
        menu_principal.putExtra("mensaje", "");
        startActivity(menu_principal);
        finish();
        System.exit(0);
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

}