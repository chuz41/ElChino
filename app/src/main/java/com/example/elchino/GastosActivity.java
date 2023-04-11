package com.example.elchino;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Calendar;
import java.util.Date;

public class GastosActivity extends AppCompatActivity {

    private EditText et_ID;
    private Integer monto_abono;
    private TextView tv_esperar;
    private String mes;
    private String anio;
    private String fecha;
    private String hora;
    private String dia;
    private String minuto;
    private Button bt_entregar;
    private Button bt_recibir;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private String monto_disponible = "";
    private boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private String caja = "caja.txt";
    private TextView tv_caja;
    private String credit_ID = "ruta";
    private Date hoy_LD = Calendar.getInstance().getTime();
    private EditText et_notasGastos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gastos);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        if (!mensaje_recibido.equals("")) {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        et_notasGastos = (EditText) findViewById(R.id.et_notasGastos);
        et_notasGastos.setText("");
        //et_notasGastos.setVisibility(View.INVISIBLE);
        TextView tv_abonar = (TextView) findViewById(R.id.tv_abonar);
        String string = "Registrar gasto de ruta";
        tv_abonar.setText(string);
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        et_ID = (EditText) findViewById(R.id.et_ID);
        bt_entregar = (Button) findViewById(R.id.bt_entregar);
        string = "REGISTRAR";
        TextView tv_recibir = (TextView) findViewById(R.id.tv_recibir);
        tv_recibir.setVisibility(View.INVISIBLE);
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        bt_recibir = (Button) findViewById(R.id.bt_recibir);
        bt_recibir.setClickable(false);
        bt_recibir.setEnabled(false);
        bt_recibir.setVisibility(View.INVISIBLE);
        bt_entregar.setText(string);
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("GASTOS DE RUTA");
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");
        mostrar_caja();
        separarFecha();
        if (!cliente_recibido.equals("")) {
            flag_client_reciv = true;
            cliente_ID = cliente_recibido;
        }
        text_listener();
    }

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(hoy_LD);
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

    private void actualizar_disponible (String operador) throws IOException {
        monto_abono = Integer.parseInt(et_ID.getText().toString());
        int monto_caja_mostrar = 0;
        long monto_nuevo = 0;
        String contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            String[] split = linea.split(" ");
            if (operador.equals("sumar")) {
                monto_nuevo = Integer.parseInt(split[1]) + monto_abono;
                monto_caja_mostrar = monto_abono;
            } else if (operador.equals("restar")) {
                monto_nuevo = Integer.parseInt(split[1]) - monto_abono;
                monto_caja_mostrar = monto_abono * -1;
            }
            linea = linea.replace(split[1], String.valueOf(monto_nuevo));
            contenido = linea;
            br.close();
            archivo.close();
            new BorrarArchivo(caja, getApplicationContext());
            new GuardarArchivo(caja, contenido, getApplicationContext()).guardarFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
        actualizar_cierre(monto_caja_mostrar, obtener_caja());
        contenido = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("cajax_caja_.txt"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null && !linea.equals("")) {
                String[] split = linea.split("_separador_");
                if (split[0].equals("caja")) {
                    linea = linea.replace(split[1], String.valueOf(monto_nuevo));
                } else if (split[0].equals("estado_archivo")) {
                    if (!split[1].equals("abajo")) {
                        linea = linea.replace("arriba", "abajo");
                    }
                }
                contenido = contenido + linea + "\n";
                Log.v("actualizar_disponible_1", "Banca.\n\nLinea:\n\n" + linea + "\n\n.");
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        new GuardarArchivo("cajax_caja_.txt", contenido, getApplicationContext()).guardarFile();
        String string = "Gasto de ruta: " + String.valueOf(monto_abono);
        salir(string);
    }

    private void salir (String s) {
        Intent activity_volver = new Intent(this, MenuPrincipal.class);
        activity_volver.putExtra("mensaje", s);
        startActivity(activity_volver);
        finish();
        System.exit(0);
    }

    private void actualizar_cierre (Integer monto_abono, Integer saldo_caja) {
        String notas = et_notasGastos.getText().toString();
        notas = notas.replace("\n", "");
        notas = notas.replace(" ", "_");
        Log.v("actualizar_cierre_0", "Gastos.\n\nNotas: " + notas + "\n\n.");
        String linea_cierre = "banca " + String.valueOf(monto_abono) + " " + saldo_caja + " " + notas;
        new AgregarLinea(linea_cierre, "cierre.txt", getApplicationContext());
        String lineaCierre = "banca_separador_" + String.valueOf(monto_abono) + "_separador_" + saldo_caja + "_separador_" + notas;
        new AgregarLinea(lineaCierre, "cierre_cierre_.txt", getApplicationContext(), "cierre");
    }

    private Integer obtener_caja () {
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

    public void abonar (View view) throws IOException {
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
        String notas = et_notasGastos.getText().toString();
        if (notas.isEmpty()) {
            bt_entregar.setEnabled(true);
            bt_entregar.setClickable(true);
            et_notasGastos.setError("Digite las notas del gasto...");
            et_notasGastos.requestFocus();
        } else if (notas.length() > 30) {
            bt_entregar.setEnabled(true);
            bt_entregar.setClickable(true);
            et_notasGastos.setError("Maximo 30 caracteres...");
            et_notasGastos.setText("");
            et_notasGastos.requestFocus();
        } else {
            actualizar_disponible("restar");
        }
    }

    public void recibir (View view) {
        bt_entregar.setClickable(false);
        bt_entregar.setEnabled(false);
    }

    private void text_listener () {
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
                    if (!String.valueOf(s).equals("")) {
                        String monto_en_caja = "0";
                        try {
                            InputStreamReader archivo = new InputStreamReader(openFileInput(caja));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            String[] split = linea.split(" ");
                            long monto = Integer.parseInt(split[1]);
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
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
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
                e.printStackTrace();
            }
        }
        return contenido;
    }

}