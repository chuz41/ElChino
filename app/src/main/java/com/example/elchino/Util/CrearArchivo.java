package com.example.elchino.Util;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class CrearArchivo extends AppCompatActivity {

    private String File;
    private Context ContexT;

    public CrearArchivo () {
    }

    public CrearArchivo(String file, Context context) {
        this.File = file;
        this.ContexT = context;
        creacion();
    }

    private void creacion () {
        try{
            Log.v("creacion_0", "CrearArchivo.\n\nArchivo: " + File + "\n\n.");
            OutputStreamWriter archivo = new OutputStreamWriter(ContexT.getApplicationContext().openFileOutput(File, ContexT.getApplicationContext().MODE_PRIVATE));
            archivo.flush();
            archivo.close();
            File = "Archivo " + File + " creado exitosamente!";
        }catch (IOException e) {
            File = "***********ERROR***********\nArchivo " + File + " NO SE PUDO CREAR!!!";
        }
    }

    public String getFile () {
        return File;
    }

}
