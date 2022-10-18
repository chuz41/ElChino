package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
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

public class BancaActivity extends AppCompatActivity {

    private Integer monto_credito = 0;
    private Integer monto_cuota = 0;
    private String fecha_pago = "";//Fecha que debe pagar la proxima cuota.
    private Integer saldo_mas_intereses = 0;
    private Integer tasa = 0;//Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
    private String ID_credito = "";
    private String plazo = "";//Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%) Se elige con un spinner


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
    private String onlines = "onlines.txt";
    private Button bt_entregar;
    private Button bt_recibir;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private String monto_disponible = "";
    //private Spinner sp_plazos;
    private boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private String caja = "caja.txt";
    private String credit_ID = "";

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
        //sp_plazos = (Spinner) findViewById(R.id.sp_plazos);
        //sp_plazos.setVisibility(View.INVISIBLE);
        tv_saludo.setText("ABONAR/RECIBIR FONDOS DE BANCA");
        //et_ID.setVisibility(View.INVISIBLE);
        separar_fechaYhora();
        if (cliente_recibido.equals("")) {
            //Do nothing.
        } else {
            flag_client_reciv = true;
            cliente_ID = cliente_recibido;
        }
        text_listener();
    }

    private void actualizar_disponible (String operador) {
        int monto_abono = Integer.parseInt(et_ID.getText().toString());
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            int monto_nuevo = 0;
            if (operador.equals("sumar")) {
                monto_nuevo = Integer.parseInt(split[1]) + monto_abono;
            } else if (operador.equals("restar")) {
                monto_nuevo = Integer.parseInt(split[1]) - monto_abono;
            } else {
                //Do nothing.
            }
            linea = linea.replace(split[1], String.valueOf(monto_nuevo));
            br.close();
            archivo.close();
            borrar_archivo(caja);
            crear_archivo(caja);
            guardar(linea, caja);
            esperar("Operacion bancaria realizada con exito.\nMonto en caja: " + monto_nuevo, "Pendiente de confirmacion por parte de la Banca!!!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void abonar (View view) {
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        actualizar_disponible("restar");
    }

    public void recibir (View view) {
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        actualizar_disponible("sumar");
    }

    /*public void consultar (View view) throws JSONException, IOException {
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);

        if (tv_esperar.getText().toString().equals("Escoja el plazo del credito")) {
            restar_disponible();
            generar_credito();
        } else if (tv_esperar.getText().toString().equals("Digite el monto del abono")) {//TODO: Rechazar si supera el monto disponible del cliente.

            monto_credito = Integer.parseInt(et_ID.getText().toString());
            Log.v("monto_credito", ".\n\nmonto_credito: " + monto_credito + "\n\n.");
            et_ID.setText("");
            et_ID.setFocusableInTouchMode(false);
            et_ID.setEnabled(false);
            et_ID.setVisibility(View.INVISIBLE);
            bt_entregar.setVisibility(View.INVISIBLE);
            bt_recibir.setVisibility(View.INVISIBLE);
            tv_esperar.setText("");
            tv_esperar.setVisibility(View.INVISIBLE);
            sp_plazos.setVisibility(View.VISIBLE);
            //tv_esperar.setText("Escoja el plazo del credito");
            //restar_disponible();
            //llenar_spinner();
            obtener_plazo();
        } else if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
            String archivos[] = fileList();
            String puntuacion_cliente = "";
            String archivoCompleto = "";
            String file_to_consult = "";
            if (flag_client_reciv) {
                file_to_consult = cliente_recibido;
            } else {
                file_to_consult = et_ID.getText().toString();
            }
            for (int i = 0; i < archivos.length; i++) {
                Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(archivos[i]);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    //TODO: Abrir archivo y leerlo.
                    try {
                        InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        while (linea != null) {
                            Log.v("Digite_cedula", ".\n\nlinea:\n\n" + linea + "\n\n.");
                            String[] split = linea.split("_separador_");
                            if (split[0].equals("puntuacion_cliente")) {
                                puntuacion_cliente = split[1];
                            }
                            if (split[0].equals("ID_cliente")) {
                                cliente_ID = split[1];
                            }
                            if (split[0].equals("monto_disponible")) {
                                monto_disponible = split[1];
                            }
                            linea = linea.replace("_separador_", ": ");
                            linea = linea.replace("_cliente", "");
                            linea = linea.replace("_", " ");
                            archivoCompleto = archivoCompleto + linea + "\n";
                            linea = br.readLine();
                        }
                        br.close();
                        archivo.close();
                    } catch (IOException e) {
                    }
                    break;
                } else {
                    //Continue with the execution.
                }
            }
            if (archivoCompleto.equals("")) {
                //No se encontro el cliente
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
                text_listener();
            } else {
                Toast.makeText(this, "Cliente encontrado", Toast.LENGTH_SHORT).show();
                et_ID.setText("");
                et_ID.setFocusableInTouchMode(false);
                et_ID.setEnabled(false);
                et_ID.setVisibility(View.INVISIBLE);
                bt_entregar.setVisibility(View.INVISIBLE);
                bt_recibir.setVisibility(View.INVISIBLE);
                tv_esperar.setText("");
                tv_esperar.setVisibility(View.INVISIBLE);
                //Aqui se llama al metodo principal.
                entregar_recibir_fondos();
            }
        } else {
            //TODO: no se sabe que hacer aqui!!!
        }
    }*/

    private void llenar_spinner () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        String plazos = "Escoja el plazo del credito_5 semanas (20%)_6 semanas (20%)_9 semanas (40%)_3 quincenas (25%)_5 quincenas (40%)";
        String[] split = plazos.split("_");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.custom_spinner, split);
        //sp_plazos.setAdapter(adapter2);
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        //spinner_listener();
    }

    /*private void spinner_listener () {
        sp_plazos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = sp_plazos.getSelectedItem().toString();
                        if (seleccion.equals("Escoja el plazo del credito")) {
                            bt_entregar.setClickable(false);
                            bt_entregar.setEnabled(false);
                            bt_recibir.setClickable(false);
                            bt_recibir.setEnabled(false);
                            //Do nothing!
                        }else {
                            plazo = sp_plazos.getSelectedItem().toString();
                            bt_entregar.setEnabled(true);
                            bt_entregar.setClickable(true);
                            bt_entregar.setVisibility(View.VISIBLE);
                            bt_entregar.setFocusableInTouchMode(true);
                            bt_entregar.requestFocus();
                            bt_recibir.setEnabled(true);
                            bt_recibir.setClickable(true);
                            bt_recibir.setVisibility(View.VISIBLE);
                            bt_recibir.setFocusableInTouchMode(true);
                            bt_recibir.requestFocus();
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }*/

    private String calcular_cuota () {
        String flag = "";
        int interes = 0;
        int cuotas = 0;
        String[] split = plazo.split(" ");
        if (split[1].equals("semanas")) {
            if (split[0].equals("5")) {
                interes = 20;
                cuotas = 5;
            } else if (split[0].equals("6")) {
                interes = 20;
                cuotas = 6;
            } else if (split[0].equals("9")) {
                interes = 40;
                cuotas = 9;
            } else {
                //Do nothing. Never come here!!!
            }
        } else if (split[1].equals("quincenas")) {
            if (split[0].equals("3")) {
                interes = 25;
                cuotas = 3;
            } else if (split[0].equals("5")) {
                interes = 40;
                cuotas = 5;
            } else {
                //Do nothing. Never come here!!!
            }
        } else {
            //Do nothing. Never come here!!!
        }
        double monto_total = monto_credito + ((monto_credito * interes) / 100);
        double cuota = monto_total / cuotas;
        int flag_int = (int) cuota;
        Log.v("monto_total", ".\n\nMonto total: " + monto_total + "\n\nMonto del credito: " + monto_credito + "\n\n.");
        flag = String.valueOf(flag_int);
        Log.v("flag",".\n\nFlag: " + flag + "\n\n.");
        return flag;
    }

    private String calcular_saldo () {
        String flag = "";
        int interes = 0;
        String[] split = plazo.split(" ");
        if (split[1].equals("semanas")) {
            if (split[0].equals("5")) {
                interes = 20;
            } else if (split[0].equals("6")) {
                interes = 20;
            } else if (split[0].equals("9")) {
                interes = 40;
            } else {
                //Do nothing. Never come here!!!
            }
        } else if (split[1].equals("quincenas")) {
            if (split[0].equals("3")) {
                interes = 25;
            } else if (split[0].equals("5")) {
                interes = 40;
            } else {
                //Do nothing. Never come here!!!
            }
        } else {
            //Do nothing. Never come here!!!
        }
        double monto_total = monto_credito + ((monto_credito * interes) / 100);
        int flag_int = (int) monto_total;
        flag = String.valueOf(flag_int);
        return flag;
    }

    private String obtener_tasa () {
        String flag = "";
        int interes = 0;
        String[] split = plazo.split(" ");
        if (split[1].equals("semanas")) {
            if (split[0].equals("5")) {
                interes = 20;
            } else if (split[0].equals("6")) {
                interes = 20;
            } else if (split[0].equals("9")) {
                interes = 40;
            } else {
                //Do nothing. Never come here!!!
            }
        } else if (split[1].equals("quincenas")) {
            if (split[0].equals("3")) {
                interes = 25;
            } else if (split[0].equals("5")) {
                interes = 40;
            } else {
                //Do nothing. Never come here!!!
            }
        } else {
            //Do nothing. Never come here!!!
        }
        flag = String.valueOf(interes);
        return flag;
    }

    private String calcular_cuotas () {
        String flag = "";
        int cuotas = 0;
        String[] split = plazo.split(" ");
        if (split[1].equals("semanas")) {
            if (split[0].equals("5")) {
                cuotas = 5;
            } else if (split[0].equals("6")) {
                cuotas = 6;
            } else if (split[0].equals("9")) {
                cuotas = 9;
            } else {
                //Do nothing. Never come here!!!
            }
        } else if (split[1].equals("quincenas")) {
            if (split[0].equals("3")) {
                cuotas = 3;
            } else if (split[0].equals("5")) {
                cuotas = 5;
            } else {
                //Do nothing. Never come here!!!
            }
        } else {
            //Do nothing. Never come here!!!
        }
        flag = String.valueOf(cuotas);
        return flag;
    }

    private String obtener_proximo_abono () {
        String flag = "alguna fecha";



        return flag;
    }
