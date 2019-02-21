package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class UserSetting extends AppCompatActivity implements MultiDatabaseAccess {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar myToolbar = findViewById(R.id.user_settings_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Authentication authentication = Authentication.getAuthentication();
        User user = authentication.getUser();
        TextView username = findViewById(R.id.user_settings_username);
        username.setText(user.getName());
    }

    public void onClick(View view) {
        Util.enactMultipleDatabaseOperations(this);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Util.toolbarItemSelection(this, item);
    }
}
