package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.TypeConverter;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class PaymentTypeConverter {

    @TypeConverter
    public String fromPayment(Payment payment) {
        if (payment == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Payment>() {
        }.getType();
        return gson.toJson(payment, type);
    }

    @TypeConverter
    public Payment toPayment(String payment) {
        if (payment == null) {
            return (null);
        }
        Gson gson = new Gson();
        Type type = new TypeToken<Payment>() {}.getType();
        return gson.fromJson(payment, type);
    }
}
