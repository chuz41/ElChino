package com.example.elchino;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.autofill.AutofillValue;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.elchino.Util.DateUtilities;
import com.example.elchino.Util.TranslateUtil;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class Registrar_cliente_nuevoActivity extends AppCompatActivity {

    TextInputLayout nombre_cliente,telefono2_cliente,telefono1_cliente,apellido1_cliente,apellido2_cliente,apodo_cliente,notas_cliente,direccion_cliente,monto_disponible,ID_cliente;
    private String u0,u1,u2,u3,u4,u5,u6,u7,u9,u10,u11,u12,u13,u14;//u0: cedula, u1: nombre, u2: apellido, u4: Apodo
    private boolean flag_u0 = false;
    private boolean flag_u1 = false;
    private boolean flag_u2 = false;
    private boolean flag_u3 = false;
    private boolean flag_u4 = false;
    private boolean flag_u5 = false;
    private boolean flag_u6 = false;
    private boolean flag_u7 = false;
    //private boolean flag_u8 = false;
    private boolean flag_u9 = false;
    private boolean flag_u10 = false;
    private boolean flag_u11 = false;
    private boolean flag_u12 = false;
    private boolean flag_u13 = false;
    private boolean flag_u14 = false;
    private String nombre_clienteS = "";
    private String telefono1_clienteS = "";
    private String telefono2_clienteS = "";
    private String apellido1_clienteS = "";
    private String apellido2_clienteS = "";
    private String apodo_clienteS = "";
    private String notas_clienteS = "";
    private String direccion_clienteS = "";
    private String puntuacion_clienteS = "9";
    private String tasa_clienteS = "25";
    private String monto_disponibleS = "";
    private String interes_moraS = "1";
    private String ID_clienteS = "";
    private String fecha_registroS = "";
    private String file_content = "";
    private EditText et_ID;
    private TextView tv_esperar;
    private Map<String, Integer> meses = new HashMap<String, Integer>();private String dia;
    private String mes;
    private String anio;
    private String fecha;
    private int mes_selected;
    private int anio_selected;
    private int fecha_selected;
    private String hora;
    private String minuto;
    private String nombre_dia;
    private String cobrador = "a_sfile_cobrador_sfile_a.txt";
    private String spreadsheet_cobradores = "1y5wRGgrkH48EWgd2OWwon_Um42mxN94CdmJSi_XCwvM";
    private String readRowURL = "https://script.google.com/macros/s/AKfycbxJNCrEPYSw8CceTwPliCscUtggtQ2l_otieFmE/exec?spreadsheetId=";
    private String addRowURL = "https://script.google.com/macros/s/AKfycbweyYb-DHVgyEdCWpKoTmvOxDGXleawjAN8Uw9AeJYbZ24t9arB/exec";
    private HashMap<String, String> abajos = new HashMap<String, String>();
    private String sheet_cobradores = "cobradores";
    private Button confirmar;
    private String onlines = "onlines.txt";
    private TextInputEditText tiet_fecha_nacimiento;

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
        //tiet_fecha_nacimiento = (TextInputEditText) findViewById(R.id.tiet_fecha_nacimiento);
        apodo_cliente = (TextInputLayout) findViewById(R.id.apodo_cliente);
        //edad_cliente = (TextInputLayout) findViewById(R.id.edad_cliente);
        notas_cliente = (TextInputLayout) findViewById(R.id.notas_cliente);
        direccion_cliente = (TextInputLayout) findViewById(R.id.direccion_cliente);
        //puntuacion_cliente = (TextInputLayout) findViewById(R.id.puntuacion_cliente);
        //tasa_cliente = (TextInputLayout) findViewById(R.id.tasa_cliente);
        monto_disponible = (TextInputLayout) findViewById(R.id.monto_disponible);
        //interes_mora = (TextInputLayout) findViewById(R.id.interes_mora);
        //fecha_registro = (TextInputLayout) findViewById(R.id.fecha_registro);
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        confirmar = (Button) findViewById(R.id.bt_confirmar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        et_ID.setVisibility(View.INVISIBLE);
        separar_fechaYhora();
    }

 /*   public void select_fecha_nacimiento (View view) {
        //tiet_fecha_nacimiento.setClickable(false);
        //tiet_fecha_nacimiento.setEnabled(false);
        final Calendar c = Calendar.getInstance();
        final boolean[] edad_permitida = {true};
        mes_selected = (c.get(Calendar.MONTH));
        //Toast.makeText(this, "mes selected: " + mes_selected, Toast.LENGTH_LONG).show();
        anio_selected = c.get(Calendar.YEAR);
        fecha_selected = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                String i_s = String.valueOf(i);
                String i1_s = String.valueOf(i1 + 1);
                String i2_s = String.valueOf(i2);
                if (i_s.length() == 1) {
                    i_s = "0" + i_s;
                }
                if (i1_s.length() == 1) {
                    i1_s = "0" + i1_s;
                }
                if (i2_s.length() == 1) {
                    i2_s = "0" + i2_s;
                }
                tiet_fecha_nacimiento.setText(i2_s + "/" + i1_s + "/" + i_s);
                //edad_cliente.autofill(AutofillValue.forText(String.valueOf(i2) + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i)));
                mes_selected = i1+1;
                anio_selected = i;
                fecha_selected = i2;
                //Generamos numero comparador:
                int comparador_selected = anio_selected;
                comparador_selected = comparador_selected * 100;
                comparador_selected = comparador_selected + mes_selected;
                comparador_selected = comparador_selected * 100;
                comparador_selected = comparador_selected + fecha_selected;
                int comparador = Integer.parseInt(anio);
                comparador = comparador * 100;
                comparador = comparador + meses.get(mes);
                comparador = comparador * 100;
                comparador = comparador + Integer.parseInt(fecha);
                if (comparador_selected > (comparador - 16)) {
                    edad_permitida[0] = false;
                } else {
                    mes_selected = (c.get(Calendar.MONTH));
                    anio_selected = c.get(Calendar.YEAR);
                    fecha_selected = c.get(Calendar.DAY_OF_MONTH);
                    Log.v("select_fecha", String.valueOf(fecha_selected) + "/" + String.valueOf(mes_selected + 1) + "/" + String.valueOf(anio_selected));
                }
            }
        },anio_selected,mes_selected,fecha_selected);
        datePickerDialog.show();
    }*/

    public void confirm(View view) throws IOException, JSONException {
        confirmar.setClickable(false);
        confirmar.setEnabled(false);
        if (!ID_cliente() | !nombre_cliente() | !apellido1_cliente() | !apellido2_cliente() | !apodo_cliente() |
                !notas_cliente() | !direccion_cliente() | !puntuacion_cliente() | !tasa_cliente() | !monto_disponible() |
                !interes_mora() | !telefono1_cliente() | !telefono2_cliente() | !fecha_registro()){
            Toast.makeText(this, "Debe llenar todos los campos! ", Toast.LENGTH_LONG).show();
            confirmar.setClickable(true);
            confirmar.setEnabled(true);
            //return;
        }
        else {
            Toast.makeText(this, "Cliente " + u1 + " se ha registrado correctamente!!!", Toast.LENGTH_SHORT).show();
            String file = u0 + "_C_.txt";
            crear_archivo(file);
            guardar(file_content, file);
            //agregar_linea_archivo(//u0: cedula, u1: nombre, u2: apellido, u4: Apodo )
            Log.v("Archivo", ".\n\nContenido del archivo:\n\n" + imprimir_archivo(file) + "\n\n.");
            subir_archivo(file);
            //esperar(u1 + " " + u2 + " se ha registrado correctamente.");
        }
    }

    public boolean ID_cliente () {//Se debe llenar automaticamente
        if (ID_cliente.getEditText()!=null){
            u0 = ID_cliente.getEditText().getText().toString().trim();
        }

        if (u0.isEmpty()) {

            ID_cliente.setError(getText(R.string.cantempty_cedula));
            return false;

        }

        else if (u0.length() > 15) {

            ID_cliente.setError(getText(R.string.toolong_cedula));
            return false;
        }

        else if (u0.length() < 8) {
            ID_cliente.setError(getText(R.string.toolow_cedula));
            return false;
        }

        else {
            ID_cliente.setError(null);

            //Aqui se programa las acciones que se van a ejecutar con este parametro.
            //Se crea una linea de texto que se va a agregar al archivo nuevo que se va a crear.
            //String linea = "Comision_vendedor  " + u7 + "\n";
            //nuevo_archivo = nuevo_archivo + linea;
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

    /*public boolean edad_cliente () {//Se llena con un onClick listener!!!\

        int anio_selected_n = 0;
        int mes_selected_n = 0;
        int fecha_selected_n = 0;
        String[] split;

        if (edad_cliente.getEditText()!=null){

            u8 = edad_cliente.getEditText().getText().toString().trim();

        }

        //Generamos numero comparador:

        int comparador_selected = anio_selected_n;
        comparador_selected = comparador_selected * 100;
        comparador_selected = comparador_selected + mes_selected_n;
        comparador_selected = comparador_selected * 100;
        comparador_selected = comparador_selected + fecha_selected_n;
        int comparador = Integer.parseInt(anio);
        comparador = comparador * 100;
        comparador = comparador + meses.get(mes);
        comparador = comparador * 100;
        comparador = comparador + Integer.parseInt(fecha);

        if (u8.isEmpty()) {

            edad_cliente.setError(getText(R.string.cantempty_edad));
            return false;

        } else if (comparador_selected > (comparador - 16)) {

            edad_cliente.setError(getText(R.string.muy_joven));
            return false;

        } else if (u8.length() > 10) {

            edad_cliente.setError(getText(R.string.toolong_edad));
            return false;

        } else if (u8.length() < 10) {

            edad_cliente.setError(getText(R.string.toolow_edad));
            return false;

        } else {

            edad_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u8) {

                if (u8.equals(edad_clienteS)) {

                    //Do nothing.

                } else {

                    file_content.replace("edad_cliente_separador_" + edad_clienteS, "edad_cliente_separador_" + u8);

                }

                edad_clienteS = u8;
                return true;

            } else {

                String linea = "edad_cliente_separador_" + u8;
                file_content = file_content + linea + "\n";
                flag_u8 = true;
                return true;

            }

        }

    }*/

    public boolean fecha_registro () {//Se llena con un onClick listener!!!

        Date fecha_hoy = Calendar.getInstance().getTime();
        String fecha_hoy_S = DateUtilities.dateToString(fecha_hoy);
        String[] split = fecha_hoy_S.split("-");
        fecha_hoy_S = split[2] + "/" + split[1] + "/" + split[0];

        u14 = fecha_hoy_S;

        if (u14.isEmpty()) {

            //fecha_registro.setError(getText(R.string.cantempty_edad));
            return false;

        }

        else if (u14.length() > 10) {

            //fecha_registro.setError(getText(R.string.toolong_edad));
            return false;
        }

        else if (u14.length() < 10) {

            //fecha_registro.setError(getText(R.string.toolow_edad));
            return false;
        }

        else {
            //fecha_registro.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u14) {
                if (u14.equals(fecha_registroS)) {
                    //Do nothing.
                } else {
                    file_content.replace("fecha_registro_separador_" + fecha_registroS, "fecha_registro_separador_" + u14);
                }
                fecha_registroS = u14;
                return true;
            } else {
                String linea = "fecha_registro_separador_" + u14;
                file_content = file_content + linea + "\n";
                flag_u14 = true;
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

        else if (u10.length() > 100) {

            direccion_cliente.setError(getText(R.string.toolong_direccion));
            return false;
        }

        else if (u10.length() < 15) {

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

    public boolean tasa_cliente () {//Se debe llenar automaticamente

        u5 = "25";

        if (u5.isEmpty()) {

            //tasa_cliente.setError(getText(R.string.cantempty_tasa));
            return false;

        }

        else if (u5.length() > 3) {

            //tasa_cliente.setError(getText(R.string.toolong_tasa));
            return false;
        }

        else if (Integer.parseInt(u5) > 100) {
            //tasa_cliente.setError(getText(R.string.toolong_tasa));
            return false;
        }

        else if (Integer.parseInt(u5) < 0) {
            //tasa_cliente.setError(getText(R.string.toolow_tasa));
            return false;
        }

        else {
            //tasa_cliente.setError(null);

            //Aqui se programa las acciones que se van a ejecutar con este parametro.
            //Se crea una linea de texto que se va a agregar al archivo nuevo que se va a crear.
            //String linea = "Comision_vendedor  " + u7 + "\n";
            //nuevo_archivo = nuevo_archivo + linea;
            if (flag_u5) {
                if (u5.equals(tasa_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("tasa_cliente_separador_" + tasa_clienteS, "tasa_cliente_separador_" + u5);
                }
                tasa_clienteS = u5;
                return true;
            } else {
                String linea = "tasa_cliente_separador_" + u5;
                file_content = file_content + linea + "\n";
                flag_u5 = true;
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
                String linea = "monto_disponible_separador_" + u6;
                file_content = file_content + linea + "\n";
                flag_u6 = true;
                return true;
            }
        }

    }

    public boolean interes_mora () {//Se debe llenar automaticamente

        u7 = "1";

        if (u7.isEmpty()) {

            //interes_mora.setError(getText(R.string.cantempty_tasa));
            return false;

        }

        else if (u7.length() > 3) {

            //interes_mora.setError(getText(R.string.toolong_tasa));
            return false;
        }

        else if (Integer.parseInt(u7) > 100) {
            //interes_mora.setError(getText(R.string.toolong_tasa));
            return false;
        }

        else if (Integer.parseInt(u7) < 0) {
            //interes_mora.setError(getText(R.string.toolow_tasa));
            return false;
        }

        else {
            //interes_mora.setError(null);

            //Aqui se programa las acciones que se van a ejecutar con este parametro.
            //Se crea una linea de texto que se va a agregar al archivo nuevo que se va a crear.
            //String linea = "Comision_vendedor  " + u7 + "\n";
            //nuevo_archivo = nuevo_archivo + linea;
            if (flag_u7) {
                if (u7.equals(interes_moraS)) {
                    //Do nothing.
                } else {
                    file_content.replace("interes_mora_separador_" + interes_moraS, "interes_mora_separador_" + u7);
                }
                interes_moraS = u7;
                return true;
            } else {
                String linea = "interes_mora_separador_" + u7;
                file_content = file_content + linea + "\n";
                flag_u7 = true;
                return true;
            }
        }

    }

    public boolean puntuacion_cliente () {//Se llena automaticamente!!!


        u11 = "9";

        if (u11.isEmpty()) {

            //puntuacion_cliente.setError(getText(R.string.cantempty_puntuacion));
            return false;

        }

        else if (u11.length() > 1) {

            //puntuacion_cliente.setError(getText(R.string.toolong_puntuacion));
            return false;
        }

        else if (u11.length() < 1) {

            //puntuacion_cliente.setError(getText(R.string.toolow_puntuacion));
            return false;
        }

        else {
            //puntuacion_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u11) {
                if (u11.equals(puntuacion_clienteS)) {
                    //Do nothing.
                } else {
                    file_content.replace("puntuacion_cliente_separador_" + puntuacion_clienteS, "puntuacion_cliente_separador_" + u11);
                }
                puntuacion_clienteS = u11;
                return true;
            } else {
                String linea = "puntuacion_cliente_separador_" + u11;
                file_content = file_content + linea + "\n";
                flag_u11 = true;
                return true;
            }
        }

    }

    //Metodos comunes//

    private void esperar (String s) {
        ocultar_todito();
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
        Intent menu_principal = new Intent(this, MenuPrincipal.class);
        menu_principal.putExtra("mensaje", s);
        startActivity(menu_principal);
        finish();
        System.exit(0);
    }

    public  void borrar_archivo(String file) throws IOException {
        File archivo = new File(file);
        String empty_string = "";
        guardar(empty_string, file);
        archivo.delete();
    }

    public  void guardar (String contenido, String file_name) throws IOException {
        try {
            //borrar_archivo(file_name);
            //crear_archivo(file_name);
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(file_name, Activity.MODE_PRIVATE));
            archivo.write(contenido);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
        }
    }

    private void separar_fechaYhora(){
        llenar_mapa_meses();
        Date now = Calendar.getInstance().getTime();
        String ahora = now.toString();
        String[] split = ahora.split(" ");
        nombre_dia = split[0];
        dia = split[2];
        mes = String.valueOf(meses.get(split[1]));
        anio = split[5];
        String hora_completa = split[3];
        fecha = split[2];
        split = hora_completa.split(":");
        minuto = split[1];
        hora = split[0];
    }

    private void llenar_mapa_meses() {
        meses.put("Jan",1);
        meses.put("Feb",2);
        meses.put("Mar",3);
        meses.put("Apr",4);
        meses.put("May",5);
        meses.put("Jun",6);
        meses.put("Jul",7);
        meses.put("Aug",8);
        meses.put("Sep",9);
        meses.put("Oct",10);
        meses.put("Nov",11);
        meses.put("Dec",12);
        meses.put("1",1);
        meses.put("2",2);
        meses.put("3",3);
        meses.put("4",4);
        meses.put("5",5);
        meses.put("6",6);
        meses.put("7",7);
        meses.put("8",8);
        meses.put("9",9);
        meses.put("10",10);
        meses.put("11",11);
        meses.put("12",12);
    }

    public  void agregar_linea_archivo (String new_line, String file_name) {
        String archivos[] = fileList();
        String ArchivoCompleto = "";//Aqui se lee el contenido del archivo guardado.
        if (archivo_existe(archivos, file_name)) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null) {
                    ArchivoCompleto = ArchivoCompleto + linea + "\n";
                    linea = br.readLine();
                }
                ArchivoCompleto = ArchivoCompleto + new_line + "\n";
                br.close();
                archivo.close();
            } catch (IOException e) {
            }
        } else {
            crear_archivo(file_name);
            agregar_linea_archivo(file_name, new_line);
            return;
        }
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(file_name, Activity.MODE_PRIVATE));
            archivo.write(ArchivoCompleto);
            archivo.flush();
        } catch (IOException e) {
        }
    }

    private boolean archivo_existe (String[] archivos, String file_name){
        for (int i = 0; i < archivos.length; i++) {
            if (file_name.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

    private void crear_archivo(String nombre_archivo) {
        try{
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(nombre_archivo, Activity.MODE_PRIVATE));
            archivo.flush();
            archivo.close();
        }catch (IOException e) {
        }
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

    private void mostrar_todito() {
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        //et_ID.setVisibility(View.INVISIBLE);
        ID_cliente.setVisibility(View.VISIBLE);
        nombre_cliente.setVisibility(View.VISIBLE);
        apellido1_cliente.setVisibility(View.VISIBLE);
        apellido2_cliente.setVisibility(View.VISIBLE);
        apodo_cliente.setVisibility(View.VISIBLE);
        //edad_cliente.setVisibility(View.VISIBLE);
        notas_cliente.setVisibility(View.VISIBLE);
        direccion_cliente.setVisibility(View.VISIBLE);
        //puntuacion_cliente.setVisibility(View.VISIBLE);
        //tasa_cliente.setVisibility(View.VISIBLE);
        monto_disponible.setVisibility(View.VISIBLE);
        //interes_mora.setVisibility(View.VISIBLE);
        confirmar.setVisibility(View.VISIBLE);
        telefono1_cliente.setVisibility(View.VISIBLE);
        telefono2_cliente.setVisibility(View.VISIBLE);
        //fecha_registro.setVisibility(View.VISIBLE);
    }

    private void ocultar_todito() {
        Log.v("ocultar_todito", "Se hace todo invisible");
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("conectando, por favor espere...");
        //et_ID.setVisibility(View.VISIBLE);
        ID_cliente.setVisibility(View.INVISIBLE);
        nombre_cliente.setVisibility(View.INVISIBLE);
        apellido1_cliente.setVisibility(View.INVISIBLE);
        apellido2_cliente.setVisibility(View.INVISIBLE);
        apodo_cliente.setVisibility(View.INVISIBLE);
        //edad_cliente.setVisibility(View.INVISIBLE);
        notas_cliente.setVisibility(View.INVISIBLE);
        direccion_cliente.setVisibility(View.INVISIBLE);
        //puntuacion_cliente.setVisibility(View.INVISIBLE);
        //tasa_cliente.setVisibility(View.INVISIBLE);
        monto_disponible.setVisibility(View.INVISIBLE);
        //interes_mora.setVisibility(View.INVISIBLE);
        confirmar.setVisibility(View.INVISIBLE);
        telefono1_cliente.setVisibility(View.INVISIBLE);
        telefono2_cliente.setVisibility(View.INVISIBLE);
        //fecha_registro.setVisibility(View.INVISIBLE);
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

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void ocultar_teclado(){
        View view = this.getCurrentFocus();
        InputMethodManager imn = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imn.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //Metodos comunes online//

    private boolean verificar_internet() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(this, "Debe estar conectado a una red WiFi o datos mobiles.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            //Si esta conectado a internet.
            //Toast.makeText(this, "Conectado a internet!", Toast.LENGTH_LONG).show();
            return true;
        }
    }

    private void subir_archivo (String file) throws JSONException {
        ocultar_todito();
        String sp_clientes = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split(" ");
                if (split[0].equals("Sclientes")) {
                    sp_clientes = split[1];
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        String spid = sp_clientes;
        String json_string = "";
        JSONObject jsonObject = new JSONObject();
        String sheet = "clientes";
        String id_cliente = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null && !linea.isEmpty()) {
                String[] split = linea.split("_separador_");
                if (split[0].equals("ID_cliente")) {
                    id_cliente = split[1];
                } else {
                    json_string = json_string + split[1] + "_n_";
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonObject = TranslateUtil.string_to_Json(json_string, spid, sheet, id_cliente);
        subir_nuevo_cliente(jsonObject, file);
    }

    private void subir_nuevo_cliente (JSONObject jsonObject, String file) {
        if (verificar_internet()) {
            agregar_linea_archivo("abajo " + file, onlines);
            RequestQueue queue;
            queue = Volley.newRequestQueue(this);
            //Llamada POST usando Volley:
            RequestQueue requestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            Network network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);

            // Start the queue
            requestQueue.start();

            //Toast.makeText(this, "Debug:\nConsecutivo: " + Consecutivo + "\nconsecutivo: " + consecutivo + "\nDeben ser iguales.", Toast.LENGTH_LONG).show();

            String url = addRowURL;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                    (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {

                        @Override
                        public void onResponse(JSONObject response) {
                            String[] split = response.toString().split("\"");
                            int length_split = split.length;
                            Log.v("info_sub_file_resag: ", "\n\n" + response + "\n\n");
                            if (length_split > 3) {//TODO: Corregir este if. Debe ser mas especifico y detectar si la respuesta no es correcta.
                                for (int i = 0; i < length_split; i++) {
                                    Log.v("split[" + i + "]", split[i]);
                                }
                                if (split[2].equals(":")) {//TODO: Todo de arriba tiene que ver tambien con este.
                                    cambiar_bandera1(file);
                                    esperar("\"Cliente se ha registrado correctamente en el servidor.\"");
                                } else {
                                    //Nunca debe llegar aqui!!!
                                }
                            } else {
                                //No se subio correctamente!
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // TODO: Handle error
                            //mensaje_error_en_subida();
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(jsonObjectRequest);
        } else {//No hay internet!!!
            agregar_linea_archivo("abajo " + file, onlines);
            //msg("Para registrar al vendedor en el servidor, debe estar conectado a internet.");
            mostrar_todito();
            esperar("\"Para registrar al vendedor en el servidor, debe estar conectado a internet.\"");
        }
    }

    private void cambiar_bandera1 (String file) {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(onlines));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String contenido = "";
            while (linea != null) {
                Log.v("cambiar_bandera_file", "  Linea: " + linea + "\n\n");
                String[] split = linea.split(" ");
                if (split[0].equals("arriba")) {
                    //Dejar perder la linea
                } else if (split[0].equals("abajo")) {
                    if (split[1].equals(file)) {
                        linea = linea.replace(split[0], "arriba");
                        contenido = contenido + linea + "\n";
                    } else {
                        contenido = contenido + linea + "\n";
                    }
                } else {
                    //Do nothing. Nunca llega aqui.
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            borrar_archivo(onlines);
            guardar(contenido, onlines);//Aqui se eliminan las lineas que corresponden a archivos que ya se han subido.
            mostrar_todito();
            Log.v("cambiar_band_result", "\n\nArchivo \"onlines.txt\":\n\n" + imprimir_archivo(onlines));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
