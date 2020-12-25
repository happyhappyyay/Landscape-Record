package com.happyhappyyay.landscaperecord.pojo;

import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

import com.happyhappyyay.landscaperecord.enums.LogActivityAction;
import com.happyhappyyay.landscaperecord.enums.LogActivityType;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseObjects;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;

import org.bson.Document;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;
import static com.mongodb.client.model.Filters.gt;
import static com.mongodb.client.model.Projections.excludeId;

//log actions performed by user
@Entity
public class LogActivity implements DatabaseObjects<LogActivity> {
    @PrimaryKey @NonNull
    private String id = UUID.randomUUID().toString();
    private String info;
    private int logActivityAction, logActivityType;
    private long modifiedTime;
    private String username;
    private String objId;

    public LogActivity(String username, String info, int logActivityAction, int logActivityType) {
        modifiedTime = System.currentTimeMillis();
        this.info = info;
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
                LogActivityAction.CHECKED_OUT.toString(), LogActivityAction.COMPLETED.toString());
        List<String> logTypeList = Arrays.asList(LogActivityType.USER.toString(),
                LogActivityType.CUSTOMER.toString(), LogActivityType.PAYMENT.toString(),
                LogActivityType.HOURS.toString(), LogActivityType.JOB.toString(),
                LogActivityType.SERVICES.toString(), LogActivityType.DATABASE.toString(),
                LogActivityType.WORKDAY.toString(), LogActivityType.EXPENSE.toString());
        activityAction += logActivityList.get(logActivityAction) + " ";
        activityAction += logTypeList.get(logActivityType) + " ";
        activityAction += info;

        return activityAction;
    }

    public String getObjId() {
        return objId;
    }

    public void setObjId(String objId) {
        this.objId = objId;
    }

    public String getInfo() {
        return info;
    }

    public String getUsername() {
        return username;
    }

    public void setInfo(String info) {
        this.info = info;
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

    @Override
    public LogActivity retrieveClassInstanceFromDatabaseID(DatabaseOperator db, String id) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getLogById(id);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        Document document = od.getCollection(OnlineDatabase.LOG).find(eq("id", id)).projection(excludeId()).first();
        return OnlineDatabase.convertDocumentToObject(document, LogActivity.class);
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
        List<Document> documents = od.getCollection(OnlineDatabase.LOG).find().projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    @Override
    public void deleteClassInstanceFromDatabase(DatabaseOperator db, LogActivity objectToDelete) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            ad.logDao().deleteLog(objectToDelete);
        }
        else {
            String idToDelete = objectToDelete.getId();
            OnlineDatabase ad = (OnlineDatabase) db;
            MongoDatabase od = ad.getMongoDb();
            od.getCollection(OnlineDatabase.LOG).deleteOne(eq("id", idToDelete));
        }
    }

    @Override
    public LogActivity retrieveClassInstanceFromDatabaseString(DatabaseOperator db, String string) {
        return null;
    }

    @NonNull
    @Override
    public String getId() {
        return id;
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
        List<Document> documents = od.getCollection(OnlineDatabase.LOG).find(and(gt("modifiedTime", modifiedTime),
                eq("logActivityAction", logActivityAction))).projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    public List<LogActivity> retrieveClassInstancesAfterModifiedTimeWithType(DatabaseOperator db, long modifiedTime, int logActivityType) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getNewlyModifiedTypeLogs(modifiedTime, logActivityType);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.LOG).find(and(gt("modifiedTime", modifiedTime),
                eq("logActivityType", logActivityType))).projection(excludeId()).into(new ArrayList<Document>());
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
        List<Document> documents = od.getCollection(OnlineDatabase.LOG).find(gt("modifiedTime",
                modifiedTime)).projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    public List<LogActivity> retrieveClassInstancesFromUsername(DatabaseOperator db, String username) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getLogsByName(username);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        List<Document> documents = od.getCollection(OnlineDatabase.LOG).find(eq("username",
                username)).projection(excludeId()).into(new ArrayList<Document>());
        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }

    public List<LogActivity> retrieveClassInstancesFromActedUser(DatabaseOperator db, String username) {
        if(db instanceof AppDatabase) {
            AppDatabase ad = (AppDatabase) db;
            return ad.logDao().getLogsByActedUser(username);
        }
        OnlineDatabase ad = (OnlineDatabase) db;
        MongoDatabase od = ad.getMongoDb();
        Pattern regex = Pattern.compile(username, Pattern.CASE_INSENSITIVE);
        List<Document> documents = od.getCollection(OnlineDatabase.LOG).find(
                Filters.or(Filters.regex("info",regex), eq("username", username)))
                .projection(excludeId()).into(new ArrayList<Document>());

        return OnlineDatabase.convertDocumentsToObjects(documents, LogActivity.class);
    }


    public void setId(@NonNull String id) {
        this.id = id;
    }
}
