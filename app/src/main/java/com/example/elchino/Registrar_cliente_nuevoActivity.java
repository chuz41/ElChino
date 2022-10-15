package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
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
import com.example.elchino.Util.TranslateUtil;
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

    TextInputLayout nombre_cliente,apellido1_cliente,apellido2_cliente,apodo_cliente,edad_cliente,sexo_cliente,direccion_cliente,puntuacion_cliente,tasa_cliente,monto_disponible,interes_mora,ID_cliente;
    private String u0,u1,u2,u3,u4,u5,u6,u7,u8,u9,u10,u11;
    private boolean flag_u0 = false;
    private boolean flag_u1 = false;
    private boolean flag_u2 = false;
    private boolean flag_u3 = false;
    private boolean flag_u4 = false;
    private boolean flag_u5 = false;
    private boolean flag_u6 = false;
    private boolean flag_u7 = false;
    private boolean flag_u8 = false;
    private boolean flag_u9 = false;
    private boolean flag_u10 = false;
    private boolean flag_u11 = false;
    private String file_content = "";
    private EditText et_ID;
    private TextView tv_esperar;
    private Map<String, Integer> meses = new HashMap<String, Integer>();private String dia;
    private String mes;
    private String anio;
    private String fecha;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_cliente_nuevo);
        ID_cliente = (TextInputLayout) findViewById(R.id.ID_cliente);
        nombre_cliente = (TextInputLayout) findViewById(R.id.nombre_cliente);
        apellido1_cliente = (TextInputLayout) findViewById(R.id.apellido1_cliente);
        apellido2_cliente = (TextInputLayout) findViewById(R.id.apellido2_cliente);
        apodo_cliente = (TextInputLayout) findViewById(R.id.apodo_cliente);
        edad_cliente = (TextInputLayout) findViewById(R.id.edad_cliente);
        sexo_cliente = (TextInputLayout) findViewById(R.id.sexo_cliente);
        direccion_cliente = (TextInputLayout) findViewById(R.id.direccion_cliente);
        puntuacion_cliente = (TextInputLayout) findViewById(R.id.puntuacion_cliente);
        tasa_cliente = (TextInputLayout) findViewById(R.id.tasa_cliente);
        monto_disponible = (TextInputLayout) findViewById(R.id.monto_disponible);
        interes_mora = (TextInputLayout) findViewById(R.id.interes_mora);
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        confirmar = (Button) findViewById(R.id.bt_confirmar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        et_ID.setVisibility(View.INVISIBLE);
        separar_fechaYhora();
    }

    public void confirm(View view) throws IOException, JSONException {
        confirmar.setClickable(false);
        confirmar.setEnabled(false);
        if (!ID_cliente() | !nombre_cliente() | !apellido1_cliente() | !apellido2_cliente() | !apodo_cliente() | !edad_cliente() |
                !sexo_cliente() | !direccion_cliente() | !puntuacion_cliente() | !tasa_cliente() | !monto_disponible() | !interes_mora()){
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
            if (flag_u0) {
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
                return true;
            } else {
                String linea = "apodo_cliente_separador_" + u4;
                file_content = file_content + linea + "\n";
                flag_u4 = true;
                return true;
            }
        }

    }

    public boolean edad_cliente () {//Se llena con un onClick listener!!!
        if (edad_cliente.getEditText()!=null){
            u8 = edad_cliente.getEditText().getText().toString().trim();
        }

        if (u8.isEmpty()) {

            edad_cliente.setError(getText(R.string.cantempty_edad));
            return false;

        }

        else if (u8.length() > 10) {

            edad_cliente.setError(getText(R.string.toolong_edad));
            return false;
        }

        else if (u8.length() < 10) {

            edad_cliente.setError(getText(R.string.toolow_edad));
            return false;
        }

        else {
            edad_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u8) {
                return true;
            } else {
                String linea = "edad_cliente_separador_" + u8;
                file_content = file_content + linea + "\n";
                flag_u8 = true;
                return true;
            }
        }

    }

    public boolean sexo_cliente () {//Se llena con un onClick listener!!!
        if (sexo_cliente.getEditText()!=null){
            u9 = sexo_cliente.getEditText().getText().toString().trim();
        }

        if (u9.isEmpty()) {

            sexo_cliente.setError(getText(R.string.cantempty_sexo));
            return false;

        }

        else if (u9.length() > 6) {

            sexo_cliente.setError(getText(R.string.toolong_sexo));
            return false;
        }

        else {
            sexo_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u9) {
                return true;
            } else {
                String linea = "sexo_cliente_separador_" + u9;
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

        else if (u10.length() < 20) {

            direccion_cliente.setError(getText(R.string.toolow_direccion));
            return false;
        }

        else {
            direccion_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u10) {
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
        if (tasa_cliente.getEditText()!=null){
            u5 = tasa_cliente.getEditText().getText().toString().trim();
        }

        if (u5.isEmpty()) {

            tasa_cliente.setError(getText(R.string.cantempty_tasa));
            return false;

        }

        else if (u5.length() > 3) {

            tasa_cliente.setError(getText(R.string.toolong_tasa));
            return false;
        }

        else if (Integer.parseInt(u5) > 100) {
            tasa_cliente.setError(getText(R.string.toolong_tasa));
            return false;
        }

        else if (Integer.parseInt(u5) < 0) {
            tasa_cliente.setError(getText(R.string.toolow_tasa));
            return false;
        }

        else {
            tasa_cliente.setError(null);

            //Aqui se programa las acciones que se van a ejecutar con este parametro.
            //Se crea una linea de texto que se va a agregar al archivo nuevo que se va a crear.
            //String linea = "Comision_vendedor  " + u7 + "\n";
            //nuevo_archivo = nuevo_archivo + linea;
            if (flag_u5) {
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
        if (interes_mora.getEditText()!=null){
            u7 = interes_mora.getEditText().getText().toString().trim();
        }

        if (u7.isEmpty()) {

            interes_mora.setError(getText(R.string.cantempty_tasa));
            return false;

        }

        else if (u7.length() > 3) {

            interes_mora.setError(getText(R.string.toolong_tasa));
            return false;
        }

        else if (Integer.parseInt(u7) > 100) {
            interes_mora.setError(getText(R.string.toolong_tasa));
            return false;
        }

        else if (Integer.parseInt(u7) < 0) {
            interes_mora.setError(getText(R.string.toolow_tasa));
            return false;
        }

        else {
            interes_mora.setError(null);

            //Aqui se programa las acciones que se van a ejecutar con este parametro.
            //Se crea una linea de texto que se va a agregar al archivo nuevo que se va a crear.
            //String linea = "Comision_vendedor  " + u7 + "\n";
            //nuevo_archivo = nuevo_archivo + linea;
            if (flag_u7) {
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
        if (puntuacion_cliente.getEditText()!=null){
            u11 = puntuacion_cliente.getEditText().getText().toString().trim();
        }

        if (u11.isEmpty()) {

            puntuacion_cliente.setError(getText(R.string.cantempty_puntuacion));
            return false;

        }

        else if (u11.length() > 1) {

            puntuacion_cliente.setError(getText(R.string.toolong_puntuacion));
            return false;
        }

        else if (u11.length() < 1) {

            puntuacion_cliente.setError(getText(R.string.toolow_puntuacion));
            return false;
        }

        else {
            puntuacion_cliente.setError(null);
            //Do nothing. Este nombre se va a usar en la proxima activity (HorariosagregarActivity.java)
            if (flag_u11) {
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
        salir();
    }

    private void salir() {
        Intent menu_principal = new Intent(this, MenuPrincipal.class);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
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
        meses.put("Dic",12);
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
        edad_cliente.setVisibility(View.VISIBLE);
        sexo_cliente.setVisibility(View.VISIBLE);
        direccion_cliente.setVisibility(View.VISIBLE);
        puntuacion_cliente.setVisibility(View.VISIBLE);
        tasa_cliente.setVisibility(View.VISIBLE);
        monto_disponible.setVisibility(View.VISIBLE);
        interes_mora.setVisibility(View.VISIBLE);
        confirmar.setVisibility(View.VISIBLE);
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
        edad_cliente.setVisibility(View.INVISIBLE);
        sexo_cliente.setVisibility(View.INVISIBLE);
        direccion_cliente.setVisibility(View.INVISIBLE);
        puntuacion_cliente.setVisibility(View.INVISIBLE);
        tasa_cliente.setVisibility(View.INVISIBLE);
        monto_disponible.setVisibility(View.INVISIBLE);
        interes_mora.setVisibility(View.INVISIBLE);
        confirmar.setVisibility(View.INVISIBLE);
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
    /*
    private void check_onlines () throws JSONException {
        if (verificar_internet()) {
            boolean flag = true;
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(onlines));
                //imprimir_archivo("facturas_online.txt");
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                //String contenido = "";
                abajos.clear();
                Integer countercito = 0;
                while (linea != null) {
                    countercito++;
                    String count = String.valueOf(countercito);
                    String[] split = linea.split(" ");
                    if (split[0].equals("abajo")) {
                        Log.v("OJOF_abajo: ", "\n\nLinea: " + linea + " Fin de linea!!!");
                        abajos.put(count, split[1]);
                        flag = false;
                    } else if (split[0].equals("arriba")) {
                        Log.v("OJOF_arriba: ", "\n\nLinea: " + linea + " Fin de linea!!!");
                        //TODO: Pensar que hacer!!!
                    } else {
                        Log.v("OJOF_(error): ", "\n\n(No deberia llegar aqui!!!\n\nLinea: " + linea + " Fin de linea!!!");
                        //Do nothing.
                    }
                    linea = br.readLine();
                }
                archivo.close();
                br.close();


            } catch (IOException e) {
                e.printStackTrace();
            }

            if (flag) {
                return;
            } else {
                //Do nothing. Continue with the work
            }

            abajiar();
            //return objeto_json;
        } else {

        }
    }

    private void abajiar() throws JSONException {
        String sp_clientes = "";
        String sp_creditos = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            //imprimir_archivo("facturas_online.txt");
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            int cont = 0;
            while (linea != null) {
                String[] split = linea.split(" ");
                if (split[0].equals("Screditos")) {
                    sp_creditos = split[1];
                }
                if (split[0].equals("Sclientes")) {
                    sp_clientes = split[1];
                }
                linea = br.readLine();
                cont++;
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String spid = "";
        String sheet = "";
        for (String key : abajos.keySet()) {
            String json_string = "";
            JSONObject jsonObject = new JSONObject();
            String[] split_pre = abajos.get(key).split("_");
            if (split_pre[1].equals("C")) {
                spid = sp_clientes;
                sheet = "clientes";
            } else {
                spid = sp_creditos;
                sheet = "creditos";
            }
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(abajos.get(key)));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null && !linea.isEmpty()) {
                    String[] split = linea.split("_separador_");
                    json_string = json_string + split[1] + "_n_";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            jsonObject = TranslateUtil.string_to_Json(json_string, spid, sheet, split_pre[0]);
            subir_archivo_resagado(jsonObject, abajos.get(key), key);
            break;
        }
    }

    private void subir_archivo_resagado (JSONObject jsonObject, String file, String key) {
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

        //ocultar_todo();

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
                                cambiar_bandera (file, key);
                                try {
                                    abajiar();
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            } else {
                                String factura_num = split[15];
                            }
                        } else {
                            //No se subio correctamente!
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // TODO: Handle error
                    }
                });

        // Add the request to the RequestQueue.
        requestQueue.add(jsonObjectRequest);

    }

    private void cambiar_bandera (String file, String key) {
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
                        abajos.remove(key);
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
            Log.v("cambiar_band_result", "\n\nArchivo \"onlines.txt\":\n\n" + imprimir_archivo(onlines));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    */
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
                }
                split = linea.split("_separador_");
                json_string = json_string + split[1] + "_n_";
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
