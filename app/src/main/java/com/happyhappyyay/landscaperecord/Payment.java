package com.happyhappyyay.landscaperecord;

import java.util.ArrayList;
import java.util.List;

public class Payment {
    private List<String> services;
    private List<Double> prices;
    private List<String> paymentReceiptDates;
    private List<Double> paymentReceiptAmount;
    private Double amountPaid;
    private Double amountRemaining;

    public Payment() {
    services = new ArrayList<>();
    prices = new ArrayList<>();
    paymentReceiptDates = new ArrayList<>();
    paymentReceiptAmount = new ArrayList<>();
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

    public String retrieveAllPaymentReceipts() {
        StringBuilder paymentList = new StringBuilder();
        String paymentInformation = "";
        for (int i = 0; i < paymentReceiptDates.size(); i++) {
            paymentInformation = paymentReceiptDates.get(i) + " $" + paymentReceiptAmount.get(i) +
                    System.getProperty ("line.separator");
            paymentList.append(paymentInformation);
        }
        return paymentList.toString();
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
    }

    public List<String> getServices() {
        return services;
    }

    public List<Double> getPrices() {
        return prices;
    }

    public Double getAmountPaid() {
        return amountPaid;
    }

    public Double getAmountRemaining() {
        return amountRemaining;
    }
}