/*
    private void generar_credito () throws IOException, JSONException {
        String file_content = "";
        file_content = file_content + "monto_credito_separador_" + monto_credito + "\n";
        String monto_cuota = calcular_cuota();
        file_content = file_content + "monto_cuota_separador_" + monto_cuota + "\n";
        String fecha_credito = dia + "/" + mes + "/" + anio;
        file_content = file_content + "fecha_credito_separador_" + fecha_credito + "\n";
        String saldo_mas_intereses = calcular_saldo();
        file_content = file_content + "saldo_mas_intereses_separador_" + saldo_mas_intereses + "\n";
        String tasa_interes = obtener_tasa();
        file_content = file_content + "tasa_separador_" + tasa_interes + "\n";
        String proximo_abono = obtener_proximo_abono();
        file_content = file_content + "proximo_abono_separador_" + proximo_abono + "\n";
        String cuotass = calcular_cuotas();
        file_content = file_content + "cuotas_separador_" + cuotass + "\n";
        credit_ID = obtener_id();
        file_content = file_content + "ID_credito_separador_" + credit_ID + "\n";
        String file_name = credit_ID + ".txt";
        crear_archivo(file_name);
        guardar(file_content, file_name);
        actualizar_caja();
        subir_archivo(file_name);
    }
*/
    private void actualizar_caja () {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            int monto_nuevo = Integer.parseInt(split[1]) - monto_credito;
            linea = linea.replace(split[1], String.valueOf(monto_nuevo));
            br.close();
            archivo.close();
            borrar_archivo(caja);
            crear_archivo(caja);
            guardar(linea, caja);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String obtener_id () {
        String flag = "";
        String cliente_file = cliente_ID + "_C_.txt";
        String lista_archivos = "";
        String archivos[] = fileList();
        for (int i = 0; i < archivos.length; i++) {
            Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                lista_archivos = lista_archivos + archivos[i] + "_sep_";
            }
        }
        int end_id = 0;
        if (lista_archivos.equals("")) {
            flag = "1";
        } else {
            String[] split = lista_archivos.split("_sep_");
            int spl_long = split.length;
            /*for (int i = 0; i < spl_long; i++) {
                String[] splii = split[i].split("_P_");
                end_id = end_id + Integer.parseInt(splii[1]);
            }*/
            end_id = spl_long + 1;
            flag = String.valueOf(end_id);
        }
        flag = cliente_ID + "_P_" + String.valueOf(flag) + "_P_";
        return flag;
    }

    private void entregar_recibir_fondos () {
        //Algoritmo principal
        tv_esperar.setVisibility(View.VISIBLE);
        //tv_esperar.setText("Digite el monto del abono");
        et_ID.setEnabled(true);
        et_ID.setVisibility(View.VISIBLE);
        et_ID.requestFocus();
        et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_ID.setClickable(true);
        et_ID.setText("");
        et_ID.setFocusableInTouchMode(true);
        et_ID.requestFocus();
        //et_ID.setText("0");
        bt_entregar.setText("ENTREGAR");
        bt_entregar.setVisibility(View.VISIBLE);
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir.setText("RECIBIR");
        bt_recibir.setVisibility(View.VISIBLE);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        text_listener();
    }

    private void obtener_plazo () {

        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("Opcion x");
        bt_entregar.setText("ENTREGAR");
        bt_recibir.setText("RECIBIR");
        llenar_spinner();
        //spinner_listener();
    }

    private void text_listener() {

        //Implementacion de un text listener
        et_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
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
                            int monto = Integer.parseInt(split[1]);
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

    private void esperar (String s1, String s2) {
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        et_ID.setVisibility(View.INVISIBLE);
        ocultar_todito();
        tv_esperar.setText("Conectando, por favor espere...");
        Toast.makeText(this, s1, Toast.LENGTH_LONG).show();
        tv_esperar.setText(s1);
        for (int i = 0; i > 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        salir(s2);
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
        menu_principal.putExtra("mensaje", "");
        startActivity(menu_principal);
        finish();
        System.exit(0);
    }

    private void mostrar_todito() {
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        bt_entregar.setVisibility(View.VISIBLE);
        bt_recibir.setVisibility(View.VISIBLE);
        //sp_plazos.setVisibility(View.VISIBLE);
    }

    private void ocultar_todito() {
        Log.v("ocultar_todito", "Se hace todo invisible");
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("conectando, por favor espere...");
        bt_entregar.setVisibility(View.INVISIBLE);
        bt_recibir.setVisibility(View.INVISIBLE);
        //sp_plazos.setVisibility(View.INVISIBLE);
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
        String sp_creditos = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split(" ");
                if (split[0].equals("Screditos")) {
                    sp_creditos = split[1];
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        String spid = sp_creditos;
        String json_string = "";
        JSONObject jsonObject = new JSONObject();
        String sheet = "creditos";
        String id_credito = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null && !linea.equals("")) {
                String[] split = linea.split("_separador_");
                Log.v("subir_archivo", ".\n\nLinea:\n\n" + linea + "\n\n.");
                if (split[0].equals("credit_ID")) {
                    id_credito = split[1];
                } else {
                    //split = linea.split("_separador_");
                    json_string = json_string + split[1] + "_n_";
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        jsonObject = TranslateUtil.string_to_Json(json_string, spid, sheet, id_credito);
        subir_nuevo_credito(jsonObject, file);
    }

    private void subir_nuevo_credito (JSONObject jsonObject, String file) {
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
                                if (split[21].equals(credit_ID)) {//TODO: Todo de arriba tiene que ver tambien con este.
                                    cambiar_bandera1(file);
                                    //esperar("\"Credito generado y registrado correctamente en el servidor.\"");
                                } else {
                                    //esperar("Error al subir informacion del credito al servidor. Conectese a internet!!!");
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
            //esperar("\"Para registrar al vendedor en el servidor, debe estar conectado a internet.\"");

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