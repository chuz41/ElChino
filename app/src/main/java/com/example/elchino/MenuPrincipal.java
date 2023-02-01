package com.example.elchino;

import androidx.appcompat.app.AppCompatActivity;
import android.app.ActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;
import com.example.elchino.Util.SubirArchivo;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MenuPrincipal extends AppCompatActivity {

    private String dia;
    private String mes;
    private String anio;
    private String hora;
    private String fecha;
    private String minuto;
    private Button bt_nuevo_cliente;
    private Button bt_estado_cliente;
    private Button bt_cierre;
    //private Button bt_refinanciar;
    //private Button bt_nuevo_credito;
    private Button bt_banca;
    private TextView tv_saludo;
    private TextView tv_fecha;
    private boolean flag_salir = false;
    private String mensaje_recibido = "";
    private TextView tv_caja;
    private String caja = "caja.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag_salir = false;
        setContentView(R.layout.activity_menu_principal);
        bt_nuevo_cliente = (Button) findViewById(R.id.bt_nuevo_cliente);
        bt_estado_cliente = (Button) findViewById(R.id.bt_estado_cliente);
        bt_cierre = (Button) findViewById(R.id.bt_cierre);
        bt_banca = (Button) findViewById(R.id.bt_banca);
        bt_banca.setText("ENTREGAR/RECIBIR FONDOS DE BANCA");
        //bt_refinanciar = (Button) findViewById(R.id.bt_refinanciar);
        //bt_nuevo_credito = (Button) findViewById(R.id.bt_nuevo_credito);
        tv_saludo = (TextView) findViewById(R.id.tv_saludoMenu);
        tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");
        mostrar_caja();
        separarFecha();
        //verArchivos();//debug function!
        try {
            corregirArchivos();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_fecha.setText(dia + "/" + mes + "/" + anio);
        tv_saludo.setText("Menu principal");
        mensaje_recibido = getIntent().getStringExtra( "mensaje");
        boolean flagServicio = false;
        if (mensaje_recibido.equals("null") || (mensaje_recibido == "")) {
            //Do nothing.
        } else {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        ActivityManager manager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (SubirArchivo.class.getName().equals(service.service.getClassName())) {
                flagServicio = true;
            }
        }
        Log.v("onCreate_0", "MenuPrincipal.\n\nflagServicio: " + flagServicio + "\n\n.");
        if (!flagServicio) {
            startService(new Intent(getApplicationContext(), SubirArchivo.class));
        }
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

    private void corregirArchivos () throws IOException {

        //////// ARCHIVO cierre  ////////////////////////////////////////////////////////////
        String archivos[] = fileList();
        boolean flag_borrar = false;
        if (archivo_existe(archivos, "cierre.txt")) {
            try {
                InputStreamReader archivo = new InputStreamReader(openFileInput("cierre.txt"));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                String[] split = linea.split(" ");
                int fecha_file = Integer.parseInt(split[1]);
                int hoy_fecha = Integer.parseInt(dia);
                //Log.v("corregir_archivos_0", "MenuPrincipal.\n\nfecha_file: " + fecha_file + "\nfecha_hoy: " + hoy_fecha + "\n\n");
                if (fecha_file != hoy_fecha) {
                    flag_borrar = true;
                } else {
                    //Do nothing.
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
            }
        } else {
            new AgregarLinea("fecha " + dia, "cierre.txt", getApplicationContext());//La clase AgregarLinea crea el archivo en caso de que este no exista.
        }
        if (flag_borrar) {
            new BorrarArchivo("cierre.txt", getApplicationContext());
            new AgregarLinea("fecha " + dia, "cierre.txt", getApplicationContext());
        }

        /////////////////////////////////////////////////////////////////////////////////////

        //////// ARCHIVO prestamo  //////////////////////////////////////////////////////////


        for (int i = 0; i < archivos.length; i++) {
            String fileContent = "";
            String file = archivos[i];
            Pattern pattern = Pattern.compile("_P_", Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(file);
            boolean flag = true;
            boolean matchFound = matcher.find();
            if (matchFound) {
                try {
                    InputStreamReader archivo = new InputStreamReader(openFileInput(file));
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();

                    while (linea != null) {
                        if (linea.equals("Saldo pendiente: 0 colones")) {
                            flag = true;
                            //Do nothing.
                        } else {
                            fileContent = fileContent + linea + "\n";
                        }
                        linea = br.readLine();
                    }
                    br.close();
                    archivo.close();
                    if (flag) {
                        new BorrarArchivo(file, getApplicationContext());
                        if (new GuardarArchivo(file, fileContent, getApplicationContext()).guardarFile()) {
                        } else {
                            Toast.makeText(this, "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                            Toast.makeText(this, "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                        }
                    }
                } catch (IOException e) {
                }
            } else {
                //Do nothing.
            }
        }
        /////////////////////////////////////////////////////////////////////////////////////

    }

    private void mostrar_caja() {
        tv_caja.setText(imprimir_archivo(caja));
    }

    public void abonar(View view){
        Intent abonar = new Intent(this, AbonarActivity.class);
        abonar.putExtra("msg", "");
        abonar.putExtra("cliente_recivido", "");
        abonar.putExtra("abono_cero", "");
        startActivity(abonar);
        finish();
        System.exit(0);
    }

    public void banca(View view){
        Intent banca = new Intent(this, BancaActivity.class);
        banca.putExtra("msg", "");
        banca.putExtra("cliente_recivido", "");
        startActivity(banca);
        finish();
        System.exit(0);
    }

    public void estado_cliente(View view){
        Intent estado_cliente = new Intent(this, Estado_clienteActivity.class);
        estado_cliente.putExtra("cliente_ID", "");
        startActivity(estado_cliente);
        finish();
        System.exit(0);
    }

    public void registrar_cliente_nuevo(View view){
        Intent registrar_cliente_nuevo = new Intent(this, Registrar_cliente_nuevoActivity.class);
        startActivity(registrar_cliente_nuevo);
        finish();
        System.exit(0);
    }

    public void cierre(View view){
        Intent cierre = new Intent(this, CierreActivity.class);
        startActivity(cierre);
        finish();
        System.exit(0);
    }

    public void refinanciar(View view){
        //Intent refinanciar = new Intent(this, Re_financiarActivity.class);
        //refinanciar.putExtra("msg", "");
        //refinanciar.putExtra("cliente_recivido", "");
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        //startActivity(refinanciar);
        //finish();
        //System.exit(0);
    }

    public void nuevo_credito(View view){
        Intent nuevo_credito = new Intent(this, Nuevo_creditoActivity.class);
        nuevo_credito.putExtra("msg", "");
        nuevo_credito.putExtra("cliente_recivido", "");
        nuevo_credito.putExtra("activity_devolver", "MenuPrincipal");
        startActivity(nuevo_credito);
        finish();
        System.exit(0);
    }

    //Metodos comunes//

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
        msg("Presione atras nuevamente para salir...");
        boton_atras();
    }

    private void boton_atras() {
        if (flag_salir) {
            Log.v("onDestroy_0", "MenuPrincipal.\n\nContext de la aplicacion:\n\n" +
                    getApplicationContext().toString() + "\n\n.");
            stopService (new Intent(getApplicationContext(), SubirArchivo.class));
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            finish();
            System.exit(0);
        } else {
            flag_salir = true;
        }
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

    private void msg(String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}