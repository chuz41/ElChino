package com.example.elchino.Util;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;
import androidx.annotation.Nullable;
import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class SubirArchivo extends Service {

    private Context thisContext = this;
    private boolean flaG = false;
    private String cobrador = "a_sfile_cobrador_sfile_a.txt";
    private String addRowURL = "https://script.google.com/macros/s/AKfycbweyYb-DHVgyEdCWpKoTmvOxDGXleawjAN8Uw9AeJYbZ24t9arB/exec";
    private MyThread myThread = new MyThread();

    @Override
    public void onCreate () {
    }

    @Override
    public int onStartCommand (Intent intent, int flag, int idProcess) {

        //Para llamar a la clase:
        //startService (new Intent(this, SubirArchivo.class));
        Log.v("onStartCommand_0", "SubirArchivo.\n\nflag: " + flaG + "\n\n.");
        myThread.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy () {
        //myThread.stop();
        //Detener el servicio. Es requerido, sino nunca se detiene.
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class MyThread extends Thread {

        private String sheet;

        @Override
        public void run() {
            try {
                cargarArchivos();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        private void cargarArchivos () throws JSONException {
            String archivos[] = fileList();
            boolean matarFor = false;
            for (int i = 0; i < archivos.length; i++) {
                try {
                    InputStreamReader archivo = new InputStreamReader(thisContext.getApplicationContext().openFileInput(archivos[i]));//Se abre archivo
                    BufferedReader br = new BufferedReader(archivo);
                    String linea = br.readLine();//Se lee archivo
                    while (linea != null) {
                        if (linea.contains("estado_archivo_separador_abajo") || linea.equals("estado_archivo_separador_abajo")) {
                            Log.v("cargarArchivos_0", "SubirArchivo.\n\nLinea encontrada!\n\nLinea: " + linea + "\n\nArchivo: " + archivos[i] + "\n\nContenido del archivo:\n\n" + imprimirArchivo(archivos[i]) + "\n\n.");
                            matarFor = true;
                            if (archivos[i].contains("_caja_")) {
                                sheet = "caja";
                            } else if (archivos[i].contains("_P_")) {
                                if (esAbono(archivos[i])) {
                                    sheet = "abonos";
                                } else {
                                    sheet = "creditos";
                                }
                            } else if (archivos[i].contains("_C_")) {
                                sheet = "clientes";
                            } else if (archivos[i].contains("_S_")) {
                                sheet = "solicitudes";
                            } else {
                                sheet = "error";
                            }
                        }
                        linea = br.readLine();
                    }
                    br.close();
                    archivo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (matarFor) {
                    subirArchivo(archivos[i]);
                    break;
                }
            }
        }

        private boolean esAbono (String file) {
            boolean flag = false;
            try {
                InputStreamReader archivo = new InputStreamReader(thisContext.getApplicationContext().openFileInput(file));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null && !linea.isEmpty()) {
                    String[] split = linea.split("_separador_");
                    if (split[0].equals("monto_abono")) {
                        Log.v("esAbono_0", "SubirArchivo.\n\nlinea: " + linea + "\n\n.");
                        if (Integer.parseInt(split[1]) > 0) {
                            flag = true;
                        }
                    }
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return flag;
        }

        private void subirArchivo (String file) throws JSONException {

            String spreadSheet = null;
            try {
                Log.v("SubirArchivo_-1", "SubirArchivo.\n\nFile: " + cobrador + "\n\ncontenido:\n\n" + imprimirArchivo(cobrador) + "\n\n.");
                InputStreamReader archivo = new InputStreamReader(thisContext.getApplicationContext().openFileInput(cobrador));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                Log.v("SubirArchivo_-0", "SubirArchivo.\n\nFile: " + file + "\n\nlinea: " + linea + "\n\n.");
                while (linea != null) {
                    String[] split = linea.split(" ");
                    if (sheet.equals("clientes")) {
                        if (split[0].equals("Sclientes")) {
                            spreadSheet = split[1];
                        }
                    } else if (sheet.equals("creditos") | sheet.equals("abonos")) {
                        if (split[0].equals("Screditos")) {
                            spreadSheet = split[1];
                        }
                    } else if (sheet.equals("solicitudes")) {
                        if (split[0].equals("Screditos")) {
                            spreadSheet = split[1];
                        }
                    } else if (sheet.equals("caja")) {
                        if (split[0].equals("Screditos")) {
                            spreadSheet = split[1];
                        }
                    }
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            String spid = spreadSheet;
            String json_string = "";
            Log.v("subirArchivo_1", "SubirArchivo.\n\nSpreadSheetId: " + spid + "\n\nSheet: " + sheet + "\n\nfile: " + file + "\n\n.");
            try {
                InputStreamReader archivo = new InputStreamReader(thisContext.getApplicationContext().openFileInput(file));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null && !linea.isEmpty()) {
                    String[] split = linea.split("_separador_");
                    json_string = json_string + split[1] + "_n_";
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.v("subirArchivo_4", "SubirArchivo.\n\njsonString:\n" + json_string + "\n\n.");
            JSONObject jsonObject = TranslateUtil.string_to_Json(json_string, spid, sheet);
            Log.v("subirArchivo_5", "SubirArchivo.\n\njsonObject.toString():\n\n" + jsonObject + "\n\n.");
            subirNuevo(jsonObject, file);
        }

        private void subirNuevo (JSONObject jsonObject, String file) {

            if (verificar_internet()) {
                Log.v("subirNuevo_0", "SubirArchivo.\n\nfile: " + file + "\nContenido:\n\n" + imprimirArchivo(file) + "\n\n.");
                RequestQueue queue;
                queue = Volley.newRequestQueue(thisContext.getApplicationContext());
                //Llamada POST usando Volley:
                RequestQueue requestQueue;

                // Instantiate the cache
                Cache cache = new DiskBasedCache(getCacheDir(), 1024 * 1024); // 1MB cap

                // Set up the network to use HttpURLConnection as the HTTP client.
                Network network = new BasicNetwork(new HurlStack());

                // Instantiate the RequestQueue with the cache and network.
                requestQueue = new RequestQueue(cache, network);

                // Start the queue
                requestQueue.start();

                String url = addRowURL;

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.POST, url, jsonObject, new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                String[] split = response.toString().split("\"");
                                int length_split = split.length;
                                Log.v("subirNuevo_1", "SubirArchivo.\n\nResponse:\n\n" + response + "\n\n.");
                                if (length_split > 3) {//TODO: Corregir este if. Debe ser mas especifico y detectar si la respuesta no es correcta.
                                    for (int i = 0; i < length_split; i++) {
                                        Log.v("split[" + i + "]", split[i]);
                                    }
                                    if (split[2].equals(":")) {//TODO: Todo de arriba tiene que ver tambien con este.
                                        cambiarBandera(file);
                                    }
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                            }
                        });
                requestQueue.add(jsonObjectRequest);// Add the request to the RequestQueue.
            } else {//No hay internet!!!
                Log.v("SubirNuevo_2","SubirArchivo.\n\nPara registrar al vendedor en el servidor, debe estar conectado a internet.\n\n.");
                esperar(1);
            }
        }

        private String imprimirArchivo (String file_name){
            String archivos[] = fileList();
            String contenido = "";//Aqui se lee el contenido del archivo guardado.
            if (archivo_existe(archivos, file_name)) {//Archivo nombre_archivo es el archivo que vamos a imprimir
                try {
                    InputStreamReader archivo = new InputStreamReader(thisContext.getApplicationContext().openFileInput(file_name));//Se abre archivo
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

        private boolean archivo_existe (String[] archivos, String file_name){
            for (int i = 0; i < archivos.length; i++) {
                if (file_name.equals(archivos[i])) {
                    return true;
                }
            }
            return false;
        }

        private void cambiarBandera (String file) {
            String contenido = "";
            try {
                InputStreamReader archivo = new InputStreamReader(thisContext.getApplicationContext().openFileInput(file));
                BufferedReader br = new BufferedReader(archivo);
                String linea = br.readLine();
                while (linea != null) {
                    if (linea.contains("estado_archivo_separador_abajo") || linea.equals("estado_archivo_separador_abajo")) {
                        Log.v("cambiarBandera_0", "SubirArchivo.\n\nLinea: " + linea + "\n\n.");
                        linea = "estado_archivo_separador_arriba";
                        contenido = contenido + linea + "\n";
                    } else {
                        contenido = contenido + linea + "\n";
                    }
                    linea = br.readLine();
                }
                br.close();
                archivo.close();
                new BorrarArchivo(file, thisContext.getApplicationContext());
                new GuardarArchivo(contenido, file, thisContext);
                if (new GuardarArchivo(file, contenido, getApplicationContext()).guardarFile()) {
                    Log.v("cambiar_bandera_0", "SubirArchivo.\n\nArchivo: " + file + "\n\n.");
                } else {
                    Toast.makeText(thisContext.getApplicationContext(), "*** ERROR al crear el archivo. ***", Toast.LENGTH_LONG).show();
                    Toast.makeText(thisContext.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                    Toast.makeText(thisContext.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                    Toast.makeText(thisContext.getApplicationContext(), "Informe a soporte tecnico!", Toast.LENGTH_LONG).show();
                }
                Log.v("cambiarBandera_1", "SubirArchivo\n\nArchivo: " + file + "\n\nContenido del archivo: \n\n" + imprimirArchivo(file) + "\n\n.");
                try {
                    cargarArchivos();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void esperar (int tiempoMinutos) {
            for (int i = 0; i < 60; i++) {
                for (int o = 0; o < tiempoMinutos; o++) {
                    try {
                        myThread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            try {
                cargarArchivos();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private boolean verificar_internet () {
        ConnectivityManager cm = (ConnectivityManager)this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnectedOrConnecting();
        if (!isConnected) {
            //No hay internet
            return false;
        } else {
            //Si esta conectado a internet.
            return true;
        }
    }

}