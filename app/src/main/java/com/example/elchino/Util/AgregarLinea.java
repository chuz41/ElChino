package com.example.elchino.Util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class AgregarLinea {

    private String File;
    private Context ContexT;
    private String Linea;
    private String ArchivoCompleto;//Aqui se lee el contenido de cada archivo guardado.

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
