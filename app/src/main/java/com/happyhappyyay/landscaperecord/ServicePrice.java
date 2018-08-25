package com.happyhappyyay.landscaperecord;

import java.util.ArrayList;
import java.util.List;

public class ServicePrice {
    private List<String> services;
    private List<Double> prices;

    public ServicePrice() {
    services = new ArrayList<>();
    prices = new ArrayList<>();
    }

    public void addServicePrice(String service, Double price) {
        int servicePosition = findServicePosition(service);
        if (servicePosition != -1) {
            prices.set(servicePosition, price);
        }
        else {
            prices.add(price);
            services.add(service);
        }
    }

    private boolean doesServiceExist(String service) {
        int servicePosition = findServicePosition(service);

        return servicePosition != -1;
    }

    public double checkServiceForPrice(String service) {
        if (doesServiceExist(service)) {
            return findServicePosition(service);
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

    public List<String> getServices() {
        return services;
    }

    public List<Double> getPrices() {
        return prices;
    }
}
