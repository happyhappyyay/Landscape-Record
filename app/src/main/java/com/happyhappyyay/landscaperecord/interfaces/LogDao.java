package com.happyhappyyay.landscaperecord.interfaces;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.happyhappyyay.landscaperecord.pojo.LogActivity;

import java.util.List;

@Dao
public interface LogDao {

    @Insert
    void insert(LogActivity... logs);

    @Query("SELECT * FROM LogActivity")
    List<LogActivity> getAllLogs();

    @Query("SELECT * FROM LogActivity WHERE id > :logId")
    LogActivity getLogById(String logId);

    @Query("SELECT * FROM LogActivity WHERE modifiedTime > :modifiedTime AND logActivityAction == :logActivityAction")
    List<LogActivity> getNewlyModifiedActionLogs(long modifiedTime, int logActivityAction);

    @Query("SELECT * FROM LogActivity WHERE modifiedTime > :modifiedTime AND logActivityType == :logActivityType")
    List<LogActivity> getNewlyModifiedTypeLogs(long modifiedTime, int logActivityType);

    @Query("SELECT * FROM LogActivity WHERE modifiedTime > :modifiedTime")
    List<LogActivity> getNewlyModifiedLogs(long modifiedTime);

    @Query("SELECT * FROM LogActivity WHERE username == :username")
    List<LogActivity> getLogsByName(String username);

    @Query("SELECT * FROM LogActivity WHERE info LIKE :username OR username == :username")
    List<LogActivity> getLogsByActedUser(String username);

    @Delete
    void deleteLog(LogActivity log);
}
