package com.example.elchino;

import static java.time.temporal.ChronoUnit.DAYS;

import androidx.annotation.ColorInt;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
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
import com.example.elchino.Util.DateUtilities;
import com.example.elchino.Util.TranslateUtil;
import com.example.elchino.Util.BluetoothUtil;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CuadraturaActivity extends AppCompatActivity {

    private Integer monto_abono = 0;
    private Button bt_imprimir;
    private Integer monto_cuota = 0;
    private String cambio;
    private String fecha_pago = "";//Fecha que debe pagar la proxima cuota.
    private Integer saldo_mas_intereses = 0;
    private Integer tasa = 0;//Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
    private String ID_credito = "";
    private String credito_aplicar = "";
    private String morosidad = "D";
    private Integer cantidad_cuotas_pendientes = 0;
    private String interes_mora_parcial;
    private String archivo_prestamo = "";
    private String plazo = "";
    private String cuotas = "";
    private EditText et_ID;
    private TextView tv_esperar;
    private String activity_devolver;
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
    private Button bt_consultar;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private String monto_disponible = "";
    private boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private String abono_cero = "0";
    private String caja = "caja.txt";
    private String credit_ID = "";
    private int cantidad_de_creditos = 0;
    private String interes_mora = "";
    private String interes_mora_total = "0";
    private String lista_archivos = "";
    private String proximo_abono = "";
    private String puntuacion_cliente = "";
    private String presentar_et_esperar = "";
    private String cuadratura = "";
    private Spinner sp_plazos;
    private Button sequi1;
    private Button sequi2;
    private Button sequi3;
    private Button sequi4;
    private Button sequi5;
    private Button sequi6;
    private Button sequi7;
    private Button sequi8;
    private Button sequi9;
    private TextView tv_cambio;
    private String monto_creditito;
    private String cliente_Id_volver;
    private String mensaje_imprimir = "";
    private String mensaje_imprimir_pre = "";
    private String nombre_cliente = "";
    private String apellido_cliente = "";

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuadratura);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        cuadratura = getIntent().getStringExtra( "cuadratura");
        cambio = getIntent().getStringExtra("cambio");
        activity_devolver = getIntent().getStringExtra("activity_devolver");
        monto_creditito = getIntent().getStringExtra("monto_creditito");
        mensaje_imprimir_pre = getIntent().getStringExtra("mensaje_imprimir_pre");
        tv_cambio = (TextView) findViewById(R.id.tv_cambio);
        bt_imprimir = (Button) findViewById(R.id.bt_imprimir);
        bt_imprimir.setVisibility(View.INVISIBLE);
        tv_cambio.setVisibility(View.INVISIBLE);
        Integer cambio_int = Integer.valueOf(cambio);
        if (cambio_int > 0) {
            tv_cambio.setText("Cambio:\n\n" + cambio);
            tv_cambio.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Devolver " + cambio + " colones sobrantes.", Toast.LENGTH_LONG).show();
        }
        if (Integer.parseInt(monto_creditito) > 0) {
            tv_cambio.setText("Prestamo aprobado.\n\nMonto del credito:\n" + monto_creditito + " colones.");
            tv_cambio.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Entregar " + monto_creditito + " colones al cliente.", Toast.LENGTH_LONG).show();
        }
        et_ID = (EditText) findViewById(R.id.et_ID);
        sp_plazos = (Spinner) findViewById(R.id.sp_plazos);
        sp_plazos.setVisibility(View.INVISIBLE);
        bt_consultar = (Button) findViewById(R.id.bt_consultar_ab);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        if (mensaje_recibido.equals("")) {
            //Do nothing.
        } else {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        cliente_Id_volver = cliente_recibido;
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        sequi1 = (Button) findViewById(R.id.sequi1);
        sequi2 = (Button) findViewById(R.id.sequi2);
        sequi3 = (Button) findViewById(R.id.sequi3);
        sequi4 = (Button) findViewById(R.id.sequi4);
        sequi5 = (Button) findViewById(R.id.sequi5);
        sequi6 = (Button) findViewById(R.id.sequi6);
        sequi7 = (Button) findViewById(R.id.sequi7);
        sequi8 = (Button) findViewById(R.id.sequi8);
        sequi9 = (Button) findViewById(R.id.sequi9);
        sequi1.setVisibility(View.INVISIBLE);
        sequi2.setVisibility(View.INVISIBLE);
        sequi3.setVisibility(View.INVISIBLE);
        sequi4.setVisibility(View.INVISIBLE);
        sequi5.setVisibility(View.INVISIBLE);
        sequi6.setVisibility(View.INVISIBLE);
        sequi7.setVisibility(View.INVISIBLE);
        sequi8.setVisibility(View.INVISIBLE);
        sequi9.setVisibility(View.INVISIBLE);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("ESTADO DE CUENTA\n\nCliente ID: " + cliente_recibido);
        separar_fechaYhora();

        if (!cuadratura.equals("")) {
            try {
                presentar_cuadratura();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (cliente_recibido.equals("")) {
                //Do nothing.
            } else if (cliente_recibido.equals("CERO")) {
                //Do nothing.
            } else {
                flag_client_reciv = true;
                cliente_ID = cliente_recibido;
                try {
                    consultar(null);
                } catch (JSONException | InterruptedException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            text_listener();
        }
    }

    private String obtener_cuotas_morosas (String cuotas_pendientes, String plazo, String fecha_proximo_abono) throws ParseException {
        String flag = "";
        int factor = 0;
        int dias_atrasados = 0;
        int cantidad_abonos = 0;
        String[] split = plazo.split("_");
        Log.v("cuotas_morosas", ".\n\nCuotas pendientes: " + cuotas_pendientes + "\n\nPlazo: " + plazo + "\n\nFecha proximo abono: " + fecha_proximo_abono + "\n\n.");
        cantidad_abonos = Integer.parseInt(split[0]);
        if (split[1].equals("semanas")) {
            factor = 1;
        } else if (split[1].equals("quincenas")) {
            factor = 2;
        }

        String[] split_fecha_next = fecha_proximo_abono.split("/");
        fecha_proximo_abono = split_fecha_next[2] + "-" + split_fecha_next[1] + "-" + split_fecha_next[0];
        Date fehca_next_abono = DateUtilities.stringToDate(fecha_proximo_abono);
        Date fecha_de_hoy = Calendar.getInstance().getTime();
        dias_atrasados = DateUtilities.daysBetween(fecha_de_hoy, fehca_next_abono);//Cantidad positiva indica morosidad.

        if (dias_atrasados < 0) {
            flag = "0";
        } else if (dias_atrasados == 0) {
            flag = "1";
        } else {
            morosidad = "M";
            //Algoritmo que calcula las cuotas pendientes.
            double cantidad_de_cuotas_pendientes = (dias_atrasados / (7 * factor));
            if (cantidad_de_cuotas_pendientes <= 1) {
                puntuacion_cliente = String.valueOf(Integer.parseInt(puntuacion_cliente) - 1);
                flag = "1";
            } else {
                int c_d_c_p = (int) cantidad_de_cuotas_pendientes;
                puntuacion_cliente = String.valueOf(Integer.parseInt(puntuacion_cliente) - c_d_c_p);
                if (cantidad_abonos < cantidad_de_cuotas_pendientes) {
                    cantidad_de_cuotas_pendientes = cantidad_abonos;
                    flag = String.valueOf(cantidad_de_cuotas_pendientes);
                } else {
                    flag = String.valueOf(c_d_c_p);
                }
            }
        }
        Log.v("Obteniend_cuotas_moros", ".\n\nflag: " + flag + "\n\n.");
        cantidad_cuotas_pendientes = Integer.parseInt(flag);
        return flag;
    }

    
    private void llenar_spinner () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        String creditos = "Escoja el credito...___";
        String archivos[] = fileList();
        for (int i = 0; i < archivos.length; i++) {
            Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                try {
                    String fecha_next_abono = "";
                    String intereses_mor = "";
                    String saldo_mas_intereses_s = "";
                    String plazoz = "";
                    String numero_de_credito = "";
                    String cuotas_morosas = "";
                    String cuadratura_pre = "";
                    int factor_semanas = 0;
                    String file_name = archivos[i];
                    String[] split_indice = file_name.split("_P_");
                    numero_de_credito = split_indice[1];
                    InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    while (linea != null) {
                        Log.v("llenando_spinner", ".\n\nLinea:\n\n" + linea + "\n\n.");
                        String[] split = linea.split("_separador_");
                        if (split[0].equals("proximo_abono")) {
                            fecha_next_abono = split[1];
                        }
                        if (split[0].equals("plazo")) {
                            plazoz = split[1];
                        }
                        if (split[0].equals("cuadratura")) {
                            cuadratura_pre = split[1];
                        }
                        if (split[0].equals("saldo_mas_intereses")) {
                            saldo_mas_intereses_s = split[1];
                        }
                        if (split[0].equals("cuotas")) {
                            cuotas_morosas = split[1];
                        }
                        if (split[0].equals("intereses_moratorios")) {
                            intereses_mor = split[1];
                        }
                        linea = br.readLine();
                    }

                    br.close();
                    archivo.close();
                    String[] piezas = plazoz.split("_");
                    Log.v("llenando_spinner2", ".\n\nPlazoz: " + plazoz + "\n\npiezas[0]: " + piezas[0] + "\n\n.");
                    if (piezas[1].equals("quincenas")) {
                        factor_semanas = 2;
                    } else if (piezas[1].equals("semanas")) {
                        factor_semanas = 1;
                    } else {
                        factor_semanas = -1;
                        //ERROR
                    }

                    String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                    String intereses_moritas = obtener_intereses_moratorios(saldo_plus_s, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                    interes_mora_total = intereses_moritas;
                    cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0);
                    saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor);
                    cuotas_morosas = obtener_cuotas_morosas(cuotas_morosas, plazoz, fecha_next_abono);

                    Log.v("llenando_spinner2", "Cuadratura.\n\nMorosidad: " + morosidad + "\n\n.");
                    double saldo_mas_intereses_D = Double.parseDouble(saldo_mas_intereses_s);
                    int saldo_mas_intereses_I = (int) saldo_mas_intereses_D;
                    saldo_mas_intereses_s = String.valueOf(saldo_mas_intereses_I);

                    if (Integer.parseInt(saldo_mas_intereses_s) > 100) {
                        creditos = creditos + "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas + "___";
                    } else {
                        //Do notring.
                    }
                    //Log.v("restar_disponible2", ".\n\nArchivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
                } catch (IOException e) {
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        String[] split_spinner = creditos.split("___");
        sp_plazos.setEnabled(true);
        sp_plazos.setVisibility(View.VISIBLE);
        tv_esperar.setText("Credito:");
        et_ID.setText("");
        et_ID.setEnabled(false);
        et_ID.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, split_spinner);
        sp_plazos.setAdapter(adapter);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        spinner_listener();
    }


    private String obtener_intereses_moratorios (String saldo_plus, String next_pay) throws ParseException {
        String flag = "";
        String saldo = "";
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Log.v("obt_int_morat0", ".\n\nCuadratura. Proximo abono: " + proximo_abono_formato + "\n\n.");
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        Date fecha_hoy = Calendar.getInstance().getTime();
        int diferencia_en_dias = DateUtilities.daysBetween(fecha_hoy, proximo_abono_LD);
        Log.v("obt_int_morat1", ".\n\nCuadratura. Diferencia en dias: " + diferencia_en_dias + "\n\nfecha_hoy: " + fecha_hoy.toString() + "\n\nProximo abono: " + proximo_abono_LD + "\n\n.");
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            saldo = saldo_plus;
            morosidad = "D";
            interes_mora_parcial = "0";
        } else {//Significa que esta atrazado!!!

            //saldo = String.valueOf(Integer.parseInt(saldo_plus) + (diferencia_en_dias * ((Integer.parseInt(interes_mora))/100) * Integer.parseInt(saldo_plus)));//No se suman intereses sobre los intereses moratorios, pero si sobre el interes acordado del credito!!!
            Log.v("obt_int_morat_late1", ".\n\nCuadratura. Diferencia en dias: " + diferencia_en_dias + "\n\ninteres_mora: " + interes_mora + "\n\nSaldo_plus: " + saldo_plus + "\n\n.");
            double pre_num0 = diferencia_en_dias * (Integer.parseInt(interes_mora)) * (Integer.parseInt(saldo_plus));
            double pre_num = pre_num0 / 100;
            int pre_num_int = (int) pre_num;
            Log.v("obt_int_morat_late2", ".\n\nCuadratura. pre_num_int: " + pre_num_int + "\n\n.");
            if (pre_num_int > 0) {
                morosidad = "M";
                interes_mora_parcial = String.valueOf(pre_num_int);
            } else {
                interes_mora_parcial = "0";
            }

        }
        flag = interes_mora_parcial;
        interes_mora_total = interes_mora_parcial;
        Log.v("obt_int_morat2", ".\n\nCuadratura. intereses moratorios: " + interes_mora_parcial + "\n\n.");
        return flag;
    }

    private String obtener_saldo_al_dia (String saldo_plus, String next_pay, String intereses_de_mora) throws ParseException {
        String flag = "";
        String saldo = "";
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        Date fecha_hoy = Calendar.getInstance().getTime();
        int diferencia_en_dias = DateUtilities.daysBetween(fecha_hoy, proximo_abono_LD);
        Log.v("obt_sald_al_dia0", "Cuadratura.\n\nDiferencia en dias: " + diferencia_en_dias + "\n\nnext_pay: " + next_pay + "\n\nIntereses de mora: " + intereses_de_mora + "\n\nSaldo_plus: " + saldo_plus + "\n\n.");
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            saldo = saldo_plus;
            morosidad = "D";
        } else {//Significa que esta atrazado!!!
            double pre_saldo = diferencia_en_dias * (Integer.parseInt(interes_mora)) * Integer.parseInt(saldo_plus);
            pre_saldo = pre_saldo / 100;
            saldo = String.valueOf(Integer.parseInt(saldo_plus) + (pre_saldo) + Integer.parseInt(intereses_de_mora));//No se suman intereses sobre los intereses moratorios, pero si sobre el interes acordado del credito!!!
            Log.v("obt_saldo_al_dia1", "Cuadratura.\n\nSaldo: " + saldo + "\n\n.");
            double pre_num_pre = Integer.parseInt(interes_mora) * Integer.parseInt(saldo_plus) * diferencia_en_dias;
            pre_num_pre = pre_num_pre / 100;
            double pre_num = (pre_num_pre) + Integer.parseInt(intereses_de_mora);
            int pre_num_int = (int) pre_num;
            if (pre_num_int > 0) {
                morosidad = "M";
            }
            interes_mora_total = String.valueOf(pre_num_int);
            interes_mora_parcial = interes_mora_total;
        }
        flag = saldo;
        return flag;
    }

    /*
    private void metodo() {
        separar_fechaYhora();
        String fecha_mostrar = dia + "/" + mes + "/" + anio;
        //SimpleDateFormat formato = new SimpleDateFormat("dd/mm/yyyy");
        String fecha_hoy = "";
        String fecha_mostrar3 = "";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            fecha_hoy = LocalDate.now().toString();
            fecha_mostrar3 = LocalDate.now().plusDays(10).toString();
        }
        String[] partes = fecha_hoy.split("-");
        fecha_mostrar = fecha_hoy;//Estas fechas son HOY.
        LocalDate fecha_hoy_LD = LocalDate.now();
        fecha_hoy = partes[2] + "/" + partes[1] + "/" + partes[0];
        String fecha_prueba = "26/10/2022";//FECHA DE PRUEBA...
        String fecha_prueba_presentar = fecha_prueba;
        String[] partecitas = fecha_prueba.split("/");
        fecha_prueba = partecitas[2] + "-" + partecitas[1] + "-" + partecitas[0];
        //SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        //LocalDate nueva_fecha = null;
        LocalDate fecha_prueba_LD = LocalDate.parse(fecha_prueba);
        //nueva_fecha = localDate.plusDays(13);
        //fecha_prueba = localDate.                                                              //              menos esta         esta           (Osea, estan alreves!!!)
        String diferencia_fechas = String.valueOf(DAYS.between(fecha_prueba_LD, fecha_hoy_LD));//DAYS.between(fecha_hoy_LD, fecha_prueba_LD)) Si es positivo significa que esta atrasado.
        tv.setText("Hoy: " + fecha_hoy + "\n\n" + "Fecha de prueba: " + fecha_prueba_presentar + "\n\nDiferencia entre fechas: " + diferencia_fechas);
    }
     */

    private boolean revisar_creditos () {
        boolean flasg = false;
        String flag = "";
        lista_archivos = "";
        String cliente_file = cliente_ID + "_C_.txt";
        String archivos[] = fileList();
        for (int i = 0; i < archivos.length; i++) {
            String saldo_mas_int_tempo = "";
            Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    while (linea != null) {
                        Log.v("revisar_creditos", ".\n\nlinea:\n\n" + linea + "\n\n.");
                        String[] split = linea.split("_separador_");
                        if (split[0].equals("saldo_mas_intereses")) {
                            saldo_mas_int_tempo = split[1];
                            if (Integer.parseInt(saldo_mas_int_tempo) < 100) {
                                //Do nothing. Credito ya ha sido cancelado casi en su totalidad, por lo que se toma como cancelado al 100% y no se muestra.
                            } else {
                                lista_archivos = lista_archivos + archivos[i] + "_sep_";//Significa que es un credito activo.
                            }
                        }
                        linea = br.readLine();
                    }
                    br.close();
                    archivo.close();
                } catch (IOException e) {
                }
            }
        }

        if (lista_archivos.equals("")) {
            flag = "0";
        } else {
            String[] split = lista_archivos.split("_sep_");
            int spl_long = split.length;
            flag = String.valueOf(spl_long);
        }
        if (Integer.parseInt(flag) == 0) {
            //msg("Cliente no posee creditos activos!");
            cantidad_de_creditos = 0;
            esperar("Cliente no posee creditos activos!");
        } else if (Integer.parseInt(flag) == 1) {//Solo tiene un credito activo.
            cantidad_de_creditos = 1;
            String[] split = lista_archivos.split("_sep_");
            Log.v("revisar_creditos", ".\n\nCuadratura. Archivo correcto: " + split[0] + "\n\n.");
            archivo_prestamo = split[0];
            Log.v("revisar_creditos_F", ".\n\nCuadratura. Contenido del archivo " + archivo_prestamo + ":\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
        } else {
            flasg = true;
            cantidad_de_creditos = 2;//Significa que son 2 o mas creditos. Se debe activar el spinner.
        }
        return flasg;
    }

    /*private void sumar_disponible () {
        String ArchivoCompleto = "";
        int nuevo_monto = 0;
        try {
            String file_name = cliente_ID + "_C_.txt";
            InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                Log.v("restar_disponible", ".\n\nLinea:\n\n" + linea + "\n\n.");
                String[] split = linea.split("_separador_");
                if (split[0].equals("monto_disponible")) {
                    nuevo_monto = Integer.parseInt(monto_disponible) - (monto_abono);
                    linea = linea.replace(split[1], String.valueOf(nuevo_monto));
                }
                ArchivoCompleto = ArchivoCompleto + linea + "\n";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            borrar_archivo(file_name);
            crear_archivo(file_name);
            guardar(ArchivoCompleto, file_name);
            Log.v("restar_disponible2", ".\n\nArchivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
        } catch (IOException e) {
        }
    }*/

    
    public void consultar (View view) throws JSONException, IOException, InterruptedException {
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);

        if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
            String archivos[] = fileList();
            String archivoCompleto = "";
            String file_to_consult = "";
            if (flag_client_reciv) {
                file_to_consult = cliente_recibido + "_C_";
            } else {
                file_to_consult = et_ID.getText().toString() + "_C_";
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
                            if (split[0].equals("interes_mora")) {
                                interes_mora = split[1];
                            }
                            if (split[0].equals("nombre_cliente")) {
                                nombre_cliente = split[1];
                            }
                            if (split[0].equals("apellido1_cliente")) {
                                apellido_cliente = split[1];
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
                //No se encontro el cliente. NUNCA DEBERIA LLEGAR AQUI!!! (FILTRO PREVIO)
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
                text_listener();
            } else {
                boolean credits = revisar_creditos();
                if (credits) {
                    llenar_spinner();//Aqui se debe llamar a presentar_info_credito().
                } else {
                    presentar_info_credito("UNO");
                }
                /*
                Toast.makeText(this, "Cliente encontrado", Toast.LENGTH_SHORT).show();
                et_ID.setText("");
                et_ID.setFocusableInTouchMode(false);
                et_ID.setEnabled(false);
                et_ID.setVisibility(View.INVISIBLE);
                bt_consultar.setVisibility(View.INVISIBLE);
                tv_esperar.setText("");
                tv_esperar.setVisibility(View.INVISIBLE);
                //Aqui se llama al metodo principal.
                recibir_fondos_cliente();
                 */
            }
        } else if (tv_esperar.getText().toString().equals("Monto a pagar al dia de hoy: ")) {
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);

            tv_esperar.setText("");

            procesar_abono2();

        } else if (tv_esperar.getText().toString().equals("Prestamo a consultar:")){
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
            Log.v("Prestamo_a_consultar", ".\n\nPrestamo que se va a actualizar: " + et_ID.getText().toString() + "\n\n.");
            String[] parts_prestamo = et_ID.getText().toString().split(" ");
            int monto_a_pagar = 0;
            cantidad_cuotas_pendientes = Integer.parseInt(parts_prestamo[3]);
            morosidad = parts_prestamo[2];
            monto_cuota = obtener_monto_cuota(parts_prestamo[0]);
            presentar_monto_a_pagar(monto_a_pagar);

        } else {
            //TODO: no se sabe que hacer aqui!!!
        }
    }

    
    private void procesar_abono2 () {

        String file_name = archivo_prestamo;
        String contenido = "";
        String fecha_next_abono = "";
        int factor_semanas = 0;
        int monto_ingresado = 0;
        //actualizar_caja(monto_ingresado);
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split("_separador_");

                if (split[0].equals("cuadratura")) {
                    cuadratura = split[1];
                } else if (split[0].equals("proximo_abono")) {
                    proximo_abono = split[1];
                } else if (split[0].equals("plazo")) {
                    plazo = split[1];
                } else if (split[0].equals("saldo_mas_intereses")) {
                    saldo_mas_intereses = Integer.parseInt(split[1]);
                } else if (split[0].equals("cuotas")) {
                    cuotas = split[1];
                } else if (split[0].equals("morosidad")) {
                    morosidad = split[1];
                //} else if (split[0].equals("intereses_moratorios")) {
                //    interes_mora_total = split[1];
                } else {
                }
                contenido = contenido + linea + "\n";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            Log.v("prosesar_abono2", ".\n\nArchivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\nPlazo: " + plazo + "\n\n.");
            String[] piezas = plazo.split("_");
            if (piezas[1].equals("quincenas")) {
                factor_semanas = 2;
            } else if (piezas[1].equals("semanas")) {
                factor_semanas = 1;
            } else {
                factor_semanas = -1;
                //ERROR
            }

            Log.v("antes_de_cuadra_chang", ".\n\nCuadratura. Archivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
            cuadratura = obtener_cuadratura(cuadratura, fecha_next_abono, factor_semanas, monto_ingresado);//Aqui se obtiene la verdadera y final morosidad.
            Log.v("despues_de_cuadra_chang", ".\n\nCuadratura. Archivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
            //cuotas = obtener_cuotas_nuevas(cuadratura);
            saldo_mas_intereses = Integer.parseInt(obtener_saldo_plus(cuadratura));

            actualizar_archivo_credito();

        } catch (IOException e) {
        }

    }

    
    private void actualizar_archivo_credito() {
        //archivo_prestamo

        String contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(archivo_prestamo));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split("_separador_");

                if (split[0].equals("morosidad")) {
                    linea = linea.replace(split[1], morosidad);
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("intereses_moratorios")) {
                    linea = linea.replace(split[1], interes_mora_total);
                    contenido = contenido + linea + "\n";
                } else {
                    contenido = contenido + linea + "\n";
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            Log.v("actualizar_archiv_cred1", ".\n\nCuadratura. Archivo: " + archivo_prestamo + "\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
            borrar_archivo(archivo_prestamo);
            crear_archivo(archivo_prestamo);
            guardar(contenido, archivo_prestamo);
            actualizar_archivo_cliente();

        } catch (IOException e) {
        }

    }

    
    private void presentar_cuadratura () throws ParseException {
        //TODO: Imprimir la informacion que esta en la cuadratura

        mensaje_imprimir = "\n\nFecha: " + dia + "/" + mes + "/" + anio + "\n\n\n******** Prestamos El Chino ********\n\nCliente: " +
                nombre_cliente + " " + apellido_cliente +"\n\n\n******************************\n\n" + mensaje_imprimir_pre + "\n******************************\n\n" +
                "Estimado cliente, no olvide\nrevisar su tiquete antes de\nque se retire el cobrador.\n\n\n";

        bt_consultar.setEnabled(false);
        bt_consultar.setVisibility(View.INVISIBLE);
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        et_ID.setText("");
        et_ID.setVisibility(View.INVISIBLE);
        et_ID.setEnabled(false);
        sp_plazos.setEnabled(false);
        sp_plazos.setVisibility(View.INVISIBLE);
        String[] split_1 = cuadratura.split("__");
        int largo_split = split_1.length;
        HashMap<Integer, Button> botones = new HashMap<Integer, Button>();
        botones.put(0, sequi1);
        botones.put(1, sequi2);
        botones.put(2, sequi3);
        botones.put(3, sequi4);
        botones.put(4, sequi5);
        botones.put(5, sequi6);
        botones.put(6, sequi7);
        botones.put(7, sequi8);
        botones.put(8, sequi9);
        TreeMap<Integer, Button> botones_tree = getTreeMapBotones(botones);
        //Map<Integer, Button> botones_treeMap = getTreeMap(botones);
        Date hoy_LD = Calendar.getInstance().getTime();
        for (int i = 0; i < largo_split; i++) {
            String[] split = split_1[i].split("_");//TODO: Si estan en cero o al dia, se debe pintar verde el boton, si es hoy el dia, pintar amarillo, si esta atrazado, pintar verde.
            String fecha_cuadrito = split[3];
            String[] split_fec = fecha_cuadrito.split("/");
            fecha_cuadrito = split_fec[2] + "-" + split_fec[1] + "-" + split_fec[0];
            Date fecha_cuadrito_LD = DateUtilities.stringToDate(fecha_cuadrito);
            String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_cuadrito_LD));
            String info_boton = split[3] + "\n" + split[0] + " " + split[1] + "\n" + split[2];
            if (Integer.parseInt(diferencia_fechas) > 0) {//Significa que esta atrasado.
                if (Integer.parseInt(split[2]) == 0) {
                    botones_tree.get(i).setTextColor(0XFF0D7302);//VERDE OBSCURO
                    botones_tree.get(i).setBackgroundColor(0xFF97FD8C);//VERDE CLARO
                } else {
                    botones_tree.get(i).setTextColor(0XFFDE0037);//ROJO OBSCURO
                    botones_tree.get(i).setBackgroundColor(0xFFFDAAC5);//ROJO CLARO
                }
            } else if (Integer.parseInt(diferencia_fechas) < 0 ) {//Es una fecha posterior a hoy
                botones_tree.get(i).setTextColor(0XFF0D7302);//VERDE OBSCURO
                botones_tree.get(i).setBackgroundColor(0xFF97FD8C);//VERDE CLARO
            } else if (Integer.parseInt(diferencia_fechas) == 0 ) {//Es hoy
                if (Integer.parseInt(split[2]) == 0) {
                    botones_tree.get(i).setTextColor(0XFF0D7302);//VERDE OBSCURO
                    botones_tree.get(i).setBackgroundColor(0xFF97FD8C);//VERDE CLARO
                } else {
                    botones_tree.get(i).setTextColor(0XFF949900);//AMARILLO OBSCURO
                    botones_tree.get(i).setBackgroundColor(0xFFF9FF61);//AMARILLO CLARO
                }
            }

            botones_tree.get(i).setVisibility(View.VISIBLE);
            botones_tree.get(i).setText(info_boton);
            botones_tree.get(i).setClickable(false);//TODO: Aqui se debe hacer un algoritmo que al tener monto pendiente, se pueda cancelar solo esa cuota.
            bt_imprimir.setVisibility(View.VISIBLE);
        }
    }

    private TreeMap<Integer, Button> getTreeMapBotones(HashMap<Integer, Button> botones) {
        TreeMap<Integer, Button> treeMap = new TreeMap<Integer, Button>();
        for (int key : botones.keySet()) {
            treeMap.put(key, botones.get(key));
        }

        return treeMap;
    }

    
    private void actualizar_archivo_cliente () {
        String[] spliti = archivo_prestamo.split("_");
        String archivo_cliente = spliti[0] + "_C_.txt";
        String contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(archivo_cliente));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split("_separador_");

                if (split[0].equals("puntuacion")) {
                    linea = linea.replace(split[1], String.valueOf(puntuacion_cliente));
                    contenido = contenido + linea + "\n";
                //} else if (split[0].equals("")) {

                } else {
                    contenido = contenido + linea + "\n";
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            Log.v("actualiz_archiv_client1", ".\n\nCuadratura. Archivo: " + archivo_cliente + "\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_cliente) + "\n\n.");
            borrar_archivo(archivo_cliente);
            crear_archivo(archivo_cliente);
            guardar(contenido, archivo_cliente);

            presentar_cuadratura();

        } catch (IOException e) {
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    private String obtener_saldo_plus (String cuadratura) {
        String flag = "";

        String[] split = cuadratura.split("__");
        int largo_split = split.length;
        int saldo_plus_plus = 0;
        for (int i = 0; i < largo_split; i++) {

            String[] split_1 = split[i].split("_");
            Log.v("Obtener_saldo_plus", ".\n\nCuadratura: " + cuadratura + "\n\n.");
            if (Integer.parseInt(split_1[2]) > 0) {//Significa que tiene esta cuota pendiente.
                saldo_plus_plus = saldo_plus_plus + Integer.parseInt(split_1[2]);
            }
        }

        flag = String.valueOf(saldo_plus_plus);

        return flag;
    }

    
    private String obtener_cuadratura (String cuadratura, String fecha_next_abono, int factor_semanas, int monto_ingresado) {

        String flag = "";
        int monto_temporal = monto_ingresado - Integer.parseInt(interes_mora_total);
        if (monto_temporal < 0) {//No alcanzo siquiera para pagar los intereses. Debe retornar TODO: Revisar
            morosidad = "M";
            flag = cuadratura;
            return flag;


        } else if (monto_temporal == 0) {//Aqui paga el monto completo, solo de los intereses moratorios, no abona nada a los abonos ordinarios. Debe retornar

            flag = cuadratura;
            morosidad = "D";
            //monto_disponible = "0";
            interes_mora_total = "0";
            return flag;

        } else {
            //Do nothing. Never come here!!!
            Log.v("Obtener_cuadratura_else", ".\n\nERROR EN DATO DE ARCHIVO\n\nContenido del archivo: \n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
        }

        //flag = cuadratura;
        return flag;

    }

/*    
    private String obtener_proximo_abono (String fecha_next_abono) {
        String flag = "";
        int factor_semanas = 0;
        String[] piezas = plazo.split(" ");
        if (piezas[1].equals("quincenas")) {
            factor_semanas = 2;
        } else if (piezas[1].equals("semanas")) {
            factor_semanas = 1;
        } else {
            factor_semanas = -1;
            //flag = "ERROR";
        }

        LocalDate fecha_next_abono_LD = LocalDate.parse(fecha_next_abono);
        String fecha_mostrar2 = "";
        if (morosidad.equals("D")) {
            fecha_mostrar2 = fecha_next_abono_LD.plusWeeks(factor_semanas).toString();
        } else if (morosidad.equals("M")) {

        } else {

        }

        String[] partes = fecha_mostrar2.split("-");
        fecha_mostrar2 = partes[2] + "/" + partes[1] + "/" + partes[0];
        flag = fecha_mostrar2;
        return flag;
    }*/

    
    private void presentar_monto_a_pagar (int monto_a_pagar) throws JSONException, IOException, InterruptedException {

        et_ID.setEnabled(true);
        et_ID.setText(String.valueOf("0"));
        et_ID.setFocusableInTouchMode(true);
        et_ID.setVisibility(View.VISIBLE);
        //bt_consultar.setVisibility(View.VISIBLE);
        //bt_consultar.setText("REALIZAR PAGO");
        tv_esperar.setText("Monto a pagar al dia de hoy: ");
        tv_esperar.setVisibility(View.VISIBLE);
        et_ID.requestFocus();
        consultar(null);
    }

    private Integer obtener_monto_cuota (String s) {
        int flag = 0;
        String[] split = s.split("#");
        Log.v("obt_monto_cuota", ".\n\nString: "+ s + "\n\nSplit[0]: " + split[0] + "\n\nSplit[1]: " + split[1] + "\n\n.");
        String archivos[] = fileList();
        s = split[1];
        for (int i = 0; i < archivos.length; i++) {
            Pattern pattern = Pattern.compile(cliente_ID + "_P_" + s + "_P_", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                try {
                    String numero_de_credito = "";
                    String file_name = archivos[i];
                    String[] split_indice = file_name.split("_P_");
                    numero_de_credito = split_indice[1];
                    if (numero_de_credito.equals(split[1])) {
                        archivo_prestamo = file_name;
                        InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        while (linea != null) {
                            String[] splitre = linea.split("_separador_");
                            if (split[0].equals("monto_cuota")) {
                                monto_cuota = Integer.parseInt(splitre[1]);
                                flag = monto_cuota;
                                Log.v("obt_mont_cuota2", ".\n\nLinea:\n\n" + linea + "\n\n.");
                            }
                            linea = br.readLine();
                        }
                        br.close();
                        archivo.close();
                    }
                    //Log.v("restar_disponible2", ".\n\nArchivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
                } catch (IOException e) {
                }
            }
        }
        return flag;
    }

    private void spinner_listener () {
        sp_plazos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = sp_plazos.getSelectedItem().toString();
                        if (seleccion.equals("Escoja el credito...")) {
                            bt_consultar.setClickable(false);
                            bt_consultar.setEnabled(false);
                            //Do nothing!
                        }else {
                            credito_aplicar = sp_plazos.getSelectedItem().toString();
                            sp_plazos.setEnabled(false);
                            sp_plazos.setVisibility(View.INVISIBLE);
                            bt_consultar.setEnabled(true);
                            bt_consultar.setClickable(true);
                            bt_consultar.setVisibility(View.VISIBLE);
                            tv_esperar.setEnabled(true);
                            tv_esperar.setVisibility(View.VISIBLE);
                            bt_consultar.setFocusableInTouchMode(true);
                            tv_esperar.setText("");
                            tv_esperar.setFocusableInTouchMode(true);
                            tv_esperar.requestFocus();
                            try {
                                presentar_info_credito(sp_plazos.getSelectedItem().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    
    private void presentar_info_credito (String s) throws JSONException, IOException, InterruptedException {

        if (s.equals("UNO")) {
            String archivos[] = fileList();
            //String puntuacion_cliente = "";
            //String archivoCompleto = "";
            String file_to_consult = "";

            if (flag_client_reciv) {
                file_to_consult = archivo_prestamo;
            } else {
                file_to_consult = archivo_prestamo;
            }
            for (int i = 0; i < archivos.length; i++) {
                Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(archivos[i]);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    //TODO: Abrir archivo y leerlo.
                    try {
                        String fecha_next_abono = "";
                        String intereses_mor = "";
                        String saldo_mas_intereses_s = "";
                        String plazoz = "";
                        String numero_de_credito = "";
                        String cuotas_morosas = "";
                        String valor_presentar_s = "";
                        String cuadratura_pre = "";
                        int factor_semanas = 0;
                        //String indice_file = "";
                        String file_name = archivos[i];
                        String[] split_indice = file_name.split("_P_");
                        numero_de_credito = split_indice[1];
                        InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        while (linea != null) {
                            Log.v("Presentar_info_cli_ONE", ".\n\nlinea:\n\n" + linea + "\n\n.");
                            String[] split = linea.split("_separador_");
                            if (split[0].equals("proximo_abono")) {
                                fecha_next_abono = split[1];
                            }
                            if (split[0].equals("plazo")) {
                                plazoz = split[1];
                            }
                            if (split[0].equals("saldo_mas_intereses")) {
                                saldo_mas_intereses_s = split[1];
                            }
                            if (split[0].equals("cuotas")) {
                                cuotas_morosas = split[1];
                            }
                            if (split[0].equals("cuadratura")) {
                                cuadratura_pre = split[1];
                            }
                            if (split[0].equals("intereses_moratorios")) {
                                intereses_mor = split[1];
                            }
                            linea = br.readLine();
                        }

                        br.close();
                        archivo.close();

                        String[] piezas = plazoz.split("_");
                        if (piezas[1].equals("quincenas")) {
                            factor_semanas = 2;
                        } else if (piezas[1].equals("semanas")) {
                            factor_semanas = 1;
                        } else {
                            factor_semanas = -1;
                            //ERROR
                        }

                        String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                        String intereses_moritas = obtener_intereses_moratorios(saldo_plus_s, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                        interes_mora_total = intereses_moritas;
                        cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0);
                        saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor);
                        cuotas_morosas = obtener_cuotas_morosas(cuotas_morosas, plazoz, fecha_next_abono);

                        valor_presentar_s = "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas;
                        presentar_et_esperar = valor_presentar_s;
                        et_ID.setText("");
                        et_ID.setFocusableInTouchMode(false);
                        et_ID.setClickable(false);
                        et_ID.setEnabled(true);
                        et_ID.setVisibility(View.VISIBLE);
                        et_ID.setText(valor_presentar_s);
                        et_ID.setEnabled(false);
                        tv_esperar.setEnabled(true);
                        tv_esperar.setText("");
                        tv_esperar.setVisibility(View.VISIBLE);
                        tv_esperar.setText("Prestamo a consultar:");
                        consultar(null);
                    } catch (IOException e) {
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    break;
                } else {
                    //Continue with the execution.
                }
            }
        } else {
            String valor_presentar_s = s;
            String[] splitte = valor_presentar_s.split(" ");
            String num_credit = splitte[0];
            num_credit = num_credit.replace("#", "");
            String archivos[] = fileList();
            String file_to_consult = "";
            Log.v("valor_a_presentar", ".\n\nnum_credit: " + num_credit + "\n\n.");
            if (flag_client_reciv) {
                file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            } else {
                file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            }
            for (int i = 0; i < archivos.length; i++) {
                Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(archivos[i]);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    //TODO: Abrir archivo y leerlo.
                    try {
                        String fecha_next_abono = "";
                        String intereses_mor = "";
                        String saldo_mas_intereses_s = "";
                        String plazoz = "";
                        String numero_de_credito = "";
                        String cuotas_morosas = "";
                        String cuadratura_pre = "";
                        int factor_semanas = 0;
                        String file_name = archivos[i];
                        String[] split_indice = file_name.split("_P_");
                        numero_de_credito = split_indice[1];
                        InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        while (linea != null) {
                            Log.v("Presentar_info_cli_MORE", ".\n\nlinea:\n\n" + linea + "\n\n.");
                            String[] split = linea.split("_separador_");
                            if (split[0].equals("proximo_abono")) {
                                fecha_next_abono = split[1];
                            }
                            if (split[0].equals("plazo")) {
                                plazoz = split[1];
                            }
                            if (split[0].equals("saldo_mas_intereses")) {
                                saldo_mas_intereses_s = split[1];
                            }
                            if (split[0].equals("cuotas")) {
                                cuotas_morosas = split[1];
                            }
                            if (split[0].equals("cuadratura")) {
                                cuadratura_pre = split[1];
                            }
                            if (split[0].equals("intereses_moratorios")) {
                                intereses_mor = split[1];
                            }
                            linea = br.readLine();
                        }
                        br.close();
                        archivo.close();

                        //TODO: calcular intereses moratorios aqui!!!

                        String[] piezas = plazoz.split("_");
                        if (piezas[1].equals("quincenas")) {
                            factor_semanas = 2;
                        } else if (piezas[1].equals("semanas")) {
                            factor_semanas = 1;
                        } else {
                            factor_semanas = -1;
                            //ERROR
                        }

                        String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                        String intereses_moritas = obtener_intereses_moratorios(saldo_plus_s, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                        interes_mora_total = intereses_moritas;
                        cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0);
                        saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor);
                        cuotas_morosas = obtener_cuotas_morosas(cuotas_morosas, plazoz, fecha_next_abono);


                        valor_presentar_s = "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas;
                        presentar_et_esperar = valor_presentar_s;
                        et_ID.setText("");
                        et_ID.setFocusableInTouchMode(false);
                        et_ID.setClickable(false);
                        et_ID.setEnabled(true);
                        et_ID.setVisibility(View.VISIBLE);
                        et_ID.setText(valor_presentar_s);
                        et_ID.setEnabled(false);
                        tv_esperar.setEnabled(true);
                        tv_esperar.setText("");
                        tv_esperar.setVisibility(View.VISIBLE);
                        tv_esperar.setText("Prestamo a consultar:");
                        consultar(null);
                        //bt_consultar.setEnabled(true);
                        //bt_consultar.setVisibility(View.VISIBLE);
                        //bt_consultar.setClickable(true);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

/*    private String calcular_cuota () {
        String flag = "";
        int interes = 0;
        int cuotas = 0;
        String[] split = credito_aplicar.split(" ");
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
        double monto_total = monto_abono + ((monto_abono * interes) / 100);
        double cuota = monto_total / cuotas;
        int flag_int = (int) cuota;
        Log.v("monto_total", ".\n\nMonto total: " + monto_total + "\n\nMonto del credito: " + monto_abono + "\n\n.");
        flag = String.valueOf(flag_int);
        Log.v("flag",".\n\nFlag: " + flag + "\n\n.");
        return flag;
    }

    private String calcular_saldo () {
        String flag = "";
        int interes = 0;
        String[] split = credito_aplicar.split(" ");
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
        double monto_total = monto_abono + ((monto_abono * interes) / 100);
        int flag_int = (int) monto_total;
        flag = String.valueOf(flag_int);
        return flag;
    }

    private String obtener_tasa () {
        String flag = "";
        int interes = 0;
        String[] split = credito_aplicar.split(" ");
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
        String[] split = credito_aplicar.split(" ");
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
            end_id = spl_long + 1;
            flag = String.valueOf(end_id);
        }
        flag = cliente_ID + "_P_" + String.valueOf(flag) + "_P_";
        return flag;
    }

    private void recibir_fondos_cliente () {
        //Algoritmo principal

        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("Digite el monto del abono");
        et_ID.setEnabled(true);
        et_ID.setVisibility(View.VISIBLE);
        et_ID.requestFocus();
        et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_ID.setClickable(true);
        et_ID.setText("");
        et_ID.setFocusableInTouchMode(true);
        et_ID.requestFocus();
        //et_ID.setText("0");
        bt_consultar.setText("CONFIRMAR");
        bt_consultar.setVisibility(View.VISIBLE);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        text_listener();
    }*/

    private void text_listener () {

        //Implementacion de un text listener
        et_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tv_esperar.getText().toString().equals("Digite el monto del abono")) {

                    bt_consultar.setClickable(false);
                    bt_consultar.setEnabled(false);
                    et_ID.setEnabled(true);
                    et_ID.setVisibility(View.VISIBLE);
                    et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();

                    if (String.valueOf(s).equals("")) {
                        //Do nothing.
                    } else {
                        bt_consultar.setClickable(true);
                        bt_consultar.setEnabled(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
                    et_ID.setEnabled(true);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();
                    String archivos[] = fileList();
                    for (int i = 0; i < archivos.length; i++) {
                        Pattern pattern = Pattern.compile(et_ID.getText().toString(), Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(archivos[i]);
                        Log.v("text_listener_identifi", ".\n\narchivos[" + i + "]: " + archivos[i] + "\n\n.");
                        boolean matchFound = matcher.find();
                        if (matchFound) {
                            if (s.length() >= 9) {
                                bt_consultar.setEnabled(true);
                                bt_consultar.setClickable(true);
                            }
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

    private void esperar (String s) {
        ocultar_todito();
        //Toast.makeText(this, s, Toast.LENGTH_LONG).show();
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
        bt_imprimir.setVisibility(View.INVISIBLE);
        boton_atras();
    }

    private void boton_atras() {

        if (activity_devolver.equals("MenuPrincipal")) {
            Intent activity_volver = new Intent(this, MenuPrincipal.class);
            activity_volver.putExtra("mensaje", "");
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else if (activity_devolver.equals("Estado_cliente")) {
            Intent activity_volver = new Intent(this, Estado_clienteActivity.class);
            activity_volver.putExtra("mensaje", "");
            activity_volver.putExtra("cliente_ID", cliente_Id_volver);
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else {

        }


    }

    private void mostrar_todito() {
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.VISIBLE);
    }

    private void ocultar_todito () {
        Log.v("ocultar_todito", "Se hace todo invisible");
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("conectando, por favor espere...");
        bt_consultar.setVisibility(View.INVISIBLE);
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

/*    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void ocultar_teclado(){
        View view = this.getCurrentFocus();
        InputMethodManager imn = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imn.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }*/

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
        Log.v("json_string_debug", ".\n\njson_string: " + "\n\n" + json_string + "\n\n.");
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
                                    esperar("\"Credito generado y registrado correctamente en el servidor.\"");
                                } else {
                                    esperar("Error al subir informacion del credito al servidor. Conectese a internet!!!");
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

    public void printIt (View view) {

            BluetoothSocket socket;
            socket = null;
            byte[] data = mensaje_imprimir.getBytes();

            //Get BluetoothAdapter
            BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
            if (btAdapter == null) {
                Toast.makeText(getBaseContext(), "BlueTooth abierto!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Get sunmi InnerPrinter BluetoothDevice
            String impresora = get_impresora();
            BluetoothDevice device = BluetoothUtil.getDevice(btAdapter, impresora);
            if (device == null) {
                Toast.makeText(getBaseContext(), "Asegurese de tener conectada una impresora!!!", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                socket = BluetoothUtil.getSocket(device);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert socket != null;
                BluetoothUtil.sendData(data, socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

    private String get_impresora() {
        String impresora = "00:11:22:33:44:55";
        return impresora;
    }
}
