package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

public abstract class OnlineDatabase implements DatabaseOperator{
    public final static String CUSTOMER = "Customer";
    public final static String USER = "User";
    public final static String WORK_DAY = "WorkDay";
    public final static String LOG = "Log";
    private static OnlineDatabase instance;
    private static MongoClient mongoClient;
    private static String databaseName;

    public static OnlineDatabase getOnlineDatabase(Context context) {
        if (instance == null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String dbURI = sharedPref.getString("pref_key_database_uri", "");
            databaseName = sharedPref.getString("pref_key_dbname","");
            MongoClientURI uri = new MongoClientURI(dbURI);
            mongoClient = new MongoClient(uri);
            instance = new OnlineDatabase() {
            };
        }
        return instance;
    }

        public static <T extends DatabaseObjects> List<T> convertDocumentsToObjects(List<Document> docs, Class<T> objClass) {
        Gson gson=new Gson();
        List<T> objects = new ArrayList<>();
        for(Document d: docs) {
            String s = d.toJson();
            objects.add(gson.fromJson(s, objClass));
        }
        return objects;
    }

    public static <T extends DatabaseObjects> T convertDocumentsToObjects(Document doc, Class<T> objClass) {
        Gson gson=new Gson();
            String s = doc.toJson();
            return gson.fromJson(s, objClass);
    }

    public static Document convertFromObjectToDocument (Object obj) {
        Gson gson = new Gson();
        String json = gson.toJson(obj);
        return Document.parse(json);

    }

    public MongoDatabase getMongoDb() {
        return mongoClient.getDatabase(databaseName);
    }
}
