package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class IntArrayConverter {

    @TypeConverter
    public String fromIntArray(int[][] array) {
        if (array == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<int[][]>() {}.getType();
        return gson.toJson(array, type);
    }

    @TypeConverter
    public int[][] toIntArray(String array) {
        if (array == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<int[][]>() {}.getType();
        return gson.fromJson(array, type);
    }
}
