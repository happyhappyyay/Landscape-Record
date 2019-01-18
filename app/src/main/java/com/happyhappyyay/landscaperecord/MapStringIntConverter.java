package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MapStringIntConverter {

    @TypeConverter
    public String fromMap(Map<String, Integer> integerList) {
        if (integerList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        return gson.toJson(integerList, type);
    }

    @TypeConverter
    public Map<String, Integer> toMap(String integerList) {
        if (integerList == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, Integer>>() {}.getType();
        return gson.fromJson(integerList, type);
    }
}
