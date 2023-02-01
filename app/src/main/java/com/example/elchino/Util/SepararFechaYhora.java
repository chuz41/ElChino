package com.example.elchino.Util;

import android.util.Log;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class SepararFechaYhora {

    private String Hora;
    private String Minuto;
    private String Dia;
    private String Mes;
    private String Anio;
    private Date Fecha;
    private Date Fech;
    private Map<String, Integer> meses = new HashMap<String, Integer>();

    public SepararFechaYhora () {
    }

    public SepararFechaYhora (Date fech) {
        Date now = Calendar.getInstance().getTime();
        String nuevaFecha;
        this.Fecha = now;
        this.Fech = fech;
        if (Fech != null) {
            nuevaFecha = separacion(Fech);
        } else {
            nuevaFecha = separacion(Fecha);
        }
        String split[] = nuevaFecha.split("_");
        this.Dia = (split[0]);
        this.Mes = (split[1]);
        this.Anio = (split[2]);
        this.Hora = (split[3]);
        this.Minuto = (split[4]);
    }

    public String getHora () {
        return Hora;
    }

    public String getMinuto () {
        return Minuto;
    }

    public String getDia () {
        return Dia;
    }

    public String getMes () {
        return Mes;
    }

    public String getAnio () {
        return Anio;
    }

    private String separacion (Date fecha) {
        llenar_mapa_meses();
        String ahora = fecha.toString();
        String[] split = ahora.split(" ");
        String dia, mes, anio, hora, minuto;
        dia = split[2];
        mes = String.valueOf(meses.get(split[1]));
        anio = split[5];
        String hora_completa = split[3];
        split = hora_completa.split(":");
        minuto = split[1];
        hora = split[0];
        if (dia.length() == 1) {
            dia = "0" + dia;
        }
        if (mes.length() == 1) {
            mes = "0" + mes;
        }
        String nuevaFecha = dia + "_" + mes + "_" + anio + "_" + hora + "_" + minuto;
        Log.v("separacion_0", "seaprarFechaYhora.\n\nnuevaFecha: " + nuevaFecha + "\n(dia_mes_anio_hora_minuto)\n\n.");
        return nuevaFecha;
    }

    private void llenar_mapa_meses() {
        meses.put("Jan",1);
        meses.put("Feb",2);
        meses.put("Mar",3);
        meses.put("Apr",4);
        meses.put("May",5);
        meses.put("Jun",6);
        meses.put("Jul",7);
        meses.put("Aug",8);
        meses.put("Sep",9);
        meses.put("Oct",10);
        meses.put("Nov",11);
        meses.put("Dec",12);
        meses.put("1",1);
        meses.put("2",2);
        meses.put("3",3);
        meses.put("4",4);
        meses.put("5",5);
        meses.put("6",6);
        meses.put("7",7);
        meses.put("8",8);
        meses.put("9",9);
        meses.put("10",10);
        meses.put("11",11);
        meses.put("12",12);
    }

}
