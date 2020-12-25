package com.happyhappyyay.landscaperecord.pojo;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import androidx.annotation.NonNull;

import com.happyhappyyay.landscaperecord.converter.PaymentConverter;
import com.happyhappyyay.landscaperecord.converter.ServiceListConverter;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseObjects;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;
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
    private String id = UUID.randomUUID().toString();
    private String first;
    private String last;
    private String address;
    private String city;
    private String phone;
    private String email;
    private String business;
    private String day;
    private String state;
    private Double mi;
    private long modifiedTime;

    @TypeConverters(PaymentConverter.class)
    private Payment payment;

    @TypeConverters(ServiceListConverter.class)
    private List<Service> services;

    public Customer(String first, String last, String address) {
        this.first = first;
        this.last = last;
        this.address = address;
        services = new ArrayList<>();
        payment = new Payment();
        modifiedTime = System.currentTimeMillis();
    }

    @Ignore
    public Customer() {
        services = new ArrayList<>();
    }

    public void addService(Service customerService) {
        services.add(customerService);
    }

    public void updateService(Service customerService, int indexPosition) {
        services.set(indexPosition, customerService);
    }

    public void updateService(Service existingService) {
        for(int i = 0; i < services.size(); i++) {
            if (services.get(i).getId() == existingService.getId()) {
                services.set(i, existingService);
                break;
            }
        }
    }

    public void updateServices(List<Service> existingServices) {
        for(int i = 0; i < services.size(); i++) {
            for(Service s: existingServices) {
                if (services.get(i).getId() == s.getId()) {
                    services.set(i, s);
                }
            }
        }
    }

    public List<String> retrieveServicesWithPrices() {
        List<String> servicesWithPrices = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            String services = this.services.get(i).getServices();
            Service service = this.services.get(i);
            servicesWithPrices.add(Util.convertLongToStringDate(service.getEndTime()) + " " + services + " $ " + payment.returnServicePrice(services));
        }
        return servicesWithPrices;
    }

    public boolean hasUnpricedServicesForMonth(int month) {
        for (int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            int serviceMonth = Util.retrieveMonthFromLong(service.getEndTime());
            if(!service.isPriced() && serviceMonth == month) {
                return true;
            }
        }
        return false;
    }

    public List<Service> retrieveUnpricedServicesForMonth(int month) {
        List<Service> unpricedServices = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            int serviceMonth = Util.retrieveMonthFromLong(service.getEndTime());
            if(!service.isPriced() && serviceMonth == month) {
                unpricedServices.add(services.get(i));
            }
        }
        return unpricedServices;
    }

    public int retrieveNumberUnpricedServiceMonths() {
        List<Integer> months = new ArrayList<>();
        for (int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            if(service.getEndTime() > 0) {
                int serviceMonth = Util.retrieveMonthFromLong(service.getEndTime());
                if (!service.isPriced() && !months.contains(serviceMonth)) {
                    months.add(serviceMonth);
                }
            }
        }
        return months.size();
    }

    public boolean hasUnfinishedServicesForMonth(int month) {
        for (int i = 0; i < services.size(); i++) {
            Service service = services.get(i);
            int serviceMonth = Util.retrieveMonthFromLong(service.getStartTime());
            if(!service.isPriced() && serviceMonth == month && service.getEndTime() <= 0) {
                return true;
            }
        }
        return false;
    }

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getBusiness() {
        return business;
    }

    public void setBusiness(String business) {
        this.business = business;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public List<Service> getServices() {
        return services;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public Double getMi() {
        return mi;
    }

    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

    public void setMi(Double mi) {
        this.mi = mi;
    }

    @Override
    public String toString() {
        return (business == null ? first + " " + last : business) + " " + concatenateFullAddress();
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
    public String getName() {
        return business == null ? first + " " + last : business;
    }

    @Override
    public Customer retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.customerDao().findCustomerById(id);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        Document document = od.getCollection(OnlineDatabase.CUSTOMER).find(eq("id", id)).projection(excludeId()).first();
        return OnlineDatabase.convertDocumentToObject(document, Customer.class);
    }

    @Override
    public List<Customer> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.customerDao().getAllCustomers();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.CUSTOMER).find().projection(excludeId()).into(new ArrayList<Document>());
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
        List<Document> documents = od.getCollection(OnlineDatabase.CUSTOMER).find(gt("modifiedTime", modifiedTime)).projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, Customer.class);
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, Customer objectToDelete) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.customerDao().deleteCustomer(objectToDelete);
        }
        else {
            String idToDelete = objectToDelete.getId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.CUSTOMER).deleteOne(eq("id", idToDelete));
        }
    }

    @Override
    public Customer retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string) {
        return null;
    }

    @Override
    public void updateClassInstanceFromDatabase(DatabaseOperator db, Customer objectToUpdate) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.customerDao().updateCustomer(objectToUpdate);
        }
        else {
            String idToUpdate = objectToUpdate.getId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.CUSTOMER).replaceOne(eq("id", idToUpdate),
                    OnlineDatabase.convertFromObjectToDocument(objectToUpdate));
        }
    }

    @NonNull
    @Override
    public String getId() {
        return id;
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

    public void setId(@NonNull String id) {
        this.id = id;
    }

    public String concatenateFullAddress() {
        if(city != null) {
            return getAddress() + " " + getCity()
                    + ", " + getState();
        }
            return getAddress() + " " + getState();
    }

    public String createFullName() {
        return first + " " + last;
    }
}
