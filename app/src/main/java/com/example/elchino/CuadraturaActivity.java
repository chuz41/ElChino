package com.example.elchino;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
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
import com.example.elchino.Util.BluetoothUtil;
import com.example.elchino.Util.BorrarArchivo;
import com.example.elchino.Util.DateUtilities;
import com.example.elchino.Util.GuardarArchivo;
import com.example.elchino.Util.SepararFechaYhora;
import org.json.JSONException;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CuadraturaActivity extends AppCompatActivity {

    private String abonar = "";
    private Button whatsapp;
    private TextView tvWhatsApp;
    private Integer intereses_monroe = 0;
    private String imprimir_intermedio = "";
    private Button bt_imprimir;
    private Integer monto_cuota = 0;
    private String monto_prestado_final = "0";
    private Integer saldo_mas_intereses = 0;
    private String morosidad = "D";
    private String interes_mora_parcial;
    private String archivo_prestamo = "";
    private String plazo = "";
    private EditText et_ID;
    private TextView tv_esperar;
    private String activity_devolver;
    private String dia;
    private String mes;
    private String anio;
    private Button bt_consultar;
    private String cliente_ID = "";
    private TextView tv_saludo;
    private boolean flag_client_reciv = false;
    private String cliente_recibido = "";
    private final String interes_mora = "1";
    private String interes_mora_total = "0";
    private String puntuacion_cliente = "";
    private String cuadratura = "";
    private Spinner sp_plazos;
    private Button sequi1;
    private Button sequi2;
    private Button sequi3;
    private Button sequi4;
    private Button sequi5;
    private Button sequi6;
    private Button sequi7;
    private Button sequi8;
    private Button sequi9;
    private Button sequi10;
    private Button sequi11;
    private Button sequi12;
    private Button sequi13;
    private Button sequi14;
    private Button sequi15;
    private Button sequi16;
    private Button sequi17;
    private Button sequi18;
    private Button sequi19;
    private Button sequi20;
    private Button sequi21;
    private String cliente_Id_volver;
    private String mensaje_imprimir = "";
    private String mensaje_imprimir_pre = "";
    private String cobrador_s = "";
    private String telefono_s = "";
    private String telefono = "";
    private Date hoy_LD;
    private String fecha_hoy_string;
    private String nombreCliente;
    private final String globalVar = "globalVar_globalVar_.txt";
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuadratura);
        String mensaje_recibido = getIntent().getStringExtra( "msg");
        abonar = getIntent().getStringExtra("abonar");
        telefono = getIntent().getStringExtra("telefono");
        cuadratura = getIntent().getStringExtra( "cuadratura");
        nombreCliente = getIntent().getStringExtra("nombreCliente");
        String cambio = getIntent().getStringExtra("cambio");
        activity_devolver = getIntent().getStringExtra("activity_devolver");
        String monto_creditito = getIntent().getStringExtra("monto_creditito");
        mensaje_imprimir_pre = getIntent().getStringExtra("mensaje_imprimir_pre");
        TextView tv_cambio = findViewById(R.id.tv_cambio);
        bt_imprimir = findViewById(R.id.bt_imprimir);
        bt_imprimir.setVisibility(View.INVISIBLE);
        whatsapp = findViewById(R.id.whatsapp);
        whatsapp.setVisibility(View.INVISIBLE);
        tvWhatsApp = findViewById(R.id.tvWhatsApp);
        tvWhatsApp.setVisibility(View.INVISIBLE);
        tv_cambio.setVisibility(View.INVISIBLE);
        int cambio_int = Integer.parseInt(cambio);
        if (cambio_int > 0) {
            String string = "Cambio:\n\n" + cambio;
            tv_cambio.setText(string);
            tv_cambio.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Devolver " + cambio + " colones sobrantes.", Toast.LENGTH_LONG).show();
        }
        if (Integer.parseInt(monto_creditito) > 0) {
            String montoIngresado_s = String.valueOf(monto_creditito);
            char[] chars = montoIngresado_s.toCharArray();
            Log.v("debug1", "\n\nmonto_creditito: " + monto_creditito + "\n\nchars.length: " + chars.length + " \n\n.");
            for (char c : chars) {
                Log.v("forDebug_1", "char: " + c);
            }
            if (chars.length == 1) {
                montoIngresado_s = String.valueOf(chars[0]) + ",00";
            } else if (chars.length == 2) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
            } else if (chars.length == 3) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
            } else if (chars.length == 4) {
                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
            } else if (chars.length == 5) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
            } else if (chars.length == 6) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
            } else if (chars.length == 7) {
                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
            }
            Log.v("debug1", "\n\nmontoIngresado_s: " + montoIngresado_s + "\n\n.");
            String string = "***** Prestamo  aprobado *****.\n\nMonto del credito:\n" + montoIngresado_s + " colones.";
            tv_cambio.setText(string);
            imprimir_intermedio = "***** Prestamo  aprobado *****.\n\nMonto del credito:\n" + montoIngresado_s + " colones.\n";
            tv_cambio.setVisibility(View.VISIBLE);
            Toast.makeText(this, "Entregar " + montoIngresado_s + " colones al cliente.", Toast.LENGTH_LONG).show();
        }
        et_ID = findViewById(R.id.et_ID);
        sp_plazos = findViewById(R.id.sp_plazos);
        sp_plazos.setVisibility(View.INVISIBLE);
        bt_consultar = findViewById(R.id.bt_consultar_ab);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        if (!mensaje_recibido.equals("")) {
            Toast.makeText(this, mensaje_recibido, Toast.LENGTH_LONG).show();
        }
        cliente_recibido = getIntent().getStringExtra( "cliente_recivido");
        Log.v("onCreate0", "Cuadratura.\n\nCliente recibido: " + cliente_recibido + "\n\n.");
        cliente_ID = cliente_recibido;
        cliente_Id_volver = cliente_recibido;
        tv_esperar = findViewById(R.id.tv_esperar);
        sequi1 = findViewById(R.id.sequi1);
        sequi2 = findViewById(R.id.sequi2);
        sequi3 = findViewById(R.id.sequi3);
        sequi4 = findViewById(R.id.sequi4);
        sequi5 = findViewById(R.id.sequi5);
        sequi6 = findViewById(R.id.sequi6);
        sequi7 = findViewById(R.id.sequi7);
        sequi8 = findViewById(R.id.sequi8);
        sequi9 = findViewById(R.id.sequi9);
        sequi10 = findViewById(R.id.sequi10);
        sequi11 = findViewById(R.id.sequi11);
        sequi12 = findViewById(R.id.sequi12);
        sequi13 = findViewById(R.id.sequi13);
        sequi14 = findViewById(R.id.sequi14);
        sequi15 = findViewById(R.id.sequi15);
        sequi16 = findViewById(R.id.sequi16);
        sequi17 = findViewById(R.id.sequi17);
        sequi18 = findViewById(R.id.sequi18);
        sequi19 = findViewById(R.id.sequi19);
        sequi20 = findViewById(R.id.sequi20);
        sequi21 = findViewById(R.id.sequi21);
        sequi1.setVisibility(View.INVISIBLE);
        sequi2.setVisibility(View.INVISIBLE);
        sequi3.setVisibility(View.INVISIBLE);
        sequi4.setVisibility(View.INVISIBLE);
        sequi5.setVisibility(View.INVISIBLE);
        sequi6.setVisibility(View.INVISIBLE);
        sequi7.setVisibility(View.INVISIBLE);
        sequi8.setVisibility(View.INVISIBLE);
        sequi9.setVisibility(View.INVISIBLE);
        sequi10.setVisibility(View.INVISIBLE);
        sequi11.setVisibility(View.INVISIBLE);
        sequi12.setVisibility(View.INVISIBLE);
        sequi13.setVisibility(View.INVISIBLE);
        sequi14.setVisibility(View.INVISIBLE);
        sequi15.setVisibility(View.INVISIBLE);
        sequi16.setVisibility(View.INVISIBLE);
        sequi17.setVisibility(View.INVISIBLE);
        sequi18.setVisibility(View.INVISIBLE);
        sequi19.setVisibility(View.INVISIBLE);
        sequi20.setVisibility(View.INVISIBLE);
        sequi21.setVisibility(View.INVISIBLE);
        tv_saludo = findViewById(R.id.tv_saludo);
        String string = "ESTADO DE CUENTA\nCliente: " + nombreCliente + "\nID: " + cliente_recibido;
        tv_saludo.setText(string);
        hoy_LD = Calendar.getInstance().getTime();
        Log.v("onCreate1", "Cuadratura.\n\nFecha hoy: " + hoy_LD + "\n\n.");
        Log.v("onCreate2", "Cuadratura.\n\nFecha hoy string pre: " + fecha_hoy_string + "\n\n.");
        Log.v("onCreate3", "Cuadratura.\n\nFecha hoy: " + hoy_LD.toString() + "\n\n.");
        fecha_hoy_string = DateUtilities.dateToString(hoy_LD);
        Log.v("onCreate4", "Cuadratura.\n\nFecha hoy string post: " + fecha_hoy_string + "\n\n.");
        try {
            hoy_LD = DateUtilities.stringToDate(fecha_hoy_string);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        String[] split_fecha_hoy_string = fecha_hoy_string.split("-");
        fecha_hoy_string = split_fecha_hoy_string[2] + "/" + split_fecha_hoy_string[1] + "/" + split_fecha_hoy_string[0];
        separarFecha();
        datos_cobrador();
        if (!cuadratura.equals("null")) {
            try {
                presentar_cuadratura();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        } else {
            if (!cliente_recibido.equals("")) {
                flag_client_reciv = true;
                cliente_ID = cliente_recibido;
                Log.v("onCreate5", "Cuadratura.\n\nCliente ID: " + cliente_ID + "\n\n.");
                try {
                    consultar(null);
                } catch (JSONException | InterruptedException | IOException e) {
                    e.printStackTrace();
                }
            }
            text_listener();
        }
    }

    //Send an image to a phone number trhow whatsapp.
    public void sendImage (String path) {

        //Abrir el archivo "globalVar_globalVar_.txt", revisar su contenido, que debe ser una sola linea, y guardarlo en la variable valueglovalVar.
        String valueGlovalVar = "";
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(globalVar));//Se abre archivo
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();//Se lee archivo
            while (linea != null) {
                valueGlovalVar = linea;
                Log.v("sendImage", "Cuadratura.\n\nLinea: " + linea + "\n\n.");
                if (linea.equals("imagen") || linea.equals("texto") || linea.equals("error")) {
                    break;
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            valueGlovalVar = "error";
            e.printStackTrace();
        }

        //Si la variable global es "imagen", enviar la imagen por whatsapp. Si no, enviar el texto por whatsapp.

        if (valueGlovalVar.equals("texto")) {
            Intent sendIntent0 = new Intent();
            sendIntent0.setAction(Intent.ACTION_VIEW);
            String uri0 = "whatsapp://send?phone=" + telefono + "&text=" + "Hola " + nombreCliente + "\n\n" + mensaje_imprimir;
            sendIntent0.setData(Uri.parse(uri0));
            try {
                new BorrarArchivo(globalVar, this.getApplicationContext());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                new GuardarArchivo(globalVar, "imagen", this.getApplicationContext()).guardarFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            try {
                startActivity(sendIntent0);
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
            }
        } else if (valueGlovalVar.equals("imagen")) {
            Log.v("sendImage_1", "Cuadratura.\n\nGlobal variable: " + valueGlovalVar + "\n\n.");//Debe ser imagen.
            //Make a wait pause in the current thread
            esperar(path);
        } else {
            Log.v("sendImage_2", "Cuadratura.\n\nGlobal variable: " + valueGlovalVar + "\n\n.");//Debe ser error.
        }
    }

    public void sendImage2 (String path) {

        Intent sendIntent0 = new Intent();
        sendIntent0.setAction(Intent.ACTION_VIEW);
        String uri0 = "whatsapp://send?text=" + "\n" + mensaje_imprimir;
        sendIntent0.setData(Uri.parse(uri0));
        try {
            new BorrarArchivo(globalVar, this.getApplicationContext());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            new GuardarArchivo(globalVar, "imagen", this.getApplicationContext()).guardarFile();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try {
            startActivity(sendIntent0);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }

        /*Intent sendIntent = new Intent(Intent.ACTION_SEND);

        sendIntent.setPackage("com.whatsapp");

        sendIntent.putExtra(Intent.EXTRA_TEXT, "Estado de cuenta.");
        Log.v("sendImage_0", "Cuadratura.\n\nPath: " + path + "\n\n.");
        //String uri = "whatsapp://send?phone=" + telefono + "&text=" + "Hola " + nombreCliente + ", te envio tu estado de cuenta.";
        //String uri = "whatsapp://send?phone=" + telefono + "&png=" + path;
        //sendIntent.setData(Uri.parse(uri));
        Uri uri = Uri.parse(path);
        sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
        sendIntent.setType("image/*");
        try {
            startActivity(sendIntent);
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(this, "Whatsapp have not been installed.", Toast.LENGTH_SHORT).show();
        }*/


        //sendIntent.setData(Uri.parse(uri));


        /*sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola " + nombreCliente + ", te envio tu estado de cuenta.");
        sendIntent.putExtra("jid", telefono + "@s.whatsapp.net");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse(path));
        sendIntent.setType("image/png");
        sendIntent.setPackage("com.whatsapp");*/
        //startActivity(sendIntent);
    }

    //Create an image that contains a text messege. The image is saved in the external storage and the path of the image is returned.
    private void createImage () {
        Resources resources = this.getApplicationContext().getResources();
        float scale = resources.getDisplayMetrics().density;
        String[] split_mensaje_imprimir = mensaje_imprimir.split("\n");
        int longTiquet = split_mensaje_imprimir.length;
        Bitmap bitmap = Bitmap.createBitmap((int) (150 * scale), (int) (longTiquet * 13 * scale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.WHITE);
        Paint paint = new Paint();
        paint.setColor(Color.DKGRAY);
        paint.setTextSize(24);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        Rect bounds = new Rect();
        paint.getTextBounds(mensaje_imprimir, 0, mensaje_imprimir.length(), bounds);
        int x = 10;
        int y = 35;
        for (int i = 0; i < split_mensaje_imprimir.length; i++) {
            canvas.drawText(split_mensaje_imprimir[i], x, y, paint);
            y += paint.descent() - paint.ascent();
        }
        String path = Environment.getExternalStorageDirectory().toString();
        Toast.makeText(this, "Path: " + path, Toast.LENGTH_LONG).show();
        path = path + "/ElChino/";
        OutputStream fOut = null;
        Integer counter = 0;
        File file = new File(path, "image" + counter.toString() + ".png");
        file.getParentFile().mkdirs();
        while (file.exists() && !file.isDirectory()) {
            counter++;
            file = new File(path, "image" + counter.toString() + ".png");
        }
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 75, fOut);
            bitmap.recycle();
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(getApplicationContext(), "Path: " + path, Toast.LENGTH_LONG).show();
        Log.v("whatsapp_0", "Cuadratura.\n\nfile Absolute Path:\n" + file.getAbsolutePath() + "\n\n.");
        sendImage(file.getAbsolutePath());
    }

    private void esperar (String path) {
        ocultar_todito();
        //Toast.makeText(this, "Cliente no posee creditos activos!", Toast.LENGTH_LONG).show();
        //String string = "Cliente no posee creditos activos!";
        //tv_esperar.setText(string);
        for (int i = 0; i < 1; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        sendImage2(path);
    }



   /* public void sendImage(String path) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, "Hola");
        sendIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file://" + path));
        sendIntent.setType("image/png");
        sendIntent.setPackage("com.whatsapp");
        //startActivity(sendIntent);
    }

    //Send a text messege to whatsapp.
    public void sendText(String text) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }

    //Send a text messege to whatsapp.
    public void sendText(String text, String number) {
        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, text);
        sendIntent.putExtra(Intent.EXTRA_PHONE_NUMBER, number);
        sendIntent.setType("text/plain");
        sendIntent.setPackage("com.whatsapp");
        startActivity(sendIntent);
    }*/

    //create an image, put a text messege contained in a string into the image, save the image in the external storage and return the path of the image.
    public void whatsapp (View view) {
        createImage();
        /*Resources resources = this.getApplicationContext().getResources();
        float scale = resources.getDisplayMetrics().density;
        Bitmap bitmap = Bitmap.createBitmap((int) (300 * scale), (int) (300 * scale), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        bitmap.eraseColor(Color.BLACK);
        Paint paint = new Paint();
        paint.setColor(Color.YELLOW);
        paint.setTextSize(14 * scale);
        paint.setAntiAlias(true);
        paint.setFakeBoldText(true);
        Rect bounds = new Rect();
        paint.getTextBounds(stringHola, 0, stringHola.length(), bounds);
        int x = (bitmap.getWidth() - bounds.width()) / 2;
        int y = (bitmap.getHeight() + bounds.height()) / 2;
        canvas.drawText(stringHola, x * scale, y * scale, paint);
        String path = Environment.getExternalStorageDirectory().toString();
        path = path + "/Download/ElChino/";
        OutputStream fOut = null;
        Integer counter = 0;
        File file = new File(path, "image" + counter.toString() + ".jpg");
        file.getParentFile().mkdirs();
        while (file.exists() && !file.isDirectory()) {
            counter++;
            file = new File(path, "image" + counter.toString() + ".jpg");
        }
        try {
            file.createNewFile();
            fOut = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.v("whatsapp_0", "Cuadratura.\n\nfile Absolute Path:\n" + file.getAbsolutePath() + "\n\n.");*/
    }


//Log.v("whatsapp_0", "Cuadratura.\n\nfile Absolute Path:\n" + file.getAbsolutePath() + "\n\n.");

    private void separarFecha () {
        SepararFechaYhora datosFecha = new SepararFechaYhora(hoy_LD);
        anio = datosFecha.getAnio();
        mes = datosFecha.getMes();
        dia = datosFecha.getDia();
    }

    private String obtener_cuotas_morosas (String cuadratura) throws ParseException {
        String[] split1 = cuadratura.split("__");
        String fecha_next_abono_bkUp = fecha_hoy_string;
        Log.v("obt_cuot_moro0", "Abonar.\n\ncuadratura: " + cuadratura + "\n\nfecha_next_abonobkUp: " + fecha_next_abono_bkUp + "\n\n.");
        int length_split1 = split1.length;
        int cont = 0;
        for (int i = 0; i < length_split1; i++) {
            fecha_next_abono_bkUp = fecha_hoy_string;
            String[] split = split1[i].split("_");
            Log.v("obt_cuot_moro1", "Abonar.\n\nsplit1[" + i + "]: " + split1[i] + "\n\n.");
            String fecha_cuadra_S = split[3];
            String[] split_fecha_cuadra_S = fecha_cuadra_S.split("/");
            fecha_cuadra_S = split_fecha_cuadra_S[2] + "-" + split_fecha_cuadra_S[1] + "-" + split_fecha_cuadra_S[0];
            int monto = Integer.parseInt(split[2]);
            Log.v("obt_cuout_moro2", "Abonar.\n\nMonto: " + monto + "\n\n.");
            if (monto > 100) {
                Date fecha_cuadra = DateUtilities.stringToDate(fecha_cuadra_S);
                Log.v("obt_cuout_moro3", "Abonar.\n\nfecha_cuadra: " + fecha_cuadra + "\n\n.");
                String[] split_fecha_next_abono_bkUp = fecha_next_abono_bkUp.split("/");
                Log.v("obt_cuout_moro4", "Abonar.\n\nfecha_next_abono_pre: " + fecha_next_abono_bkUp + "\n\n.");
                int length_split = split_fecha_next_abono_bkUp.length;
                Log.v("obt_cuout_moro5", "Abonar.\n\nlength_split: " + length_split + "\n\n.");
                fecha_next_abono_bkUp = split_fecha_next_abono_bkUp[2] + "-" + split_fecha_next_abono_bkUp[1] + "-" + split_fecha_next_abono_bkUp[0];
                Log.v("obt_cuout_moro6", "Abonar.\n\nfecha_next_abono_post: " + fecha_next_abono_bkUp + "\n\n.");
                Date fecha_next_abono_bkUp_D = DateUtilities.stringToDate(fecha_next_abono_bkUp);
                Log.v("obt_cuout_moro7", "Abonar.\n\nfecha_next_abono_D: " + fecha_next_abono_bkUp_D + "\n\n.");
                int dias_atrasados = DateUtilities.daysBetween(fecha_next_abono_bkUp_D, fecha_cuadra);//Positivo indica morosidad
                Log.v("obt_cuout_moro8", "Abonar.\n\ndias_atrasados: " + dias_atrasados + "\n\n.");
                if (dias_atrasados > 0) {
                    morosidad = "M";
                    cont++;
                }
            }
        }
        int cantidad_de_cuotas_pendientes = cont;
        puntuacion_cliente = String.valueOf(Integer.parseInt(puntuacion_cliente) - cantidad_de_cuotas_pendientes);
        Log.v("obt_cuout_moro9", "Abonar.\n\nCantidad de cuotas pendientes: " + cantidad_de_cuotas_pendientes + "\n\nCantidad de cuotas pendientes: " +
                cantidad_de_cuotas_pendientes + "\n\n.");
        return String.valueOf(cantidad_de_cuotas_pendientes);
    }

    private void llenar_spinner () {
        //Plazos y tasas: 5semanas (20%), 6semanas (20%), 9semanas (40%), 3quincenas (25%), 5quincenas (40%)
        StringBuilder creditos = new StringBuilder("Escoja el credito...___");
        String[] archivos = fileList();
        Log.v("llenando_spinner0", "Cuadratura.\n\nCantidad de archivos: " + archivos.length + "\n\n.");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("llenando_spinner1", "****ERROR*********Cuadratura.\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
            for (String s : archivos) {
                Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(s);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    Log.v("llenando_spinner2", "Cuadratura.\n\nFile: " + s + "\n\n.");
                    try {
                        String fecha_next_abono = "";
                        String intereses_mora = "";
                        String saldo_mas_intereses_s = "";
                        String morosidad_s = "";
                        String cuadratura_pre = "";
                        String cuadratura_bkup = "";
                        String monto_prestado = "";
                        String[] split_indice = s.split("_P_");
                        String numero_de_credito = split_indice[1];
                        InputStreamReader archivo = new InputStreamReader(openFileInput(s));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        while (linea != null) {
                            String[] split = linea.split("_separador_");
                            if (split[0].equals("proximo_abono")) {
                                fecha_next_abono = split[1];
                                Log.v("llenar_spinner2.5", "Cuadratura.\n\nfecha_next_abono: " + fecha_next_abono + "\n\n.");
                            }
                            if (split[0].equals("monto_credito")) {
                                monto_prestado = split[1];
                            }
                            if (split[0].equals("saldo_mas_intereses")) {
                                saldo_mas_intereses_s = split[1];
                            }
                            if (split[0].equals("morosidad")) {
                                morosidad_s = split[1];
                            }
                            if (split[0].equals("cuadratura")) {
                                cuadratura_pre = split[1];
                                cuadratura_bkup = cuadratura_pre;
                            }
                            if (split[0].equals("intereses_moratorios")) {
                                intereses_mora = split[1];
                            }
                            linea = br.readLine();
                        }
                        br.close();
                        archivo.close();
                        String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                        String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                        Log.v("llenando_spinner4", "Cuadratura.\n\ninteres_moritas: " + intereses_moritas + "\n\n.");
                        interes_mora_total = intereses_moritas;
                        Log.v("llenando_spinner5", "Cuadratura.\n\ninteres_mora_total: " + interes_mora_total + "\n\n.");
                        interes_mora_parcial = interes_mora_total;
                        Log.v("llenando_spinner6", "Cuadratura.\n\ninteres_mora_parcial: " + interes_mora_parcial + "\n\n.");
                        saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mora, monto_prestado);
                        Log.v("llenando_spinner8", "Cuadratura.\n\nsaldo_mas_intereses: " + saldo_mas_intereses_s + "\n\n.");
                        String cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup);
                        Log.v("llenando_spinner9", "Cuadratura.\n\ncuotas_morosas: " + cuotas_morosas + "\n\n.");
                        Log.v("llenando_spinner10", "Cuadratura.\n\nMorosidad_s: " + morosidad_s + "\n\nMorosidad: " + morosidad + "\n\n.");
                        double saldo_mas_intereses_D = Double.parseDouble(saldo_mas_intereses_s);
                        int saldo_mas_intereses_I = (int) saldo_mas_intereses_D;
                        saldo_mas_intereses_s = String.valueOf(saldo_mas_intereses_I);
                        if (Integer.parseInt(saldo_plus_s) > 1000) {
                            creditos.append("#").append(numero_de_credito).append(" ").append(saldo_mas_intereses_s).append(" ").append(morosidad).append(" ").append(cuotas_morosas).append("___");
                        }
                    } catch (IOException | ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        String[] split_spinner = creditos.toString().split("___");
        sp_plazos.setEnabled(true);
        sp_plazos.setVisibility(View.VISIBLE);
        String string = "Credito:";
        tv_esperar.setText(string);
        et_ID.setText("");
        et_ID.setEnabled(false);
        et_ID.setVisibility(View.INVISIBLE);
        bt_consultar.setVisibility(View.INVISIBLE);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, R.layout.custom_spinner, split_spinner);
        sp_plazos.setAdapter(adapter);
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        spinner_listener();
    }

    private void datos_cobrador () {
        try {
            String cobrador = "a_sfile_cobrador_sfile_a.txt";
            InputStreamReader archivo = new InputStreamReader(openFileInput(cobrador));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split(" ");
                if (split[0].equals("apodo")) {
                    cobrador_s = split[1];
                }
                if (split[0].equals("telefono")) {
                    telefono_s = split[1];
                }
                linea = br.readLine();
            }
            br.close();
            archivo.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String obtener_intereses_moratorios (String saldo_plus, String next_pay) throws ParseException {
        if (next_pay.equals("Prestamo cancelado")) {
            next_pay = dia + "/" + mes + "/" + anio;
        }
        String flag;
        String[] split2 = next_pay.split("/");
        Log.v("obt_int_morat_0", "Cuadratura.\n\nfecha next pay: " + next_pay + "\n\n.");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Log.v("obt_int_morat_1", "Cuadratura.\n\nProximo abono: " + proximo_abono_formato + "\n\n.");
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        int diferencia_en_dias = DateUtilities.daysBetween(hoy_LD, proximo_abono_LD);
        Log.v("obt_int_morat_2", "Cuadratura.\n\nDiferencia en dias: " + diferencia_en_dias + "\n\nfecha_hoy: " + hoy_LD.toString() + "\n\nProximo abono: " + proximo_abono_LD + "\n\n.");
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            morosidad = "D";
            interes_mora_parcial = "0";
        } else {//Significa que esta atrazado!!!
            Log.v("obt_int_morat_3", "Cuadratura.\n\nDiferencia en dias: " + diferencia_en_dias + "\n\ninteres_mora: " + interes_mora + "\n\nSaldo_plus: " + saldo_plus + "\n\n.");
            double pre_num0 = diferencia_en_dias * (Integer.parseInt(interes_mora)) * (Integer.parseInt(saldo_plus));
            double pre_num = pre_num0 / 100;
            int pre_num_int = (int) pre_num;
            Log.v("obt_int_morat_4", "Cuadratura.\n\npre_num_int: " + pre_num_int + "\n\n.");
            if (pre_num_int > 0) {
                morosidad = "M";
                interes_mora_parcial = String.valueOf(pre_num_int);
            } else {
                interes_mora_parcial = "0";
            }
        }
        flag = interes_mora_parcial;
        interes_mora_total = interes_mora_parcial;
        Log.v("obt_int_morat_5", "Cuadratura.\n\nintereses moratorios: " + interes_mora_parcial + "\n\n.");
        return flag;
    }

    private String obtener_saldo_al_dia (String saldo_plus, String next_pay, String intereses_de_mora, String monto_prestado) throws ParseException {
        if (next_pay.equals("Prestamo cancelado")) {
            next_pay = dia + "/" + mes + "/" + anio;
        }
        String flag, saldo;
        String[] split2 = next_pay.split("/");
        String proximo_abono_formato = split2[2] + "-" + split2[1] + "-" + split2[0];
        Date proximo_abono_LD = DateUtilities.stringToDate(proximo_abono_formato);
        int diferencia_en_dias = DateUtilities.daysBetween(hoy_LD, proximo_abono_LD);
        Log.v("obt_sald_al_dia0", "Abonar.\n\nDiferencia en dias: " + diferencia_en_dias + "\n\nnext_pay: " + next_pay + "\n\nIntereses de mora: " + intereses_de_mora + "\n\nSaldo_plus: " + saldo_plus + "\n\n.");
        if (diferencia_en_dias <= 0) {//Significa que esta al dia!!!
            saldo = String.valueOf(Integer.parseInt(saldo_plus) + Integer.parseInt(intereses_de_mora));
            morosidad = "D";
        } else {//Significa que esta atrazado!!!
            double pre_saldo = diferencia_en_dias * (Integer.parseInt(interes_mora)) * Integer.parseInt(saldo_plus);
            Log.v("obt_saldo_al_diaM1", "Abonar.\n\nPre saldo: " + pre_saldo + "\n\n.");
            Log.v("obt_saldo_al_diaM1.1", "Abonar.\n\ninteres_mora_total: " + interes_mora_total + "\n\nintereses_de_mora: " + intereses_de_mora + "\n\n.");
            saldo = String.valueOf(Integer.parseInt(saldo_plus)  + Integer.parseInt(interes_mora_total) + Integer.parseInt(intereses_de_mora));//No se suman intereses sobre los intereses moratorios, pero si sobre el interes acordado del credito!!!
            Log.v("obt_saldo_al_diaM2", "Abonar.\n\nSaldo: " + saldo + "\n\n.");
            double pre_num_pre = Integer.parseInt(interes_mora) * Integer.parseInt(monto_prestado) * diferencia_en_dias;
            pre_num_pre = pre_num_pre / 100;
            double pre_num = (pre_num_pre) + Integer.parseInt(intereses_de_mora);
            int pre_num_int = (int) pre_num;
            if (pre_num_int > 0) {
                morosidad = "M";
            }
        }
        Log.v("obt_saldo_al_dia_end", "Abonar.\n\nSaldo (flag): " + saldo + "\n\n.");
        flag = saldo;
        return flag;
    }

    private boolean revisar_creditos () {
        Log.v("revisando_creditos0", ".\n\nAbonar. Revisando creditos.");
        boolean flasg = false;
        String flag;
        StringBuilder lista_archivos = new StringBuilder();
        String[] archivos = fileList();
        Log.v("revisando_creditos1", ".\n\nAbonar. \n\nTotal de archivos: " + archivos.length + "\n\n.");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("revisando_creditos2", "*********ERROR*************Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
            for (String s : archivos) {
                String cuadratura_tempo;
                Pattern pattern = Pattern.compile(cliente_ID + "_P_", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(s);
                boolean matchFound = matcher.find();
                if (matchFound) {
                    try {
                        InputStreamReader archivo = new InputStreamReader(openFileInput(s));
                        BufferedReader br = new BufferedReader(archivo);
                        String linea = br.readLine();
                        while (linea != null) {
                            String[] split = linea.split("_separador_");
                            if (split[0].equals("cuadratura")) {
                                cuadratura_tempo = split[1];
                                Log.v("revisando_creditos3", "Cuadratura.\n\nlinea:\n\n" + linea + "\n\nCuadratura:\n\n" + cuadratura_tempo + "\n\n.");
                                //cuadratura_separador_semana_1_0_09/1/2023__semana_2_0_16/1/2023__semana_3_0_23/1/2023__semana_4_15555_30/1/2023__semana_5_15555_06/2/2023__semana_6_15555_13/2/2023__semana_7_15555_20/2/2023__semana_8_15555_27/2/2023__semana_9_15555_06/3/2023__
                                String[] splitCuadra_segs = cuadratura_tempo.split("__");
                                int deuda = 0;
                                for (String splitCuadra_seg : splitCuadra_segs) {
                                    String[] splitCuadra_vals = splitCuadra_seg.split("_");
                                    deuda = deuda + Integer.parseInt(splitCuadra_vals[2]);
                                    Log.v("revisando_creditos4", "Cuadratura. Deuda: " + deuda + ".");
                                }
                                if (deuda >= 1000) {
                                    lista_archivos.append(s).append("_sep_");//Significa que es un credito activo.
                                    Log.v("revisando_creditos5", "Cuadratura.\n\nlista_archivos:\n\n" + lista_archivos + "\n\n.");
                                }
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
        }
        if (lista_archivos.toString().equals("")) {
            flag = "0";
        } else {
            String[] split = lista_archivos.toString().split("_sep_");
            int spl_long = split.length;
            flag = String.valueOf(spl_long);
        }
        if (Integer.parseInt(flag) == 0) {
            esperar();
        } else if (Integer.parseInt(flag) == 1) {
            String[] split = lista_archivos.toString().split("_sep_");
            Log.v("revisando_creditos5", ".\n\nAbonar. Archivo correcto: " + split[0] + "\n\n.");
            archivo_prestamo = split[0];
            Log.v("revisando_creditos6", ".\n\nAbonar. Contenido del archivo " + archivo_prestamo + ":\n\n" + imprimir_archivo(archivo_prestamo) + "\n\n.");
        } else {
            flasg = true;
        }
        Log.v("revisando_creditos8", ".\n\nflag: " + flasg + "\n\n.");
        return flasg;
    }

    public void consultar (View view) throws JSONException, IOException, InterruptedException {
        bt_consultar.setClickable(false);
        bt_consultar.setEnabled(false);
        if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
            String[] archivos = fileList();
            StringBuilder archivoCompleto = new StringBuilder();
            String file_to_consult;
            if (flag_client_reciv) {
                file_to_consult = cliente_recibido + "_C_";
            } else {
                file_to_consult = et_ID.getText().toString() + "_C_";
            }
            if (file_to_consult.contains("*") || file_to_consult.contains(" ")) {
                Log.v("Consultar0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            } else {
                Log.v("consultar0.2", "Abonar.\n\nCliente_ID: " + cliente_ID + "\n\nFile to consult: " + file_to_consult + "\n\n.");
                for (String s : archivos) {
                    Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(s);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        try {
                            InputStreamReader archivo = new InputStreamReader(openFileInput(s));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            while (linea != null) {
                                Log.v("Digite_cedula", ".\n\nlinea:\n\n" + linea + "\n\n.");
                                String[] split = linea.split("_separador_");
                                if (split[0].equals("puntuacion_cliente")) {
                                    puntuacion_cliente = split[1];
                                }
                                if (split[0].equals("ID_cliente")) {
                                    cliente_ID = split[1];
                                }
                                String string = "ESTADO DE CUENTA\nCliente: " + nombreCliente + "\nID: " + cliente_recibido;
                                tv_saludo.setText(string);
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
                        break;
                    }
                }
            }
            if (archivoCompleto.toString().equals("")) {
                //No se encontro el cliente. NUNCA DEBERIA LLEGAR AQUI!!! (FILTRO PREVIO)
                Toast.makeText(this, "Cliente no encontrado", Toast.LENGTH_SHORT).show();
                text_listener();
            } else {
                Log.v("consultar_digite_ced", ".\n\nLlamando a llenar spinner." + "\n\n.");
                boolean credits = revisar_creditos();
                if (credits) {
                    llenar_spinner();//Aqui se debe llamar a presentar_info_credito().
                } else {
                    presentar_info_credito("UNO");
                }
            }
        } else if (tv_esperar.getText().toString().equals("Monto a pagar al dia de hoy: ")) {
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
            tv_esperar.setText("");
            procesar_abono2();
        } else if (tv_esperar.getText().toString().equals("Prestamo a consultar:")){
            bt_consultar.setClickable(false);
            bt_consultar.setEnabled(false);
            Log.v("Prestamo_a_consultar1", ".\n\nPrestamo que se va a abonar: " + et_ID.getText().toString() + "\n\n.");
            String[] parts_prestamo = et_ID.getText().toString().split(" ");
            int monto_a_pagar;
            Integer cantidad_cuotas_pendientes = Integer.parseInt(parts_prestamo[3]);
            morosidad = parts_prestamo[2];
            monto_cuota = obtener_monto_cuota(parts_prestamo[0]);
            Log.v("Prestamo_a_consultar2", ".\n\nCoutas pendientes: " + cantidad_cuotas_pendientes + "\n\nInteres mora total: " +
                    interes_mora_parcial + "\n\nMorosidad: " + morosidad + "\n\nMonto cuota: " + monto_cuota + "\n\n.");
            int interesMoraTotal;
            if (Integer.parseInt(parts_prestamo[3]) == 0) {
                monto_a_pagar = monto_cuota + intereses_monroe;
                interesMoraTotal = intereses_monroe;
                Log.v("consultar0.01", "Abonar.\n\nmonto a pagar: " + monto_a_pagar + "\ninteresMoraTotal: " + interesMoraTotal + "\n\n");
            } else {
                if (morosidad.equals("D")) {
                    Log.v("consultar0.02", "Abonar.\n\nESTO NUNCA VA A PASAR ERROR\n\n");
                } else {
                    monto_a_pagar = cantidad_cuotas_pendientes * monto_cuota + Integer.parseInt(interes_mora_parcial);
                    interesMoraTotal = Integer.parseInt(interes_mora_parcial);
                    Log.v("consultar0.03", "Abonar.\n\nmonto a pagar: " + monto_a_pagar + "\ninteresMoraTotal: " +
                            interesMoraTotal + "\n\ncantidad_cuotas_pendientes: " + cantidad_cuotas_pendientes + "\n\n");
                }
            }
            presentar_monto_a_pagar();
        }
    }

    private void procesar_abono2 () {
        String file_name = archivo_prestamo;
        Log.v("procesar_abono20", "Cuadratura.\n\nfile_name: " + file_name + "\n\n.");
        StringBuilder contenido = new StringBuilder();
        String fecha_next_abono = "";
        String interes_mora_total_s = "";
        String saldo_mas_intereses_s;
        try {
            InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
            BufferedReader br = new BufferedReader(archivo);
            String linea = br.readLine();
            while (linea != null) {
                String[] split = linea.split("_separador_");
                switch (split[0]) {
                    case "cuadratura":
                        cuadratura = split[1];
                        break;
                    case "proximo_abono":
                        fecha_next_abono = split[1];
                        break;
                    case "plazo":
                        plazo = split[1];
                        break;
                    case "monto_credito":
                        monto_prestado_final = split[1];
                        break;
                    case "saldo_mas_intereses":
                        saldo_mas_intereses_s = split[1];
                        saldo_mas_intereses = Integer.parseInt(saldo_mas_intereses_s);
                        break;
                    case "morosidad":
                        morosidad = split[1];
                        break;
                    case "intereses_moratorios":
                        interes_mora_total_s = split[1];
                        break;
                }
                contenido.append(linea).append("\n");
                linea = br.readLine();
            }
            br.close();
            archivo.close();
            Log.v("prosesar_abono2", "Cuadratura.\n\nArchivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\nPlazo: " + plazo + "\n\n.");
            String saldo_plus_s = obtener_saldo_plus(cuadratura);
            Log.v("proc_abono00", "Cuadratura.\n\nSaldo plus: " + saldo_plus_s + "\n\n.");
            interes_mora_total = obtener_intereses_moratorios(monto_prestado_final, fecha_next_abono);
            interes_mora_parcial = interes_mora_total;
            Log.v("antes_de_cuadra_chang", ".\n\nCuadratura. Archivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
            intereses_monroe = Integer.parseInt(interes_mora_total_s);//Son los intereses guardados en el archivo. calculados en un periodo que se abono solo parte de los intereses.
            Log.v("proc_abo_21", "Cuadratura.\n\nsaldo_mas_intereses_s: " + saldo_mas_intereses + "\n\n.");
            presentar_cuadratura();
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    private void presentar_cuadratura () throws ParseException {
        String cuadratura_print = generar_cuadra_print();
        if (mensaje_imprimir_pre == null) {
            mensaje_imprimir_pre = "*****  ESTADO DE CUENTA  *****\n";
        } else if (mensaje_imprimir_pre.equals("")) {
            mensaje_imprimir_pre = "*****  CREDITO APROBADO  *****\n";
        }
        mensaje_imprimir = "\n\nFecha: " + dia + "/" + mes + "/" + anio + "\n\n\n***** Prestamos El Chino *****\n\nCliente: " +
                nombreCliente + "\nCedula: " + cliente_ID + "\n\n\n******************************\n\n" + mensaje_imprimir_pre +
                "\n******************************\n\n" + cuadratura_print +
                "Estimado cliente, no olvide\nrevisar su tiquete antes de\nque se retire el cobrador.\n\nSi necesita dinero,\nllame a " +
                cobrador_s + "\nTelefono: " + telefono_s + "\n\n\n\n\n";
        bt_consultar.setEnabled(false);
        bt_consultar.setVisibility(View.INVISIBLE);
        tv_esperar.setText("");
        tv_esperar.setVisibility(View.INVISIBLE);
        et_ID.setText("");
        et_ID.setVisibility(View.INVISIBLE);
        et_ID.setEnabled(false);
        sp_plazos.setEnabled(false);
        sp_plazos.setVisibility(View.INVISIBLE);
        String[] split_1 = cuadratura.split("__");
        int largo_split = split_1.length;
        HashMap<Integer, Button> botones = new HashMap<>();
        botones.put(0, sequi1);
        botones.put(1, sequi2);
        botones.put(2, sequi3);
        botones.put(3, sequi4);
        botones.put(4, sequi5);
        botones.put(5, sequi6);
        botones.put(6, sequi7);
        botones.put(7, sequi8);
        botones.put(8, sequi9);
        botones.put(9, sequi10);
        botones.put(10, sequi11);
        botones.put(11, sequi12);
        botones.put(12, sequi13);
        botones.put(13, sequi14);
        botones.put(14, sequi15);
        botones.put(15, sequi16);
        botones.put(16, sequi17);
        botones.put(17, sequi18);
        botones.put(18, sequi19);
        botones.put(19, sequi20);
        botones.put(20, sequi21);
        TreeMap<Integer, Button> botones_tree = getTreeMapBotones(botones);
        for (int i = 0; i < largo_split; i++) {
            String[] split = split_1[i].split("_");//TODO: Si estan en cero o al dia, se debe pintar verde el boton, si es hoy el dia, pintar amarillo, si esta atrazado, pintar verde.
            //Log.v("presentar_cuadratura0", "Cuadratura.\n\nfecha cuadro: " + split[3] + "\n\n");
            String fecha_cuadrito = split[3];
            String[] split_fec = fecha_cuadrito.split("/");
            fecha_cuadrito = split_fec[2] + "-" + split_fec[1] + "-" + split_fec[0];
            Date fecha_cuadrito_LD = DateUtilities.stringToDate(fecha_cuadrito);
            //Log.v("presentar_cuadratura1", "Cuadratura.\n\nfecha_cuadro_D: " + fecha_cuadrito_LD.toString() + "\n\nfea_cuadrito: " + fecha_cuadrito + "\n\n");
            String diferencia_fechas = String.valueOf(DateUtilities.daysBetween(hoy_LD, fecha_cuadrito_LD));
            //Log.v("presentar_cuadratura2", "Cuadratura.\n\nhoy_LD:\n" + hoy_LD + "\n\nFecha cuadrito_LD:\n" + fecha_cuadrito_LD + "\n\nDiferencia en dias: "+ diferencia_fechas + "\n\n.");
            String info_boton = split[3] + "\n" + split[0] + " " + split[1] + "\n" + split[2];
            if (Integer.parseInt(diferencia_fechas) > 0) {//Significa que esta atrasado.
                if (Integer.parseInt(split[2]) == 0) {
                    Objects.requireNonNull(botones_tree.get(i)).setTextColor(0XFF0D7302);//VERDE OBSCURO
                    Objects.requireNonNull(botones_tree.get(i)).setBackgroundColor(0xFF97FD8C);//VERDE CLARO
                } else {
                    Objects.requireNonNull(botones_tree.get(i)).setTextColor(0XFFDE0037);//ROJO OBSCURO
                    Objects.requireNonNull(botones_tree.get(i)).setBackgroundColor(0xFFFDAAC5);//ROJO CLARO
                }
            } else if (Integer.parseInt(diferencia_fechas) < 0 ) {//Es una fecha posterior a hoy
                Objects.requireNonNull(botones_tree.get(i)).setTextColor(0XFF0D7302);//VERDE OBSCURO
                Objects.requireNonNull(botones_tree.get(i)).setBackgroundColor(0xFF97FD8C);//VERDE CLARO
            } else if (Integer.parseInt(diferencia_fechas) == 0 ) {//Es hoy
                if (Integer.parseInt(split[2]) == 0) {
                    Objects.requireNonNull(botones_tree.get(i)).setTextColor(0XFF0D7302);//VERDE OBSCURO
                    Objects.requireNonNull(botones_tree.get(i)).setBackgroundColor(0xFF97FD8C);//VERDE CLARO
                } else {
                    Objects.requireNonNull(botones_tree.get(i)).setTextColor(0XFF949900);//AMARILLO OBSCURO
                    Objects.requireNonNull(botones_tree.get(i)).setBackgroundColor(0xFFF9FF61);//AMARILLO CLARO
                }
            }
            Objects.requireNonNull(botones_tree.get(i)).setVisibility(View.VISIBLE);
            Objects.requireNonNull(botones_tree.get(i)).setText(info_boton);
            Objects.requireNonNull(botones_tree.get(i)).setClickable(false);//TODO: Aqui se debe hacer un algoritmo que al tener monto pendiente, se pueda cancelar solo esa cuota.
            bt_imprimir.setVisibility(View.VISIBLE);
            whatsapp.setVisibility(View.VISIBLE);
            tvWhatsApp.setVisibility(View.VISIBLE);
        }
    }

    private String generar_cuadra_print() {//******************************
        StringBuilder flag = new StringBuilder("\n******************************\n********* CUADRATURA *********\n******************************\n");
        String[] split = cuadratura.split("__");
        int cuottas = 0;
        for (String s : split) {
            String[] split_1 = s.split("_");
            String montoIngresado_s = String.valueOf(split_1[2]);
            char[] chars = montoIngresado_s.toCharArray();
            Log.v("debug0", "\n\nsplit_1[2]: " + split_1[2] + "\n\nchars.length: " + chars.length + " \n\n.");
            for (char c : chars) {
                Log.v("forDebug_0", "char: " + c);
            }
            if (chars.length == 1) {
                montoIngresado_s = String.valueOf(chars[0]) + ",00";
            } else if (chars.length == 2) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + ",00";
            } else if (chars.length == 3) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + ",00";
            } else if (chars.length == 4) {
                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + ",00";
            } else if (chars.length == 5) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + "." + String.valueOf(chars[2]) + String.valueOf(chars[3]) + String.valueOf(chars[4]) + ",00";
            } else if (chars.length == 6) {
                montoIngresado_s = String.valueOf(chars[0]) + String.valueOf(chars[1]) + String.valueOf(chars[2]) + "." + String.valueOf(chars[3]) + String.valueOf(chars[4]) + String.valueOf(chars[5]) + ",00";
            } else if (chars.length == 7) {
                montoIngresado_s = String.valueOf(chars[0]) + "." + String.valueOf(chars[1]) + String.valueOf(chars[2]) + String.valueOf(chars[3]) + "." + String.valueOf(chars[4]) + String.valueOf(chars[5]) + String.valueOf(chars[6]) + ",00";
            }
            Log.v("debug0", "\n\nmontoIngresado_s: " + montoIngresado_s + "\n\n.");
            flag.append("\nCuota ").append(split_1[0]).append(" ").append(split_1[1]).append(":\n").append(montoIngresado_s).append(" colones.\nFecha de pago: ").append(split_1[3]).append("\n");
            if (Integer.parseInt(split_1[2]) > 0) {//Significa que tiene esta cuota pendiente.
                cuottas = cuottas + 1;
            }
        }
        if (abonar != null) {
            flag = new StringBuilder("\n******************************\n");
        } else {
            flag.append("\n******************************\n").append(imprimir_intermedio).append("\n******************************\n\n");
        }
        return flag.toString();
    }

    private TreeMap<Integer, Button> getTreeMapBotones(HashMap<Integer, Button> botones) {
        TreeMap<Integer, Button> treeMap = new TreeMap<>();
        for (int key : botones.keySet()) {
            treeMap.put(key, botones.get(key));
        }
        return treeMap;
    }

    private String obtener_saldo_plus (String cuadratura_s) {
        String flag;
        Log.v("obt_sald_plus", "Cuadratura.\n\nCuadratura:\n\n" + cuadratura_s + "\n\n.");
        String[] split = cuadratura_s.split("__");
        int saldo_plus_plus = 0;
        for (String s : split) {
            String[] split_1 = s.split("_");
            if (Integer.parseInt(split_1[2]) > 0) {//Significa que tiene esta cuota pendiente.
                saldo_plus_plus = saldo_plus_plus + Integer.parseInt(split_1[2]);
            }
        }
        flag = String.valueOf(saldo_plus_plus);
        return flag;
    }

    private void presentar_monto_a_pagar () throws JSONException, IOException, InterruptedException {
        et_ID.setEnabled(true);
        et_ID.setText("0");
        et_ID.setFocusableInTouchMode(true);
        et_ID.setVisibility(View.VISIBLE);
        et_ID.setHint("Digite el monto a abonar...");
        bt_consultar.setEnabled(true);
        String string = "REALIZAR PAGO";
        bt_consultar.setText(string);
        bt_consultar.setVisibility(View.VISIBLE);
        bt_consultar.setClickable(true);
        string = "Monto a pagar al dia de hoy: ";
        tv_esperar.setText(string);
        tv_esperar.setVisibility(View.VISIBLE);
        et_ID.requestFocus();
        consultar(null);
    }

    private Integer obtener_monto_cuota (String s) {
        int flag = 0;
        String[] split = s.split("#");
        s = split[1];
        String[] archivos = fileList();
        Log.v("obt_monto_cuota", ".\n\nString: "+ s + "\n\nCliente ID: " + cliente_ID + "\n\nCantidd de archivos: " + archivos.length + "\n\nSplit[0]: " + split[0] + "\n\nSplit[1]: " + split[1] + "\n\n.");
        if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
            Log.v("obtener_monto_cuota.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
        } else {
            for (int i = 0; i < archivos.length; i++) {
                Pattern pattern = Pattern.compile(cliente_ID + "_P_" + s + "_P_", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(archivos[i]);
                boolean matchFound = matcher.find();
                Log.v("buscando_archivos", ".\n\nFile: #" + i + ": " + archivos[i] + "\n\n.");
                if (matchFound) {
                    Log.v("buscando_files", ".\n\nArchivo encontrado.\nContenido del archivo:\n\n" + imprimir_archivo(archivos[i]) + "\n\n.");
                    try {
                        String numero_de_credito;
                        String file_name = archivos[i];
                        String[] split_indice = file_name.split("_P_");
                        numero_de_credito = split_indice[1];
                        if (numero_de_credito.equals(split[1])) {
                            archivo_prestamo = file_name;
                            InputStreamReader archivo = new InputStreamReader(openFileInput(file_name));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            while (linea != null) {
                                Log.v("file_found", ".\n\nLinea: " + linea + "\n\n.");
                                String[] splitre = linea.split("_separador_");
                                if (splitre[0].equals("monto_cuota")) {
                                    monto_cuota = Integer.parseInt(splitre[1]);
                                    flag = monto_cuota;
                                    Log.v("obt_mont_cuota2", ".\n\nLinea:\n\n" + linea + "\n\n.");
                                }
                                linea = br.readLine();
                            }
                            br.close();
                            archivo.close();
                        }
                        //Log.v("restar_disponible2", ".\n\nArchivo: " + file_name + "\n\nContenido del archivo:\n\n" + imprimir_archivo(file_name) + "\n\n.");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return flag;
    }

    private void spinner_listener () {
        sp_plazos.setOnItemSelectedListener(
                new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        //Crear diccionario con la informacion de la loteria seleccionada
                        String seleccion = sp_plazos.getSelectedItem().toString();
                        if (seleccion.equals("Escoja el credito...")) {
                            bt_consultar.setClickable(false);
                            bt_consultar.setEnabled(false);
                        }else {
                            sp_plazos.setEnabled(false);
                            sp_plazos.setVisibility(View.INVISIBLE);
                            bt_consultar.setEnabled(true);
                            bt_consultar.setClickable(true);
                            bt_consultar.setVisibility(View.VISIBLE);
                            tv_esperar.setEnabled(true);
                            tv_esperar.setVisibility(View.VISIBLE);
                            bt_consultar.setFocusableInTouchMode(true);
                            tv_esperar.setText("");
                            tv_esperar.setFocusableInTouchMode(true);
                            tv_esperar.requestFocus();
                            try {
                                presentar_info_credito(sp_plazos.getSelectedItem().toString());
                            } catch (JSONException | IOException | InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                    }
                });
    }

    private void presentar_info_credito (String s) throws JSONException, IOException, InterruptedException {
        if (s.equals("UNO")) {
            String[] archivos = fileList();
            String file_to_consult;
            file_to_consult = archivo_prestamo;
            if (file_to_consult.contains("*") || file_to_consult.contains(" ")) {
                Log.v("presentar_info_credito0", "Abonar.\n\nClienteID: " + cliente_ID + "\n\nfile_to_consult: " + file_to_consult + "\n\n.");
            } else {
                for (String value : archivos) {
                    Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(value);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        try {
                            String fecha_next_abono = "";
                            String intereses_mor_archivo = "";
                            String saldo_mas_intereses_s = "";
                            String cuotas_morosas = "";
                            String valor_presentar_s;
                            String cuadratura_pre = "";
                            String cuadratura_bkup = "";
                            String monto_prestado = "";
                            String[] split_indice = value.split("_P_");
                            String numero_de_credito = split_indice[1];
                            InputStreamReader archivo = new InputStreamReader(openFileInput(value));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            while (linea != null) {
                                Log.v("presentar_info_credito1", ".\n\nlinea:\n\n" + linea + "\n\n.");
                                String[] split = linea.split("_separador_");
                                if (split[0].equals("proximo_abono")) {
                                    fecha_next_abono = split[1];
                                    Log.v("presentar_info_credito2", "Abonar.\n\nfecha_next_abono: " + fecha_next_abono + "\n\n.");
                                }
                                if (split[0].equals("monto_credito")) {
                                    monto_prestado = split[1];
                                }
                                if (split[0].equals("cuadratura")) {
                                    cuadratura_pre = split[1];
                                    cuadratura_bkup = cuadratura_pre;
                                }
                                if (split[0].equals("saldo_mas_intereses")) {
                                    saldo_mas_intereses_s = split[1];
                                }
                                if (split[0].equals("cuotas")) {
                                    cuotas_morosas = split[1];
                                }
                                if (split[0].equals("intereses_moratorios")) {
                                    intereses_mor_archivo = split[1];
                                }
                                linea = br.readLine();
                            }
                            br.close();
                            archivo.close();
                            Log.v("presentar_info_cred_2.5", "Cuadratura.\n\nCuadratura:\n\n" + cuadratura_pre + "\n\n.");
                            String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                            Log.v("presentar_info_credito3", "Cuadratura.\n\nsaldo_plus: " + saldo_plus_s + "\n\nsaldo_mas_intereses: " + saldo_mas_intereses_s + "\n\nIntereses_moratorios: " + intereses_mor_archivo + "\n\n.");
                            Log.v("presentar_info_credito4", "Cuadratura.\n\nsaldo_plus_s: " + saldo_plus_s + "\n\n.");
                            String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                            Log.v("presentar_info_credito5", "Cuadratura.\n\nintereses_moritas: " + intereses_moritas + "\n\n.");
                            interes_mora_total = intereses_moritas;
                            interes_mora_parcial = interes_mora_total;
                            Log.v("presentar_info_credito6", "Cuadratura.\n\ninteres_mora_total: " + interes_mora_total + "\n\n.");
                            intereses_monroe = Integer.parseInt(intereses_mor_archivo);//Son los intereses guardados en el archivo. calculados en un periodo que se abono solo parte de los intereses.
                            Log.v("presentar_info_credito8", "Cuadratura.\n\nsaldo_mas_intereses_s: " + saldo_mas_intereses_s + "\n\n.");
                            saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor_archivo, monto_prestado);
                            Log.v("presentar_info_credito9", "Cuadratura.\n\ncuotas_morosas_pre: " + cuotas_morosas + "\n\n.");
                            cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup);
                            Log.v("present_info_credito10", ".Cuadratura\n\ncuotas_morosas_post: " + cuotas_morosas + "\n\n.");
                            valor_presentar_s = "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas;
                            et_ID.setText("");
                            et_ID.setFocusableInTouchMode(false);
                            et_ID.setClickable(false);
                            et_ID.setEnabled(true);
                            et_ID.setVisibility(View.VISIBLE);
                            et_ID.setText(valor_presentar_s);
                            et_ID.setEnabled(false);
                            tv_esperar.setEnabled(true);
                            tv_esperar.setText("");
                            tv_esperar.setVisibility(View.VISIBLE);
                            String string = "Prestamo a consultar:";
                            tv_esperar.setText(string);
                            consultar(null);
                        } catch (IOException | ParseException e) {
                            e.printStackTrace();
                        }
                        break;
                    }
                }
            }
        } else {
            String valor_presentar_s = s;
            String[] splitte = valor_presentar_s.split(" ");
            String num_credit = splitte[0];
            num_credit = num_credit.replace("#", "");
            String[] archivos = fileList();
            String file_to_consult;
            Log.v("present_info_credito11", "Abonar.\n\nnum_credit: " + num_credit + "\n\n.");
            file_to_consult = cliente_ID + "_P_" + num_credit + "_P_";
            if (cliente_ID.contains("*") || cliente_ID.contains(" ")) {
                Log.v("present_info_credito12", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
            } else {
                for (String value : archivos) {
                    Pattern pattern = Pattern.compile(file_to_consult, Pattern.CASE_INSENSITIVE);
                    Matcher matcher = pattern.matcher(value);
                    boolean matchFound = matcher.find();
                    if (matchFound) {
                        try {
                            String fecha_next_abono = "";
                            String intereses_mor = "";
                            String saldo_mas_intereses_s = "";
                            String cuotas_morosas;
                            String cuadratura_pre = "";
                            String monto_prestado = "";
                            String cuadratura_bkup = "";
                            String[] split_indice = value.split("_P_");
                            String numero_de_credito = split_indice[1];
                            InputStreamReader archivo = new InputStreamReader(openFileInput(value));
                            BufferedReader br = new BufferedReader(archivo);
                            String linea = br.readLine();
                            while (linea != null) {
                                Log.v("present_info_credito13", ".\n\nlinea:\n\n" + linea + "\n\n.");
                                String[] split = linea.split("_separador_");
                                if (split[0].equals("proximo_abono")) {
                                    fecha_next_abono = split[1];
                                    Log.v("present_info_credit13.5", ".\n\nfecha_next_abono:\n\n" + fecha_next_abono + "\n\n.");
                                }
                                if (split[0].equals("monto_credito")) {
                                    monto_prestado = split[1];
                                }
                                if (split[0].equals("saldo_mas_intereses")) {
                                    saldo_mas_intereses_s = split[1];
                                }
                                if (split[0].equals("cuadratura")) {
                                    cuadratura_pre = split[1];
                                    cuadratura_bkup = cuadratura_pre;
                                }
                                if (split[0].equals("intereses_moratorios")) {
                                    intereses_mor = split[1];
                                }
                                linea = br.readLine();
                            }
                            br.close();
                            archivo.close();
                            String saldo_plus_s = obtener_saldo_plus(cuadratura_pre);
                            Log.v("present_info_credito14", "Abonar.\n\nsaldo_plus: " + saldo_plus_s + "\n\nsaldo_mas_intereses: " + saldo_mas_intereses_s + "\n\nIntereses_moratorios: " + intereses_mor + "\n\n.");
                            Log.v("present_info_credito15", ".\n\nsaldo_plus_s: " + saldo_plus_s + "\n\n.");
                            String intereses_moritas = obtener_intereses_moratorios(monto_prestado, fecha_next_abono);//Aqui se obtienen los intereses moratorios hasta hoy.
                            Log.v("present_info_credito16", ".\n\nintereses_moritas: " + intereses_moritas + "\n\n.");
                            interes_mora_total = intereses_moritas;
                            interes_mora_parcial = interes_mora_total;
                            Log.v("present_info_credito17", ".\n\ninteres_mora_total: " + interes_mora_total + "\n\n.");
                            intereses_monroe = Integer.parseInt(intereses_mor);
                            saldo_mas_intereses_s = obtener_saldo_al_dia(saldo_mas_intereses_s, fecha_next_abono, intereses_mor, monto_prestado);
                            Log.v("present_info_credito19", ".\n\nsaldo_mas_intereses_s: " + saldo_mas_intereses_s + "\n\n.");
                            cuotas_morosas = obtener_cuotas_morosas(cuadratura_bkup);
                            Log.v("present_info_credito20", ".\n\ncuotas_morosas: " + cuotas_morosas + "\n\n.");
                            valor_presentar_s = "#" + numero_de_credito + " " + saldo_mas_intereses_s + " " + morosidad + " " + cuotas_morosas;
                            et_ID.setText("");
                            et_ID.setFocusableInTouchMode(false);
                            et_ID.setClickable(false);
                            et_ID.setEnabled(true);
                            et_ID.setVisibility(View.VISIBLE);
                            et_ID.setText(valor_presentar_s);
                            et_ID.setEnabled(false);
                            tv_esperar.setEnabled(true);
                            tv_esperar.setText("");
                            tv_esperar.setVisibility(View.VISIBLE);
                            String string = "Prestamo a consultar:";
                            tv_esperar.setText(string);
                            consultar(null);
                        } catch (IOException | JSONException | InterruptedException | ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }

    private void text_listener () {
        //Implementacion de un text listener
        et_ID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (tv_esperar.getText().toString().equals("Digite el monto del abono")) {
                    bt_consultar.setClickable(false);
                    bt_consultar.setEnabled(false);
                    et_ID.setEnabled(true);
                    et_ID.setVisibility(View.VISIBLE);
                    et_ID.setInputType(InputType.TYPE_CLASS_NUMBER);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();
                    if (!String.valueOf(s).equals("")) {
                        bt_consultar.setClickable(true);
                        bt_consultar.setEnabled(true);
                    }
                } else if (tv_esperar.getText().toString().equals("Digite la identificacion del cliente")) {
                    et_ID.setEnabled(true);
                    et_ID.setFocusableInTouchMode(true);
                    et_ID.requestFocus();
                    //bt_consultar.setClickable(false);
                    String[] archivos = fileList();
                    //bt_consultar.setEnabled(false);
                    if (et_ID.getText().toString().contains("*") || et_ID.getText().toString().contains(" ")) {
                        Log.v("text_listener0.1", "Abonar.\n\nClienteID: " + cliente_ID + "\n\n");
                    } else {
                        for (String archivo : archivos) {
                            Pattern pattern = Pattern.compile(et_ID.getText().toString(), Pattern.CASE_INSENSITIVE);
                            Matcher matcher = pattern.matcher(archivo);
                            //Log.v("text_listener_identifi", ".\n\narchivos[" + i + "]: " + archivos[i] + "\n\n.");
                            boolean matchFound = matcher.find();
                            if (matchFound) {
                                if (s.length() >= 9) {
                                    bt_consultar.setEnabled(true);
                                    bt_consultar.setClickable(true);
                                }
                            }
                        }
                    }
                }
            }
            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void esperar () {
        ocultar_todito();
        Toast.makeText(this, "Cliente no posee creditos activos!", Toast.LENGTH_LONG).show();
        String string = "Cliente no posee creditos activos!";
        tv_esperar.setText(string);
        for (int i = 0; i < 10; i++) {
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        salir();
    }

    private void salir() {
        if (activity_devolver.equals("MenuPrincipal")) {
            Intent activity_volver = new Intent(this, MenuPrincipal.class);
            activity_volver.putExtra("mensaje", "Cliente no posee creditos activos!");
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else if (activity_devolver.equals("Estado_cliente")) {
            Intent activity_volver = new Intent(this, Estado_clienteActivity.class);
            activity_volver.putExtra("mensaje", "Cliente no posee creditos activos!");
            activity_volver.putExtra("cliente_ID", cliente_Id_volver);
            startActivity(activity_volver);
            finish();
            System.exit(0);
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

    @Override
    public void onBackPressed(){
        bt_imprimir.setVisibility(View.INVISIBLE);
        whatsapp.setVisibility(View.INVISIBLE);
        tvWhatsApp.setVisibility(View.INVISIBLE);
        boton_atras();
    }

    private void boton_atras() {
        if (activity_devolver.equals("MenuPrincipal")) {
            Intent activity_volver = new Intent(this, MenuPrincipal.class);
            activity_volver.putExtra("mensaje", "");
            startActivity(activity_volver);
            finish();
            System.exit(0);
        } else if (activity_devolver.equals("Estado_cliente")) {
            Intent activity_volver = new Intent(this, Estado_clienteActivity.class);
            activity_volver.putExtra("mensaje", "");
            activity_volver.putExtra("cliente_ID", cliente_Id_volver);
            startActivity(activity_volver);
            finish();
            System.exit(0);
        }
    }

    private void ocultar_todito () {
        Log.v("ocultar_todito", "Se hace todo invisible");
        tv_esperar.setVisibility(View.VISIBLE);
        String string = "conectando, por favor espere...";
        tv_esperar.setText(string);
        bt_consultar.setVisibility(View.INVISIBLE);
    }

    private String imprimir_archivo(String file_name){
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

    public void printIt (View view) {
            BluetoothSocket socket;
            socket = null;
            byte[] data = mensaje_imprimir.getBytes();
            //Get BluetoothAdapter
            BluetoothAdapter btAdapter = BluetoothUtil.getBTAdapter();
            if (btAdapter == null) {
                Toast.makeText(getBaseContext(), "BlueTooth abierto!", Toast.LENGTH_SHORT).show();
                return;
            }
            // Get sunmi InnerPrinter BluetoothDevice
            String impresora = "00:11:22:33:44:55";
            BluetoothDevice device = BluetoothUtil.getDevice(btAdapter, impresora);
            if (device == null) {
                Toast.makeText(getBaseContext(), "Asegurese de tener conectada una impresora!!!", Toast.LENGTH_LONG).show();
                return;
            }
            try {
                socket = BluetoothUtil.getSocket(device);
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                assert socket != null;
                BluetoothUtil.sendData(data, socket);
            } catch (IOException e) {
                e.printStackTrace();
            }
    }

}