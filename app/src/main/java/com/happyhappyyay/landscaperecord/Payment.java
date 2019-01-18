package com.happyhappyyay.landscaperecord;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Payment {
    private Map<String, Double> serviceDefaultPricing;
    private List<String> paymentReceiptDates;
    private List<Double> paymentReceiptAmount;
    private Map<String, Double> servicesPriced;
    private List<String> checkNumbers;
    private Double amountPaid;
    private Double amountRemaining;

    public Payment() {
    paymentReceiptDates = new ArrayList<>();
    paymentReceiptAmount = new ArrayList<>();
    checkNumbers = new ArrayList<>();
    amountPaid = 0.0;
    amountRemaining = 0.0;
    serviceDefaultPricing = new LinkedHashMap<>();
    servicesPriced = new LinkedHashMap<>();
    }

    public void addServicePrice(String service, Double price) {
        if(serviceDefaultPricing.containsKey(service)){
            servicesPriced.put(service, price);
            amountRemaining += price;
        }
        else {
            serviceDefaultPricing.put(service, price);
            servicesPriced.put(service, price);
            amountRemaining += price;
        }
    }

    public void addServicePrice(String service, Double price, boolean override) {
        if(serviceDefaultPricing.containsKey(service)){
            if(override) {
                serviceDefaultPricing.put(service, price);
            }
            servicesPriced.put(service, price);
            amountRemaining += price;
        }
        else {
            serviceDefaultPricing.put(service, price);
            servicesPriced.put(service, price);
            amountRemaining += price;
        }
    }

    public List<String> retrieveAllPaymentReceipts() {
        String paymentInformation = "";
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < paymentReceiptDates.size(); i++) {
            paymentInformation = paymentReceiptDates.get(i) + " $" + paymentReceiptAmount.get(i);
            strings.add(paymentInformation);
        }
        return strings;
    }

    public List<String> retrieveAllServicePrices() {
        List<String> strings = new ArrayList<>();
        Set< Map.Entry<String, Double> > mapSet = servicesPriced.entrySet();
        int count = 0;

        for (Map.Entry< String, Double> mapEntry:mapSet)
        {
            String priceEntry = mapEntry.getKey() + " $" + mapEntry.getValue();
            strings.add(count, priceEntry);
            count++;
        }
        return strings;
    }

    public double returnServicePrice(String service) {
        if (servicesPriced.containsKey(service)) {
            return servicesPriced.get(service);
        }
        return 0;
    }

    public double returnDefaultServicePrice(String service) {
        if (serviceDefaultPricing.containsKey(service)) {
            return serviceDefaultPricing.get(service);
        }
        return 0;
    }

    public void payForServices(double amount, String date) {
        amountPaid += amount;
        amountRemaining -= amount;
        paymentReceiptAmount.add(amount);
        paymentReceiptDates.add(date);
        checkNumbers.add("CASH");
    }

    public void payForServices(double amount, String date, String checkNumber) {
        amountPaid += amount;
        amountRemaining -= amount;
        paymentReceiptAmount.add(amount);
        paymentReceiptDates.add(date);
        checkNumbers.add(checkNumber);
    }

    public List<String> getCheckNumbers() {
        return checkNumbers;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public Double getAmountRemaining() {
        return amountRemaining;
    }

    public List<Double> getPaymentReceiptAmount() {
        return paymentReceiptAmount;
    }

    public Map<String, Double> getServiceDefaultPricing() {
        return serviceDefaultPricing;
    }

    public void setServiceDefaultPricing(Map<String, Double> serviceDefaultPricing) {
        this.serviceDefaultPricing = serviceDefaultPricing;
    }

    public Map<String, Double> getServicesPriced() {
        return servicesPriced;
    }

    public void setServicesPriced(Map<String, Double> servicesPriced) {
        this.servicesPriced = servicesPriced;
    }
}
