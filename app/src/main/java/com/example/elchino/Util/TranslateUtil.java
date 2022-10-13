package com.example.elchino.Util;

//convierte archivos (ficheros) a formato Json para poder enviarlos a Google Sheets

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class TranslateUtil {

    public static JSONObject string_to_Json(String s, String spreadSheetId, String sheet, String id_file) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("spreadsheet_id", spreadSheetId);
        jsonObject.put("sheet", sheet);
        JSONArray rowsArray = new JSONArray();
        String[] split = s.split("_n_");// la letra "l" representa la linea.
        JSONArray row = new JSONArray();
        for (int i = 0; i < split.length; i++) {
            row.put(split[i]);
        }
        row.put(id_file);
        rowsArray.put(row);
        jsonObject.put("rows", rowsArray);
        return jsonObject;
    }

}
