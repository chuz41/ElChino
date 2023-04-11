package com.example.elchino.Util;

import android.content.Context;
import android.util.Log;
import com.example.elchino.Clases_comunes.Cliente;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class GuardarArchivo {

    private String File;
    private Cliente ClientE;
    private String Content;
    private Context ContexT;
    private String Estado;

    public GuardarArchivo () {
    }

    public GuardarArchivo(Cliente cliente, String file, String estado, Context context) {
        this.ClientE = cliente;
        this.File = file;
        this.ContexT = context;
        this.Estado = estado;
    }

    public GuardarArchivo(String file, String content, Context context) {
        this.Content = content;
        this.File = file;
        this.ContexT = context;
    }

    public boolean guardarCliente () {
        String contenido = "";
        contenido = "ID_cliente_separador_" + ClientE.getId() + "\nnombre_cliente_separador_" + ClientE.getNombre() +
                "\napellido1_cliente_separador_" + ClientE.getApellido1() + "\napellido2_cliente_separador_" +
                ClientE.getApellido2() + "\napodo_cliente_separador_" + ClientE.getApodo() + "\ntelefono1_cliente_separador_" +
                ClientE.getTelefono1() + "\ntelefono2_cliente_separador_" + ClientE.getTelefono2() + "\nnotas_cliente_separador_" +
                ClientE.getNotas() + "\ndireccion_cliente_separador_" + ClientE.getDireccion() + "\nmonto_disponible_separador_" +
                ClientE.getMontoAprobado() + "\npuntuacion_cliente_separador_" + ClientE.getPuntuacion() + "\nestado_archivo_separador_" + Estado;
        //Log.v("guardarCliente_0", "GuardarArchivo.\n\nContenido:\n\n" + contenido + "\n\n.");
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(ContexT.getApplicationContext().openFileOutput(File, ContexT.getApplicationContext().MODE_PRIVATE));
            archivo.write(contenido);
            archivo.flush();
            archivo.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    public boolean guardarFile () throws IOException {
        //Log.v("guardarFile_0", "GuardarArchivo.\n\nContenido:\n\n" + Content + "\n\n.");
        try {
            OutputStreamWriter archivo = new OutputStreamWriter(ContexT.getApplicationContext().openFileOutput(File, ContexT.getApplicationContext().MODE_PRIVATE));
            archivo.write(Content);
            archivo.flush();
            archivo.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }

}
