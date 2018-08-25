package com.happyhappyyay.landscaperecord;

import org.junit.Assert;
import org.junit.Test;

public class ServicePriceUnitTest {

    @Test
    public void service_price_add_service_isCorrect() {
        String expected = "CUT pRunE spray  50.0 -5.0 299.53 344.53";
        ServicePrice servicePrice = new ServicePrice();
        servicePrice.addServicePrice("CUT", 20.00);
        servicePrice.addServicePrice("pRunE", -5.00);
        servicePrice.addServicePrice("spray", 299.53);
        servicePrice.addServicePrice("cut", 50.00);

        StringBuilder stringBuilder = new StringBuilder("");
        for(String s: servicePrice.getServices()) {
            stringBuilder.append(s + " ");
        }

        double totalPrice = 0;
        for(double d: servicePrice.getPrices()) {
            totalPrice += d;
            stringBuilder.append(" " + d);
        }

        stringBuilder.append(" " + totalPrice);

        String actual = stringBuilder.toString();

        Assert.assertEquals(expected, actual);
    }
}
