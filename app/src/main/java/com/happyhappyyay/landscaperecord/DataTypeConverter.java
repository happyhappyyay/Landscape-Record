package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class DataTypeConverter {

    @TypeConverter
    public String fromServicesList(List<Service> services) {
        if (services == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>() {}.getType();
        String json = gson.toJson(services, type);
        return json;
    }

    @TypeConverter
    public List<Service> toServicesList(String services) {
        if (services == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>() {}.getType();
        List<Service> servicesList = gson.fromJson(services, type);
        return servicesList;
    }
}
