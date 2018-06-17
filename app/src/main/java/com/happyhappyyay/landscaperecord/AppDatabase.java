package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {User.class, Customer.class, LogActivity.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
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


}
