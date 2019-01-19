package com.happyhappyyay.landscaperecord.Utility;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.happyhappyyay.landscaperecord.Converter.IntegerListConverter;
import com.happyhappyyay.landscaperecord.Converter.MapStringIntConverter;
import com.happyhappyyay.landscaperecord.Converter.PaymentConverter;
import com.happyhappyyay.landscaperecord.Converter.ServiceListConverter;
import com.happyhappyyay.landscaperecord.Converter.StringListConverter;
import com.happyhappyyay.landscaperecord.DAOInterface.CustomerDao;
import com.happyhappyyay.landscaperecord.DAOInterface.LogDao;
import com.happyhappyyay.landscaperecord.DAOInterface.UserDao;
import com.happyhappyyay.landscaperecord.DAOInterface.WorkDayDao;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseOperator;
import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.POJO.LogActivity;
import com.happyhappyyay.landscaperecord.POJO.User;
import com.happyhappyyay.landscaperecord.POJO.WorkDay;

@Database(entities = {User.class, Customer.class, LogActivity.class, WorkDay.class}, version = 1)
@TypeConverters({ServiceListConverter.class, IntegerListConverter.class, PaymentConverter.class,
        StringListConverter.class, MapStringIntConverter.class})
public abstract class AppDatabase extends RoomDatabase implements DatabaseOperator {
    private static AppDatabase instance;

    public static AppDatabase getAppDatabase(Context context) {
        if (instance == null) {
            instance = Room.databaseBuilder(context.getApplicationContext(), AppDatabase.class,
                    "user-database").build();
        }
        return instance;
    }

    public abstract UserDao userDao();

    public abstract CustomerDao customerDao();

    public abstract LogDao logDao();

    public abstract WorkDayDao workDayDao();
}
