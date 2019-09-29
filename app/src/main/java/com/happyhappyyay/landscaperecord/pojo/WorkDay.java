package com.happyhappyyay.landscaperecord.pojo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.happyhappyyay.landscaperecord.interfaces.DatabaseObjects;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.excludeId;

@Entity
public class WorkDay implements DatabaseObjects<WorkDay> {
    @PrimaryKey @NonNull
    private String id = UUID.randomUUID().toString();
    private String date;
    private long dateTime;
    private Map<String, Integer> userHours;
    private Map<String, Double> payments;
    private Map<String, Double> expenses;
    private Map<String, String> services;
    private long week;
    private long month;
    private long year;
    private long modifiedTime;

    public WorkDay (String date) {
        services = new HashMap<>();
        payments = new HashMap<>();
        expenses = new HashMap<>();
        findCalendarInformation(date);
        modifiedTime = System.currentTimeMillis();
        userHours = new HashMap<>();

    }

    @Ignore
    public WorkDay() {
    }

    public void addUserHourReference (String userReference, int hours) {
        if(userHours.containsKey(userReference)) {
            userHours.put(userReference,userHours.get(userReference) + hours);
        }
        else {
            userHours.put(userReference, hours);
        }
    }

    public List<String> retrieveUsersHoursAsString() {
        List<String> strings = new ArrayList<>();
        Set< Map.Entry<String, Integer> > mapSet = userHours.entrySet();
        int count = 0;

        for (Map.Entry< String, Integer> mapEntry:mapSet)
        {
            String priceEntry = mapEntry.getKey() + " : " + mapEntry.getValue();
            strings.add(count, priceEntry);
            count++;
        }
        return strings;
    }

    public List<String> retrieveServicesAsString() {
        List<String> strings = new ArrayList<>();
        Set< Map.Entry<String, String> > mapSet = services.entrySet();

        for (Map.Entry< String, String> mapEntry:mapSet)
        {
            String customerName = mapEntry.getKey().substring(0, mapEntry.getKey().indexOf(Util.DELIMITER));
            String serviceEntry = customerName + " : " + mapEntry.getValue();
            strings.add(serviceEntry);
        }
        return strings;
    }

    public List<String> retrievePaymentsAsString() {
        List<String> strings = new ArrayList<>();
        Set< Map.Entry<String, Double>> mapSet = payments.entrySet();

        for (Map.Entry< String, Double> mapEntry:mapSet)
        {
            int split = mapEntry.getKey().indexOf(Util.DELIMITER);
            String username = mapEntry.getKey().substring(0,split);
            String customerName = mapEntry.getKey().substring(split);
            String paymentEntry = username + " accepted : " +customerName + " $" + mapEntry.getValue();
            strings.add(paymentEntry);
        }
        return strings;
    }

    public List<String> retrieveExpensesAsString() {
        List<String> strings = new ArrayList<>();
        Set< Map.Entry<String, Double> > mapSet = expenses.entrySet();

        for (Map.Entry< String, Double> mapEntry:mapSet)
        {
            int firstSplit = mapEntry.getKey().indexOf(Util.DELIMITER);
            int secondSplit = mapEntry.getKey().substring(firstSplit).indexOf(Util.DELIMITER);
            String username = mapEntry.getKey().substring(secondSplit);
            String expenseName = mapEntry.getKey().substring(firstSplit, secondSplit);
            String priceEntry = username + " : " + expenseName + ": $" + mapEntry.getValue();
            strings.add(priceEntry);
        }
        return strings;
    }

    public int hoursForUser(String username){
        int hours = 0;
        Set< Map.Entry<String, Integer>> mapSet = userHours.entrySet();

        for (Map.Entry< String, Integer> mapEntry:mapSet)
        {
            if(mapEntry.getKey().contains(username)){
                hours += mapEntry.getValue();
                break;
            }
        }
        return hours;
    }

    public double expensesForUser(String username){
        int expense = 0;
        Set< Map.Entry<String, Double>> mapSet = expenses.entrySet();

        for (Map.Entry< String, Double> mapEntry:mapSet)
        {
            if(mapEntry.getKey().contains(username)){
                expense += mapEntry.getValue();
                break;
            }
        }
        return expense;
    }

    public double paymentsForUser(String username){
        int payment = 0;
        Set< Map.Entry<String, Double>> mapSet = payments.entrySet();

        for (Map.Entry< String, Double> mapEntry:mapSet)
        {
            if(mapEntry.getKey().contains(username)){
                payment += mapEntry.getValue();
                break;
            }
        }
        return payment;
    }

    public double numPaymentsForUser(String username){
        int payment = 0;
        Set< Map.Entry<String, Double>> mapSet = payments.entrySet();

        for (Map.Entry< String, Double> mapEntry:mapSet)
        {
            if(mapEntry.getKey().contains(username)){
                payment++;
                break;
            }
        }
        return payment;
    }

    public void addServices(Service service) {
        String key = service.getCustomerName() + Util.DELIMITER + service.getId();
        services.put(key, service.getServices());
    }

    public void addPayment(String username, Customer customer, double amount ) {
        String key = username + Util.DELIMITER + customer.getName();
        payments.put(key, amount);
    }

