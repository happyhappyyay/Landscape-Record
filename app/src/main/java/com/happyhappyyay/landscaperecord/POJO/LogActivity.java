package com.happyhappyyay.landscaperecord.POJO;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseObjects;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseOperator;
import com.happyhappyyay.landscaperecord.Enum.LogActivityAction;
import com.happyhappyyay.landscaperecord.Enum.LogActivityType;
import com.happyhappyyay.landscaperecord.Utility.AppDatabase;
import com.happyhappyyay.landscaperecord.Utility.OnlineDatabase;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.excludeId;

//log actions performed by user
@Entity
public class LogActivity implements DatabaseObjects<LogActivity> {
    @PrimaryKey @NonNull
    private String logId = UUID.randomUUID().toString();
    private String addInfo;
    private int logActivityAction, logActivityType;
    private long modifiedTime;
    private String username;
    private String objId;

    public LogActivity(String username, String addInfo, int logActivityAction, int logActivityType) {
        modifiedTime = System.currentTimeMillis();
        this.addInfo = addInfo;
        this.logActivityAction = logActivityAction;
        this.logActivityType = logActivityType;
        this.username = username;
    }

    @Ignore
    public LogActivity() {
        modifiedTime = System.currentTimeMillis();
    }

    @Override
    public String toString() {
        Date date = new Date(modifiedTime);
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
                LogActivityType.SERVICES.toString(), LogActivityType.DATABASE.toString(),
                LogActivityType.WORKDAY.toString());
        activityAction += logActivityList.get(logActivityAction) + " ";
        activityAction += logTypeList.get(logActivityType) + " ";
        activityAction += addInfo;

        return activityAction;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public @NonNull String getLogId() {
        return logId;
    }

    public void setLogId(@NonNull String logId) {
        this.logId = logId;
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
    public List<LogActivity> retrieveAllClassInstancesFromDatabase(DatabaseOperator db) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getAllLogs();
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.LOG).find().projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    @Override
    public LogActivity retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getLogById(id);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> document = od.getCollection(OnlineDatabase.LOG).find(eq("logId", id)).projection(excludeId());
        return OnlineDatabase.convertDocumentToObject(document, LogActivity.class);
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
            String idToDelete = objectToDelete.getLogId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.LOG).deleteOne(eq("logId", idToDelete));
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

    public List<LogActivity> retrieveClassInstancesAfterModifiedTimeWithAction(DatabaseOperator db, long modifiedTime, int logActivityAction) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getNewlyModifiedActionLogs(modifiedTime, logActivityAction);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.LOG).find(and(gt("modifiedTime", modifiedTime), eq("logActivityAction", logActivityAction))).projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    public List<LogActivity> retrieveClassInstancesAfterModifiedTimeWithType(DatabaseOperator db, long modifiedTime, int logActivityType) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getNewlyModifiedTypeLogs(modifiedTime, logActivityType);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.LOG).find(and(gt("modifiedTime", modifiedTime), eq("logActivityType", logActivityType))).projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    @Override
    public List<LogActivity> retrieveClassInstancesAfterModifiedTime(DatabaseOperator db, long modifiedTime) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getNewlyModifiedLogs(modifiedTime);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.LOG).find(gt("modifiedTime", modifiedTime)).projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    public List<LogActivity> retrieveClassInstancesFromUsername(DatabaseOperator db, String username) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getLogsByName(username);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        FindIterable<Document> documents = od.getCollection(OnlineDatabase.LOG).find(eq("username", username)).projection(excludeId());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }


    @Override
    public String getId() {
        return logId;
    }


}
