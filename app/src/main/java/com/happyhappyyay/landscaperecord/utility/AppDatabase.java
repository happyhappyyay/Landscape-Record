package com.happyhappyyay.landscaperecord.utility;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;

import com.happyhappyyay.landscaperecord.converter.IntegerListConverter;
import com.happyhappyyay.landscaperecord.converter.MapStringIntConverter;
import com.happyhappyyay.landscaperecord.converter.PaymentConverter;
import com.happyhappyyay.landscaperecord.converter.ServiceListConverter;
import com.happyhappyyay.landscaperecord.converter.StringListConverter;
import com.happyhappyyay.landscaperecord.dao_interface.CustomerDao;
import com.happyhappyyay.landscaperecord.dao_interface.LogDao;
import com.happyhappyyay.landscaperecord.dao_interface.UserDao;
import com.happyhappyyay.landscaperecord.dao_interface.WorkDayDao;
import com.happyhappyyay.landscaperecord.database_interface.DatabaseOperator;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;

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
