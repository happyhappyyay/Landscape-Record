package com.happyhappyyay.landscaperecord;

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

}
