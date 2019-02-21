package com.happyhappyyay.landscaperecord.pojo;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.happyhappyyay.landscaperecord.interfaces.DatabaseObjects;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.List;
import java.util.UUID;

import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.excludeId;

@Entity
public class User implements DatabaseObjects<User> {
    @PrimaryKey @NonNull
    private String userId = UUID.randomUUID().toString();
    private double hours;
    private String firstName;
    private String lastName;
    private String name;
    private String password;
    private boolean admin;
    private long startTime;
    private String nickname;
    private long modifiedTime;

    public User() {
        nickname = "";
        modifiedTime = System.currentTimeMillis();
    }

    public boolean isAdmin() {
        return admin;
    }

    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public double getHours() {
        return hours;
    }

    public void setHours(double hours) {
        this.hours = hours;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
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
        return !getNickname().isEmpty() ? nickname : name;

    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<User> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.userDao().getAllUsers();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.USER).find().projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, User.class);
    }

    @Override
    public List<User> retrieveClassInstancesAfterModifiedTime(DatabaseOperator db, long modifiedTime) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.userDao().getNewlyModifiedUsers(modifiedTime);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.USER).find(gt("modifiedTime", modifiedTime)).projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, User.class);
    }

    @Override
    public User retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.userDao().findUserByID(id);
        }
        else {
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> document = od.getCollection(OnlineDatabase.USER).find(eq("userId", id)).projection(excludeId());
        return OnlineDatabase.convertDocumentToObject(document, User.class);
        }
    }

    @Override
    public User retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.userDao().findUserByName(string);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> document = od.getCollection(OnlineDatabase.USER).find(eq("name", string)).projection(excludeId());
        return OnlineDatabase.convertDocumentToObject(document, User.class);
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, User objectToDelete) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.userDao().deleteUser(objectToDelete);
        }
        else {
            String idToDelete = objectToDelete.getUserId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.USER).deleteOne(eq("userId", idToDelete));
        }
    }

    @Override
    public void updateClassInstanceFromDatabase(DatabaseOperator db, User objectToUpdate) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.userDao().updateUser(objectToUpdate);
        }
        else {
            String idToUpdate = objectToUpdate.getUserId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.USER).replaceOne(eq("userId", idToUpdate),
                    OnlineDatabase.convertFromObjectToDocument(objectToUpdate));
        }
    }

    @Override
    public void insertClassInstanceFromDatabase(DatabaseOperator db, User objectToInsert) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.userDao().insert(objectToInsert);
        }
        else {
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.USER).insertOne(OnlineDatabase.convertFromObjectToDocument(objectToInsert));
        }
    }

    @Override
    public String getId() {
        return userId;
    }
}
