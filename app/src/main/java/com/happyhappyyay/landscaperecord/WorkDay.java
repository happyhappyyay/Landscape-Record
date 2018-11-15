package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
public class WorkDay implements DatabaseObjects<WorkDay> {
    @PrimaryKey (autoGenerate = true)
    private int workDayID;
    private String dayOfWeek;
    private String Month;
    private String Year;
    private String currentDate;
    private long currentDateAsTime;
    @TypeConverters(IntegerListConverter.class)
    private List<Integer> hours;
    @TypeConverters(StringListConverter.class)
    private List<String> userReference;
    private List<Service> services;
    private long weekInMilli;
    private long monthInMilli;
    private long yearInMilli;

    public WorkDay (String currentDate) {
        services = new ArrayList<>();
        hours = new ArrayList<>();
        userReference = new ArrayList<>();
        findCalendarInformation(currentDate);
    }

    public void addUserHourReference (String userReference, int hours) {
        boolean existingReference = false;
        for (int i = 0; i < this.userReference.size(); i++) {
            if(userReference.equals(this.userReference.get(i))) {
                this.hours.set(i, this.hours.get(i) + hours);
                existingReference = true;
                break;
            }
        }
        if(!existingReference) {
            this.hours.add(hours);
            this.userReference.add(userReference);
        }
    }

    public void addServices(Service service) {
        services.add(service);
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public List<Service> getServices() {
        return services;
    }

    protected void findCalendarInformation(String newDate) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = dateFormat.parse(newDate);
            currentDateAsTime = date.getTime();
            currentDate = newDate;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            weekInMilli = cal.getTimeInMillis();
            dayOfWeek = new SimpleDateFormat("EEEE", Locale.US).format(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            monthInMilli = cal.getTimeInMillis();
            Month = new SimpleDateFormat("MMMM", Locale.US).format(date);
            cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
            yearInMilli = cal.getTimeInMillis();
            Year = new SimpleDateFormat("yyyy", Locale.US).format(date);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getWeekInMilli() {
        return weekInMilli;
    }

    public long getMonthInMilli() {
        return monthInMilli;
    }

    public long getYearInMilli() {
        return yearInMilli;
    }

    public boolean startOfTheWeek() {
        return currentDateAsTime == weekInMilli;
    }

    public boolean startOfTheMonth() {
        return currentDateAsTime == monthInMilli;
    }

    public boolean startOfTheYear() {
        return currentDateAsTime == yearInMilli;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public int getWorkDayID() {
        return workDayID;
    }

    public void setWorkDayID(int workDayID) {
        this.workDayID = workDayID;
    }

    public long getCurrentDateAsTime() {
        return currentDateAsTime;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public void setCurrentDateAsTime(long currentDateAsTime) {
        this.currentDateAsTime = currentDateAsTime;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void setWeekInMilli(long weekInMilli) {
        this.weekInMilli = weekInMilli;
    }

    public void setMonthInMilli(long monthInMilli) {
        this.monthInMilli = monthInMilli;
    }

    public void setYearInMilli(long yearInMilli) {
        this.yearInMilli = yearInMilli;
    }

    public String getMonth() {
        return Month;
    }

    public String getYear() {
        return Year;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public void setYear(String year) {
        Year = year;
    }

    public List<Integer> getHours() {
        return hours;
    }

    public void setHours(List<Integer> hours) {
        this.hours = hours;
    }

    public List<String> getUserReference() {
        return userReference;
    }

    public void setUserReference(List<String> userReference) {
        this.userReference = userReference;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<WorkDay> retrieveAllClassInstancesFromDatabase(AppDatabase db) {
        return db.workDayDao().getAllWorkDays();
    }

    @Override
    public WorkDay retrieveClassInstanceFromDatabaseID(AppDatabase db, int time) {
        return db.workDayDao().findWorkDayByTime(time);
    }

    @Override
    public WorkDay retrieveClassInstanceFromDatabaseString(AppDatabase db, String date) {
        return db.workDayDao().findWorkDayByDate(date);
    }

    @Override
    public void deleteClassInstanceFromDatabase(WorkDay objectToDelete, AppDatabase db) {
        db.workDayDao().deleteWorkDay(objectToDelete);
    }

    @Override
    public void updateClassInstanceFromDatabase(WorkDay objectToUpdate, AppDatabase db) {
        db.workDayDao().updateWorkDay(objectToUpdate);
    }

    @Override
    public void insertClassInstanceFromDatabase(WorkDay objectToInsert, AppDatabase db) {
        db.workDayDao().insert(objectToInsert);
    }

    public List<WorkDay> retrieveClassInstancesFromDatabaseByWeek(AppDatabase db, long weekInMilli) {
        return db.workDayDao().findWorkWeekByTime(weekInMilli);
    }

    public List<WorkDay> retrieveClassInstancesFromDatabaseByMonth(AppDatabase db, long monthInMilli) {
        return db.workDayDao().findWorkMonthByTime(monthInMilli);
    }

}
