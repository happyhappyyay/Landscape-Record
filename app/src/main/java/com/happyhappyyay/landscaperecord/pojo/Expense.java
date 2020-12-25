package com.happyhappyyay.landscaperecord.pojo;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

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
public class Expense implements DatabaseObjects<Expense> {
    @PrimaryKey
    private @NonNull String id = UUID.randomUUID().toString();
    private double price;
    private String name;
    private String expenseType;
    private String paymentType;
    private long date;
    private long modifiedTime;

    public Expense(String name, long date, double price, String expenseType, String paymentType){
        this.name = name;
        this.price = price;
        this.expenseType = expenseType;
        this.paymentType = paymentType;
        this.date = date;
        modifiedTime = Util.retrieveLongCurrentDate();
    }

    @Ignore
    public Expense(){

    }

    public long getDate() {
        return date;
    }

    public void setDate(long date) {
        this.date = date;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getExpenseType() {
        return expenseType;
    }

    public void setExpenseType(String expenseType) {
        this.expenseType = expenseType;
    }

    public String getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(String paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public List<Expense> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.expenseDao().getAllExpenses();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.EXPENSE).find().projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, Expense.class);
    }

    @Override
    public List<Expense> retrieveClassInstancesAfterModifiedTime(DatabaseOperator db, long modifiedTime) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.expenseDao().getNewlyModifiedExpenses(modifiedTime);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.EXPENSE).find(gt("modifiedTime", modifiedTime)).projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, Expense.class);
    }

    @Override
    public Expense retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.expenseDao().getExpenseById(id);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        Document document = od.getCollection(OnlineDatabase.EXPENSE).find(eq("id", id)).projection(excludeId()).first();
        return OnlineDatabase.convertDocumentToObject(document, Expense.class);
    }

    @Override
    public Expense retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string) {
        return null;
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, Expense objectToDelete) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.expenseDao().deleteExpense(objectToDelete);
        }
        else {
            String idToDelete = objectToDelete.getId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.EXPENSE).deleteOne(eq("id", idToDelete));
        }
    }

    @Override
    public void updateClassInstanceFromDatabase(DatabaseOperator db, Expense objectToUpdate) {
    }

    @Override
    public void insertClassInstanceFromDatabase(DatabaseOperator db, Expense objectToInsert) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.expenseDao().insert(objectToInsert);
        }
        else {
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.EXPENSE).insertOne(OnlineDatabase.convertFromObjectToDocument(objectToInsert));
        }
    }

    public List<Expense> retrieveClassInstancesFromDatabaseByDate(DatabaseOperator db, long date) {
        if (db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.expenseDao().getExpenseByTime(date);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.EXPENSE).find(eq("date", date))
                .projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, Expense.class);
    }


    @Override
    public @NonNull String getId() {
        return id;
    }

    public void setId(@NonNull String id) {
        this.id = id;
    }

    @Override
    public long getModifiedTime() {
        return modifiedTime;
    }

    @Override
    public void setModifiedTime(long modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
