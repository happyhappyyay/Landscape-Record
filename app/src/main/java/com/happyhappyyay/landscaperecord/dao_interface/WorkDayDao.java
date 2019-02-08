package com.happyhappyyay.landscaperecord.dao_interface;

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

    @Query("SELECT * FROM WorkDay WHERE workDayId = :workDayId")
    WorkDay findWorkDayById(String workDayId);

    @Query("SELECT * FROM WorkDay WHERE currentDate = :date")
    WorkDay findWorkDayByDate(String date);

    @Query("SELECT * FROM workDay WHERE currentDateAsTime = :time")
    WorkDay findWorkDayByTime(long time);

    @Query("SELECT * FROM workDay WHERE weekInMilli = :time")
    List<WorkDay> findWorkWeekByTime(long time);

    @Query("SELECT * FROM workDay WHERE monthInMilli = :time")
    List<WorkDay> findWorkMonthByTime(long time);

    @Query("SELECT * FROM WorkDay WHERE modifiedTime > :modifiedTime")
    List<WorkDay> getNewlyModifiedWorkDays(long modifiedTime);

    @Update
    void updateWorkDay(WorkDay workDay);

    @Delete
    void deleteWorkDay(WorkDay workDay);

    @Query("DELETE FROM workDay")
    void deleteAllWorkDays();
}
