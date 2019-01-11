package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.arch.persistence.room.TypeConverters;
import android.support.annotation.NonNull;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.excludeId;

@Entity
public class WorkDay implements DatabaseObjects<WorkDay> {
    @PrimaryKey @NonNull
    private String workDayId = UUID.randomUUID().toString();
    private String dayOfWeek;
    private String Month;
    private String Year;
    private String currentDate;
    private long currentDateAsTime;
    @TypeConverters(IntegerListConverter.class)
    private List<Integer> hours;
    @TypeConverters(StringListConverter.class)
    private List<String> userReference;
    private List<Service> services;
    private long weekInMilli;
    private long monthInMilli;
    private long yearInMilli;
    private long modifiedTime;

    public WorkDay (String currentDate) {
        services = new ArrayList<>();
        hours = new ArrayList<>();
        userReference = new ArrayList<>();
        findCalendarInformation(currentDate);
        modifiedTime = System.currentTimeMillis();
    }

    @Ignore
    public WorkDay() {
        services = new ArrayList<>();
        hours = new ArrayList<>();
        userReference = new ArrayList<>();
    }

    public void addUserHourReference (String userReference, int hours) {
        boolean existingReference = false;
        for (int i = 0; i < this.userReference.size(); i++) {
            if(userReference.equals(this.userReference.get(i))) {
                this.hours.set(i, this.hours.get(i) + hours);
                existingReference = true;
                break;
            }
        }
        if(!existingReference) {
            this.hours.add(hours);
            this.userReference.add(userReference);
        }
    }

    public void addServices(Service service) {
        services.add(service);
    }

    public String getCurrentDate() {
        return currentDate;
    }

    public List<Service> getServices() {
        return services;
    }

    protected void findCalendarInformation(String newDate) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = dateFormat.parse(newDate);
            currentDateAsTime = date.getTime();
            currentDate = newDate;
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            weekInMilli = cal.getTimeInMillis();
            dayOfWeek = new SimpleDateFormat("EEEE", Locale.US).format(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getActualMinimum(Calendar.DAY_OF_MONTH));
            monthInMilli = cal.getTimeInMillis();
            Month = new SimpleDateFormat("MMMM", Locale.US).format(date);
            cal.set(Calendar.DAY_OF_YEAR, cal.getActualMinimum(Calendar.DAY_OF_YEAR));
            yearInMilli = cal.getTimeInMillis();
            Year = new SimpleDateFormat("yyyy", Locale.US).format(date);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    public long getWeekInMilli() {
        return weekInMilli;
    }

    public long getMonthInMilli() {
        return monthInMilli;
    }

    public long getYearInMilli() {
        return yearInMilli;
    }

    public boolean startOfTheWeek() {
        return currentDateAsTime == weekInMilli;
    }

    public boolean startOfTheMonth() {
        return currentDateAsTime == monthInMilli;
    }

    public boolean startOfTheYear() {
        return currentDateAsTime == yearInMilli;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public @NonNull String getWorkDayId() {
        return workDayId;
    }

    public void setWorkDayId(@NonNull String workDayId) {
        this.workDayId = workDayId;
    }

    public long getCurrentDateAsTime() {
        return currentDateAsTime;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public void setCurrentDate(String currentDate) {
        this.currentDate = currentDate;
    }

    public void setCurrentDateAsTime(long currentDateAsTime) {
        this.currentDateAsTime = currentDateAsTime;
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public void setWeekInMilli(long weekInMilli) {
        this.weekInMilli = weekInMilli;
    }

    public void setMonthInMilli(long monthInMilli) {
        this.monthInMilli = monthInMilli;
    }

    public void setYearInMilli(long yearInMilli) {
        this.yearInMilli = yearInMilli;
    }

    public String getMonth() {
        return Month;
    }

    public String getYear() {
        return Year;
    }

    public void setMonth(String month) {
        Month = month;
    }

    public void setYear(String year) {
        Year = year;
    }

    public List<Integer> getHours() {
        return hours;
    }

    public void setHours(List<Integer> hours) {
        this.hours = hours;
    }

    public List<String> getUserReference() {
        return userReference;
    }

    public void setUserReference(List<String> userReference) {
        this.userReference = userReference;
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
        return null;
    }

    @Override
    public List<WorkDay> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().getAllWorkDays();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.WORK_DAY).find().projection(excludeId());
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
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.WORK_DAY).find(gt("modifiedTime", modifiedTime)).projection(excludeId());
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
        FindIterable<Document> document = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("workDayId", id)).projection(excludeId());
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
        FindIterable<Document> document = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("currentDate", date)).projection(excludeId());
        return OnlineDatabase.convertDocumentToObject(document, WorkDay.class);
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, WorkDay objectToDelete) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.workDayDao().deleteWorkDay(objectToDelete);
        }
        else {
            String idToDelete = objectToDelete.getWorkDayId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.WORK_DAY).deleteOne(eq("workDayId", idToDelete));
        }
    }

    @Override
    public void updateClassInstanceFromDatabase(DatabaseOperator db, WorkDay objectToUpdate) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.workDayDao().updateWorkDay(objectToUpdate);
        }
        else {
            String idToUpdate = objectToUpdate.getWorkDayId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.WORK_DAY).replaceOne(eq("workDayId", idToUpdate),
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

    @Override
    public String getId() {
        return workDayId;
    }

    public WorkDay retrieveClassInstancesFromDatabaseByDay(DatabaseOperator db, long dateTime) {
        if (db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().findWorkDayByTime(dateTime);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> document = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("currentDateAsTime", dateTime)).projection(excludeId());
        return OnlineDatabase.convertDocumentToObject(document, WorkDay.class);
    }

    public List<WorkDay> retrieveClassInstancesFromDatabaseByWeek(DatabaseOperator db, long weekInMilli) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().findWorkWeekByTime(weekInMilli);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("weekInMilli", weekInMilli)).projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, WorkDay.class);
    }

    public List<WorkDay> retrieveClassInstancesFromDatabaseByMonth(DatabaseOperator db, long monthInMilli) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.workDayDao().findWorkMonthByTime(monthInMilli);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.WORK_DAY).find(eq("monthInMilli", monthInMilli)).projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, WorkDay.class);
    }

}
