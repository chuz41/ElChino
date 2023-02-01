package com.example.elchino;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.CrearArchivo;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String mes;
    private String dia;
    private String anio;
    private String fecha;
    private String hora;
    private String minuto;
    private String cobrador = "a_sfile_cobrador_sfile_a.txt";
    private EditText et_ID;
    private TextView tv_esperar;
    private String spreadsheet_cobradores = "1y5wRGgrkH48EWgd2OWwon_Um42mxN94CdmJSi_XCwvM";
    private String readRowURL = "https://script.google.com/macros/s/AKfycbxJNCrEPYSw8CceTwPliCscUtggtQ2l_otieFmE/exec?spreadsheetId=";
    private String sheet_cobradores = "cobradores";
    private String nombre_cobra = "";
    private Button boton_submit;
    private String ID_cobrador;
    private CheckBox checkedTextView;
    private Boolean flag_caja = false;
    private String caja = "caja.txt";
    private Date hoy_LD = new Date();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        boton_submit = (Button) findViewById(R.id.boton_submit);
        boton_submit.setVisibility(View.INVISIBLE);
        checkedTextView = (CheckBox) findViewById(R.id.checkedTextView);
        checkedTextView.setText("Mostrar password");
        checkedTextView.setVisibility(View.INVISIBLE);
        hoy_LD = Calendar.getInstance().getTime();
        separarFecha();
        menu_principal("Se recomienda usar bloqueo de pantalla!!!");
        check_activation();
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

    private void guardar_datos_cobrador () {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String archivo_completo = "";
            while (linea != null) {
                String[] split = linea.split(" ");
                if (split[0].equals("cobrador_ID")) {
                    linea = linea.replace("cobrador_ID " + split[1], "cobrador_ID " + ID_cobrador);
                    archivo_completo = archivo_completo + linea + "\n";
                } else {
                    archivo_completo = archivo_completo + linea + "\n";
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            new BorrarArchivo(cobrador, getApplicationContext());
            if (new GuardarArchivo(cobrador, archivo_completo, getApplicationContext()).guardarFile()) {
                Log.v("guardar_datos_cobrador0", "Main.\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
            } else {
                Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            }
            Log.v("guardar_datos_cobrador1", "Main.\n\narchivo cobrador: " + cobrador + "\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
        } catch (IOException e) {
        }
    }

    public void abonar(){
        Intent abonar = new Intent(this, AbonarActivity.class);
        abonar.putExtra("msg", "");
        abonar.putExtra("cliente_recivido", "");
        abonar.putExtra("abono_cero", "");
        startActivity(abonar);
        finish();
        System.exit(0);
    }

    private void menu_principal (String messag) {
        Intent menu_principal = new Intent(this, MenuPrincipal.class);
        menu_principal.putExtra("mensaje", messag);
        startActivity(menu_principal);
        finish();
        System.exit(0);
    }

    private void crear_archivoS () {

        String archivos[] = fileList();

        /////////////////Se crea el archivo cobrador.txt///////////////
        if (archivo_existe(archivos, cobrador)) {
            check_activation();
        } else {
            new CrearArchivo(cobrador, getApplicationContext());
            new AgregarLinea("Cobrador1 FALSE " + fecha, cobrador, getApplicationContext());
            check_activation();
        }
        ////////////////////////////////////////////////////////////////

        ////////////////////Se crea el archivo caja.txt/////////////////
        if (archivo_existe(archivos, caja)) {
            //Do nothing.
        } else {
            new CrearArchivo(caja, getApplicationContext());
            flag_caja = true;
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
                        //ocultar_teclado();
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
                } else if (tv_esperar.getText().toString().equals("Ingrese el monto inicial de caja...")) {
                    if (s.toString().equals("")) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else if (s == null) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else {
                        boton_submit.setEnabled(true);
                        boton_submit.setClickable(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite su password")) {
                    Log.v("Digite_password", ".\n\nS: " + s + "\n\n.");
                    if (s.toString().equals("")) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else if (s == null) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else {
                        boton_submit.setEnabled(true);
                        boton_submit.setClickable(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite su usuario")) {
                    Log.v("Digite_usuario", ".\n\nS: " + s + "\n\n.");
                    if (s.toString().equals("")) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else if (s == null) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else {
                        boton_submit.setEnabled(true);
                        boton_submit.setClickable(true);
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
                Log.v("verificar_codigo" + String.valueOf(i), "Valor a evaluar: " + valor);
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

    private void check_activation () {
        String archivos[] = fileList();
        boolean crear = true;
        for (int i = 0; i < archivos.length; i++) {
            Pattern pattern = Pattern.compile("a_sfile_cobrador_sfile_a", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                //abrir archivo y leerlo crear_loteria_de crear_loteria_demo mo
                Log.v("check_activation_0", "Main.\n\nArchivo encontrado: " + archivos[i] + "\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                crear = false;
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String[] split = linea.split(" ");
                    Log.v("Check_activation_1", "Main.\n\nLinea: " + linea + "\n\nFecha: " + fecha + "\n\nsplit[1]: " + split[1] + "\n\nsplit[2]: "+ split[2] + "\n\n.");
                    if (split[1].equals("TRUE") && split[2].equals(fecha)) {
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
                //Do nothing.
            }
        }
        if (crear) {
            crear_archivoS();
            ocultar_todito();
            text_listener();
        } else {
            //Do nothing.
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
                            Log.v("check_activ_online1", "Main.\n\nResponse:\n" + response + "\n\n.");
                            if (response != null) {
                                String[] split = response.split("estado");
                                for (int i = 1; i < split.length; i++) {
                                    String[] split2 = split[i].split("\"");
                                    Log.v("check_activ_online1", "Main.\n\nSplit22: " + split2[22] + ", Split2: " + split2[2] + "\net_ID.getText().toString(): " + et_ID.getText().toString() + "\n");
                                    if (split2[22].equals(codigo)) {
                                        if (split2[2].equals("TRUE")) {
                                            try {
                                                InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
                                                BufferedReader br = new BufferedReader(archivo);
                                                String linea = br.readLine();
                                                Log.v("check_activ_online2", ".\n\nLinea: " + linea + "\n\n.");
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
                                                contenido = contenido + "Sclientes " + split2[18] + "\n";
                                                contenido = contenido + "apodo " + split2[34] + "\n";
                                                contenido = contenido + "telefono " + split2[30];
                                                new BorrarArchivo(cobrador, getApplicationContext());
                                                if (new GuardarArchivo(cobrador, contenido, getApplicationContext()).guardarFile()) {
                                                    Log.v("guardar_datos_cobrador0", "Main.\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                                                } else {
                                                    msg("*** ERROR al crear el archivo. ***");
                                                    msg("Informe a soporte tecnico!");
                                                }
                                                Log.v("check_activ_online3", ".Main\n\nArchivo cobrador.txt:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                            //Continua trabajando con la app.
                                            mostrar_todito();
                                            autenticar_cobrador(codigo);
                                            break;
                                        } else {//Cobrador inactivo. Se cierra la app.
                                            tv_esperar.setText("Cobrador inactivo. La app se cierra ahora...");
                                            esperar();
                                            break;
                                        }
                                    } else {
                                        tv_esperar.setText("Debe ingresar un codigo valido!");
                                        //msg("Codigo invalido!");
                                        //msg("Debe ingresar un codigo valido!");
                                        text_listener();
                                    }
                                }
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            check_activation();
                            // Handle error
                        }
                    });
            // Add the request to the RequestQueue.
            requestQueue.add(stringRequest);
        } else {
            //Do nothing.
        }
    }

    private void verificar_usuario (String codigo) {//codigo corresponde al ID del cobrador.

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

    private void verificar_password (String codigo) {//codigo corresponde al ID del cobrador.

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
                                            if (flag_caja) {
                                                pedir_caja();
                                            } else {
                                                guardar_datos_cobrador();
                                                menu_principal("");
                                            }
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

    private void pedir_caja () {
        boton_submit.setVisibility(View.VISIBLE);
        boton_submit.setClickable(true);
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("Ingrese el monto inicial de caja...");
        et_ID.setEnabled(true);
        et_ID.setVisibility(View.VISIBLE);
        et_ID.setText("");
        et_ID.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_ID.setFocusableInTouchMode(true);
        et_ID.setHint("MONTO CAJA INICIAL");
        et_ID.requestFocus();
        text_listener();
    }

    private void autenticar_cobrador (String codigo) {
        ID_cobrador = codigo;
        ocultar_todito();
        boton_submit.setVisibility(View.VISIBLE);
        boton_submit.setClickable(false);
        boton_submit.setEnabled(false);
        tv_esperar.setText("Digite su usuario");
        et_ID.setEnabled(true);
        et_ID.setText("");
        et_ID.setFocusableInTouchMode(true);
        et_ID.setHint("USUARIO");
        text_listener();
    }

    private void autenticar_cobrador2 (String codigo) {
        checkedTextView.setChecked(false);
        ID_cobrador = codigo;
        ocultar_todito();
        boton_submit.setVisibility(View.VISIBLE);
        boton_submit.setClickable(false);
        boton_submit.setEnabled(false);
        checkedTextView.setVisibility(View.VISIBLE);
        tv_esperar.setText("Digite su password");
        et_ID.setEnabled(true);
        et_ID.setTransformationMethod(PasswordTransformationMethod.getInstance());
        et_ID.setText("");
        et_ID.setFocusableInTouchMode(true);
        et_ID.setHint("PASSWORD");
        text_listener();
    }

    public void submit (View view) throws IOException {

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
        } else if (tv_esperar.getText().toString().equals("Ingrese el monto inicial de caja...")) {
            if (et_ID.getText().toString().equals("")) {
                //Do nothing.
            } else if (et_ID.getText() == null) {
                //Do nothing.
            } else {
                tv_esperar.setText("Conectando, por favor espere...");
                et_ID.setFocusableInTouchMode(false);
                et_ID.setEnabled(false);
                guardar_caja(et_ID.getText().toString());
            }
        } else {
            //Do nothing.
        }
    }

    private void guardar_caja (String cajita) throws IOException {
        if (new GuardarArchivo(caja, "caja " + cajita, getApplicationContext()).guardarFile()) {
            Log.v("guardar_caja_0", "Main.\n\nContenido del archivo:\n\n" + imprimir_archivo(caja) + "\n\n.");
        } else {
            Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
        }
        menu_principal("Caja inicial: " + cajita);
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

    private boolean verificar_internet() {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            Toast.makeText(this, "Debe estar conectado a una red WiFi o datos mobiles.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            return true;
        }
    }

}