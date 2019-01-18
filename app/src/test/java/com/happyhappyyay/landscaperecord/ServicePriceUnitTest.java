package com.happyhappyyay.landscaperecord;

import org.junit.Assert;
import org.junit.Test;

public class ServicePriceUnitTest {

    @Test
    public void service_price_add_service_isCorrect() {
        String expected = "CUT $20.0 pRunE $-5.0 spray $299.53 cut $50.0 ";
        Payment servicePrice = new Payment();
        servicePrice.addServicePrice("CUT", 20.00);
        servicePrice.addServicePrice("pRunE", -5.00);
        servicePrice.addServicePrice("spray", 299.53);
        servicePrice.addServicePrice("cut", 50.00);

        StringBuilder stringBuilder = new StringBuilder();
        for(String s: servicePrice.retrieveAllServicePrices()) {
            stringBuilder.append(s + " ");
        }

        String actual = stringBuilder.toString();

        Assert.assertEquals(expected, actual);
    }

    @Test
    public void service_price_pay_for_service_isCorrect() {
        Payment servicePrice = new Payment();
        String expected = Util.retrieveStringCurrentDate() + " $20.0" + System.getProperty ("line.separator") +
                Util.retrieveStringCurrentDate() + " $100.0" + System.getProperty ("line.separator") +
                Util.retrieveStringCurrentDate() + " $245.0" + System.getProperty ("line.separator");
                servicePrice.addServicePrice("CUT", 20.00);
        servicePrice.addServicePrice("pRunE", -5.00);
        servicePrice.addServicePrice("spray", 299.53);
        servicePrice.addServicePrice("cut", 50.00);

        servicePrice.payForServices(20, Util.retrieveStringCurrentDate());
        servicePrice.payForServices(100, Util.retrieveStringCurrentDate());
        servicePrice.payForServices(245, Util.retrieveStringCurrentDate());
        StringBuilder stringBuilder = new StringBuilder();
        for(String s: servicePrice.retrieveAllPaymentReceipts()) {
            stringBuilder.append(s + System.getProperty ("line.separator"));
        }
        String actual = stringBuilder.toString();

        Assert.assertEquals(expected, actual);
    }
}
