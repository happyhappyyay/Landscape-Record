package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.content.Context;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

@Entity
public class WorkDay {
    @PrimaryKey (autoGenerate = true)
    private int workDayID;
    private String dayOfWeek;
    private String Month;
    private String Year;
    private String currentDate;
    private long currentDateAsTime;
    @TypeConverters(IntegerListConverter.class)
    private List<Integer> hours;
    private List<Integer> userReference;
    private List<Service> services;
    private long weekInMilli;
    private long monthInMilli;
    private long yearInMilli;

    public WorkDay () {
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        currentDate = dateFormat.format(new Date(System.currentTimeMillis()));

        try {
            date = dateFormat.parse(currentDate);
            currentDateAsTime = date.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        services = new ArrayList<>();
        hours = new ArrayList<>();
        userReference = new ArrayList<>();
        findCalendarInformation();
    }

    public void addUserHourReference (int userReference, int hours) {
        this.hours.add(hours);
        this.userReference.add(userReference);

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

    private void findCalendarInformation() {
        Calendar cal = Calendar.getInstance();
        cal = clearCalendar(cal);
//TODO: Allow setting of first day of the week (default is sunday)
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        weekInMilli = cal.getTimeInMillis();
        Date date = new Date(weekInMilli);
        dayOfWeek = new SimpleDateFormat("EEEE", Locale.US).format(date);

        cal = clearCalendar(cal);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        monthInMilli = cal.getTimeInMillis();
        date = new Date(monthInMilli);
        Month = new SimpleDateFormat("MMMM", Locale.US).format(date);


        cal = clearCalendar(cal);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        yearInMilli = cal.getTimeInMillis();
        date = new Date(yearInMilli);
        Year = new SimpleDateFormat("yyyy", Locale.US).format(date);

    }
    private Calendar clearCalendar(Calendar cal) {
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        return cal;
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

    public List<Integer> getUserReference() {
        return userReference;
    }

    public void setUserReference(List<Integer> userReference) {
        this.userReference = userReference;
    }
}
