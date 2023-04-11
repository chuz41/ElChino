package com.example.elchino;

import android.Manifest;
import android.app.ActivityManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.elchino.Util.AgregarLinea;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.SepararFechaYhora;
import com.example.elchino.Util.SubirArchivo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MenuPrincipal extends AppCompatActivity {

    private String dia;
    private String mes;
    private String anio;
    private boolean flag_salir = false;
    private TextView tv_caja;
    private ImageView amarillo;
    private ImageView verde;
    private ImageView rojo;
    ActivityResultLauncher<String[]> sPermissionResultLauncher;
    //private boolean isReadExternalPermissionGranted = false;
    private boolean isManageExternalPermissionGranted = false;
    //private boolean isWriteExternalPermissionGranted = false;
    private boolean isSendSmsPermissionGranted = false;

    @Override
    protected void onCreate (Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flag_salir = false;
        setContentView(R.layout.activity_menu_principal);
        amarillo = (ImageView) findViewById(R.id.imageView);
        verde = (ImageView) findViewById(R.id.imageView2);
        rojo = (ImageView) findViewById(R.id.imageView3);
        amarillo.setVisibility(View.INVISIBLE);
        verde.setVisibility(View.INVISIBLE);
        rojo.setVisibility(View.INVISIBLE);
        Button bt_nuevo_cliente = (Button) findViewById(R.id.bt_nuevo_cliente);
        Button bt_estado_cliente = (Button) findViewById(R.id.bt_estado_cliente);
        Button bt_cierre = (Button) findViewById(R.id.bt_cierre);
        Button bt_gastos = (Button) findViewById(R.id.bt_gastos);
        Button btMorosos = (Button) findViewById(R.id.btMorosos);
        Button btPaganHoy = (Button) findViewById(R.id.btPaganHoy);
        //private Button bt_refinanciar;
        //private Button bt_nuevo_credito;
        Button bt_banca = (Button) findViewById(R.id.bt_banca);
        bt_banca.setText("ENTREGAR/RECIBIR FONDOS DE BANCA");
        //bt_refinanciar = (Button) findViewById(R.id.bt_refinanciar);
        //bt_nuevo_credito = (Button) findViewById(R.id.bt_nuevo_credito);
        TextView tv_saludo = (TextView) findViewById(R.id.tv_saludoMenu);
        TextView tv_fecha = (TextView) findViewById(R.id.tv_fecha);
        tv_caja = (TextView) findViewById(R.id.tv_caja);
        tv_caja.setHint("Caja...");
        mostrarEstado();
        mostrar_caja();
        separarFecha();
        Log.v("onCreate_0", "MenuPrincipal.\n\nSe inicia con la presentacion de los archivos...\n\n.");
        //verArchivos();//debug function!
        try {
            corregirArchivos();
        } catch (IOException e) {
            e.printStackTrace();
        }
        tv_fecha.setText(dia + "/" + mes + "/" + anio);
        tv_saludo.setText("Menu principal");
        String mensaje_recibido = getIntent().getStringExtra("mensaje");
        boolean flagServicio = false;
        if (!(mensaje_recibido.equals("null")) && !(mensaje_recibido == "")) {
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
        sPermissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult (Map<String, Boolean> result) {

                /*if (result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE) != null) {
                    isReadExternalPermissionGranted = result.get(android.Manifest.permission.READ_EXTERNAL_STORAGE);
                }*/

                if (result.get(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE) != null) {
                    isManageExternalPermissionGranted = result.get(android.Manifest.permission.MANAGE_EXTERNAL_STORAGE);
                }

                /*if (result.get(android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != null) {
                    isWriteExternalPermissionGranted = result.get(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                }*/
                
                if (result.get(Manifest.permission.SEND_SMS) != null) {
                    isSendSmsPermissionGranted = result.get(Manifest.permission.SEND_SMS);
                }

                if (isSendSmsPermissionGranted && isManageExternalPermissionGranted) {
                    //Toast.makeText(getApplicationContext(), "Se concedieron los permisos necesarios", Toast.LENGTH_LONG).show();
                } else {
                    //Toast.makeText(getApplicationContext(), "No se concedieron los permisos necesarios!!!", Toast.LENGTH_LONG).show();
                }
            }
        });
        requestPermissions();
    }

    private void requestPermissions () {
        /*isReadExternalPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;*/
        isManageExternalPermissionGranted = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.MANAGE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;
        isSendSmsPermissionGranted = ContextCompat.checkSelfPermission(
                getApplicationContext(),
                Manifest.permission.SEND_SMS
        ) == PackageManager.PERMISSION_GRANTED;
        /*isWriteExternalPermissionGranted = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;*/
        List<String> permissionRequest = new ArrayList<>();
        /*if (!isReadExternalPermissionGranted) {
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        }*/
        if (!isManageExternalPermissionGranted) {
            permissionRequest.add(Manifest.permission.MANAGE_EXTERNAL_STORAGE);
        }
        if (!isSendSmsPermissionGranted) {
            permissionRequest.add(Manifest.permission.SEND_SMS);
        }
        /*if (!isWriteExternalPermissionGranted) {
            permissionRequest.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }*/
        if (!permissionRequest.isEmpty()) {
            sPermissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
        }
    }

    private void verArchivos () {
        String[] files = fileList();
        int cont = 0;
        for (String file : files) {
            Log.v("verArchivos_" + cont, "MenuPrincipal.\n\nfile: " + file + "\n\ncontenido:\n\n" + imprimir_archivo(file) + "\n\n.");
            cont++;
        }
    }

    private void mostrarEstado () {
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput("estado_online.txt"));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            Log.v("mostrarEstado_0", "MenuPrincipar.\n\nlinea: " + linea + "\n\n.");
            if (linea.equals("verde")) {
                verde.setVisibility(View.VISIBLE);
                rojo.setVisibility(View.INVISIBLE);
                amarillo.setVisibility(View.INVISIBLE);
            } else if (linea.equals("amarillo")) {
                verde.setVisibility(View.INVISIBLE);
                rojo.setVisibility(View.INVISIBLE);
                amarillo.setVisibility(View.VISIBLE);
            } else if (linea.equals("rojo")) {
                verde.setVisibility(View.INVISIBLE);
                rojo.setVisibility(View.VISIBLE);
                amarillo.setVisibility(View.INVISIBLE);
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(null);
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        dia = datosFecha.getDia();
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
                if (linea != null) {
                    String[] split = linea.split(" ");
                    int fecha_file = Integer.parseInt(split[1]);
                    int hoy_fecha = Integer.parseInt(dia);
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
            new AgregarLinea("fecha " + dia, "cierre.txt", getApplicationContext());//La clase AgregarLinea crea el archivo en caso de que este no exista.
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }
        if (flag_borrar) {
            new BorrarArchivo("cierre.txt", getApplicationContext());
            new AgregarLinea("fecha " + dia, "cierre.txt", getApplicationContext());
            new BorrarArchivo("cierre_cierre_.txt", getApplicationContext());
            new AgregarLinea("estado_archivo_separador_arriba", "cierre_cierre_.txt", getApplicationContext());
        }
    }

    private void mostrar_caja () {
        String caja = "caja.txt";
        tv_caja.setText(imprimir_archivo(caja));
    }

    public void abonar (View view){
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

    public void gastos (View view){
        Intent gastos = new Intent(this, GastosActivity.class);
        gastos.putExtra("msg", "");
        gastos.putExtra("cliente_recivido", "");
        startActivity(gastos);
        finish();
        System.exit(0);
    }

    public void estado_cliente (View view){
        Intent estado_cliente = new Intent(this, Estado_clienteActivity.class);
        estado_cliente.putExtra("cliente_ID", "");
        startActivity(estado_cliente);
        finish();
        System.exit(0);
    }

    public void paganHoy (View view){
        Intent paganHoy = new Intent(this, Pagan_hoy.class);
        startActivity(paganHoy);
        finish();
        System.exit(0);
    }

    public void quincenas (View view){
        Intent quincenasPagan = new Intent(this, QuincenasActivity.class);
        startActivity(quincenasPagan);
        finish();
        System.exit(0);
    }

    public void meses (View view){
        Intent mesesPagan = new Intent(this, MesesActivity.class);
        startActivity(mesesPagan);
        finish();
        System.exit(0);
    }

    public void registrar_cliente_nuevo (View view){
        Intent registrar_cliente_nuevo = new Intent(this, Registrar_cliente_nuevoActivity.class);
        startActivity(registrar_cliente_nuevo);
        finish();
        System.exit(0);
    }

    public void cierre (View view){
        Intent cierre = new Intent(this, CierreActivity.class);
        startActivity(cierre);
        finish();
        System.exit(0);
    }

    public void refinanciar (View view){
        //Intent refinanciar = new Intent(this, Re_financiarActivity.class);
        //refinanciar.putExtra("msg", "");
        //refinanciar.putExtra("cliente_recivido", "");
        //abonar.putExtra("sid_vendidas", sid_vendidas);
        //startActivity(refinanciar);
        //finish();
        //System.exit(0);
    }

    public void nuevo_credito (View view){
        Intent nuevo_credito = new Intent(this, Nuevo_creditoActivity.class);
        nuevo_credito.putExtra("msg", "");
        nuevo_credito.putExtra("cliente_recivido", "");
        nuevo_credito.putExtra("activity_devolver", "MenuPrincipal");
        startActivity(nuevo_credito);
        finish();
        System.exit(0);
    }

    public void morosos (View view){
        Intent morosos = new Intent(this, MorososActivity.class);
        startActivity(morosos);
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
        msg("Presione atras nuevamente para salir...");
        boton_atras();
    }

    private void boton_atras () {
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
                e.printStackTrace();
            }
        }
        return contenido;
    }

    private void msg (String s) {
        Toast.makeText(this, s, Toast.LENGTH_LONG).show();
    }

}