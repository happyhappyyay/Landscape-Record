package com.happyhappyyay.landscaperecord;

import java.util.ArrayList;
import java.util.List;

public class Payment {
    private List<String> services;
    private List<Double> prices;
    private List<String> paymentReceiptDates;
    private List<Double> paymentReceiptAmount;
    private List<String> checkNumbers;
    private Double amountPaid;
    private Double amountRemaining;

    public Payment() {
    services = new ArrayList<>();
    prices = new ArrayList<>();
    paymentReceiptDates = new ArrayList<>();
    paymentReceiptAmount = new ArrayList<>();
    checkNumbers = new ArrayList<>();
    amountPaid = 0.0;
    amountRemaining = 0.0;
    }

    public void addServicePrice(String service, Double price) {
        int servicePosition = findServicePosition(service);
        if (servicePosition != -1) {
            prices.set(servicePosition, price);
            amountRemaining += price;
        }
        else {
            prices.add(price);
            services.add(service);
            amountRemaining += price;
        }
    }

    private boolean doesServiceExist(String service) {
        int servicePosition = findServicePosition(service);

        return servicePosition != -1;
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

    public double checkServiceForPrice(String service) {
        if (doesServiceExist(service)) {
            return prices.get(findServicePosition(service));
        }
        return 0;
    }

    private int findServicePosition(String service) {
        for(int i = 0; i < services.size(); i++) {
            if(service.toLowerCase().equals(services.get(i).toLowerCase())) {
                return i;
            }
        }
        return -1;
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

    public List<String> getServices() {
        return services;
    }

    public List<Double> getPrices() {
        return prices;
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

    public List<String> getPaymentReceiptDates() {
        return paymentReceiptDates;
    }

    public List<Double> getPaymentReceiptAmount() {
        return paymentReceiptAmount;
    }
}
