package com.example.elchino.Util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AgregarLinea {

    private String File;
    private Context ContexT;
    private String Linea;
    private String ArchivoCompleto;//Aqui se lee el contenido de cada archivo guardado.
    private String Cierre;

    public AgregarLinea () {
    }

    public AgregarLinea (String lineaAgregar, String file, Context context) {//AgregarLinea tambien crea el archivo en caso de que este no exista. Favor tomarlo en cuenta en el analisis!
        this.File = file;
        this.ContexT = context;
        this.Linea = lineaAgregar;
        String archivos[] = ContexT.getApplicationContext().fileList();

        if (archivo_existe(archivos)) {
            obtenerContenido();
        } else {
            String archivoCreado = new CrearArchivo(File, ContexT.getApplicationContext()).getFile();
            Log.v("AgregarLinea_0", "AgregarLinea.\n\nResultado de la creacion del archivo:\n\n" + archivoCreado + "\n\n.");
            obtenerContenido();
            return;
        }
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(ContexT.getApplicationContext().openFileOutput(File, Activity.MODE_PRIVATE));
            archivo.write(ArchivoCompleto);
            archivo.flush();
            archivo.close();
        } catch (IOException e) {
        }
    }

    public AgregarLinea (String lineaAgregar, String file, Context context, String cierre) {//Funcion AgregarLinea poliformada para actuar sobre el archivo cierre_cierre_.txt
        this.File = file;
        this.ContexT = context;
        this.Linea = lineaAgregar;
        this.Cierre = cierre;
        StringBuilder contenido = new StringBuilder();
        String archivos[] = ContexT.getApplicationContext().fileList();
        Boolean flagContenido = false;
        if (archivo_existe(archivos)) {
            try {
                InputStreamReader archivo = new InputStreamReader(ContexT.getApplicationContext().openFileInput(File));//Se abre archivo
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();//Se lee archivo
                while (linea != null) {
                    if (linea.contains("estado_archivo")) {
                        String[] splitLinea = linea.split("_separador_");
                        if (splitLinea[1].equals("abajo")) {
                            flagContenido = true;
                        }
                    } else {
                        contenido.append(linea).append("\n");
                    }
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                new BorrarArchivo(File, ContexT.getApplicationContext());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            if (flagContenido) {
                try {
                    contenido.append("estado_archivo_separador_abajo");
                    if (new GuardarArchivo(File, contenido.toString(), ContexT.getApplicationContext()).guardarFile()) {
                        Log.v("AgregarLinea(Polyform)_0", "AgregarLinea.\n\nContenido del archivo:\n\n" + imprimirArchivo() + "\n\n.");
                    } else {
                        Toast.makeText(ContexT.getApplicationContext(), "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                        Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                        Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                        Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            } else {
                Linea = Linea + "\nestado_archivo_separador_abajo";
                try {
                    if (new GuardarArchivo(File, Linea, ContexT.getApplicationContext()).guardarFile()) {
                        Log.v("AgregarLinea(Polyform)_1", "AgregarLinea.\n\nContenido del archivo:\n\n" + imprimirArchivo() + "\n\n.");
                    } else {
                        Toast.makeText(ContexT.getApplicationContext(), "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                        Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                        Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                        Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                    }
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        } else {
            String archivoCreado = new CrearArchivo(File, ContexT.getApplicationContext()).getFile();
            Log.v("AgregarLinea(Polyform)_2", "AgregarLinea.\n\nResultado de la creacion del archivo:\n\n" + archivoCreado + "\n\n.");
            try {
                Log.v("AgregarLinea(Polyform)_3", "AgregarLinea.\n\nFile to add: " + File + "\n\nArchivo completo: " + contenido.toString() + "\n\n.");
                OutputStreamWriter archivo = new OutputStreamWriter(ContexT.getApplicationContext().openFileOutput(File, Activity.MODE_PRIVATE));
                archivo.write(contenido.toString());
                archivo.flush();
                archivo.close();
            } catch (IOException e) {
            }
        }

    }

    private String imprimirArchivo (){
        String[] archivos = ContexT.getApplicationContext().fileList();
        StringBuilder contenido = new StringBuilder();//Aqui se lee el contenido del archivo guardado.
        if (archivo_existe(archivos)) {//Archivo nombre_archivo es el archivo que vamos a imprimir
            try {
                InputStreamReader archivo = new InputStreamReader(ContexT.getApplicationContext().openFileInput(File));//Se abre archivo
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

    private void obtenerContenido () {
        ArchivoCompleto = "";//Se inicializa la variable
        try {
            InputStreamReader archivo = new InputStreamReader(ContexT.getApplicationContext().openFileInput(File));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                ArchivoCompleto = ArchivoCompleto + linea + "\n";
                linea = br.readLine();
            }
            this.ArchivoCompleto = ArchivoCompleto + Linea;
            br.close();
            archivo.close();
        } catch (IOException e) {
        }
    }

    private boolean archivo_existe (String[] archivos){
        for (int i = 0; i < archivos.length; i++) {
            if (File.equals(archivos[i])) {
                return true;
            }
        }
        return false;
    }
    
}
