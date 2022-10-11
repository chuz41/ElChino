package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MenuPrincipal extends AppCompatActivity {

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
    private String sheet_cobradores = "cobradores";
    private Button bt_nuevo_cliente;
    private Button bt_estado_cliente;
    private Button bt_abonar;
    private Button bt_refinanciar;
    private Button bt_nuevo_credito;
    private TextView tv_saludo;
    private TextView tv_fecha;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_principal);
        bt_nuevo_cliente = (Button) findViewById(R.id.bt_nuevo_cliente);
        bt_estado_cliente = (Button) findViewById(R.id.bt_estado_cliente);
        bt_abonar = (Button) findViewById(R.id.bt_abonar);
        bt_refinanciar = (Button) findViewById(R.id.bt_refinanciar);
        bt_nuevo_credito = (Button) findViewById(R.id.bt_nuevo_credito);
        tv_saludo = (TextView) findViewById(R.id.tv_saludoMenu);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        separar_fechaYhora();
        tv_fecha.setText(fecha + "/" + mes + "/" + anio);
        tv_saludo.setText("El Chino");
    }

    public void abonar(View view){
        Intent abonar = new Intent(this, AbonarActivity.class);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(abonar);
        finish();
        System.exit(0);
    }

    public void estado_cliente(View view){
        Intent estado_cliente = new Intent(this, Estado_clienteActivity.class);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(estado_cliente);
        finish();
        System.exit(0);
    }

    public void registrar_cliente_nuevo(View view){
        Intent registrar_cliente_nuevo = new Intent(this, Registrar_cliente_nuevoActivity.class);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(registrar_cliente_nuevo);
        finish();
        System.exit(0);
    }

    public void refinanciar(View view){
        Intent refinanciar = new Intent(this, Re_financiarActivity.class);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(refinanciar);
        finish();
        System.exit(0);
    }

    public void nuevo_credito(View view){
        Intent nuevo_credito = new Intent(this, Nuevo_creditoActivity.class);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(nuevo_credito);
        finish();
        System.exit(0);
    }

    //Funciones comunes//

    private void esperar () {
        ocultar_todito();
        Toast.makeText(this, "Cobrador inactivo. La app se cierra ahora...", Toast.LENGTH_LONG).show();
        //tv_esperar.setText("Cobrador inactivo. La app se cierra ahora...");
        for (int i = 0; i > 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        mostrar_todito();
        //salir();
    }

    public  void borrar_archivo(String file) throws IOException {
        File archivo = new File(file);
        String empty_string = "";
        guardar(empty_string, file);
        archivo.delete();
    }

    public  void guardar (String contenido, String file_name) throws IOException {
        try {
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
        //Intent main = new Intent(this, MainActivity.class);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        //startActivity(main);
        finish();
        System.exit(0);
    }

    private void mostrar_todito() {
        //tv_esperar.setText("");
        //tv_esperar.setVisibility(View.INVISIBLE);
        //et_ID.setVisibility(View.INVISIBLE);
    }

    private void ocultar_todito() {
        Log.v("ocultar_todito", "Se hace todo invisible");
        //tv_esperar.setVisibility(View.VISIBLE);
        //tv_esperar.setText("Ingrese su codigo de cobrador...");
        //et_ID.setVisibility(View.VISIBLE);
    }

    private void ocultar_todo() {
        ocultar_todito();
        //TODO: todo
    }

    private void mostrar_todo() {
        mostrar_todito();
        //TODO: todo
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

}