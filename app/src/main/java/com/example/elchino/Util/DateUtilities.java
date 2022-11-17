package com.example.elchino.Util;

//Realiza tareas referentes a calculos con fechas. Equivale a la clase LocalDate del api 26

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class DateUtilities {

    private static String dia;
    private static String mes;
    private static String anio;
    private static String fecha;
    private static Map<String, Integer> meses = new HashMap<String, Integer>();

    public static int daysBetween(Date fecha_inicio, Date fecha_final) {
        int diferencia = 0;
        long tiempo_transcurrido = fecha_inicio.getTime() - fecha_final.getTime();
        TimeUnit unidad = TimeUnit.DAYS;
        long dias = unidad.convert(tiempo_transcurrido, TimeUnit.MILLISECONDS);
        diferencia = (int) dias;
        diferencia = diferencia;
        return diferencia;
    }

    public static Date stringToDate(String fechaS) throws ParseException {
        Date date;
        SimpleDateFormat formato = new SimpleDateFormat("yyyy-MM-dd");
        date = formato.parse(fechaS);
        return date;
    }

    public static Date addDays(Date fecha_inicio, int days) throws ParseException {
        Date date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha_inicio);
        calendar.add(Calendar.DAY_OF_YEAR, days);
        date = calendar.getTime();
        return date;
    }

    public static Date addWeeks(Date fechaS, int weeks) throws ParseException {
        Date date;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaS);
        calendar.add(Calendar.WEEK_OF_YEAR, weeks);
        date = calendar.getTime();
        return date;
    }

    public static String dateToString(Date fechaLd) {
        String date;
        llenar_mapa_meses();
        separar_fechaYhora(fechaLd);
        date = anio + "-" + mes + "-" + fecha;
        return date;
    }

    private static void separar_fechaYhora(Date fechaLd){
        String fechaS = fechaLd.toString();
        String[] split = fechaS.split(" ");
        dia = split[2];
        mes = String.valueOf(meses.get(split[1]));
        anio = split[5];
        String hora_completa = split[3];
        fecha = split[2];
        split = hora_completa.split(":");
    }

    private static void llenar_mapa_meses() {
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
