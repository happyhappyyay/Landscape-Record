package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.List;

public class Settings extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener, MultiDatabaseAccess{

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
        String databaseUsageKey = "pref_settings_database_usage";
        if(s.equals(databaseUsageKey)) {
            if(sharedPreferences.getBoolean(databaseUsageKey, true)){
                Util.enactMultipleDatabaseOperations(this);
            }
        }
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
        return null;
    }

    @Override
    public String createLogInfo() {
        return null;
    }

    @Override
    public void onPostExecute(List databaseObjects) {

    }
}
