package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.DatabaseInterface.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.User;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Authentication;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.List;

public class UserSettings extends AppCompatActivity implements MultiDatabaseAccess {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Authentication authentication;
        User user;
        TextView username;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        authentication = Authentication.getAuthentication();
        user = authentication.getUser();
        username = findViewById(R.id.user_settings_username);
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
}
