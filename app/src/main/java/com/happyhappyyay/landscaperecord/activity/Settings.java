package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.fragments.SettingsFragment;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class Settings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MultiDatabaseAccess {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        getFragmentManager().beginTransaction()
                .replace(R.id.settings_frame_layout, new SettingsFragment())
                .commit();
        Toolbar myToolbar = findViewById(R.id.settings_toolbar);
        setSupportActionBar(myToolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Util.toolbarItemSelection(this, item);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String s) {
        String databaseUsageKey = getString(R.string.pref_key_database_usage);
        String databaseURIKey = getString(R.string.pref_key_database_uri);
        String databaseNameKey = getString(R.string.pref_key_database_name);
        String prefName = sharedPreferences.getString(databaseNameKey, null);
        String prefURI = sharedPreferences.getString(databaseURIKey, null);
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        if(s.equals(databaseUsageKey)) {
            if(sharedPreferences.getBoolean(s, true)){
                if(OnlineDatabase.connectionIsValid(this)) {
                    OnlineDatabase.getOnlineDatabase(this).resetDatabaseInstance();
                    Util.enactMultipleDatabaseOperations(this);
                }
                else if(prefName != null && prefURI != null){
                    invalidToast();
                }
            }
        }
        else if(s.equals(databaseURIKey)) {
            if(sharedPreferences.getString(databaseNameKey, null) != null) {
                if(!OnlineDatabase.connectionIsValid(this)){
                    invalidToast();
                }
                else {
                    OnlineDatabase.getOnlineDatabase(this).resetDatabaseInstance();
                    Util.enactMultipleDatabaseOperations(this);
                }
            }
        }
        else if(s.equals(databaseNameKey)) {
            if(sharedPreferences.getString(databaseURIKey, null) != null) {
                if(!OnlineDatabase.connectionIsValid(this)){
                    invalidToast();
                }
                else {
                    OnlineDatabase.getOnlineDatabase(this).resetDatabaseInstance();
                    Util.enactMultipleDatabaseOperations(this);
                }
            }
        }
    }

    private void invalidToast() {
        Toast.makeText(this, "Database connection is not valid. Check connectivity and database uri/ database name.", Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onResume(){
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.registerOnSharedPreferenceChangeListener(this);


    }
    @Override
    protected void onPause(){
        super.onPause();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        pref.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        Util.updateDatabases(this);
    }

    @Override
    public void createCustomLog() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return null;
    }

    @Override
    public void onPostExecute(List databaseObjects) {

    }
}
