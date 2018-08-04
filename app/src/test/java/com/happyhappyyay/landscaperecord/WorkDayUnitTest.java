package com.happyhappyyay.landscaperecord;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
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
        List<String> actual = day.convertCustomerServicesToString(services);
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
        day.setUsers(users);
        WorkDay workDay = new WorkDay();
        for (int i = 0; i < users.size(); i++) {
            workDay.addUserHourReference(i, 2*(i+1));
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
        day.setUsers(users);
        List<WorkDay> workDays = new ArrayList<>();
        for (int i = 0; i < users.size(); i++) {
            WorkDay workDay = new WorkDay();
            for (int j = 0; j < users.size(); j++) {
                workDay.addUserHourReference(j, j);
            }
            workDays.add(workDay);
        }
        List<String> actual = day.createStringFromUserHourReferences(workDays);
        Assert.assertEquals(expected,actual);
    }
}
