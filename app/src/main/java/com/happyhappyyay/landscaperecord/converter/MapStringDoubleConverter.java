package com.happyhappyyay.landscaperecord.converter;

import androidx.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MapStringDoubleConverter {

    @TypeConverter
    public String fromMap(Map<String, Double> doubleMap) {
        if (doubleMap == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Double>>() {}.getType();
        return gson.toJson(doubleMap, type);
    }

    @TypeConverter
    public Map<String, Double> toMap(String doubleMap) {
        if (doubleMap == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Double>>() {}.getType();
        return gson.fromJson(doubleMap, type);
    }
}
