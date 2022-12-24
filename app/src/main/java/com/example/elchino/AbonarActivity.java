package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.elchino.Util.*;
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
import java.text.ParseException;
//import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbonarActivity extends AppCompatActivity {

    private Integer monto_abono = 0;
    private Integer monto_anterior = 0;
    private EditText et_mensaje;
    private boolean flag_perdon = false;
    private Integer monto_perdonado = 0;
    private boolean flag_solicitud = false;
    private String mensaje_solicitud = "";
    private String monto_prestado_final = "";
    private Integer total_cuotas = 0;
    private TextView tv_indicador;
    private Integer cont = 0;
    private TextView tv_debug;
    private boolean flag_fecha = false;
    private String fecha_abono = "";
    private String mensaje_imprimir = "";
    private Integer intereses_monroe = 0;
    private Integer cambio = 0;
    private String interes_mora_parcial;
    private Integer monto_cuota = 0;
    private String fecha_pago = "";//Fecha que debe pagar la proxima cuota.
    private Integer saldo_mas_intereses = 0;
    private Integer tasa = 0;//Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
    private String ID_credito = "";
    private String credito_aplicar = "";
    private String morosidad = "D";
    private Integer cantidad_cuotas_pendientes = 0;
    private String archivo_prestamo = "";
    private String plazo = "";
    private String cuotas = "";
    private EditText et_ID;
    private TextView tv_esperar;
    private Map<String, Integer> meses = new HashMap<String, Integer>();
    private String dia;
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
    private String sheet_abonos = "abonos";
    private String onlines = "onlines.txt";
    private Button bt_consultar;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private String monto_disponible = "0";
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
    private TextView tv_caja;
    private String nombre_cliente = "";
    private String apellido_cliente = "";
    private Button bt_cambiar_fecha;
    private Integer mes_selected = 0;
    private Integer anio_selected = 0;
    private Integer fecha_selected = 0;
    private Date hoy_LD;
    private String activity_volver;
    private Spinner sp_plazos;
    //private Button bt_debug;
    private String fecha_hoy_string;
    private Button bt_perdon;
    private String solicitud_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_abonar);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        if (mensaje_recibido.equals("")) {
            //Do nothing.
        } else {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        tv_indicador = (TextView) findViewById(R.id.tv_indicador);
        tv_indicador.setText("");
        et_mensaje = (EditText) findViewById(R.id.et_mensaje);
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        abono_cero = getIntent().getStringExtra( "abono_cero");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        //tv_debug = (TextView) findViewById(R.id.tv_debug);
        et_ID = (EditText) findViewById(R.id.et_ID);
        bt_consultar = (Button) findViewById(R.id.bt_consultar_ab);
        bt_perdon = (Button) findViewById(R.id.bt_perdon);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        activity_volver = getIntent().getStringExtra("activity_devolver");
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("ABONO A CREDITO");
        sp_plazos = (Spinner) findViewById(R.id.sp_plazos);
        sp_plazos.setVisibility(View.INVISIBLE);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");
        bt_cambiar_fecha = (Button) findViewById(R.id.bt_cambiar_fecha);
        bt_cambiar_fecha.setVisibility(View.INVISIBLE);
        mostrar_caja();
        hoy_LD = Calendar.getInstance().getTime();
        Log.v("OnCreate0", "Abonar.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        fecha_hoy_string = DateUtilities.dateToString(hoy_LD);
        Log.v("OnCreate1", "Abonar.\n\nFecha hoy: " + fecha_hoy_string + "\n\n.");
        Log.v("OnCreate2", "Abonar.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        separar_fechaYhora();
        try {
            corregir_archivos();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            hoy_LD = DateUtilities.stringToDate(fecha_hoy_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] split_fecha_hoy_string = fecha_hoy_string.split("-");
        fecha_hoy_string = split_fecha_hoy_string[2] + "/" + split_fecha_hoy_string[1] + "/" + split_fecha_hoy_string[0];
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
                Log.v("corregir_archivos0", "Abonar.\n\nfecha_file: " + fecha_file + "\nfecha_hoy: " + hoy_fecha + "\n\n");
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
            crear_archivo("cierre.txt");
            borrar_archivo("cierre.txt");
            crear_archivo("cierre.txt");
            agregar_linea_archivo("fecha " + fecha, "cierre.txt");
        }
        if (flag_borrar) {
            borrar_archivo("cierre.txt");
            crear_archivo("cierre.txt");
            agregar_linea_archivo("fecha " + fecha, "cierre.txt");
        }

        /////////////////////////////////////////////////////////////////////////////////////

    }

    public void imprimir_archivos_todos (View view) {
        String archivos[] = fileList();
        et_ID.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.INVISIBLE);
        if (cont < archivos.length) {
            String file_name = archivos[cont];
            tv_saludo.setText(file_name);
            tv_esperar.setText("");
            tv_debug.setText(imprimir_archivo(file_name));
            et_ID.setVisibility(View.INVISIBLE);
            bt_consultar.setVisibility(View.INVISIBLE);
            cont++;
        }
    }

    private void mostrar_caja () {
        tv_caja.setText(imprimir_archivo(caja));
    }

    private String obtener_cuotas_morosas (String cuadratura, String fecha_next_abono) throws ParseException {
        String[] split1 = cuadratura.split("__");
        String fecha_next_abono_bkUp = fecha_hoy_string;
        Log.v("obt_cuot_moro0", "Abonar.\n\ncuadratura: " + cuadratura + "\n\nfecha_next_abono_bkUp: " + fecha_next_abono_bkUp + "\n\n.");
        int length_split1 = split1.length;
        int cont = 0;
        for (int i = 0; i < length_split1; i++) {
            fecha_next_abono_bkUp = fecha_hoy_string;
            String[] split = split1[i].split("_");
            Log.v("obt_cuot_moro1", "Abonar.\n\nsplit1[" + i + "]: " + split1[i] + "\n\n.");
            String fecha_cuadra_S = split[3];
            String[] split_fecha_cuadra_S = fecha_cuadra_S.split("/");
            fecha_cuadra_S = split_fecha_cuadra_S[2] + "-" + split_fecha_cuadra_S[1] + "-" + split_fecha_cuadra_S[0];
            int monto = Integer.parseInt(split[2]);
            Log.v("obt_cuout_moro2", "Abonar.\n\nMonto: " + monto + "\n\n.");
            if (monto > 100) {
                Date fecha_cuadra = DateUtilities.stringToDate(fecha_cuadra_S);
                Log.v("obt_cuout_moro3", "Abonar.\n\nfecha_cuadra: " + fecha_cuadra + "\n\n.");
                String[] split_fecha_next_abono_bkUp = fecha_next_abono_bkUp.split("/");
                Log.v("obt_cuout_moro4", "Abonar.\n\nfecha_next_abono_pre: " + fecha_next_abono_bkUp + "\n\n.");
                int length_split = split_fecha_next_abono_bkUp.length;
                Log.v("obt_cuout_moro5", "Abonar.\n\nlength_split: " + length_split + "\n\n.");
                fecha_next_abono_bkUp = split_fecha_next_abono_bkUp[2] + "-" + split_fecha_next_abono_bkUp[1] + "-" + split_fecha_next_abono_bkUp[0];
                Log.v("obt_cuout_moro6", "Abonar.\n\nfecha_next_abono_post: " + fecha_next_abono_bkUp + "\n\n.");
                Date fecha_next_abono_bkUp_D = DateUtilities.stringToDate(fecha_next_abono_bkUp);
                Log.v("obt_cuout_moro7", "Abonar.\n\nfecha_next_abono_D: " + fecha_next_abono_bkUp_D + "\n\n.");
                int dias_atrasados = DateUtilities.daysBetween(fecha_next_abono_bkUp_D, fecha_cuadra);//Positivo indica morosidad
                Log.v("obt_cuout_moro8", "Abonar.\n\ndias_atrasados: " + dias_atrasados + "\n\n.");
                if (dias_atrasados > 0) {
                    morosidad = "M";
                    cont++;
                }
            }
        }
        int cantidad_de_cuotas_pendientes = cont;
        int c_d_c_p = (int) cantidad_de_cuotas_pendientes;
        puntuacion_cliente = String.valueOf(Integer.parseInt(puntuacion_cliente) - c_d_c_p);
        Log.v("obt_cuout_moro9", "Abonar.\n\nCantidad de cuotas pendientes: " + c_d_c_p + "\n\nCantidad de cuotas pendientes: " +
                cantidad_de_cuotas_pendientes + "\n\n.");
        return String.valueOf(c_d_c_p);
    }

    private void llenar_spinner () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        String creditos = "Escoja el credito...___";
        String archivos[] = fileList();
        Log.v("llenando_spinner0", ".\n\nCantidad de archivos: " + archivos.length + "\n\n.");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("llenando_spinner1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            //Do nothing.
        } else {
            for (int i = 0; i < archivos.length; i++) {
                Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(archivos[i]);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    Log.v("llenando_spinner2", ".\n\nFile: " + archivos[i] + "\n\n.");
                    try {
                        String fecha_next_abono = "";
                        String intereses_mora = "";
                        String saldo_mas_intereses_s = "";
                        String plazoz = "";
                        String numero_de_credito = "";
                        String morosidad_s = "";
                        String cuadratura_pre = "";
                        String cuadratura_bkup = "";
                        Date fecha_credito = new Date();
                        String monto_prestado = "";
                        String cuotas_morosas = "";
                        int factor_semanas = 0;
                        String file_name = archivos[i];
                        String[] split_indice = file_name.split("_P_");
                        numero_de_credito = split_indice[1];
                        InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        while (linea != null) {
                            String[] split = linea.split("_separador_");
                            if (split[0].equals("proximo_abono")) {
                                fecha_next_abono = split[1];
                                Log.v("llenar_spinner2.5", "Abonar.\n\nfecha_next_abono: " + fecha_next_abono + "\n\n.");
                            }
                            if (split[0].equals("plazo")) {
                                plazoz = split[1];
                            }
                            if (split[0].equals("monto_credito")) {
                                monto_prestado = split[1];
                            }
                            if (split[0].equals("saldo_mas_intereses")) {
                                saldo_mas_intereses_s = split[1];
                            }
                            if (split[0].equals("cuotas")) {
                                cuotas_morosas = split[1];
                            }
                            if (split[0].equals("morosidad")) {
                                morosidad_s = split[1];
                            }
                            if (split[0].equals("cuadratura")) {
                                cuadratura_pre = split[1];
                                cuadratura_bkup = cuadratura_pre;
                            }
                            if (split[0].equals("intereses_moratorios")) {
                                intereses_mora = split[1];
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
                        } else if (piezas[1].equals("meses")) {
                            factor_semanas = 4;
                        } else {
                            factor_semanas = -1;
                            //ERROR
                        }
                        String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                        Log.v("llenando_spinner3", "Abonar.\n\nsaldo_plus: " + saldo_plus_s + "\n\nsaldo_mas_intereses: " +
                                saldo_mas_intereses_s + "\n\nIntereses_moratorios: " + intereses_mora + "\n\n." + "\n\n.");
                        String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                        Log.v("llenando_spinner4", "interes_moritas: " + intereses_moritas + "\n\n.");
                        interes_mora_total = intereses_moritas;
                        Log.v("llenando_spinner5", "interes_mora_total: " + interes_mora_total + "\n\n.");
                        interes_mora_parcial = interes_mora_total;
                        Log.v("llenando_spinner6", "Abonar.\n\ninteres_mora_parcial: " + interes_mora_parcial + "\n\n.");
                        cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0, fecha_credito, "n");
                        Log.v("llenando_spinner7", "Abonar.\n\ncuadratura_pre:\n\n" + cuadratura_pre + "\n\n.");
                        saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mora, monto_prestado);
                        Log.v("llenando_spinner8", "Abonar.\n\nsaldo_mas_intereses: " + saldo_mas_intereses_s + "\n\n.");
                        cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup, fecha_next_abono);
                        Log.v("llenando_spinner9", "Abonar.\n\ncuotas_morosas: " + cuotas_morosas + "\n\n.");
                        Log.v("llenando_spinner10", "Abonar.\n\nMorosidad_s: " + morosidad_s + "\n\nMorosidad: " + morosidad + "\n\n.");
                        double saldo_mas_intereses_D = Double.parseDouble(saldo_mas_intereses_s);
                        int saldo_mas_intereses_I = (int) saldo_mas_intereses_D;
                        saldo_mas_intereses_s = String.valueOf(saldo_mas_intereses_I);
                        if (Integer.parseInt(saldo_mas_intereses_s) > 100) {
                            creditos = creditos + "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas + "___";
                        } else {
                            //Do nothing.
                        }
                        //Log.v("restar_disponible2", ".\n\nArchivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
                    } catch (IOException e) {
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
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

    private String obtener_saldo_al_dia (String saldo_plus, String next_pay, String intereses_de_mora, String monto_prestado) throws ParseException {
        String flag = "";
        String saldo = "";
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        int diferencia_en_dias = DateUtilities.daysBetween(hoy_LD, proximo_abono_LD);
        Log.v("obt_sald_al_dia0", "Abonar.\n\nDiferencia en dias: " + diferencia_en_dias + "\n\nnext_pay: " + next_pay + "\n\nIntereses de mora: " + intereses_de_mora + "\n\nSaldo_plus: " + saldo_plus + "\n\n.");
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            saldo = String.valueOf(Integer.parseInt(saldo_plus) + Integer.parseInt(intereses_de_mora));
            morosidad = "D";
        } else {//Significa que esta atrazado!!!
            double pre_saldo = diferencia_en_dias * (Integer.parseInt(interes_mora)) * Integer.parseInt(saldo_plus);
            Log.v("obt_saldo_al_diaM1", "Abonar.\n\nPre saldo: " + pre_saldo + "\n\n.");
            pre_saldo = pre_saldo / 100;//interes_mora_total no guarda el interes que se guarda en el archivo. (interes_monroe)
            Log.v("obt_saldo_al_diaM1.1", "Abonar.\n\ninteres_mora_total: " + interes_mora_total + "\n\nintereses_de_mora: " + intereses_de_mora + "\n\n.");
            saldo = String.valueOf(Integer.parseInt(saldo_plus)  + Integer.valueOf(interes_mora_total) + Integer.parseInt(intereses_de_mora));//No se suman intereses sobre los intereses moratorios, pero si sobre el interes acordado del credito!!!
            Log.v("obt_saldo_al_diaM2", "Abonar.\n\nSaldo: " + saldo + "\n\n.");
            double pre_num_pre = Integer.parseInt(interes_mora) * Integer.parseInt(monto_prestado) * diferencia_en_dias;
            pre_num_pre = pre_num_pre / 100;
            double pre_num = (pre_num_pre) + Integer.parseInt(intereses_de_mora);
            int pre_num_int = (int) pre_num;
            if (pre_num_int > 0) {
                morosidad = "M";
            }
        }
        Log.v("obt_saldo_al_dia_end", "Abonar.\n\nSaldo (flag): " + saldo + "\n\n.");
        flag = saldo;
        return flag;
    }

    private boolean revisar_creditos () {
        Log.v("revisando_creditos0", ".\n\nAbonar. Revisando creditos.");
        boolean flasg = false;
        String flag = "";
        lista_archivos = "";
        String cliente_file = cliente_ID + "_C_.txt";
        String archivos[] = fileList();
        Log.v("revisando_creditos1", ".\n\nAbonar. \n\nTotal de archivos: " + archivos.length + "\n\n.");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("revisando_creditos2", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
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
                            String[] split = linea.split("_separador_");
                            if (split[0].equals("saldo_mas_intereses")) {
                                saldo_mas_int_tempo = split[1];
                                Log.v("revisando_creditos3", "Abonar.\n\nlinea:\n\n" + linea + "\n\n.");
                                if (Integer.parseInt(saldo_mas_int_tempo) < 100) {
                                    //Do nothing. Credito ya ha sido cancelado casi en su totalidad, por lo que se toma como cancelado al 100% y no se muestra.
                                } else {
                                    lista_archivos = lista_archivos + archivos[i] + "_sep_";//Significa que es un credito activo.
                                    Log.v("revisando_creditos4", "Abonar.\n\nlista_archivos:\n\n" + lista_archivos + "\n\n.");
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
        } else if (Integer.parseInt(flag) == 1) {
            cantidad_de_creditos = 1;
            String[] split = lista_archivos.split("_sep_");
            Log.v("revisando_creditos5", ".\n\nAbonar. Archivo correcto: " + split[0] + "\n\n.");
            archivo_prestamo = split[0];
            Log.v("revisando_creditos6", ".\n\nAbonar. Contenido del archivo " + archivo_prestamo + ":\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
        } else {
            flasg = true;
            cantidad_de_creditos = 2;//Significa que son 2 o mas creditos. Se debe activar el spinner.
            Log.v("revisando_creditos7", ".\n\ncantidad de creditos: " + cantidad_de_creditos + "\n\n.");
        }
        Log.v("revisando_creditos8", ".\n\nflag: " + flasg + "\n\n.");
        return flasg;
    }

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
            if (file_to_consult.contains("*") || file_to_consult.contains(" ")) {
                Log.v("Consultar0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                //Do nothing.
            } else {
                Log.v("consultar0.2", "Abonar.\n\nCliente_ID: " + cliente_ID + "\n\nFile to consult: " + file_to_consult + "\n\n.");
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
                                if (split[0].equals("nombre_cliente")) {
                                    nombre_cliente = split[1];
                                }
                                if (split[0].equals("apellido1_cliente")) {
                                    apellido_cliente = split[1];
                                }
                                if (split[0].equals("monto_disponible")) {
                                    monto_disponible = split[1];
                                }
                                if (split[0].equals("interes_mora")) {
                                    interes_mora = split[1];
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
            }
            if (archivoCompleto.equals("")) {
                //No se encontro el cliente. NUNCA DEBERIA LLEGAR AQUI!!! (FILTRO PREVIO)
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
                text_listener();
            } else {
                Log.v("consultar_digite_ced", ".\n\nLlamando a llenar spinner." + "\n\n.");
                boolean credits = revisar_creditos();
                if (credits) {
                    llenar_spinner();//Aqui se debe llamar a presentar_info_credito().
                } else {
                    presentar_info_credito("UNO");
                }
            }
        } else if (tv_esperar.getText().toString().equals("Monto a pagar al dia de hoy: ")) {
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
            tv_esperar.setText("");
            bt_perdon.setVisibility(View.INVISIBLE);
            procesar_abono2();
        } else if (tv_esperar.getText().toString().equals("Prestamo a consultar:")){
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
            Log.v("Prestamo_a_consultar1", ".\n\nPrestamo que se va a abonar: " + et_ID.getText().toString() + "\n\n.");
            String[] parts_prestamo = et_ID.getText().toString().split(" ");
            int monto_a_pagar = 0;
            cantidad_cuotas_pendientes = Integer.parseInt(parts_prestamo[3]);
            morosidad = parts_prestamo[2];
            monto_cuota = obtener_monto_cuota(parts_prestamo[0]);
            Log.v("Prestamo_a_consultar2", ".\n\nCoutas pendientes: " + cantidad_cuotas_pendientes + "\n\nInteres mora total: " +
                    interes_mora_parcial + "\n\nMorosidad: " + morosidad + "\n\nMonto cuota: " + monto_cuota + "\n\n.");
            //archivo_prestamo = file_name; Checked!!!
            int montoAPagar = 0;
            int interesMoraTotal = 0;
            if (Integer.parseInt(parts_prestamo[3]) == 0) {
                monto_a_pagar = monto_cuota + intereses_monroe;
                montoAPagar = monto_cuota;
                interesMoraTotal = intereses_monroe;
                Log.v("consultar0.01", "Abonar.\n\nmonto a pagar: " + monto_a_pagar + "\ninteresMoraTotal: " + interesMoraTotal + "\n\n");
            } else {
                //morosidad
                if (morosidad.equals("D")) {
                    Log.v("consultar0.02", "Abonar.\n\nESTO NUNCA VA A PASAR ERROR\n\n");
                    monto_a_pagar = cantidad_cuotas_pendientes * monto_cuota + intereses_monroe;
                    montoAPagar = cantidad_cuotas_pendientes * monto_cuota;
                    interesMoraTotal = intereses_monroe;
                } else {
                    //monto a pagar
                    monto_a_pagar = cantidad_cuotas_pendientes * monto_cuota + Integer.parseInt(interes_mora_parcial);
                    montoAPagar = cantidad_cuotas_pendientes * monto_cuota + Integer.parseInt(interes_mora_parcial) - Integer.parseInt(interes_mora_parcial);
                    interesMoraTotal = Integer.parseInt(interes_mora_parcial);
                    Log.v("consultar0.03", "Abonar.\n\nmonto a pagar: " + monto_a_pagar + "\ninteresMoraTotal: " +
                            interesMoraTotal + "\n\ncantidad_cuotas_pendientes: " + cantidad_cuotas_pendientes + "\n\n");
                }
            }
            presentar_monto_a_pagar(monto_a_pagar, interesMoraTotal, montoAPagar);
        } else {
            //TODO: no se sabe que hacer aqui!!!
        }
    }

    private void procesar_abono2 () {
        String file_name = archivo_prestamo;
        String contenido = "";
        String fecha_next_abono = "";
        String interes_mora_total_s = "";
        String saldo_mas_intereses_s = "";
        Date fecha_credito = new Date();
        int factor_semanas = 0;
        int monto_ingresado = Integer.parseInt(et_ID.getText().toString());
        actualizar_caja(monto_ingresado);
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split("_separador_");

                if (split[0].equals("cuadratura")) {
                    cuadratura = split[1];
                } else if (split[0].equals("proximo_abono")) {
                    fecha_next_abono = split[1];
                } else if (split[0].equals("plazo")) {
                    plazo = split[1];
                } else if (split[0].equals("fecha_credito")) {
                    String fecha_aux = split[1];
                    String[] split_fecha_aux = fecha_aux.split("/");
                    fecha_aux = split_fecha_aux[2] + "-" + split_fecha_aux[1] + "-" + split_fecha_aux[0];
                    fecha_credito = DateUtilities.stringToDate(fecha_aux);
                } else if (split[0].equals("monto_credito")) {
                    monto_prestado_final = split[1];
                } else if (split[0].equals("saldo_mas_intereses")) {
                    saldo_mas_intereses_s = split[1];
                    saldo_mas_intereses = Integer.parseInt(saldo_mas_intereses_s);
                } else if (split[0].equals("cuotas")) {
                    cuotas = split[1];
                } else if (split[0].equals("tasa")) {
                    tasa = Integer.parseInt(split[1]);
                } else if (split[0].equals("ID_credito")) {
                    credit_ID = split[1];
                } else if (split[0].equals("morosidad")) {
                    morosidad = split[1];
                } else if (split[0].equals("intereses_moratorios")) {
                    interes_mora_total_s = split[1];
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
            } else if (piezas[1].equals("meses")) {
                factor_semanas = 4;
            } else {
                factor_semanas = -1;
                //ERROR
            }
            String saldo_plus_s = obtener_saldo_plus(cuadratura);
            Log.v("proc_abono00", "Abonar.\n\nSaldo plus: " + saldo_plus_s + "\n\n.");
            String intereses_moritas = obtener_intereses_moratorios(monto_prestado_final, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
            interes_mora_total = intereses_moritas;
            interes_mora_parcial = interes_mora_total;
            Log.v("antes_de_cuadra_chang", ".\n\nAbonar. Archivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
            cuadratura = obtener_cuadratura(cuadratura, fecha_next_abono, factor_semanas, monto_ingresado, fecha_credito, "final");//Aqui se obtiene la verdadera y final morosidad.
            Log.v("despues_de_cuadra_chang", ".\n\nAbonar. Archivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
            Log.v("proc_abo_20", ".Abonar.\n\nproc_abo_2: " + cuadratura + "\n\n.");
            intereses_monroe = Integer.parseInt(interes_mora_total_s);//Son los intereses guardados en el archivo. calculados en un periodo que se abono solo parte de los intereses.
            Log.v("proc_abo_21", ".Abonar.\n\nsaldo_mas_intereses_s: " + saldo_mas_intereses + "\n\n.");
            //saldo_mas_intereses = Integer.valueOf(obtener_saldo_al_dia(saldo_plus_s, fecha_next_abono, interes_mora_total_s, monto_prestado_final));
            Log.v("proc_abo_22", "Abonar.\n\nsaldo mas intereses: " + saldo_mas_intereses + "\n\n.");
            cuotas = obtener_cuotas_nuevas(cuadratura);
            //saldo_mas_intereses = Integer.parseInt(obtener_saldo_plus(cuadratura));



            actualizar_archivo_credito();
        } catch (IOException | ParseException e) {
        }
    }

    private void actualizar_archivo_credito () {
        String contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(archivo_prestamo));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split("_separador_");
                Log.v("Linea", linea);
                if (split[0].equals("cuadratura")) {
                    linea = linea.replace(split[1], cuadratura);
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("monto_abono")) {
                    Log.v("actualiz_arch_cred0", "Abonar.\n\nmonto_abono: " + monto_abono + "\n\n.");
                    linea = linea.replace(split[1], String.valueOf(monto_abono));
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("proximo_abono")) {
                    linea = linea.replace(split[1], proximo_abono);
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("saldo_mas_intereses")) {
                    linea = linea.replace(split[1], String.valueOf(saldo_mas_intereses));
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("cuotas")) {
                    linea = linea.replace(split[1], cuotas);
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("ID_credito")) {
                    contenido = contenido + linea + "\n";
                    credit_ID = split[1];
                    //Log.v("act_file_cred", "Abonar.\n\ncredit_ID: " + credit_ID + "\n\n.");
                } else if (split[0].equals("morosidad")) {
                    linea = linea.replace(split[1], morosidad);
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("intereses_moratorios")) {
                    linea = linea.replace(split[1], interes_mora_total);
                    contenido = contenido + linea + "\n";
                } else {
                    contenido = contenido + linea + "\n";
                }
                Log.v("Linea_post", linea);
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            Log.v("actualizar_archiv_cred1", ".\n\nAbonar. Archivo: " + archivo_prestamo + "\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
            borrar_archivo(archivo_prestamo);
            crear_archivo(archivo_prestamo);
            guardar(contenido, archivo_prestamo);
            Log.v("actualizar_archiv_cred2", ".\n\nAbonar. Archivo: " + archivo_prestamo + "\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
            actualizar_cierre(monto_abono, obtener_caja(), credit_ID);
            actualizar_archivo_cliente(archivo_prestamo);
        } catch (IOException e) {
        }
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


    private void actualizar_cierre (Integer monto_abono, Integer saldo_caja, String credit_ID) {
        String linea_cierre = "abono " + String.valueOf(monto_abono) + " " + saldo_caja + " " + credit_ID;
        agregar_linea_archivo(linea_cierre, "cierre.txt");
    }

    private void presentar_cuadratura () {
        //TODO: llamar a la activity estado_de_cuenta
        Intent CuadraturaAc = new Intent(this, CuadraturaActivity.class);
        CuadraturaAc.putExtra("cuadratura", cuadratura);
        CuadraturaAc.putExtra("msg", "Abono realizado con exito!!!");
        Log.v("presentar_cuadra0", "Abonar.\n\nCliente_ID: " + cliente_ID + "\n\n.");
        CuadraturaAc.putExtra("cliente_recivido", cliente_ID);
        CuadraturaAc.putExtra("cambio", String.valueOf(cambio));
        CuadraturaAc.putExtra("monto_creditito", "0");
        CuadraturaAc.putExtra("activity_devolver", "Estado_cliente");
        CuadraturaAc.putExtra("mensaje_imprimir_pre", mensaje_imprimir);
        CuadraturaAc.putExtra("nombre_cliente", nombre_cliente + " " + apellido_cliente);
        CuadraturaAc.putExtra("abonar", "abonar" + " " + "abonar");
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(CuadraturaAc);
        finish();
        System.exit(0);
    }

    private void actualizar_archivo_cliente (String archivo_P) {
        String[] spliti = archivo_P.split("_");
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
                } else if (split[0].equals("monto_disponible")) {
                    Log.v("act_arch_cli_mont_disp", ".\n\nMonto disponible: " + monto_disponible + "\n\n.");
                    linea = linea.replace(split[1], String.valueOf(monto_disponible));
                    contenido = contenido + linea + "\n";
                } else {
                    contenido = contenido + linea + "\n";
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            Log.v("actualiz_archiv_client1", ".\n\nAbonar. Archivo: " + archivo_cliente + "\n\nContenido del archivo:\n\n" +
                    imprimir_archivo(archivo_cliente) + "\n\n.");
            borrar_archivo(archivo_cliente);
            crear_archivo(archivo_cliente);
            guardar(contenido, archivo_cliente);
            //String file_name = credit_ID + ".txt";
            Log.v("actualiz_archiv_client2", ".\n\nAbonar. Archivo: " + archivo_cliente + "\n\nContenido del archivo:\n\n" +
                    imprimir_archivo(archivo_cliente) + "\n\n.");
            subir_archivo(archivo_P);
        } catch (IOException | JSONException e) {
        }
    }

    private String obtener_saldo_plus (String cuadratura_s) {
        String flag = "";
        Log.v("obt_sald_plus", "Abonar.\n\nCuadratura:\n\n" + cuadratura_s + "\n\n.");
        String[] split = cuadratura_s.split("__");
        int largo_split = split.length;
        int saldo_plus_plus = 0;
        for (int i = 0; i < largo_split; i++) {
            String[] split_1 = split[i].split("_");
            if (Integer.parseInt(split_1[2]) > 0) {//Significa que tiene esta cuota pendiente.
                saldo_plus_plus = saldo_plus_plus + Integer.parseInt(split_1[2]);
            }
        }
        flag = String.valueOf(saldo_plus_plus);
        return flag;
    }

    private String obtener_cuotas_nuevas (String cuadratura) {
        String flag = "";
        String[] split = cuadratura.split("__");
        int largo_split = split.length;
        int cuottas = 0;
        for (int i = 0; i < largo_split; i++) {
            String[] split_1 = split[i].split("_");
            if (Integer.parseInt(split_1[2]) > 0) {//Significa que tiene esta cuota pendiente.
                cuottas = cuottas + 1;
            }
        }
        flag = String.valueOf(cuottas);
        return flag;
    }

    public void perdonar (View view) throws JSONException, IOException {
        String tv_esperar_text = tv_esperar.getText().toString();
        if (tv_esperar_text.equals("Monto a pagar al dia de hoy: ")) {
            monto_anterior = Integer.parseInt(et_ID.getText().toString());
            bt_perdon.setClickable(false);
            bt_perdon.setEnabled(false);
            tv_esperar.setText("Digite el monto a perdonar:");
            et_mensaje.setVisibility(View.VISIBLE);
            et_mensaje.setHint("Mensaje...");
            et_ID.setText("");
            et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
            et_ID.setHint("Monto a perdonar...");
            et_ID.requestFocus();
            bt_consultar.setVisibility(View.INVISIBLE);
            bt_perdon.setText("CONFIRMAR");
            bt_perdon.setEnabled(true);
            bt_perdon.setClickable(true);
        } else if (tv_esperar_text.equals("Digite el monto a perdonar:")) {
            int monto_digitado = Integer.parseInt(et_ID.getText().toString());
            if (monto_digitado > Integer.parseInt(interes_mora_total)) {
                Toast.makeText(this, "Debe digitar un monto menor o igual al monto de los intereses!", Toast.LENGTH_LONG).show();
                bt_perdon.setClickable(false);
                bt_perdon.setEnabled(false);
                tv_esperar.setText("Digite el monto a perdonar:");
                et_mensaje.setVisibility(View.VISIBLE);
                et_mensaje.setText("");
                et_ID.setText("");
                et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
                et_ID.setHint("Monto a perdonar...");
                et_ID.requestFocus();
                bt_consultar.setVisibility(View.INVISIBLE);
                bt_perdon.setText("CONFIRMAR");
                bt_perdon.setEnabled(true);
                bt_perdon.setClickable(true);
            } else {
                if (et_mensaje.getText().toString().equals("")) {
                    Toast.makeText(this, "Debe digitar un mensaje!", Toast.LENGTH_LONG).show();
                    bt_perdon.setClickable(false);
                    bt_perdon.setEnabled(false);
                    tv_esperar.setText("Digite el monto a perdonar:");
                    et_mensaje.setVisibility(View.VISIBLE);
                    et_mensaje.setText("");
                    et_ID.setText("");
                    et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
                    et_ID.setHint("Monto a perdonar...");
                    et_ID.requestFocus();
                    bt_consultar.setVisibility(View.INVISIBLE);
                    bt_perdon.setText("CONFIRMAR");
                    bt_perdon.setEnabled(true);
                    bt_perdon.setClickable(true);
                } else {
                    flag_perdon = true;
                    mensaje_solicitud = et_mensaje.getText().toString();
                    bt_perdon.setClickable(false);
                    bt_perdon.setEnabled(false);
                    bt_perdon.setVisibility(View.INVISIBLE);
                    int nuevo_monto_intereses = 0;
                    int viejo_monto_intereses = Integer.parseInt(interes_mora_total);
                    nuevo_monto_intereses = viejo_monto_intereses - monto_digitado;
                    interes_mora_total = String.valueOf(nuevo_monto_intereses);
                    String texto_cuadro = tv_indicador.getText().toString();
                    Log.v("perdonar0", "Abonar.\n\nmonto_viejo: " + viejo_monto_intereses + "\n\nmonto_digitado: " + monto_digitado + "\n\n.");
                    Log.v("perdonar1", "Abonar.\n\ntexto_cuadro (pre):\n\n" + texto_cuadro + "\n\nlinea a reemplazar: " + "Intereses moratorios: " +
                            String.valueOf(viejo_monto_intereses) + "\n\n.");
                    texto_cuadro = texto_cuadro.replace("Intereses moratorios: " +
                            String.valueOf(viejo_monto_intereses), "Intereses moratorios: " +
                            String.valueOf(nuevo_monto_intereses));
                    Log.v("perdonar2", "Abonar.\n\ntexto_cuadro (post):\n\n" + texto_cuadro + "\n\n.");
                    msg("Se han perdonado: " + String.valueOf(monto_digitado) + " colones.");
                    tv_indicador.setText(texto_cuadro);
                    tv_esperar.setText("Monto a pagar al dia de hoy: ");
                    et_mensaje.setVisibility(View.INVISIBLE);
                    int monto_poner = monto_anterior - monto_digitado;
                    et_ID.setText(String.valueOf(monto_poner));
                    et_ID.setFocusable(true);
                    et_ID.requestFocus();
                    flag_solicitud = true;
                    bt_consultar.setEnabled(true);
                    bt_consultar.setClickable(true);
                    bt_consultar.setVisibility(View.VISIBLE);
                    subir_solicitud(monto_digitado, mensaje_solicitud);
                }
            }
        }
    }

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void subir_solicitud (Integer monto_perdonador, String mensaje_solicitud) throws JSONException, IOException {
        //ocultar_todito();
        monto_perdonado = monto_perdonador;
        String sp_creditos = "";
        long ID_solic_final = 9000000;
        String solicitud_ID_S = "";
        String cobrador_ID_S = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] splitr = linea.split(" ");
            cobrador_ID_S = splitr[0];
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

        String archivos[] = fileList();
        Log.v("subir_solicitud1", ".\n\nAbonar. \n\nTotal de archivos: " + archivos.length + "\n\n.");
        Log.v("subir_solicitud2", "Abonar.\n\ncobrador_ID_S: " + cobrador_ID_S + "\n\n");

        for (int i = 0; i < archivos.length; i++) {
            String saldo_mas_int_tempo = "";
            Pattern pattern = Pattern.compile(cobrador_ID_S + "_S_", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    while (linea != null) {
                        String[] split = linea.split("_separador_");
                        if (split[0].equals("solicitud_ID")) {
                            solicitud_ID_S = split[1];
                            Log.v("subir_solicitud3", "Abonar.\n\nlinea:\n\n" + linea + "\n\n.");
                            String[] split_solicitud_ID_S = solicitud_ID_S.split("S");
                            int solicitud_ID_temp = Integer.parseInt(split_solicitud_ID_S[1]);
                            Log.v("subir_solicitud4", "Abonar.\n\nsolicitud_ID_temp: " + solicitud_ID_temp + "\n\n.");
                            if (solicitud_ID_temp > ID_solic_final) {
                                ID_solic_final = solicitud_ID_temp;
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

        ID_solic_final++;
        solicitud_ID = cobrador_ID_S + "S" + String.valueOf(ID_solic_final);
        long contador_name = ID_solic_final - 9000000;
        int contador_name_I = (int) contador_name;
        String file = cobrador_ID_S + "_S_" + String.valueOf(contador_name_I) + "_S_.txt";
        String contenido_file = "solicitud_ID_separador_" + solicitud_ID + "\n";
        contenido_file = contenido_file + "mensaje_separador_" + mensaje_solicitud + "\n";
        contenido_file = contenido_file + "monto_perdonado_separador_" + monto_perdonador;
        crear_archivo(file);
        borrar_archivo(file);
        crear_archivo(file);
        guardar(contenido_file, file);
        Log.v("subir_solicitud5", "Abonar.\n\nfile: " + "\n\n" + file + "\n\nContenido de file:\n\n" + imprimir_archivo(file) + "\n\n.");
        subir_solicitud2(file, sp_creditos);
    }

    private void subir_solicitud2 (String file, String sp_creditos) throws JSONException {
        //ocultar_todito();
        String spid = sp_creditos;
        String json_string = "";
        JSONObject jsonObject = new JSONObject();
        String sheet = "solicitudes";
        String id_solicitud = "";
        Log.v("subir_solicitud20", "Abonar.\n\nfile: " + file + "\n\ncontenido del archivo:\n\n" + imprimir_archivo(file));
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null && !linea.equals("")) {
                String[] split = linea.split("_separador_");
                Log.v("subir_solicitud21", "Abonar.\n\nLinea:\n\n" + linea + "\n\n.");
                json_string = json_string + split[1] + "_n_";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("subir_solicitud22", "Abonar.\n\njson_string: " + "\n\n" + json_string + "\n\n.");
        jsonObject = TranslateUtil.string_to_Json(json_string, spid, sheet, id_solicitud);
        subir_nuevo_solic(jsonObject, file);
    }

    private void subir_nuevo_caj (JSONObject jsonObject, String file) {
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
                            Log.v("subir_nuevo_caj0", "Abonar.\n\nresponse:\n\n" + response + "\n\n.");
                            if (length_split > 3) {//
                                for (int i = 0; i < length_split; i++) {
                                    Log.v("subir_nuevo_caj1" + i, "split[" + i + "]: " + split[i]);
                                }
                                cambiar_bandera2(file);
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
        }
    }

    private void subir_nuevo_solic (JSONObject jsonObject, String file) {
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
                            Log.v("subir_nuevo_solic0", "Abonar.\n\nresponse:\n\n" + response + "\n\n.");
                            if (length_split > 3) {//
                                for (int i = 0; i < length_split; i++) {
                                    Log.v("subir_nuevo_solic" + i, "split[" + i + "]: " + split[i]);
                                }
                                if (split[7].equals(solicitud_ID)) {//
                                    cambiar_bandera2(file);
                                } else {
                                    Log.v("subir_nuevo_solic(i+1)", "Error al subir informacion del credito al servidor.");
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
        }
    }

    private String obtener_cuadratura (String cuadratura, String fecha_next_abono, int factor_semanas, int monto_ingresado, Date fecha_credito, String paso) throws ParseException {
        String flag = "";
        if (paso.equals("final")) {
            if (flag_perdon) {
                int interes_moratorio_I = Integer.parseInt(interes_mora_total);
                interes_moratorio_I = interes_moratorio_I - monto_perdonado;
                interes_mora_total = String.valueOf(interes_moratorio_I);
                flag_solicitud = true;
                Log.v("obt_cuadra-5", "Abonar.\n\ninteres_mora_total: " + interes_mora_total + "\n\n.");
            }
        }
        Log.v("obt_cuadra-4", "Abonar.\n\ninteres_mora_total: " + interes_mora_total + "\n\n.");
        //mensaje_imprimir = "\n******************************\n";
        int monto_temporal = monto_ingresado - Integer.parseInt(interes_mora_total);
        int monto_temporal_fix = monto_temporal;
        if (monto_ingresado > 0) {
            if (Integer.parseInt(interes_mora_total) > 0) {
                mensaje_imprimir = mensaje_imprimir + "Monto abonado: " + monto_ingresado + " colones.\n";
                mensaje_imprimir = mensaje_imprimir + "Intereses moratorios: " + interes_mora_total + "\n";
                mensaje_imprimir = mensaje_imprimir + "******************************\n\n";
            }
        }
        Log.v("Debug_cuadra0", "Abonar.\n\nCuadratura: " + cuadratura + "\n\ninteres mora total: " + interes_mora_total +
                "\n\nfecha next abono: " + fecha_next_abono + "\n\nMonto ingresado: " + monto_ingresado + "\n\nMonto temporal: " + monto_temporal + "\n\n.");
        if (monto_temporal < 0) {//No alcanzo siquiera para pagar los intereses. Debe retornar
            String[] split2 = fecha_next_abono.split("/");
            String fecha_nx_abo = split2[2] + "-" + split2[1] + "-" + split2[0];
            Date fecha_nx_abo_LD = DateUtilities.stringToDate(fecha_nx_abo);
            String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_nx_abo_LD));
            int interes_mora_diario = Integer.parseInt(interes_mora_total) / Integer.parseInt(diferencia_fechas);
            int dias_pagados = monto_ingresado / interes_mora_diario;
            Log.v("obt_cuadra-2", "Abonar.\n\nMonto ingresado: " + monto_ingresado + "\n\ninteres mora diario: " + interes_mora_diario + "\n\n.");
            Date fecha_nextr = DateUtilities.addDays(fecha_nx_abo_LD, dias_pagados);
            interes_mora_total = String.valueOf(Integer.parseInt(interes_mora_total) - monto_ingresado);
            Log.v("obt_cuadra_mo_paga-1", "Abonar.\n\ninteres_mora_diario: " + interes_mora_diario + "\n\ninteres_mora_total: " +
                    interes_mora_total + "\n\ndias_pagados: " + dias_pagados + "\n\nfecha_nextr: " + fecha_nextr + "\n\n.");
            Log.v("obtener_cuadra_no_paga0", "Abonar.\n\ninteres_mora_tota: " + interes_mora_total + "\n\n.");
            proximo_abono = DateUtilities.dateToString(fecha_nextr);
            String[] split = proximo_abono.split("-");
            proximo_abono = split[2] + "/" + split[1] + "/" + split[0];
            Log.v("obtener_cuadra_no_paga1", "Abonar.\n\nproximo abono: " + proximo_abono + "\n\n.");
            morosidad = "M";
            flag = cuadratura;//TODO: No se le ha hecho nada a cuadratura :-( (Porque no hay que hacerle nada!!!)
            Log.v("obt_cuadra_no_paga_int", "Abonar.\n\nCuadratura:\n\n" + cuadratura + "\n\nInteres mora total: " + interes_mora_total +
                    "\n\nProximo abono: " + proximo_abono + "\n\n.");
            monto_abono = monto_ingresado;
            return flag;
        } else if (monto_temporal == 0) {//Aqui paga el monto completo, solo de los intereses moratorios, no abona nada a los abonos ordinarios. Debe retornar
            flag = cuadratura;//TODO: No se le ha hecho nada a cuadratura :-( (Porque no hay que hacerle nada!!!)
            proximo_abono = DateUtilities.dateToString(hoy_LD);
            String[] split = proximo_abono.split("-");
            proximo_abono = split[2] + "/" + split[1] + "/" + split[0];
            morosidad = "M";
            interes_mora_total = "0";
            if (monto_ingresado > 0) {
                mensaje_imprimir = mensaje_imprimir + "Monto abonado: " + monto_ingresado + " colones\n";
                mensaje_imprimir = mensaje_imprimir + "Intereses moratorios: " + interes_mora_total + "\n";
                mensaje_imprimir = mensaje_imprimir + "Abono al capital:     " + "0" + "\n******************************\n";
            }
            monto_abono = monto_ingresado;
            return flag;
        } else if (monto_temporal > 0) {//Aqui paga el monto de los intereses y ademas, paga tambien parte o to-do lo de las cuotas pendientes y/o futuras.
            String[] split = cuadratura.split("__");
            double factor = tasa + 100;
            double x = monto_temporal * 100;
            x = x / factor;
            double debuge = factor;
            double restar_disponibleL =  monto_temporal - x;
            restar_disponibleL = restar_disponibleL / 100;
            Log.v("debug_cuadra1_pre_pre", ".\n\nAbonar. \n\nRestar disponible: " + restar_disponibleL + "\n\nMonto disponible: " + monto_disponible +
                    "\n\nDebuge: " + debuge + "\n\n.");
            int restar_disponible = (int) restar_disponibleL;
            int xx = (int) x;
            float monto_disponible_F = Float.parseFloat(monto_disponible);
            int monto_disponible_I = (int) monto_disponible_F;
            monto_disponible = String.valueOf(monto_disponible_I);
            monto_disponible = String.valueOf((Integer.parseInt(monto_disponible) + xx));
            Log.v("debug_cuadra1_pre", ".\n\nAbonar. \n\nRestar disponible: " + restar_disponible + "\n\nmonto_disponible " + monto_disponible +
                    "\n\nxx: " + xx + "\n\nx: " + x + "\n\nDebuge: " + debuge + "\n\n.");
            Log.v("debug_cadra1", ".\n\nMonto temporal: " + monto_temporal + "\n\nTasa: " + tasa + "\n\nRestar disponible: " + restar_disponible +
                    "\n\nMonto disponible: " + monto_disponible + "\n\n.");
            int largo_split = split.length;
            for (int i = 0; i < largo_split; i++) {
                String[] split_1 = split[i].split("_");
                if (Integer.parseInt(split_1[2]) > 0) {//Significa que tiene esta cuota pendiente.
                    monto_temporal = monto_temporal - Integer.parseInt(split_1[2]);//Esta es la cantidad que va quedando del abono.
                    if (monto_temporal < 0) {//Significa que no alcanza para esta cuota. Debe retornar
                        String fecha_cuadrito = split_1[3];
                        String numero_cuota = split_1[1];
                        Log.v("paga_parcial_cuota", "Abonar.\n\nNumero de cuota: " + numero_cuota + "\n\n");
                        String[] split_fec = fecha_cuadrito.split("/");
                        fecha_cuadrito = split_fec[2] + "-" + split_fec[1] + "-" + split_fec[0];
                        Date fecha_cuadrito_LD = DateUtilities.stringToDate(fecha_cuadrito);
                        String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_cuadrito_LD));
                        Log.v("paga_cuotas", ".\n\nfecha_cuadrito: " + fecha_cuadrito + "\n\nDiferencia entre fechas: " + diferencia_fechas + "\n\n.");
                        if (monto_ingresado > 0) {
                            int monto_abonado_I = Integer.parseInt(split_1[2]) + monto_temporal;
                            //saldo_mas_intereses = saldo_mas_intereses - monto_abonado_I;
                            mensaje_imprimir = mensaje_imprimir + "\n\nPaga la cuota # " + numero_cuota + "\nde manera parcial.\nMonto abonado\ncuota #" +
                                    split_1[1] + " de " + total_cuotas + ": " + monto_abonado_I + " colones.\nSaldo pendiente\ncuota #" + split_1[1] + ": " +
                                    String.valueOf(0 - monto_temporal) + " colones.\n";
                        }
                        if (Integer.parseInt(diferencia_fechas) > 0) {//Significa que esta atrasado.
                            morosidad = "M";
                            if (monto_ingresado > 0) {
                                mensaje_imprimir = mensaje_imprimir + "\n** *** ** *** ** *** ** *** **\nCuota # " + numero_cuota +
                                        " se encuentra atrasada!\n** *** ** *** ** *** ** *** **\n";
                            }
                        } else if (Integer.parseInt(diferencia_fechas) <= 0 ) {
                            morosidad = "D";
                        } else {
                            //Do nothing.
                        }
                        int saldo_cuadro = Integer.parseInt(split_1[2]);
                        saldo_cuadro = 0 - monto_temporal;//Did it:Que pasa si el monto ingresado es mayor al monto de una cuota o de todas juntas?
                        int monto_abono_c = Integer.parseInt(split_1[2]) - saldo_cuadro;
                        proximo_abono = split_1[3];
                        if (monto_ingresado > 0) {
                            mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ": " + monto_abono_c + "\n";
                            mensaje_imprimir = mensaje_imprimir + "\n******************************\nMonto abonado: " + monto_ingresado + " colones.\n";
                            mensaje_imprimir = mensaje_imprimir + "Intereses moratorios: " + interes_mora_total + "\n";
                            mensaje_imprimir = mensaje_imprimir + "Abono al capital:     " + monto_temporal_fix + "\n******************************\n";
                            saldo_mas_intereses = saldo_mas_intereses - monto_temporal_fix;
                        }
                        interes_mora_total = "0";
                        cuadratura = cuadratura.replace(split_1[0] + "_" + split_1[1] + "_" + split_1[2] + "_" + split_1[3],
                                split_1[0] + "_" + split_1[1] + "_" + String.valueOf(saldo_cuadro) + "_" + split_1[3]);
                        flag = cuadratura;
                        monto_abono = monto_ingresado;
                        return flag;
                    } else if (monto_temporal > 0) {//Alcanza para pagar esta cuota y sobra. NO RETORNA!!! Debe continuar... (TODO: A no ser que el monto supere toda la deuda!!!)
                        //
                        cuadratura = cuadratura.replace(split_1[0] + "_" + split_1[1] + "_" + split_1[2] + "_" + split_1[3],
                                split_1[0] + "_" + split_1[1] + "_0_" + split_1[3]);//TODO: Hacer que if (i == split_length) {retornar_cambio}
                        Log.v("obtener_cuadra4", "Abonar.\n\ncuadratura:\n\n" + cuadratura + "\n\n.");
                        if (i == (largo_split - 1)) {
                            proximo_abono = "Prestamo cancelado.\nSaldo pendiente: 0 colones";
                            cambio = monto_temporal;//TODO: CORREGIR MONTO DISPONIBLE CUANDO SOBRA CAMBIO
                            actualizar_caja((0-cambio));
                            monto_disponible = String.valueOf(Integer.parseInt(monto_disponible) - cambio);
                            flag = cuadratura;
                            if (monto_ingresado > 0) {
                                if (cambio > 0) {
                                    mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ": " + split_1[2] +
                                            " colones.\nSaldo pendiente\ncuota #" + split_1[1] +
                                            ": 0 colones.\nCambio: " + cambio + " colones.\n";
                                } else {
                                    mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ": " +
                                            split_1[2] + " colones.\nSaldo pendiente\ncuota #" + split_1[1] + ": 0 colones.\n";
                                }
                                mensaje_imprimir = mensaje_imprimir + "\n******************************\nMonto abonado: " + String.valueOf(monto_ingresado - cambio) +
                                        " colones.\n";
                                mensaje_imprimir = mensaje_imprimir + "Intereses moratorios: " + interes_mora_total + "\n";
                                mensaje_imprimir = mensaje_imprimir + "Abono al capital:     " + String.valueOf(monto_temporal_fix - cambio) +
                                        "\n******************************\n\nFecha proximo abono:\n" + proximo_abono + "\n";
                                saldo_mas_intereses = saldo_mas_intereses - (monto_temporal_fix - cambio);
                            }
                            monto_abono = monto_ingresado - cambio;
                            return flag;
                        } else {
                            if (monto_ingresado > 0) {
                                mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ": " +
                                        split_1[2] + " colones.\nSaldo pendiente\ncuota #" + split_1[1] + ": 0 colones.\n";
                            }
                        }
                    } else if (monto_temporal == 0) {//Alcanza para pagar esta cuota pero no sobra nada. Debe retornar!!!
                        //
                        cuadratura = cuadratura.replace(split_1[0] + "_" + split_1[1] + "_" + split_1[2] + "_" + split_1[3],
                                split_1[0] + "_" + split_1[1] + "_0_" + split_1[3]);
                        String fecha_cuadrito = split_1[3];
                        String[] split_fec = fecha_cuadrito.split("/");
                        fecha_cuadrito = split_fec[2] + "-" + split_fec[1] + "-" + split_fec[0];
                        Date fecha_cuadrito_LD = DateUtilities.stringToDate(fecha_cuadrito);
                        String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_cuadrito_LD));
                        Log.v("obtener_cuadra2", "Abonar.\n\nfecha cuadrito Date: " + fecha_cuadrito_LD.toString() +
                                "\n\nDiferencia entre la fecha de hoy\ny la fecha de la cuota que acaba de pagar: " + diferencia_fechas + " dias.\n\n.");
                        if (Integer.parseInt(diferencia_fechas) > 0) {//Significa que esta atrasado y se va a cancelar esta cuota.
                            Date next_pago;
                            if (factor_semanas == 1) {
                                next_pago = DateUtilities.addWeeks(fecha_cuadrito_LD, factor_semanas);
                            } else if (factor_semanas == 2) {
                                next_pago = DateUtilities.addQuincenas(fecha_cuadrito_LD, 1, fecha_credito);
                            } else if (factor_semanas == 4) {
                                next_pago = DateUtilities.addMonths(fecha_cuadrito_LD, 1);
                            } else {
                                next_pago = fecha_cuadrito_LD;
                            }
                            String next_pago_S = DateUtilities.dateToString(next_pago);
                            String[] split_pago = next_pago_S.split("-");
                            next_pago_S = split_pago[2] + "/" + split_pago[1] + "/" + split_pago[0];
                            proximo_abono = next_pago_S;
                            int diferencia_a_hoy = DateUtilities.daysBetween(hoy_LD, next_pago);
                            Log.v("obtener_cuadra5", "Abonar.\n\nproximo abono: " + proximo_abono + "\n\nDiferencia en dias a hoy: " + diferencia_a_hoy + "\n\n.");
                            if (diferencia_a_hoy > 0) {//Esta atrasado
                                morosidad = "M";
                            } else {
                                morosidad = "D";
                            }
                        } else if (Integer.parseInt(diferencia_fechas) <= 0 ) {//Cuota al dia que se va a cancelar.
                            morosidad = "D";
                            Date proximo_abono_LD = new Date();
                            if (factor_semanas == 1) {
                                proximo_abono_LD = DateUtilities.addWeeks(fecha_cuadrito_LD, factor_semanas);
                            } else if (factor_semanas == 2) {
                                proximo_abono_LD = DateUtilities.addQuincenas(fecha_cuadrito_LD, 1, fecha_credito);
                            } else if (factor_semanas == 4) {
                                proximo_abono_LD = DateUtilities.addMonths(fecha_cuadrito_LD, 1);
                            } else {
                                proximo_abono_LD = fecha_cuadrito_LD;
                            }
                            Log.v("obtener_cuadra3", "Abonar.\n\nProximo abono calculado: " + proximo_abono_LD.toString() + "\n\n.");
                            fecha_cuadrito = DateUtilities.dateToString(proximo_abono_LD);
                            String[] split_fe_cua = fecha_cuadrito.split("-");
                            fecha_cuadrito = split_fe_cua[2] + "/" + split_fe_cua[1] + "/" + split_fe_cua[0];
                            Log.v("obtener_cuadra4", "Abonar.\n\nProximo abono calculado: " + fecha_cuadrito + "\n\n.");
                            proximo_abono = fecha_cuadrito;
                        }
                        if (monto_ingresado > 0) {
                            mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ": " + split_1[2] +
                                    " colones.\nSaldo pendiente\ncuota #" + split_1[1] + ": 0 colones\n";
                            mensaje_imprimir = mensaje_imprimir + "\n******************************\nMonto abonado: " + monto_ingresado + " colones.\n";
                            mensaje_imprimir = mensaje_imprimir + "Intereses moratorios: " + interes_mora_total + "\n";
                            mensaje_imprimir = mensaje_imprimir + "Abono al capital:     " + monto_temporal_fix +
                                    "\n******************************\n\nFecha proximo abono:\n" + proximo_abono + "\n";
                            saldo_mas_intereses = saldo_mas_intereses - monto_temporal_fix;
                        }
                        interes_mora_total = "0";
                        flag = cuadratura;
                        monto_abono = monto_ingresado;
                        return flag;
                    }
                } else if (Integer.parseInt(split_1[2]) < 0) {//Nunca debe ser negativo el monto pendiente
                    Log.v("Obtener_cuadratura3", ".\n\nERROR EN DATO DE ARCHIVO\n\nContenido del archivo: \n\n" +
                            imprimir_archivo(archivo_prestamo) + "\n\n.");
                } else if (Integer.parseInt(split_1[2]) == 0) {//Esta cuota ya ha sido pagada, continuar...
                    //Do nothing. Continue...
                }
            }
            Log.v("Obtener_cuadratura_pF", ".\n\nERROR EN RETORNO\n\nContenido del archivo: \n\n" + imprimir_archivo(archivo_prestamo) +
                    "\n\ncuadratura:\n\n" + cuadratura + "\n\n.");
            return cuadratura;
        } else {
            //Do nothing. Never come here!!!
            Log.v("Obtener_cuadratura_else", ".\n\nERROR EN DATO DE ARCHIVO\n\nContenido del archivo: \n\n" +
                    imprimir_archivo(archivo_prestamo) + "\n\n.");
        }
        return flag;
    }

    private void presentar_monto_a_pagar (int monto_a_pagar, int interes_mora_total, int montoAPagar) {

        et_ID.setEnabled(true);
        //tv_indicador.setVisibility(View.INVISIBLE);
        et_ID.setText(String.valueOf(monto_a_pagar));
        et_ID.setFocusableInTouchMode(true);
        et_ID.setVisibility(View.VISIBLE);
        et_ID.setHint("Digite el monto a abonar...");
        bt_consultar.setEnabled(true);
        bt_consultar.setText("REALIZAR PAGO");
        bt_consultar.setVisibility(View.VISIBLE);
        bt_consultar.setClickable(true);
        tv_esperar.setText("Monto a pagar al dia de hoy: ");
        tv_esperar.setVisibility(View.VISIBLE);
        et_ID.requestFocus();
        tv_indicador.setVisibility(View.VISIBLE);
        tv_indicador.setText("Intereses moratorios: " + interes_mora_total + "\nAbono al capital: " + montoAPagar);
        bt_perdon.setVisibility(View.VISIBLE);


    }

    public void cambiar_fecha (View view) {

        /*final Calendar c = Calendar.getInstance();
        final boolean[] edad_permitida = {true};
        mes_selected = (c.get(Calendar.MONTH));
        //Toast.makeText(this, "mes selected: " + mes_selected, Toast.LENGTH_LONG).show();
        anio_selected = c.get(Calendar.YEAR);
        fecha_selected = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
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
                fecha_abono = (i2_s + "/" + i1_s + "/" + i_s);
                //edad_cliente.autofill(AutofillValue.forText(String.valueOf(i2) + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i)));
                mes_selected = i1+1;
                anio_selected = i;
                fecha_selected = i2;
                anio = String.valueOf(anio_selected);
                mes = String.valueOf(mes_selected);
                dia = String.valueOf(fecha_selected);
                flag_fecha = true;
                if (mes.length() == 1) {
                    mes = "0" + mes;
                }
                if (dia.length() == 1) {
                    dia = "0" + dia;
                }
                String fecha_nueva = anio + "-" + mes + "-" + dia;
                Date fecha_nueva_D = null;
                try {
                    fecha_nueva_D = DateUtilities.stringToDate(fecha_nueva);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                hoy_LD = fecha_nueva_D;
                Log.v("select_fecha", String.valueOf(fecha_selected) + "/" + String.valueOf(mes_selected + 1) + "/" + String.valueOf(anio_selected));
            }
        },anio_selected,mes_selected,fecha_selected);
        datePickerDialog.show();*/
    }

    private Integer obtener_monto_cuota (String s) {
        int flag = 0;
        String[] split = s.split("#");
        s = split[1];
        String archivos[] = fileList();
        Log.v("obt_monto_cuota", ".\n\nString: "+ s + "\n\nCliente ID: " + cliente_ID + "\n\nCantidd de archivos: " + archivos.length + "\n\nSplit[0]: " + split[0] + "\n\nSplit[1]: " + split[1] + "\n\n.");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("obtener_monto_cuota.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            //Do nothing.
        } else {
            for (int i = 0; i < archivos.length; i++) {
                Pattern pattern = Pattern.compile(cliente_ID + "_P_" + s + "_P_", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(archivos[i]);
                boolean matchFound = matcher.find();
                Log.v("buscando_archivos", ".\n\nFile: #" + i + ": " + archivos[i] + "\n\n.");
                if (matchFound) {
                    Log.v("buscando_files", ".\n\nArchivo encontrado.\nContenido del archivo:\n\n" + imprimir_archivo(archivos[i]) + "\n\n.");
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
                                Log.v("file_found", ".\n\nLinea: " + linea + "\n\n.");
                                String[] splitre = linea.split("_separador_");
                                if (splitre[0].equals("monto_cuota")) {
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
            String file_to_consult = "";
            if (flag_client_reciv) {
                file_to_consult = archivo_prestamo;
            } else {
                file_to_consult = archivo_prestamo;
            }
            if (file_to_consult.contains("*") || file_to_consult.contains(" ")) {
                Log.v("presentar_info_credito0", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                //Do nothing.
            } else {
                for (int i = 0; i < archivos.length; i++) {
                    Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(archivos[i]);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        //TODO: Abrir archivo y leerlo.
                        try {
                            String fecha_next_abono = "";
                            String intereses_mor_archivo = "";
                            String saldo_mas_intereses_s = "";
                            String plazoz = "";
                            Date fecha_credito = new Date();
                            String numero_de_credito = "";
                            String cuotas_morosas = "";
                            String valor_presentar_s = "";
                            String cuadratura_pre = "";
                            String cuadratura_bkup = "";
                            String monto_prestado = "";
                            int factor_semanas = 0;
                            String file_name = archivos[i];
                            String[] split_indice = file_name.split("_P_");
                            numero_de_credito = split_indice[1];
                            InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            while (linea != null) {
                                Log.v("presentar_info_credito1", ".\n\nlinea:\n\n" + linea + "\n\n.");
                                String[] split = linea.split("_separador_");
                                if (split[0].equals("proximo_abono")) {
                                    fecha_next_abono = split[1];
                                    Log.v("presentar_info_credito2", "Abonar.\n\nfecha_next_abono: " + fecha_next_abono + "\n\n.");
                                }
                                if (split[0].equals("monto_credito")) {
                                    monto_prestado = split[1];
                                }
                                if (split[0].equals("fecha_credito")) {
                                    String fecha_aux = split[1];
                                    String[] split_fecha_aux = fecha_aux.split("/");
                                    fecha_aux = split_fecha_aux[2] + "-" + split_fecha_aux[1] + "-" + split_fecha_aux[0];
                                    fecha_credito = DateUtilities.stringToDate(fecha_aux);
                                }
                                if (split[0].equals("plazo")) {
                                    plazoz = split[1];
                                }
                                if (split[0].equals("cuadratura")) {
                                    cuadratura_pre = split[1];
                                    cuadratura_bkup = cuadratura_pre;
                                }
                                if (split[0].equals("saldo_mas_intereses")) {
                                    saldo_mas_intereses_s = split[1];
                                }
                                if (split[0].equals("cuotas")) {
                                    cuotas_morosas = split[1];
                                    total_cuotas = Integer.parseInt(split[1]);
                                }
                                if (split[0].equals("intereses_moratorios")) {
                                    intereses_mor_archivo = split[1];
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
                            } else if (piezas[1].equals("meses")) {
                                factor_semanas = 4;
                            } else {
                                factor_semanas = -1;
                                //ERROR
                            }
                            String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                            Log.v("presentar_info_credito3", "Abonar.\n\nsaldo_plus: " + saldo_plus_s + "\n\nsaldo_mas_intereses: " + saldo_mas_intereses_s + "\n\nIntereses_moratorios: " + intereses_mor_archivo + "\n\n.");
                            Log.v("presentar_info_credito4", ".\n\nsaldo_plus_s: " + saldo_plus_s + "\n\n.");
                            String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                            Log.v("presentar_info_credito5", ".\n\nintereses_moritas: " + intereses_moritas + "\n\n.");
                            interes_mora_total = intereses_moritas;
                            interes_mora_parcial = interes_mora_total;
                            Log.v("presentar_info_credito6", ".\n\ninteres_mora_total: " + interes_mora_total + "\n\n.");
                            cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0, fecha_credito, "no");
                            Log.v("presentar_info_credito7", ".\n\ncuadratura_pre: " + cuadratura_pre + "\n\n.");
                            intereses_monroe = Integer.parseInt(intereses_mor_archivo);//Son los intereses guardados en el archivo. calculados en un periodo que se abono solo parte de los intereses.
                            Log.v("presentar_info_credito8", ".\n\nsaldo_mas_intereses_s: " + saldo_mas_intereses_s + "\n\n.");
                            saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor_archivo, monto_prestado);
                            Log.v("presentar_info_credito9", ".\n\ncuotas_morosas_pre: " + cuotas_morosas + "\n\n.");
                            cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup, fecha_next_abono);
                            Log.v("present_info_credito10", ".\n\ncuotas_morosas_post: " + cuotas_morosas + "\n\n.");
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
            }
        } else {
            String valor_presentar_s = s;
            String[] splitte = valor_presentar_s.split(" ");
            String num_credit = splitte[0];
            num_credit = num_credit.replace("#", "");
            String archivos[] = fileList();
            String file_to_consult = "";
            Log.v("present_info_credito11", "Abonar.\n\nnum_credit: " + num_credit + "\n\n.");
            if (flag_client_reciv) {
                file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            } else {
                file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            }

            if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
                Log.v("present_info_credito12", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                //Do nothing.
            } else {
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
                            Date fecha_credito = new Date();
                            String monto_prestado = "";
                            String cuadratura_bkup = "";
                            int factor_semanas = 0;
                            String file_name = archivos[i];
                            String[] split_indice = file_name.split("_P_");
                            numero_de_credito = split_indice[1];
                            InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            while (linea != null) {
                                Log.v("present_info_credito13", ".\n\nlinea:\n\n" + linea + "\n\n.");
                                String[] split = linea.split("_separador_");
                                if (split[0].equals("proximo_abono")) {
                                    fecha_next_abono = split[1];
                                    Log.v("present_info_credit13.5", ".\n\nfecha_next_abono:\n\n" + fecha_next_abono + "\n\n.");
                                }
                                if (split[0].equals("plazo")) {
                                    plazoz = split[1];
                                }
                                if (split[0].equals("monto_credito")) {
                                    monto_prestado = split[1];
                                }
                                if (split[0].equals("fecha_credito")) {
                                    String fecha_aux = split[1];
                                    Log.v("", "");
                                    String[] split_fecha_aux = fecha_aux.split("/");
                                    fecha_aux = split_fecha_aux[2] + "-" + split_fecha_aux[1] + "-" + split_fecha_aux[0];
                                    fecha_credito = DateUtilities.stringToDate(fecha_aux);
                                }
                                if (split[0].equals("saldo_mas_intereses")) {
                                    saldo_mas_intereses_s = split[1];
                                }
                                if (split[0].equals("cuotas")) {
                                    cuotas_morosas = split[1];
                                    total_cuotas = Integer.parseInt(split[1]);
                                }
                                if (split[0].equals("cuadratura")) {
                                    cuadratura_pre = split[1];
                                    cuadratura_bkup = cuadratura_pre;
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
                            } else if (piezas[1].equals("meses")) {
                                factor_semanas = 4;
                            } else {
                                factor_semanas = -1;
                                //ERROR
                            }

                            String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                            Log.v("present_info_credito14", "Abonar.\n\nsaldo_plus: " + saldo_plus_s + "\n\nsaldo_mas_intereses: " + saldo_mas_intereses_s + "\n\nIntereses_moratorios: " + intereses_mor + "\n\n.");
                            Log.v("present_info_credito15", ".\n\nsaldo_plus_s: " + saldo_plus_s + "\n\n.");
                            //String saldo_prestamo = obtener_monto_prestado();
                            String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                            Log.v("present_info_credito16", ".\n\nintereses_moritas: " + intereses_moritas + "\n\n.");
                            interes_mora_total = intereses_moritas;
                            interes_mora_parcial = interes_mora_total;
                            Log.v("present_info_credito17", ".\n\ninteres_mora_total: " + interes_mora_total + "\n\n.");
                            cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0, fecha_credito, "no");
                            intereses_monroe = Integer.parseInt(intereses_mor);
                            Log.v("present_info_credito18", ".\n\ncuadratura_pre: " + cuadratura_pre + "\n\n.");
                            saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor, monto_prestado);
                            Log.v("present_info_credito19", ".\n\nsaldo_mas_intereses_s: " + saldo_mas_intereses_s + "\n\n.");
                            cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup, fecha_next_abono);
                            Log.v("present_info_credito20", ".\n\ncuotas_morosas: " + cuotas_morosas + "\n\n.");


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
                            //tv_indicador.setVisibility(View.INVISIBLE);
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
    }

    private String obtener_intereses_moratorios (String saldo_plus, String next_pay) throws ParseException {
        String flag = "";
        String saldo = "";
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Log.v("obt_int_morat0", ".\n\nProximo abono: " + proximo_abono_formato + "\n\n.");
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        int diferencia_en_dias = DateUtilities.daysBetween(hoy_LD, proximo_abono_LD);
        Log.v("obt_int_morat1", ".\n\nDiferencia en dias: " + diferencia_en_dias + "\n\nfecha_hoy: " + hoy_LD.toString() + "\n\nProximo abono: " + proximo_abono_LD + "\n\n.");
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            saldo = saldo_plus;
            morosidad = "D";
            interes_mora_parcial = "0";
        } else {//Significa que esta atrazado!!!

            //saldo = String.valueOf(Integer.parseInt(saldo_plus) + (diferencia_en_dias * ((Integer.parseInt(interes_mora))/100) * Integer.parseInt(saldo_plus)));//No se suman intereses sobre los intereses moratorios, pero si sobre el interes acordado del credito!!!
            Log.v("obt_int_morat_late1", ".\n\nDiferencia en dias: " + diferencia_en_dias + "\n\ninteres_mora: " + interes_mora + "\n\nSaldo_plus: " + saldo_plus + "\n\n.");
            double pre_num0 = diferencia_en_dias * (Integer.parseInt(interes_mora)) * (Integer.parseInt(saldo_plus));
            double pre_num = pre_num0 / 100;
            int pre_num_int = (int) pre_num;
            Log.v("obt_int_morat_late2", ".\n\npre_num_int: " + pre_num_int + "\n\n.");
            if (pre_num_int > 0) {
                morosidad = "M";
                interes_mora_parcial = String.valueOf(pre_num_int);
            } else {
                interes_mora_parcial = "0";
            }

        }
        flag = interes_mora_parcial;
        interes_mora_total = interes_mora_parcial;
        Log.v("obt_int_morat3", "Abonar.\n\nintereses moratorios: " + interes_mora_parcial + "\n\n.");
        return flag;
    }

    private void actualizar_caja (int monto_ingresado) {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            long monto_nuevo = Integer.parseInt(split[1]) + monto_ingresado;
            linea = linea.replace(split[1], String.valueOf(monto_nuevo));
            br.close();
            archivo.close();
            borrar_archivo(caja);
            crear_archivo(caja);
            guardar(linea, caja);
            crear_archivo("cajax_caja_.txt");
            borrar_archivo("cajax_caja_.txt");
            crear_archivo("cajax_caja_.txt");
            linea = linea.replace(" ", "_separador_");
            guardar(linea, "cajax_caja_.txt");
            subir_caja();
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
    }

    private void subir_caja () throws JSONException, IOException {
        String sp_creditos = "";
        String cobrador_ID_S = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] splitr = linea.split(" ");
            cobrador_ID_S = splitr[0];
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

        String archivos[] = fileList();
        Log.v("subir_caja1", ".\n\nAbonar. \n\nTotal de archivos: " + archivos.length + "\n\n.");
        Log.v("subir_caja2", "Abonar.\n\ncobrador_ID_S: " + cobrador_ID_S + "\n\n");

        subir_caja2(sp_creditos);
    }

    private void subir_caja2 (String sp_creditos) throws JSONException {
        String spid = sp_creditos;
        String json_string = "";
        JSONObject jsonObject = new JSONObject();
        String sheet = "caja";
        String id_caja = "";
        Log.v("subir_caja20", "Abonar.\n\nfile: " + "cajax_caja_.txt" + "\n\ncontenido del archivo:\n\n" + imprimir_archivo("cajax_caja_.txt"));
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("cajax_caja_.txt"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null && !linea.equals("")) {
                String[] split = linea.split("_separador_");
                Log.v("subir_caja21", "Abonar.\n\nLinea:\n\n" + linea + "\n\n.");
                json_string = json_string + split[1] + "_n_";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("subir_caja22", "Abonar.\n\njson_string: " + "\n\n" + json_string + "\n\n.");
        jsonObject = TranslateUtil.string_to_Json(json_string, spid, sheet, id_caja);
        subir_nuevo_caj(jsonObject, "cajax_caja_.txt");
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
                if (tv_esperar.getText().toString().equals("Digite el monto del abono")) {

                    bt_consultar.setClickable(false);
                    bt_consultar.setEnabled(false);
                    et_ID.setEnabled(true);
                    et_ID.setVisibility(View.VISIBLE);
                    et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();
                    //tv_indicador.setVisibility(View.INVISIBLE);

                    if (String.valueOf(s).equals("")) {
                        //Do nothing.
                    } else {
                        bt_consultar.setClickable(true);
                        bt_consultar.setEnabled(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
                    et_ID.setEnabled(true);
                    et_ID.setFocusableInTouchMode(true);
                    //et_ID.setText("");
                    et_ID.requestFocus();
                    //bt_consultar.setClickable(false);
                    //bt_consultar.setEnabled(false);
                    String archivos[] = fileList();

                    if (et_ID.getText().toString().contains("*") || et_ID.getText().toString().contains(" ")) {
                        Log.v("text_listener0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                        //Do nothing.
                    } else {
                        for (int i = 0; i < archivos.length; i++) {
                            Pattern pattern = Pattern.compile(et_ID.getText().toString(), Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(archivos[i]);
                            //Log.v("text_listener_identifi", ".\n\narchivos[" + i + "]: " + archivos[i] + "\n\n.");
                            boolean matchFound = matcher.find();
                            if (matchFound) {
                                if (s.length() >= 9) {
                                    bt_consultar.setEnabled(true);
                                    bt_consultar.setClickable(true);
                                }
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

    private void salir (String s) {
        if (activity_volver.equals("MenuPrincipal")) {
            Intent activity_volver = new Intent(this, MenuPrincipal.class);
            activity_volver.putExtra("mensaje", s);
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else if (activity_volver.equals("Estado_cliente")) {
            Intent activity_volver = new Intent(this, Estado_clienteActivity.class);
            activity_volver.putExtra("mensaje", s);
            Log.v("salir0", "Abonar.\n\ncliente_recibido para devolver: " + cliente_recibido + "\n\n.");
            activity_volver.putExtra("cliente_ID", cliente_recibido);
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else {
            //Do nothing.
        }
    }

    public  void borrar_archivo (String file) throws IOException {
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

    private void separar_fechaYhora (){
        llenar_mapa_meses();
        String ahora = hoy_LD.toString();
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

    private void llenar_mapa_meses () {
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

    private void crear_archivo (String nombre_archivo) {
        try{
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(nombre_archivo, Activity.MODE_PRIVATE));
            archivo.flush();
            archivo.close();
        }catch (IOException e) {
        }
    }

    @Override
    public void onBackPressed (){
        boton_atras();
    }

    private void boton_atras () {
        if (activity_volver.equals("MenuPrincipal")) {
            Intent activity_volver = new Intent(this, MenuPrincipal.class);
            activity_volver.putExtra("mensaje", "");
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else if (activity_volver.equals("Estado_cliente")) {
            Intent activity_volver = new Intent(this, Estado_clienteActivity.class);
            activity_volver.putExtra("mensaje", "");
            Log.v("salir0", "Abonar.\n\ncliente_recibido para devolver: " + cliente_recibido + "\n\n.");
            activity_volver.putExtra("cliente_ID", cliente_recibido);
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else {
            //Do nothing.
        }
    }

    private void mostrar_todito () {
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.VISIBLE);
        et_ID.setEnabled(true);
        et_ID.setClickable(true);
        tv_indicador.setVisibility(View.VISIBLE);
    }

    private void ocultar_todito () {
        Log.v("ocultar_todito", "Se hace todo invisible");
        tv_esperar.setVisibility(View.VISIBLE);
        et_ID.setEnabled(false);
        et_ID.setClickable(false);
        tv_esperar.setText("conectando, por favor espere...");
        bt_consultar.setVisibility(View.INVISIBLE);
        tv_indicador.setVisibility(View.INVISIBLE);
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
            }
        }
        return contenido;
    }

    //Metodos comunes online//

    private boolean verificar_internet () {
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
        String id_credito = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null && !linea.equals("")) {
                String[] split = linea.split("_separador_");
                Log.v("subir_archivo", ".\n\nLinea:\n\n" + linea + "\n\n.");
                json_string = json_string + split[1] + "_n_";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.v("json_string_debug", ".\n\njson_string: " + "\n\n" + json_string + "\n\n.");
        jsonObject = TranslateUtil.string_to_Json(json_string, spid, sheet_abonos, id_credito);
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
                            if (length_split > 3) {//
                                for (int i = 0; i < length_split; i++) {
                                    Log.v("split[" + i + "]", split[i]);
                                }
                                if (split[23].equals(credit_ID)) {//
                                    cambiar_bandera1(file);

                                } else {
                                    Log.v("onResponse_nuev_cred", "Error al subir informacion del credito al servidor.");
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
            presentar_cuadratura();
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
            presentar_cuadratura();
            Log.v("camb_band_nuev_cred", "\"Credito generado y registrado correctamente en el servidor.\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void cambiar_bandera2 (String file) {
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
            //mostrar_todito();
            Log.v("cambiar_band_result", "\n\nArchivo \"onlines.txt\":\n\n" + imprimir_archivo(onlines));
            //presentar_cuadratura();
            Log.v("camb_band_nuev_cred", "\"Credito generado y registrado correctamente en el servidor.\"");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
