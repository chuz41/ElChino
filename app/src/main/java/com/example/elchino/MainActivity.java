package com.example.elchino;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CheckedTextView;
import android.widget.EditText;
import android.widget.RadioGroup;
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
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.elchino.Util.TranslateUtil;

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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

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
    private EditText et_ID;
    private TextView tv_esperar;
    private String sheet_cobradores = "cobradores";
    private String nombre_cobra = "";
    private Button boton_submit;
    private String ID_cobrador;
    private CheckBox checkedTextView;
    private String onlines = "onlines.txt";

    @Override
    protected void onPause() {
        super.onPause();
        try {
            check_onlines();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        try {
            check_onlines();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        boton_submit = (Button) findViewById(R.id.boton_submit);
        boton_submit.setVisibility(View.INVISIBLE);
        checkedTextView = findViewById(R.id.checkedTextView);
        checkedTextView.setText("Mostrar password");
        checkedTextView.setVisibility(View.INVISIBLE);
        separar_fechaYhora();
        try {
            check_activation();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void menu_principal () {
        Intent menuPrincipal = new Intent(this, MenuPrincipal.class);
        //menuPrincipal.putExtra("var1", var1);
        startActivity(menuPrincipal);
        finish();
        System.exit(0);
    }

    private void crear_archivoS () throws JSONException {

        String archivos[] = fileList();

        /////////////////Se crea el archivo cobrador.txt///////////////
        if (archivo_existe(archivos, cobrador)) {
            check_activation();
        } else {
            crear_archivo(cobrador);
            agregar_linea_archivo("Cobrador1 FALSE " + fecha, cobrador);
            check_activation();
        }
        ////////////////////////////////////////////////////////////////

        /////////////////Se crea el archivo onlines.txt///////////////
        if (archivo_existe(archivos, onlines)) {
            check_onlines();
        } else {
            crear_archivo(onlines);
        }
        ////////////////////////////////////////////////////////////////
    }

    private void salir() {
        try {
            Toast.makeText(this, "Cobrador inactivo\nLa app se cierra ahora...", Toast.LENGTH_LONG).show();
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Toast.makeText(this, "Cobrador inactivo\nLa app se cierra ahora...", Toast.LENGTH_LONG).show();
            Thread.sleep(1500);
            finish();
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void check_box_listener(View view) {
        String texto = "";
        if (checkedTextView.isChecked()) {
            et_ID.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            et_ID.setFocusableInTouchMode(true);
            texto = et_ID.getText().toString();
            et_ID.setText(texto);
        } else {
            et_ID.setTransformationMethod(PasswordTransformationMethod.getInstance());
            et_ID.setFocusableInTouchMode(true);
            texto = et_ID.getText().toString();
            et_ID.setText(texto);
        }
    }

    private void text_listener() {
        et_ID.setText("");
        et_ID.setEnabled(true);
        et_ID.setFocusableInTouchMode(true);
        et_ID.requestFocus();
        //Implementacion de un text listener
        et_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tv_esperar.getText().toString().equals("Ingrese su codigo de cobrador...") | tv_esperar.getText().toString().equals("Debe ingresar un codigo valido!")) {
                    String codigo = et_ID.getText().toString();//Se parcea el valor a un string
                    if (codigo.length() == 11) {
                        tv_esperar.setText("Conectando, por favor espere...");
                        et_ID.setFocusableInTouchMode(false);
                        et_ID.setEnabled(false);
                        ocultar_teclado();
                        boolean aceptado = verificar_codigo(codigo);
                        if (aceptado) {
                            check_activation_online(codigo);
                            //gen_personalized_vars();
                        } else {
                            tv_esperar.setText("Debe ingresar un codigo valido!");
                            msg("Debe ingresar un codigo valido!");
                            et_ID.setText("");
                            et_ID.setFocusableInTouchMode(true);
                            et_ID.requestFocus();
                            text_listener();
                        }
                    } else {
                        //
                    }
                } else {
                    //Do nothing. Significa que esta pidiendo usuario y/o password
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private Boolean verificar_codigo(String codigo){
        boolean retorno = true;
        for (int i = 0; i < codigo.length(); i++){
            if (i == 0) {
                String valor = String.valueOf(codigo.charAt(i));
                Log.v("verificar_codigo " + String.valueOf(i), "Valor a evaluar: " + valor);
                if (valor.equals("C")) {
                    //Do nothing. todo bien!
                } else {
                    retorno = false;
                    break;
                }
            } else {
                String valor = String.valueOf(codigo.charAt(i));
                Log.v("verificar_codigo " + String.valueOf(i), "Valor a evaluar: " + valor);
                boolean isNumeric = (valor != null && valor.matches("[0-9]"));
                if (isNumeric) {
                    //Do nothing. Todo bien!
                } else {
                    retorno = false;
                    break;
                }
            }
        }
        return retorno;
    }

    private void check_activation() throws JSONException {
        ocultar_todo();
        String archivos[] = fileList();
        boolean crear = true;
        for (int i = 0; i < archivos.length; i++) {
            Pattern pattern = Pattern.compile("a_sfile_cobrador_sfile_a", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                //abrir archivo y leerlo crear_loteria_de crear_loteria_demo mo
                Log.v("ErrorCrearLoterias", "Archivo encontrado: " + archivos[i] + "\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                crear = false;
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String[] split = linea.split(" ");
                    if (split[1].equals("TRUE") && split[2].equals(fecha)) {
                        mostrar_todo();
                        Toast.makeText(this, "Bienvenido " + nombre_cobra, Toast.LENGTH_LONG).show();
                        autenticar_cobrador(split[0]);
                        br.close();
                        archivo.close();
                        break;
                    } else if (split[1].equals("FALSE") && split[2].equals(fecha)) {
                        if (verificar_internet()) {
                            ocultar_todito();
                            text_listener();
                            br.close();
                            archivo.close();
                            break;
                        } else {
                            Toast.makeText(this, "Debe estar conectado a Internet!", Toast.LENGTH_SHORT).show();
                            salir();
                            br.close();
                            archivo.close();
                            break;
                        }
                    } else {
                        ocultar_todito();
                        text_listener();
                        br.close();
                        archivo.close();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                //crear = true; (No hace falta hacer esto porque arriba se hizo!!!)
            }
        }

        if (crear) {
            crear_archivoS();
            ocultar_todito();
            text_listener();
        } else {
            //TODO Do nothing.
        }
    }

    private void check_activation_online(String codigo) {//codigo corresponde al ID del cobrador.

        boolean dato_listo = true;

        if (dato_listo) {
            RequestQueue requestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            BasicNetwork network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);

            // Start the queue
            requestQueue.start();

            String url = readRowURL + spreadsheet_cobradores + "&sheet=" + sheet_cobradores;

            Log.v("Crear file active URL ", ".\nurl: " + url + "\n.");

            // Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(String response) {
                            // Do something with the response
                            Log.v("check_activ config 0", ".\nResponse:\n" + response);
                            if (response != null) {
                                String[] split = response.split("estado");
                                for (int i = 1; i < split.length; i++) {
                                    String[] split2 = split[i].split("\"");
                                    Log.v("check_activ config 1", ".\nSplit:\nSplit22: " + split2[22] + ", Split2: " + split2[2] + "\net_ID.getText().toString(): " + et_ID.getText().toString() + "\n");
                                    if (split2[22].equals(codigo)) {
                                        if (split2[2].equals("TRUE")) {
                                            try {
                                                InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
                                                BufferedReader br = new BufferedReader(archivo);
                                                String linea = br.readLine();
                                                Log.v("Linea_active_online", ".\n\nLinea: " + linea + "\n\n.");
                                                String[] split_linea_1 = linea.split(" ");
                                                linea = linea.replace(split_linea_1[0], codigo);
                                                linea = linea.replace(split_linea_1[1], split2[2]);
                                                linea = linea.replace(split_linea_1[2], fecha);
                                                String contenido = linea + "\n";
                                                br.close();
                                                archivo.close();
                                                nombre_cobra = split2[6];
                                                contenido = contenido + "nombre " + split2[6] + "\n";
                                                contenido = contenido + "usuario " + split2[26] + "\n";
                                                contenido = contenido + "password " + split2[10] + "\n";
                                                contenido = contenido + "Screditos " + split2[14] + "\n";
                                                contenido = contenido + "Sclientes " + split2[18];
                                                borrar_archivo(cobrador);
                                                guardar(contenido, cobrador);
                                                Log.v("Debug_file_cobra", ".\n\nArchivo cobrador.txt:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            //Continua trabajando con la app.
                                            mostrar_todito();
                                            //msg("Bienvenido " + nombre_cobra);
                                            autenticar_cobrador(codigo);
                                            break;
                                        } else {//Cobrador inactivo. Se cierra la app.
                                            //crear_archivo("vent_active.txt");
                                            //agregar_linea_archivo("vent_active.txt", "FALSE_separador_" + codigo + "_separador_" + sid_loterias + "_separador_" + sid_vendidas + "_separador_" + fecha + "_separador_" + split2[30]);
                                            tv_esperar.setText("Cobrador inactivo. La app se cierra ahora...");
                                            esperar();
                                            break;
                                        }
                                    } else {
                                        tv_esperar.setText("Debe ingresar un codigo valido!");
                                        msg("Codigo invalido!");
                                        msg("Debe ingresar un codigo valido!");
                                        text_listener();
                                    }
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            try {
                                check_activation();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Handle error
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        } else {
            //Do nothing.
        }
    }

    private void verificar_usuario(String codigo) {//codigo corresponde al ID del cobrador.

        boolean dato_listo = true;

        if (dato_listo) {
            RequestQueue requestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            BasicNetwork network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);

            // Start the queue
            requestQueue.start();

            String url = readRowURL + spreadsheet_cobradores + "&sheet=" + sheet_cobradores;

            Log.v("verificar_usuario()", ".\nurl: " + url + "\n.");

            // Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(String response) {
                            // Do something with the response
                            Log.v("ver_activ config 0", ".\nResponse:\n" + response);
                            if (response != null) {
                                String[] split = response.split("estado");
                                for (int i = 1; i < split.length; i++) {
                                    String[] split2 = split[i].split("\"");
                                    Log.v("verif_usrs config 0", ".\nSplit:\nSplit22: " + split2[22] + ", Split26: " + split2[26] + "\net_ID.getText().toString(): " + et_ID.getText().toString() + "\n");
                                    if (split2[22].equals(codigo)) {
                                        if (split2[26].equals(et_ID.getText().toString())) {
                                            mostrar_todito();
                                            //msg("Bienvenido " + nombre_cobra);
                                            autenticar_cobrador2(codigo);
                                            break;
                                        } else {//Cobrador inactivo. Se cierra la app.
                                            msg("Nombre de usuario incorrecto. Trate de nuevo");
                                            tv_esperar.setText("Nombre de usuario incorrecto. Trate de nuevo");
                                            mostrar_todito();
                                            autenticar_cobrador(codigo);
                                            break;
                                        }
                                    } else {
                                        //Do nothing, continue
                                    }
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        } else {
            //Do nothing.
        }
    }

    private void verificar_password(String codigo) {//codigo corresponde al ID del cobrador.

        boolean dato_listo = true;

        if (dato_listo) {
            RequestQueue requestQueue;

            // Instantiate the cache
            Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

            // Set up the network to use HttpURLConnection as the HTTP client.
            BasicNetwork network = new BasicNetwork(new HurlStack());

            // Instantiate the RequestQueue with the cache and network.
            requestQueue = new RequestQueue(cache, network);

            // Start the queue
            requestQueue.start();

            String url = readRowURL + spreadsheet_cobradores + "&sheet=" + sheet_cobradores;

            Log.v("verificar_password()", ".\nurl: " + url + "\n.");

            // Formulate the request and handle the response.
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    new Response.Listener<String>() {
                        @RequiresApi(api = Build.VERSION_CODES.N)
                        @Override
                        public void onResponse(String response) {
                            // Do something with the response
                            Log.v("verif_pass config 0", ".\nResponse:\n" + response);
                            if (response != null) {
                                String[] split = response.split("estado");
                                for (int i = 1; i < split.length; i++) {
                                    String[] split2 = split[i].split("\"");
                                    Log.v("verif_pass config 1", ".\nSplit:\nSplit22: " + split2[22] + ", Split10: " + split2[10] + "\net_ID.getText().toString(): " + et_ID.getText().toString() + "\n");
                                    if (split2[22].equals(codigo)) {
                                        if (split2[10].equals(et_ID.getText().toString())) {
                                            //Continua trabajando con la app.
                                            mostrar_todito();
                                            msg("password aceptado!!!");
                                            checkedTextView.setVisibility(View.INVISIBLE);
                                            boton_submit.setVisibility(View.INVISIBLE);
                                            menu_principal();
                                            break;
                                        } else {
                                            msg("Password incorrecto. Trate de nuevo");
                                            tv_esperar.setText("Password incorrecto. Trate de nuevo");
                                            mostrar_todito();
                                            autenticar_cobrador2(codigo);
                                            break;
                                        }
                                    } else {
                                        //Do nothing, continue
                                    }
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // Handle error
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        } else {
            //Do nothing.
        }
    }

    private void autenticar_cobrador (String codigo) {
        ID_cobrador = codigo;
        ocultar_todito();
        boton_submit.setVisibility(View.VISIBLE);
        boton_submit.setClickable(true);
        tv_esperar.setText("Digite su usuario");
        et_ID.setEnabled(true);
        et_ID.setText("");
        et_ID.setFocusableInTouchMode(true);
        et_ID.setHint("USUARIO");
    }

    private void autenticar_cobrador2 (String codigo) {
        checkedTextView.setChecked(false);
        ID_cobrador = codigo;
        ocultar_todito();
        boton_submit.setVisibility(View.VISIBLE);
        boton_submit.setClickable(true);
        checkedTextView.setVisibility(View.VISIBLE);
        tv_esperar.setText("Digite su password");
        et_ID.setEnabled(true);
        et_ID.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_ID.setText("");
        et_ID.setFocusableInTouchMode(true);
        et_ID.setHint("PASSWORD");
    }

    public void submit(View view) {

        et_ID.setFocusableInTouchMode(false);
        et_ID.setEnabled(false);
        boton_submit.setClickable(false);
        boton_submit.setVisibility(View.INVISIBLE);
        if (tv_esperar.getText().toString().equals("Digite su usuario")) {
            tv_esperar.setText("Conectando, por favor espere...");
            et_ID.setFocusableInTouchMode(false);
            et_ID.setEnabled(false);
            verificar_usuario(ID_cobrador);
        } else if (tv_esperar.getText().toString().equals("Digite su password")) {
            tv_esperar.setText("Conectando, por favor espere...");
            et_ID.setFocusableInTouchMode(false);
            et_ID.setEnabled(false);
            verificar_password(ID_cobrador);
        } else {
            //Do nothing.
        }
    }

    //Funciones comunes//

    private void esperar () {
        ocultar_todito();
        Toast.makeText(this, "Cobrador inactivo. La app se cierra ahora...", Toast.LENGTH_LONG).show();
        tv_esperar.setText("Cobrador inactivo. La app se cierra ahora...");
        for (int i = 0; i > 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mostrar_todito();
        salir();
    }

    public  void borrar_archivo(String file) throws IOException {
        File archivo = new File(file);
        String empty_string = "";
        guardar(empty_string, file);
        archivo.delete();
    }

    public  void guardar (String contenido, String file_name) throws IOException {
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(file_name, Activity.MODE_PRIVATE));
            archivo.write(contenido);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
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
                e.printStackTrace();
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
            e.printStackTrace();
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
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed(){
        boton_atras();
    }

    private void boton_atras() {
        //ocultar_teclado();
        finish();
        System.exit(0);
    }

    private void mostrar_todito() {
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        et_ID.setVisibility(View.INVISIBLE);
    }

    private void ocultar_todito() {
        Log.v("ocultar_todito", "Se hace todo invisible");
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("Ingrese su codigo de cobrador...");
        et_ID.setVisibility(View.VISIBLE);
    }

    private void ocultar_todo() {
        ocultar_todito();
        //TODO: todo
    }

    private void mostrar_todo() {
        mostrar_todito();
        //TODO: todo
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
                e.printStackTrace();
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
                    /*} else if (split[0].equals("BORRADA")) {
                        Log.v("OJOF_BORRADA: ", "\n\nLinea: " + linea + " Fin de linea!!!");
                        //TODO: Pensar que hacer!!!*/
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
                //mostrar_todo();
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
                            String factura_num = split[15];
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

}