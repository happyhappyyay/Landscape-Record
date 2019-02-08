package com.happyhappyyay.landscaperecord;

import com.happyhappyyay.landscaperecord.activity.ViewWorkDay;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;
import com.happyhappyyay.landscaperecord.utility.Util;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class WorkDayUnitTest {
    @Test
    public void service_string_conversion_isCorrect() {
        List<String> expected = new ArrayList<>();
        expected.add("George" + System.lineSeparator() + "eat a banana");
        Service service = new Service();
        service.setServices("eat a banana#*#");
        service.setCustomerName("George");
        List<Service> services = new ArrayList<>();
        services.add(service);
        ViewWorkDay day = new ViewWorkDay();
        List<String> actual = day.removeCustomerServicesStopCharacters(services);
        Assert.assertEquals(expected, actual);
    }

    @Test
    public void create_string_from_user_hours_isCorrect() {
        List<String> expected = Arrays.asList("Bob : 2", "Mike : 4", "John : 6");
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setName("Bob");
        users.add(user);
        user = new User();
        user.setName("Mike");
        users.add(user);
        user = new User();
        user.setName("John");
        users.add(user);
        ViewWorkDay day = new ViewWorkDay();
        WorkDay workDay = new WorkDay(Util.retrieveStringCurrentDate());
        for (int i = 0; i < users.size(); i++) {
            workDay.addUserHourReference(users.get(i).getName(), 2*(i+1));
        }
        List<String> actual = day.createStringFromUserHourReferences(workDay);


        Assert.assertEquals(expected, actual);
    }

    @Test
    public void create_string_from_user_hours_list_isCorrect() {
        List<String> expected = Arrays.asList("Bob : 0", "Mike : 3", "John : 6");
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setName("Bob");
        users.add(user);
        user = new User();
        user.setName("Mike");
        users.add(user);
        user = new User();
        user.setName("John");
        users.add(user);
        ViewWorkDay day = new ViewWorkDay();
        List<WorkDay> workDays = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            WorkDay workDay = new WorkDay(Util.retrieveStringCurrentDate());
            for (int j = 0; j < users.size(); j++) {
                workDay.addUserHourReference(users.get(j).getName(), j);
            }
            workDays.add(workDay);
        }
        List<String> actual = day.createStringFromUserHourReferences(workDays);
        Assert.assertEquals(expected,actual);
    }

    @Test
    public void create_string_from_user_hours_simulation_isCorrect() {
        List<String> expected = Arrays.asList("Bob : 2");
        List<User> users = new ArrayList<>();
        User user = new User();
        user.setName("Bob");
        users.add(user);
        ViewWorkDay day = new ViewWorkDay();
        WorkDay workDay = new WorkDay(Util.retrieveStringCurrentDate());
        for (int i = 0; i < users.size(); i++) {
            workDay.addUserHourReference(users.get(i).getName(), 2*(i+1));
        }
        List<String> actual = day.createStringFromUserHourReferences(workDay);


        Assert.assertEquals(expected, actual);
    }
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
