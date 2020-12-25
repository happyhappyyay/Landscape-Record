package com.happyhappyyay.landscaperecord.utility;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import android.content.Context;

import com.happyhappyyay.landscaperecord.converter.IntegerListConverter;
import com.happyhappyyay.landscaperecord.converter.MapStringDoubleConverter;
import com.happyhappyyay.landscaperecord.converter.MapStringIntConverter;
import com.happyhappyyay.landscaperecord.converter.MapStringStringConverter;
import com.happyhappyyay.landscaperecord.converter.PaymentConverter;
import com.happyhappyyay.landscaperecord.converter.ServiceListConverter;
import com.happyhappyyay.landscaperecord.converter.StringListConverter;
import com.happyhappyyay.landscaperecord.interfaces.CustomerDao;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.interfaces.ExpenseDao;
import com.happyhappyyay.landscaperecord.interfaces.LogDao;
import com.happyhappyyay.landscaperecord.interfaces.UserDao;
import com.happyhappyyay.landscaperecord.interfaces.WorkDayDao;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Expense;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;

@Database(entities = {User.class, Customer.class, LogActivity.class, WorkDay.class, Expense.class}, version = 1)
@TypeConverters({ServiceListConverter.class, IntegerListConverter.class, PaymentConverter.class,
        StringListConverter.class, MapStringIntConverter.class, MapStringDoubleConverter.class, MapStringStringConverter.class})
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

    public abstract ExpenseDao expenseDao();
}
