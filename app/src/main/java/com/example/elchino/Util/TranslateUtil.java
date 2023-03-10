package com.example.elchino.Util;

//convierte archivos (ficheros) a formato Json para poder enviarlos a Google Sheets

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TranslateUtil {

    public static JSONObject string_to_Json(String s, String spreadSheetId, String sheet) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spreadsheet_id", spreadSheetId);
        jsonObject.put("sheet", sheet);
        JSONArray rowsArray = new JSONArray();
        if (sheet.equals("cierre")) {
            String[] split = s.split("_l_");// la letra "l" representa la linea.
            for (int i = 0; i < split.length; i++) {
                String[] split2 = split[i].split("_n_");
                JSONArray row = new JSONArray();
                row.put(split2[0]);
                row.put(split2[1]);
                row.put(split2[2]);
                row.put(split2[3]);
                rowsArray.put(row);
            }
            jsonObject.put("rows", rowsArray);
        } else {
            String[] split = s.split("_n_");// la letra "l" representa la linea.
            JSONArray row = new JSONArray();
            for (int i = 0; i < split.length; i++) {
                row.put(split[i]);
            }
            rowsArray.put(row);
            jsonObject.put("rows", rowsArray);
        }
        return jsonObject;
    }

}
