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
    @TypeConverters(IntArrayConverter.class)
    private int[][] userHourReference;
    private List<Service> services;
    private long weekInMilli;
    private long monthInMilli;
    private long yearInMilli;

    public WorkDay () {
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        currentDate = dateFormat.format(new Date(System.currentTimeMillis()));
        String TAG = "WorkDay";
        Log.d(TAG, currentDate);

        try {
            date = dateFormat.parse(currentDate);
            currentDateAsTime = date.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        userHourReference = new int[0][0];
        services = new ArrayList<>();
        findCalendarInformation();
    }

    private static WorkDay instance;

    public static WorkDay getWorkDay(Context context) {
        if (instance == null) {
            instance = new WorkDay();
        }
        return instance;
    }

    public void addUserHourReference (int userReference, int hours) {
        final int USER_ROW = 0;
        final int HOUR_ROW = 1;
        final int NUMBER_OF_ROWS = 2;
        if (userHourReference.length > 0) {
            int[][] tempArray;
            int tempPositionMarker = -1;
            for (int h = 0; h < userHourReference[USER_ROW].length; h++) {
                if (userHourReference[USER_ROW][h] == userReference) {
                    tempPositionMarker = h;
                }
            }
            if (tempPositionMarker == -1) {
                String TAG = "Inside";
                tempArray = new int[NUMBER_OF_ROWS][userHourReference[USER_ROW].length + 1];
                for (int i = 0; i < userHourReference.length; i++) {
                    for (int j = 0; j < userHourReference[i].length; j++) {
                        tempArray[i][j] = userHourReference[i][j];
                    }
                }
                Log.d(TAG, "Size of array " + tempArray.length + tempArray[0].length);
                tempArray[USER_ROW][userHourReference[USER_ROW].length] = userReference;
                tempArray[HOUR_ROW][userHourReference[USER_ROW].length] = hours;
            }
            else {
                tempArray = new int[userHourReference.length][userHourReference[USER_ROW].length];
                for (int i = 0; i < userHourReference.length; i++) {
                    for (int j = 0; j < userHourReference[i].length; j++) {
                        tempArray[i][j] = userHourReference[i][j];
                    }
                }
                tempArray[HOUR_ROW][tempPositionMarker] = tempArray[HOUR_ROW][tempPositionMarker] + hours;
            }
            userHourReference = new int[tempArray.length][tempArray[HOUR_ROW].length];
            for (int i = 0; i < tempArray.length; i++) {
                for (int j = 0; j < tempArray[i].length; j++) {
                    userHourReference[i][j] = tempArray[i][j];
                }
            }
        }
        else {
            userHourReference = new int[NUMBER_OF_ROWS][1];
            userHourReference[USER_ROW][0] = userReference;
            userHourReference[HOUR_ROW][0] = hours;
        }
    }

    public void addServices(Service service) {
        services.add(service);
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public int[][] getUserHourReference() {
        return userHourReference;
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

    public void setUserHourReference(int[][] userHourReference) {
        this.userHourReference = userHourReference;
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
}
