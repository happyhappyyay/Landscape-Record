package com.happyhappyyay.landscaperecord;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;

import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static com.mongodb.client.model.Filters.eq;

//log actions performed by user
@Entity
public class LogActivity implements DatabaseObjects<LogActivity> {
    @PrimaryKey(autoGenerate = true)
    private int logID;
    private long time;
    private String username, addInfo;
    private int logActivityAction, logActivityType;

    public LogActivity(String username, String addInfo, int logActivityAction, int logActivityType) {
        time = System.currentTimeMillis();
        this.username = username;
        this.addInfo = addInfo;
        this.logActivityAction = logActivityAction;
        this.logActivityType = logActivityType;
    }

    @Ignore
    public LogActivity() {
        time = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        Date date = new Date(time);
        SimpleDateFormat formatter = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss", Locale.US);
        String dateMessage = formatter.format(date);
        return dateMessage + ": " + username + " " + convertActivityToString();
    }

    private String convertActivityToString() {
        String activityAction = "";
        List<String> logActivityList = Arrays.asList(LogActivityAction.ADD.toString(),
                LogActivityAction.DELETE.toString(), LogActivityAction.UPDATE.toString(),
                LogActivityAction.PAY.toString(),LogActivityAction.CHECKED_IN.toString(),
                LogActivityAction.CHECKED_OUT.toString());
        List<String> logTypeList = Arrays.asList(LogActivityType.USER.toString(),
                LogActivityType.CUSTOMER.toString(), LogActivityType.PAYMENT.toString(),
                LogActivityType.HOURS.toString(), LogActivityType.JOB.toString(),
                LogActivityType.SERVICES.toString());
        activityAction += logActivityList.get(logActivityAction) + " ";
        activityAction += logTypeList.get(logActivityType) + " ";
        activityAction += addInfo;

        return activityAction;
    }

    public int getLogID() {
        return logID;
    }

    public void setLogID(int logID) {
        this.logID = logID;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public String getUsername() {
        return username;
    }

    public String getAddInfo() {
        return addInfo;
    }

    public int getLogActivityAction() {
        return logActivityAction;
    }

    public int getLogActivityType() {
        return logActivityType;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setAddInfo(String addInfo) {
        this.addInfo = addInfo;
    }

    public void setLogActivityAction(int logActivityAction) {
        this.logActivityAction = logActivityAction;
    }

    public void setLogActivityType(int logActivityType) {
        this.logActivityType = logActivityType;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public List<LogActivity> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getAllLogs();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.LOG).find().into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    @Override
    public LogActivity retrieveClassInstanceFromDatabaseID(DatabaseOperator db, int id) {
        return null;
    }

    @Override
    public LogActivity retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string) {
        return null;
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, LogActivity objectToDelete) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.logDao().deleteLog(objectToDelete);
        }
        else {
            int idToDelete = objectToDelete.getLogID();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.LOG).deleteOne(eq("logID", idToDelete));
        }
    }

    @Override
    public void updateClassInstanceFromDatabase(DatabaseOperator db, LogActivity objectToUpdate) {
    }

    @Override
    public void insertClassInstanceFromDatabase(DatabaseOperator db, LogActivity objectToInsert) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.logDao().insert(objectToInsert);
        }
        else {
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.LOG).insertOne(OnlineDatabase.convertFromObjectToDocument(objectToInsert));
        }
    }


}
