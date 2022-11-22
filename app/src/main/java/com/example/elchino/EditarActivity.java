package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EditarActivity extends AppCompatActivity {

    private String archivo_cliente;
    private String cliente_ID;
    private TextView tv_saludo;
    private TextView tv_esperar;
    private TextView tv_mensaje;
    private Button bt_editar_cliente;
    private Button bt_editar_credito;
    private Button bt_guardar_cambios;
    private TextView tv_ID_cliente;
    private TextView tv_nombre_cliente;
    private TextView tv_apellido1_cliente;
    private TextView tv_apellido2_cliente;
    private TextView tv_apodo_cliente;
    private TextView tv_sexo_cliente;
    private TextView tv_direccion_cliente;
    private TextView tv_puntuacion_cliente;
    private TextView tv_monto_disponible;
    private TextView tv_telefono1_cliente;
    private TextView tv_telefono2_cliente;

    private EditText et_ID_cliente;
    private EditText et_nombre_cliente;
    private EditText et_apellido1_cliente;
    private EditText et_apellido2_cliente;
    private EditText et_apodo_cliente;
    private EditText et_sexo_cliente;
    private EditText et_direccion_cliente;
    private EditText et_puntuacion_cliente;
    private EditText et_monto_disponible;
    private EditText et_telefono1_cliente;
    private EditText et_telefono2_cliente;

    private String ID_cliente;
    private String nombre_cliente;
    private String apellido1_cliente;
    private String apellido2_cliente;
    private String apodo_cliente;
    private String sexo_cliente;
    private String direccion_cliente;
    private String puntuacion_cliente;
    private String monto_disponible;
    private String telefono1_cliente;
    private String telefono2_cliente;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editar);
        archivo_cliente = getIntent().getStringExtra("archivo_cliente");
        cliente_ID = getIntent().getStringExtra("cliente_id");
        tv_esperar = (TextView) findViewById(R.id.tv_esperar);
        tv_esperar.setText("Seleccione la opcion que desea editar...");
        tv_saludo = (TextView) findViewById(R.id.tv_saludo);
        tv_saludo.setText("Editar");
        tv_mensaje = (TextView) findViewById(R.id.tv_mensaje);
        bt_editar_cliente = (Button) findViewById(R.id.bt_editar_cliente);
        bt_editar_credito = (Button) findViewById(R.id.bt_editar_credito);
        bt_guardar_cambios = (Button) findViewById(R.id.bt_guardar_cambios);
        bt_guardar_cambios.setVisibility(View.INVISIBLE);
        tv_ID_cliente = (TextView) findViewById(R.id.tv_ID_cliente);
        tv_nombre_cliente = (TextView) findViewById(R.id.tv_nombre_cliente);
        tv_apellido1_cliente = (TextView) findViewById(R.id.tv_apellido1_cliente);
        tv_apellido2_cliente = (TextView) findViewById(R.id.tv_apellido2_cliente);
        tv_apodo_cliente = (TextView) findViewById(R.id.tv_apodo_cliente);
        tv_sexo_cliente = (TextView) findViewById(R.id.tv_sexo_cliente);
        tv_direccion_cliente = (TextView) findViewById(R.id.tv_direccion_cliente);
        tv_puntuacion_cliente = (TextView) findViewById(R.id.tv_puntuacion_cliente);
        tv_monto_disponible = (TextView) findViewById(R.id.tv_monto_disponible);
        tv_telefono1_cliente = (TextView) findViewById(R.id.tv_telefono1_cliente);
        tv_telefono2_cliente = (TextView) findViewById(R.id.tv_telefono2_cliente);
        tv_ID_cliente.setVisibility(View.INVISIBLE);
        tv_nombre_cliente.setVisibility(View.INVISIBLE);
        tv_apellido1_cliente.setVisibility(View.INVISIBLE);
        tv_apellido2_cliente.setVisibility(View.INVISIBLE);
        tv_apodo_cliente.setVisibility(View.INVISIBLE);
        tv_sexo_cliente.setVisibility(View.INVISIBLE);
        tv_direccion_cliente.setVisibility(View.INVISIBLE);
        tv_puntuacion_cliente.setVisibility(View.INVISIBLE);
        tv_monto_disponible.setVisibility(View.INVISIBLE);
        tv_telefono1_cliente.setVisibility(View.INVISIBLE);
        tv_telefono2_cliente.setVisibility(View.INVISIBLE);

        et_ID_cliente = (EditText) findViewById(R.id.et_ID_cliente);
        et_nombre_cliente = (EditText) findViewById(R.id.et_nombre_cliente);
        et_apellido1_cliente = (EditText) findViewById(R.id.et_apellido1_cliente);
        et_apellido2_cliente = (EditText) findViewById(R.id.et_apellido2_cliente);
        et_apodo_cliente = (EditText) findViewById(R.id.et_apodo_cliente);
        et_sexo_cliente = (EditText) findViewById(R.id.et_sexo_cliente);
        et_direccion_cliente = (EditText) findViewById(R.id.et_direccion_cliente);
        et_puntuacion_cliente = (EditText) findViewById(R.id.et_puntuacion_cliente);
        et_monto_disponible = (EditText) findViewById(R.id.et_monto_disponible);
        et_telefono1_cliente = (EditText) findViewById(R.id.et_telefono1_cliente);
        et_telefono2_cliente = (EditText) findViewById(R.id.et_telefono2_cliente);
        et_ID_cliente.setVisibility(View.INVISIBLE);
        et_nombre_cliente.setVisibility(View.INVISIBLE);
        et_apellido1_cliente.setVisibility(View.INVISIBLE);
        et_apellido2_cliente.setVisibility(View.INVISIBLE);
        et_apodo_cliente.setVisibility(View.INVISIBLE);
        et_sexo_cliente.setVisibility(View.INVISIBLE);
        et_direccion_cliente.setVisibility(View.INVISIBLE);
        et_puntuacion_cliente.setVisibility(View.INVISIBLE);
        et_monto_disponible.setVisibility(View.INVISIBLE);
        et_telefono1_cliente.setVisibility(View.INVISIBLE);
        et_telefono2_cliente.setVisibility(View.INVISIBLE);



        String texto = "####!#### CUIDADO!!! ####!####\n\nEstimado usuario, se recomienda\nseguir los siguientes consejos:\n\n" +
                "1. Recuerde que todo cambio que\ntenga que ver con las cuentas debe\nser aprobado por la banca.\n\n" +
                "2. Debe estar seguro que el cambio\nque va a realizar sea\nABSOLUTAMENTE necesario.\n\n" +
                "3. Nunca debe utilizar esta funcion\ncon fines que atenten contra las\nfinanzas de la empresa.\n\n" +
                "4. Se recomienda tomar nota manual\ndel cambio que aqui se va a realizar.\n\n" +
                "5. NO DEJE ESPACIOS EN BLANCO!!!";
        tv_mensaje.setText(texto);

    }

    public void editar_cliente (View view) {
        ocultar_todo();

        tv_ID_cliente.setEnabled(true);
        et_ID_cliente.setEnabled(true);
        tv_nombre_cliente.setEnabled(true);
        et_nombre_cliente.setEnabled(true);
        tv_apellido1_cliente.setEnabled(true);
        et_apellido1_cliente.setEnabled(true);
        tv_apellido2_cliente.setEnabled(true);
        et_apellido2_cliente.setEnabled(true);
        tv_apodo_cliente.setEnabled(true);
        et_apodo_cliente.setEnabled(true);
        tv_sexo_cliente.setEnabled(true);
        et_sexo_cliente.setEnabled(true);
        tv_direccion_cliente.setEnabled(true);
        et_direccion_cliente.setEnabled(true);
        tv_monto_disponible.setEnabled(true);
        et_monto_disponible.setEnabled(true);
        tv_telefono1_cliente.setEnabled(true);
        et_telefono1_cliente.setEnabled(true);
        tv_telefono2_cliente.setEnabled(true);
        et_telefono2_cliente.setEnabled(true);
        tv_puntuacion_cliente.setEnabled(true);
        et_puntuacion_cliente.setEnabled(true);

        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(archivo_cliente));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split("_separador_");
                if (split[0].equals("ID_cliente")) {
                    ID_cliente = split[1];
                    tv_ID_cliente.setText("Identificacion");
                    et_ID_cliente.setText(ID_cliente);
                    text_listener_ID();
                }
                if (split[0].equals("nombre_cliente")) {
                    nombre_cliente = split[1];
                    tv_nombre_cliente.setText("Nombre");
                    et_nombre_cliente.setText(nombre_cliente);
                }
                if (split[0].equals("apellido1_cliente")) {
                    apellido1_cliente = split[1];
                    tv_apellido1_cliente.setText("Primer apellido");
                    et_apellido1_cliente.setText(apellido1_cliente);
                }
                if (split[0].equals("apellido2_cliente")) {
                    apellido2_cliente = split[1];
                    tv_apellido2_cliente.setText("Segundo apellido");
                    et_apellido2_cliente.setText(apellido2_cliente);
                }
                if (split[0].equals("apodo_cliente")) {
                    apodo_cliente = split[1];
                    tv_apodo_cliente.setText("Apodo");
                    et_apodo_cliente.setText(apodo_cliente);
                }
                if (split[0].equals("sexo_cliente")) {
                    sexo_cliente = split[1];
                    tv_sexo_cliente.setText("Sexo");
                    et_sexo_cliente.setText(sexo_cliente);
                }
                if (split[0].equals("direccion_cliente")) {
                    direccion_cliente = split[1];
                    tv_direccion_cliente.setText("Direccion");
                    et_direccion_cliente.setText(direccion_cliente);
                }
                if (split[0].equals("monto_disponible")) {
                    monto_disponible = split[1];
                    tv_monto_disponible.setText("Monto disponible");
                    et_monto_disponible.setText(monto_disponible);
                }
                if (split[0].equals("telefono1_cliente")) {
                    telefono1_cliente = split[1];
                    tv_telefono1_cliente.setText("Telefono 1");
                    et_telefono1_cliente.setText(telefono1_cliente);
                }
                if (split[0].equals("telefono2_cliente")) {
                    telefono2_cliente = split[1];
                    tv_telefono2_cliente.setText("Telefono 2");
                    et_telefono2_cliente.setText(telefono2_cliente);
                }
                if (split[0].equals("puntuacion_cliente")) {
                    puntuacion_cliente = split[1];
                    tv_puntuacion_cliente.setText("Puntuacion");
                    et_puntuacion_cliente.setText(puntuacion_cliente);
                }
                linea = br.readLine();
            }
            //archivoCompleto = archivoCompleto + linea + "\n";
            br.close();
            archivo.close();
        } catch (IOException e) {
        }

        et_ID_cliente.setHint("Identificacion...");
        et_nombre_cliente.setHint("Nombre...");
        et_apellido1_cliente.setHint("Primer apellido...");
        et_apellido2_cliente.setHint("Segundo apellido...");
        et_apodo_cliente.setHint("Apodo...");
        et_sexo_cliente.setHint("Sexo...");
        et_direccion_cliente.setHint("Direccion...");
        et_puntuacion_cliente.setHint("Puntuacione...");
        et_monto_disponible.setHint("Monto disponible...");
        et_telefono1_cliente.setHint("Telefono 1...");
        et_telefono2_cliente.setHint("Telefono 2...");

    }

    public void confirmar (View view) throws IOException {
        String contenido = "";
        boolean flag = true;
        if (archivo_cliente.contains(" ") || archivo_cliente.contains("*")) {
            flag = false;// Archivo corrompido.
        } else {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput(archivo_cliente));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();

                while (linea != null) {
                    String[] split = linea.split("_separador_");

                    if (split[0].equals("ID_cliente")) {
                        ID_cliente = et_ID_cliente.getText().toString();
                        if (ID_cliente.equals("")) {
                            flag = false;
                            msg("Identificacion vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            ID_cliente.replace("\n", "");
                            ID_cliente.replace(" ", "");
                            if (ID_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en Identificacion del cliente.");
                            } else {
                                tv_ID_cliente.setEnabled(false);
                                et_ID_cliente.setEnabled(false);
                                msg("La identificacion del cliente no se puede cambiar!!!");
                                //linea = linea.replace(split[1], ID_cliente);
                            }
                        }

                    } else if (split[0].equals("nombre_cliente")) {
                        nombre_cliente = et_nombre_cliente.getText().toString();
                        if (nombre_cliente.equals("")) {
                            flag = false;
                            msg("Nombre vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            if (nombre_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en nombre del cliente.");
                            } else {
                                nombre_cliente.replace("\n", "");
                                tv_nombre_cliente.setEnabled(false);
                                et_nombre_cliente.setEnabled(false);
                                linea = linea.replace(split[1], nombre_cliente);
                            }
                        }


                    } else if (split[0].equals("apellido1_cliente")) {
                        apellido1_cliente = et_apellido1_cliente.getText().toString();
                        if (apellido1_cliente.equals("")) {
                            flag = false;
                            msg("Primer apellido vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            if (apellido1_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en apellido 1 del cliente.");
                            } else {
                                apellido1_cliente.replace("\n", "");
                                tv_apellido1_cliente.setEnabled(false);
                                et_apellido1_cliente.setEnabled(false);
                                linea = linea.replace(split[1], apellido1_cliente);
                            }
                        }
                    } else if (split[0].equals("apellido2_cliente")) {
                        apellido2_cliente = et_apellido2_cliente.getText().toString();
                        if (apellido2_cliente.equals("")) {
                            flag = false;
                            msg("Segundo apellido vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            if (apellido2_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en apellido 2 del cliente.");
                            } else {
                                apellido2_cliente.replace("\n", "");
                                tv_apellido2_cliente.setEnabled(false);
                                et_apellido2_cliente.setEnabled(false);
                                linea = linea.replace(split[1], apellido2_cliente);
                            }
                        }
                    } else if (split[0].equals("apodo_cliente")) {
                        apodo_cliente = et_apodo_cliente.getText().toString();
                        if (apodo_cliente.equals("")) {
                            flag = false;
                            msg("Apodo vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            if (apodo_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en apodo del cliente.");
                            } else {
                                apodo_cliente.replace("\n", "");
                                tv_apodo_cliente.setEnabled(false);
                                et_apodo_cliente.setEnabled(false);
                                linea = linea.replace(split[1], apodo_cliente);
                            }
                        }
                    } else if (split[0].equals("sexo_cliente")) {
                        sexo_cliente = et_sexo_cliente.getText().toString();
                        if (sexo_cliente.equals("")) {
                            flag = false;
                            msg("Sexo vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            if (sexo_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en sexo del cliente.");
                            } else {
                                sexo_cliente.replace("\n", "");
                                tv_sexo_cliente.setEnabled(false);
                                et_sexo_cliente.setEnabled(false);
                                linea = linea.replace(split[1], sexo_cliente);
                            }
                        }
                    } else if (split[0].equals("direccion_cliente")) {
                        direccion_cliente = et_direccion_cliente.getText().toString();
                        if (direccion_cliente.equals("")) {
                            flag = false;
                            msg("Direccion vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            if (direccion_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en apodo del cliente.");
                            } else {
                                direccion_cliente.replace("\n", "");
                                tv_direccion_cliente.setEnabled(false);
                                et_direccion_cliente.setEnabled(false);
                                linea = linea.replace(split[1], direccion_cliente);
                            }
                        }
                    } else if (split[0].equals("monto_disponible")) {
                        monto_disponible = et_monto_disponible.getText().toString();
                        if (monto_disponible.equals("")) {
                            flag = false;
                            msg("Monto disponible vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {

                            if (monto_disponible.contains("\n")) {
                                flag = false;
                                msg("Error en monto disponible.");
                            } else {
                                boolean isNumeric = monto_disponible.matches("[+-]?\\d*(\\.\\d+)?");

                                if (isNumeric) {

                                    if (Integer.valueOf(monto_disponible) < 1000 | Long.valueOf(monto_disponible) > 30000000) {
                                        flag = false;
                                        msg("Monto disponible debe ser un numero valido!");
                                    } else {
                                        tv_monto_disponible.setEnabled(false);
                                        et_monto_disponible.setEnabled(false);
                                        linea = linea.replace(split[1], monto_disponible);
                                    }

                                } else {
                                    flag = false;
                                    msg("Monto disponible debe ser un numero!");
                                }
                            }

                        }
                    } else if (split[0].equals("telefono1_cliente")) {
                        telefono1_cliente = et_telefono1_cliente.getText().toString();
                        if (telefono1_cliente.equals("")) {
                            flag = false;
                            msg("Telefono 1 vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            if (telefono1_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en telefono 1 del cliente.");
                            } else {
                                tv_telefono1_cliente.setEnabled(false);
                                et_telefono1_cliente.setEnabled(false);
                                linea = linea.replace(split[1], telefono1_cliente);
                            }
                        }
                    } else if (split[0].equals("telefono2_cliente")) {
                        telefono2_cliente = et_telefono2_cliente.getText().toString();
                        if (telefono2_cliente.equals("")) {
                            flag = false;
                            msg("Telefono 2 vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {
                            if (telefono2_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en telefono 2 del cliente.");
                            } else {
                                tv_telefono2_cliente.setEnabled(false);
                                et_telefono2_cliente.setEnabled(false);
                                linea = linea.replace(split[1], telefono2_cliente);
                            }
                        }
                    } else if (split[0].equals("puntuacion_cliente")) {

                        puntuacion_cliente = et_puntuacion_cliente.getText().toString();
                        puntuacion_cliente.replace("\n", "");
                        puntuacion_cliente.replace(" ", "");
                        if (puntuacion_cliente.equals("")) {
                            flag = false;
                            msg("Puntuacion vacio.\n\nDebe llenar todos los\ncampos para poder editar!");
                        } else {


                            if (puntuacion_cliente.contains("\n")) {
                                flag = false;
                                msg("Error en puntuacion del cliente.");
                            } else {
                                boolean isNumeric = puntuacion_cliente.matches("[+-]?\\d*(\\.\\d+)?");

                                if (isNumeric) {

                                    if (Integer.valueOf(puntuacion_cliente) < 1 | Integer.valueOf(puntuacion_cliente) > 9) {
                                        flag = false;
                                        msg("Puntuacion debe ser un numero del 1 al 9.");
                                    } else {
                                        tv_puntuacion_cliente.setEnabled(false);
                                        et_puntuacion_cliente.setEnabled(false);
                                        linea = linea.replace(split[1], puntuacion_cliente);
                                    }


                                } else {
                                    flag = false;
                                    msg("Puntuacion debe ser un numero del 1 al 9.");
                                }
                            }
                        }
                    } else {
                        //Do nothing. La linea se queda igual.
                    }
                    contenido = contenido + linea + "\n";
                    linea = br.readLine();
                }
                //archivoCompleto = archivoCompleto + linea + "\n";
                br.close();
                archivo.close();
            } catch (IOException e) {
            }
        }
        if (flag) {
            borrar_archivo(archivo_cliente);
            crear_archivo(archivo_cliente);
            guardar(contenido, archivo_cliente);
            Toast.makeText(this, "Archivo del cliente se ha\nmodificado correctamente!!!", Toast.LENGTH_LONG).show();
            mostrar_todo();
        } else {
            Toast.makeText(this, "ARCHIVO CORROMPIDO!\nNo se realizo ninguna accion!", Toast.LENGTH_LONG).show();
            mostrar_todo();
        }

    }

    public void editar_credito (View view) {
        Toast.makeText(this, "En construccion!!!\n\nVolveremos pronto...", Toast.LENGTH_LONG).show();
    }

    private void mostrar_todo () {
        tv_ID_cliente.setVisibility(View.INVISIBLE);
        tv_nombre_cliente.setVisibility(View.INVISIBLE);
        tv_apellido1_cliente.setVisibility(View.INVISIBLE);
        tv_apellido2_cliente.setVisibility(View.INVISIBLE);
        tv_apodo_cliente.setVisibility(View.INVISIBLE);
        tv_sexo_cliente.setVisibility(View.INVISIBLE);
        tv_direccion_cliente.setVisibility(View.INVISIBLE);
        tv_puntuacion_cliente.setVisibility(View.INVISIBLE);
        tv_monto_disponible.setVisibility(View.INVISIBLE);
        tv_telefono1_cliente.setVisibility(View.INVISIBLE);
        tv_telefono2_cliente.setVisibility(View.INVISIBLE);
        et_ID_cliente.setVisibility(View.INVISIBLE);
        et_nombre_cliente.setVisibility(View.INVISIBLE);
        et_apellido1_cliente.setVisibility(View.INVISIBLE);
        et_apellido2_cliente.setVisibility(View.INVISIBLE);
        et_apodo_cliente.setVisibility(View.INVISIBLE);
        et_sexo_cliente.setVisibility(View.INVISIBLE);
        et_direccion_cliente.setVisibility(View.INVISIBLE);
        et_puntuacion_cliente.setVisibility(View.INVISIBLE);
        et_monto_disponible.setVisibility(View.INVISIBLE);
        et_telefono1_cliente.setVisibility(View.INVISIBLE);
        et_telefono2_cliente.setVisibility(View.INVISIBLE);
        bt_guardar_cambios.setVisibility(View.INVISIBLE);
        tv_mensaje.setVisibility(View.VISIBLE);
        bt_editar_credito.setVisibility(View.VISIBLE);
        bt_editar_cliente.setVisibility(View.VISIBLE);
        tv_esperar.setVisibility(View.VISIBLE);
    }

    private void ocultar_todo () {
        tv_ID_cliente.setVisibility(View.VISIBLE);
        tv_nombre_cliente.setVisibility(View.VISIBLE);
        tv_apellido1_cliente.setVisibility(View.VISIBLE);
        tv_apellido2_cliente.setVisibility(View.VISIBLE);
        tv_apodo_cliente.setVisibility(View.VISIBLE);
        tv_sexo_cliente.setVisibility(View.VISIBLE);
        tv_direccion_cliente.setVisibility(View.VISIBLE);
        tv_puntuacion_cliente.setVisibility(View.VISIBLE);
        tv_monto_disponible.setVisibility(View.VISIBLE);
        tv_telefono1_cliente.setVisibility(View.VISIBLE);
        tv_telefono2_cliente.setVisibility(View.VISIBLE);
        et_ID_cliente.setVisibility(View.VISIBLE);
        et_nombre_cliente.setVisibility(View.VISIBLE);
        et_apellido1_cliente.setVisibility(View.VISIBLE);
        et_apellido2_cliente.setVisibility(View.VISIBLE);
        et_apodo_cliente.setVisibility(View.VISIBLE);
        et_sexo_cliente.setVisibility(View.VISIBLE);
        et_direccion_cliente.setVisibility(View.VISIBLE);
        et_puntuacion_cliente.setVisibility(View.VISIBLE);
        et_monto_disponible.setVisibility(View.VISIBLE);
        et_telefono1_cliente.setVisibility(View.VISIBLE);
        et_telefono2_cliente.setVisibility(View.VISIBLE);
        bt_guardar_cambios.setVisibility(View.VISIBLE);
        tv_mensaje.setVisibility(View.INVISIBLE);
        bt_editar_credito.setVisibility(View.INVISIBLE);
        bt_editar_cliente.setVisibility(View.INVISIBLE);
        tv_esperar.setVisibility(View.INVISIBLE);
    }

    private boolean archivo_existe (String[] archivos, String file_name){
        for (int i = 0; i < archivos.length; i++) {
            if (file_name.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }

    private void crear_archivo (String nombre_archivo) {
        try{
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(nombre_archivo, Activity.MODE_PRIVATE));
            archivo.flush();
            archivo.close();
        }catch (IOException e) {
        }
    }

    public  void borrar_archivo (String file) throws IOException {
        File archivo = new File(file);
        String empty_string = "";
        guardar(empty_string, file);
        archivo.delete();
    }

    public  void guardar (String contenido, String file_name) throws IOException {
        try {
            //borrar_archivo(file_name);
            //crear_archivo(file_name);
            OutputStreamWriter archivo = new OutputStreamWriter(openFileOutput(file_name, Activity.MODE_PRIVATE));
            archivo.write(contenido);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
        }
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

    private void text_listener_ID () {

        /*//Implementacion de un text listener
        et_ID_cliente.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (s.length() < 10 | s.length() > 13 ){
                    //msg("Entrada incorrecta!!! s: " + s);
                } else {

                }

            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });*/
    }

    private void msg (String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

}