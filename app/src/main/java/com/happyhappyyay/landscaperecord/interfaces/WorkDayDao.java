package com.happyhappyyay.landscaperecord.interfaces;

import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.happyhappyyay.landscaperecord.pojo.WorkDay;

import java.util.List;

@Dao
public interface WorkDayDao {

    @Insert
    void insert(WorkDay... workDays);

    @Query("SELECT * FROM workDay")
    List<WorkDay> getAllWorkDays();

    @Query("SELECT * FROM WorkDay WHERE id = :workDayId")
    WorkDay findWorkDayById(String workDayId);

    @Query("SELECT * FROM WorkDay WHERE date = :date")
    WorkDay findWorkDayByDate(String date);

    @Query("SELECT * FROM workDay WHERE dateTime = :time")
    WorkDay findWorkDayByTime(long time);

    @Query("SELECT * FROM workDay WHERE week = :time")
    List<WorkDay> findWorkWeekByTime(long time);

    @Query("SELECT * FROM workDay WHERE month = :time")
    List<WorkDay> findWorkMonthByTime(long time);

    @Query("SELECT * FROM WorkDay WHERE modifiedTime > :modifiedTime")
    List<WorkDay> getNewlyModifiedWorkDays(long modifiedTime);

    @Update
    void updateWorkDay(WorkDay workDay);

    @Delete
    void deleteWorkDay(WorkDay workDay);
}
