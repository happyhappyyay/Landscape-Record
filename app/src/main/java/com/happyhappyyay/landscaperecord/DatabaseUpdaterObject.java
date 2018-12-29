package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class DatabaseUpdaterObject implements DatabaseObjects<DatabaseUpdaterObject> {
    @PrimaryKey
    @NonNull
    private String databaseUpdaterObjectId = UUID.randomUUID().toString();
    private DatabaseObjects databaseObject;
    private int timesAccessed;
    private String objectModificationType;

    public DatabaseUpdaterObject(DatabaseObjects databaseObject, String modificationType) {
        this.databaseObject = databaseObject;
        timesAccessed = 1;
        objectModificationType = modificationType;
    }

    @Ignore
    public DatabaseUpdaterObject() {
    }

    public static void putClassInstanceFromDatabase(DatabaseOperator db, DatabaseObjects objectToInsert, String modificationType) {
        DatabaseUpdaterObject dUO = new DatabaseUpdaterObject(objectToInsert, modificationType);
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
//            ad.updaterDao().insert(dUO);
        }
        else {
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(Util.UPDATE).insertOne(OnlineDatabase.convertFromObjectToDocument(dUO));
        }
    }

    public static List<DatabaseUpdaterObject> retrieveClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
//            return ad.updaterDao().getAllDatabaseUpdaterObjects();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(Util.UPDATE).find().into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, DatabaseUpdaterObject.class);
    }

    public String getObjectModificationType() {
        return objectModificationType;
    }

    public void setObjectModificationType(String objectModificationType) {
        this.objectModificationType = objectModificationType;
    }

    public @NonNull String getDatabaseUpdaterObjectId() {
        return databaseUpdaterObjectId;
    }

    public void setDatabaseUpdaterObjectId(@NonNull String databaseUpdaterObjectId) {
        this.databaseUpdaterObjectId = databaseUpdaterObjectId;
    }

    public DatabaseObjects getDatabaseObject() {
        return databaseObject;
    }

    public void setDatabaseObject(DatabaseObjects databaseObject) {
        this.databaseObject = databaseObject;
    }

    public int getTimesAccessed() {
        return timesAccessed;
    }

    public void setTimesAccessed(int timesAccessed) {
        this.timesAccessed = timesAccessed;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<DatabaseUpdaterObject> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
//            return ad.updaterDao().getAllDatabaseUpdaterObjects();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(Util.UPDATE).find().into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, DatabaseUpdaterObject.class);
    }

    @Override
    public List<DatabaseUpdaterObject> retrieveClassInstancesAfterModifiedTime(DatabaseOperator db, long modifiedTime) {
        return null;
    }

    @Override
    public DatabaseUpdaterObject retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id) {
        return null;
    }

    @Override
    public DatabaseUpdaterObject retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string) {
        return null;
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, DatabaseUpdaterObject objectToDelete) {

    }

    @Override
    public void updateClassInstanceFromDatabase(DatabaseOperator db, DatabaseUpdaterObject objectToUpdate) {

    }

    @Override
    public void insertClassInstanceFromDatabase(DatabaseOperator db, DatabaseUpdaterObject objectToInsert) {

    }

    @Override
    public String getId() {
        return databaseUpdaterObjectId;
    }

    @Override
    public long getModifiedTime() {
        return 0;
    }

    @Override
    public void setModifiedTime(long modifiedTime) {

    }
}
