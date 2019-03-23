package com.happyhappyyay.landscaperecord.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Map;

public class MapStringStringConverter {

    @TypeConverter
    public String fromMap(Map<String, String> stringMap) {
        if (stringMap == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        return gson.toJson(stringMap, type);
    }

    @TypeConverter
    public Map<String, String> toMap(String stringMap) {
        if (stringMap == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Map<String, String>>() {}.getType();
        return gson.fromJson(stringMap, type);
    }
}
