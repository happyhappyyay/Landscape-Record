package com.happyhappyyay.landscaperecord.pojo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.happyhappyyay.landscaperecord.converter.PaymentConverter;
import com.happyhappyyay.landscaperecord.converter.ServiceListConverter;
import com.happyhappyyay.landscaperecord.database_interface.DatabaseObjects;
import com.happyhappyyay.landscaperecord.database_interface.DatabaseOperator;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.excludeId;

@Entity
public class Customer implements DatabaseObjects<Customer> {
    @PrimaryKey @NonNull
    private String customerId = UUID.randomUUID().toString();
    private String customerFirstName;
    private String customerLastName;
    private String customerAddress;
    private String customerCity;
    private String customerPhoneNumber;
    private String customerEmail;
    private String customerBusiness;
    private String customerDay;
    private String customerState;
    private Double customerMileage;
    private long modifiedTime;

    @TypeConverters(PaymentConverter.class)
    private Payment payment;

    @TypeConverters(ServiceListConverter.class)
    private List<Service> customerServices;

    public Customer(String customerFirstName, String customerLastName, String customerAddress) {
        this.customerFirstName = customerFirstName;
        this.customerLastName = customerLastName;
        this.customerAddress = customerAddress;
        customerServices = new ArrayList<>();
        payment = new Payment();
        modifiedTime = System.currentTimeMillis();
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

    public void updateService(Service existingService) {
        for(int i = 0; i < customerServices.size(); i++) {
            if (customerServices.get(i).getServiceID() == existingService.getServiceID()) {
                customerServices.set(i, existingService);
                break;
            }
        }

    }

    public List<String> retrieveServicesWithPrices() {
        List<String> servicesWithPrices = new ArrayList<>();
        for (int i = 0; i < customerServices.size(); i++) {
            String services = customerServices.get(i).getServices();
            Service service = customerServices.get(i);
            servicesWithPrices.add(Util.convertLongToStringDate(service.getEndTime()) + " " + services + " $ " + payment.returnServicePrice(services));
        }
        return servicesWithPrices;
    }

    public boolean hasUnpricedServicesForMonth(int month) {
        for (int i = 0; i < customerServices.size(); i++) {
            Service service = customerServices.get(i);
            int serviceMonth = Util.retrieveMonthFromLong(service.getEndTime());
            if(!service.isPriced() && serviceMonth == month) {
                return true;
            }
        }
        return false;
    }

    public List<Service> retrieveUnpricedServicesForMonth(int month) {
        List<Service> unpricedServices = new ArrayList<>();
        for (int i = 0; i < customerServices.size(); i++) {
            Service service = customerServices.get(i);
            int serviceMonth = Util.retrieveMonthFromLong(service.getEndTime());
            if(!service.isPriced() && serviceMonth == month) {
                unpricedServices.add(customerServices.get(i));
            }
        }
        return unpricedServices;
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

    public @NonNull String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(@NonNull String customerId) {
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

    public Double getCustomerMileage() {
        return customerMileage;
    }

    public void setCustomerMileage(Double customerMileage) {
        this.customerMileage = customerMileage;
    }

    @Override
    public long getModifiedTime() {
        return modifiedTime;
    }

    @Override
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    @Override
    public String toString() {
        return (customerBusiness == null ? customerFirstName + customerLastName : customerBusiness) + " " + concatenateFullAddress();
    }

    @Override
    public String getName() {
        return customerBusiness == null ? customerFirstName + " " + customerLastName : customerBusiness;
    }

    @Override
    public List<Customer> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.customerDao().getAllCustomers();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.CUSTOMER).find().projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, Customer.class);
    }

    @Override
    public List<Customer> retrieveClassInstancesAfterModifiedTime(DatabaseOperator db, long modifiedTime) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.customerDao().getNewlyModifiedCustomers(modifiedTime);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.CUSTOMER).find(gt("modifiedTime", modifiedTime)).projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, Customer.class);
    }

    @Override
    public Customer retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.customerDao().findCustomerById(id);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> document = od.getCollection(OnlineDatabase.CUSTOMER).find(eq("customerId", id)).projection(excludeId());
        return OnlineDatabase.convertDocumentToObject(document, Customer.class);

    }

    @Override
    public Customer retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string) {
        return null;
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, Customer objectToDelete) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.customerDao().deleteCustomer(objectToDelete);
        }
        else {
            String idToDelete = objectToDelete.getCustomerId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.CUSTOMER).deleteOne(eq("customerId", idToDelete));
        }
    }

    @Override
    public void updateClassInstanceFromDatabase(DatabaseOperator db, Customer objectToUpdate) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.customerDao().updateCustomer(objectToUpdate);
        }
        else {
            String idToUpdate = objectToUpdate.getCustomerId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.CUSTOMER).replaceOne(eq("customerId", idToUpdate),
                    OnlineDatabase.convertFromObjectToDocument(objectToUpdate));
        }
    }

    @Override
    public void insertClassInstanceFromDatabase(DatabaseOperator db, Customer objectToInsert) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.customerDao().insert(objectToInsert);
        }
        else {
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.CUSTOMER).insertOne(OnlineDatabase.convertFromObjectToDocument(objectToInsert));
        }
    }

    @Override
    public String getId() {
        return customerId;
    }

    public String concatenateFullAddress() {
        if(customerCity != null) {
            return getCustomerAddress() + " " + getCustomerCity()
                    + ", " + getCustomerState();
        }
            return getCustomerAddress() + " " + getCustomerState();
    }

    public String getFullName() {
        return customerFirstName + " " + customerLastName;
    }
}
