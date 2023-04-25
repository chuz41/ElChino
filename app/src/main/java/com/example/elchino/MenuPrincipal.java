package com.example.elchino;

import static com.example.elchino.Util.DateUtilities.daysBetween;
import static com.example.elchino.Util.DateUtilities.stringToDate;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.android.volley.Cache;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.elchino.Clases_comunes.Cliente;
import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.CrearArchivo;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;
import com.example.elchino.Util.SubirArchivo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuPrincipal extends AppCompatActivity {

    private final String cobrador = "a_sfile_cobrador_sfile_a.txt";
    private final String spreadsheet_cobradores = "1y5wRGgrkH48EWgd2OWwon_Um42mxN94CdmJSi_XCwvM";
    private String dia;
    private String mes;
    private Integer contSheets = 0;
    private Integer contadorBarra = 0;
    private String anio;
    private String fecha;
    private boolean flag_salir = false;
    private boolean flag_salir_2 = true;
    private TextView tv_caja;
    private ImageView amarillo;
    private ImageView verde;
    private ImageView rojo;
    ActivityResultLauncher<String[]> sPermissionResultLauncher;
    //private boolean isReadExternalPermissionGranted = false;
    private boolean isManageExternalPermissionGranted = false;
    //private boolean isWriteExternalPermissionGranted = false;
    private boolean isSendSmsPermissionGranted = false;
    //private ProgressBar progressBar;
    //private TextView tvProgressBar;
    private Button bt_nuevo_cliente;
    private Button bt_estado_cliente;
    private Button bt_cierre;
    private Button bt_gastos;
    private Button btMorosos;
    private Button btPaganHoy;
    private Button bt_banca;
    private TextView tv_saludo;
    private TextView tv_fecha;
    private final String caja = "caja.txt";
    private final String readRowURL = "https://script.google.com/macros/s/AKfycbyx-sv9enLGRWxYsFiXzcUhRIysGzzSeJa-wS8fXCMHUQk7iCQRzyAPb-17pUWryy5j/exec?spreadsheetId=";
    private final String readSheets = "https://script.google.com/macros/s/AKfycbzX2Z4Jm7g7qTYXpUFz1SXOG9w9-or9u3ZNFmUYZ98tI_Nrw-36Wz1XYODm68Ad8bJ1vA/exec?spreadsheetId=";
    private final String sheet_cobradores = "cobradores";
    private String spreadsheet_clientes;
    private HashMap<String, Cliente> clientesEncontrados = new HashMap<>();//Contendra a todos los clientes leidos y guardados.
    private Map<String, String> sheetsLeidas = new HashMap<>();
    private Map<String, String> creditosLeidos = new HashMap<>();
    private Map<String, String> abonosLeidos = new HashMap<>();
    private Map<String, String> abonosGuardar = new HashMap<>();
    private Map<String, String> archivosGuardar = new HashMap<>();
    private String spreadsheet_creditos;
    private String contenidoCier;
    private String contenidoCier2 = "";
    private String nombre_cobra = "";
    private Integer cajita = 0;
    private Boolean flagTrabajando = false;
    private Button btRepQuincenal;
    private Button btRepMensual;
    private final String REFRESH = "refresh_refresh_.txt";
    private final String globalVar = "globalVar_globalVar_.txt";


    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
        //progressBar.setVisibility(View.INVISIBLE);
        //tvProgressBar = (TextView) findViewById(R.id.tvProgressBar);
        //tvProgressBar.setVisibility(View.INVISIBLE);
        //btRepQuincenal = (Button) findViewById(R.id.//btRepQuincenal);
        //btRepMensual = (Button) findViewById(R.id.//btRepMensual);
        flag_salir = false;
        flag_salir_2 = true;
        setContentView(R.layout.activity_menu_principal);
        amarillo = (ImageView) findViewById(R.id.imageView);
        verde = (ImageView) findViewById(R.id.imageView2);
        rojo = (ImageView) findViewById(R.id.imageView3);
        amarillo.setVisibility(View.INVISIBLE);
        verde.setVisibility(View.INVISIBLE);
        rojo.setVisibility(View.INVISIBLE);
        bt_nuevo_cliente = (Button) findViewById(R.id.bt_nuevo_cliente);
        bt_estado_cliente = (Button) findViewById(R.id.bt_estado_cliente);
        bt_cierre = (Button) findViewById(R.id.bt_cierre);
        bt_gastos = (Button) findViewById(R.id.bt_gastos);
        btMorosos = (Button) findViewById(R.id.btMorosos);
        btPaganHoy = (Button) findViewById(R.id.btPaganHoy);
        //private Button bt_refinanciar;
        //private Button bt_nuevo_credito;
        bt_banca = (Button) findViewById(R.id.bt_banca);
        bt_banca.setText("ENTREGAR/RECIBIR FONDOS DE BANCA");
        //bt_refinanciar = (Button) findViewById(R.id.bt_refinanciar);
        //bt_nuevo_credito = (Button) findViewById(R.id.bt_nuevo_credito);
        tv_saludo = (TextView) findViewById(R.id.tv_saludoMenu);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");
        try {
            new BorrarArchivo(globalVar, this.getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            new GuardarArchivo(globalVar, "texto", this.getApplicationContext()).guardarFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mostrarEstado();
        mostrar_caja();
        separarFecha();
        contenidoCier = "fecha " + fecha + "\n";
        Log.v("onCreate_0", "MenuPrincipal.\n\nSe inicia con la presentacion de los archivos...\n\n.");
        //verArchivos();//debug function!
        try {
            corregirArchivos();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_fecha.setText(dia + "/" + mes + "/" + anio);
        tv_saludo.setText("Menu principal");
        String mensaje_recibido = getIntent().getStringExtra("mensaje");
        boolean flagServicio = false;
        if (!(mensaje_recibido.equals("null")) && !(mensaje_recibido == "")) {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SubirArchivo.class.getName().equals(service.service.getClassName())) {
                flagServicio = true;
            }
        }
        Log.v("onCreate_0", "MenuPrincipal.\n\nflagServicio: " + flagServicio + "\n\n.");
        if (!flagServicio) {
            startService(new Intent(getApplicationContext(), SubirArchivo.class));
        }
        sPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult (Map<String, Boolean> result) {

                /*if (result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                    isReadExternalPermissionGranted = result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }*/

                if (result.get(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) != null) {
                    isManageExternalPermissionGranted = result.get(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                }

                /*if (result.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isWriteExternalPermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }*/
                
                if (result.get(Manifest.permission.SEND_SMS) != null) {
                    isSendSmsPermissionGranted = result.get(Manifest.permission.SEND_SMS);
                }

                if (isSendSmsPermissionGranted && isManageExternalPermissionGranted) {
                    //Toast.makeText(getApplicationContext(), "Se concedieron los permisos necesarios", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "No se concedieron los permisos necesarios!!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        requestPermissions();
    }

    private void check_activation_online () {//codigo corresponde al ID del cobrador.

        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            //Log.v("check_activ_online_2", "MenuPrincipal.\n\nLinea: " + linea + "\n\n.");
            String[] split_linea_1 = linea.split(" ");
            String contenido = linea + "\n";
            while (linea != null) {
                String[] split = linea.split(" ");
                if (split[0].equals("Screditos")) {
                    spreadsheet_creditos = split[1];
                } else if (split[0].equals("Sclientes")) {
                    spreadsheet_clientes = split[1];
                }
                linea = br.readLine();
            }
            //Log.v("check_activ_online3", ".MenuPrincipal\n\nArchivo cobrador.txt:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }//Continua trabajando con la app.
        check_activeUser();
    }

    private void getUserCode () {
        //String code = "";
        check_activation_online();
    }

    public void check_activation (View view) {
        if (flag_salir_2) {
            getUserCode();
        } else {
            msg("Espere a que termine la operacion...");
        }
    }

    public void check_activeUser () {
        String[] archivos = fileList();
        //boolean crear = true;
        for (String s : archivos) {
            Pattern pattern = Pattern.compile("a_sfile_cobrador_sfile_a", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(s);
            boolean matchFound = matcher.find();
            if (matchFound) {
                //Log.v("check_activation_0", "Main.\n\nArchivo encontrado: " + s + "\n\nContenido del archivo:\n\n" + imprimir_archivo(cobrador) + "\n\n.");
                //crear = false;
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    String[] split = linea.split(" ");
                    //Log.v("Check_activation_1", "Main.\n\nLinea: " + linea + "\n\nFecha: " + fecha + "\n\nsplit[1]: " + split[1] + "\n\nsplit[2]: " + split[2] + "\n\n.");
                    if (split[1].equals("TRUE") && split[2].equals(fecha)) {
                        Toast.makeText(getApplicationContext(), "Bienvenido " + nombre_cobra, Toast.LENGTH_LONG).show();
                        cargarData();
                        br.close();
                        archivo.close();
                        break;
                    } else {
                        br.close();
                        archivo.close();
                        mostrarTodo();
                        break;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private boolean verificar_internet () {
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

    private void mostrarBarra (String s, int divisionesBarra) {
        //progressBar.setVisibility(View.VISIBLE);
        //tvProgressBar.setVisibility(View.VISIBLE);
        tv_saludo.setText(s);
        //progressBar.setMax(divisionesBarra);
        //progressBar.setProgress(0);
        amarillo.setVisibility(View.INVISIBLE);
        verde.setVisibility(View.INVISIBLE);
        rojo.setVisibility(View.INVISIBLE);
        bt_nuevo_cliente.setVisibility(View.INVISIBLE);
        bt_estado_cliente.setVisibility(View.INVISIBLE);
        bt_cierre.setVisibility(View.INVISIBLE);
        bt_gastos.setVisibility(View.INVISIBLE);
        btMorosos.setVisibility(View.INVISIBLE);
        btPaganHoy.setVisibility(View.INVISIBLE);
        bt_banca.setVisibility(View.INVISIBLE);
        tv_caja.setVisibility(View.INVISIBLE);
        //tv_saludo.setVisibility(View.INVISIBLE);
        tv_fecha.setVisibility(View.INVISIBLE);
        //btRepQuincenal.setVisibility(View.INVISIBLE);
        //btRepMensual.setVisibility(View.INVISIBLE);
    }

    private void mostrarTodo () {
        flag_salir_2 = true;
        Log.v("mostrarTodo_0", "MenuPrincipal.\n\nFlag_salir_2: " + flag_salir_2 + "\n\n.");
        //progressBar.setProgress(0);
        //progressBar.setVisibility(View.INVISIBLE);
        //tvProgressBar.setVisibility(View.INVISIBLE);
        amarillo.setVisibility(View.VISIBLE);
        verde.setVisibility(View.VISIBLE);
        rojo.setVisibility(View.VISIBLE);
        bt_nuevo_cliente.setVisibility(View.VISIBLE);
        bt_estado_cliente.setVisibility(View.VISIBLE);
        bt_cierre.setVisibility(View.VISIBLE);
        bt_gastos.setVisibility(View.VISIBLE);
        btMorosos.setVisibility(View.VISIBLE);
        btPaganHoy.setVisibility(View.VISIBLE);
        bt_banca.setVisibility(View.VISIBLE);
        tv_caja.setVisibility(View.VISIBLE);
        //tv_saludo.setVisibility(View.VISIBLE);
        tv_fecha.setVisibility(View.VISIBLE);
        //btRepQuincenal.setVisibility(View.VISIBLE);
        //btRepMensual.setVisibility(View.VISIBLE);

    }

    private void guardarClientes (String[] clientesArrary) {//Depende del formato Json para funcionar.
        int divisionesBarra = (clientesArrary.length);
        mostrarBarra("Guardando clientes...", divisionesBarra);
        //progressBar.setProgress(0);
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
                    //progressBar.setProgress(cont);
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
                        new GuardarArchivo(cliente, newFile, "arriba", getApplicationContext()).guardarCliente();
                    } else {
                        //Log.v("guardarClientes_1", "Main.\n\nCliente ya se ha ingresado:\n\n" + clientesEncontrados.get(idEncontrado) + "\n\n.");
                    }
                }
            }
        }
        getSheets();
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
        String refresh = "";
        mostrarBarra("Descargando creditos...", contSheets);
        //Leer archivo refresh_refresh_.txt y guardar su contenido en una variable string.
        try {
            InputStream inputStream = getApplicationContext().openFileInput(REFRESH);
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                StringBuilder stringBuilder = new StringBuilder();
                while ( (receiveString = bufferedReader.readLine()) != null ) {
                    stringBuilder.append(receiveString);
                }
                inputStream.close();
                bufferedReader.close();
                refresh = stringBuilder.toString();
            }
        } catch (FileNotFoundException e) {
            Log.e("guardarCreditos_0", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("guardarCreditos_0", "Can not read file: " + e.toString());
        }
        Log.v("guardarCreditos_0", "Main.\n\nrefresh: " + refresh + "\n\n.");
        for (String sheets : splitsheetName) {
            //Log.v("guardarCreditos_1", "Main.\n\nsheets: " + sheets + "\n\n.");
            if (!sheets.equals("solicitudes")) {
                if (!sheetsLeidas.containsKey(sheets)) {
                    if (!refresh.contains(sheets)) {
                        sheetsLeidas.put(sheets, sheets);
                        new AgregarLinea(sheets, REFRESH, this.getApplicationContext());
                    }
                }
            }
        }
        llenarMapas("first");
    }

    private void bajarInfoCreditos () {

        RequestQueue requestQueue;
        flag_salir_2 = false;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 256 * 256); // 1MB cap

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
                //break;
            }
        }
        //Log.v("bajarInfoCreditos_0", "Main.\n\nsheetLeer: " + sheetLeer + "\n\n.");
        if (keyToRemoveCierre != null) {
            sheetsLeidas.remove(keyToRemoveCierre);
            //bajarInfoCreditos();
        }
        // Formulate the request and handle the response.
        if (sheetLeer != null) {
            String url = readRowURL + spreadsheet_creditos + "&sheet=" + sheetLeer;
            String finalSheetLeer = sheetLeer;
            StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                    response -> {
                        if (response != null) {
                            //("bajarInfoCreditos_1", "Main.\n\nurl: " + url + "\n\n.");
                            //Log.v("bajarInfoCreditos_2", "Main.\n\nresponse:\n\n" + response + "\n\n.");
                            if (response.contains("DOCTYPE")) {
                                //Log.v("bajarInfoCreditos_3", "Main.\n\nDOCTYPE ERROR DOCTYPE ERROR DOCTYPE\n\nresponse:\n\n" + response + "\n\n.");
                                esperar2(1, "bajarInfoCreditos");
                                //bajarInfoCreditos();
                            } else {
                                sheetsLeidas.remove(finalSheetLeer);
                                if (!response.equals("[]")) {
                                    if (contadorBarra <= contSheets) {
                                        contadorBarra++;
                                        //.setProgress(contadorBarra);
                                    }
                                    //Log.v("bajarInfoCreditos_4", "Main.\n\nresponse:\n\n" + response + "\n\n.");
                                    // Obtener la instancia de la clase RequestQueue
                                    //RequestQueue requestQueuE = Volley.newRequestQueue(getApplicationContext());

                                    // Obtener la instancia de caché para una URL determinada
                                    //Cache cache = requestQueue.getCache();
                                    Cache.Entry entry = cache.get(url);
                                    if (entry != null) {
                                        // Eliminar la entrada de caché correspondiente
                                        cache.remove(url);
                                    }
                                    llenarMapas(response);
                                } else {
                                    contadorBarra++;
                                    //progressBar.setProgress(contadorBarra);
                                    bajarInfoCreditos();
                                }
                            }
                        } else {
                            //Log.v("bajarInfoCreditos_5", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\n.");
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

    private void actualizarCaja () throws IOException {
        long monto_nuevo = 0;
        String contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            monto_nuevo = cajita;
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
    }

    private Integer fechas (String cuadratura, String cuadraturA) {
        String[] split1 = cuadratura.split("__");
        String[] split2 = cuadraturA.split("__");
        String[] split11 = split1[0].split("_");
        String[] split22 = split2[0].split("_");
        String[] split111 = split11[3].split("/");
        String[] split222 = split22[3].split("/");
        String fechaSavedComplete = split111[2] + split111[1] + split111[0];
        String fechaNewComplete = split222[2] + split222[1] + split222[0];
        int fechaSavedInt = Integer.parseInt(fechaSavedComplete);
        int fechaNewInt = Integer.parseInt(fechaNewComplete);
        //Log.v("fechas_0", "Main.\n\nfechaSavedInt: " + fechaSavedInt + "\nfechaNewInt: " + fechaNewInt + "\n\n.");
        return fechaSavedInt - fechaNewInt;
    }

    private void crearArchivosCreditos () {
        boolean flagFinish,flagBorrar,flagGuardar;
        flagBorrar = false;
        flagGuardar = true;
        String fileName,fileContent,cuadratura,key1,key2;
        String fileNamE = "";
        //Naming convention. Key = ->
        //fileName + "_separ_" + cuadratura + "_separ_" + montoAbon;
        key1 = key2 = fileName = fileContent = cuadratura = "";
        if (!abonosLeidos.isEmpty()) {
            for (String key : abonosLeidos.keySet()) {//Se escoge algun abono que se encuentre en el map abonosLeidos.
                Log.v("crearArchivosCreditos_0", "Main.\n\nKey: " + key + "\n\nValue: " + abonosLeidos.get(key) + "\n\n.");
                String[] split = key.split("_separ_");
                fileName = split[0];
                fileContent = abonosLeidos.get(key);
                cuadratura = split[1];
                key1 = key;
                flagGuardar = false;
                break;
            }
            String keyBorrar = "";
            for (String key : abonosLeidos.keySet()) {
                key2 = key;
                String[] split = key.split("_separ_");
                fileNamE = split[0];
                String fileContenido = abonosLeidos.get(key);
                String cuadraturA = split[1];
                if (!key1.equals(key2)) {
                    if (fileNamE.equals(fileName)) {//Se trata de otro abono al mismo credito, o puede ser un credito distinto, pero con el mismo nombre.
                        flagGuardar = true;//crear map con los que si se van a guardar.
                        if (fechas(cuadratura, cuadraturA) > 0) {//se borra la que sea mas antigua, y se guarda la mas reciente.
                            //Se borra key2.
                            keyBorrar = key2;
                            flagBorrar = true;
                            break;
                        } else if (fechas(cuadratura, cuadraturA) < 0) {
                            //Se borra key1.
                            keyBorrar = key1;
                            flagBorrar = true;
                            break;
                        } else {
                            if (saldo(fileContent, cuadratura) > saldo(fileContenido, cuadraturA)) {
                                //Se borra key1.
                                keyBorrar = key1;
                                flagBorrar = true;
                                break;
                            } else if (saldo(fileContent, cuadratura) < saldo(fileContenido, cuadraturA)) {
                                //Se borra key2.
                                keyBorrar = key2;
                                flagBorrar = true;
                                break;
                            } else {
                                //Se borra la key que tenga mas intereses moratorios.
                                //Log.v("crearArchivosCreditos_1", "Main.\n\nKey elejida: " + elejirAdelantoInteres(abonosLeidos.get(key1), abonosLeidos.get(key2), key1, key2) +
                                //"\nKey1: " + key1 + "\nkey2: " + key2 + "\n\n.");
                                keyBorrar = elejirAdelantoInteres(abonosLeidos.get(key1), abonosLeidos.get(key2), key1, key2);
                                flagBorrar = true;
                                break;
                            }
                        }
                    }
                } else {
                    //Is the same key.
                    if (abonosLeidos.size() == 1) {
                        flagGuardar = false;
                    }
                }
            }
            //Log.v("crearArchivosCred_0", "Main.\n\nflagBorrar: " + flagBorrar + "\n\nflagGuardar: " + flagGuardar + "\n\nkey1: " + key1 + "\n\nkey2: " + key2 + "\n\nkeyBorrar: " + keyBorrar + "\n\n.");
            if (flagBorrar && flagGuardar) {
                if (fileName.equals(fileNamE)) {//To-do bien.
                    abonosLeidos.remove(keyBorrar);
                } else {//Control de errores...
                    //Log.v("CrearArchCredERROR_1", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\n.");
                }
            }
            if (!flagGuardar) {
                if (!abonosGuardar.containsKey(key1) && abonosLeidos.containsKey(key1)) {
                    abonosGuardar.put(key1, abonosLeidos.get(key1));
                    abonosLeidos.remove(key1);
                } else if (abonosLeidos.containsKey(key1)) {
                    abonosLeidos.remove(key1);
                } else {//Control de errores...
                    //Log.v("CrearArchCredERROR_2", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\n.");
                }
            }
            flagFinish = true;
            for (String key : abonosLeidos.keySet()) {//Se verifica que ya no existen abonos en el map.
                //Log.v("CrearArchCredDebug_0", "Main.\n\nkey: " + key + "\n\ncontenido:\n\n" + abonosLeidos.get(key) + "\n\n.");
                flagFinish = false;
                break;
            }
            if (flagFinish) {
                //Log.v("crearArchivosCreditos_1", "Main.\n\nflagFinish: " + flagFinish + "\n\n.");
                //concretarGuardado();
                //esperar2();
            } else {
                crearArchivosCreditos();
            }
        } else {
            //Log.v("crearArchivosCreditos_2", "Main.\n\nTarea terminada!!!\n\n.");
            //esperar2();
            if (!flagTrabajando) {
                concretarGuardado();
            }
        }
    }

    private void esperar2 (int cant, String method) {
        msg("Espere un minuto mas...");
        cant = (cant * 10) + 10;
        for (int i = 0; i < cant; i++) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (method.equals("cargarData")) {
            cargarData();
        } else if (method.equals("bajarInfoCreditos")) {
            bajarInfoCreditos();
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
            key = key1;
        } else if (InteresesMoratorios1 < InteresesMoratorios2) {
            //Log.v("elejirAdelantoInteres_2", "Main.\n\nIntereses moratorios 1: " + InteresesMoratorios1 + "\nIntereses moratorios 2: " + InteresesMoratorios2 + "\n\n.");
            key = key2;
        } else {
            key = revisarNextAbono(s1, s2, key1, key2);//Se borra la mas antigua.
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
            key = key1;// Or key = key2. It is the same!!!
            //Log.v("revisarNextAbonoERROR_0", "Main.\n\nERROR ERROR ERROR ERROR ERROR ERROR\n\nFecha next 1: " + fechaNext1 + "\nFecha next 2: " + fechaNext2 + "\n\n.");
        }
        return key;
    }

    private int saldo (String content, String cuadratura) {
        int monto = 0;
        if (content.contains(cuadratura)) {
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
        String[] split11 = split[11].split("_separador_");
        int interesesMoratori = Integer.parseInt(split11[1]);
        //Log.v("(polyForm)saldo_3", "Main.\n\ninteresMoratori: " + interesesMoratori + "\n\n.");
        saldoMasInter = saldoMasInter + interesesMoratori;
        //Log.v("(polyForm)saldo_4", "Main.\n\nsuma: " + saldoMasInter + "\n\n.");
        return saldoMasInter;
    }

    private boolean fechaMayor (String abonoViejo, String creditoNuevo) {
        String[] splitViejo11 = abonoViejo.split("\n");
        String[] splitNuevo11 = creditoNuevo.split("\n");
        boolean flag = false;
        int fechaVieja = 0,fechaNueva = 0;
        for (int i = 0; i < splitViejo11.length; i++) {
            String row = splitViejo11[i];
            if (row.contains("fecha_credito")) {
                String[] splitViejo1 = splitViejo11[i].split("_separador_");
                String fechaViejaS = splitViejo1[1];
                String dIa,mEs,anIo;
                String[] fechaViejaSplt = fechaViejaS.split("/");
                if (fechaViejaSplt[0].length() == 1) {
                    dIa = "0" + fechaViejaSplt[0];
                } else {
                    dIa = fechaViejaSplt[0];
                }
                if (fechaViejaSplt[1].length() == 1) {
                    mEs = "0" + fechaViejaSplt[1];
                } else {
                    mEs = fechaViejaSplt[1];
                }
                anIo = fechaViejaSplt[2];
                fechaViejaS = anIo + mEs + dIa;
                fechaVieja = Integer.parseInt(fechaViejaS);
                break;
            }
        }
        for (int i = 0; i < splitNuevo11.length; i++) {
            String row = splitNuevo11[i];
            if (row.contains("fecha_credito")) {
                String[] splitNuevo1 = splitNuevo11[i].split("_separador_");
                String fechaNuevaS = splitNuevo1[1];
                String dIa,mEs,anIo;
                String[] fechaNuevaSplt = fechaNuevaS.split("/");
                if (fechaNuevaSplt[0].length() == 1) {
                    dIa = "0" + fechaNuevaSplt[0];
                } else {
                    dIa = fechaNuevaSplt[0];
                }
                if (fechaNuevaSplt[1].length() == 1) {
                    mEs = "0" + fechaNuevaSplt[1];
                } else {
                    mEs = fechaNuevaSplt[1];
                }
                anIo = fechaNuevaSplt[2];
                fechaNuevaS = anIo + mEs + dIa;
                fechaNueva = Integer.parseInt(fechaNuevaS);
                break;
            }
        }
        Log.v("fechaMayor_0", "Main.\n\nfechaNueva: " + fechaNueva + "\n\nfechaVieja: " + fechaVieja + "\n\n.");
        if (fechaNueva == 0 || fechaVieja == 0) {
            Log.v("fechaMayor_1", "Main.\n\nERROR en fechas!!!\n\n.");
        } else {
            if (fechaVieja < fechaNueva) {
                flag = true;
            }
        }
        return flag;
    }

    private void concretarGuardado () {
        flagTrabajando = true;
        archivosGuardar.clear();
        mostrarBarra("Guardando creditos...", abonosGuardar.size());
        int contDebug = 0;
        //Log.v("concretarGuardDEBUG_0", "Main.\n\nInicio del ciclo...\n\n.");
        for (String key : abonosGuardar.keySet()) {
            //Log.v("concretarGuardDEBUG_" + contDebug, "Main.\n\nCuenta: " + contDebug + "\n\n.");
            contDebug++;
            String[] split = key.split("_separ_");
            if (!archivosGuardar.containsKey(split[0])) {
                archivosGuardar.put(split[0], abonosGuardar.get(key));//Llenamos a archivosGuardar con todos los abonos.
            }
        }
        for (String key : creditosLeidos.keySet()) {
            if (!archivosGuardar.containsKey(key)) {
                archivosGuardar.put(key, creditosLeidos.get(key));//Significa que no se ha hecho ningun abono a este credito.
            } else {
                if (fechaMayor(archivosGuardar.get(key), creditosLeidos.get(key))) {
                    archivosGuardar.replace(key, creditosLeidos.get(key));
                }// else: se queda to do igual!
            }
        }
        abonosLeidos.clear();
        HashMap<String, String> abonosLeidosTemp = new HashMap<>();
        for (String key : archivosGuardar.keySet()) {
            if (!abonosLeidosTemp.containsKey(key)) {
                //Log.v("concretarGuardado_-1", "Main.DEBUGG\n\nSe ha agregado el credito:\n\n" + archivosGuardar.get(key) + "\n\nSaldo del credito: " + saldo(archivosGuardar.get(key)) + "\n\nkey: " + key + "\n\n.");
                abonosLeidosTemp.put(key, archivosGuardar.get(key));//Copiamos el map archivosGuardar en abonosLeidosTemp.
            }
        }
        for (String key : abonosLeidosTemp.keySet()) {
            if (saldo(abonosLeidosTemp.get(key)) < 1000) {
                archivosGuardar.remove(key);
                //Log.v("concretarGuardado_0", "Main.\n\nSe ha borrado el credito:\n\n" + abonosLeidos.get(key) + "\n\nSaldo del credito: " + saldo(abonosLeidos.get(key)) + "\n\n.");
            }
        }
        contadorBarra = 0;
        for (String key : archivosGuardar.keySet()) {
            contadorBarra++;
            //progressBar.setProgress(contadorBarra);
            if (!archivo_existe(fileList(), archivosGuardar.get(key))) {
                try {
                    new GuardarArchivo(key, archivosGuardar.get(key), getApplicationContext()).guardarFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                try {
                    new BorrarArchivo(archivosGuardar.get(key), getApplicationContext());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                try {
                    new GuardarArchivo(key, archivosGuardar.get(key), getApplicationContext()).guardarFile();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        Log.v("concretarGuardado_0", "Main.\n\nSe han guardado " + archivosGuardar.size() + " creditos.\n\n.");
        mostrarTodo();
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
            //Log.v("llenarMapas_0", "Main.\n\nresponse:\n\n" + response + "\n\nresponsE:\n\n" + responsE + "\n\n.");
            if (responsE.contains("cuadratura")) {//Es el contenido de una sheet de abonos o creditos.
                String[] splitTransaccion = responsE.split("\"monto_credito\":\"");
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
                                tv_saludo.setText("Leyendo archivo " + fileName + "...");
                                //Log.v("llenarMapas_CREDITO", "Main.\n\nfileName(key): " + fileName + "\n\nContent:\n\n" + fileContent + "\n\n.");
                            } else {
                                revisarReciente(fileName, creditosLeidos.get(fileName), fileContent);
                            }
                        } else {
                            //Se crea nuevo separador: _separ_
                            //Naming convention:
                            //fileName + "_separ_" + cuadratura + "_separ_" + montoAbon;
                            String key = fileName + "_separ_" + cuadratura + "_separ_" + montoAbon;
                            if (!abonosLeidos.containsKey(key)) {
                                abonosLeidos.put(key, fileContent);
                                tv_saludo.setText("Leyendo archivo " + fileName + "...");
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
                int montoCaja = Integer.parseInt(montoString);
                cajita = montoCaja;
                Log.v("llenarMapas_2", "MenuPrincipal.\n\ndato: " + splitCaja[indice] + "\n\nmontoCaja: " + montoString + "\n\ncajita: " + cajita + "\n\n.");
                tv_saludo.setText("Leyendo archivo " + caja + "...");
            } else if (responsE.contains("tipo")) {
                contenidoCier = "fecha " + fecha + "\n";
                contenidoCier2 = "";
                tv_saludo.setText("Leyendo archivo " + "cierre.txt" + "...");
                String[] splitCierre = responsE.split("\"tipo\":\"");
                String tipo,monto,cajA,cliente;
                tipo = monto = cajA = cliente = "";
                for (String datoCierre : splitCierre) {
                    if (!datoCierre.equals("[{")) {
                        //Log.v("llenarMapas_3", "Main.\n\nDatoCierre: " + datoCierre + "\nfecha: " + fecha + "\n\n.");
                        String[] splitDatos = datoCierre.split("\",\"");
                        tipo = splitDatos[0];
                        String[] splitMonto = splitDatos[1].split("\":\"");
                        monto = splitMonto[1];
                        String[] splitCaja = splitDatos[2].split("\":\"");
                        cajA = splitCaja[1];
                        String[] splitCliente = splitDatos[3].split("\":\"");
                        cliente = splitCliente[1];
                        cliente = cliente.replace("\"", "");
                        cliente = cliente.replace("}", "");
                        cliente = cliente.replace("]", "");
                        cliente = cliente.replace(",", "");
                        cliente = cliente.replace("{", "");
                        String data = tipo + " " + monto + " " + cajA + " " + cliente;
                        if (!contenidoCier.contains(data)) {
                            contenidoCier = contenidoCier + data + "\n";
                            contenidoCier2 = contenidoCier2 + tipo + "_separador_" + monto + "_separador_" + cajA + "_separador_" + cliente + "\n";
                        }
                        //Log.v("llenarMapas_3", "Main.\n\nDatoCierre: " + datoCierre + "\nfecha: " + fecha + "\n\n.");
                    }
                }
                contenidoCier2 = contenidoCier2 + "estado_archivo_separador_arriba";
                //Log.v("llenarMapas_4", "Main.\n\ntipo: " + tipo + "\nmonto: " + monto + "\ncaja: " + cajA + "\ncliente: " + cliente + "\n\n.");
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

    private void revisarReciente (String key, String savedFile, String newFile) {
        String[] splitSaved = savedFile.split("\n");
        String[] splitNew = newFile.split("\n");
        String[] splitFechaSaved = splitSaved[3].split("_separador_");
        String fechaSaved = splitFechaSaved[1];
        String[] splitFechaNew = splitNew[3].split("_separador_");
        String fechaNew = splitFechaNew[1];
        //Log.v("revisarReciente_0", "Main.\n\nfechaSaved: " + fechaSaved + "\nfechaNew: " + fechaNew + "\n\n.");
        String[] fechaSavedSplited = fechaSaved.split("/");
        String[] fechaNewSplited = fechaNew.split("/");
        String fechaSavedComplete = fechaSavedSplited[2] + fechaSavedSplited[1] + fechaSavedSplited[0];
        String fechaNewComplete = fechaNewSplited[2] + fechaNewSplited[1] + fechaNewSplited[0];
        int fechaSavedInt = Integer.parseInt(fechaSavedComplete);
        int fechaNewInt = Integer.parseInt(fechaNewComplete);
        //Log.v("revisarReciente_1", "Main.\n\nfechaSavedInt: " + fechaSavedInt + "\nfechaNewInt: " + fechaNewInt + "\n\n.");
        if (fechaSavedInt < fechaNewInt) {
            creditosLeidos.replace(key, newFile);
        }
    }

    private boolean esCredito (String cuadratura, String montoAbono, String montoCuota) {
        //Log.v("esCredito_0", "Main.\n\nmontoAbono: " + montoAbono + "\nmontoCuota: " + montoCuota + "\n\n.");
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
        //Log.v("esCredito_1", "Main.\n\nflagCuadra: " + flagCuadra + "\nmontoCuota: " + montoCuota + "\n\n.");
        int montoAbonado = Integer.parseInt(montoAbono);
        if (montoAbonado > 0) {
            flagAbono = false;
        } else {
            flagAbono = true;
        }
        //Log.v("esCredito_2", "Main.\n\nflagAbono: " + flagAbono + "flagCuadra: " + flagCuadra + "\n\nflagAbono & flagCuadra: " +
        //String.valueOf((flagAbono & flagCuadra)) + "\nmontoCuota: " + montoCuota + "\n\n.");
        return (flagCuadra & flagAbono);
    }

    private void cargarData () {

        mostrarBarra("Leyendo informacion de la nube...", 4);

        RequestQueue requestQueue;

        // Instantiate the cache
        Cache cache = new DiskBasedCache(getCacheDir(), 256 * 256); // 1/4 MB cap

        // Set up the network to use HttpURLConnection as the HTTP client.
        BasicNetwork network = new BasicNetwork(new HurlStack());

        // Instantiate the RequestQueue with the cache and network.
        requestQueue = new RequestQueue(cache, network);

        // Start the queue
        requestQueue.start();

        String sheetClientes = "clientes";
        String url = readRowURL + spreadsheet_clientes + "&sheet=" + sheetClientes;
//        //Log.v("cargarData_0", "Main.\n\nurl: " + url + "\n\n.");
        // Formulate the request and handle the response.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                response -> {
                    if (response != null) {
                        //Log.v("cargarData_1", "Main.\n\nresponse:\n\n" + response + "\n\n.");
                        if (response.contains("DOCTYPE")) {
                            esperar2(1, "cargarData");
                            //cargarData();
                        } else {
                            String[] split = response.split("ID_cliente");
                            //int progresoMedio = ((split.length) / 2);
                            guardarClientes(split);
                            //progressBar.setProgress(progresoMedio);
                        }
                    }
                },
                error -> {
                    cargarData();
                });
        requestQueue.add(stringRequest);// Add the request to the RequestQueue.
    }

    private void requestPermissions () {
        /*isReadExternalPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;*/
        isManageExternalPermissionGranted = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
        isSendSmsPermissionGranted = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED;
        /*isWriteExternalPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;*/
        List<String> permissionRequest = new ArrayList<>();
        /*if (!isReadExternalPermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }*/
        if (!isManageExternalPermissionGranted) {
            permissionRequest.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        if (!isSendSmsPermissionGranted) {
            permissionRequest.add(Manifest.permission.SEND_SMS);
        }
        /*if (!isWriteExternalPermissionGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/
        if (!permissionRequest.isEmpty()) {
            sPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }

    private void verArchivos () {
        String[] files = fileList();
        int cont = 0;
        for (String file : files) {
            Log.v("verArchivos_" + cont, "MenuPrincipal.\n\nfile: " + file + "\n\ncontenido:\n\n" + imprimir_archivo(file) + "\n\n.");
            cont++;
        }
    }

    private void mostrarEstado () {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("estado_online.txt"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            Log.v("mostrarEstado_0", "MenuPrincipar.\n\nlinea: " + linea + "\n\n.");
            if (linea.equals("verde")) {
                verde.setVisibility(View.VISIBLE);
                rojo.setVisibility(View.INVISIBLE);
                amarillo.setVisibility(View.INVISIBLE);
            } else if (linea.equals("amarillo")) {
                verde.setVisibility(View.INVISIBLE);
                rojo.setVisibility(View.INVISIBLE);
                amarillo.setVisibility(View.VISIBLE);
            } else if (linea.equals("rojo")) {
                verde.setVisibility(View.INVISIBLE);
                rojo.setVisibility(View.VISIBLE);
                amarillo.setVisibility(View.INVISIBLE);
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(null);
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        dia = datosFecha.getDia();
        fecha = datosFecha.getDia();
    }

    private void corregirArchivos () throws IOException {
        //////// ARCHIVO cierre  ////////////////////////////////////////////////////////////
        String archivos[] = fileList();
        boolean flag_borrar = false;
        if (archivo_existe(archivos, "cierre.txt")) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("cierre.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                if (linea != null) {
                    String[] split = linea.split(" ");
                    int fecha_file = Integer.parseInt(split[1]);
                    int hoy_fecha = Integer.parseInt(dia);
                    if (fecha_file != hoy_fecha) {
                        flag_borrar = true;
                    }
                } else {
                    flag_borrar = true;
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new AgregarLinea("fecha " + dia, "cierre.txt", getApplicationContext());//La clase AgregarLinea crea el archivo en caso de que este no exista.
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }
        if (flag_borrar) {
            new BorrarArchivo("cierre.txt", getApplicationContext());
            new AgregarLinea("fecha " + dia, "cierre.txt", getApplicationContext());
            new BorrarArchivo("cierre_cierre_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }
    }

    private void mostrar_caja () {
        String caja = "caja.txt";
        tv_caja.setText(imprimir_archivo(caja));
    }

    public void abonar (View view){
        Intent abonar = new Intent(this, AbonarActivity.class);
        abonar.putExtra("msg", "");
        abonar.putExtra("cliente_recivido", "");
        abonar.putExtra("abono_cero", "");
        startActivity(abonar);
        finish();
        System.exit(0);
    }

    public void banca(View view){
        Intent banca = new Intent(this, BancaActivity.class);
        banca.putExtra("msg", "");
        banca.putExtra("cliente_recivido", "");
        startActivity(banca);
        finish();
        System.exit(0);
    }

    public void gastos (View view){
        Intent gastos = new Intent(this, GastosActivity.class);
        gastos.putExtra("msg", "");
        gastos.putExtra("cliente_recivido", "");
        startActivity(gastos);
        finish();
        System.exit(0);
    }

    public void estado_cliente (View view){
        Intent estado_cliente = new Intent(this, Estado_clienteActivity.class);
        estado_cliente.putExtra("cliente_ID", "");
        startActivity(estado_cliente);
        finish();
        System.exit(0);
    }

    public void paganHoy (View view){
        Intent paganHoy = new Intent(this, Pagan_hoy.class);
        startActivity(paganHoy);
        finish();
        System.exit(0);
    }

    public void quincenas (View view){
        if (flag_salir_2) {
            Intent quincenasPagan = new Intent(this, QuincenasActivity.class);
            startActivity(quincenasPagan);
            finish();
            System.exit(0);
        } else {
            msg("Espere a que termine la operacion...");
        }
    }

    public void meses (View view){
        if (flag_salir_2) {
            Intent mesesPagan = new Intent(this, MesesActivity.class);
            startActivity(mesesPagan);
            finish();
            System.exit(0);
        } else {
            msg("Espere a que termine la operacion...");
        }
    }

    public void registrar_cliente_nuevo (View view){
        Intent registrar_cliente_nuevo = new Intent(this, Registrar_cliente_nuevoActivity.class);
        startActivity(registrar_cliente_nuevo);
        finish();
        System.exit(0);
    }

    public void cierre (View view){
        Intent cierre = new Intent(this, CierreActivity.class);
        startActivity(cierre);
        finish();
        System.exit(0);
    }

    public void refinanciar (View view){
        //Intent refinanciar = new Intent(this, Re_financiarActivity.class);
        //refinanciar.putExtra("msg", "");
        //refinanciar.putExtra("cliente_recivido", "");
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        //startActivity(refinanciar);
        //finish();
        //System.exit(0);
    }

    public void nuevo_credito (View view){
        Intent nuevo_credito = new Intent(this, Nuevo_creditoActivity.class);
        nuevo_credito.putExtra("msg", "");
        nuevo_credito.putExtra("cliente_recivido", "");
        nuevo_credito.putExtra("activity_devolver", "MenuPrincipal");
        startActivity(nuevo_credito);
        finish();
        System.exit(0);
    }

    public void morosos (View view){
        Intent morosos = new Intent(this, MorososActivity.class);
        startActivity(morosos);
        finish();
        System.exit(0);
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
    public void onBackPressed (){
        if (!flag_salir_2) {
            msg("Espere a que termine la operacion...");
        } else {
            msg("Presione atras nuevamente para salir...");
        }
        boton_atras();
    }

    private void boton_atras () {
        if (flag_salir && flag_salir_2) {
            Log.v("onDestroy_0", "MenuPrincipal.\n\nContext de la aplicacion:\n\n" +
                    getApplicationContext().toString() + "\n\n.");
            stopService (new Intent(getApplicationContext(), SubirArchivo.class));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
            System.exit(0);
        } else {
            flag_salir = true;
        }
    }

    private String imprimir_archivo (String file_name){
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

    private void msg (String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}