package com.example.uiux.Utils;

import com.example.uiux.Model.Province;

import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class JsonParser {

    public static List<Province> parseProvinces(String jsonResponse) {
        List<Province> provinces = new ArrayList<>();

        try {
            JSONObject jsonObject = new JSONObject(jsonResponse);
            JSONArray results = jsonObject.getJSONArray("results");

            for (int i = 0; i < results.length(); i++) {
                JSONObject provinceObj = results.getJSONObject(i);
                String provinceId = provinceObj.getString("province_id");
                String provinceName = provinceObj.getString("province_name");
                String provinceType = provinceObj.getString("province_type");

                Province province = new Province(provinceId, provinceName, provinceType);
                provinces.add(province);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return provinces;
    }
}
