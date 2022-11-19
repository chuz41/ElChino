package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.HashMap;
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
    private TextView tv_caja;
    private String caja = "caja.txt";
    private Button bt_find_name;
    private int contador_de_opciones = 1;
    private Spinner sp_opciones;
    private String buscar_por = "";
    private String clientes = "clientes_cred.txt";
    private HashMap<String, String> sp_helper = new HashMap<String, String>();
    private String nombre_cliente = "";
    private String apellido1_cliente = "";
    private String apellido2_cliente = "";
    private String apodo_cliente = "";
    private String archivo_cliente = "";
    private Button bt_editar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_cliente);

        saludo_estado = (TextView) findViewById(R.id.tv_saludoEstado);
        cliente_ID = getIntent().getStringExtra("cliente_ID");
        saludo_estado.setText(" Estado del cliente");
        et_ID = (EditText) findViewById(R.id.et_ID);
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        bt_consultar = (Button) findViewById(R.id.bt_consultar);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        bt_editar = (Button) findViewById(R.id.bt_editar);
        bt_editar.setVisibility(View.INVISIBLE);
        bt_prestar = (Button) findViewById(R.id.bt_prestar);
        bt_represtar = (Button) findViewById(R.id.bt_represtar);
        bt_prestar.setVisibility(View.INVISIBLE);
        bt_represtar.setVisibility(View.INVISIBLE);
        bt_abonar = (Button) findViewById(R.id.bt_abonar);
        bt_abonar.setVisibility(View.INVISIBLE);
        bt_estado_cuenta = (Button) findViewById(R.id.bt_estado_cuenta);
        bt_estado_cuenta.setVisibility(View.INVISIBLE);
        bt_find_name = (Button) findViewById(R.id.bt_find_name);
        bt_find_name.setText("Cambiar");
        sp_opciones = (Spinner) findViewById(R.id.sp_opciones);
        sp_opciones.setEnabled(false);
        sp_opciones.setVisibility(View.INVISIBLE);
        //bt_find_name.setVisibility(View.INVISIBLE);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");

        mostrar_caja();
        if (cliente_ID.equals("")) {
            text_listener();
        } else {
            consultar(null);
        }
    }

    public void editar_archivo (View view) {
        Intent editar_ac = new Intent(this, EditarActivity.class);
        editar_ac.putExtra("archivo_cliente", archivo_cliente);
        editar_ac.putExtra("cliente_id", cliente_ID);
        startActivity(editar_ac);
        finish();
        System.exit(0);
    }

    private void mostrar_caja () {
        tv_caja.setText(imprimir_archivo(caja));
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

    private void spinner_listener () {
        sp_opciones.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                        String seleccion = sp_opciones.getSelectedItem().toString();


                        if (tv_esperar.getText().toString().equals("Ingrese el nombre del cliente") | tv_esperar.getText().toString().equals("Ingrese el apodo del cliente") | tv_esperar.getText().toString().equals("Ingrese el apellido del cliente")) {
                            for (String key : sp_helper.keySet()) {
                                Log.v("Sp_listener0", "Estado_cliente.\n\nKey:\n\n" + key + "\n\nDato: " + sp_helper.get(key) + "\n\nseleccion: " + seleccion + "\n\n.");
                                if (seleccion.equals(key)) {
                                    archivo_cliente = sp_helper.get(key);
                                    String[] split_ot = archivo_cliente.split("_C_");
                                    cliente_ID = split_ot[0];
                                    Log.v("Sp_listener1", "Estado_cliente.\n\nCliente ID: " + cliente_ID + "\n\nArchivo cliente: " + archivo_cliente + "\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_cliente));
                                    bt_consultar.setEnabled(true);
                                    bt_consultar.setClickable(true);
                                    bt_consultar.setVisibility(View.VISIBLE);
                                }
                            }
                        } else {

                        }

                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    public void buscar_por_nombre (View view) {

        contador_de_opciones = contador_de_opciones + 1;
        if (contador_de_opciones == 5) {
            contador_de_opciones = 1;
        }

        if (contador_de_opciones == 1) {
            et_ID.setHint("Cedula...");
            tv_esperar.setText("Digite la identificacion del cliente");
            buscar_por = "ID_cliente";
            //bt_consultar.setClickable(true);
            //bt_consultar.setEnabled(true);
        } else if (contador_de_opciones == 2) {
            et_ID.setHint("Nombre...");
            tv_esperar.setText("Ingrese el nombre del cliente");
            buscar_por = "nombre_cliente";
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
        } else if (contador_de_opciones == 3) {
            et_ID.setHint("Apellido...");
            tv_esperar.setText("Ingrese el apellido del cliente");
            buscar_por = "apellido1_cliente";
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
        } else if (contador_de_opciones == 4) {
            et_ID.setHint("Apodo...");
            tv_esperar.setText("Ingrese el apodo del cliente");
            buscar_por = "apodo_cliente";
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
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

    private void text_listener () {
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


                if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
                    bt_consultar.setClickable(false);
                    bt_consultar.setEnabled(false);
                    sp_opciones.setVisibility(View.INVISIBLE);
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
                } else if (tv_esperar.getText().toString().equals("Ingrese el nombre del cliente") || tv_esperar.getText().toString().equals("Ingrese el apellido del cliente") || tv_esperar.getText().toString().equals("Ingrese el apodo del cliente")) {


                    String parametro = "";
                    String archivos[] = fileList();
                    for (int i = 0; i < archivos.length; i++) {
                        Pattern pattern = Pattern.compile("_", Pattern.CASE_INSENSITIVE);
                        Matcher matcher = pattern.matcher(archivos[i]);
                        boolean matchFound = true;
                        if (matchFound) {
                            //TODO: Abrir archivo y leerlo.
                            try {
                                InputStreamReader archivo = new InputStreamReader(openFileInput(archivos[i]));
                                BufferedReader br = new BufferedReader(archivo);
                                String linea = br.readLine();
                                boolean param_encontrado = false;
                                while (linea != null) {

                                    String[] split = linea.split("_separador_");
                                    //Log.v("no_cedula", "Estado_cliente.\n\nLinea:\n\n" + linea + "\n\n.");
                                    if (split[0].equals(buscar_por)) {
                                        if (split[1].contains(s)) {
                                            param_encontrado = true;
                                            Log.v("param_encontrado", "Estado_cliente.\n\nParam: " + split[0] + "\n\nContenido del parametro: " + split[1] + "\n\n.");
                                        }
                                    }

                                    if (split[0].equals("nombre_cliente")){
                                        nombre_cliente = split[1];
                                    } else if (split[0].equals("apellido1_cliente")){
                                        apellido1_cliente = split[1];
                                    } else if (split[0].equals("apellido2_cliente")){
                                        apellido2_cliente = split[1];
                                    } else if (split[0].equals("apodo_cliente")){
                                        apodo_cliente = split[1];
                                    } else {
                                        //Do nothing. Continue...
                                    }
                                    linea = br.readLine();
                                }

                                if (param_encontrado) {
                                    Log.v("param_encontrado0", "Estrado_cliente.\n\nNombre cliente: " + nombre_cliente + "\n\napellido1 cliente: " + apellido1_cliente +
                                            "\n\napellido2 cliente: " + apellido2_cliente + "\n\nApodo cliente: " + apodo_cliente + "\n\n.");
                                    parametro = parametro + nombre_cliente + "_sep_" + apellido1_cliente + "_sep_" + apellido2_cliente + "_sep_(" + apodo_cliente + ")" + "_sep_" + archivos[i] + "_sep_"  + "_sop_";
                                    Log.v("param_encontrado1", "Estado_cliente.\n\nparametro:\n\n" + parametro + "\n\n.");
                                    //param_encontrado = false;
                                }

                                br.close();
                                archivo.close();
                            } catch (IOException e) {
                            }
                        } else {
                            //Continue with the execution.
                        }
                    }

                    llenar_spinner(parametro);

                } else {
                    //Do nothing.
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void llenar_spinner (String parametro) {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)

        Log.v("llenando_spinner0", "Estado_cliente.\n\nCantidad de archivos: " + "No se sabe aqui!!!"+ "\n\nParametro:\n\n" + parametro + "\n\n.");
        String[] split = parametro.split("_sop_");
        int largo_split = split.length;
        String spinner_llenar = "";
        sp_helper.clear();

        if (largo_split > 0) {
            for (int i = 0; i < largo_split; i++) {
                String[] splitw = split[i].split("_sep_");
                Log.v("llenando_spinner1", "Estado_cliente.\n\nsplit[" + i + "]: " + split[i] + "\n\nParametro:\n\n" + parametro + "\n\n.");
                int largo_splitw = splitw.length;
                Log.v("llenando_spinner1.5", "Estado_cliente.\n\nLargo_splitw: " + largo_splitw + "\n\nsplitw[0]: " + splitw[0] + "\n\n.");
                //parametro = parametro + nombre_cliente + "_sep_" + apellido1_cliente + "_sep_" + apellido2_cliente + "_sep_(" + apodo_cliente + ")" + "_sep_" + archivos[i] + "_sep_"  + "_sop_";
                if (largo_splitw > 3) {
                    String helperS = split[i].replace(splitw[4], "");
                    helperS = helperS.replace("_sep_", " ");
                    spinner_llenar = spinner_llenar + helperS + "_sip_";
                    Log.v("llenando_spinner2", "Estado_cliente.\n\nhelperS: \n\n" + helperS + "\n\nArchivo credito: " + splitw[4] + "\n\n.");
                    sp_helper.put(helperS, splitw[4]);
                } else {
                    //Do nothing.
                }
            }
        } else {
            //Do nothing.
        }

        String[] split_spinner = spinner_llenar.split("_sip_");

        int largo_otroSpinner = split_spinner.length;

        if (largo_otroSpinner == 0) {
            //Do nothing.
        } else {

            sp_opciones.setEnabled(true);
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.custom_spinner, split_spinner);
            sp_opciones.setVisibility(View.VISIBLE);
            sp_opciones.setAdapter(adapter);
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
            spinner_listener();
        }
    }

    public void consultar (View view) {
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        bt_consultar.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.INVISIBLE);
        sp_opciones.setEnabled(false);
        sp_opciones.setVisibility(View.INVISIBLE);
        //bt_consultar.setVisibility(View.INVISIBLE);
        sp_opciones.setVisibility(View.INVISIBLE);
        bt_find_name.setVisibility(View.INVISIBLE);
        bt_find_name.setEnabled(false);
        tv_esperar.setText("");
        String archivos[] = fileList();
        String puntuacion_cliente = "";
        String archivoCompleto = "";
        String consultador;
        boolean flag = false;
        if (cliente_ID.equals("") && tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
            Log.v("consultar-1", "Estado_cliente.\n\n");
            consultador = et_ID.getText().toString() + "_C_";
            cliente_ID = et_ID.getText().toString();
        } else {
            consultador = cliente_ID + "_C_";
            //bt_consultar.setVisibility(View.INVISIBLE);
        }
        Log.v("consultar0", "Estado_cliente.\n\nCliente ID: " + cliente_ID + "\n\n");
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
                            Log.v("consultar1", "Estado_cliente.\n\nPuntuacion cliente: " + split[1] + "\n\n");
                            puntuacion_cliente = split[1];
                        } else if (split[0].equals("ID_cliente")) {
                            if (cliente_ID.equals(split[1])) {
                                flag = true;
                                //break;
                            }
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
                //break;
            } else {
                //Continue with the execution.
            }
            if (archivoCompleto.equals("")) {
                //No se encontro el cliente
                //Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
                //continue
            } else {
                //Toast.makeText(this, "Cliente encontrado", Toast.LENGTH_SHORT).show();
                //bt_consultar.setVisibility(View.INVISIBLE);
                //sp_opciones.setVisibility(View.INVISIBLE);
                Log.v("consultar2", "Estado_cliente.\n\narchivoCompleto:\n\n" + archivoCompleto + "\n\n.");
                presentar_cliente(archivoCompleto, puntuacion_cliente);
                break;
            }
        }

        if (archivoCompleto.equals("")) {
            //No se encontro el cliente
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
            //continue
            text_listener();
        } else {
            //Toast.makeText(this, "Cliente encontrado", Toast.LENGTH_SHORT).show();
            //bt_consultar.setVisibility(View.INVISIBLE);
            //sp_opciones.setVisibility(View.INVISIBLE);
            Log.v("consultar3", "Estado_cliente.\n\narchivoCompleto:\n\n" + archivoCompleto + "\n\n.");
            //presentar_cliente(archivoCompleto, puntuacion_cliente);
            //break;
        }
    }

    private void presentar_cliente (String archivoCompleto, String puntuacionS) {
        Log.v("presentar_cliente0", "Estado_cliente.\n\narchivoCompleto:\n\n" + archivoCompleto + "\n\nPuntuacion: " + puntuacionS + "\n\n.");
        int puntuacion = Integer.parseInt(puntuacionS);
        sp_opciones.setEnabled(true);
        bt_consultar.setEnabled(true);
        bt_consultar.setClickable(false);
        sp_opciones.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.INVISIBLE);
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
        bt_editar.setVisibility(View.VISIBLE);
        if (puntuacion < 5) {
            bt_prestar.setClickable(false);
            bt_prestar.setEnabled(false);
            bt_represtar.setClickable(false);
            bt_represtar.setEnabled(false);
        }
    }

    public void refinanciar(View view){
        //Intent refinanciar = new Intent(this, Re_financiarActivity.class);
        //refinanciar.putExtra("msg", "");
        //refinanciar.putExtra("cliente_recivido", cliente_ID);
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        //startActivity(refinanciar);
        //finish();
        //System.exit(0);
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

    public void estado_cuenta (View view) {
        Intent cuadra_tura = new Intent(this, CuadraturaActivity.class);
        cuadra_tura.putExtra("msg", "");
        cuadra_tura.putExtra("cuadratura", "");
        cuadra_tura.putExtra("cliente_recivido", cliente_ID);
        Log.v("estado_cuenta0", "Estado_cliente.\n\nCliente recibido: " + cliente_ID + "\n\n.");
        cuadra_tura.putExtra("cambio", "0");
        cuadra_tura.putExtra("monto_creditito", "0");
        cuadra_tura.putExtra("activity_devolver", "Estado_cliente");
        //cuadra_tura.putExtra("mensaje_imprimir_pre", ""); //Para que sea: null.
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