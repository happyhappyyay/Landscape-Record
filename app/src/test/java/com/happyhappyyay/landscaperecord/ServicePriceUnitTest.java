package com.happyhappyyay.landscaperecord;

import org.junit.Assert;
import org.junit.Test;

public class ServicePriceUnitTest {

    @Test
    public void service_price_add_service_isCorrect() {
        String expected = "CUT pRunE spray  50.0 -5.0 299.53 344.53";
        Payment servicePrice = new Payment();
        servicePrice.addServicePrice("CUT", 20.00);
        servicePrice.addServicePrice("pRunE", -5.00);
        servicePrice.addServicePrice("spray", 299.53);
        servicePrice.addServicePrice("cut", 50.00);

        StringBuilder stringBuilder = new StringBuilder();
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

    @Test
    public void service_price_pay_for_service_isCorrect() {
        Payment servicePrice = new Payment();
        String expected = "10/06/2018 $20.0" + System.getProperty ("line.separator") +
                "10/06/2018 $100.0" + System.getProperty ("line.separator") +
                "10/06/2018 $245.0" + System.getProperty ("line.separator");
                servicePrice.addServicePrice("CUT", 20.00);
        servicePrice.addServicePrice("pRunE", -5.00);
        servicePrice.addServicePrice("spray", 299.53);
        servicePrice.addServicePrice("cut", 50.00);

        servicePrice.payForServices(20, Util.retrieveStringCurrentDate());
        servicePrice.payForServices(100, Util.retrieveStringCurrentDate());
        servicePrice.payForServices(245, Util.retrieveStringCurrentDate());
        String actual = servicePrice.retrieveAllPaymentReceipts();

        Assert.assertEquals(expected, actual);
    }
}