    public void addExpense(String username, Expense expense) {
        String key = expense.getId() + Util.DELIMITER + expense.getName() + Util.DELIMITER + username;
        expenses.put(key, expense.getPrice());
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    private void findCalendarInformation(String newDate) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = dateFormat.parse(newDate);
            dateTime = date.getTime();
            this.date = newDate;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            week = cal.getTimeInMillis();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            month = cal.getTimeInMillis();
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
            year = cal.getTimeInMillis();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Map<String, Double> getPayments() {
        return payments;
    }

    public void setPayments(Map<String, Double> payments) {
        this.payments = payments;
    }

    public Map<String, Double> getExpenses() {
        return expenses;
    }

    public void setExpenses(Map<String, Double> expenses) {
        this.expenses = expenses;
    }

    public Map<String, String> getServices() {
        return services;
    }

    public void setServices(Map<String, String> services) {
        this.services = services;
    }

    public long getWeek() {
        return week;
    }

    public void setWeek(long week) {
        this.week = week;
    }

    public long getMonth() {
        return month;
    }

    public void setMonth(long month) {
        this.month = month;
    }

    public long getYear() {
        return year;
    }

    public void setYear(long year) {
        this.year = year;
    }

    public long getDateTime() {
        return dateTime;
    }

    public void setDateTime(long dateTime) {
        this.dateTime = dateTime;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
    }

    @Override
    public long getModifiedTime() {
        return modifiedTime;
    }

    @Override
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }

    public Map<String, Integer> getUserHours() {
        return userHours;
    }

    public void setUserHours(Map<String, Integer> userHours) {
        this.userHours = userHours;
    }

    @Override
    public String getName() {
        return null;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Override
    public List<WorkDay> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().getAllWorkDays();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.WORK_DAY).find().projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, WorkDay.class);
    }

    @Override
    public List<WorkDay> retrieveClassInstancesAfterModifiedTime(DatabaseOperator db, long modifiedTime) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().getNewlyModifiedWorkDays(modifiedTime);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.WORK_DAY).find(gt("modifiedTime", modifiedTime)).projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, WorkDay.class);
    }

    @Override
    public WorkDay retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().findWorkDayById(id);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        Document document = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("id", id)).projection(excludeId()).first();
        return OnlineDatabase.convertDocumentToObject(document, WorkDay.class);
    }

    @Override
    public WorkDay retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String date) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().findWorkDayByDate(date);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        Document document = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("date", date)).projection(excludeId()).first();
        return OnlineDatabase.convertDocumentToObject(document, WorkDay.class);
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, WorkDay objectToDelete) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.workDayDao().deleteWorkDay(objectToDelete);
        }
        else {
            String idToDelete = objectToDelete.getId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.WORK_DAY).deleteOne(eq("id", idToDelete));
        }
    }

    @Override
    public void updateClassInstanceFromDatabase(DatabaseOperator db, WorkDay objectToUpdate) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.workDayDao().updateWorkDay(objectToUpdate);
        }
        else {
            String idToUpdate = objectToUpdate.getId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.WORK_DAY).replaceOne(eq("id", idToUpdate),
                    OnlineDatabase.convertFromObjectToDocument(objectToUpdate));
        }
    }

    @Override
    public void insertClassInstanceFromDatabase(DatabaseOperator db, WorkDay objectToInsert) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.workDayDao().insert(objectToInsert);
        }
        else {
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.WORK_DAY).insertOne(OnlineDatabase.convertFromObjectToDocument(objectToInsert));
        }
    }

    public WorkDay retrieveClassInstancesFromDatabaseByDay(DatabaseOperator db, long dateTime) {
        if (db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().findWorkDayByTime(dateTime);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        Document document = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("dateTime", dateTime)).projection(excludeId()).first();
        return OnlineDatabase.convertDocumentToObject(document, WorkDay.class);
    }

    public List<WorkDay> retrieveClassInstancesFromDatabaseByWeek(DatabaseOperator db, long weekInMilli) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().findWorkWeekByTime(weekInMilli);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("week", weekInMilli)).projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, WorkDay.class);
    }

    public List<WorkDay> retrieveClassInstancesFromDatabaseByMonth(DatabaseOperator db, long monthInMilli) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().findWorkMonthByTime(monthInMilli);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("month", monthInMilli)).projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, WorkDay.class);
    }

//    public List<WorkDay> retrieveClassInstancesFromDatabaseByUser(DatabaseOperator db, String username) {
//        if(db instanceof AppDatabase) {
//            AppDatabase ad = (AppDatabase) db;
//            return ad.workDayDao().findWorkDayByUser(username);
//        }
//        OnlineDatabase ad = (OnlineDatabase) db;
//        MongoDatabase od = ad.getMongoDb();
//        Pattern regex = Pattern.compile(username, Pattern.CASE_INSENSITIVE);
//        List<Document> documents = od.getCollection(OnlineDatabase.LOG).find(
//                Filters.or(Filters.regex("userHours",regex), Filters.regex("payments",regex),
//                        Filters.regex("expenses",regex)))
//                .projection(excludeId()).into(new ArrayList<Document>());
//
//        return OnlineDatabase.convertDocumentsToObjects(documents, WorkDay.class);
//    }
}
