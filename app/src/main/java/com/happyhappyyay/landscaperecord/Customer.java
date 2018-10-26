package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;

import java.util.ArrayList;
import java.util.List;

@Entity
public class Customer implements DatabaseObjects<Customer> {
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
    private String customerState;

    @TypeConverters(PaymentTypeConverter.class)
    private Payment payment;

    @TypeConverters(DataTypeConverter.class)
    private List<Service> customerServices;

    public Customer(String customerFirstName, String customerLastName, String customerAddress) {
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerAddress = customerAddress;
        customerServices = new ArrayList<>();
    }

    @Ignore
    public Customer() {
        customerServices = new ArrayList<>();
    }

    public void addService(Service customerService) {
        customerServices.add(customerService);
    }

    public void updateService(Service customerService, int indexPosition) {
        customerServices.set(indexPosition, customerService);
    }

    public void removeService(Service customerService) {
        customerServices.remove(customerService);
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

    public List<Service> getCustomerServices() {
        return customerServices;
    }

    public void setCustomerServices(List<Service> customerServices) {
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

    public String getCustomerState() {
        return customerState;
    }

    public void setCustomerState(String customerState) {
        this.customerState = customerState;
    }

    public void setCustomerFirstName(String customerFirstName) {
        this.customerFirstName = customerFirstName;
    }

    public void setCustomerLastName (String customerLastName) {
        this.customerLastName = customerLastName;
    }

    public void setCustomerAddress(String customerAddress) {
        this.customerAddress = customerAddress;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    @Override
    public String toString() {
        return customerBusiness == null ? customerFirstName + customerLastName : customerBusiness;
    }

    @Override
    public String getName() {
        return toString();
    }

    @Override
    public List<Customer> retrieveAllClassInstancesFromDatabase(AppDatabase db) {
        return db.customerDao().getAllCustomers();
    }

    @Override
    public Customer retrieveClassInstanceFromDatabaseID(AppDatabase db, int id) {
        return db.customerDao().findCustomerByID(id);
    }

    @Override
    public Customer retrieveClassInstanceFromDatabaseString(AppDatabase db, String string) {
        return null;
    }

    @Override
    public void deleteClassInstanceFromDatabase(Customer objectToDelete, AppDatabase db) {
        db.customerDao().deleteCustomer(objectToDelete);
    }

    @Override
    public void updateClassInstanceFromDatabase(Customer objectToUpdate, AppDatabase db) {
        db.customerDao().updateCustomer(objectToUpdate);
    }

    @Override
    public void insertClassInstanceFromDatabase(Customer objectToInsert, AppDatabase db) {
        db.customerDao().insert(objectToInsert);
    }

    public String concatenateFullAddress() {
        return getCustomerAddress() + " " + getCustomerCity()
                + ", " + getCustomerState();
    }
}
