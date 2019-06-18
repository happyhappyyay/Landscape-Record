package com.happyhappyyay.landscaperecord;

import com.happyhappyyay.landscaperecord.utility.Util;

import org.junit.Assert;
import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

public class WorkDayUnitTest {
    @Test
    public void create_week_isCorrect() {
        String expected = "07/29/2018";
        Calendar cal = Calendar.getInstance();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        long weekInMilli = cal.getTimeInMillis();
        String dates = Util.convertLongToStringDate(weekInMilli);

        Assert.assertEquals(expected, dates);
    }

    @Test
    public void create_week_alter_isCorrect() {
        String expected = "08/05/2018";
        Calendar cal = Calendar.getInstance();
        Date date = new Date(2018-1900,7,11);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        long weekInMilli = cal.getTimeInMillis();
        String dates = Util.convertLongToStringDate(weekInMilli);

        Assert.assertEquals(expected, dates);
    }

    @Test
    public void create_week_month_year_isCorrect() {
        String expected = "08/19/2018 08/01/2018 01/01/2018 ";
        Calendar cal = Calendar.getInstance();
        Date date = new Date(2018-1900,7,20);
        cal.setTime(date);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.clear(Calendar.MINUTE);
        cal.clear(Calendar.SECOND);
        cal.clear(Calendar.MILLISECOND);
        cal.setTime(date);
        cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
        long dateInMilli = cal.getTimeInMillis();
        String dates = Util.convertLongToStringDate(dateInMilli) + " ";
        cal.set(Calendar.DAY_OF_MONTH, cal.getFirstDayOfWeek());
        dateInMilli = cal.getTimeInMillis();
        dates += Util.convertLongToStringDate(dateInMilli) + " ";
        cal.set(Calendar.DAY_OF_YEAR, cal.getFirstDayOfWeek());
        dateInMilli = cal.getTimeInMillis();
        dates += Util.convertLongToStringDate(dateInMilli) + " ";

        Assert.assertEquals(expected, dates);
    }
}
