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
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.text.method.NumberKeyListener;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Nuevo_creditoActivity extends AppCompatActivity {

    private boolean flag_cuotass = false;
    private boolean flag_sema_quince_mes = false;
    private boolean flag_interess = false;
    private String activity_devolver;
    private String cuotass_S = "";
    private String sema_quince_mes_S = "";
    private String interess_S = "";
    private String cliente_Id_volver;
    private Spinner sp_cuotas;
    private Spinner sp_tipo_cobro;
    private Spinner sp_interes;
    private Button bt_personalizar;
    private Integer monto_credito = 0;
    private String nombre_cliente = "";
    private String apellido_cliente = "";
    private Integer monto_cuota = 0;
    private String fecha_pago = "";//Fecha que debe pagar la proxima cuota.
    private Integer saldo_mas_intereses = 0;
    private Integer tasa = 0;//Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
    private String ID_credito = "";
    private String plazo = "";//Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%) Se elige con un spinner
    private String cuadratura = "";
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
    private Button bt_consultar;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private String monto_disponible = "";
    private Spinner sp_plazos;
    private boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private String caja = "caja.txt";
    private TextView tv_caja;
    private String credit_ID = "";
    private String interes_mora = "";
    private String puntuacion_cliente = "";
    private Button bt_cambiar_fecha;
    private Integer mes_selected = 0;
    private Integer anio_selected = 0;
    private Integer fecha_selected = 0;
    private String  fecha_credito = "";
    private boolean flag_fecha = false;
    private Date hoy_LD = new Date();
    private String fecha_hoy_hoy = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_credito);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        if (mensaje_recibido.equals("")) {
            //Do nothing.
        } else {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        bt_consultar = (Button) findViewById(R.id.bt_consultar);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        bt_cambiar_fecha = (Button) findViewById(R.id.bt_cambiar_fecha);
        bt_cambiar_fecha.setText("Cambiar fecha");
        bt_cambiar_fecha.setVisibility(View.INVISIBLE);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        sp_plazos = (Spinner) findViewById(R.id.sp_plazos);
        sp_plazos.setVisibility(View.INVISIBLE);
        tv_saludo.setText("NUEVO CREDITO");
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        sp_cuotas = (Spinner) findViewById(R.id.sp_cuotas);
        sp_tipo_cobro = (Spinner) findViewById(R.id.sp_tipo_cobro);
        sp_interes = (Spinner) findViewById(R.id.sp_interes);
        bt_personalizar = (Button) findViewById(R.id.bt_personalizar);
        activity_devolver = getIntent().getStringExtra("activity_devolver");
        hoy_LD = Calendar.getInstance().getTime();
        Log.v("OnCreate0", "Nuevo_credito.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        String fecha_hoy_string = DateUtilities.dateToString(hoy_LD);
        Log.v("OnCreate1", "Nuevo_credito.\n\nFecha hoy: " + fecha_hoy_string + "\n\n.");
        cliente_Id_volver = cliente_recibido;
        try {
            hoy_LD = DateUtilities.stringToDate(fecha_hoy_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        Log.v("OnCreate2", "Nuevo_credito.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");

        tv_caja.setHint("Caja...");
        mostrar_caja();
        separar_fechaYhora();
        fecha_hoy_hoy = fecha;
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
            try {
                consultar(null);
            } catch (JSONException | IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
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
                int hoy_fecha = Integer.parseInt(fecha_hoy_hoy);
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
            crear_archivo("cierre.txt");
            borrar_archivo("cierre.txt");
            crear_archivo("cierre.txt");
            agregar_linea_archivo("fecha " + fecha_hoy_hoy, "cierre.txt");
        }
        if (flag_borrar) {
            borrar_archivo("cierre.txt");
            crear_archivo("cierre.txt");
            agregar_linea_archivo("fecha " + fecha_hoy_hoy, "cierre.txt");
        }

        /////////////////////////////////////////////////////////////////////////////////////

    }

    public void cambiar_fecha (View view) {

        final Calendar c = Calendar.getInstance();
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
                fecha_credito = (i2_s + "/" + i1_s + "/" + i_s);
                //edad_cliente.autofill(AutofillValue.forText(String.valueOf(i2) + "/" + String.valueOf(i1+1) + "/" + String.valueOf(i)));
                mes_selected = i1+1;
                anio_selected = i;
                fecha_selected = i2;
                flag_fecha = true;
                Log.v("select_fecha", String.valueOf(fecha_selected) + "/" + String.valueOf(mes_selected + 1) + "/" + String.valueOf(anio_selected));
            }
        },anio_selected,mes_selected,fecha_selected);
        datePickerDialog.show();
    }

    private void restar_disponible () {//TODO: Se debe actualizar la informacion en internet.
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
                    nuevo_monto = Integer.parseInt(monto_disponible) - (monto_credito);
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
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void consultar (View view) throws JSONException, IOException, ParseException {
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        if (tv_esperar.getText().toString().equals("Escoja el plazo del credito")) {
            restar_disponible();
            generar_credito();
        } else if (tv_esperar.getText().toString().equals("Digite el monto del credito")) {//TODO: Rechazar si supera el monto disponible del cliente.
            monto_credito = Integer.parseInt(et_ID.getText().toString());
            Log.v("monto_credito", ".\n\nmonto_credito: " + monto_credito + "\n\n.");
            et_ID.setText("");
            et_ID.setFocusableInTouchMode(false);
            et_ID.setEnabled(false);
            et_ID.setVisibility(View.INVISIBLE);
            bt_consultar.setVisibility(View.INVISIBLE);
            tv_esperar.setText("");
            tv_esperar.setVisibility(View.INVISIBLE);
            sp_plazos.setVisibility(View.VISIBLE);
            obtener_plazo();
        } else if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
            String archivos[] = fileList();
            String archivoCompleto = "";
            String file_to_consult = "";
            if (flag_client_reciv) {
                file_to_consult = cliente_recibido + "_C_";
            } else {
                file_to_consult = et_ID.getText().toString() + "_C_";
            }
            if (file_to_consult.contains("*") || file_to_consult.contains(" ")) {
                Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                //Do nothing.
            } else {
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
                                if (split[0].equals("nombre_cliente")) {
                                    nombre_cliente = split[1];
                                }
                                if (split[0].equals("apellido1_cliente")) {
                                    apellido_cliente = split[1];
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
                //No se encontro el cliente
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
                text_listener();
            } else {
                Toast.makeText(this, "Cliente encontrado", Toast.LENGTH_SHORT).show();
                et_ID.setText("");
                et_ID.setFocusableInTouchMode(false);
                et_ID.setEnabled(false);
                et_ID.setVisibility(View.INVISIBLE);
                bt_consultar.setVisibility(View.INVISIBLE);
                tv_esperar.setText("");
                tv_esperar.setVisibility(View.INVISIBLE);
                //Aqui se llama al metodo principal.
                nuevo_credito();
            }
        } else {
            //TODO: no se sabe que hacer aqui!!!
        }
    }

    private void llenar_spinner () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        String plazos = "Escoja el plazo del credito_5 semanas (20%)_6 semanas (20%)_9 semanas (40%)_3 quincenas (25%)_5 quincenas (40%)";
        String[] split = plazos.split("_");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.custom_spinner, split);
        sp_plazos.setAdapter(adapter2);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        spinner_listener();
    }

    private void spinner_listener () {
        sp_plazos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = sp_plazos.getSelectedItem().toString();
                        if (seleccion.equals("Escoja el plazo del credito")) {
                            bt_consultar.setClickable(false);
                            bt_consultar.setEnabled(false);
                            //Do nothing!
                        }else {
                            plazo = sp_plazos.getSelectedItem().toString();
                            bt_consultar.setEnabled(true);
                            bt_consultar.setClickable(true);
                            bt_consultar.setVisibility(View.VISIBLE);
                            Log.v("spinner_listener0", "Nuevo_credito.\n\nplazo: " + plazo + "\n\n.");
                            //bt_consultar.setFocusableInTouchMode(true);
                            //bt_consultar.requestFocus();
                        }
                        bt_personalizar.setEnabled(true);
                        bt_personalizar.setClickable(true);
                        bt_personalizar.setVisibility(View.VISIBLE);
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private String calcular_cuota () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        String flag = "";
        String[] split = plazo.split(" ");
        Log.v("calcular_cuota0", "Nuevo_credito.\n\nPlazo: " + plazo + "\n\n.");
        String intereses_S = split[2].replace("(", "");
        intereses_S = intereses_S.replace(")", "");
        intereses_S = intereses_S.replace("%", "");
        intereses_S = intereses_S.replace("\n", "");
        intereses_S = intereses_S.replace(",", "");
        intereses_S = intereses_S.replace(" ", "");
        Log.v("calcular_cuota1", "Nuevo_credito.\n\nintereses_S: " + intereses_S + "\n\n.");
        int interes = Integer.parseInt(intereses_S);
        String cuotas_S = split[0];
        cuotas_S = cuotas_S.replace("semanas", "");
        cuotas_S = cuotas_S.replace("meses", "");
        cuotas_S = cuotas_S.replace("quincenas", "");
        cuotas_S = cuotas_S.replace(" ", "");
        cuotas_S = cuotas_S.replace(",", "");
        cuotas_S = cuotas_S.replace("\n", "");
        int cuotas = Integer.parseInt(cuotas_S);
        double monto_parcial = (monto_credito * interes);
        monto_parcial = monto_parcial / 100;
        double monto_total = monto_credito + monto_parcial;
        double cuota = monto_total / cuotas;
        int flag_int = (int) cuota;
        Log.v("calcular_cuota0", "Nuevo_credito.\n\nMonto total: " + monto_total + "\n\nMonto del credito: " + monto_credito + "\n\n.");
        flag = String.valueOf(flag_int);
        Log.v("calcular_cuota1","Nuevo_credito.\n\nFlag: " + flag + "\n\n.");
        return flag;
    }

    private String calcular_saldo () {
        String flag = "";
        String[] split = plazo.split(" ");
        String intereses_S = split[2].replace("(", "");
        intereses_S = intereses_S.replace(")", "");
        intereses_S = intereses_S.replace("%", "");
        intereses_S = intereses_S.replace("\n", "");
        intereses_S = intereses_S.replace(",", "");
        intereses_S = intereses_S.replace(" ", "");
        int interes = Integer.parseInt(intereses_S);
        double monto_parcial = (monto_credito * interes);
        monto_parcial = monto_parcial / 100;
        double monto_total = monto_credito + monto_parcial;
        int flag_int = (int) monto_total;
        flag = String.valueOf(flag_int);
        return flag;
    }

    private String obtener_tasa () {
        String flag = "";
        String[] split = plazo.split(" ");
        String intereses_S = split[2].replace("(", "");
        intereses_S = intereses_S.replace(")", "");
        intereses_S = intereses_S.replace("%", "");
        intereses_S = intereses_S.replace("\n", "");
        intereses_S = intereses_S.replace(",", "");
        intereses_S = intereses_S.replace(" ", "");
        int interes = Integer.parseInt(intereses_S);
        flag = String.valueOf(interes);
        return flag;
    }

    private String calcular_cuotas () {
        String flag = "";
        String[] split = plazo.split(" ");
        String cuotas_S = split[0];
        cuotas_S = cuotas_S.replace("semanas", "");
        cuotas_S = cuotas_S.replace("meses", "");
        cuotas_S = cuotas_S.replace("quincenas", "");
        cuotas_S = cuotas_S.replace(" ", "");
        cuotas_S = cuotas_S.replace(",", "");
        cuotas_S = cuotas_S.replace("\n", "");
        int cuotas = Integer.parseInt(cuotas_S);
        flag = String.valueOf(cuotas);
        return flag;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private String obtener_proximo_abono () throws ParseException {
        String flag = "";

        int factor_semanas = 0;
        String[] piezas = plazo.split(" ");
        if (piezas[1].equals("quincenas")) {
            factor_semanas = 2;
        } else if (piezas[1].equals("semanas")) {
            factor_semanas = 1;
        } else if (piezas[1].equals("meses")) {
            factor_semanas = 4;
        } else {
            factor_semanas = -1;
            //flag = "ERROR";
        }

        Log.v("obt_prox_abo0", "Nuevo_credito.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        String hoy_LD_string = DateUtilities.dateToString(hoy_LD);
        Log.v("obt_prox_abo1", "Nuevo_credito.\n\nFecha hoy: " + hoy_LD_string + "\n\n.");
        hoy_LD = DateUtilities.stringToDate(hoy_LD_string);
        Log.v("obt_prox_abo2", "Nuevo_credito.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        Date fecha_mostrar_D = new Date();
        if (factor_semanas == 2) {
            Log.v("Obt_prox_abo3F2", "Nuevo_credito.\n\nfecha_mostrar_D_pre:\n\n" + fecha_mostrar_D + "\n\n");
            fecha_mostrar_D = DateUtilities.addQuincenas(hoy_LD, 1, hoy_LD);
            Log.v("Obt_prox_abo4F2", "Nuevo_credito.\n\nfecha_mostrar_D_post:\n\n" + fecha_mostrar_D + "\n\n");
        } else if (factor_semanas == 4) {
            Log.v("Obt_prox_abo3F3", "Nuevo_credito.\n\nfecha_mostrar_D_pre:\n\n" + fecha_mostrar_D + "\n\n");
            fecha_mostrar_D = DateUtilities.addMonths(hoy_LD, 1);
            Log.v("Obt_prox_abo4F3", "Nuevo_credito.\n\nfecha_mostrar_D_post:\n\n" + fecha_mostrar_D + "\n\n");
        } else {
            Log.v("Obt_prox_abo3F4", "Nuevo_credito.\n\nfecha_mostrar_D_pre:\n\n" + fecha_mostrar_D + "\n\n");
            fecha_mostrar_D = DateUtilities.addWeeks(hoy_LD, 1);
            Log.v("Obt_prox_abo4F3", "Nuevo_credito.\n\nfecha_mostrar_D_post:\n\n" + fecha_mostrar_D + "\n\n");
        }
        Log.v("Obt_prox_abo5", "Nuevo_credito.\n\nfecha_mostrar_D:\n\n" + fecha_mostrar_D + "\n\n");
        String fecha_mostrar2 = DateUtilities.dateToString(fecha_mostrar_D);
        String[] partes = fecha_mostrar2.split("-");
        fecha_mostrar2 = partes[2] + "/" + partes[1] + "/" + partes[0];
        flag = fecha_mostrar2;
        Log.v("Obt_prox_abo6", "Nuevo_credito.\n\nfecha_mostrar2:\n\n" + fecha_mostrar2 + "\n\n");
        return flag;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void generar_credito () throws IOException, JSONException, ParseException {
        String file_content = "";
        file_content = file_content + "monto_credito_separador_" + monto_credito + "\n";
        String plazo_presentar = plazo.replace(" ", "_");
        file_content = file_content + "plazo_separador_" + plazo_presentar + "\n";
        String monto_cuota = calcular_cuota();
        file_content = file_content + "monto_cuota_separador_" + monto_cuota + "\n";
        String fecha_sustituta = "";
        //fecha_hoy = DateUtilities.stringToDate(fecha_sustituta);
        String sema_quince = "";
        int factor = 0;
        String[] split = plazo_presentar.split("_");
        if (split[1].equals("semanas")) {
            sema_quince = "semana";
            factor = 1;
        } else if (split[1].equals("quincenas")) {
            sema_quince = "quincena";
            factor = 2;
        } else if (split[1].equals("meses")) {
            sema_quince = "mes";
            factor = 4;
        } else {
            //do nothing here!!
        }
        boolean flag_proximo_abono = true;
        if (flag_fecha) {
            dia = String.valueOf(fecha_selected);
            if (dia.length() == 1) {
                dia = "0" + dia;
            }
            mes = String.valueOf(mes_selected);
            if (mes.length() == 1) {
                mes = "0" + mes;
            }
            anio = String.valueOf(anio_selected);
            fecha_sustituta = anio + "-" + mes + "-" + dia;
            Date fecha_hoy = DateUtilities.stringToDate(fecha_sustituta);
            String fecha_creditt = anio + "-" + mes + "-" + dia;
            Date fecha_creditt_D = DateUtilities.stringToDate(fecha_creditt);
            if (factor == 2) {
                fecha_hoy = DateUtilities.addQuincenas(fecha_hoy, 1, fecha_creditt_D);
            } else if (factor == 4) {
                fecha_hoy = DateUtilities.addMonths(fecha_hoy, 1);
            } else {
                fecha_hoy = DateUtilities.addWeeks(fecha_hoy, 1);
            }
            fecha_sustituta = DateUtilities.dateToString(fecha_hoy);
            String[] split_fecha = fecha_sustituta.split("-");
            fecha_sustituta = split_fecha[2] + "/" + split_fecha[1] + "/" + split_fecha[0];
            flag_proximo_abono = false;
        }
        String fecha_credit = dia + "/" + mes + "/" + anio;
        String proximo_abono = "";
        file_content = file_content + "fecha_credito_separador_" + fecha_credit + "\n";
        if (flag_proximo_abono) {
            proximo_abono = obtener_proximo_abono();
            Log.v("generar_credito0", "Nuevo_credito.\n\nproximo_abono: " + proximo_abono + "\n\n.");
        } else {
            proximo_abono = fecha_sustituta;
        }
        Log.v("generar_credito1", "Nuevo_credito.\n\nproximo_abono: " + proximo_abono + "\n\n.");
        file_content = file_content + "proximo_abono_separador_" + proximo_abono + "\n";
        String saldo_mas_intereses = calcular_saldo();
        file_content = file_content + "saldo_mas_intereses_separador_" + saldo_mas_intereses + "\n";
        String tasa_interes = obtener_tasa();
        file_content = file_content + "tasa_separador_" + tasa_interes + "\n";
        String cuotass = calcular_cuotas();
        file_content = file_content + "cuotas_separador_" + cuotass + "\n";
        credit_ID = obtener_id();
        file_content = file_content + "ID_credito_separador_" + credit_ID + "\n";
        String morosidad = "D";
        file_content = file_content + "morosidad_separador_" + morosidad + "\n";
        Date fecha_hoy = hoy_LD;
        if (flag_fecha) {
            dia = String.valueOf(fecha_selected);
            if (dia.length() == 1) {
                dia = "0" + dia;
            }
            mes = String.valueOf(mes_selected);
            if (mes.length() == 1) {
                mes = "0" + mes;
            }
            anio = String.valueOf(anio_selected);
            fecha_sustituta = anio + "-" + mes + "-" + dia;
            fecha_hoy = DateUtilities.stringToDate(fecha_sustituta);
        }
        Date fecha_poner = fecha_hoy;
        for (int i = 0; i < Integer.parseInt(cuotass); i++) {
            if (factor == 2) {
                fecha_poner = DateUtilities.addQuincenas(fecha_poner, 1, fecha_hoy);
            } else if (factor == 4) {
                fecha_poner = DateUtilities.addMonths(fecha_poner, 1);
            } else {
                fecha_poner = DateUtilities.addWeeks(fecha_poner, 1);
            }
            String fecha_poner_S = DateUtilities.dateToString(fecha_poner);
            String[] splet = fecha_poner_S.split("-");
            Log.v("generar_credito", ".\n\nFecha_poner: " + String.valueOf(fecha_poner) + "\n\n.");
            String fecha_S_poner = splet[2] + "/" + splet[1] + "/" + splet[0];
            cuadratura = cuadratura + sema_quince + "_" + String.valueOf(i + 1) + "_" + monto_cuota + "_" + fecha_S_poner + "__";
        }
        file_content = file_content + "cuadratura_separador_" + cuadratura + "\n";
        file_content = file_content + "intereses_moratorios_separador_0\n";
        file_content = file_content + "monto_abono_separador_0";
        String file_name = credit_ID + ".txt";
        crear_archivo(file_name);
        guardar(file_content, file_name);
        actualizar_caja();
        actualizar_cierre(monto_credito, obtener_caja(), credit_ID);
        Log.v("antes_de_subir", ".\n\nArchivo a subir: " + file_name + "\n\nContenido de " + file_name + ":\n\n" + imprimir_archivo(file_name) + "\n\n.");
        subir_archivo(file_name);
    }

    private void actualizar_cierre (Integer monto_credito, Integer saldo_caja, String credit_ID) {
        String linea_cierre = "credito " + String.valueOf(monto_credito) + " " + saldo_caja + " " + credit_ID;
        agregar_linea_archivo(linea_cierre, "cierre.txt");
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

    private void actualizar_caja () {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            long monto_nuevo = Integer.parseInt(split[1]) - monto_credito;
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

    private String obtener_id () {
        String flag = "";
        String cliente_file = cliente_ID + "_C_.txt";
        String lista_archivos = "";
        String archivos[] = fileList();
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
            for (int i = 0; i < archivos.length; i++) {
                Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(archivos[i]);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    lista_archivos = lista_archivos + archivos[i] + "_sep_";
                }
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

    private void nuevo_credito () {
        //Algoritmo principal
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("Digite el monto del credito");
        bt_cambiar_fecha.setVisibility(View.VISIBLE);
        et_ID.setEnabled(true);
        et_ID.setVisibility(View.VISIBLE);
        et_ID.requestFocus();
        et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_ID.setClickable(true);
        et_ID.setText("");
        et_ID.setHint("Monto solicitado...");
        et_ID.setFocusableInTouchMode(true);
        et_ID.requestFocus();
        bt_consultar.setText("CONFIRMAR");
        bt_consultar.setVisibility(View.VISIBLE);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        text_listener();
    }

    private void obtener_plazo () {
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("Escoja el plazo del credito");
        bt_consultar.setText("CONFIRMAR");
        llenar_spinner();
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
                if (tv_esperar.getText().toString().equals("Digite el monto del credito")) {
                    et_ID.setVisibility(View.VISIBLE);
                    et_ID.setEnabled(true);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();
                    bt_consultar.setClickable(false);
                    bt_consultar.setEnabled(false);
                    if (String.valueOf(s).equals("")) {
                        et_ID.setEnabled(false);
                    } else {
                        boolean flag1 = false;
                        boolean flag2 = false;
                        Log.v("text_listener", ".\n\nMonto disponible: " + monto_disponible + "\n\nmonto solicitado: " + String.valueOf(s) + "\n\ns:\n\n-->" + s + "<--\n\n.");
                        if ((Integer.parseInt(String.valueOf(s))) > Integer.parseInt(monto_disponible)) {
                            flag1 = false;
                        } else {
                            flag1 = true;
                        }
                        try {
                            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            String[] split = linea.split(" ");
                            Log.v("leer_caja.txt", ".\n\nLinea: " + linea + "\n\nMonto credito: " + monto_credito + "\n\nMonto caja: " + split[1] + "\n\n.");
                            if (Integer.parseInt(String.valueOf(s)) > Integer.parseInt(split[1])) {
                                flag2 = false;
                            } else {
                                flag2 = true;
                            }
                            br.close();
                            archivo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        if (flag1 & flag2) {
                            bt_consultar.setClickable(true);
                            bt_consultar.setEnabled(true);
                        } else {
                            bt_consultar.setClickable(false);
                            bt_consultar.setEnabled(false);
                            if (flag1) {
                                msg("Monto en caja es insuficiente para realizar el credito!");
                            }
                            if (flag2) {
                                msg("Cliente no posee suficiente credito!");
                                msg("Intente con un monto menor");
                            }
                        }
                    }
                } else if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
                    et_ID.setEnabled(true);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();
                    String archivos[] = fileList();
                    if (et_ID.getText().toString().contains("*") || et_ID.getText().toString().contains(" ")) {
                        Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                    } else {
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
        Date now = hoy_LD;
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
        sp_plazos.setVisibility(View.VISIBLE);
        sp_plazos.setClickable(true);
        sp_plazos.setEnabled(true);
        bt_personalizar.setVisibility(View.VISIBLE);
        bt_cambiar_fecha.setVisibility(View.VISIBLE);
    }

    private void ocultar_todito() {
        Log.v("ocultar_todito", "Se hace todo invisible");
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("conectando, por favor espere...");
        bt_consultar.setVisibility(View.INVISIBLE);
        sp_plazos.setVisibility(View.INVISIBLE);
        bt_personalizar.setVisibility(View.INVISIBLE);
        bt_cambiar_fecha.setVisibility(View.INVISIBLE);
    }

    public void personalizar (View v) {
        flag_cuotass = false;
        flag_interess = false;
        flag_sema_quince_mes = false;
        bt_personalizar.setClickable(false);
        bt_personalizar.setEnabled(false);
        bt_personalizar.setVisibility(View.INVISIBLE);
        sp_cuotas.setVisibility(View.VISIBLE);
        sp_interes.setVisibility(View.VISIBLE);
        sp_tipo_cobro.setVisibility(View.VISIBLE);
        sp_plazos.setClickable(false);
        sp_plazos.setEnabled(false);
        llenar_spinner1();
        llenar_spinner2();
        llenar_spinner3();
    }

    private void llenar_spinner1 () {
        String cuotass = "Cuotas_2_3_4_5_6_7_8_9_10_11_12_13_14_15_16_17_18_19_20_21";
        String[] split = cuotass.split("_");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, split);
        sp_cuotas.setAdapter(adapter);
        sp_listener1();
    }

    private void llenar_spinner2 () {
        String sema_quince_mes = "Periodo_semanas_quincenas_meses";
        String[] split = sema_quince_mes.split("_");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, split);
        sp_tipo_cobro.setAdapter(adapter);
        sp_listener2();
    }

    private void llenar_spinner3 () {
        String interess = "Interes_5%_10%_15%_20%_25%_30%_35%_40%_45%_50%_55%_60%_65%_70%_75%_80%_85%_90%_95%";
        String[] split = interess.split("_");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, split);
        sp_interes.setAdapter(adapter);
        sp_listener3();
    }

    private void sp_listener1 () {
        sp_cuotas.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = sp_cuotas.getSelectedItem().toString();
                        if (seleccion.equals("Coutas")) {
                            //Do nothing!
                        }else {
                            flag_cuotass = true;
                            cuotass_S = sp_cuotas.getSelectedItem().toString();
                            if (flag_interess & flag_sema_quince_mes & flag_cuotass) {
                                confirm_new_credit_config();
                            } else {
                                //Do nothing.
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void sp_listener2 () {
        sp_tipo_cobro.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = sp_tipo_cobro.getSelectedItem().toString();
                        if (seleccion.equals("Periodo")) {
                            //Do nothing!
                        }else {
                            flag_sema_quince_mes = true;
                            sema_quince_mes_S = sp_tipo_cobro.getSelectedItem().toString();
                            if (flag_interess & flag_sema_quince_mes & flag_cuotass) {
                                confirm_new_credit_config();
                            } else {
                                //Do nothing.
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void sp_listener3 () {
        sp_interes.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = sp_interes.getSelectedItem().toString();
                        if (seleccion.equals("Interes")) {
                            //Do nothing!
                        }else {
                            flag_interess = true;
                            interess_S = sp_interes.getSelectedItem().toString();
                            if (flag_interess & flag_sema_quince_mes & flag_cuotass) {
                                confirm_new_credit_config();
                            } else {
                                //Do nothing.
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void confirm_new_credit_config () {
        sp_interes.setVisibility(View.INVISIBLE);
        sp_tipo_cobro.setVisibility(View.INVISIBLE);
        sp_cuotas.setVisibility(View.INVISIBLE);
        llenar_spinner_aux();
    }

    private void llenar_spinner_aux () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        String plazos = cuotass_S + " " + sema_quince_mes_S + " (" + interess_S + ")";
        String[] split = plazos.split("_");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, R.layout.custom_spinner, split);
        sp_plazos.setAdapter(adapter2);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        spinner_listener();
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

    private void msg (String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void ocultar_teclado (){
        View view = this.getCurrentFocus();
        InputMethodManager imn = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        imn.hideSoftInputFromWindow(view.getWindowToken(), 0);
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
        String sheet = "creditos";
        String id_credito = "";
        Log.v("subir_archivo0", "Nuevo_credito.\n\nfile: " + file + "\n\ncontenido del archivo:\n\n" + imprimir_archivo(file));
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null && !linea.equals("")) {
                String[] split = linea.split("_separador_");
                Log.v("subir_archivo1", ".\n\nLinea:\n\n" + linea + "\n\n.");
                json_string = json_string + split[1] + "_n_";
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

    private void mostrar_caja () {
        tv_caja.setText(imprimir_archivo(caja));
    }

    private void presentar_cuadratura () {
        Intent CuadraturaAc = new Intent(this, CuadraturaActivity.class);
        CuadraturaAc.putExtra("cuadratura", cuadratura);
        CuadraturaAc.putExtra("msg", "Operacion realizada con exito!!!");
        CuadraturaAc.putExtra("cliente_recivido", cliente_ID);
        CuadraturaAc.putExtra("cambio", "0");
        CuadraturaAc.putExtra("monto_creditito", String.valueOf(monto_credito));
        CuadraturaAc.putExtra("activity_devolver", "MenuPrincipal");
        CuadraturaAc.putExtra("mensaje_imprimir_pre", "");
        CuadraturaAc.putExtra("nombre_cliente", nombre_cliente + " " + apellido_cliente);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(CuadraturaAc);
        finish();
        System.exit(0);
    }

}
