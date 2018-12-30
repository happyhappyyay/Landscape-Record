package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
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
        if (doc != null) {
            Gson gson = new Gson();
            String s = doc.toJson();
            return gson.fromJson(s, objClass);
        }
                return null;
    }

    public static <T extends DatabaseObjects> T convertDocumentToObject(FindIterable<Document> doc, Class<T> objClass) {
        MongoCursor<Document> dbc = doc.iterator();

        while (dbc.hasNext()) {
            try {
                JsonParser jsonParser = new JsonFactory().createParser(dbc.next().toJson());
                ObjectMapper mapper = new ObjectMapper();

                return mapper.readValue(jsonParser, objClass);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("DOCSTOOBJS", "convertDocumentsToObjects: fail");
            }
        }
        return null;
    }

    public static <T extends DatabaseObjects> List<T> convertDocumentsToObjects(FindIterable<Document> doc, Class<T> objClass) {
        MongoCursor<Document> dbc = doc.iterator();
        List<T> convertedObjects = new ArrayList<>();

        while (dbc.hasNext()) {
            try {
                JsonParser jsonParser = new JsonFactory().createParser(dbc.next().toJson());
                ObjectMapper mapper = new ObjectMapper();

                T object = mapper.readValue(jsonParser, objClass);
                convertedObjects.add(object);

            } catch (Exception e) {
                e.printStackTrace();
                Log.d("DOCSTOOBJS", "convertDocumentsToObjects: fail");
            }
        }
        return convertedObjects;
    }

    public static Document convertFromObjectToDocument (Object obj) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            String json = mapper.writeValueAsString(obj);
            return Document.parse(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MongoDatabase getMongoDb() {
        return mongoClient.getDatabase(databaseName);
    }
}
