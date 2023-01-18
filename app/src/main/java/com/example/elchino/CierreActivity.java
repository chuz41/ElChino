package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.service.voice.VoiceInteractionService;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elchino.Util.BluetoothUtil;
import com.example.elchino.Util.DateUtilities;
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

public class CierreActivity extends AppCompatActivity {

    private Map<String, Integer> meses = new HashMap<String, Integer>();
    private Button bt_imprimir;
    private String dia;
    private String mensaje_imprimir = "";
    private String mes;
    private String anio;
    private String fecha;
    private String hora;
    private String minuto;
    private String nombre_dia;
    private TextView tv_saludo;
    private String caja = "caja.txt";
    private String cobrador = "a_sfile_cobrador_sfile_a.txt";
    private TextView tv_caja;
    private Date hoy_LD;
    private String fecha_hoy_string;
    private TextView tv_fecha;
    private EditText tv_multiline;
    private String cobrador_ID = "";
    private String apodo_cobrador = "";
    private Map<Integer, String> abonos = new HashMap<Integer, String>();
    private Map<Integer, String> creditos = new HashMap<Integer, String>();
    private Map<Integer, String> bancas = new HashMap<Integer, String>();
    private Integer balance_general_abonos = 0;
    private Integer balance_general_creditos = 0;
    private Integer balance_general_banca_entrega = 0;
    private Integer balance_general_banca_recibe = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cierre);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("*** CIERRE ***");
        bt_imprimir = (Button) findViewById(R.id.bt_imprimir);
        bt_imprimir.setVisibility(View.INVISIBLE);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_multiline = (EditText) findViewById(R.id.tv_multiline);
        datos_vendedor();
        mostrar_caja();
        hoy_LD = Calendar.getInstance().getTime();
        Log.v("OnCreate0", "Abonar.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        fecha_hoy_string = DateUtilities.dateToString(hoy_LD);
        Log.v("OnCreate1", "Abonar.\n\nFecha hoy: " + fecha_hoy_string + "\n\n.");
        Log.v("OnCreate2", "Abonar.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        separar_fechaYhora();
        try {
            hoy_LD = DateUtilities.stringToDate(fecha_hoy_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] split_fecha_hoy_string = fecha_hoy_string.split("-");
        fecha_hoy_string = split_fecha_hoy_string[2] + "/" + split_fecha_hoy_string[1] + "/" + split_fecha_hoy_string[0];
        tv_fecha.setText(fecha_hoy_string);
        generar_cierre();
    }

    private void datos_vendedor() {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] splitr = linea.split(" ");
            cobrador_ID = splitr[0];
            while (linea != null) {
                String[] split = linea.split(" ");
                if (split[0].equals("apodo")) {
                    apodo_cobrador = split[1];
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getNombreCliente (String ID_buscado) {
        String nombreCliente = "";
        String archivos[] = fileList();
        for (int i = 0; i < archivos.length; i++) {
            Pattern pattern = Pattern.compile(ID_buscado + "_C_.txt", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    while (linea != null) {
                        Log.v("getNombreCliente0", "Cierre.\n\nlinea:\n\n" + linea + "\n\n.");
                        String[] split = linea.split("_separador_");
                        if (split[0].equals("nombre_cliente")) {
                            if (nombreCliente.equals("")) {
                                nombreCliente = split[1];
                            } else {
                                nombreCliente = nombreCliente + " " + split[1];
                            }
                        }
                        if (split[0].equals("apellido1_cliente")) {
                            if (nombreCliente.equals("")) {
                                nombreCliente = split[1];
                            } else {
                                nombreCliente = nombreCliente + " " + split[1];
                            }
                        }
                        linea = br.readLine();
                    }
                    Log.v("getNombreCliente1", "Cierre.\n\nnombreCliente: " + nombreCliente + "\n\n.");
                    br.close();
                    archivo.close();
                } catch (IOException e) {
                }
                break;
            } else {
                //Continue with the execution.
            }
        }
        return nombreCliente;
    }

    private void generar_cierre() {
        boolean flag_null = false;
        bt_imprimir.setVisibility(View.VISIBLE);
        String contenido_cierre = "*#*#*#*#*#* CIERRE *#*#*#*#*#*\n\nCobrador: " + apodo_cobrador
                + "\nCobrador ID: " + cobrador_ID + "\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("cierre.txt"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            int fecha_file = Integer.parseInt(split[1]);
            int hoy_fecha = Integer.parseInt(fecha);
            if (fecha_file != hoy_fecha) {
                msg("Sin movimientos el dia de hoy!!!");
                contenido_cierre = contenido_cierre +
                        "Sin movimientos el dia de hoy!!!\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n\n\n";
                br.close();
                archivo.close();
                boton_atras("Sin movimientos el dia de hoy!!!");
            } else {
                linea = br.readLine();
                Log.v("generar_cierre-1", "Cierre.\n\nLinea:\n\n" + linea + "\n\n.");
                int cont_abonar = 0;
                int cont_creditos = 0;
                int cont_bancas = 0;
                while (linea != null) {
                    split = linea.split(" ");
                    int lalrgo = split.length;
                    Log.v("generar_cierre0", "Cierre.\n\ncont: " + cont_abonar + "\n\nlargo_split: " + lalrgo + "\n\n.");
                    String persona = new String();
                    if (lalrgo == 3) {
                        persona = "cobrador";
                    } else if (lalrgo == 4) {
                        persona = split[3];
                        String splitPersona[] = persona.split("_P_");
                        String nombreCliente = getNombreCliente(splitPersona[0]);
                        persona = nombreCliente;
                    }
                    String tipo = split[0];
                    String monto = split[1];
                    String caja = split[2];
                    String frase = tipo + " " + monto + " " + caja + " " + persona;
                    if (tipo.equals("abono")) {
                        Log.v("generar_cierre1", "Cierre.\n\ncont: " + cont_abonar + "\n\nvalue: " + frase + "\n\n.");
                        abonos.put(cont_abonar, frase);
                        cont_abonar++;
                    } else if (tipo.equals("credito")) {
                        Log.v("generar_cierre2", "Cierre.\n\ncont: " + cont_creditos + "\n\nvalue: " + frase + "\n\n.");
                        creditos.put(cont_creditos, frase);
                        cont_creditos++;
                    } else if (tipo.equals("banca")) {
                        Log.v("generar_cierre3", "Cierre.\n\ncont: " + cont_bancas + "\n\nvalue: " + frase + "\n\n.");
                        bancas.put(cont_bancas, frase);
                        cont_bancas++;
                    } else {
                        //Do nothing.
                    }
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            }
        } catch (IOException e) {
        }

        if (abonos.isEmpty() && creditos.isEmpty() && bancas.isEmpty()) {
            msg("Sin movimientos el dia de hoy!!!");
            contenido_cierre = contenido_cierre +
                    "Sin movimientos el dia de hoy!!!\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n\n\n";
            boton_atras("Sin movimientos el dia de hoy!!!");
        } else {
            if (abonos.isEmpty()) {
                //Do nothing. Continue...
            } else {
                contenido_cierre = contenido_cierre + "#*#*#* ABONOS RECIBIDOS *#*#*#\n\n";
                for (Integer key : abonos.keySet()) {
                    String value = abonos.get(key);
                    String[] split_value = value.split(" ");
                    int monto_tempo = Integer.parseInt(split_value[1]);
                    balance_general_abonos = balance_general_abonos + monto_tempo;
                    Log.v("generar_cierre4", "Cierre.\n\nsplit_value.lenght: " + split_value.length + "\nsplit_value[" + 3 + "]: " + split_value[3] + "\nsplit_value[" + 4 + "]: " + split_value[4] + "\n\n.");
                    String nameCliente = "";
                    if (split_value.length == 4) {
                        nameCliente = split_value[3];
                    } else if (split_value.length >= 5) {
                        nameCliente = split_value[3] + " " + split_value[4];
                    }
                    Log.v("generar_cierre5", "Cierre.\n\nnameCliente: " + nameCliente + "\n\n.");
                    contenido_cierre = contenido_cierre + "Abono de\n" + nameCliente + ":\nMonto: " +
                            split_value[1] + " colones.\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
                }
            }
            if (creditos.isEmpty()) {
                //Do nothing. Continue...
            } else {
                contenido_cierre = contenido_cierre + "*#*#* CREDITOS APROBADOS *#*#*\n\n";
                for (Integer key : creditos.keySet()) {
                    String value = creditos.get(key);
                    String[] split_value = value.split(" ");
                    int monto_tempo = Integer.parseInt(split_value[1]);
                    balance_general_creditos = balance_general_creditos + monto_tempo;
                    Log.v("generar_cierre4", "Cierre.\n\nsplit_value.lenght: " + split_value.length + "\nsplit_value[" + 3 + "]: " + split_value[3] + "\nsplit_value[" + 4 + "]: " + split_value[4] + "\n\n.");
                    String nameCliente = "";
                    if (split_value.length == 4) {
                        nameCliente = split_value[3];
                    } else if (split_value.length >= 5) {
                        nameCliente = split_value[3] + " " + split_value[4];
                    }
                    contenido_cierre = contenido_cierre + "Credito aprobado a:\n" + nameCliente + ":\nMonto: " +
                            split_value[1] + " colones.\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
                }
            }
            if (bancas.isEmpty()) {
                //Do nothing. Continue...
            } else {
                contenido_cierre = contenido_cierre + "*#*#* MOVIMIENTOS BANCA *#*#*\n\n";
                for (Integer key : bancas.keySet()) {
                    String value = bancas.get(key);
                    String[] split_value = value.split(" ");
                    int valor_monto = Integer.parseInt(split_value[1]);
                    String pre_mensaje = "";
                    if (valor_monto < 0) {
                        pre_mensaje = "Se entrega a banca:";
                        valor_monto = valor_monto * -1;
                        balance_general_banca_recibe = balance_general_banca_recibe + valor_monto;
                    } else {
                        pre_mensaje = "Se recibe de banca:";
                        balance_general_banca_entrega = balance_general_banca_entrega + valor_monto;
                    }
                    contenido_cierre = contenido_cierre + pre_mensaje + ":\nMonto: " +
                            String.valueOf(valor_monto) + " colones.\n\n*#*#*#*#*#*#*#*#*#*#*#*#*#*#*#\n\n";
                }
            }
            contenido_cierre = contenido_cierre + "\nFirma cobrador:\n\n__________________\nNombre: " + apodo_cobrador + ".\n\n" +
                   "T. cobrado: " + balance_general_abonos + " colones.\n\nT. prestado: " + balance_general_creditos +
                    " colones.\n\nAbonos banca: " + balance_general_banca_recibe + " colones.\n\nB. entrega: " +
                    balance_general_banca_entrega + " colones.\n\nBalance: " + String.valueOf(balance_general_abonos -
                    balance_general_creditos + balance_general_banca_entrega - balance_general_banca_recibe) +
                    " colones.\n\n#*#*#*#* ULTIMA LINEA *#*#*#*#\n\n\n\n";
            tv_multiline.setText(contenido_cierre);
            mensaje_imprimir = contenido_cierre;
            bt_imprimir.setVisibility(View.VISIBLE);
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
        // Get sunmi InnerPrinter BluetoothDevice\
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

    private String get_impresora () {
        String impresora = "00:11:22:33:44:55";
        return impresora;
    }

    @Override
    public void onBackPressed (){
        boton_atras("");
    }

    private void boton_atras (String s) {
        Intent activity_volver = new Intent(this, MenuPrincipal.class);
        activity_volver.putExtra("mensaje", s);
        startActivity(activity_volver);
        finish();
        System.exit(0);
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

    private boolean archivo_existe (String[] archivos, String file_name){
        for (int i = 0; i < archivos.length; i++) {
            if (file_name.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

    private void mostrar_caja () {
        tv_caja.setText(imprimir_archivo(caja));
    }

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}
