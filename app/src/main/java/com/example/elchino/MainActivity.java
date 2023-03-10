package com.example.elchino;

import static com.example.elchino.Util.DateUtilities.*;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.example.elchino.Clases_comunes.Cliente;
import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.CrearArchivo;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity {

    private String fecha;
    private final String cobrador = "a_sfile_cobrador_sfile_a.txt";
    private EditText et_ID;
    private TextView tv_esperar;
    private final String spreadsheet_cobradores = "1y5wRGgrkH48EWgd2OWwon_Um42mxN94CdmJSi_XCwvM";
    private final String readRowURL = "https://script.google.com/macros/s/AKfycbyx-sv9enLGRWxYsFiXzcUhRIysGzzSeJa-wS8fXCMHUQk7iCQRzyAPb-17pUWryy5j/exec?spreadsheetId=";
    private final String readSheets = "https://script.google.com/macros/s/AKfycbzX2Z4Jm7g7qTYXpUFz1SXOG9w9-or9u3ZNFmUYZ98tI_Nrw-36Wz1XYODm68Ad8bJ1vA/exec?spreadsheetId=";
    private final String sheet_cobradores = "cobradores";
    private String nombre_cobra = "";
    private Button boton_submit;
    private String ID_cobrador;
    private CheckBox checkedTextView;
    private Boolean flag_caja = false;
    private final String caja = "caja.txt";
    private Date hoy_LD = new Date();
    private String spreadsheet_clientes = "1mZ6hvVIW1_EHcaP8KbJJfRv6yhb03WALG00SXL2MX74";
    private String spreadsheet_creditos;
    private Integer cajita = 0;
    private HashMap<String, Cliente> clientesEncontrados = new HashMap<>();//Contendra a todos los clientes leidos y guardados.
    private Map<String, String> sheetsLeidas = new HashMap<>();
    private Map<String, String> creditosLeidos = new HashMap<>();
    private Map<String, String> abonosLeidos = new HashMap<>();
    private Map<String, String> abonosGuardar = new HashMap<>();
    private Map<String, String> archivosGuardar = new HashMap<>();
    private Integer contSheets = 0;
    private ProgressBar progressBar;
    private Integer contadorBarra = 0;
    private TextView tvProgressBar;
    private String contenidoCier;
    private String contenidoCier2 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvProgressBar = (TextView) findViewById(R.id.tvProgressBar);
        tvProgressBar.setVisibility(View.INVISIBLE);
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.INVISIBLE);
        et_ID = (EditText) findViewById(R.id.et_ID);
        boton_submit = (Button) findViewById(R.id.boton_submit);
        boton_submit.setVisibility(View.INVISIBLE);
        checkedTextView = (CheckBox) findViewById(R.id.checkedTextView);
        checkedTextView.setVisibility(View.INVISIBLE);
        hoy_LD = Calendar.getInstance().getTime();
        separarFecha();
        contenidoCier = "fecha " + fecha + "\n";
        try {
            check_activation();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cargarData () {

        mostrarBarra("Leyendo informacion de la nube...", 4);

        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String sheetClientes = "clientes";
        String url = readRowURL + spreadsheet_clientes + "&sheet=" + sheetClientes;
        progressBar.setProgress(0);
        //Log.v("cargarData_0", "Main.\n\nurl: " + url + "\n\n.");

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    if (response != null) {
                        //Log.v("cargarData_1", "Main.\n\nresponse:\n\n" + response + "\n\n.");
                        if (response.contains("DOCTYPE")) {
                            cargarData();
                        } else {
                            String[] split = response.split("ID_cliente");
                            int progresoMedio = ((split.length) / 2);
                            guardarClientes(split);
                            progressBar.setProgress(progresoMedio);
                        }
                    }
                },
                error -> {
                    cargarData();
                });
        requestQueue.add(stringRequest);// Add the request to the RequestQueue.
    }

    private void guardarClientes(String[] clientesArrary) {//Depende del formato Json para funcionar.
        int divisionesBarra = (clientesArrary.length);
        mostrarBarra("Guardando clientes...", divisionesBarra);
        progressBar.setProgress(0);
        int cont = 0;
        for (String infoToFile : clientesArrary) {
            //Log.v("guardarClientes_0", "info: " + infoToFile + ".\n\nclientesArray.length(): " + clientesArrary.length + "\n\n.");
            String[] split = infoToFile.split("\",\"");
            String u0,u1,u2,u3,u4,u6,u9,u10,u11,u12,u13,newFile;//u0 = clienteID
            u0=u1=u2=u3=u4=u12=u13=u9=u10=u6=u11=newFile=null;
            boolean flagControl = false;
            for (String clienteInfo : split) {

                if (split.length > 10) {
                    flagControl = true;
                    //Log.v("guardarClientes_*", "split.length: " + split.length + ", InfoCliente: " + clienteInfo + ".");
                    String[] splitInfo = clienteInfo.split("\":\"");
                    if (splitInfo[0].isEmpty()) {
                        //Log.v("guardarCliente_#", "Main.\nID_cliente: " + splitInfo[1] + "\n.");
                        u0 = splitInfo[1];
                        newFile = splitInfo[1] + "_C_.txt";
                    } else if (splitInfo[0].equals("nombre_cliente")) {
                        u1 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("apellido1_cliente")) {
                        u2 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("apellido2_cliente")) {
                        u3 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("apodo_cliente")) {
                        u4 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("telefono1_cliente")) {
                        u12 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("telefono2_cliente")) {
                        u13 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("notas_cliente")) {
                        u9 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("direccion_cliente")) {
                        u10 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("puntuacion_cliente")) {
                        u11 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    } else if (splitInfo[0].equals("monto_disponible")) {
                        u6 = splitInfo[1];
                        //Log.v("guardarCliente_#", "Main.\n" + splitInfo[0] + ": " + splitInfo[1] + "\n.");
                    }
                    cont++;
                    progressBar.setProgress(cont);
                } else {
                    flagControl = false;
                }

            }
            if (flagControl) {
                //u0 ID_cliente, u1 nombre_cliente, u2 apellido1_cliente, u3 apellido2_cliente, u4 apodo_cliente, u12 telefono1_cliente, u13 telefono2_cliente, u9 notas_cliente, u10 direccion_cliente, u11 puntuacion_cliente, u6 monto_disponible, "arriba" estado_archivo) {
                //if (u6 == null) throw new AssertionError();//
                Cliente cliente = new Cliente(u0, u1, u2, u3, u4, u12, u13, u9, u10, Integer.parseInt(u6), u11, "arriba");
                String idEncontrado = "ninguno";
                if (!clientesEncontrados.containsKey(u0)) {
                    boolean flagEncontrado = false;
                    for (String key : clientesEncontrados.keySet()) {
                        if (clientesEncontrados.get(key).getId().contains(u0)) {
                            flagEncontrado = true;
                            idEncontrado = clientesEncontrados.get(key).getId();
                        }
                        if (u0.contains(clientesEncontrados.get(key).getId())) {
                            flagEncontrado = true;
                            idEncontrado = clientesEncontrados.get(key).getId();
                        }
                    }

                    if (!flagEncontrado) {
                        clientesEncontrados.put(u0, cliente);
                        if (new GuardarArchivo(cliente, newFile, "arriba", getApplicationContext()).guardarCliente()) {
                            //Log.v("guardarCliente_0", "Main.\n\nContenido del archivo:\n\n" + imprimir_archivo(newFile) + "\n\n.");
                        } else {
                            Toast.makeText(this, "*** ERROR ***", Toast.LENGTH_LONG).show();
                        }
                    } else {
                        Log.v("guardarClientes_1", "Main.\n\nCliente ya se ha ingresado:\n\n" + clientesEncontrados.get(idEncontrado) + "\n\n.");
                    }
                }
            }
        }
        getSheets();
    }

    private void mostrarBarra (String s, int divisionesBarra) {
        progressBar.setVisibility(View.VISIBLE);
        tv_esperar.setVisibility(View.INVISIBLE);
        tvProgressBar.setVisibility(View.VISIBLE);
        tvProgressBar.setText(s);
        et_ID.setVisibility(View.INVISIBLE);
        boton_submit.setVisibility(View.INVISIBLE);
        checkedTextView.setVisibility(View.INVISIBLE);
        progressBar.setMax(divisionesBarra);
        progressBar.setProgress(0);
    }

    private void getSheets () {

        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        // Generate the url
        String url = readSheets + spreadsheet_creditos;

        //Log.v("getCreditos_0", "Main.\n\nurl: " + url + "\n\n.");

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    if (response != null) {
                        //Log.v("getCreditos_1", "Main.\n\nresponse:\n\n" + response + "\n\n.");
                        //response: [{"sheetName":"creditos","data":[["monto_credito","plazo_credito","monto_cuota","fecha_credito","fecha_proxima_cuota","saldo_mas_intereses","tasa","cuotas","ID_credito","moroso","cuadratura","intereses_moratorios","monto_abonado","estado_archivo"]]},{"sheetName":"Wed-Feb-01-2023-abonos","data":[["monto_credito","plazo_credito","monto_cuota","fecha_credito","fecha_proxima_cuota","saldo_mas_intereses","tasa","cuotas","ID_credito","moroso","cuadratura","intereses_moratorios","monto_abonado","estado_archivo"],[100000,"6_semanas_(20%)",20000,"2023-02-01T06:00:00.000Z","2023-02-15T06:00:00.000Z",100000,20,5,"25570207_P_1_P_","D","semana_1_0_08/2/2023__semana_2_20000_15/2/2023__semana_3_20000_22/2/2023__semana_4_20000_01/3/2023__semana_5_20000_08/3/2023__semana_6_20000_15/3/2023__",0,20000,""],[50000,"5_semanas_(20%)",12000,"2023-01-30T06:00:00.000Z","2023-03-13T06:00:00.000Z",0,20,0,"333333333_P_1_P_","D","semana_1_0_06/2/2023__semana_2_0_13/2/2023__semana_3_0_20/2/2023__semana_4_0_27/2/2023__semana_5_0_06/3/2023__",0,60000,""],[100000,"9_semanas_(40%)",15555,"2023-02-01T06:00:00.000Z","2023-02-15T06:00:00.000Z",124445,40,9,"222222222_P_2_P_","D","semana_1_0_08/2/2023__semana_2_15555_15/2/2023__semana_3_15555_22/2/2023__semana_4_15555_01/3/2023__semana_5_15555_08/3/2023__semana_6_15555_15/3/2023__semana_7_15555_22/3/2023__semana_8_15555_29/3/2023__semana_9_15555_05/4/2023__",0,15555,"abajo"],[50000,"6_meses_(85%)",15416,"2023-01-28T06:00:00.000Z","2023-03-28T06:00:00.000Z",77084,85,6,"333333333_P_3_P_","D","mes_1_0_28/2/2023__mes_2_15416_28/3/2023__mes_3_15416_28/4/2023__mes_4_15416_28/5/2023__mes_5_15416_28/6/2023__mes_6_15416_28/7/2023__",0,15416,"abajo"],[100000,"9_semanas_(40%)",15555,"2023-01-04T06:00:00.000Z","2023-02-01T06:00:00.000Z",93335,40,9,"222222222_P_1_P_","D","semana_1_0_11/1/2023__semana_2_0_18/1/2023__semana_3_0_25/1/2023__semana_4_15555_01/2/2023__semana_5_15555_08/2/2023__semana_6_15555_15/2/2023__semana_7_15555_22/2/2023__semana_8_15555_01/3/2023__semana_9_15555_08/3/2023__",0,56665,"abajo"],[100000,"9_semanas_(40%)",15555,"2023-01-04T06:00:00.000Z","2023-02-01T06:00:00.000Z",93335,40,9,"222222222_P_1_P_","D","semana_1_0_11/1/2023__semana_2_0_18/1/2023__semana_3_0_25/1/2023__semana_4_15555_01/2/2023__semana_5_15555_08/2/2023__semana_6_15555_15/2/2023__semana_7_15555_22/2/2023__semana_8_15555_01/3/2023__semana_9_15555_08/3/2023__",-7000,7000,"abajo"],[100000,"9_semanas_(40%)",15555,"2023-02-01T06:00:00.000Z","2023-02-22T06:00:00.000Z",108890,40,9,"222222222_P_2_P_","D","semana_1_0_08/2/2023__semana_2_0_15/2/2023__semana_3_15555_22/2/2023__semana_4_15555_01/3/2023__semana_5_15555_08/3/2023__semana_6_15555_15/3/2023__semana_7_15555_22/3/2023__semana_8_15555_29/3/2023__semana_9_15555_05/4/2023__",0,15555,"abajo"],[50000,"6_meses_(85%)",15416,"2023-01-28T06:00:00.000Z","2023-04-28T06:00:00.000Z",61668,85,6,"333333333_P_3_P_","D","mes_1_0_28/2/2023__mes_2_0_28/3/2023__mes_3_15416_28/4/2023__mes_4_15416_28/5/2023__mes_5_15416_28/6/2023__mes_6_15416_28/7/2023__",0,15416,"abajo"],[100000,"9_semanas_(40%)",15555,"2023-02-01T06:00:00.000Z","2023-03-01T06:00:00.000Z",93335,40,9,"222222222_P_2_P_","D","semana_1_0_08/2/2023__semana_2_0_15/2/2023__semana_3_0_22/2/2023__semana_4_15555_01/3/2023__semana_5_15555_08/3/2023__semana_6_15555_15/3/2023__semana_7_15555_22/3/2023__semana_8_15555_29/3/2023__semana_9_15555_05/4/2023__",0,15555,"abajo"]]},{"sheetName":"Wed-Feb-01-2023-creditos","data":[["monto_credito","plazo_credito","monto_cuota","fecha_credito","fecha_proxima_cuota","saldo_mas_intereses","tasa","cuotas","ID_credito","moroso","cuadratura","intereses_moratorios","monto_abonado","estado_archivo"],[100000,"6_semanas_(20%)",20000,"2023-02-01T06:00:00.000Z","2023-02-08T06:00:00.000Z",120000,20,6,"25570207_P_1_P_","D","semana_1_20000_08/2/2023__semana_2_20000_15/2/2023__semana_3_20000_22/2/2023__semana_4_20000_01/3/2023__semana_5_20000_08/3/2023__semana_6_20000_15/3/2023__",0,0,""],[100000,"9_semanas_(40%)",15555,"2023-01-04T06:00:00.000Z","2023-01-11T06:00:00
                        guardarCreditos(response);
                    }
                },
                error -> {
                    getSheets();
                });
        requestQueue.add(stringRequest);// Add the request to the RequestQueue.

        //Log.v("getCreditos_2", "Main.\n\nValidando getCreditos.\n\n.");
    }

    private void guardarCreditos (String data) {
        //response: [{"sheetName":"creditos","data":[["monto_credito","plazo_credito","monto_cuota","fecha_credito","fecha_proxima_cuota","saldo_mas_intereses","tasa","cuotas","ID_credito","moroso","cuadratura","intereses_moratorios","monto_abonado","estado_archivo"]]},{"sheetName":"Wed-Feb-01-2023-abonos","data":[["monto_credito","plazo_credito","monto_cuota","fecha_credito","fecha_proxima_cuota","saldo_mas_intereses","tasa","cuotas","ID_credito","moroso","cuadratura","intereses_moratorios","monto_abonado","estado_archivo"],[100000,"6_semanas_(20%)",20000,"2023-02-01T06:00:00.000Z","2023-02-15T06:00:00.000Z",100000,20,5,"25570207_P_1_P_","D","semana_1_0_08/2/2023__semana_2_20000_15/2/2023__semana_3_20000_22/2/2023__semana_4_20000_01/3/2023__semana_5_20000_08/3/2023__semana_6_20000_15/3/2023__",0,20000,""],[50000,"5_semanas_(20%)",12000,"2023-01-30T06:00:00.000Z","2023-03-13T06:00:00.000Z",0,20,0,"333333333_P_1_P_","D","semana_1_0_06/2/2023__semana_2_0_13/2/2023__semana_3_0_20/2/2023__semana_4_0_27/2/2023__semana_5_0_06/3/2023__",0,60000,""],[100000,"9_semanas_(40%)",15555,"2023-02-01T06:00:00.000Z","2023-02-15T06:00:00.000Z",124445,40,9,"222222222_P_2_P_","D","semana_1_0_08/2/2023__semana_2_15555_15/2/2023__semana_3_15555_22/2/2023__semana_4_15555_01/3/2023__semana_5_15555_08/3/2023__semana_6_15555_15/3/2023__semana_7_15555_22/3/2023__semana_8_15555_29/3/2023__semana_9_15555_05/4/2023__",0,15555,"abajo"],[50000,"6_meses_(85%)",15416,"2023-01-28T06:00:00.000Z","2023-03-28T06:00:00.000Z",77084,85,6,"333333333_P_3_P_","D","mes_1_0_28/2/2023__mes_2_15416_28/3/2023__mes_3_15416_28/4/2023__mes_4_15416_28/5/2023__mes_5_15416_28/6/2023__mes_6_15416_28/7/2023__",0,15416,"abajo"],[100000,"9_semanas_(40%)",15555,"2023-01-04T06:00:00.000Z","2023-02-01T06:00:00.000Z",93335,40,9,"222222222_P_1_P_","D","semana_1_0_11/1/2023__semana_2_0_18/1/2023__semana_3_0_25/1/2023__semana_4_15555_01/2/2023__semana_5_15555_08/2/2023__semana_6_15555_15/2/2023__semana_7_15555_22/2/2023__semana_8_15555_01/3/2023__semana_9_15555_08/3/2023__",0,56665,"abajo"],[100000,"9_semanas_(40%)",15555,"2023-01-04T06:00:00.000Z","2023-02-01T06:00:00.000Z",93335,40,9,"222222222_P_1_P_","D","semana_1_0_11/1/2023__semana_2_0_18/1/2023__semana_3_0_25/1/2023__semana_4_15555_01/2/2023__semana_5_15555_08/2/2023__semana_6_15555_15/2/2023__semana_7_15555_22/2/2023__semana_8_15555_01/3/2023__semana_9_15555_08/3/2023__",-7000,7000,"abajo"],[100000,"9_semanas_(40%)",15555,"2023-02-01T06:00:00.000Z","2023-02-22T06:00:00.000Z",108890,40,9,"222222222_P_2_P_","D","semana_1_0_08/2/2023__semana_2_0_15/2/2023__semana_3_15555_22/2/2023__semana_4_15555_01/3/2023__semana_5_15555_08/3/2023__semana_6_15555_15/3/2023__semana_7_15555_22/3/2023__semana_8_15555_29/3/2023__semana_9_15555_05/4/2023__",0,15555,"abajo"],[50000,"6_meses_(85%)",15416,"2023-01-28T06:00:00.000Z","2023-04-28T06:00:00.000Z",61668,85,6,"333333333_P_3_P_","D","mes_1_0_28/2/2023__mes_2_0_28/3/2023__mes_3_15416_28/4/2023__mes_4_15416_28/5/2023__mes_5_15416_28/6/2023__mes_6_15416_28/7/2023__",0,15416,"abajo"],[100000,"9_semanas_(40%)",15555,"2023-02-01T06:00:00.000Z","2023-03-01T06:00:00.000Z",93335,40,9,"222222222_P_2_P_","D","semana_1_0_08/2/2023__semana_2_0_15/2/2023__semana_3_0_22/2/2023__semana_4_15555_01/3/2023__semana_5_15555_08/3/2023__semana_6_15555_15/3/2023__semana_7_15555_22/3/2023__semana_8_15555_29/3/2023__semana_9_15555_05/4/2023__",0,15555,"abajo"]]},{"sheetName":"Wed-Feb-01-2023-creditos","data":[["monto_credito","plazo_credito","monto_cuota","fecha_credito","fecha_proxima_cuota","saldo_mas_intereses","tasa","cuotas","ID_credito","moroso","cuadratura","intereses_moratorios","monto_abonado","estado_archivo"],[100000,"6_semanas_(20%)",20000,"2023-02-01T06:00:00.000Z","2023-02-08T06:00:00.000Z",120000,20,6,"25570207_P_1_P_","D","semana_1_20000_08/2/2023__semana_2_20000_15/2/2023__semana_3_20000_22/2/2023__semana_4_20000_01/3/2023__semana_5_20000_08/3/2023__semana_6_20000_15/3/2023__",0,0,""],[100000,"9_semanas_(40%)",15555,"2023-01-04T06:00:00.000Z","2023-01-11T06:00:00
        data = data.replace("\\", "");
        data = data.replace("\"", "");
        data = data.replace("[", "");
        data = data.replace("]", "");
        //Log.v("guardarCreditos_0", "Main.\n\nResponse: " + data + "\n\n.");

        //Response: creditos,Wed-Feb-08-2023-abonos,Wed-Feb-08-2023-creditos,Fri-Nov-25-2022-creditos,Fri-Nov-25-2022-abonos,solicitudes,abonos,caja

        String[] splitsheetName = data.split(",");
        contSheets = splitsheetName.length;
        contSheets = contSheets * 2;
        contadorBarra = 0;
        mostrarBarra("Descargando creditos...", contSheets);
        for (String sheets : splitsheetName) {
            //Log.v("guardarCreditos_1", "Main.\n\nsheets: " + sheets + "\n\n.");
            if (!sheets.equals("solicitudes")) {
                sheetsLeidas.put(sheets, sheets);
            }
        }
        llenarMapas("first");
    }

    private void bajarInfoCreditos () {

        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String sheetLeer = null;
        String keyToRemoveCierre = null;
        for (String key : sheetsLeidas.keySet()) {
            if (!sheetsLeidas.get(key).contains("-cierre")) {
                sheetLeer = key;
                break;
            } else {
                keyToRemoveCierre = key;
            }
        }
        Log.v("bajarInfoCreditos_0", "Main.\n\nsheetLeer: " + sheetLeer + "\n\n.");
        if (keyToRemoveCierre != null) {
            sheetsLeidas.remove(keyToRemoveCierre);
        }

        // Formulate the request and handle the response.
        if (sheetLeer != null) {
            String url = readRowURL + spreadsheet_creditos + "&sheet=" + sheetLeer;

            String finalSheetLeer = sheetLeer;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        if (response != null) {
                            Log.v("bajarInfoCreditos_1", "Main.\n\nurl: " + url + "\n\n.");
                            Log.v("bajarInfoCreditos_2", "Main.\n\nresponse:\n\n" + response + "\n\n.");
                            if (response.contains("DOCTYPE")) {
                                Log.v("bajarInfoCreditos_3", "Main.\n\nDOCTYPE ERROR DOCTYPE ERROR DOCTYPE\n\nresponse:\n\n" + response + "\n\n.");
                                bajarInfoCreditos();
                            } else {
                                sheetsLeidas.remove(finalSheetLeer);
                                if (!response.equals("[]")) {
                                    if (contadorBarra <= contSheets) {
                                        contadorBarra++;
                                        progressBar.setProgress(contadorBarra);
                                    }
                                    //Log.v("bajarInfoCreditos_4", "Main.\n\nresponse:\n\n" + response + "\n\n.");
                                    llenarMapas(response);
                                } else {
                                    contadorBarra++;
                                    progressBar.setProgress(contadorBarra);
                                    bajarInfoCreditos();
                                }
                            }
                        } else {
                            Log.v("bajarInfoCreditos_5", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\n.");
                        }
                    },
                    error -> {
                        //Log.v("cargarData_2", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\n.");
                        bajarInfoCreditos();
                    });
            requestQueue.add(stringRequest);// Add the request to the RequestQueue.
        } else {
            //Se han leido todas las sheets.
            llenarMapas("final");
        }
    }

    private void llenarMapas (Object response) {
        String responsE = (String) response.toString();
        if (responsE.equals("first")) {
            bajarInfoCreditos();//Pasamos directo a bajar creditos
        } else if (responsE.equals("final")) {
            contadorBarra = 0;
            mostrarBarra("Guardando datos de creditos...", abonosLeidos.size());
            try {
                actualizarCaja();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            crearArchivosCreditos();
        } else {
            Log.v("llenarMapas_0", "Main.\n\nresponse:\n\n" + response + "\n\nresponsE:\n\n" + responsE + "\n\n.");
            if (responsE.contains("cuadratura")) {//Es el contenido de una sheet de abonos o creditos.
                String[] splitTransaccion = responsE.split("\"monto_credito\":\"");
                //boolean flagCredito;
                for (String transaccion : splitTransaccion) {
                    String[] splitDatos = transaccion.split("\",\"");
                    if (splitDatos.length > 1) {
                        String montoCred,plazoCred,montoCuot,fechaCred,fechaProx,saldoMasInt,tasaCred,cutoasCred,idCred,morosCred,cuadraturaCred,inteMora,montoAbon,cuadratura,montoAbono,montoCuota;
                        idCred = splitDatos[8].replace("\":\"", "_separador_");
                        String[] splitIdCred = idCred.split("_separador_");
                        idCred = splitIdCred[1];
                        String fileName = idCred + ".txt";
                        montoCred = "monto_credito_separador_" + splitDatos[0];
                        plazoCred = splitDatos[1].replace("\":\"", "_separador_");
                        String[] splitDatitos = plazoCred.split("_separador_");
                        plazoCred = plazoCred.replace(splitDatitos[0], "plazo");
                        montoCuot = splitDatos[2].replace("\":\"", "_separador_");
                        splitDatitos = montoCuot.split("_separador_");
                        montoCuota = splitDatitos[1];
                        fechaCred = splitDatos[3].replace("\":\"", "_separador_");
                        fechaProx = splitDatos[4].replace("\":\"", "_separador_");
                        splitDatitos = fechaProx.split("_separador_");
                        fechaProx = fechaProx.replace(splitDatitos[0], "proximo_abono");
                        saldoMasInt = splitDatos[5].replace("\":\"", "_separador_");
                        tasaCred = splitDatos[6].replace("\":\"", "_separador_");
                        cutoasCred = splitDatos[7].replace("\":\"", "_separador_");
                        idCred = splitDatos[8].replace("\":\"", "_separador_");
                        morosCred = splitDatos[9].replace("\":\"", "_separador_");
                        splitDatitos = morosCred.split("_separador_");
                        morosCred = morosCred.replace(splitDatitos[0], "morosidad");
                        cuadraturaCred = splitDatos[10].replace("\":\"", "_separador_");
                        splitDatitos = cuadraturaCred.split("_separador_");
                        cuadratura = splitDatitos[1];
                        inteMora = splitDatos[11].replace("\":\"", "_separador_");
                        montoAbon = splitDatos[12].replace("\":\"", "_separador_");
                        montoAbon = montoAbon.replace("\"},{", "");
                        montoAbon = montoAbon.replace("\"}]", "");
                        splitDatitos = montoAbon.split("_separador_");
                        montoAbono = splitDatitos[1];
                        String fileContent =
                                montoCred + "\n" +
                                plazoCred + "\n" +
                                montoCuot + "\n" +
                                fechaCred + "\n" +
                                fechaProx + "\n" +
                                saldoMasInt + "\n" +
                                tasaCred + "\n" +
                                cutoasCred + "\n" +
                                idCred + "\n" +
                                morosCred + "\n" +
                                cuadraturaCred + "\n" +
                                inteMora + "\n" +
                                montoAbon + "\n" +
                                "estado_archivo_separador_arriba";
                        //montoCred,plazoCred,montoCuot,fechaCred,fechaProx,saldoMasInt,tasaCred,cutoasCred,idCred,morosCred,cuadraturaCred,inteMora,montoAbon
                        //Log.v("llenarMapas_%-->", "Main.\n\nFile name: " + fileName + "\n\nContenido:\n\n" + fileContent + "\n\n.");
                        if (esCredito(cuadratura, montoAbono, montoCuota)) {
                            if (!creditosLeidos.containsKey(fileName)) {
                                creditosLeidos.put(fileName, fileContent);
                                tvProgressBar.setText("Leyendo archivo " + fileName + "...");
                                //Log.v("llenarMapas_CREDITO", "Main.\n\nfileName(key): " + fileName + "\n\nContent:\n\n" + fileContent + "\n\n.");
                            }
                        } else {
                            //Se crea nuevo separador: _separ_
                            //Naming convention:
                            //fileName + "_separ_" + cuadratura + "_separ_" + montoAbon;
                            String key = fileName + "_separ_" + cuadratura + "_separ_" + montoAbon;
                            if (!abonosLeidos.containsKey(key)) {
                                abonosLeidos.put(key, fileContent);
                                tvProgressBar.setText("Leyendo archivo " + fileName + "...");
                                //Log.v("llenarMapas_ABONO", "Main.\n\nKey: " + key + "\n\nContent:\n\n" + fileContent + "\n\n.");
                            }
                        }
                    }
                }
            } else if (responsE.contains("\"caja\"")) {//Es caja
                String[] splitCaja = responsE.split("\"caja\":\"");
                //Log.v("llenarMapas_1", "Main.\n\ncantidad: " + splitCaja.length + "\n\n.");
                int indice = (splitCaja.length-1);
                String montoString = splitCaja[indice];
                montoString = montoString.replace("\"", "");
                montoString = montoString.replace(",", "");
                montoString = montoString.replace(":", "");
                montoString = montoString.replace("}", "");
                montoString = montoString.replace("]", "");
                montoString = montoString.replace("abajo", "");
                montoString = montoString.replace("estado_archivo", "");
                montoString = montoString.replace("arriba", "");
                //Log.v("llenarMapas_2", "Main.\n\ndato: " + splitCaja[indice] + "\n\nmontoCaja: " + montoString + "\n\n.");
                int montoCaja = Integer.parseInt(montoString);
                cajita = montoCaja;
                tvProgressBar.setText("Leyendo archivo " + caja + "...");
            } else if (responsE.contains("tipo")) {
                contenidoCier = "fecha " + fecha + "\n";
                contenidoCier2 = "";
                tvProgressBar.setText("Leyendo archivo " + "cierre.txt" + "...");
                String[] splitCierre = responsE.split("\"tipo\":\"");
                String tipo,monto,caja,cliente;
                tipo = monto = caja = cliente = "";
                for (String datoCierre : splitCierre) {
                    if (!datoCierre.equals("[{")) {
                        //Log.v("llenarMapas_3", "Main.\n\nDatoCierre: " + datoCierre + "\nfecha: " + fecha + "\n\n.");
                        String[] splitDatos = datoCierre.split("\",\"");
                        tipo = splitDatos[0];
                        String[] splitMonto = splitDatos[1].split("\":\"");
                        monto = splitMonto[1];
                        String[] splitCaja = splitDatos[2].split("\":\"");
                        caja = splitCaja[1];
                        String[] splitCliente = splitDatos[3].split("\":\"");
                        cliente = splitCliente[1];
                        cliente = cliente.replace("\"", "");
                        cliente = cliente.replace("}", "");
                        cliente = cliente.replace("]", "");
                        cliente = cliente.replace(",", "");
                        cliente = cliente.replace("{", "");
                        contenidoCier = contenidoCier + tipo + " " + monto + " " + caja + " " + cliente + "\n";
                        contenidoCier2 = contenidoCier2 + tipo + "_separador_" + monto + "_separador_" + caja + "_separador_" + cliente + "\n";
                        Log.v("llenarMapas_3", "Main.\n\nDatoCierre: " + datoCierre + "\nfecha: " + fecha + "\n\n.");
                    }
                }
                contenidoCier2 = contenidoCier2 + "estado_archivo_separador_arriba";

                Log.v("llenarMapas_4", "Main.\n\ntipo: " + tipo + "\nmonto: " + monto + "\ncaja: " + caja + "\ncliente: " + cliente + "\n\n.");
                try {
                    new BorrarArchivo("cierre.txt", getApplicationContext());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    new GuardarArchivo("cierre.txt", contenidoCier, getApplicationContext()).guardarFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    new BorrarArchivo("cierre_cierre_.txt", getApplicationContext());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    new GuardarArchivo("cierre_cierre_.txt", contenidoCier2, getApplicationContext()).guardarFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        bajarInfoCreditos();
    }

    private void crearArchivosCreditos () {
        boolean flagFinish,flagBorrar,flagGuardar;
        flagBorrar = false;
        flagGuardar = true;
        String fileName,fileContent,cuadratura,key1,key2;
        //Naming convention. Key = ->
        //fileName + "_separ_" + cuadratura + "_separ_" + montoAbon;
        key1 = key2 = fileName = fileContent = cuadratura = "";
        //int datosPendientes = abonosLeidos.size();
        //Log.v("crearArchivosCreditos_0", "Main.\n\ndatosPendientes: " + datosPendientes + "\n\n.");
        for (String key : abonosLeidos.keySet()) {//Se escoge algun abono que se encuentre en el map abonosLeidos.
            //Log.v("crearArchivosCreditos_" + datosPendientes, "Main.\n\ndatosPendientes: " + datosPendientes + "\n\n.");
            String[] split = key.split("_separ_");
            fileName = split[0];
            fileContent = abonosLeidos.get(key);
            cuadratura = split[1];
            key1 = key;
            flagGuardar = false;
            break;
        }
        for (String key : abonosLeidos.keySet()) {
            key2 = key;
            String[] split = key.split("_separ_");
            String fileNamE = split[0];
            String fileContenido = abonosLeidos.get(key);
            String cuadraturA = split[1];
            if (!key1.equals(key2)) {
                if (fileNamE.equals(fileName)) {//Se trata de otro abono al mismo credito
                    flagGuardar = true;//crear map con los que si se van a guardar.
                    if (saldo(fileContent, cuadratura) > saldo(fileContenido, cuadraturA)) {
                        //Se borra key1.
                        key2 = key1;
                        flagBorrar = true;
                        break;
                    } else if (saldo(fileContent, cuadratura) < saldo(fileContenido, cuadraturA)) {
                        //Se borra key2.
                        key1 = key2;
                        flagBorrar = true;
                        break;
                    } else {
                        //Se borra la key que tenga mas intereses moratorios.
                        //Log.v("crearArchivosCreditos_1", "Main.\n\nKey elejida: " + elejirAdelantoInteres(abonosLeidos.get(key1), abonosLeidos.get(key2), key1, key2) +
                                //"\nKey1: " + key1 + "\nkey2: " + key2 + "\n\n.");
                        key1 = elejirAdelantoInteres(abonosLeidos.get(key1), abonosLeidos.get(key2), key1, key2);
                        key2 = key1;
                        flagBorrar = true;
                        break;
                    }
                }
            }
        }
        if (flagBorrar) {
            if (key1.equals(key2)) {//To-do bien.
                abonosLeidos.remove(key1);
            } else {//Control de errores...
                Log.v("CrearArchCredERROR_1", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\n.");
            }
        }
        if (!flagGuardar) {
            if (!abonosGuardar.containsKey(key1)) {
                abonosGuardar.put(key1, abonosLeidos.get(key1));
                abonosLeidos.remove(key1);
            } else {//Control de errores...
                Log.v("CrearArchCredERROR_2", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\n.");
            }
        }
        flagFinish = true;
        for (String key : abonosLeidos.keySet()) {//Se verifica que ya no existen abonos en el map.
            Log.v("CrearArchCredDebug_0", "Main.\n\nkey: " + key + "\n\ncontenido:\n\n" + abonosLeidos.get(key) + "\n\n.");
            flagFinish = false;
            break;
        }

        if (flagFinish) {
            concretarGuardado();
        } else {
            crearArchivosCreditos();
        }
    }

    private String elejirAdelantoInteres (String s1, String s2, String key1, String key2) {
        String key = new String();
        String[] split = s1.split("\n");
        String[] split11 = split[11].split("_separador_");
        int InteresesMoratorios1 = Integer.parseInt(split11[1]);
        String[] splitDos = s2.split("\n");
        String[] splitDos11 = splitDos[11].split("_separador_");
        //Log.v("elejirAdelantoIntereses_0", "split11[0]: " + split11[0] + "\nsplitDos11[0]: " + splitDos11[0]);
        int InteresesMoratorios2 = Integer.parseInt(splitDos11[1]);
        if (InteresesMoratorios1 > InteresesMoratorios2) {
            //Log.v("elejirAdelantoInteres_0", "Main.\n\n" + "Intereses moratorios 1: " + InteresesMoratorios1 + "\nIntereses moratorios 2: " + InteresesMoratorios2 + "\n\n.");
            key = key2;
        } else if (InteresesMoratorios1 < InteresesMoratorios2) {
            //Log.v("elejirAdelantoInteres_2", "Main.\n\nIntereses moratorios 1: " + InteresesMoratorios1 + "\nIntereses moratorios 2: " + InteresesMoratorios2 + "\n\n.");
            key = key1;
        } else {
            key = revisarNextAbono(s1, s2, key1, key2);
            //Log.v("elejirAdelantoInteres_3", "Main.\n\nIntereses moratorios 1: " + InteresesMoratorios1 + "\nIntereses moratorios 2: " + InteresesMoratorios2 + "\n\n.");
        }
        return key;
    }

    private String revisarNextAbono (String s1, String s2, String key1, String key2) {
        String key = new String();
        String[] split = s1.split("\n");
        String[] split4 = split[4].split("_separador_");
        String[] splitFecha1 = split4[1].split("/");
        String fecha1 = splitFecha1[2] + "-" + splitFecha1[1] + "-" + splitFecha1[0];
        Date fechaNext1;
        try {
            fechaNext1 = stringToDate(fecha1);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        String[] splitDos = s2.split("\n");
        String[] splitDos4 = splitDos[4].split("_separador_");
        String[] splitFecha2 = splitDos4[1].split("/");
        String fecha2 = splitFecha2[2] + "-" + splitFecha2[1] + "-" + splitFecha2[0];
        Date fechaNext2;
        try {
            fechaNext2 = stringToDate(fecha2);
        } catch (ParseException e) {
            throw new RuntimeException(e);
        }
        int daysBetween = daysBetween(fechaNext1, fechaNext2);
        //Log.v("revisarNextAbono_0", "Main.\n\ndaysBetween: " + daysBetween + "\nsplit4[0]: " + split4[0] + "\nsplitDos4[0]: " + splitDos4[0] + "\nsplit[1]: " + split4[1] + "\nsplitDos[1]: " + splitDos4[1] + "\n\n.");
        if (daysBetween < 0) {
            key = key1;
        } else if (daysBetween > 0) {
            key = key2;
        } else {
            //Log.v("revisarNextAbonoERROR_0", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\nFecha next 1: " + fechaNext1 + "\nFecha next 2: " + fechaNext2 + "\n\n.");
        }
        return key;
    }

    private int saldo(String content, String cuadratura) {
        int monto = 0;
        if (content.contains(cuadratura)) {
            //Log.v("saldo_0", "Main.\n\nTodo bien. contenido contiene a cuadratura.\n\n.");
        } else {//Control de errores...
            //Log.v("saldoERROR_0", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\nContent:\n\n" + content + "\n\n.");
        }
        String[] splitCuadratura = cuadratura.split("__");
        for (String cuota : splitCuadratura) {
            String[] splitCuota = cuota.split("_");
            monto = monto + Integer.parseInt(splitCuota[2]);
        }
        String[] split = content.split("\n");
        String[] split5 = split[5].split("_separador_");
        int saldoMasInter = Integer.parseInt(split5[1]);
        if (saldoMasInter > monto) {
            //Log.v("saldo_1", "Main.\n\nmonto en cuadratura: " + monto + "\nsaldoMasInter: " + saldoMasInter + "\n\n.");
        } else {
            //Log.v("saldo_2", "Main.\n\nmonto en cuadratura: " + monto + "\nsaldoMasInter: " + saldoMasInter + "\n\n.");
        }
        String[] split11 = split[11].split("_separador_");
        int interesesMoratori = Integer.parseInt(split11[1]);
        //Log.v("saldo_3", "Main.\n\ninteresMoratori: " + interesesMoratori + "\n\n.");
        saldoMasInter = saldoMasInter + interesesMoratori;
        //Log.v("saldo_4", "Main.\n\nsuma: " + saldoMasInter + "\n\n.");
        return saldoMasInter;
    }

    private int saldo(String content) {
        int monto = 0;
        String[] split = content.split("\n");
        String[] split5 = split[5].split("_separador_");
        int saldoMasInter = Integer.parseInt(split5[1]);
        if (saldoMasInter > monto) {
            //Log.v("(polyForm)saldo_1", "Main.\n\nmonto en cuadratura: " + monto + "\nsaldoMasInter: " + saldoMasInter + "\n\n.");
        } else {
            //Log.v("(polyForm)saldo_2", "Main.\n\nmonto en cuadratura: " + monto + "\nsaldoMasInter: " + saldoMasInter + "\n\n.");
        }
        String[] split11 = split[11].split("_separador_");
        int interesesMoratori = Integer.parseInt(split11[1]);
        //Log.v("(polyForm)saldo_3", "Main.\n\ninteresMoratori: " + interesesMoratori + "\n\n.");
        saldoMasInter = saldoMasInter + interesesMoratori;
        //Log.v("(polyForm)saldo_4", "Main.\n\nsuma: " + saldoMasInter + "\n\n.");
        return saldoMasInter;
    }

    private void concretarGuardado () {

        mostrarBarra("Guardando creditos...", archivosGuardar.size());
        int contDebug = 0;
        //Log.v("concretarGuardDEBUG_0", "Main.\n\nInicio del ciclo...\n\n.");
        for (String key : abonosGuardar.keySet()) {
            //Log.v("concretarGuardDEBUG_" + contDebug, "Main.\n\nCuenta: " + contDebug + "\n\n.");
            contDebug++;
            String[] split = key.split("_separ_");
            if (!archivosGuardar.containsKey(split[0])) {
                archivosGuardar.put(split[0], abonosGuardar.get(key));//Llenamos a archivosGuardar.
            }
        }

        for (String key : creditosLeidos.keySet()) {
            if (!archivosGuardar.containsKey(key)) {
                archivosGuardar.put(key, creditosLeidos.get(key));//Significa que no se ha hecho ningun abono a este credito.
            }
        }

        abonosLeidos.clear();
        for (String key : archivosGuardar.keySet()) {
            if (!abonosLeidos.containsKey(key)) {
                abonosLeidos.put(key, archivosGuardar.get(key));//Copiamos el map archivosGuardar.
            }
        }

        for (String key : abonosLeidos.keySet()) {
            if (saldo(abonosLeidos.get(key)) < 1000) {
                archivosGuardar.remove(key);
                //Log.v("concretarGuardado_0", "Main.\n\nSe ha borrado el credito:\n\n" + abonosLeidos.get(key) + "\n\nSaldo del credito: " + saldo(abonosLeidos.get(key)) + "\n\n.");
            }
        }


        contadorBarra = 0;

        for (String key : archivosGuardar.keySet()) {
            contadorBarra++;
            progressBar.setProgress(contadorBarra);
            try {
                if (new GuardarArchivo(key, archivosGuardar.get(key), getApplicationContext()).guardarFile()) {
                    //Log.v("concretarGuardado_@--->", "Main.\n\nFile: " + key + "\n\nContenido del archivo:\n\n" + imprimir_archivo(key) + "\n\n.");
                } else {
                    Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                    Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        menu_principal("Caja inicial: " + String.valueOf(cajita));
    }

    private boolean esCredito (String cuadratura, String montoAbono, String montoCuota) {
        Log.v("esCredito_0", "Main.\n\nmontoAbono: " + montoAbono + "\nmontoCuota: " + montoCuota + "\n\n.");
        boolean flagCuadra,flagAbono;
        String[] splitCuadraturaCuotas = cuadratura.split("__");
        flagCuadra = false;
        for (String cuota : splitCuadraturaCuotas) {
            String[] splitCuota = cuota.split("_");
            if (splitCuota[2].equals(montoCuota)) {
                flagCuadra = true;
            } else {
                flagCuadra = false;
                break;
            }
        }
        Log.v("esCredito_1", "Main.\n\nflagCuadra: " + flagCuadra + "\nmontoCuota: " + montoCuota + "\n\n.");
        int montoAbonado = Integer.parseInt(montoAbono);
        if (montoAbonado > 0) {
            flagAbono = false;
        } else {
            flagAbono = true;
        }
        Log.v("esCredito_2", "Main.\n\nflagAbono: " + flagAbono + "\nmontoCuota: " + montoCuota + "\n\n.");
        return (flagCuadra & flagAbono);
    }


    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(hoy_LD);
        fecha = datosFecha.getDia();
    }

    private void actualizarCaja () throws IOException {
        long monto_nuevo = 0;
        String contenido = "";
        boolean flagCajaxNoCreada = false;
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            monto_nuevo = Integer.parseInt(split[1]) + cajita;
            linea = linea.replace(split[1], String.valueOf(monto_nuevo));
            contenido = linea;
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new GuardarArchivo(caja, contenido, getApplicationContext()).guardarFile();
        contenido = "";
        String[] archivos = fileList();
        if (archivo_existe(archivos, "cajax_caja_.txt")) {
            //Log.v("actualizarCaja_1", "Main.\n\nfile: " + "cajax_caja_.txt" + "\n\ncontenido del archivo:\n\n" + imprimir_archivo("cajax_caja_.txt") + "\n\n.");
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("cajax_caja_.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null && !linea.equals("")) {
                    String[] split = linea.split("_separador_");
                    if (split[0].equals("caja")) {
                        linea = linea.replace(split[1], String.valueOf(monto_nuevo));
                    }
                    contenido = contenido + linea + "\n";
                    //Log.v("actualizarCaja_2", "Main.\n\nLinea:\n\n" + linea + "\n\n.");
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new CrearArchivo("cajax_caja_.txt", getApplicationContext());
            new AgregarLinea("caja_separador_" + String.valueOf(monto_nuevo), "cajax_caja_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_arriba", "cajax_caja_.txt", getApplicationContext());
            //Log.v("actualizarCaja_3", "Main.\n\ncontenido de cajax_caja_.txt:\n\n" + imprimir_archivo("cajax_caja_.txt") + "\n\n.");
            flagCajaxNoCreada = true;
        }

        if (!flagCajaxNoCreada) {
            new BorrarArchivo("cajax_caja_.txt", getApplicationContext());
            new GuardarArchivo("cajax_caja_.txt", contenido, getApplicationContext()).guardarFile();
        }
    }

    private void guardar_datos_cobrador () {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            StringBuilder archivo_completo = new StringBuilder();
            while (linea != null) {
                String[] split = linea.split(" ");
                if (split[0].equals("cobrador_ID")) {
                    linea = linea.replace("cobrador_ID " + split[1], "cobrador_ID " + ID_cobrador);
                    archivo_completo.append(linea).append("\n");
                } else {
                    archivo_completo.append(linea).append("\n");
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            new BorrarArchivo(cobrador, getApplicationContext());
            new GuardarArchivo(cobrador, archivo_completo.toString(), getApplicationContext()).guardarFile();
            //Log.v("guard_dat_cobr_0", "Main.\n\narchivo cobrador: " + cobrador + "\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void abonar(){
        Intent abonar = new Intent(getApplicationContext(), AbonarActivity.class);
        abonar.putExtra("msg", "");
        abonar.putExtra("cliente_recivido", "");
        abonar.putExtra("abono_cero", "");
        startActivity(abonar);
        finish();
        System.exit(0);
    }

    private void menu_principal (String messag) {
        Log.v("concretarGuardado_0", "Main.\n\ncontenido2:\n" + contenidoCier2 + "\n\ncontenido:\n" + contenidoCier +
                "\n\ncontenido del archivo cierre.txt:\n\n" + imprimirArchivo("cierre.txt") +
                "\n\ncontenido del archivo cierre_cierre.txt:\n\n" + imprimirArchivo("cierre_cierre_.txt") + "\n\n.");
        Intent menu_principal = new Intent(this, MenuPrincipal.class);
        menu_principal.putExtra("mensaje", messag);
        startActivity(menu_principal);
        finish();
        System.exit(0);
    }

    private void crear_archivoS () throws IOException {

        String[] archivos = fileList();
        /////////////////Se crea el archivo cobrador.txt///////////////
        if (!archivo_existe(archivos, cobrador)) {
            new CrearArchivo(cobrador, getApplicationContext());
            new AgregarLinea("Cobrador1 FALSE " + fecha, cobrador, getApplicationContext());
        }
        ////////////////////////////////////////////////////////////////

        ////////////////////Se crea el archivo caja.txt/////////////////
        if (!archivo_existe(archivos, caja)) {
            new CrearArchivo(caja, getApplicationContext());
            new AgregarLinea("caja 0", caja, getApplicationContext());
            flag_caja = true;
        }
        ////////////////////////////////////////////////////////////////

        /////////////////Se crea el archivo cajax_caja_.txt//////////////
        if (!archivo_existe(archivos, "cajax_caja_.txt")) {
            new CrearArchivo("cajax_caja_.txt", getApplicationContext());
            new AgregarLinea("caja_separador_0", "cajax_caja_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_arriba", "cajax_caja_.txt", getApplicationContext());
        }
        ////////////////////////////////////////////////////////////////

        //////// ARCHIVOS cierre  ////////////////////////////////////////////////////////////

        if (!archivo_existe(archivos, "cierre.txt")) {
            new CrearArchivo("cierre.txt", getApplicationContext());
            new AgregarLinea("fecha 00", "cierre.txt", getApplicationContext());
        }
        if (!archivo_existe(archivos, "cierre_cierre_.txt")) {
            new CrearArchivo("cierre_cierre_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }

        /////////////////////////////////////////////////////////////////////////////////////

        ////////////////////// archivo estado_online.txt ////////////////////////////////////

        if (!archivo_existe(archivos, "estado_online.txt")) {
            new CrearArchivo("estado_online.txt", getApplicationContext());
            new AgregarLinea("amarillo" ,"estado_online.txt", getApplicationContext());
        }

        /////////////////////////////////////////////////////////////////////////////////////

        check_activation();
    }

    private void salir() {
        try {
            Toast.makeText(this.getApplicationContext(), "Cobrador inactivo\nLa app se cierra ahora...", Toast.LENGTH_LONG).show();
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        try {
            Toast.makeText(this.getApplicationContext(), "Cobrador inactivo\nLa app se cierra ahora...", Toast.LENGTH_LONG).show();
            Thread.sleep(1500);
            finish();
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void check_box_listener(View view) {
        String texto;
        if (checkedTextView.isChecked()) {
            et_ID.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
        } else {
            et_ID.setTransformationMethod(PasswordTransformationMethod.getInstance());
        }
        et_ID.setFocusableInTouchMode(true);
        texto = et_ID.getText().toString();
        et_ID.setText(texto);
    }

    private void text_listener () {
        et_ID.setText("");
        et_ID.setEnabled(true);
        et_ID.setFocusableInTouchMode(true);
        et_ID.requestFocus();
        et_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tv_esperar.getText().toString().equals("Ingrese su codigo de cobrador...") | tv_esperar.getText().toString().equals("Debe ingresar un codigo valido!")) {
                    String codigo = et_ID.getText().toString();//Se parcea el valor a un string
                    if (codigo.length() == 11) {
                        String string = "Conectando, por favor espere...";
                        tv_esperar.setText(string);
                        et_ID.setFocusableInTouchMode(false);
                        et_ID.setEnabled(false);
                        boolean aceptado = verificar_codigo(codigo);
                        if (aceptado) {
                            check_activation_online(codigo);
                        } else {
                            String string2 = "Debe ingresar un codigo valido!";
                            tv_esperar.setText(string2);
                            msg("Debe ingresar un codigo valido!");
                            et_ID.setText("");
                            et_ID.setFocusableInTouchMode(true);
                            et_ID.requestFocus();
                            text_listener();
                        }
                    }
                } else if (tv_esperar.getText().toString().equals("Ingrese el monto inicial de caja...")) {
                    if (s.toString().equals("")) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else {
                        boton_submit.setEnabled(true);
                        boton_submit.setClickable(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite su password")) {
                    //Log.v("Digite_password", ".\n\nS: " + s + "\n\n.");
                    if (s.toString().equals("")) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else {
                        boton_submit.setEnabled(true);
                        boton_submit.setClickable(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite su usuario")) {
                    //Log.v("Digite_usuario", ".\n\nS: " + s + "\n\n.");
                    if (s.toString().equals("")) {
                        boton_submit.setClickable(false);
                        boton_submit.setEnabled(false);
                    } else {
                        boton_submit.setEnabled(true);
                        boton_submit.setClickable(true);
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private Boolean verificar_codigo (String codigo){
        boolean retorno = true;
        for (int i = 0; i < codigo.length(); i++){
            String valor = String.valueOf(codigo.charAt(i));
            if (i == 0) {
                //Log.v("verificar_codigo_" + i, "Valor a evaluar: " + valor);
                if (!valor.equals("C")) {
                    retorno = false;
                    break;
                }
            } else {
                //Log.v("verificar_codigo " + i, "Valor a evaluar: " + valor);
                boolean isNumeric = (valor.matches("[0-9]"));
                if (!isNumeric) {
                    retorno = false;
                    break;
                }
            }
        }
        return retorno;
    }

    private void check_activation () throws IOException {
        String[] archivos = fileList();
        boolean crear = true;
        for (String s : archivos) {
            Pattern pattern = Pattern.compile("a_sfile_cobrador_sfile_a", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(s);
            boolean matchFound = matcher.find();
            if (matchFound) {
                //Log.v("check_activation_0", "Main.\n\nArchivo encontrado: " + s + "\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                crear = false;
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String[] split = linea.split(" ");
                    //Log.v("Check_activation_1", "Main.\n\nLinea: " + linea + "\n\nFecha: " + fecha + "\n\nsplit[1]: " + split[1] + "\n\nsplit[2]: " + split[2] + "\n\n.");
                    if (split[1].equals("TRUE") && split[2].equals(fecha)) {
                        Toast.makeText(this, "Bienvenido " + nombre_cobra, Toast.LENGTH_LONG).show();
                        menu_principal("Bienvenido de nuevo " + nombre_cobra);
                        br.close();
                        archivo.close();
                        break;
                    } else if (split[1].equals("FALSE") && split[2].equals(fecha)) {
                        if (verificar_internet()) {
                            ocultar_todito();
                            text_listener();
                        } else {
                            Toast.makeText(this, "Debe estar conectado a Internet!", Toast.LENGTH_SHORT).show();
                            br.close();
                            archivo.close();
                            salir();
                        }
                        br.close();
                        archivo.close();
                        break;
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
            }
        }
        if (crear) {
            crear_archivoS();
            ocultar_todito();
            text_listener();
        }
    }

    private void check_activation_online(String codigo) {//codigo corresponde al ID del cobrador.

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

        //Log.v("Crear file active URL ", ".\nurl: " + url + "\n.");

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    //Log.v("check_activ_online_0", "Main.\n\nResponse:\n" + response + "\n\n.");
                    if (response != null) {
                        String[] split = response.split("estado");
                        for (int i = 1; i < split.length; i++) {
                            String[] split2 = split[i].split("\"");
                            //Log.v("check_activ_online_1", "Main.\n\nSplit22: " + split2[22] + ", Split2: " + split2[2] + "\net_ID.getText().toString(): " + et_ID.getText().toString() + "\n");
                            if (split2[22].equals(codigo)) {
                                if (split2[2].equals("TRUE")) {
                                    try {
                                        InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
                                        BufferedReader br = new BufferedReader(archivo);
                                        String linea = br.readLine();
                                        //Log.v("check_activ_online_2", ".\n\nLinea: " + linea + "\n\n.");
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
                                        spreadsheet_clientes = split2[18];
                                        spreadsheet_creditos = split2[14];
                                        new BorrarArchivo(cobrador, getApplicationContext());
                                        if (new GuardarArchivo(cobrador, contenido, getApplicationContext()).guardarFile()) {
                                            //Log.v("guardar_datos_cobrador0", "Main.\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                                        } else {
                                            msg("*** ERROR al crear el archivo. ***");
                                            msg("Informe a soporte tecnico!");
                                        }
                                        //Log.v("check_activ_online3", ".Main\n\nArchivo cobrador.txt:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }//Continua trabajando con la app.
                                    mostrar_todito();
                                    autenticar_cobrador(codigo);
                                } else {//Cobrador inactivo. Se cierra la app.
                                    String string = "Cobrador inactivo. La app se cierra ahora...";
                                    tv_esperar.setText(string);
                                    esperar();
                                }
                                break;
                            } else {
                                String string = "Debe ingresar un codigo valido!";
                                tv_esperar.setText(string);
                                text_listener();
                            }
                        }
                    }
                },
                error -> {
                    try {
                        check_activation();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        requestQueue.add(stringRequest);// Add the request to the RequestQueue.
    }

    private void verificar_usuario (String codigo) {//codigo corresponde al ID del cobrador.

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

        //Log.v("verificar_usuario_0", ".\nurl: " + url + "\n.");

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    //Log.v("verificar_usuario_1", "Main.\n\nResponse:\n\n" + response + "\n\n.");
                    if (response != null) {
                        String[] split = response.split("estado");
                        for (int i = 1; i < split.length; i++) {
                            String[] split2 = split[i].split("\"");
                            //Log.v("verificar_usuario_2", "Main.\n\nSplit:\nSplit22: " + split2[22] + ", Split26: " + split2[26] + "\net_ID.getText().toString(): " + et_ID.getText().toString() + "\n\n.");
                            if (split2[22].equals(codigo)) {
                                if (split2[26].equals(et_ID.getText().toString())) {
                                    mostrar_todito();
                                    autenticar_cobrador2(codigo);
                                } else {//Cobrador inactivo. Se cierra la app.
                                    msg("Nombre de usuario incorrecto. Trate de nuevo");
                                    String string = "Nombre de usuario incorrecto. Trate de nuevo";
                                    tv_esperar.setText(string);
                                    mostrar_todito();
                                    autenticar_cobrador(codigo);
                                }
                                break;
                            }
                        }
                    }
                },
                error -> {
                    // Handle error
                });
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private void verificar_password (String codigo) {//codigo corresponde al ID del cobrador.

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

        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    //Log.v("verificar_password_0", ".\nResponse:\n" + response);
                    if (response != null) {
                        String[] split = response.split("estado");
                        for (int i = 1; i < split.length; i++) {
                            String[] split2 = split[i].split("\"");
                            //Log.v("verificar_password_1", ".\nSplit:\nSplit22: " + split2[22] + ", Split10: " + split2[10] + "\net_ID.getText().toString(): " + et_ID.getText().toString() + "\n");
                            if (split2[22].equals(codigo)) {
                                if (split2[10].equals(et_ID.getText().toString())) {
                                    mostrar_todito();
                                    msg("password aceptado!!!");
                                    checkedTextView.setVisibility(View.INVISIBLE);
                                    boton_submit.setVisibility(View.INVISIBLE);
                                    if (flag_caja) {
                                        cargarData();
                                    } else {
                                        guardar_datos_cobrador();
                                        menu_principal("");
                                    }
                                } else {
                                    msg("Password incorrecto. Trate de nuevo");
                                    String string = "Password incorrecto. Trate de nuevo";
                                    tv_esperar.setText(string);
                                    mostrar_todito();
                                    autenticar_cobrador2(codigo);
                                }
                                break;
                            }
                        }
                    }
                },
                error -> {
                    // Handle error
                });
        // Add the request to the RequestQueue.
        requestQueue.add(stringRequest);
    }

    private void autenticar_cobrador (String codigo) {
        ID_cobrador = codigo;
        ocultar_todito();
        boton_submit.setVisibility(View.VISIBLE);
        boton_submit.setClickable(false);
        boton_submit.setEnabled(false);
        String string = "Digite su usuario";
        tv_esperar.setText(string);
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
        String string = "Digite su password";
        tv_esperar.setText(string);
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
            String string = "Conectando, por favor espere...";
            tv_esperar.setText(string);
            et_ID.setFocusableInTouchMode(false);
            et_ID.setEnabled(false);
            verificar_usuario(ID_cobrador);
        } else if (tv_esperar.getText().toString().equals("Digite su password")) {
            String string = "Conectando, por favor espere...";
            tv_esperar.setText(string);
            et_ID.setFocusableInTouchMode(false);
            et_ID.setEnabled(false);
            verificar_password(ID_cobrador);
        }
    }

    private void esperar () {
        ocultar_todito();
        Toast.makeText(this, "Cobrador inactivo. La app se cierra ahora...", Toast.LENGTH_LONG).show();
        String string = "Cobrador inactivo. La app se cierra ahora...";
        tv_esperar.setText(string);
        for (int i = 0; i < 10; i++) {
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
        for (String archivo : archivos) {
            if (file_name.equals(archivo)) {
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
        Toast.makeText(this, "Espere a que termine la operacion...", Toast.LENGTH_LONG).show();
    }

    private void mostrar_todito() {
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        et_ID.setVisibility(View.INVISIBLE);
    }

    private void ocultar_todito() {
        tv_esperar.setVisibility(View.VISIBLE);
        String string = "Ingrese su codigo de cobrador...";
        tv_esperar.setText(string);
        et_ID.setVisibility(View.VISIBLE);
    }

    private String imprimirArchivo (String file_name){
        String[] archivos = fileList();
        StringBuilder contenido = new StringBuilder();//Aqui se lee el contenido del archivo guardado.
        if (archivo_existe(archivos, file_name)) {//Archivo nombre_archivo es el archivo que vamos a imprimir
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));//Se abre archivo
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();//Se lee archivo
                while (linea != null) {
                    contenido.append(linea).append("\n");
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return contenido.toString();
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