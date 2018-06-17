package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;

import java.util.List;

@Dao
public interface LogDao {

    @Insert
    void insert(LogActivity... logs);

    @Query("SELECT * FROM LogActivity")
    List<LogActivity> getAllLogs();

    @Delete
    void deleteCustomer(Customer customer);

    @Query("DELETE FROM LogActivity")
    void deleteAllLogs();

}
