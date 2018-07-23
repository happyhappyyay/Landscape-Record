package com.happyhappyyay.landscaperecord;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkDay {
    private String dayOfWeek;
    private String currentDate;
    private long currentDateAsTime;
    private int[][] userHourReference;
    private List<Service> services;
    private long weekInMilli;
    private long monthInMilli;
    private long yearInMilli;

    public WorkDay () {
        Date date;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        currentDate =dateFormat.format(new Date(System.currentTimeMillis()));

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

    public void addUserHourReference (int userReference, int hours) {
        final int USER_ROW = 0;
        final int HOUR_ROW = 1;
        if (userHourReference.length > 0) {
            int[][] tempArray;
            int tempPositionMarker = -1;
            for (int h = 0; h < userHourReference[USER_ROW].length; h++) {
                if (userHourReference[USER_ROW][h] == userReference) {
                    tempPositionMarker = h;
                }
            }
            if (tempPositionMarker == -1) {
                tempArray = new int[userHourReference.length][userHourReference[USER_ROW].length + 1];
                System.arraycopy(userHourReference, 0, tempArray, 0, userHourReference.length);
                tempArray[USER_ROW][userHourReference[USER_ROW].length + 1] = userReference;
                tempArray[HOUR_ROW][userHourReference[USER_ROW].length + 1] = hours;
            }
            else {
                tempArray = new int[userHourReference.length][userHourReference[USER_ROW].length];
                System.arraycopy(userHourReference, 0, tempArray, 0, userHourReference.length);
                tempArray[HOUR_ROW][tempPositionMarker] = tempArray[HOUR_ROW][tempPositionMarker] + hours;
            }
            userHourReference = new int[tempArray.length][tempArray[HOUR_ROW].length];
            System.arraycopy(tempArray, 0, userHourReference, 0, tempArray.length);
        }
        else {
            userHourReference = new int[2][1];
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
        dayOfWeek = new SimpleDateFormat("EE", Locale.US).format(date);

        cal = clearCalendar(cal);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        monthInMilli = cal.getTimeInMillis();

        cal = clearCalendar(cal);
        cal.set(Calendar.DAY_OF_YEAR, 1);
        yearInMilli = cal.getTimeInMillis();

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
}
