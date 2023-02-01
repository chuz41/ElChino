package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;
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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.CrearArchivo;
import com.example.elchino.Util.DateUtilities;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Re_financiarActivity extends AppCompatActivity {

    private Integer monto_abono = 0;
    private String interes_mora_parcial = "";
    private Integer cambio = 0;
    private Integer monto_cuota = 0;
    private Integer monto_credito = 0;
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
    private String mes;
    private String anio;
    private String fecha;
    private String hora;
    private String dia;
    private String minuto;
    private Button bt_consultar;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private String monto_disponible = "0";
    private boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private String abono_cero = "0";
    private String caja = "caja.txt";
    private TextView tv_caja;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_re_financiar);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        if (mensaje_recibido.equals("")) {
            //Do nothing.
        } else {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        abono_cero = getIntent().getStringExtra( "abono_cero");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        bt_consultar = (Button) findViewById(R.id.bt_consultar_ab);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("RE-FINANCIAR CREDITO");
        sp_plazos = (Spinner) findViewById(R.id.sp_plazos);
        sp_plazos.setVisibility(View.INVISIBLE);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");
        mostrar_caja();
        separarFecha();

        if (cliente_recibido.equals("")) {
            //Do nothing.
        } else if (cliente_recibido.equals("CERO")) {

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

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(null);
        hora = datosFecha.getHora();
        minuto = datosFecha.getMinuto();
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        dia = datosFecha.getDia();
        fecha = dia;
    }

    private void mostrar_caja () {
        tv_caja.setText(imprimir_archivo(caja));
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
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            //Do nothing.
        } else {
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
                        String file_name = archivos[i];
                        String cuadratura_pre = "";
                        int factor_semanas = 0;
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

                        Log.v("llenando_spinner2", "Re-financiar.\n\nMorosidad: " + morosidad + "\n\n.");
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

    private String obtener_saldo_al_dia (String saldo_plus, String next_pay, String intereses_de_mora) throws ParseException {
        String flag = "";
        String saldo = "";
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        Date fecha_hoy = Calendar.getInstance().getTime();
        int diferencia_en_dias = DateUtilities.daysBetween(fecha_hoy, proximo_abono_LD);
        Log.v("obt_sald_al_dia0", ".\n\nDiferencia en dias: " + diferencia_en_dias + "\n\nnext_pay: " + next_pay + "\n\nIntereses de mora: " + intereses_de_mora + "\n\nSaldo_plus: " + saldo_plus + "\n\n.");
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            saldo = saldo_plus;
            morosidad = "D";
        } else {//Significa que esta atrazado!!!
            double pre_saldo = diferencia_en_dias * (Integer.parseInt(interes_mora)) * Integer.parseInt(saldo_plus);
            pre_saldo = pre_saldo / 100;
            saldo = String.valueOf(Integer.parseInt(saldo_plus) + (pre_saldo) + Integer.parseInt(intereses_de_mora));//No se suman intereses sobre los intereses moratorios, pero si sobre el interes acordado del credito!!!
            Log.v("obt_saldo_al_dia1", "Abonar.\n\nSaldo: " + saldo + "\n\n.");
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

    private String obtener_intereses_moratorios (String saldo_plus, String next_pay) throws ParseException {
        String flag = "";
        String saldo = "";
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Log.v("obt_int_morat0", ".\n\nRe-financiar. Proximo abono: " + proximo_abono_formato + "\n\n.");
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        Date fecha_hoy = Calendar.getInstance().getTime();
        int diferencia_en_dias = DateUtilities.daysBetween(fecha_hoy, proximo_abono_LD);
        Log.v("obt_int_morat1", ".\n\nRe-financiar. Diferencia en dias: " + diferencia_en_dias + "\n\nfecha_hoy: " + fecha_hoy.toString() + "\n\nProximo abono: " + proximo_abono_LD + "\n\n.");
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            saldo = saldo_plus;
            morosidad = "D";
            interes_mora_parcial = "0";
        } else {//Significa que esta atrazado!!!

            //saldo = String.valueOf(Integer.parseInt(saldo_plus) + (diferencia_en_dias * ((Integer.parseInt(interes_mora))/100) * Integer.parseInt(saldo_plus)));//No se suman intereses sobre los intereses moratorios, pero si sobre el interes acordado del credito!!!
            Log.v("obt_int_morat_late1", ".\n\nRe-financiar. Diferencia en dias: " + diferencia_en_dias + "\n\ninteres_mora: " + interes_mora + "\n\nSaldo_plus: " + saldo_plus + "\n\n.");
            double pre_num0 = diferencia_en_dias * (Integer.parseInt(interes_mora)) * (Integer.parseInt(saldo_plus));
            double pre_num = pre_num0 / 100;
            int pre_num_int = (int) pre_num;
            Log.v("obt_int_morat_late2", ".\n\nRe-financiar. pre_num_int: " + pre_num_int + "\n\n.");
            if (pre_num_int > 0) {
                morosidad = "M";
                interes_mora_parcial = String.valueOf(pre_num_int);
            } else {
                interes_mora_parcial = "0";
            }

        }
        flag = interes_mora_parcial;
        interes_mora_total = interes_mora_parcial;
        Log.v("obt_int_morat2", ".\n\nRe-financiar. intereses moratorios: " + interes_mora_parcial + "\n\n.");
        return flag;
    }

    private boolean revisar_creditos () {
        boolean flasg = false;
        String flag = "";
        lista_archivos = "";
        String cliente_file = cliente_ID + "_C_.txt";
        String archivos[] = fileList();
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            //Do nothing.
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
            Log.v("revisar_creditos", ".\n\nRe-financiar. Archivo correcto: " + split[0] + "\n\n.");
            archivo_prestamo = split[0];
            Log.v("revisar_creditos_F", ".\n\nRe-financiar. Contenido del archivo " + archivo_prestamo + ":\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
        } else {
            flasg = true;
            cantidad_de_creditos = 2;//Significa que son 2 o mas creditos. Se debe activar el spinner.
        }
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
                Log.v("consultar_0", "Re_financiar.\n\nClienteID: " + cliente_ID + "\n\n");
                //Do nothing.
            } else {
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
                                Log.v("consultar_1", "Re_financiar.\n\nlinea:\n\n" + linea + "\n\n.");
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
            Log.v("Prestamo_a_consultar", ".\n\nPrestamo que se va a abonar: " + et_ID.getText().toString() + "\n\n.");
            String[] parts_prestamo = et_ID.getText().toString().split(" ");
            int monto_a_pagar = 0;
            cantidad_cuotas_pendientes = Integer.parseInt(parts_prestamo[3]);
            morosidad = parts_prestamo[2];
            monto_cuota = obtener_monto_cuota(parts_prestamo[0]);
            //archivo_prestamo = file_name; Checked!!!
            if (Integer.parseInt(parts_prestamo[3]) == 0) {
                monto_a_pagar = monto_cuota;
            } else {
                //morosidad
                if (morosidad.equals("D")) {
                    monto_a_pagar = cantidad_cuotas_pendientes * monto_cuota;
                } else {
                    //monto a pagar
                    monto_a_pagar = cantidad_cuotas_pendientes * monto_cuota + Integer.parseInt(interes_mora_parcial);
                }
            }
            presentar_monto_a_pagar();

        } else {
            //TODO: no se sabe que hacer aqui!!!
        }
    }
    
    private void procesar_abono2 () {

        String file_name = archivo_prestamo;
        String contenido = "";
        String fecha_next_abono = "";
        int factor_semanas = 0;
        int monto_ingresado;// = Integer.parseInt(et_ID.getText().toString());

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
                } else if (split[0].equals("tasa")) {
                    tasa = Integer.parseInt(split[1]);
                } else if (split[0].equals("cuotas")) {
                    cuotas = split[1];
                } else if (split[0].equals("morosidad")) {
                    morosidad = split[1];
                //} else if (split[0].equals("intereses_moratorios")) {
                    //interes_mora_total = split[1];
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

            saldo_mas_intereses = Integer.parseInt(obtener_saldo_plus(cuadratura)) + Integer.parseInt(interes_mora_total);
            monto_abono = saldo_mas_intereses;
            Log.v("procesar_abono_refin", ".\n\nRe-financiar. Antes de cuadra changes. Saldo mas intereses: " + saldo_mas_intereses + "\n\ninteres mora total: " + interes_mora_total + "\n\nCuadratura:\n\n" + cuadratura + "\n\n.");
            actualizarCaja(saldo_mas_intereses);
            monto_ingresado = saldo_mas_intereses;
            Log.v("antes_de_cuadra_chang", ".\n\nRe-financiar. Archivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
            cuadratura = obtener_cuadratura(cuadratura, fecha_next_abono, factor_semanas, monto_ingresado);//Aqui se obtiene la verdadera y final morosidad.
            interes_mora_total = "0";
            Log.v("despues_de_cuadra_chang", ".\n\nRe-financiar. Archivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
            Log.v("procesar_abono_refin", ".\n\nRe-financiar. Despues de cuadra changes. Saldo mas intereses: " + saldo_mas_intereses + "\n\ninteres mora total: " + interes_mora_total + "\n\nCuadratura:\n\n" + cuadratura + "\n\n.");
            cuotas = obtener_cuotas_nuevas(cuadratura);
            //saldo_mas_intereses = Integer.parseInt(obtener_saldo_plus(cuadratura));
            actualizar_archivo_credito();


        } catch (IOException e) {
        } catch (ParseException e) {
            e.printStackTrace();
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
            //subir_solicitud(monto_digitado, mensaje_solicitud);
            actualizar_archivo_cliente(archivo_prestamo);
        } catch (IOException | JSONException | ParseException e) {
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
        new AgregarLinea(linea_cierre, "cierre.txt", getApplicationContext());
    }

    private void actualizar_archivo_cliente (String archivo_P) throws JSONException, IOException, ParseException {
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
            String archivoCreado = new CrearArchivo(archivo_cliente, getApplicationContext()).getFile();
            Log.v("actualiz_archiv_client2", "Abonar.\n\nResultado de la creacion del archivo:\n\n" + archivoCreado + "\n\n.");
            if (new GuardarArchivo(archivo_cliente, contenido, getApplicationContext()).guardarFile()) {
                Log.v("actualiz_archiv_client3", "Abonar.\n\nContenido del archivo:\n\n" + imprimir_archivo(archivoCreado) + "\n\n.");
            } else {
                Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            }
            Log.v("actualiz_archiv_client2", ".\n\nAbonar. Archivo: " + archivo_cliente + "\n\nContenido del archivo:\n\n" +
                    imprimir_archivo(archivo_cliente) + "\n\n.");
        } catch (IOException e) {
        }
        renovar_credito(archivo_P);
    }
    
    private void renovar_credito (String file) throws IOException {
        String file_content = "";
        file_content = file_content + "monto_credito_separador_" + monto_credito + "\n";
        Log.v("generar_credito_RE", ".\n\nPlazo: " + plazo + "\n\n.");
        String plazo_presentar = plazo;
        file_content = file_content + "plazo_separador_" + plazo_presentar + "\n";
        file_content = file_content + "monto_cuota_separador_" + monto_cuota + "\n";
        String fecha_credito = dia + "/" + mes + "/" + anio;
        file_content = file_content + "fecha_credito_separador_" + fecha_credito + "\n";
        String proximo_abono = obtener_proximo_abono();
        file_content = file_content + "proximo_abono_separador_" + proximo_abono + "\n";
        String saldo_mas_intereses = calcular_saldo();
        file_content = file_content + "saldo_mas_intereses_separador_" + saldo_mas_intereses + "\n";
        String tasa_interes = obtener_tasa();
        file_content = file_content + "tasa_separador_" + tasa_interes + "\n";
        String cuotass = calcular_cuotas();
        file_content = file_content + "cuotas_separador_" + cuotass + "\n";
        file_content = file_content + "ID_credito_separador_" + credit_ID + "\n";
        String morosidad = "D";
        file_content = file_content + "morosidad_separador_" + morosidad + "\n";
        String sema_quince = "";
        int factor = 0;
        String[] split = plazo_presentar.split("_");
        if (split[1].equals("semanas")) {
            sema_quince = "semana";
            factor = 1;
        } else if (split[1].equals("quincenas")) {
            sema_quince = "quincena";
            factor = 2;
        } else {
            //do nothing here!!
        }
        Date fecha_hoy = Calendar.getInstance().getTime();
        Date fecha_poner = fecha_hoy;
        cuadratura = "";
        for (int i = 0; i < Integer.parseInt(cuotass); i++) {

            fecha_poner = DateUtilities.addWeeks(fecha_poner, factor);
            String[] splet = fecha_poner.toString().split("-");
            String fecha_S_poner = splet[2] + "/" + splet[1] + "/" + splet[0];
            cuadratura = cuadratura + sema_quince + "_" + String.valueOf(i + 1) + "_" + monto_cuota + "_" + fecha_S_poner + "__";
        }
        file_content = file_content + "cuadratura_separador_" + cuadratura + "\n";
        file_content = file_content + "intereses_moratorios_separador_0";
        String file_name = file;
        new BorrarArchivo(file, getApplicationContext());
        new CrearArchivo(file_name, getApplicationContext());
        if (new GuardarArchivo(file_name, file_content, getApplicationContext()).guardarFile()) {
            Log.v("restar_disponible_2", "Nuevo_credito.\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
        } else {
            Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
        }
        actualizarCaja((0-monto_credito));
        monto_credito = monto_credito - monto_abono;
        Log.v("antes_de_subir", ".\n\nRe_financiar. Archivo a subir: " + file_name + "\n\nContenido de " + file_name + ":\n\n" + imprimir_archivo(file_name) + "\n\n.");
        //subir_archivo(file_name);
    }

    private void presentar_cuadratura () {
        //TODO: llamar a la activity estado_de_cuenta
        Intent CuadraturaAc = new Intent(this, CuadraturaActivity.class);
        CuadraturaAc.putExtra("cuadratura", cuadratura);
        CuadraturaAc.putExtra("msg", "Operacion realizada con exito!!!");
        CuadraturaAc.putExtra("cliente_recivido", cliente_ID);
        CuadraturaAc.putExtra("cambio", "0");
        CuadraturaAc.putExtra("monto_creditito", String.valueOf(monto_credito));
        CuadraturaAc.putExtra("activity_devolver", "MenuPrincipal");
        CuadraturaAc.putExtra("mensaje_imprimir_pre", "");
        startActivity(CuadraturaAc);
        finish();
        System.exit(0);
    }

    private String calcular_cuotas () {
        String flag = "";
        int cuotas = 0;
        String[] split = plazo.split("_");
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

    private String obtener_tasa () {
        String flag = "";
        int interes = 0;
        String[] split = plazo.split("_");
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

    private String calcular_saldo () {
        String flag = "";
        int interes = 0;
        String[] split = plazo.split("_");
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
        double monto_parcial = (monto_credito * interes);
        monto_parcial = monto_parcial / 100;
        double monto_total = monto_credito + monto_parcial;
        int flag_int = (int) monto_total;
        flag = String.valueOf(flag_int);
        return flag;
    }
    
    private String obtener_proximo_abono () {
        String flag = "";
        int factor_semanas = 0;
        String[] piezas = plazo.split("_");
        if (piezas[1].equals("quincenas")) {
            factor_semanas = 2;
        } else if (piezas[1].equals("semanas")) {
            factor_semanas = 1;
        } else {
            factor_semanas = -1;
            //flag = "ERROR";
        }
        Date fecha_hoy = Calendar.getInstance().getTime();
        Date fecha_mostrar2_D = DateUtilities.addWeeks(fecha_hoy, factor_semanas);
        String fecha_mostrar2 = DateUtilities.dateToString(fecha_mostrar2_D);
        String[] partes = fecha_mostrar2.split("-");
        fecha_mostrar2 = partes[2] + "/" + partes[1] + "/" + partes[0];
        flag = fecha_mostrar2;

        return flag;
    }

    private String obtener_saldo_plus (String cuadratura) {
        String flag = "";

        String[] split = cuadratura.split("__");
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

    private String obtener_cuotas_nuevas(String cuadratura) {

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

    private String obtener_cuadratura (String cuadratura, String fecha_next_abono, int factor_semanas, int monto_ingresado) throws ParseException, IOException {

        String flag = "";
        int monto_temporal = monto_ingresado - Integer.parseInt(interes_mora_total);

        if (monto_temporal < 0) {//No alcanzo siquiera para pagar los intereses. Debe retornar


            Date hoy_LD = Calendar.getInstance().getTime();
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
            //monto_disponible = "0";
            morosidad = "M";
            flag = cuadratura;//TODO: No se le ha hecho nada a cuadratura :-( (Porque no hay que hacerle nada!!!)
            return flag;


        } else if (monto_temporal == 0) {//Aqui paga el monto completo, solo de los intereses moratorios, no abona nada a los abonos ordinarios. Debe retornar

            flag = cuadratura;//TODO: No se le ha hecho nada a cuadratura :-( (Porque no hay que hacerle nada!!!)
            Date proximo_abono_D = Calendar.getInstance().getTime();
            proximo_abono = DateUtilities.dateToString(proximo_abono_D);
            String[] split = proximo_abono.split("-");
            proximo_abono = split[2] + "/" + split[1] + "/" + split[0];
            morosidad = "M";
            //monto_disponible = "0";
            interes_mora_total = "0";
            return flag;

        } else if (monto_temporal > 0) {//Aqui paga el monto de los intereses y ademas, paga tambien parte o to-do lo de las cuotas pendientes y/o futuras.

            String[] split = cuadratura.split("__");

            double factor = tasa + 100;
            double x = monto_temporal * 100;
            x = x / factor;
            double debuge = factor;
            double restar_disponibleL =  monto_temporal - x;
            restar_disponibleL = restar_disponibleL / 100;
            Log.v("debug_cuadra1_pre_pre", ".\n\nRe-financiar. \n\nRestar disponible: " + restar_disponibleL + "\n\nDebuge: " + debuge + "\n\n.");
            int restar_disponible = (int) restar_disponibleL;
            int xx = (int) x;
            float monto_disponible_F = Float.parseFloat(monto_disponible);
            int monto_disponible_I = (int) monto_disponible_F;
            monto_disponible = String.valueOf(monto_disponible_I);
            monto_disponible = String.valueOf((Integer.parseInt(monto_disponible) + xx));
            Log.v("debug_cadra1", ".\n\nMonto temporal: " + monto_temporal + "\n\nTasa: " + tasa + "\n\nRestar disponible: " + restar_disponible + "\n\nMonto disponible: " + monto_disponible + "\n\n.");
            int largo_split = split.length;
            for (int i = 0; i < largo_split; i++) {

                String[] split_1 = split[i].split("_");

                if (Integer.parseInt(split_1[2]) > 0) {//Significa que tiene esta cuota pendiente.

                    monto_temporal = monto_temporal - Integer.parseInt(split_1[2]);//Esta es la cantidad que va quedando del abono.

                    if (monto_temporal < 0) {//Significa que no alcanza para esta cuota. Debe retornar
                        Date hoy_LD = Calendar.getInstance().getTime();
                        String fecha_cuadrito = split_1[3];
                        String[] split_fec = fecha_cuadrito.split("/");
                        fecha_cuadrito = split_fec[2] + "-" + split_fec[1] + "-" + split_fec[0];
                        Date fecha_cuadrito_LD = DateUtilities.stringToDate(fecha_cuadrito);
                        String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_cuadrito_LD));
                        if (Integer.parseInt(diferencia_fechas) > 0) {//Significa que esta atrasado.
                            morosidad = "M";
                        } else if (Integer.parseInt(diferencia_fechas) <= 0 ) {
                            morosidad = "D";
                        } else {
                            //Do nothing.
                        }
                        int saldo_cuadro = Integer.parseInt(split_1[2]);
                        saldo_cuadro = 0 - monto_temporal;//TODO:Que pasa si el monto ingresado es mayor al monto de una cuota o de todas juntas?
                        proximo_abono = split_1[3];
                        interes_mora_total = "0";
                        cuadratura = cuadratura.replace(split_1[0] + "_" + split_1[1] + "_" + split_1[2] + "_" + split_1[3],
                                split_1[0] + "_" + split_1[1] + "_" + String.valueOf(saldo_cuadro) + "_" + split_1[3]);
                        flag = cuadratura;
                        return flag;
                    } else if (monto_temporal > 0) {//Alcanza para pagar esta cuota y sobra. NO RETORNA!!! Debe continuar... (TODO: A no ser que el monto supere toda la deuda!!!)
                        //
                        cuadratura = cuadratura.replace(split_1[0] + "_" + split_1[1] + "_" + split_1[2] + "_" + split_1[3],
                                split_1[0] + "_" + split_1[1] + "_0_" + split_1[3]);//TODO: Hacer que if (i == split_length) {retornar_cambio}
                        if (i == (largo_split - 1)) {
                            cambio = monto_temporal;
                            actualizarCaja((0-cambio));
                            monto_disponible = String.valueOf(Integer.parseInt(monto_disponible) - cambio);
                            flag = cuadratura;
                            return flag;
                        } else {
                            //Do nothing.
                        }
                    } else if (monto_temporal == 0) {//Alcanza para pagar esta cuota pero no sobra nada. Debe retornar!!!
                        //
                        cuadratura = cuadratura.replace(split_1[0] + "_" + split_1[1] + "_" + split_1[2] + "_" + split_1[3],
                                split_1[0] + "_" + split_1[1] + "_0_" + split_1[3]);
                        Date hoy_LD = Calendar.getInstance().getTime();
                        String fecha_cuadrito = split_1[3];
                        String[] split_fec = fecha_cuadrito.split("/");
                        fecha_cuadrito = split_fec[2] + "-" + split_fec[1] + "-" + split_fec[0];
                        Date fecha_cuadrito_LD = DateUtilities.stringToDate(fecha_cuadrito);
                        String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_cuadrito_LD));
                        if (Integer.parseInt(diferencia_fechas) > 0) {//Significa que esta atrasado. Pago todos los intereses, pero sigue atrasado. proximo_abono = hoy.
                            morosidad = "M";
                            String fecha_de_hoy = DateUtilities.dateToString(hoy_LD);
                            String[] split_hoy = fecha_de_hoy.split("-");
                            fecha_de_hoy = split_hoy[2] + "/" + split_hoy[1] + "/" + split_hoy[0];
                            proximo_abono = fecha_de_hoy;
                        } else if (Integer.parseInt(diferencia_fechas) <= 0 ) {
                            morosidad = "D";
                            Date proximo_abono_LD = DateUtilities.addWeeks(fecha_cuadrito_LD, factor_semanas);
                            fecha_cuadrito = DateUtilities.dateToString(proximo_abono_LD);
                            String[] split_fe_cua = fecha_cuadrito.split("-");
                            fecha_cuadrito = split_fe_cua[2] + "/" + split_fe_cua[1] + "/" + split_fe_cua[0];
                            proximo_abono = fecha_cuadrito;
                        } else {
                            //Do nothing.
                        }

                        interes_mora_total = "0";
                        flag = cuadratura;
                        return flag;
                    }

                } else if (Integer.parseInt(split_1[2]) < 0) {//Nunca debe ser negativo el monto pendiente
                    Log.v("Obtener_cuadratura", ".\n\nERROR EN DATO DE ARCHIVO\n\nContenido del archivo: \n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
                } else if (Integer.parseInt(split_1[2]) == 0) {//Esta cuota ya ha sido pagada, continuar...
                    //Do nothing. Continue...
                }
            }
            Log.v("Obtener_cuadratura_pF", ".\n\nERROR EN RETORNO\n\nContenido del archivo: \n\n" + imprimir_archivo(archivo_prestamo) + "\n\ncuadratura:\n\n" + cuadratura + "\n\n.");
            return cuadratura;
        } else {
            //Do nothing. Never come here!!!
            Log.v("Obtener_cuadratura_else", ".\n\nERROR EN DATO DE ARCHIVO\n\nContenido del archivo: \n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
        }

        return flag;

    }

    private void presentar_monto_a_pagar () throws JSONException, IOException, InterruptedException {
        tv_esperar.setText("Monto a pagar al dia de hoy: ");
        tv_esperar.setVisibility(View.VISIBLE);
        consultar(null);
    }

    private Integer obtener_monto_cuota (String s) {
        int flag = 0;
        String[] split = s.split("#");
        Log.v("obt_monto_cuota", ".\n\nString: "+ s + "\n\nSplit[0]: " + split[0] + "\n\nSplit[1]: " + split[1] + "\n\n.");
        String archivos[] = fileList();
        s = split[1];
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            //Do nothing.
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
            //String puntuacion_cliente = "";
            //String archivoCompleto = "";
            String file_to_consult = "";

            if (flag_client_reciv) {
                file_to_consult = archivo_prestamo;
            } else {
                file_to_consult = archivo_prestamo;
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
                            String fecha_next_abono = "";
                            String intereses_mor = "";
                            String saldo_mas_intereses_s = "";
                            String plazoz = "";
                            String numero_de_credito = "";
                            String cuotas_morosas = "";
                            String valor_presentar_s = "";
                            //String indice_file = "";
                            String file_name = archivos[i];
                            String[] split_indice = file_name.split("_P_");
                            numero_de_credito = split_indice[1];
                            InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            String cuadratura_pre = "";
                            int factor_semanas = 0;
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
            //String puntuacion_cliente = "";
            //String archivoCompleto = "";
            String file_to_consult = "";
            Log.v("valor_a_presentar", ".\n\nnum_credit: " + num_credit + "\n\n.");
            if (flag_client_reciv) {
                file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            } else {
                file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            }
            if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
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
                            String fecha_next_abono = "";
                            String intereses_mor = "";
                            String saldo_mas_intereses_s = "";
                            String plazoz = "";
                            String numero_de_credito = "";
                            String cuotas_morosas = "";
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
    }

    private void actualizarCaja (int monto_ingresado) throws IOException {
        long monto_nuevo = 0;
        String contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            monto_nuevo = Integer.parseInt(split[1]) + monto_ingresado;
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
        if (new GuardarArchivo("cajax_caja_.txt", contenido, getApplicationContext()).guardarFile()) {
            Log.v("actualizarCaja_3", "Nuevo_credito.\n\nContenido del archivo:\n\n" + imprimir_archivo("cajax_caja_.txt") + "\n\n.");
        } else {
            Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
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
                        Log.v("llenar_spinner0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                        //Do nothing.
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
        Intent menu_principal = new Intent(this, MenuPrincipal.class);
        menu_principal.putExtra("mensaje", "");
        startActivity(menu_principal);
        finish();
        System.exit(0);
    }

    private void mostrar_todito () {
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.VISIBLE);
    }

    private void ocultar_todito () {
        Log.v("ocultar_todito", "Se hace todo invisible");
        tv_esperar.setVisibility(View.VISIBLE);
        tv_esperar.setText("conectando, por favor espere...");
        bt_consultar.setVisibility(View.INVISIBLE);
        et_ID.setVisibility(View.INVISIBLE);
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

}