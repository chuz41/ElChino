package com.example.elchino;

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
import androidx.appcompat.app.AppCompatActivity;
import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.DateUtilities;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.util.Date;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Estado_clienteActivity extends AppCompatActivity {

    private EditText et_ID;
    private TextView tv_esperar;
    private Button bt_consultar;
    private Button bt_prestar;
    private Button bt_represtar;
    private Button bt_abonar;
    private String cliente_ID = "";
    private Button bt_estado_cuenta;
    private TextView tv_caja;
    private Button bt_find_name;
    private int contador_de_opciones = 1;
    private Spinner sp_opciones;
    private String buscar_por = "";
    private final HashMap<String, String> sp_helper = new HashMap<>();
    private String nombre_cliente = "";
    private String apellido1_cliente = "";
    private String apellido2_cliente = "";
    private String apodo_cliente = "";
    private String archivo_cliente = "";
    private String telefono = "";
    private Button bt_editar;
    private String mes;
    private String anio;
    private String fecha;
    private final String globalVar = "globalVar_globalVar_.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_estado_cliente);
        cliente_ID = getIntent().getStringExtra("cliente_ID");
        String mensaje = getIntent().getStringExtra("mensaje");
        if (mensaje != null) {
            msg(mensaje);
        }
        TextView tv_fecha = findViewById(R.id.tv_fecha);
        et_ID = findViewById(R.id.et_ID);
        tv_esperar = findViewById(R.id.tv_esperar);
        bt_consultar = findViewById(R.id.bt_consultar);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        bt_editar = findViewById(R.id.bt_editar);
        bt_editar.setVisibility(View.INVISIBLE);
        bt_prestar = findViewById(R.id.bt_prestar);
        bt_represtar = findViewById(R.id.bt_represtar);
        bt_prestar.setVisibility(View.INVISIBLE);
        bt_represtar.setVisibility(View.INVISIBLE);
        bt_abonar = findViewById(R.id.bt_abonar);
        bt_abonar.setVisibility(View.INVISIBLE);
        bt_estado_cuenta = findViewById(R.id.bt_estado_cuenta);
        bt_estado_cuenta.setVisibility(View.INVISIBLE);
        bt_find_name = findViewById(R.id.bt_find_name);
        String string = "CAMBIAR";
        bt_find_name.setText(string);
        sp_opciones = findViewById(R.id.sp_opciones);
        sp_opciones.setEnabled(false);
        sp_opciones.setVisibility(View.INVISIBLE);
        tv_caja = findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");
        try {
            new BorrarArchivo(globalVar, this.getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            new GuardarArchivo(globalVar, "texto", this.getApplicationContext()).guardarFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        separarFecha();
        String fechaSaludo = fecha + "/" + mes + "/" + anio;
        tv_fecha.setText(fechaSaludo);
        try {
            corregirArchivos();
        } catch (IOException e) {
            e.printStackTrace();
        }
        mostrar_caja();
        if (cliente_ID.equals("")) {
            text_listener();
        } else {
            consultar(null);
        }
    }

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(null);
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        fecha = datosFecha.getDia();
    }

    public void editar_archivo (View view) {
        Intent editar_ac = new Intent(this, EditarActivity.class);
        editar_ac.putExtra("archivo_cliente", archivo_cliente);
        editar_ac.putExtra("cliente_id", cliente_ID);
        startActivity(editar_ac);
        finish();
        System.exit(0);
    }

    private void corregirArchivos () throws IOException {
        //////// ARCHIVO cierre  ////////////////////////////////////////////////////////////
        String[] archivos = fileList();
        boolean flag_borrar = false;
        if (archivo_existe(archivos, "cierre.txt")) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("cierre.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                if (linea != null) {
                    String[] split = linea.split(" ");
                    int fecha_file = Integer.parseInt(split[1]);
                    int hoy_fecha = Integer.parseInt(fecha);
                    if (fecha_file != hoy_fecha) {
                        flag_borrar = true;
                    }
                } else {
                    flag_borrar = true;
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            new AgregarLinea("fecha " + fecha, "cierre.txt", getApplicationContext());//La clase AgregarLinea crea el archivo en caso de que este no exista.
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }
        if (flag_borrar) {
            new BorrarArchivo("cierre.txt", getApplicationContext());
            new AgregarLinea("fecha " + fecha, "cierre.txt", getApplicationContext());
            new BorrarArchivo("cierre_cierre_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }
    }

    private void mostrar_caja () {
        String caja = "caja.txt";
        tv_caja.setText(imprimir_archivo(caja));
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

    private void spinner_listener () {
        sp_opciones.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        String seleccion = sp_opciones.getSelectedItem().toString();
                        if (tv_esperar.getText().toString().equals("Ingrese el nombre del cliente") || tv_esperar.getText().toString().equals("Ingrese el dia de la semana") || tv_esperar.getText().toString().equals("Ingrese el apodo del cliente") || tv_esperar.getText().toString().equals("Ingrese el apellido del cliente")) {
                            for (String key : sp_helper.keySet()) {
                                //Log.v("Sp_listener0", "Estado_cliente.\n\nKey:\n\n" + key + "\n\nDato: " + sp_helper.get(key) + "\n\nseleccion: " + seleccion + "\n\n.");
                                if (seleccion.equals(key)) {
                                    archivo_cliente = sp_helper.get(key);
                                    assert archivo_cliente != null;
                                    String[] split_ot = archivo_cliente.split("_C_");
                                    cliente_ID = split_ot[0];
                                    Log.v("Sp_listener1", "Estado_cliente.\n\nCliente ID: " + cliente_ID + "\n\nArchivo cliente: " + archivo_cliente + "\n\nContenido del archivo:\n\n" + imprimir_archivo(archivo_cliente));
                                    bt_consultar.setEnabled(true);
                                    bt_consultar.setClickable(true);
                                    bt_consultar.setVisibility(View.VISIBLE);
                                }
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    public void buscar_por_nombre (View view) {
        contador_de_opciones = contador_de_opciones + 1;
        if (contador_de_opciones == 6) {
            contador_de_opciones = 1;
        }
        if (contador_de_opciones == 1) {
            et_ID.setHint("Cedula...");
            String string = "Digite la identificacion del cliente";
            tv_esperar.setText(string);
            buscar_por = "ID_cliente";
        } else if (contador_de_opciones == 2) {
            et_ID.setHint("Nombre...");
            String string = "Ingrese el nombre del cliente";
            tv_esperar.setText(string);
            buscar_por = "nombre_cliente";
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
        } else if (contador_de_opciones == 3) {
            et_ID.setHint("Apellido...");
            String string = "Ingrese el apellido del cliente";
            tv_esperar.setText(string);
            buscar_por = "apellido1_cliente";
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
        } else if (contador_de_opciones == 4) {
            et_ID.setHint("Apodo...");
            String string = "Ingrese el apodo del cliente";
            tv_esperar.setText(string);
            buscar_por = "apodo_cliente";
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
        } else if (contador_de_opciones == 5) {
            et_ID.setHint("Dia de pago...");
            String string = "Ingrese el dia de la semana";
            tv_esperar.setText(string);
            buscar_por = "dia_semana";
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
        }
    }

    private boolean archivo_existe (String[] archivos, String file_name){
        for (String archivo : archivos) {
            if (file_name.equals(archivo)) {
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
        et_ID.addTextChangedListener(new TextWatcher() {//Implementacion de un text listener
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
                    bt_consultar.setClickable(false);
                    bt_consultar.setEnabled(false);
                    sp_opciones.setVisibility(View.INVISIBLE);
                    String[] archivos = fileList();
                    if (et_ID.getText().toString().contains("*") || et_ID.getText().toString().contains(" ")) {
                        Log.v("TextListener0.1", "******EERROR************Estado_cliente.\n\nClienteID: " + cliente_ID + "\n\n");
                    } else {
                        for (String archivo : archivos) {
                            Pattern pattern = Pattern.compile(et_ID.getText().toString(), Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(archivo);
                            boolean matchFound = matcher.find();
                            if (matchFound) {
                                if (s.length() >= 9) {
                                    bt_consultar.setEnabled(true);
                                    bt_consultar.setClickable(true);
                                }
                            }
                        }
                    }
                } else if (tv_esperar.getText().toString().equals("Ingrese el nombre del cliente") || tv_esperar.getText().toString().equals("Ingrese el apellido del cliente") || tv_esperar.getText().toString().equals("Ingrese el apodo del cliente")) {
                    StringBuilder parametro = new StringBuilder();
                    String[] archivos = fileList();
                    if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
                        Log.v("Consultarxxx", "*******ERRPR***********Estado_cliente.\n\nClienteID: " + cliente_ID + "\n\n");
                    } else {
                        for (String value : archivos) {
                            if (value.contains(" ") || value.contains("*")) {
                                Log.v("Consultarxxx", "*******ERRPR***********Estado_cliente.\n\nClienteID: " + cliente_ID + "\n\n");
                            } else {
                                String cliente_ID_s;
                                try {
                                    InputStreamReader archivo = new InputStreamReader(openFileInput(value));
                                    BufferedReader br = new BufferedReader(archivo);
                                    String linea = br.readLine();
                                    boolean param_encontrado = false;
                                    while (linea != null) {
                                        if (linea.contains("_separador_")) {
                                            String[] split = linea.split("_separador_");
                                            if (split[0].equals(buscar_por)) {
                                                if (split[1].contains(s)) {
                                                    param_encontrado = true;
                                                    Log.v("param_encontrado", "Estado_cliente.\n\nParam: " + split[0] + "\n\nContenido del parametro: " + split[1] + "\n\n.");
                                                }
                                            }
                                            switch (split[0]) {
                                                case "nombre_cliente":
                                                    nombre_cliente = split[1];
                                                    break;
                                                case "apellido1_cliente":
                                                    apellido1_cliente = split[1];
                                                    break;
                                                case "apellido2_cliente":
                                                    apellido2_cliente = split[1];
                                                    break;
                                                case "ID_cliente":
                                                    cliente_ID_s = split[1];
                                                    if (cliente_ID_s.contains(" ") || cliente_ID_s.contains("*")) {
                                                        param_encontrado = false;
                                                    }
                                                    break;
                                                case "apodo_cliente":
                                                    apodo_cliente = split[1];
                                                    break;
                                            }
                                        }
                                        linea = br.readLine();
                                    }
                                    if (param_encontrado) {
                                        Log.v("param_encontrado0", "Estrado_cliente.\n\nNombre cliente: " + nombre_cliente + "\n\napellido1 cliente: " + apellido1_cliente +
                                                "\n\napellido2 cliente: " + apellido2_cliente + "\n\nApodo cliente: " + apodo_cliente + "\n\n.");
                                        parametro.append(nombre_cliente).append("_sep_").append(apellido1_cliente).append("_sep_").append(apellido2_cliente).append("_sep_(").append(apodo_cliente).append(")").append("_sep_").append(value).append("_sep_").append("_sop_");
                                        Log.v("param_encontrado1", "Estado_cliente.\n\nparametro:\n\n" + parametro + "\n\n.");
                                    }
                                    br.close();
                                    archivo.close();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                        }
                    }
                    llenar_spinner(parametro.toString());
                } else if (tv_esperar.getText().toString().equals("Ingrese el dia de la semana")) {
                    StringBuilder parametro = new StringBuilder();
                    String[] archivos = fileList();
                    String dia_encontrado = "";
                    boolean param_encontrado;
                    boolean param_encontrado2;
                    String cliente_ID_obtenido = "";
                    if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
                        Log.v("Consultarxxx1", "Estado_cliente.\n\nClienteID: " + cliente_ID + "\n\n");
                    } else {
                        for (String value : archivos) {
                            param_encontrado2 = false;
                            if (value.contains(" ") || value.contains("*")) {
                                Log.v("Consultarxxx2", "Estado_cliente.\n\nClienteID: " + cliente_ID + "\n\n");
                            } else {
                                String cliente_ID_s;
                                Pattern pattern = Pattern.compile("_P_", Pattern.CASE_INSENSITIVE);
                                Matcher matcher = pattern.matcher(value);
                                boolean matchFound = matcher.find();
                                if (matchFound) {
                                    try {
                                        InputStreamReader archivo = new InputStreamReader(openFileInput(value));
                                        BufferedReader br = new BufferedReader(archivo);
                                        String linea = br.readLine();
                                        while (linea != null) {
                                            if (linea.contains("_separador_")) {
                                                String[] split = linea.split("_separador_");
                                                Log.v("por_fecha1", "Estado_cliente.\n\nLinea:\n\n" + linea + "\n\n.");
                                                if (split[0].equals("fecha_credito")) {
                                                    String fecha_creditico = split[1];
                                                    String[] split_fecha_creditico = fecha_creditico.split("/");
                                                    fecha_creditico = split_fecha_creditico[2] + "-" + split_fecha_creditico[1] + "-" + split_fecha_creditico[0];
                                                    Date fecha_creditico_D = DateUtilities.stringToDate(fecha_creditico);
                                                    String[] split_fecha_creditico_D = fecha_creditico_D.toString().split(" ");
                                                    dia_encontrado = obtener_dia_espaniol(split_fecha_creditico_D[0]);
                                                    Log.v("por_fecha1", "Estado_cliente.\n\nDia encontrado: " + dia_encontrado + "\n\n.");
                                                    if (dia_encontrado.contains(s)) {
                                                        param_encontrado2 = true;
                                                        Log.v("param_encontrado", "Estado_cliente.\n\nParam: " + split[0] + "\n\nContenido del parametro: " + split[1] + "\n\n.");
                                                    }
                                                }
                                                if (split[0].equals("saldo_mas_intereses")) {
                                                    if (Integer.parseInt(split[1]) < 100) {
                                                        param_encontrado2 = false;
                                                    }
                                                } else if (split[0].equals("ID_credito")) {
                                                    cliente_ID_obtenido = split[1];
                                                    Log.v("semana0", "Estado_cliente.\n\ncliente obtenido: " + cliente_ID_obtenido);
                                                    String[] split_ID = cliente_ID_obtenido.split("_");
                                                    cliente_ID_obtenido = split_ID[0];
                                                    Log.v("semana1", "Estado_cliente.\n\ncliente obtenido: " + cliente_ID_obtenido);
                                                }
                                            }
                                            linea = br.readLine();
                                        }
                                        br.close();
                                        archivo.close();
                                    } catch (IOException | ParseException e) {
                                        e.printStackTrace();
                                    }
                                    if (param_encontrado2) {
                                        if (cliente_ID_obtenido.contains("*") || cliente_ID_obtenido.contains(" ")) {
                                            Log.v("Consultarxxx", "Estado_cliente.\n\nClienteID: " + cliente_ID_obtenido + "\n\n");
                                        } else {
                                            try {
                                                InputStreamReader archivo = new InputStreamReader(openFileInput(cliente_ID_obtenido + "_C_.txt"));
                                                BufferedReader br = new BufferedReader(archivo);
                                                String linea = br.readLine();
                                                param_encontrado = false;
                                                while (linea != null) {
                                                    if (linea.contains("_separador_")) {
                                                        String[] split = linea.split("_separador_");
                                                        Log.v("semana2", "Estado_cliente.\n\nLinea:\n\n" + linea + "\n\n.");
                                                        if (split[0].equals("ID_cliente")) {
                                                            if (split[1].equals(cliente_ID_obtenido)) {
                                                                param_encontrado = true;
                                                                Log.v("param_encontrado_semana", "Estado_cliente.\n\nParam: " + split[0] + "\n\nContenido del parametro: " + split[1] + "\n\n.");
                                                            }
                                                        }
                                                        switch (split[0]) {
                                                            case "nombre_cliente":
                                                                nombre_cliente = split[1];
                                                                break;
                                                            case "apellido1_cliente":
                                                                apellido1_cliente = split[1];
                                                                break;
                                                            case "apellido2_cliente":
                                                                apellido2_cliente = split[1];
                                                                break;
                                                            case "ID_cliente":
                                                                cliente_ID_s = split[1];
                                                                if (cliente_ID_s.contains(" ") || cliente_ID_s.contains("*")) {
                                                                    param_encontrado = false;
                                                                }
                                                                break;
                                                            case "apodo_cliente":
                                                                apodo_cliente = split[1];
                                                                break;
                                                        }
                                                    }
                                                    linea = br.readLine();
                                                }
                                                if (param_encontrado) {
                                                    Log.v("param_encontrado0", "Estrado_cliente.\n\nNombre cliente: " + nombre_cliente + "\n\napellido1 cliente: " + apellido1_cliente +
                                                            "\n\napellido2 cliente: " + apellido2_cliente + "\n\nApodo cliente: " + apodo_cliente + "\n\n.");
                                                    parametro.append(nombre_cliente).append("_sep_").append(apellido1_cliente).append("_sep_").append(apellido2_cliente).append("_sep_(").append(dia_encontrado).append(")").append("_sep_").append(cliente_ID_obtenido).append("_C_.txt").append("_sep_").append("_sop_");
                                                    Log.v("param_encontrado1", "Estado_cliente.\n\nparametro:\n\n" + parametro + "\n\n.");
                                                }
                                                br.close();
                                                archivo.close();
                                            } catch (IOException ee) {
                                                ee.printStackTrace();
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                    llenar_spinner(parametro.toString());
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private String obtener_dia_espaniol (String dia_ingles) {
        String flag = "";
        switch (dia_ingles) {
            case "Sun":
                flag = "Domingo";
                break;
            case "Mon":
                flag = "Lunes";
                break;
            case "Tue":
                flag = "Martes";
                break;
            case "Wed":
                flag = "Miercoles";
                break;
            case "Thu":
                flag = "Jueves";
                break;
            case "Fri":
                flag = "Viernes";
                break;
            case "Sat":
                flag = "Sabado";
                break;
        }
        return flag;
    }

    private void llenar_spinner (String parametro) {
        Log.v("llenando_spinner0", "Estado_cliente.\n\nCantidad de archivos: " + "No se sabe aqui!!!"+ "\n\nParametro:\n\n" + parametro + "\n\n.");
        String[] split = parametro.split("_sop_");
        int largo_split = split.length;
        StringBuilder spinner_llenar = new StringBuilder();
        sp_helper.clear();
        if (largo_split > 0) {
            for (int i = 0; i < largo_split; i++) {
                String[] splitw = split[i].split("_sep_");
                Log.v("llenando_spinner1", "Estado_cliente.\n\nsplit[" + i + "]: " + split[i] + "\n\nParametro:\n\n" + parametro + "\n\n.");
                int largo_splitw = splitw.length;
                Log.v("llenando_spinner1.5", "Estado_cliente.\n\nLargo_splitw: " + largo_splitw + "\n\nsplitw[0]: " + splitw[0] + "\n\n.");
                if (largo_splitw > 3) {
                    String helperS = split[i].replace(splitw[4], "");
                    helperS = helperS.replace("_sep_", " ");
                    spinner_llenar.append(helperS).append("_sip_");
                    Log.v("llenando_spinner2", "Estado_cliente.\n\nhelperS: \n\n" + helperS + "\n\nArchivo cliente: " + splitw[4] + "\n\n.");
                    sp_helper.put(helperS, splitw[4]);
                }
            }
        }
        String[] split_spinner = spinner_llenar.toString().split("_sip_");
        int largo_otroSpinner = split_spinner.length;
        if (largo_otroSpinner == 0) {
            Log.v("Llenar_spinner_3", "Estado_cliente.\n\nlargo_otroSpinner: " + largo_otroSpinner + "\n\n.");
        } else {
            sp_opciones.setEnabled(true);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, split_spinner);
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
        sp_opciones.setVisibility(View.INVISIBLE);
        bt_find_name.setVisibility(View.INVISIBLE);
        bt_find_name.setEnabled(false);
        tv_esperar.setText("");
        String[] archivos = fileList();
        String puntuacion_cliente = "";
        StringBuilder archivoCompleto = new StringBuilder();
        String consultador;
        if (cliente_ID.equals("") && tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
            Log.v("consultar-1", "Estado_cliente.\n\n");
            consultador = et_ID.getText().toString() + "_C_";
            cliente_ID = et_ID.getText().toString();
        } else {
            consultador = cliente_ID + "_C_";
        }
        Log.v("consultar0", "Estado_cliente.\n\nCliente ID: " + cliente_ID + "\n\n");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("Consultar0.1", "Estado_cliente.\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
            for (String s : archivos) {
                Pattern pattern = Pattern.compile(consultador, Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(s);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    archivo_cliente = s;
                    try {
                        InputStreamReader archivo = new InputStreamReader(openFileInput(s));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        while (linea != null) {
                            String[] split = linea.split("_separador_");
                            if (split[0].equals("puntuacion_cliente")) {
                                Log.v("consultar1", "Estado_cliente.\n\nPuntuacion cliente: " + split[1] + "\n\n");
                                puntuacion_cliente = split[1];
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
                }
                if (archivoCompleto.toString().equals("")) {
                    Log.v("consultar_pre2", "Estado_cliente.\n\nArchivo sin contenido.\n\n.");//No se encontro el cliente, continue...
                } else {
                    Log.v("consultar2", "Estado_cliente.\n\narchivoCompleto:\n\n" + archivoCompleto + "\n\n.");
                    presentar_cliente(archivoCompleto.toString(), puntuacion_cliente);
                    break;
                }
            }
        }
        if (archivoCompleto.toString().equals("")) {//No se encontro el cliente
            Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
            text_listener();
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

    public void nuevo_credito(View view){
        Intent nuevo_credito = new Intent(this, Nuevo_creditoActivity.class);
        nuevo_credito.putExtra("msg", "");
        nuevo_credito.putExtra("cliente_recivido", cliente_ID);
        nuevo_credito.putExtra("activity_devolver", "Estado_cliente");
        startActivity(nuevo_credito);
        finish();
        System.exit(0);
    }

    public void estado_cuenta (View view) {
        Intent cuadra_tura = new Intent(this, CuadraturaActivity.class);
        cuadra_tura.putExtra("msg", "");
        cuadra_tura.putExtra("cuadratura", "null");
        cuadra_tura.putExtra("cliente_recivido", cliente_ID);
        obtenerNombreCliente();
        obtenerPhoneCliente();
        cuadra_tura.putExtra("nombreCliente", nombre_cliente + " " + apellido1_cliente);
        cuadra_tura.putExtra("telefono", telefono);
        Log.v("estado_cuenta_0", "Estado_cliente.\n\nCliente recibido: " + cliente_ID + "\n\n.");
        cuadra_tura.putExtra("cambio", "0");
        cuadra_tura.putExtra("monto_creditito", "0");
        cuadra_tura.putExtra("activity_devolver", "Estado_cliente");
        cuadra_tura.putExtra("mensaje_imprimir_pre", ""); //Para que sea: null.
        startActivity(cuadra_tura);
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
                        Log.v("obtenerPhoneCliente_0", "Estado_cliente.\n\nlinea:\n\n" + linea + "\n\ntelefono: " + telefono + "\n\n.");
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

    private void obtenerNombreCliente () {
        String nombreArchivo = cliente_ID + "_C_.txt";
        if (archivo_existe(fileList(), nombreArchivo)) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(nombreArchivo));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null) {
                    Log.v("obtenerNombreCliente_0", "Estado_cliente.\n\nlinea:\n\n" + linea + "\n\n.");
                    String[] split = linea.split("_separador_");
                    if (split[0].equals("nombre_cliente")) {
                        nombre_cliente = split[1];
                    }
                    if (split[0].equals("apellido1_cliente")) {
                        apellido1_cliente = split[1];
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

    private void msg (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    public void abonar(View view) {
        Intent abonar = new Intent(this, AbonarActivity.class);
        abonar.putExtra("msg", "");
        abonar.putExtra("cliente_recivido", cliente_ID);
        abonar.putExtra("abono_cero", "");
        abonar.putExtra("activity_devolver", "Estado_cliente");
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