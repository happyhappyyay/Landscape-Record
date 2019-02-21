package com.happyhappyyay.landscaperecord.utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseObjects;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public abstract class OnlineDatabase implements DatabaseOperator {
    public final static String CUSTOMER = "Customer";
    public final static String USER = "User";
    public final static String WORK_DAY = "WorkDay";
    public final static String LOG = "Log";
    private static OnlineDatabase instance;
    private static MongoClient mongoClient;
    private static String databaseName;
    private static int failedConnections = 0;

    public static OnlineDatabase getOnlineDatabase(Context context) {
        if (instance == null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String dbURI = sharedPref.getString(Util.retrieveStringFromResources(R.string.pref_key_database_uri, context), "");
            databaseName = sharedPref.getString(Util.retrieveStringFromResources(R.string.pref_key_database_name, context),"");
            MongoClientURI uri = new MongoClientURI(dbURI);
            mongoClient = new MongoClient(uri);
            instance = new OnlineDatabase() {
            };
        }
        return instance;
    }

    public static boolean connectionIsValid(Context context){
        try {
            getOnlineDatabase(context);
            return true;
        }
        catch (Exception e) {
            failedConnections++;
            return false;
        }
    }

    public static boolean hadFailedConnections() {
        return failedConnections > 0;
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

    private static String convertJSONFromLongNumberToLong(String s) {
        final String JSON_LONG = "{ \"$numberLong\" : ";
        String modifiedJsonString =s;
        int startIndex = 0;
        int endIndex;
        while(startIndex >= 0) {
            startIndex = modifiedJsonString.indexOf(JSON_LONG);
            endIndex = modifiedJsonString.indexOf("}", startIndex + JSON_LONG.length());
            if(startIndex != -1) {
                String tempBeforeLongString = modifiedJsonString.substring(0, startIndex);
                String tempAfterLongString = modifiedJsonString.substring(startIndex + JSON_LONG.length(), endIndex);
                modifiedJsonString = tempBeforeLongString + tempAfterLongString + modifiedJsonString.substring(endIndex + 1);
            }
        }
        return modifiedJsonString;
    }

    public static <T extends DatabaseObjects> T convertDocumentToObject(FindIterable<Document> doc, Class<T> objClass) {
        MongoCursor<Document> dbc = doc.iterator();

        while (dbc.hasNext()) {
            String json = convertJSONFromLongNumberToLong(dbc.next().toJson());
            try {
                JsonParser jsonParser = new JsonFactory().createParser(json);
                ObjectMapper mapper = new ObjectMapper();
                mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

                return mapper.readValue(jsonParser, objClass);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e("DOCSTOOBJS","An IOException was caught :"+e.getMessage());
            }
        }
        return null;
    }

    public static <T extends DatabaseObjects> List<T> convertDocumentsToObjects(FindIterable<Document> doc, Class<T> objClass) {
        MongoCursor<Document> dbc = doc.iterator();
        List<T> convertedObjects = new ArrayList<>();

        while (dbc.hasNext()) {
            String json = convertJSONFromLongNumberToLong(dbc.next().toJson());
            try {
                JsonParser jsonParser = new JsonFactory().createParser(json);
                ObjectMapper mapper = new ObjectMapper();

                T object = mapper.readValue(jsonParser, objClass);
                convertedObjects.add(object);

            } catch (IOException e) {
                e.printStackTrace();
                Log.e("DOCSTOOBJS","An IOException was caught :"+e.getMessage());

            }
        }
        return convertedObjects;
    }

    public static Document convertFromObjectToDocument (Object obj) {
        Gson gson = new Gson();

            String json = gson.toJson(obj);

            return Document.parse(json);
    }

    public MongoDatabase getMongoDb() {
        return mongoClient.getDatabase(databaseName);
    }

    public void resetDatabaseInstance() {
        instance = null;
    }

    public void resetFailedConnections(){
        failedConnections = 0;
    }
}
