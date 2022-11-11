package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estado_clienteActivity extends AppCompatActivity {//Esta activity va a funcionar unicamente offline.

    private EditText et_ID;
    private TextView tv_esperar;
    private TextView saludo_estado;
    private Button bt_consultar;
    private Button bt_prestar;
    private Button bt_represtar;
    private Button bt_abonar;
    private String cliente_ID = "";
    private Button bt_estado_cuenta;
    private boolean flag_consultar = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_cliente);
        saludo_estado = (TextView) findViewById(R.id.tv_saludoEstado);
        saludo_estado.setText(" Estado del cliente");
        et_ID = (EditText) findViewById(R.id.et_ID);
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        bt_consultar = (Button) findViewById(R.id.bt_consultar);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        bt_prestar = (Button) findViewById(R.id.bt_prestar);
        bt_represtar = (Button) findViewById(R.id.bt_represtar);
        bt_prestar.setVisibility(View.INVISIBLE);
        bt_represtar.setVisibility(View.INVISIBLE);
        bt_abonar = (Button) findViewById(R.id.bt_abonar);
        bt_abonar.setVisibility(View.INVISIBLE);
        bt_estado_cuenta = (Button) findViewById(R.id.bt_estado_cuenta);
        bt_estado_cuenta.setVisibility(View.INVISIBLE);
        text_listener();
    }

    private void text_listener() {
        et_ID.setText("");
        et_ID.setEnabled(true);
        et_ID.setFocusableInTouchMode(true);
        et_ID.requestFocus();
        //Implementacion de un text listener
        et_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                bt_consultar.setClickable(false);
                bt_consultar.setEnabled(false);
                String archivos[] = fileList();
                boolean crear_lot = true;
                for (int i = 0; i < archivos.length; i++) {
                    Pattern pattern = Pattern.compile(et_ID.getText().toString(), Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(archivos[i]);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        if (s.length() >= 9) {
                            bt_consultar.setEnabled(true);
                            bt_consultar.setClickable(true);
                            //String texto = et_ID.getText().toString();
                            //et_ID.setBackgroundResource(R.drawable.);
                        }
                    }
                }
                //Poner letras verdes o algo asi cuando se encuentre un cliente!!!
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    public void consultar (View view) {
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        String archivos[] = fileList();
        String puntuacion_cliente = "";
        String archivoCompleto = "";
        String consultador = et_ID.getText().toString() + "_C_";
        for (int i = 0; i < archivos.length; i++) {
            Pattern pattern = Pattern.compile(consultador, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(archivos[i]);
            boolean matchFound = matcher.find();
            if (matchFound) {
                //TODO: Abrir archivo y leerlo.
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();
                    while (linea != null) {
                        String[] split = linea.split("_separador_");
                        if (split[0].equals("puntuacion_cliente")) {
                            puntuacion_cliente = split[1];
                        } if (split[0].equals("ID_cliente")) {
                            cliente_ID = split[1];
                        } else {
                            //Do nothing.
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
            //No se encontro el cliente
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
            text_listener();
        } else {
            Toast.makeText(this, "Cliente encontrado", Toast.LENGTH_SHORT).show();
            presentar_cliente(archivoCompleto, puntuacion_cliente);
        }
    }

    private void presentar_cliente(String archivoCompleto, String puntuacionS) {
        int puntuacion = Integer.parseInt(puntuacionS);
        et_ID.setText("");
        et_ID.setFocusableInTouchMode(false);
        et_ID.setEnabled(false);
        et_ID.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.INVISIBLE);
        tv_esperar.setText(archivoCompleto);
        bt_prestar.setVisibility(View.VISIBLE);
        bt_represtar.setVisibility(View.VISIBLE);
        bt_abonar.setVisibility(View.VISIBLE);
        bt_estado_cuenta.setVisibility(View.VISIBLE);
        if (puntuacion < 5) {
            bt_prestar.setClickable(false);
            bt_prestar.setEnabled(false);
            bt_represtar.setClickable(false);
            bt_represtar.setEnabled(false);
        }
    }

    public void refinanciar(View view){
        Intent refinanciar = new Intent(this, Re_financiarActivity.class);
        refinanciar.putExtra("msg", "");
        refinanciar.putExtra("cliente_recivido", cliente_ID);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(refinanciar);
        finish();
        System.exit(0);
    }

    public void nuevo_credito(View view){
        Intent nuevo_credito = new Intent(this, Nuevo_creditoActivity.class);
        nuevo_credito.putExtra("msg", "");
        nuevo_credito.putExtra("cliente_recivido", cliente_ID);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(nuevo_credito);
        finish();
        System.exit(0);
    }

    public void estado_cuenta(View view) {
        Intent cuadra_tura = new Intent(this, CuadraturaActivity.class);
        cuadra_tura.putExtra("msg", "");
        cuadra_tura.putExtra("cuadratura", "");
        cuadra_tura.putExtra("cliente_recivido", cliente_ID);
        cuadra_tura.putExtra("cambio", "0");
        cuadra_tura.putExtra("monto_creditito", "0");
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        startActivity(cuadra_tura);
        finish();
        System.exit(0);
    }

    public void abonar(View view) {
        Intent abonar = new Intent(this, AbonarActivity.class);
        abonar.putExtra("msg", "");
        abonar.putExtra("cliente_recivido", cliente_ID);
        abonar.putExtra("abono_cero", "");
        startActivity(abonar);
        finish();
        System.exit(0);
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