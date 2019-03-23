package com.happyhappyyay.landscaperecord.pojo;

import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Payment {
    private Map<String, Double> defaultPrices;
    private List<String> dates;
    private List<Double> amounts;
    private Map<String, Double> servicePrices;
    private List<String> checkNumbers;
    private Double paid;
    private Double owed;

    public Payment() {
    dates = new ArrayList<>();
    amounts = new ArrayList<>();
    checkNumbers = new ArrayList<>();
    paid = 0.0;
    owed = 0.0;
    defaultPrices = new LinkedHashMap<>();
    servicePrices = new LinkedHashMap<>();
    }

    public void addServicePrice(String service, Double price) {
        if(defaultPrices.containsKey(service)){
            servicePrices.put(service, price);
            owed += price;
        }
        else {
            defaultPrices.put(service, price);
            servicePrices.put(service, price);
            owed += price;
        }
    }

    public void addServicePrice(String service, Double price, boolean override) {
        if(defaultPrices.containsKey(service)){
            if(override) {
                defaultPrices.put(service, price);
            }
            servicePrices.put(service, price);
            owed += price;
        }
        else {
            defaultPrices.put(service, price);
            servicePrices.put(service, price);
            owed += price;
        }
    }

    public void addDefaultServicePrice(String service, Double price){
            defaultPrices.put(service, price);
    }

    public List<String> retrieveAllPaymentReceipts() {
        String paymentInformation;
        List<String> strings = new ArrayList<>();
        for (int i = 0; i < dates.size(); i++) {
            String type = "Cash";
            if (!checkNumbers.get(i).equals("CASH")) {
                type = "Check";
            }
            paymentInformation = dates.get(i) + ":  $" + amounts.get(i) + " " + type;
            strings.add(paymentInformation);
        }
        return strings;
    }

    public List<String> retrieveAllServicePrices() {
        List<String> strings = new ArrayList<>();
        Set< Map.Entry<String, Double> > mapSet = servicePrices.entrySet();
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
        if (servicePrices.containsKey(service)) {
            return servicePrices.get(service);
        }
        return 0;
    }

    public double returnDefaultServicePrice(String service) {
        if (defaultPrices.containsKey(service)) {
            return defaultPrices.get(service);
        }
        return 0;
    }

    public void payForServices(double amount, String date) {
        int index = 0;
        if(dates.size() > 0) {
            index = sortPaymentReceiptsByDate(date);
        }
        paid += amount;
        owed -= amount;
        amounts.add(index, amount);
        dates.add(index, date);
        checkNumbers.add(index, "CASH");
    }

    public void payForServices(double amount, String date, String checkNumber) {
        int index = sortPaymentReceiptsByDate(date);
        paid += amount;
        owed -= amount;
        amounts.add(index, amount);
        dates.add(index, date);
        checkNumbers.add(index, checkNumber);
    }

    private int sortPaymentReceiptsByDate(String date) {
        long dateLong = Util.convertStringDateToMilliseconds(date);

        for (int i = 0; i < dates.size(); i++) {
            long receiptLong = Util.convertStringDateToMilliseconds(dates.get(i));
            if (dateLong > receiptLong) {
                return i;
            }
        }
        return dates.size();
    }

    public List<String> getCheckNumbers() {
        return checkNumbers;
    }

    public Double getPaid() {
        return paid;
    }

    public Double getOwed() {
        return owed;
    }

    public List<Double> getAmounts() {
        return amounts;
    }

    public Map<String, Double> getDefaultPrices() {
        return defaultPrices;
    }

    public void setDefaultPrices(Map<String, Double> defaultPrices) {
        this.defaultPrices = defaultPrices;
    }

    public Map<String, Double> getServicePrices() {
        return servicePrices;
    }

    public void setServicePrices(Map<String, Double> servicePrices) {
        this.servicePrices = servicePrices;
    }

    public List<String> getDates() {
        return dates;
    }

    public void setDates(List<String> dates) {
        this.dates = dates;
    }
}
