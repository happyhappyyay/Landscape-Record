package com.happyhappyyay.landscaperecord;

import com.happyhappyyay.landscaperecord.pojo.WorkDay;
import com.happyhappyyay.landscaperecord.utility.Util;

import junit.framework.Assert;

import org.junit.Test;

public class DateUnitTest {

    @Test
    public void calendar_date_selection_isCorrect() {
        String expected = "09/10/2000";
//      month input starts at 0
        int month = 8;
        int dayOfMonth = 10;
        int year = 2000;
        String monthString;
        String dayOfMonthString;

        if (month < 9) {
            monthString = "0" + (month + 1);
        }
        else {
            monthString = Integer.toString(month + 1);
        }
        if (dayOfMonth <= 9) {
            dayOfMonthString = "0" + dayOfMonth;
        }
        else
        {
            dayOfMonthString = Integer.toString(dayOfMonth);
        }
        String date = monthString + "/" + dayOfMonthString + "/" + year;
        Assert.assertEquals(expected, date);
    }

    @Test
    public void calendar_date_selection_isIncorrect() {
        String expected = "12/01/2000";
//      month input starts at 0
        int month = 12;
        int dayOfMonth = 1;
        int year = 2000;
        String monthString;
        String dayOfMonthString;

        if (month < 9) {
            monthString = "0" + (month + 1);
        }
        else {
            monthString = Integer.toString(month + 1);
        }
        if (dayOfMonth <= 9) {
            dayOfMonthString = "0" + dayOfMonth;
        }
        else
        {
            dayOfMonthString = Integer.toString(dayOfMonth);
        }
        String date = monthString + "/" + dayOfMonthString + "/" + year;
        Assert.assertEquals(expected, date);
    }

    @Test
    public void add_user_hours_isCorrect() {
        String expected = "0 : 8, 1 : 11, 2 : 15, ";
        WorkDay workDay = new WorkDay(Util.retrieveStringCurrentDate());
        workDay.addUserHourReference("0", 5);
        workDay.addUserHourReference("1", 10);
        workDay.addUserHourReference("2", 15);
        workDay.addUserHourReference("0",2);
        workDay.addUserHourReference("1",1);
        workDay.addUserHourReference("0",1);
        StringBuilder sb = new StringBuilder();
        for(String s: workDay.retrieveUsersHoursAsString()) {
            sb.append(s + ", ");
        }
        Assert.assertEquals(expected, sb.toString());

    }

}
