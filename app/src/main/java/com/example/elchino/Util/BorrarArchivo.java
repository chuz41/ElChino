package com.example.elchino.Util;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

public class BorrarArchivo {

    private Context ContexT;
    private String File;

    public BorrarArchivo () {
    }

    public BorrarArchivo (String file, Context context) throws IOException {

        this.ContexT = context;
        this.File = file;
        java.io.File archivo = new File(file);
        String empty_string = "";
        if (new GuardarArchivo(File, empty_string, ContexT).guardarFile()) {
            Log.v("BorrarArchivo_0", "BorrarArchivo.\n\nArchivo creado: " + File + "\n\n.");
        } else {
            Toast.makeText(ContexT.getApplicationContext(), "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
            Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
            Toast.makeText(ContexT.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
        }
        archivo.delete();
    }

}
