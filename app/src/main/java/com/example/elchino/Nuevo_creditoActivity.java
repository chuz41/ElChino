package com.example.elchino;

import android.app.DatePickerDialog;
import android.content.Intent;
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
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import com.example.elchino.Util.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
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
    private String plazo = "";//Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%) Se elige con un spinner
    private String cuadratura = "";
    private EditText et_ID;
    private TextView tv_esperar;
    private String mes;
    private String anio;
    private String dia;
    private Button bt_consultar;
    private String cliente_ID = "";
    private String monto_disponible = "";
    private Spinner sp_plazos;
    private boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private final String caja = "caja.txt";
    private TextView tv_caja;
    private Button bt_cambiar_fecha;
    private Integer mes_selected = 0;
    private Integer anio_selected = 0;
    private Integer fecha_selected = 0;
    private boolean flag_fecha = false;
    private Date hoy_LD = new Date();
    private String telefono = "";

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nuevo_credito);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        if (!mensaje_recibido.equals("")) {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        bt_consultar = (Button) findViewById(R.id.bt_consultar);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        bt_cambiar_fecha = (Button) findViewById(R.id.bt_cambiar_fecha);
        String string = "Cambiar fecha";
        bt_cambiar_fecha.setText(string);
        bt_cambiar_fecha.setVisibility(View.INVISIBLE);
        TextView tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        sp_plazos = (Spinner) findViewById(R.id.sp_plazos);
        sp_plazos.setVisibility(View.INVISIBLE);
        string = "NUEVO CREDITO";
        tv_saludo.setText(string);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        sp_cuotas = (Spinner) findViewById(R.id.sp_cuotas);
        sp_tipo_cobro = (Spinner) findViewById(R.id.sp_tipo_cobro);
        sp_interes = (Spinner) findViewById(R.id.sp_interes);
        bt_personalizar = (Button) findViewById(R.id.bt_personalizar);
        activity_devolver = getIntent().getStringExtra("activity_devolver");
        hoy_LD = Calendar.getInstance().getTime();
        Log.v("OnCreate_0", "Nuevo_credito.\n\nFecha hoy: " + hoy_LD + "\n\n.");
        String fecha_hoy_string = DateUtilities.dateToString(hoy_LD);
        Log.v("OnCreate_1", "Nuevo_credito.\n\nFecha hoy: " + fecha_hoy_string + "\n\n.");
        cliente_Id_volver = cliente_recibido;
        Log.v("OnCreate_2", "Nuevo_credito.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        tv_caja.setHint("Caja...");
        mostrar_caja();
        separarFecha();
        if (!cliente_recibido.equals("")) {
            flag_client_reciv = true;
            cliente_ID = cliente_recibido;
            try {
                consultar(null);
            } catch (IOException | ParseException e) {
                e.printStackTrace();
            }
        }
        text_listener();
    }

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(hoy_LD);
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        dia = datosFecha.getDia();
    }

    public void cambiar_fecha (View view) {
        final Calendar c = Calendar.getInstance();
        mes_selected = (c.get(Calendar.MONTH));
        anio_selected = c.get(Calendar.YEAR);
        fecha_selected = c.get(Calendar.DAY_OF_MONTH);
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (datePicker, i, i1, i2) -> {
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
            mes_selected = i1+1;
            anio_selected = i;
            fecha_selected = i2;
            flag_fecha = true;
            Log.v("select_fecha", i_s + "/" + i1_s + "/" + i2_s);
        },anio_selected,mes_selected,fecha_selected);
        datePickerDialog.show();
    }

    private void restar_disponible () throws IOException {//TODO: Se debe actualizar la informacion en internet.
        StringBuilder archivoCompleto = new StringBuilder();
        int nuevo_monto;
        String file_name = cliente_ID + "_C_.txt";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                //Log.v("restar_disponible_0", "Nuevo_credito.\n\nLinea:\n\n" + linea + "\n\n.");
                String[] split = linea.split("_separador_");
                if (split[0].equals("monto_disponible")) {
                    nuevo_monto = Integer.parseInt(monto_disponible) - (monto_credito) + 1;
                    linea = linea.replace(split[1], String.valueOf(nuevo_monto));
                }
                archivoCompleto.append(linea).append("\n");
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new GuardarArchivo(file_name, archivoCompleto.toString(), getApplicationContext()).guardarFile();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void consultar (View view) throws IOException, ParseException {
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        if (tv_esperar.getText().toString().equals("Escoja el plazo del credito")) {
            restar_disponible();
            generarCredito();
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
            String[] archivos = fileList();
            StringBuilder archivoCompleto = new StringBuilder();
            String file_to_consult;
            if (flag_client_reciv) {
                file_to_consult = cliente_recibido + "_C_";
            } else {
                file_to_consult = et_ID.getText().toString() + "_C_";
            }
            if (file_to_consult.contains("*") || file_to_consult.contains(" ")) {
                Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                //Do nothing.
            } else {
                for (String s : archivos) {
                    Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(s);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        try {
                            InputStreamReader archivo = new InputStreamReader(openFileInput(s));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            while (linea != null) {
                                String[] split = linea.split("_separador_");
                                if (split[0].equals("puntuacion_cliente")) {
                                    String puntuacion_cliente = split[1];
                                    Log.v("llenar_spinner0.2", "Nuevo_credito.\n\nPuntuacion cliente: " + puntuacion_cliente + "\n\n.");
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
                                    String interes_mora = split[1];
                                    Log.v("llenar_spinner0.3", "Nuevo_credito.\n\nInteres mora: " + interes_mora + "\n\n.");
                                }
                                linea = linea.replace("_separador_", ": ");
                                linea = linea.replace("_cliente", "");
                                linea = linea.replace("_", " ");
                                archivoCompleto.append(linea).append("\n");
                                linea = br.readLine();
                            }
                            br.close();
                            archivo.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
            if (archivoCompleto.toString().equals("")) {
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
                nuevo_credito();
            }
        }
    }

    private void llenar_spinner () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        String plazos = "Escoja el plazo del credito_5 semanas (20%)_6 semanas (20%)_9 semanas (40%)_3 quincenas (25%)_5 quincenas (40%)";
        String[] split = plazos.split("_");
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.custom_spinner, split);
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
                        }else {
                            plazo = sp_plazos.getSelectedItem().toString();
                            bt_consultar.setEnabled(true);
                            bt_consultar.setClickable(true);
                            bt_consultar.setVisibility(View.VISIBLE);
                            Log.v("spinner_listener0", "Nuevo_credito.\n\nplazo: " + plazo + "\n\n.");
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
        String flag;
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
        String flag;
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
        String flag;
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
        String flag;
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
        String flag;
        int factor_semanas;
        String[] piezas = plazo.split(" ");
        switch (piezas[1]) {
            case "quincenas":
                factor_semanas = 2;
                break;
            case "semanas":
                factor_semanas = 1;
                break;
            case "meses":
                factor_semanas = 4;
                break;
            default:
                factor_semanas = -1;
                break;
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
    private void generarCredito () throws IOException, ParseException {
        String file_content = "";
        file_content = file_content + "monto_credito_separador_" + monto_credito + "\n";
        String plazo_presentar = plazo.replace(" ", "_");
        file_content = file_content + "plazo_separador_" + plazo_presentar + "\n";
        String monto_cuota = calcular_cuota();
        file_content = file_content + "monto_cuota_separador_" + monto_cuota + "\n";
        String fecha_sustituta = "";
        String sema_quince = "";
        int factor = 0;
        String[] split = plazo_presentar.split("_");
        switch (split[1]) {
            case "semanas":
                sema_quince = "semana";
                factor = 1;
                break;
            case "quincenas":
                sema_quince = "quincena";
                factor = 2;
                break;
            case "meses":
                sema_quince = "mes";
                factor = 4;
                break;
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
        String proximo_abono;
        file_content = file_content + "fecha_credito_separador_" + fecha_credit + "\n";
        if (flag_proximo_abono) {
            proximo_abono = obtener_proximo_abono();
            Log.v("generarCredito_0", "Nuevo_credito.\n\nproximo_abono: " + proximo_abono + "\n\n.");
        } else {
            proximo_abono = fecha_sustituta;
        }
        Log.v("generarCredito_1", "Nuevo_credito.\n\nproximo_abono: " + proximo_abono + "\n\n.");
        file_content = file_content + "proximo_abono_separador_" + proximo_abono + "\n";
        String saldo_mas_intereses = calcular_saldo();
        file_content = file_content + "saldo_mas_intereses_separador_" + saldo_mas_intereses + "\n";
        String tasa_interes = obtener_tasa();
        file_content = file_content + "tasa_separador_" + tasa_interes + "\n";
        String cuotass = calcular_cuotas();
        file_content = file_content + "cuotas_separador_" + cuotass + "\n";
        String credit_ID = obtener_id();
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
            String fecha_S_poner = splet[2] + "/" + splet[1] + "/" + splet[0];//Esta es la fecha que se pondra
            String string = String.valueOf(i + 1);
            String cadena = cuadratura + sema_quince + "_" + string + "_" + monto_cuota + "_" + fecha_S_poner + "__";
            Log.v("generarCredito_2", "Nuevo_credito.\n\ncadena: " + cadena + "\n\n.");
            cuadratura = cadena;
        }
        file_content = file_content + "cuadratura_separador_" + cuadratura + "\n";
        file_content = file_content + "intereses_moratorios_separador_0\n";
        file_content = file_content + "monto_abono_separador_0\n";
        file_content = file_content + "estado_archivo_separador_abajo";
        String file_name = credit_ID + ".txt";
        String archivoCreado = new CrearArchivo(file_name, getApplicationContext()).getFile();
        Log.v("generarCredito_3", "Nuevo_credito.\n\nResultado de la creacion del archivo:\n\n" + archivoCreado + "\n\n.");
        new GuardarArchivo(file_name, file_content, getApplicationContext()).guardarFile();
        actualizarCaja();
        actualizar_cierre(monto_credito, obtener_caja(), credit_ID);
        presentar_cuadratura();
        //Log.v("generarCredito_5", ".\n\nArchivo creado: " + file_name + "\n\nContenido de " + file_name + ":\n\n" + imprimir_archivo(file_name) + "\n\n.");
    }

    private void actualizar_cierre (Integer monto_credito, Integer saldo_caja, String credit_ID) {
        String linea_cierre = "credito " + monto_credito + " " + saldo_caja + " " + credit_ID;
        String lineaCierre = "credito_separador_" + monto_credito + "_separador_" + saldo_caja + "_separador_" + credit_ID;
        new AgregarLinea(linea_cierre, "cierre.txt", getApplicationContext());
        new AgregarLinea(lineaCierre, "cierre_cierre_.txt", getApplicationContext(), "cierre");
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

    private void actualizarCaja () throws IOException {
        long monto_nuevo = 0;
        StringBuilder contenido = new StringBuilder();
        boolean flagCajaxCompleta = false;
        boolean flagCajaxNoCreada = false;
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            monto_nuevo = Integer.parseInt(split[1]) - monto_credito;
            linea = linea.replace(split[1], String.valueOf(monto_nuevo));
            contenido = new StringBuilder(linea);
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new GuardarArchivo(caja, contenido.toString(), getApplicationContext()).guardarFile();
        contenido = new StringBuilder();
        String[] archivos = fileList();
        if (archivo_existe(archivos, "cajax_caja_.txt")) {
            Log.v("actualizarCaja_1", "Nuevo_credito.\n\nfile: " + "cajax_caja_.txt" + "\n\ncontenido del archivo:\n\n" + imprimir_archivo("cajax_caja_.txt"));
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("cajax_caja_.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null && !linea.equals("")) {
                    String[] split = linea.split("_separador_");
                    if (split[0].equals("caja")) {
                        linea = linea.replace(split[1], String.valueOf(monto_nuevo));
                    } else if (split[0].equals("estado_archivo")) {
                        flagCajaxCompleta = true;
                        if (split[1].equals("arriba")) {
                            linea = linea.replace("arriba", "abajo");
                        }
                    }
                    contenido.append(linea).append("\n");
                    Log.v("actualizarCaja_2", "Nuevo_credito.\n\nLinea:\n\n" + linea + "\n\n.");
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new CrearArchivo("cajax_caja_.txt", getApplicationContext());
            new AgregarLinea("caja_separador_" + monto_nuevo, "cajax_caja_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_abajo", "cajax_caja_.txt", getApplicationContext());
            Log.v("actualizarCaja_3", "Nuevo_credito.\n\ncontenido de cajax_caja_.txt:\n\n" + imprimir_archivo("cajax_caja_.txt") + "\n\n.");
            flagCajaxNoCreada = true;
        }
        if (!flagCajaxNoCreada) {
            new BorrarArchivo("cajax_caja_.txt", getApplicationContext());
            new GuardarArchivo("cajax_caja_.txt", contenido.toString(), getApplicationContext()).guardarFile();
        }
        if (!flagCajaxCompleta) {
            new AgregarLinea("estado_archivo_separador_abajo", "cajax_caja_.txt", getApplicationContext());
        }
    }

    private String obtener_id () {
        String flag;
        StringBuilder lista_archivos = new StringBuilder();
        String[] archivos = fileList();
        cliente_ID.replace("*", "");
        cliente_ID.replace(" ", "");
        for (String archivo : archivos) {
            Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivo);
            boolean matchFound = matcher.find();
            if (matchFound) {
                lista_archivos.append(archivo).append("_sep_");
            }
        }

        int cont = 1;
        String nombreFile;
        while (true) {
            nombreFile = cliente_ID + "_P_" + String.valueOf(cont) + "_P_.txt";
            if (archivo_existe(fileList(), nombreFile)) {
                cont++;
            } else {
                break;
            }
        }

        flag = cliente_ID + "_P_" + String.valueOf(cont) + "_P_";
        return flag;
    }

    private void nuevo_credito () {
        tv_esperar.setVisibility(View.VISIBLE);
        String string = "Digite el monto del credito";
        tv_esperar.setText(string);
        bt_cambiar_fecha.setVisibility(View.VISIBLE);
        et_ID.setEnabled(true);
        et_ID.setVisibility(View.VISIBLE);
        et_ID.requestFocus();
        et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
        et_ID.setClickable(true);
        et_ID.setText("");
        string = "Monto solicitado...";
        et_ID.setHint(string);
        et_ID.setFocusableInTouchMode(true);
        et_ID.requestFocus();
        string = "CONFIRMAR";
        bt_consultar.setText(string);
        bt_consultar.setVisibility(View.VISIBLE);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        text_listener();
    }

    private void obtener_plazo () {
        tv_esperar.setVisibility(View.VISIBLE);
        String string = "Escoja el plazo del credito";
        tv_esperar.setText(string);
        string = "CONFIRMAR";
        bt_consultar.setText(string);
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
                        boolean flag1;
                        boolean flag2 = false;
                        //Log.v("text_listener", ".\n\nMonto disponible: " + monto_disponible + "\n\nmonto solicitado: " + String.valueOf(s) + "\n\ns:\n\n-->" + s + "<--\n\n.");
                        flag1 = (Integer.parseInt(String.valueOf(s))) <= Integer.parseInt(monto_disponible);
                        try {
                            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            String[] split = linea.split(" ");
                            Log.v("leer_caja.txt", ".\n\nLinea: " + linea + "\n\nMonto credito: " + monto_credito + "\n\nMonto caja: " + split[1] + "\n\n.");
                            flag2 = Integer.parseInt(String.valueOf(s)) <= Integer.parseInt(split[1]);
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
                    String[] archivos = fileList();
                    if (et_ID.getText().toString().contains("*") || et_ID.getText().toString().contains(" ")) {
                        Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                    } else {
                        for (String archivo : archivos) {
                            Pattern pattern = Pattern.compile(et_ID.getText().toString(), Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(archivo);
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
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
        }
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
        String cuotass = "Cuotas_1_2_3_4_5_6_7_8_9_10_11_12_13_14_15_16_17_18_19_20_21";
        String[] split = cuotass.split("_");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, split);
        sp_cuotas.setAdapter(adapter);
        sp_listener1();
    }

    private void llenar_spinner2 () {
        String sema_quince_mes = "Periodo_semanas_quincenas_meses";
        String[] split = sema_quince_mes.split("_");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, split);
        sp_tipo_cobro.setAdapter(adapter);
        sp_listener2();
    }

    private void llenar_spinner3 () {
        String interess = "Interes_0%_5%_10%_15%_20%_25%_30%_35%_40%_45%_50%_55%_60%_65%_70%_75%_80%_85%_90%_95%";
        String[] split = interess.split("_");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, split);
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
                        if (!seleccion.equals("Coutas")) {
                            flag_cuotass = true;
                            cuotass_S = sp_cuotas.getSelectedItem().toString();
                            if (flag_interess & flag_sema_quince_mes & flag_cuotass) {
                                confirm_new_credit_config();
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
                        if (!seleccion.equals("Periodo")) {
                            flag_sema_quince_mes = true;
                            sema_quince_mes_S = sp_tipo_cobro.getSelectedItem().toString();
                            if (flag_interess & flag_sema_quince_mes & flag_cuotass) {
                                confirm_new_credit_config();
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
                        if (!seleccion.equals("Interes")) {
                            flag_interess = true;
                            interess_S = sp_interes.getSelectedItem().toString();
                            if (flag_interess & flag_sema_quince_mes & flag_cuotass) {
                                confirm_new_credit_config();
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
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, R.layout.custom_spinner, split);
        sp_plazos.setAdapter(adapter2);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        spinner_listener();
    }

    private String imprimir_archivo (String file_name){
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

    private void msg (String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
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
        CuadraturaAc.putExtra("nombreCliente", nombre_cliente + " " + apellido_cliente);
        obtenerPhoneCliente();
        CuadraturaAc.putExtra("telefono", telefono);
        startActivity(CuadraturaAc);
        finish();
        System.exit(0);
    }

    private void obtenerPhoneCliente () {
        String nombreArchivo = cliente_ID + "_C_.txt";
        if (archivo_existe(fileList(), nombreArchivo)) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(nombreArchivo));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null) {
                    String[] split = linea.split("_separador_");
                    if (split[0].equals("telefono1_cliente")) {
                        telefono = split[1];
                        Log.v("obtenerPhoneCliente_0", "Nuevo_credito.\n\nlinea:\n\n" + linea + "\n\ntelefono: " + telefono + "\n\n.");
                    }
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void mostrar_caja () {
        tv_caja.setText(imprimir_archivo(caja));
    }

}