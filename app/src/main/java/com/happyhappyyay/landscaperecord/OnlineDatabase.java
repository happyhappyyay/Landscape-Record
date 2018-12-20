package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

public abstract class OnlineDatabase implements MongoDatabase, DatabaseOperator{
    private static MongoDatabase instance;

    public static MongoDatabase getOnlineDatabase(Context context) {
        if (instance == null) {
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String dbURI = sharedPref.getString("pref_key_database_uri", "");
            String dbName = sharedPref.getString("pref_key_dbname","");
            MongoClientURI uri = new MongoClientURI(dbURI);
            MongoClient mongoClient = new MongoClient(uri);
            instance = mongoClient.getDatabase(dbName);
        }
        return instance;
    }
}
