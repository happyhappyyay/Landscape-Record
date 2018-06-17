package com.example.kingdenis.landscaperecord;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;

import java.util.ArrayList;

@Entity
public class Customer implements SpinnerObjects {
    @PrimaryKey(autoGenerate = true)
    private int customerId;
    private String customerFirstName;
    private String customerLastName;
    private String customerAddress;
    private String customerCity;
    private String customerPhoneNumber;
    private String customerEmail;
    private String customerBusiness;
    private String customerDay;

    @Embedded
    private ArrayList<Service> customerServices;

    public Customer(String customerFirstName, String customerLastName, String customerAddress) {
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerAddress = customerAddress;
        customerServices = new ArrayList<>();
    }

    public void addService(Service customerService) {
        customerServices.add(customerService);
    }

    public void removeService(Service customerService) {
        if (customerServices.contains(customerService)) {
            customerServices.remove(customerService);
        }
    }

    public String getCustomerFirstName() {
        return customerFirstName;
    }

    public String getCustomerLastName() {
        return customerLastName;
    }

    public String getCustomerAddress() {
        return customerAddress;
    }

    public String getCustomerPhoneNumber() {
        return customerPhoneNumber;
    }

    public void setCustomerPhoneNumber(String customerPhoneNumber) {
        this.customerPhoneNumber = customerPhoneNumber;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public String getCustomerBusiness() {
        return customerBusiness;
    }

    public void setCustomerBusiness(String customerBusiness) {
        this.customerBusiness = customerBusiness;
    }

    public String getCustomerCity() {
        return customerCity;
    }

    public void setCustomerCity(String customerCity) {
        this.customerCity = customerCity;
    }

    public ArrayList<Service> getCustomerServices() {
        return customerServices;
    }

    public void setCustomerServices(ArrayList<Service> customerServices) {
        this.customerServices = customerServices;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getCustomerDay() {
        return customerDay;
    }

    public void setCustomerDay(String customerDay) {
        this.customerDay = customerDay;
    }

    @Override
    public String toString() {
        if (customerBusiness == null) {
            return customerFirstName + " " + customerLastName;
        } else {
            return customerBusiness;
        }

    }

    @Override
    public String getName() {
        return toString();
    }
}
