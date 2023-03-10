package com.example.elchino;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.CrearArchivo;
import com.example.elchino.Util.DateUtilities;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AbonarActivity extends AppCompatActivity {

    private Integer monto_abono = 0;
    private Integer intereses_moratorios_hoy = 0;
    private CheckBox chb_adelantarIntereses;
    private Integer monto_a_pagar = 0;
    private Integer monto_anterior = 0;
    private EditText et_mensaje;
    private Boolean flag_perdon = false;
    private Integer monto_perdonado = 0;
    private String mensaje_solicitud = "";
    private String monto_prestado_final = "";
    private Integer total_cuotas = 0;
    private TextView tv_indicador;
    private String mensaje_imprimir = "";
    private Integer intereses_monroe = 0;
    private Integer cambio = 0;
    private String interes_mora_parcial;
    private Integer monto_cuota = 0;
    private Integer saldo_mas_intereses = 0;
    private Integer tasa = 0;//Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
    private String morosidad = "D";
    private Integer cantidad_cuotas_pendientes = 0;
    private String archivo_prestamo = "";
    private String plazo = "";
    private EditText et_ID;
    private TextView tv_esperar;
    private String dia;
    private String mes;
    private String anio;
    private Button bt_consultar;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private String monto_disponible = "0";
    private Boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private String caja = "caja.txt";
    private String credit_ID = "";
    private Integer cantidad_de_creditos = 0;
    private String interes_mora = "1";
    private String interes_mora_total = "0";
    private String lista_archivos = "";
    private String proximo_abono = "";
    private String puntuacion_cliente = "";
    private String cuadratura = "";
    private TextView tv_caja;
    private String nombre_cliente = "";
    private String apellido_cliente = "";
    private Button bt_cambiar_fecha;
    private Date hoy_LD;
    private String activity_volver;
    private Spinner sp_plazos;
    private String fecha_hoy_string;
    private Button bt_perdon;
    private String solicitud_ID;
    private Integer monto_digitado = 0;
    private String textoTvEsperar = "";
    private String hintEt_ID = "";
    private String textEt_ID = "";
    private String archivoCredito;
    private Integer montoIngresado = 0;

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
        chb_adelantarIntereses = (CheckBox) findViewById(R.id.chb_adelantarIntereses);
        chb_adelantarIntereses.setVisibility(View.INVISIBLE);
        tv_indicador = (TextView) findViewById(R.id.tv_indicador);
        tv_indicador.setText("");
        et_mensaje = (EditText) findViewById(R.id.et_mensaje);
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
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
        separarFecha();
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
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        text_listener();
    }

    public void chb_adelantarIntereses_Listener (View view) {
        bt_perdon.setClickable(false);
        bt_perdon.setEnabled(false);
        bt_consultar.setEnabled(false);
        if (chb_adelantarIntereses.isChecked()) {
            tv_indicador.setVisibility(View.INVISIBLE);
            bt_consultar.setVisibility(View.INVISIBLE);
            textoTvEsperar = tv_esperar.getText().toString();
            hintEt_ID = et_ID.getHint().toString();
            textEt_ID = et_ID.getText().toString();
            tv_esperar.setText("Digite los intereses adelantados:");
            et_ID.setText("");
            et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
            et_ID.setHint("Monto adelanto intereses...");
            et_ID.requestFocus();
            bt_perdon.setText("CONFIRMAR");
            bt_perdon.setClickable(true);
            bt_perdon.setEnabled(true);
        } else {
            tv_indicador.setVisibility(View.VISIBLE);
            bt_consultar.setVisibility(View.VISIBLE);
            bt_consultar.setEnabled(true);
            tv_esperar.setText(textoTvEsperar);
            et_ID.setText("");
            et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
            et_ID.setHint(hintEt_ID);
            et_ID.setText(textEt_ID);
            et_ID.requestFocus();
            bt_perdon.setText("PERDONAR");
            if (intereses_moratorios_hoy > 0) {
                bt_perdon.setClickable(true);
                bt_perdon.setEnabled(true);
            } else {
                bt_perdon.setClickable(false);
                bt_perdon.setEnabled(false);
            }
        }
    }

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(hoy_LD);
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        dia = datosFecha.getDia();
    }

    private void mostrar_caja () {
        tv_caja.setText(imprimir_archivo(caja));
    }

    private String obtener_cuotas_morosas (String cuadratura) throws ParseException {
        String[] split1 = cuadratura.split("__");
        String fecha_next_abono_bkUp = fecha_hoy_string;
        int length_split1 = split1.length;
        int cont = 0;
        for (int i = 0; i < length_split1; i++) {
            fecha_next_abono_bkUp = fecha_hoy_string;
            String[] split = split1[i].split("_");
            String fecha_cuadra_S = split[3];
            String[] split_fecha_cuadra_S = fecha_cuadra_S.split("/");
            fecha_cuadra_S = split_fecha_cuadra_S[2] + "-" + split_fecha_cuadra_S[1] + "-" + split_fecha_cuadra_S[0];
            int monto = Integer.parseInt(split[2]);
            if (monto > 100) {
                Date fecha_cuadra = DateUtilities.stringToDate(fecha_cuadra_S);
                String[] split_fecha_next_abono_bkUp = fecha_next_abono_bkUp.split("/");
                fecha_next_abono_bkUp = split_fecha_next_abono_bkUp[2] + "-" + split_fecha_next_abono_bkUp[1] + "-" + split_fecha_next_abono_bkUp[0];
                Date fecha_next_abono_bkUp_D = DateUtilities.stringToDate(fecha_next_abono_bkUp);
                int dias_atrasados = DateUtilities.daysBetween(fecha_next_abono_bkUp_D, fecha_cuadra);//Positivo indica morosidad
                if (dias_atrasados > 0) {
                    morosidad = "M";
                    cont++;
                }
            }
        }
        int cantidad_de_cuotas_pendientes = cont;
        int c_d_c_p = (int) cantidad_de_cuotas_pendientes;
        puntuacion_cliente = String.valueOf(Integer.parseInt(puntuacion_cliente) - c_d_c_p);
        return String.valueOf(c_d_c_p);
    }

    private void llenar_spinner () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        String creditos = "Escoja el credito...___";
        String archivos[] = fileList();
        Log.v("llenando_spinner0", ".\n\nCantidad de archivos: " + archivos.length + "\n\n.");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("llenando_spinner1", "********ERROR***************Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
            for (int i = 0; i < archivos.length; i++) {
                Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(archivos[i]);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    Log.v("llenando_spinner2", ".\n\nFile: " + archivos[i] + "\n\n.");
                    try {
                        String fecha_next_abono = "";
                        String intereses_mora = "1";
                        String saldo_mas_intereses_s = "";
                        String plazoz = "";
                        String numero_de_credito = "";
                        String cuadratura_pre = "";
                        String cuadratura_bkup = "";
                        Date fecha_credito = new Date();
                        String monto_prestado = "";
                        String cuotas_morosas = "";
                        int factor_semanas;
                        String file_name = archivos[i];
                        String[] split_indice = file_name.split("_P_");
                        numero_de_credito = split_indice[1];
                        InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
                        archivoCredito = file_name;
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
                            if (split[0].equals("cuadratura")) {
                                cuadratura_pre = split[1];
                                cuadratura_bkup = cuadratura_pre;
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
                        String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                        interes_mora_total = intereses_moritas;
                        interes_mora_parcial = interes_mora_total;
                        cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0, fecha_credito, "n");
                        saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mora, monto_prestado);
                        cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup);
                        double saldo_mas_intereses_D = Double.parseDouble(saldo_mas_intereses_s);
                        int saldo_mas_intereses_I = (int) saldo_mas_intereses_D;
                        saldo_mas_intereses_s = String.valueOf(saldo_mas_intereses_I);
                        if (Integer.parseInt(saldo_plus_s) > 1000) {
                            creditos = creditos + "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas + "___";
                        } else {
                            //Do nothing.
                        }
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
        if (next_pay.equals("Prestamo cancelado")) {
            next_pay = dia + "/" + mes + "/" + anio;
        }
        String flag = "";
        String saldo = "";
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        int diferencia_en_dias = DateUtilities.daysBetween(hoy_LD, proximo_abono_LD);
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            saldo = String.valueOf(Integer.parseInt(saldo_plus) + Integer.parseInt(intereses_de_mora));
            morosidad = "D";
        } else {//Significa que esta atrazado!!!
            saldo = String.valueOf(Integer.parseInt(saldo_plus)  + Integer.valueOf(interes_mora_total) + Integer.parseInt(intereses_de_mora));//No se suman intereses sobre los intereses moratorios, pero si sobre el interes acordado del credito!!!
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
        Log.v("revisando_creditos_0", "Abonar.\n\nCliente_ID: " + cliente_ID + "\n\n.");
        boolean flasg = false;
        String flag = "";
        lista_archivos = "";
        String archivos[] = fileList();
        Log.v("revisando_creditos_1", ".\n\nAbonar. \n\nTotal de archivos: " + archivos.length + "\n\n.");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("revisando_creditos_2", "Abonar.\n\n****ERROR********\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
            for (int i = 0; i < archivos.length; i++) {
                Log.v("revisando_creditos_3", "Abonar.\n\nArchivo: " + archivos[i] + "\n\n.");
                String cuadratura_tempo = "";
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
                            if (split[0].equals("cuadratura")) {
                                cuadratura_tempo = split[1];
                                Log.v("revisando_creditos3", "Abonar.\n\nlinea:\n\n" + linea + "\n\nCuadratura:" + cuadratura_tempo + "\n\n.");
                                //cuadratura_separador_semana_1_0_09/1/2023__semana_2_0_16/1/2023__semana_3_0_23/1/2023__semana_4_15555_30/1/2023__semana_5_15555_06/2/2023__semana_6_15555_13/2/2023__semana_7_15555_20/2/2023__semana_8_15555_27/2/2023__semana_9_15555_06/3/2023__
                                String[] splitCuadra_segs = cuadratura_tempo.split("__");
                                int deuda = 0;
                                for (int o = 0; o < splitCuadra_segs.length; o++) {
                                    String[] splitCuadra_vals = splitCuadra_segs[o].split("_");
                                    deuda = deuda + Integer.parseInt(splitCuadra_vals[2]);
                                    Log.v("revisando_creditos4", "Abonar. Deuda: " + deuda + ".");
                                }
                                if (deuda < 1000) {
                                    //Do nothing. Credito ya ha sido cancelado casi en su totalidad, por lo que se toma como cancelado al 100% y no se muestra.
                                } else {
                                    lista_archivos = lista_archivos + archivos[i] + "_sep_";//Significa que es un credito activo.
                                    Log.v("revisando_creditos5", "Abonar.\n\nlista_archivos:\n\n" + lista_archivos + "\n\n.");
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

    public void consultar (View view) throws IOException, InterruptedException {
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
                Log.v("Consultar0.1", "**********ERROR***********Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            } else {
                Log.v("consultar0.2", "Abonar.\n\nCliente_ID: " + cliente_ID + "\n\nFile to consult: " + file_to_consult + "\n\n.");
                for (int i = 0; i < archivos.length; i++) {
                    Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(archivos[i]);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
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
                                linea = linea.replace("_separador_", ": ");
                                linea = linea.replace("_cliente", "");
                                linea = linea.replace("_", " ");
                                archivoCompleto = archivoCompleto + linea + "\n";
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
            if (archivoCompleto.equals("")) {
                //No se encontro el cliente. NUNCA DEBERIA LLEGAR AQUI!!! (FILTRO PREVIO)
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
                text_listener();
            } else {
                Log.v("consultar_digite_ced", "Abonar.\n\nLlamando a llenar spinner." + "\n\n.");
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
            monto_a_pagar = 0;
            cantidad_cuotas_pendientes = Integer.parseInt(parts_prestamo[3]);
            morosidad = parts_prestamo[2];
            monto_cuota = obtener_monto_cuota(parts_prestamo[0]);
            int montoAPagar = 0;
            int interesMoraTotal = 0;
            if (Integer.parseInt(parts_prestamo[3]) == 0) {
                monto_a_pagar = monto_cuota + intereses_monroe;
                montoAPagar = monto_cuota;
                interesMoraTotal = intereses_monroe;
            } else {//morosidad
                if (morosidad.equals("D")) {
                    monto_a_pagar = cantidad_cuotas_pendientes * monto_cuota + intereses_monroe;
                    montoAPagar = cantidad_cuotas_pendientes * monto_cuota;
                    interesMoraTotal = intereses_monroe;
                } else {//monto a pagar
                    monto_a_pagar = cantidad_cuotas_pendientes * monto_cuota + Integer.parseInt(interes_mora_parcial);
                    montoAPagar = cantidad_cuotas_pendientes * monto_cuota + Integer.parseInt(interes_mora_parcial) - Integer.parseInt(interes_mora_parcial);
                    interesMoraTotal = Integer.parseInt(interes_mora_parcial);
                }
            }
            presentar_monto_a_pagar(monto_a_pagar, interesMoraTotal, montoAPagar);
        } else {
            //Do nothing.
        }
    }

    private void actualizarCaja (int monto) throws IOException {
        long monto_nuevo = 0;
        String contenido = "";
        boolean flagCajaxCompleta = false;
        boolean flagCajaxNoCreada = false;
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            monto_nuevo = Integer.parseInt(split[1]) + monto;
            linea = linea.replace(split[1], String.valueOf(monto_nuevo));
            contenido = linea;
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (new GuardarArchivo(caja, contenido, getApplicationContext()).guardarFile()) {
            Toast.makeText(this, "Caja se ha actualizado correctamente!!!", Toast.LENGTH_SHORT).show();
            Log.v("actualizarCaja_0", "Nuevo_credito.\n\nContenido del archivo:\n\n" + imprimir_archivo(caja) + "\n\n.");
        } else {
            Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
        }
        contenido = "";
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
                        if (split[1].equals("abajo")) {
                            //Do nothing. Let the line same.
                        } else {
                            linea = linea.replace("arriba", "abajo");
                        }
                    } else {
                        //Do nothing. Let the line same.
                    }
                    contenido = contenido + linea + "\n";
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
            new AgregarLinea("caja_separador_" + String.valueOf(monto_nuevo), "cajax_caja_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_abajo", "cajax_caja_.txt", getApplicationContext());
            Log.v("actualizarCaja_3", "Nuevo_credito.\n\ncontenido de cajax_caja_.txt:\n\n" + imprimir_archivo("cajax_caja_.txt") + "\n\n.");
            flagCajaxNoCreada = true;
        }

        if (flagCajaxNoCreada) {
            //Do nothing.
        } else {
            new BorrarArchivo("cajax_caja_.txt", getApplicationContext());
            if (new GuardarArchivo("cajax_caja_.txt", contenido, getApplicationContext()).guardarFile()) {
                Log.v("actualizarCaja_4", "Nuevo_credito.\n\nContenido del archivo:\n\n" + imprimir_archivo("cajax_caja_.txt") + "\n\n.");
            } else {
                Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            }
        }
        Log.v("Debug_cajax_caja", "Abonar.\n\nflagCajaxCompleta: " + flagCajaxCompleta + ".\n\n.");
        if (flagCajaxCompleta) {
            //Do nothing.
        } else {
            new AgregarLinea("estado_archivo_separador_abajo", "cajax_caja_.txt", getApplicationContext());
        }

    }

    private void procesar_abono2 () throws IOException {
        String file_name = archivo_prestamo;
        String contenido = "";
        String fecha_next_abono = "";
        String interes_mora_total_s = "";
        String saldo_mas_intereses_s = "";
        Date fecha_credito = new Date();
        int factor_semanas = 0;
        int monto_ingresado = Integer.parseInt(et_ID.getText().toString());
        actualizarCaja(monto_ingresado);
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
            archivoCredito = file_name;
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
                } else if (split[0].equals("tasa")) {
                    tasa = Integer.parseInt(split[1]);
                } else if (split[0].equals("ID_credito")) {
                    credit_ID = split[1];
                } else if (split[0].equals("morosidad")) {
                    morosidad = split[1];
                } else if (split[0].equals("intereses_moratorios")) {
                    interes_mora_total_s = split[1];
                } else {
                    //Do nothing. Continue...
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
            String intereses_moritas = obtener_intereses_moratorios(monto_prestado_final, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
            subir_solicitud(monto_digitado, mensaje_solicitud);
            cuadratura = obtener_cuadratura(cuadratura, fecha_next_abono, factor_semanas, monto_ingresado, fecha_credito, "final");//Aqui se obtiene la verdadera y final morosidad.
            intereses_monroe = Integer.parseInt(interes_mora_total_s);//Son los intereses guardados en el archivo. calculados en un periodo que se abono solo parte de los intereses.
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
            boolean flagNoEstado = false;
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
                } else if (split[0].equals("estado_archivo")) {
                    flagNoEstado = true;
                    linea = linea.replace(split[1], "abajo");
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("saldo_mas_intereses")) {
                    linea = linea.replace(split[1], String.valueOf(saldo_mas_intereses));
                    contenido = contenido + linea + "\n";
                } else if (split[0].equals("ID_credito")) {
                    contenido = contenido + linea + "\n";
                    credit_ID = split[1];
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
            if (!flagNoEstado) {
                contenido = contenido + "estado_archivo_separador_abajo";
            }
            Log.v("actualizar_archiv_cred1", ".\n\nAbonar. Archivo: " + archivo_prestamo + "\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
            if (new GuardarArchivo(archivo_prestamo, contenido, getApplicationContext()).guardarFile()) {
                Toast.makeText(this, "Abono se ha registrado correctamente!!!", Toast.LENGTH_SHORT).show();
                Log.v("actualizar_archiv_cred3", "Abonar.\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
            } else {
                Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            }
            Log.v("actualizar_archiv_cred4", ".\n\nAbonar. Archivo: " + archivo_prestamo + "\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
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
        String lineaCierre = "abono_separador_" + String.valueOf(monto_abono) + "_separador_" + saldo_caja + "_separador_" + credit_ID;
        new AgregarLinea(lineaCierre, "cierre_cierre_.txt", getApplicationContext(), "cierre");
    }

    private void presentar_cuadratura () {
        Intent CuadraturaAc = new Intent(this, CuadraturaActivity.class);
        CuadraturaAc.putExtra("cuadratura", cuadratura);
        CuadraturaAc.putExtra("msg", "Abono realizado con exito!!!");
        Log.v("presentar_cuadratura_0", "Abonar.\n\nCliente_ID: " + cliente_ID + "\n\nCuadratura:\n\n" + cuadratura + "\n\n.");
        CuadraturaAc.putExtra("cliente_recivido", cliente_ID);
        CuadraturaAc.putExtra("cambio", String.valueOf(cambio));
        CuadraturaAc.putExtra("monto_creditito", "0");
        CuadraturaAc.putExtra("activity_devolver", "Estado_cliente");
        CuadraturaAc.putExtra("mensaje_imprimir_pre", mensaje_imprimir);
        CuadraturaAc.putExtra("nombreCliente", nombre_cliente + " " + apellido_cliente);
        CuadraturaAc.putExtra("abonar", "abonar" + " " + "abonar");
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
            String archivoCreado = new CrearArchivo(archivo_cliente, getApplicationContext()).getFile();
            if (new GuardarArchivo(archivo_cliente, contenido, getApplicationContext()).guardarFile()) {
            } else {
                Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        presentar_cuadratura();
    }

    private String obtener_saldo_plus (String cuadratura_s) {
        String flag;
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

    public void perdonar (View view) throws IOException {
        String tv_esperar_text = tv_esperar.getText().toString();
        if (tv_esperar_text.equals("Monto a pagar al dia de hoy: ")) {
            chb_adelantarIntereses.setVisibility(View.INVISIBLE);
            chb_adelantarIntereses.setChecked(false);
            monto_anterior = monto_a_pagar;
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
            monto_digitado = Integer.parseInt(et_ID.getText().toString());
            chb_adelantarIntereses.setVisibility(View.INVISIBLE);
            chb_adelantarIntereses.setChecked(false);
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
                    texto_cuadro = texto_cuadro.replace("Intereses moratorios: " +
                            String.valueOf(viejo_monto_intereses), "Intereses moratorios: " +
                            String.valueOf(nuevo_monto_intereses));
                    msg("Se han perdonado: " + String.valueOf(monto_digitado) + " colones.");
                    tv_indicador.setText(texto_cuadro);
                    tv_esperar.setText("Monto a pagar al dia de hoy: ");
                    et_mensaje.setVisibility(View.INVISIBLE);
                    int monto_poner = monto_anterior - monto_digitado;
                    et_ID.setText(String.valueOf(monto_poner));
                    et_ID.setFocusable(true);
                    et_ID.requestFocus();
                    bt_consultar.setEnabled(true);
                    bt_consultar.setClickable(true);
                    bt_consultar.setVisibility(View.VISIBLE);
                }
            }
        } else if (tv_esperar_text.equals("Digite los intereses adelantados:")) {
            bt_perdon.setClickable(false);
            bt_perdon.setEnabled(false);
            chb_adelantarIntereses.setEnabled(false);
            tv_esperar.setText("Espere por favor...");
            montoIngresado = Integer.parseInt(et_ID.getText().toString());
            et_ID.setEnabled(false);
            actualizarCreditoAdelanto();
        } else {
            //TODO any other function!
            //Do nothing for now.
        }
    }

    private void actualizarCreditoAdelanto () throws IOException {
        Log.v("actualizarClienteAde_0", "Abonar.\n\n");
        String contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(archivoCredito));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                Log.v("actualizarClienteAde_1", "Abonar.\n\nlinea:\n\n" + linea + "\n\n.");
                String[] split = linea.split("_separador_");
                if (split[0].equals("intereses_moratorios")) {
                    int intereses_mor_archivo = Integer.parseInt(split[1]);
                    intereses_mor_archivo = intereses_mor_archivo - montoIngresado;
                    linea = linea.replace("intereses_moratorios_separador_" + split[1], "intereses_moratorios_separador_" + String.valueOf(intereses_mor_archivo));
                } else if (split[0].equals("ID_credito")) {
                    credit_ID = split[1];
                } else if (split[0].equals("estado_archivo")) {
                    if (!split[1].equals("abajo")) {
                        linea = linea.replace("arriba", "abajo");
                    }
                } else if (split[0].equals("monto_abono")) {
                    Log.v("actualizarClienteAde_2", "Abonar.\n\nmonto_abono: " + String.valueOf(montoIngresado) + "\n\n.");
                    linea = linea.replace("monto_abono_separador_" + split[1], "monto_abono_separador_" + String.valueOf(montoIngresado));
                } else if (split[0].equals("cuadratura")) {
                    cuadratura = split[1];
                }
                contenido = contenido + linea + "\n";
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new BorrarArchivo(archivoCredito, getApplicationContext());
        String archivoCreado = new CrearArchivo(archivoCredito, getApplicationContext()).getFile();
        Log.v("actualizarClienteAde_3", "Abonar.\n\nResultado de la creacion del archivo:\n\n" + archivoCreado + "\n\n.");
        if (new GuardarArchivo(archivoCredito, contenido, getApplicationContext()).guardarFile()) {
            Log.v("actualizarClienteAde_4", "Abonar.\n\nContenido del archivo:\n\n" + imprimir_archivo(archivoCreado) + "\n\n.");
            actualizarCaja(montoIngresado);
            actualizar_cierre(montoIngresado, obtener_caja(), credit_ID);
            String montoIngresado_s = String.valueOf(montoIngresado);
            char[] chars = montoIngresado_s.toCharArray();
            if (chars.length == 2) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
            } else if (chars.length == 3) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
            } else if (chars.length == 4) {
                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
            } else if (chars.length == 5) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
            } else if (chars.length == 6) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
            } else if (chars.length == 7) {
                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
            }
            mensaje_imprimir = mensaje_imprimir + "Monto abonado:\n" + montoIngresado_s + " colones.\n";
            mensaje_imprimir = mensaje_imprimir + "Correspondiente a intereses.\n\n";
            mensaje_imprimir = mensaje_imprimir + "******************************\n";
            mensaje_imprimir = mensaje_imprimir + "Abono a intereses:\n" + montoIngresado_s + " colones.\n";
            mensaje_imprimir = mensaje_imprimir + "Anobo al capital: 0,00 colones.\n";
            presentar_cuadratura();
        } else {
            Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
        }
    }

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

    private void subir_solicitud (Integer monto_perdonador, String mensaje_solicitud) throws IOException {

        if (monto_perdonador <= 0 || mensaje_solicitud.equals("") || mensaje_solicitud.equals(null) || mensaje_solicitud.isEmpty()) {
            //Do nothing.
        } else {
            monto_perdonado = monto_perdonador;
            long ID_solic_final = 9000000;
            String solicitud_ID_S = "";
            String cobrador_ID_S = "";
            String archivos[] = fileList();
            Log.v("subir_solicitud_0", "Abonar.\n\nTotal de archivos: " + archivos.length + "\n\n.");
            Log.v("subir_solicitud_1", "Abonar.\n\ncobrador_ID_S: " + cobrador_ID_S + "\n\n.");

            for (int i = 0; i < archivos.length; i++) {
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
                                Log.v("subir_solicitud_2", "Abonar.\n\nlinea:\n\n" + linea + "\n\n.");
                                String[] split_solicitud_ID_S = solicitud_ID_S.split("S");
                                int solicitud_ID_temp = Integer.parseInt(split_solicitud_ID_S[1]);
                                Log.v("subir_solicitud_3", "Abonar.\n\nsolicitud_ID_temp: " + solicitud_ID_temp + "\n\n.");
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
            contenido_file = contenido_file + "monto_perdonado_separador_" + monto_perdonador + "\n";
            contenido_file = contenido_file + "estado_archivo_separador_abajo";
            String archivoCreado = new CrearArchivo(file, getApplicationContext()).getFile();
            if (new GuardarArchivo(file, contenido_file, getApplicationContext()).guardarFile()) {
                Toast.makeText(this, "Solicitud de perdon se ha registrado correctamente!!!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private String obtener_cuadratura (String cuadratura, String fecha_next_abono, int factor_semanas, int monto_ingresado, Date fecha_credito, String paso) throws ParseException, IOException {
        String flag = "";
        if (paso.equals("final")) {
            if (flag_perdon) {
                int interes_moratorio_I = Integer.parseInt(interes_mora_total);
                interes_moratorio_I = interes_moratorio_I - monto_perdonado;
                interes_mora_total = String.valueOf(interes_moratorio_I);
            }
        }
        int monto_temporal = monto_ingresado - Integer.parseInt(interes_mora_total);
        int monto_temporal_fix = monto_temporal;
        if (monto_ingresado > 0) {
            if (Integer.parseInt(interes_mora_total) > 0) {
                String montoIngresado_s = String.valueOf(monto_ingresado);
                char[] chars = montoIngresado_s.toCharArray();
                if (chars.length == 1) {
                    montoIngresado_s = String.valueOf(chars[0]) + ",00";
                } else if (chars.length == 2) {
                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                } else if (chars.length == 3) {
                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                } else if (chars.length == 4) {
                    montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                } else if (chars.length == 5) {
                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                } else if (chars.length == 6) {
                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                } else if (chars.length == 7) {
                    montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                }
                String interes_mora_total_s = String.valueOf(interes_mora_total);
                chars = interes_mora_total_s.toCharArray();
                if (chars.length == 1) {
                    interes_mora_total_s = String.valueOf(chars[0]) + ",00";
                } else if (chars.length == 2) {
                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                } else if (chars.length == 3) {
                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                } else if (chars.length == 4) {
                    interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                } else if (chars.length == 5) {
                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                } else if (chars.length == 6) {
                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                } else if (chars.length == 7) {
                    interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                }
                mensaje_imprimir = mensaje_imprimir + "Monto abonado:\n" + montoIngresado_s + " colones.\n";
                mensaje_imprimir = mensaje_imprimir + "Intereses moratorios:\n" + interes_mora_total_s + " colones.\n";
                mensaje_imprimir = mensaje_imprimir + "******************************\n\n";
            }
        }
        if (monto_temporal < 0) {//No alcanzo siquiera para pagar los intereses. Debe retornar
            String[] split2 = fecha_next_abono.split("/");
            String fecha_nx_abo = split2[2] + "-" + split2[1] + "-" + split2[0];
            Date fecha_nx_abo_LD = DateUtilities.stringToDate(fecha_nx_abo);
            String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_nx_abo_LD));
            int interes_mora_diario = Integer.parseInt(interes_mora_total) / Integer.parseInt(diferencia_fechas);
            int dias_pagados = monto_ingresado / interes_mora_diario;
            Date fecha_nextr = DateUtilities.addDays(fecha_nx_abo_LD, dias_pagados);
            interes_mora_total = String.valueOf(Integer.parseInt(interes_mora_total) - monto_ingresado);
            proximo_abono = DateUtilities.dateToString(fecha_nextr);
            String[] split = proximo_abono.split("-");
            proximo_abono = split[2] + "/" + split[1] + "/" + split[0];
            morosidad = "M";
            flag = cuadratura;
            monto_abono = monto_ingresado;
            return flag;
        } else if (monto_temporal == 0) {//Aqui paga el monto completo, solo de los intereses moratorios, no abona nada a los abonos ordinarios. Debe retornar
            flag = cuadratura;
            proximo_abono = DateUtilities.dateToString(hoy_LD);
            String[] split = proximo_abono.split("-");
            proximo_abono = split[2] + "/" + split[1] + "/" + split[0];
            morosidad = "M";
            interes_mora_total = "0";
            if (monto_ingresado > 0) {
                String montoIngresado_s = String.valueOf(monto_ingresado);
                char[] chars = montoIngresado_s.toCharArray();
                if (chars.length == 1) {
                    montoIngresado_s = String.valueOf(chars[0]) + ",00";
                } else if (chars.length == 2) {
                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                } else if (chars.length == 3) {
                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                } else if (chars.length == 4) {
                    montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                } else if (chars.length == 5) {
                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                } else if (chars.length == 6) {
                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                } else if (chars.length == 7) {
                    montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                }
                String interes_mora_total_s = String.valueOf(interes_mora_total);
                chars = interes_mora_total_s.toCharArray();
                if (chars.length == 1) {
                    interes_mora_total_s = String.valueOf(chars[0]) + ",00";
                } else if (chars.length == 2) {
                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                } else if (chars.length == 3) {
                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                } else if (chars.length == 4) {
                    interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                } else if (chars.length == 5) {
                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                } else if (chars.length == 6) {
                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                } else if (chars.length == 7) {
                    interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                }
                mensaje_imprimir = mensaje_imprimir + "Monto abonado:\n" + montoIngresado_s + " colones\n";
                mensaje_imprimir = mensaje_imprimir + "Intereses moratorios:\n" + interes_mora_total_s + " colones.\n";
                mensaje_imprimir = mensaje_imprimir + "Abono al capital:    " + "0,00 colones" + "\n******************************\n";
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
            int restar_disponible = (int) restar_disponibleL;
            int xx = (int) x;
            float monto_disponible_F = Float.parseFloat(monto_disponible);
            int monto_disponible_I = (int) monto_disponible_F;
            monto_disponible = String.valueOf(monto_disponible_I);
            monto_disponible = String.valueOf((Integer.parseInt(monto_disponible) + xx));
            int largo_split = split.length;
            for (int i = 0; i < largo_split; i++) {
                String[] split_1 = split[i].split("_");
                if (Integer.parseInt(split_1[2]) > 0) {//Significa que tiene esta cuota pendiente.
                    monto_temporal = monto_temporal - Integer.parseInt(split_1[2]);//Esta es la cantidad que va quedando del abono.
                    if (monto_temporal < 0) {//Significa que no alcanza para esta cuota. Debe retornar
                        String fecha_cuadrito = split_1[3];
                        String numero_cuota = split_1[1];
                        String[] split_fec = fecha_cuadrito.split("/");
                        fecha_cuadrito = split_fec[2] + "-" + split_fec[1] + "-" + split_fec[0];
                        Date fecha_cuadrito_LD = DateUtilities.stringToDate(fecha_cuadrito);
                        String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_cuadrito_LD));
                        if (monto_ingresado > 0) {
                            int monto_abonado_I = Integer.parseInt(split_1[2]) + monto_temporal;

                            String montoIngresado_s = String.valueOf(monto_abonado_I);
                            Log.v("debug1", "\n\nmonto_abonado_I: " + monto_abonado_I + "\n\n.");
                            char[] chars = montoIngresado_s.toCharArray();
                            if (chars.length == 1) {
                                montoIngresado_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }
                            Log.v("debug1", "\n\nmontoIngresado_s: " + montoIngresado_s + "\n\n.");

                            String montoIngresadoT_s = String.valueOf(monto_temporal);
                            montoIngresadoT_s = montoIngresadoT_s.replace("-", "");
                            chars = montoIngresadoT_s.toCharArray();
                            if (chars.length == 1) {
                                montoIngresadoT_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                montoIngresadoT_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                montoIngresadoT_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                montoIngresadoT_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                montoIngresadoT_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                montoIngresadoT_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                montoIngresadoT_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }

                            if (monto_temporal > 0) {
                                montoIngresadoT_s = "-" + montoIngresadoT_s;
                            }


                            mensaje_imprimir = mensaje_imprimir + "\n\nPaga la cuota # " + numero_cuota + "\nde manera parcial.\nMonto abonado\ncuota #" +
                                    split_1[1] + " de " + total_cuotas + ":\n" + montoIngresado_s + " colones.\nSaldo pendiente\ncuota #" + split_1[1] + ":\n" +
                                    montoIngresadoT_s + " colones.\n";
                        }
                        if (Integer.parseInt(diferencia_fechas) > 0) {//Significa que esta atrasado.
                            morosidad = "M";
                            if (monto_ingresado > 0) {
                                mensaje_imprimir = mensaje_imprimir + "\n** *** ** *** ** *** ** *** **\nCuota # " + numero_cuota +
                                        " se encuentra atrasada!\n** *** ** *** ** *** ** *** **\n";
                            }
                        } else if (Integer.parseInt(diferencia_fechas) <= 0 ) {
                            morosidad = "D";
                        }
                        int saldo_cuadro = Integer.parseInt(split_1[2]);
                        saldo_cuadro = 0 - monto_temporal;//Did it:Que pasa si el monto ingresado es mayor al monto de una cuota o de todas juntas?
                        int monto_abono_c = Integer.parseInt(split_1[2]) - saldo_cuadro;
                        proximo_abono = split_1[3];
                        if (monto_ingresado > 0) {
                            String montoIngresado_s = String.valueOf(monto_ingresado);
                            char[] chars = montoIngresado_s.toCharArray();
                            if (chars.length == 1) {
                                montoIngresado_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }

                            String montoAbono_s = String.valueOf(monto_abono_c);
                            Log.v("Debug_datos", "\n\nmonto_abono_c: " + monto_abono_c + "\n\n.");
                            chars = montoAbono_s.toCharArray();
                            if (chars.length == 1) {
                                montoAbono_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                montoAbono_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                montoAbono_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                montoAbono_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                montoAbono_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                montoAbono_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                montoAbono_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }
                            Log.v("Debug_datos", "\n\nmontoAbono_s: " + montoAbono_s + "\n\n.");

                            String interes_mora_total_s = String.valueOf(interes_mora_total);
                            chars = interes_mora_total_s.toCharArray();
                            if (chars.length == 1) {
                                interes_mora_total_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }

                            String tempoFix_s = String.valueOf(monto_temporal_fix);
                            chars = tempoFix_s.toCharArray();
                            if (chars.length == 1) {
                                tempoFix_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                tempoFix_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                tempoFix_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }

                            mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ":\n" + montoAbono_s + " colones.\n";
                            mensaje_imprimir = mensaje_imprimir + "\n******************************\nMonto abonado:\n" + montoIngresado_s + " colones.\n";
                            mensaje_imprimir = mensaje_imprimir + "Intereses moratorios:\n" + interes_mora_total_s + " colones.\n";
                            mensaje_imprimir = mensaje_imprimir + "Abono al capital:\n" + tempoFix_s + " colones.\n******************************\n";
                            saldo_mas_intereses = saldo_mas_intereses - monto_temporal_fix;
                        }
                        interes_mora_total = "0";
                        cuadratura = cuadratura.replace(split_1[0] + "_" + split_1[1] + "_" + split_1[2] + "_" + split_1[3],
                                split_1[0] + "_" + split_1[1] + "_" + String.valueOf(saldo_cuadro) + "_" + split_1[3]);
                        flag = cuadratura;
                        monto_abono = monto_ingresado;
                        return flag;
                    } else if (monto_temporal > 0) {//Alcanza para pagar esta cuota y sobra. NO RETORNA!!! Debe continuar... (TODO: A no ser que el monto supere toda la deuda!!!)
                        cuadratura = cuadratura.replace(split_1[0] + "_" + split_1[1] + "_" + split_1[2] + "_" + split_1[3],
                                split_1[0] + "_" + split_1[1] + "_0_" + split_1[3]);//TODO: Hacer que if (i == split_length) {retornar_cambio}
                        if (i == (largo_split - 1)) {
                            proximo_abono = "Prestamo cancelado";
                            interes_mora_total = "0";
                            cambio = monto_temporal;//TODO: CORREGIR MONTO DISPONIBLE CUANDO SOBRA CAMBIO
                            actualizarCaja((0-cambio));
                            monto_disponible = String.valueOf(Integer.parseInt(monto_disponible) - cambio);
                            flag = cuadratura;
                            if (monto_ingresado > 0) {
                                if (cambio > 0) {
                                    String montoIngresado_s = String.valueOf(split_1[2]);
                                    Log.v("debug2", "\n\nsplit_1[2]: " + split_1[2] + "\n\n.");
                                    char[] chars = montoIngresado_s.toCharArray();
                                    if (chars.length == 1) {
                                        montoIngresado_s = String.valueOf(chars[0]) + ",00";
                                    } else if (chars.length == 2) {
                                        montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                                    } else if (chars.length == 3) {
                                        montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                                    } else if (chars.length == 4) {
                                        montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                                    } else if (chars.length == 5) {
                                        montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                                    } else if (chars.length == 6) {
                                        montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                                    } else if (chars.length == 7) {
                                        montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                                    }
                                    Log.v("debug2", "\n\nmontoIngresado_s: " + montoIngresado_s + "\n\n.");
                                    String montoCambio_s = String.valueOf(split_1[2]);
                                    chars = montoCambio_s.toCharArray();
                                    if (chars.length == 1) {
                                        montoCambio_s = String.valueOf(chars[0]) + ",00";
                                    } else if (chars.length == 2) {
                                        montoCambio_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                                    } else if (chars.length == 3) {
                                        montoCambio_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                                    } else if (chars.length == 4) {
                                        montoCambio_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                                    } else if (chars.length == 5) {
                                        montoCambio_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                                    } else if (chars.length == 6) {
                                        montoCambio_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                                    } else if (chars.length == 7) {
                                        montoCambio_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                                    }
                                    mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ":\n" + montoIngresado_s +
                                            " colones.\nSaldo pendiente\ncuota #" + split_1[1] +
                                            ": 0,00 colones.\nCambio:\n" + montoCambio_s + " colones.\n";
                                } else {
                                    String montoIngresado_s = String.valueOf(split_1[2]);
                                    char[] chars = montoIngresado_s.toCharArray();
                                    Log.v("debug3", "\n\nsplit_1[2]: " + split_1[2] + "\n\n.");
                                    if (chars.length == 1) {
                                        montoIngresado_s = String.valueOf(chars[0]) + ",00";
                                    } else if (chars.length == 2) {
                                        montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                                    } else if (chars.length == 3) {
                                        montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                                    } else if (chars.length == 4) {
                                        montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                                    } else if (chars.length == 5) {
                                        montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                                    } else if (chars.length == 6) {
                                        montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                                    } else if (chars.length == 7) {
                                        montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                                    }
                                    Log.v("debug3", "\n\nmontoIngresado_s: " + montoIngresado_s + "\n\n.");

                                    mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ":\n" +
                                            montoIngresado_s + " colones.\nSaldo pendiente\ncuota #" + split_1[1] + ": 0,00 colones.\n";
                                }

                                String interes_mora_total_s = String.valueOf(interes_mora_total);
                                char[] chars = interes_mora_total_s.toCharArray();
                                if (chars.length == 1) {
                                    interes_mora_total_s = String.valueOf(chars[0]) + ",00";
                                } else if (chars.length == 2) {
                                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                                } else if (chars.length == 3) {
                                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                                } else if (chars.length == 4) {
                                    interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                                } else if (chars.length == 5) {
                                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                                } else if (chars.length == 6) {
                                    interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                                } else if (chars.length == 7) {
                                    interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                                }

                                String tempoFix_s = String.valueOf(monto_temporal_fix - cambio);
                                tempoFix_s = tempoFix_s.replace("-", "");
                                chars = tempoFix_s.toCharArray();
                                if (chars.length == 1) {
                                    tempoFix_s = String.valueOf(chars[0]) + ",00";
                                } else if (chars.length == 2) {
                                    tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                                } else if (chars.length == 3) {
                                    tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                                } else if (chars.length == 4) {
                                    tempoFix_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                                } else if (chars.length == 5) {
                                    tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                                } else if (chars.length == 6) {
                                    tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                                } else if (chars.length == 7) {
                                    tempoFix_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                                }

                                if ((monto_temporal_fix - cambio) < 0) {
                                    tempoFix_s = "-" + tempoFix_s;
                                }

                                String montoIngresado_s = String.valueOf(monto_ingresado - cambio);
                                montoIngresado_s = montoIngresado_s.replace("-", "");
                                chars = montoIngresado_s.toCharArray();
                                if (chars.length == 1) {
                                    montoIngresado_s = String.valueOf(chars[0]) + ",00";
                                } else if (chars.length == 2) {
                                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                                } else if (chars.length == 3) {
                                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                                } else if (chars.length == 4) {
                                    montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                                } else if (chars.length == 5) {
                                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                                } else if (chars.length == 6) {
                                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                                } else if (chars.length == 7) {
                                    montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                                }

                                if ((monto_ingresado - cambio) < 0) {
                                    montoIngresado_s = "-" + montoIngresado_s;
                                }

                                mensaje_imprimir = mensaje_imprimir + "\n******************************\nMonto abonado:\n" + montoIngresado_s +
                                        " colones.\n";
                                mensaje_imprimir = mensaje_imprimir + "Intereses moratorios:\n" + interes_mora_total_s + " colones.\n";
                                mensaje_imprimir = mensaje_imprimir + "Abono al capital:\n" + tempoFix_s +
                                        " colones.\n\n******************************\n\nFecha proximo abono:\n" + proximo_abono + "\n";
                                saldo_mas_intereses = saldo_mas_intereses - (monto_temporal_fix - cambio);
                            }
                            monto_abono = monto_ingresado - cambio;
                            return flag;
                        } else {
                            if (monto_ingresado > 0) {

                                String montoIngresado_s = String.valueOf(split_1[2]);
                                char[] chars = montoIngresado_s.toCharArray();
                                Log.v("debug4", "\n\nsplit_1[2]: " + split_1[2] + "\n\nchars.length: " + chars.length + " \n\n.");
                                for (char c : chars) {
                                    Log.v("forDebug_1", "char: " + c);
                                }
                                if (chars.length == 1) {
                                    montoIngresado_s = String.valueOf(chars[0]) + ",00";
                                } else if (chars.length == 2) {
                                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                                } else if (chars.length == 3) {
                                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                                } else if (chars.length == 4) {
                                    montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                                } else if (chars.length == 5) {
                                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                                } else if (chars.length == 6) {
                                    montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                                } else if (chars.length == 7) {
                                    montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                                }
                                Log.v("debug4", "\n\nmontoIngresado_s: " + montoIngresado_s + "\n\n.");


                                mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ":\n" +
                                        montoIngresado_s + " colones.\nSaldo pendiente\ncuota #" + split_1[1] + ": 0,00 colones.\n";
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
                            fecha_cuadrito = DateUtilities.dateToString(proximo_abono_LD);
                            String[] split_fe_cua = fecha_cuadrito.split("-");
                            fecha_cuadrito = split_fe_cua[2] + "/" + split_fe_cua[1] + "/" + split_fe_cua[0];
                            proximo_abono = fecha_cuadrito;
                        }
                        if (monto_ingresado > 0) {

                            String montoIngresado_s = String.valueOf(split_1[2]);
                            char[] chars = montoIngresado_s.toCharArray();
                            Log.v("debug5", "\n\nsplit_1[2]: " + split_1[2] + "\n\nchars.length: " + chars.length + " \n\n.");
                            for (char c : chars) {
                                Log.v("forDebug_2", "char: " + c);
                            }
                            if (chars.length == 1) {
                                montoIngresado_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }
                            Log.v("debug5", "\n\nmontoIngresado_s: " + montoIngresado_s + "\n\n.");

                            String montoIngresadRo_s = String.valueOf(monto_ingresado);
                            chars = montoIngresadRo_s.toCharArray();
                            if (chars.length == 1) {
                                montoIngresadRo_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                montoIngresadRo_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                montoIngresadRo_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                montoIngresadRo_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                montoIngresadRo_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                montoIngresadRo_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                montoIngresadRo_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }

                            String interes_mora_total_s = String.valueOf(interes_mora_total);
                            chars = interes_mora_total_s.toCharArray();
                            if (chars.length == 1) {
                                interes_mora_total_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                interes_mora_total_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                interes_mora_total_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }

                            String tempoFix_s = String.valueOf(monto_temporal_fix);
                            chars = tempoFix_s.toCharArray();
                            if (chars.length == 1) {
                                tempoFix_s = String.valueOf(chars[0]) + ",00";
                            } else if (chars.length == 2) {
                                tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
                            } else if (chars.length == 3) {
                                tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
                            } else if (chars.length == 4) {
                                tempoFix_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
                            } else if (chars.length == 5) {
                                tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
                            } else if (chars.length == 6) {
                                tempoFix_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
                            } else if (chars.length == 7) {
                                tempoFix_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
                            }


                            mensaje_imprimir = mensaje_imprimir + "\nMonto abonado\ncuota #" + split_1[1] + " de " + total_cuotas + ":\n" + montoIngresado_s +
                                    " colones.\nSaldo pendiente\ncuota #" + split_1[1] + ": 0,00 colones\n";
                            mensaje_imprimir = mensaje_imprimir + "\n******************************\nMonto abonado:\n" + montoIngresadRo_s + " colones.\n";
                            mensaje_imprimir = mensaje_imprimir + "Intereses moratorios:\n" + interes_mora_total_s + " colones.\n";
                            mensaje_imprimir = mensaje_imprimir + "Abono al capital:\n" + tempoFix_s +
                                    " colones.\n******************************\n\nFecha proximo abono:\n" + proximo_abono + "\n";
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
                }
            }
            Log.v("Obtener_cuadratura_pF", ".\n\nERROR EN RETORNO\n\nContenido del archivo: \n\n" + imprimir_archivo(archivo_prestamo) +
                    "\n\ncuadratura:\n\n" + cuadratura + "\n\n.");
            return cuadratura;
        } else {
            Log.v("Obtener_cuadratura_else", ".\n\nERROR EN DATO DE ARCHIVO\n\nContenido del archivo: \n\n" +
                    imprimir_archivo(archivo_prestamo) + "\n\n.");
        }
        return flag;
    }

    private void presentar_monto_a_pagar (int monto_a_pagar, int interes_mora_total, int montoAPagar) {
        intereses_moratorios_hoy = interes_mora_total;
        et_ID.setEnabled(true);
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
        if (interes_mora_total > 0) {
            bt_perdon.setEnabled(true);
            bt_perdon.setClickable(true);
            chb_adelantarIntereses.setVisibility(View.INVISIBLE);
        } else {
            bt_perdon.setEnabled(false);
            bt_perdon.setClickable(false);
            chb_adelantarIntereses.setVisibility(View.VISIBLE);
            chb_adelantarIntereses.setChecked(false);
        }
    }

    private Integer obtener_monto_cuota (String s) {
        int flag = 0;
        String[] split = s.split("#");
        s = split[1];
        String archivos[] = fileList();
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("obtener_monto_cuota.1", "********ERROR**************Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
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
                                if (splitre[0].equals("monto_cuota")) {
                                    monto_cuota = Integer.parseInt(splitre[1]);
                                    flag = monto_cuota;
                                }
                                linea = br.readLine();
                            }
                            br.close();
                            archivo.close();
                        }
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
                        }else {
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

    private void presentar_info_credito (String s) throws IOException, InterruptedException {
        if (s.equals("UNO")) {
            String archivos[] = fileList();
            String file_to_consult = "";
            if (flag_client_reciv) {
                file_to_consult = archivo_prestamo;
            } else {
                file_to_consult = archivo_prestamo;
            }
            if (file_to_consult.contains("*") || file_to_consult.contains(" ")) {
                Log.v("presentar_info_credito0", "*****ERROR*********Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            } else {
                for (int i = 0; i < archivos.length; i++) {
                    Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(archivos[i]);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
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
                            archivoCredito = archivos[i];
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            while (linea != null) {
                                String[] split = linea.split("_separador_");
                                if (split[0].equals("proximo_abono")) {
                                    fecha_next_abono = split[1];
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
                            String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                            interes_mora_total = intereses_moritas;
                            interes_mora_parcial = interes_mora_total;
                            cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0, fecha_credito, "no");
                            intereses_monroe = Integer.parseInt(intereses_mor_archivo);//Son los intereses guardados en el archivo. calculados en un periodo que se abono solo parte de los intereses.
                            saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor_archivo, monto_prestado);
                            cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup);
                            valor_presentar_s = "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas;
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
            String file_to_consult;
            if (flag_client_reciv) {
                file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            } else {
                file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            }
            if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
                Log.v("present_info_credito12", "**********ERROR************Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            } else {
                for (int i = 0; i < archivos.length; i++) {
                    Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(archivos[i]);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        try {
                            archivoCredito = archivos[i];
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
                                String[] split = linea.split("_separador_");
                                if (split[0].equals("proximo_abono")) {
                                    fecha_next_abono = split[1];
                                }
                                if (split[0].equals("plazo")) {
                                    plazoz = split[1];
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
                                if (split[0].equals("saldo_mas_intereses")) {
                                    saldo_mas_intereses_s = split[1];
                                }
                                if (split[0].equals("cuotas")) {
                                    cuotas_morosas = split[1];
                                    total_cuotas = Integer.parseInt(cuotas_morosas);
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
                            String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                            interes_mora_total = intereses_moritas;
                            interes_mora_parcial = interes_mora_total;
                            cuadratura_pre = obtener_cuadratura(cuadratura_pre, fecha_next_abono, factor_semanas, 0, fecha_credito, "no");
                            intereses_monroe = Integer.parseInt(intereses_mor);
                            saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor, monto_prestado);
                            cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup);
                            valor_presentar_s = "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas;
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
                            consultar(null);;
                        } catch (IOException e) {
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
        if (next_pay.equals("Prestamo cancelado")) {
            next_pay = dia + "/" + mes + "/" + anio;
        }
        String flag = "";
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        int diferencia_en_dias = DateUtilities.daysBetween(hoy_LD, proximo_abono_LD);
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            morosidad = "D";
            interes_mora_parcial = "0";
        } else {//Significa que esta atrazado!!!
            double pre_num0 = diferencia_en_dias * (Integer.parseInt(interes_mora)) * (Integer.parseInt(saldo_plus));
            double pre_num = pre_num0 / 100;
            int pre_num_int = (int) pre_num;
            if (pre_num_int > 0) {
                morosidad = "M";
                interes_mora_parcial = String.valueOf(pre_num_int);
            } else {
                interes_mora_parcial = "0";
            }
        }
        interes_mora_total = interes_mora_parcial;
        verificarAbonoAdelanto();
        flag = interes_mora_parcial;
        return flag;
    }

    private void verificarAbonoAdelanto () {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(archivoCredito));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split("_separador_");
                if (split[0].equals("intereses_moratorios")) {
                    int intereses_mor_archivo = Integer.parseInt(split[1]);
                    int intereses_morato_archivo;
                    if (intereses_mor_archivo >= 0) {
                        intereses_morato_archivo = Integer.valueOf(interes_mora_total);
                    } else {
                        intereses_morato_archivo = intereses_mor_archivo + Integer.valueOf(interes_mora_total);
                    }
                    interes_mora_total = String.valueOf(intereses_morato_archivo);
                    interes_mora_parcial = interes_mora_total;
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

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
                    if (!String.valueOf(s).equals("")) {
                        bt_consultar.setClickable(true);
                        bt_consultar.setEnabled(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
                    et_ID.setEnabled(true);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();
                    String archivos[] = fileList();
                    if (et_ID.getText().toString().contains("*") || et_ID.getText().toString().contains(" ")) {
                        Log.v("text_listener0.1", "*****ERROR***Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                    } else {
                        for (int i = 0; i < archivos.length; i++) {
                            Pattern pattern = Pattern.compile(et_ID.getText().toString(), Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(archivos[i]);
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

    //Metodos comunes//

    private void esperar (String s) {
        ocultar_todito();
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
        }
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
            String archivoCreado = new CrearArchivo(file_name, getApplicationContext()).getFile();
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
            activity_volver.putExtra("cliente_ID", cliente_recibido);
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else {
            //Do nothing.
        }
    }

    private void ocultar_todito () {
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

}