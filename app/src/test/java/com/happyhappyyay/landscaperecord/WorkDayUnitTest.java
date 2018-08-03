package com.happyhappyyay.landscaperecord;

import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

public class WorkDayUnitTest {
    @Test
    public void service_string_conversion_isCorrect() {
        List<String> expected = new ArrayList<>();
        expected.add("George" + System.lineSeparator() + "eat a dog");
        Service service = new Service();
        service.setServices("eat a dog#*#");
        service.setCustomerName("George");
        List<Service> services = new ArrayList<>();
        services.add(service);
        ViewWorkDay day = new ViewWorkDay();
        List<String> actual = day.convertCustomerServicesToString(services);
        Assert.assertEquals(expected, actual);
    }
}
