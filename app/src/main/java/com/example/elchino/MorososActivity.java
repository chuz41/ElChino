package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.example.elchino.Util.BluetoothUtil;
import com.example.elchino.Util.DateUtilities;
import com.example.elchino.Util.SepararFechaYhora;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MorososActivity extends AppCompatActivity {

    private String mes;
    private String anio;
    private String fecha;
    private TextView tv_saludo;
    private final String caja = "caja.txt";
    private TextView tv_caja;
    private Date hoy_LD;
    private TextView tv_fecha;
    private EditText tv_multiline;
    private String fecha_hoy_string;
    private Button bt_imprimir;
    private String mensaje_imprimir = "";
    private HashMap<String, String> mapMorosos = new HashMap<>();
    private Spinner spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_morosos);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("*** MOROSOS ***");
        bt_imprimir = (Button) findViewById(R.id.bt_imprimir);
        bt_imprimir.setVisibility(View.INVISIBLE);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_multiline = (EditText) findViewById(R.id.tv_multiline);
        spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setVisibility(View.INVISIBLE);
        hoy_LD = Calendar.getInstance().getTime();
        fecha_hoy_string = DateUtilities.dateToString(hoy_LD);
        separarFecha();
        mostrar_caja();
        try {
            hoy_LD = DateUtilities.stringToDate(fecha_hoy_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] split_fecha_hoy_string = fecha_hoy_string.split("-");
        fecha_hoy_string = split_fecha_hoy_string[2] + "/" + split_fecha_hoy_string[1] + "/" + split_fecha_hoy_string[0];
        tv_fecha.setText(fecha_hoy_string);
        mostrarMorosos();
    }

    //A method to read all the files that contains in its names the characteres "_P_" that means "prestamo", open and read the content of each file, read each row of the each file, find a row that contains "proximo_abono_separador_dd/mm/aaaa", split the row using the characteres "_separador_", check if the split[1] (the date) are in late, if the file are in late: Find all the files that contains in its names the characteres "_C_" that means "cliente", split both file names, the file that contains the characteres "_P_" and the file that contains the characteres "_C_", split each name using the characteres "_P_" and "_C_" respectively and compare if the first part of their names are the same when in that case, put the file name that contains the characteres "_C_" in an array of strings that contains all the files that are relationed whith the files that contains the characteres "_P_" are in late.
    private void mostrarMorosos () {
        String[] archivos = fileList();
        String contenido = "";
        String[] archivos_prestamo = new String[archivos.length];
        String[] archivos_cliente = new String[archivos.length];
        int contador_prestamo = 0;
        int contador_cliente = 0;
        for (int i = 0; i < archivos.length; i++) {
            if (archivos[i].contains("_P_")) {
                archivos_prestamo[contador_prestamo] = archivos[i];
                contador_prestamo++;
            }
            if (archivos[i].contains("_C_")) {
                archivos_cliente[contador_cliente] = archivos[i];
                contador_cliente++;
            }
        }
        for (int i = 0; i < archivos_prestamo.length; i++) {
            boolean flag = false;
            if (archivos_prestamo[i] != null) {
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(archivos_prestamo[i]));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    Date fecha_proximo_abono = new Date();
                    int saldo_mas_interes_int = 0;
                    while (linea != null) {
                        if (linea.contains("proximo_abono_separador_")) {
                            //Log.v("mostrarMorosos_0", "Morosos.\n\nFecha proximo abono: " + linea + "\n\n.");
                            String[] split_linea = linea.split("_separador_");
                            if (!split_linea[1].equals("Prestamo cancelado")) {
                                String[] split_fecha = split_linea[1].split("/");
                                String fechaaa = split_fecha[2] + "-" + split_fecha[1] + "-" + split_fecha[0];
                                fecha_proximo_abono = DateUtilities.stringToDate(fechaaa);
                                //hoy_LD.compareTo(fecha_proximo_abono)  String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_nx_abo_LD));
                                //Log.v("mostrarMorosos_0", "Morosos.\n\nArchivo prestamo: " + archivos_prestamo[i] + "\n\nFecha hoy: " + DateUtilities.dateToString(hoy_LD) + "\n\nFecha archivo: " + DateUtilities.dateToString(fecha_proximo_abono) + "\n\nDiferencia entre fechas: " + DateUtilities.daysBetween(hoy_LD, fecha_proximo_abono) + "\n\n.");
                            } else {
                                flag = true;
                                break;
                            }

                        } else if (linea.contains("saldo_mas_intereses_separador_")) {
                            String[] split_linea = linea.split("_separador_");
                            String saldo_mas_interes = split_linea[1];
                            saldo_mas_interes_int = Integer.parseInt(saldo_mas_interes);
                            Log.v("mostrarMorosos_1", "Morosos.\n\nArchivo prestamo: " + archivos_prestamo[i] + "\n\nSaldo mas interes: " + saldo_mas_interes + "\n\n.");
                        }
                        linea = br.readLine();
                    }
                    br.close();
                    archivo.close();
                    if (!flag) {
                        if (DateUtilities.daysBetween(hoy_LD, fecha_proximo_abono) > 0 && saldo_mas_interes_int >= 1000) {
                            String[] split_archivo_prestamo = archivos_prestamo[i].split("_P_");
                            for (int j = 0; j < archivos_cliente.length; j++) {
                                if (archivos_cliente[j] != null) {
                                    String[] split_archivo_cliente = archivos_cliente[j].split("_C_");
                                    if (split_archivo_prestamo[0].equals(split_archivo_cliente[0])) {
                                        contenido += archivos_cliente[j] + "\n";
                                    }
                                }
                            }
                        }
                    }
                } catch (IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        acondicionarMensaje(contenido);
    }

    private void acondicionarMensaje (String contenido) {

        //Split the string "contenido" using the characteres "\n", each row is the name of a file that contains the information of a client. Open each file, read the content and put in a string the information following the next convention: nombre_cliente + " " + apellido_cliente + " (" + apodo_cliente + ") ". Use the string with the information of the client as a key for the HashMap "mapa_clientes" and put as the value of the key in the HashMap the name of the file that contains the information of the client.
        String[] split_contenido = contenido.split("\n");
        for (int i = 0; i < split_contenido.length; i++) {
            String nombre_archivo = split_contenido[i];
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(nombre_archivo));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String nombre_cliente = "";
                String apellido_cliente = "";
                String apodo_cliente = "";
                while (linea != null) {
                    if (linea.contains("nombre_cliente_separador_")) {
                        String[] split_linea = linea.split("_separador_");
                        nombre_cliente = split_linea[1];
                    } else if (linea.contains("apellido1_cliente_separador_")) {
                        String[] split_linea = linea.split("_separador_");
                        apellido_cliente = split_linea[1];
                    } else if (linea.contains("apodo_cliente_separador_")) {
                        String[] split_linea = linea.split("_separador_");
                        apodo_cliente = split_linea[1];
                    }
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
                String llave = nombre_cliente + " " + apellido_cliente + " (" + apodo_cliente + ") ";
                mapMorosos.put(llave, nombre_archivo);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        //Iterate the HashMap "mapMorosos", put the key of the HashMap in a list, then sort the list and put the elements of the list in the string "contenido".
        contenido = "";
        List<String> keys = new ArrayList<>(mapMorosos.keySet());
        Collections.sort(keys);
        for (String key: keys) {
            contenido += key + "\n";
        }

        //Put the string "contenido" in the TextView "tv_multiline".
        tv_multiline.setText(contenido);
        mensaje_imprimir = contenido;
        bt_imprimir.setVisibility(View.VISIBLE);

        //onClickListener();

    }

    public void onClickListener(View view) {
        tv_multiline.setClickable(false);
        tv_multiline.setEnabled(false);
        tv_multiline.setVisibility(View.INVISIBLE);
        spinner.setVisibility(View.VISIBLE);
        llenarSpinner();
    }

    private void llenarSpinner() {
        //fill the spinner with the keys of the HashMap "mapMorosos". The first element of the spinner is "Seleccione un cliente moroso".
        List<String> keys = new ArrayList<>(mapMorosos.keySet());
        Collections.sort(keys);
        keys.add(0, "Seleccione un cliente moroso...");
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, keys);
        spinner.setAdapter(adapter);
        spinnerListener();
    }

    //Method that implements a spinner listener. When the user selects an element of the spinner, the method "estadoCliente" is called with the name of the file that contains the information of the client selected.
    private void spinnerListener () {
        spinner.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = spinner.getSelectedItem().toString();
                        if (seleccion.equals("Seleccione un cliente moroso...")) {
                            spinnerListener();
                        }else {
                            consultarCliente(seleccion);
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void consultarCliente(String seleccion) {
        String cliente_recibido = mapMorosos.get(seleccion);
        String[] split_cliente_recibido = cliente_recibido.split("_C_");
        cliente_recibido = split_cliente_recibido[0];
        Intent activity_volver = new Intent(this, Estado_clienteActivity.class);
        activity_volver.putExtra("mensaje", "Consulta de estado del cliente " + seleccion);
        Log.v("consultarCliente_0", "Morosos.\n\ncliente consultar: " + cliente_recibido + "\n\n.");
        activity_volver.putExtra("cliente_ID", cliente_recibido);
        startActivity(activity_volver);
        finish();
        System.exit(0);
    }

    public void printIt (View view) {
        BluetoothSocket socket;
        socket = null;
        mensaje_imprimir = "Lista de morosos:\n\n" + mensaje_imprimir + "\n\n\n\n";
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

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(null);
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        fecha = datosFecha.getDia();
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

}