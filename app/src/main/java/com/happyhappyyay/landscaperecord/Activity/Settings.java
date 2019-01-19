package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.DatabaseInterface.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.Fragment.SettingsFragment;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Util;

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
        String databaseUsageKey = "pref_settings_database_usage";
        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        if(s.equals(databaseUsageKey)) {
            if(sharedPreferences.getBoolean(s, true)){
                Util.enactMultipleDatabaseOperations(this);
            }
        }
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
