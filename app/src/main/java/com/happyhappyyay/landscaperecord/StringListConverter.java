package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class StringListConverter {

    @TypeConverter
    public String fromStringList(List<String> strings) {
        if (strings == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {
        }.getType();
        return gson.toJson(strings, type);
    }

    @TypeConverter
    public List<String> toStringList(String strings) {
        if (strings == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<String>>() {}.getType();
        return gson.fromJson(strings, type);
    }
}
