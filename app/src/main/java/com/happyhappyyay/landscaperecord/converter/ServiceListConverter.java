package com.happyhappyyay.landscaperecord.converter;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.happyhappyyay.landscaperecord.pojo.Service;

import java.lang.reflect.Type;
import java.util.List;

public class ServiceListConverter {

    @TypeConverter
    public String fromServicesList(List<Service> services) {
        if (services == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>() {
        }.getType();
        return gson.toJson(services, type);
    }

    @TypeConverter
    public List<Service> toServicesList(String services) {
        if (services == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<List<Service>>() {}.getType();
        return gson.fromJson(services, type);
    }
}
