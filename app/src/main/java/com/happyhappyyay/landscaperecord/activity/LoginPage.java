package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;
import com.happyhappyyay.landscaperecord.utility.testDatabase;

import java.util.List;

public class LoginPage extends AppCompatActivity implements DatabaseAccess<User> {

    private EditText username;
    private EditText password;
    private Authentication authentication;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        boolean afterFirstTime = sharedPref.getBoolean(getString(R.string.pref_key_after_first_time), false);
        if(!afterFirstTime) {
            boolean afterFirst = getIntent().getBooleanExtra(getString(R.string.pref_key_after_first_time), false);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putBoolean(getString(R.string.pref_key_after_first_time), afterFirst);
            editor.apply();
            boolean afterFirstTimeInside = sharedPref.getBoolean(getString(R.string.pref_key_after_first_time), false);
            if(!afterFirstTimeInside) {
                Intent intent = new Intent(this, FirstGlance.class);
                startActivity(intent);
            }
        }
        setContentView(R.layout.activity_login_page);
        authentication = Authentication.getAuthentication();
        username = findViewById(R.id.login_page_username);
        password = findViewById(R.id.login_page_password);
        progressBar = findViewById(R.id.login_page_progress_bar);

    }

    public void attemptLogin(View view) {
        if (progressBar.getVisibility() == View.INVISIBLE) {
            if (!username.getText().toString().isEmpty()) {
                loginUser();
            }
        }
    }

    public void testDatabase(View view) {
        Intent intent = new Intent(getApplicationContext(), testDatabase.class);
        startActivity(intent);
    }

    private void loginUser() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findObjectByString(this, Util.USER_REFERENCE, username.getText().toString());
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
    public void onPostExecute(List<User> databaseObjects) {
        User user = databaseObjects.get(0);
        boolean authorized = false;
        if (user != null) {
            if (user.getPassword().equals(password.getText().toString())) {
                authorized = true;
                authentication.setUser(user);
                Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                startActivity(intent);
            }
        }
        if (!authorized) {
            username.setText("");
            password.setText("");
            Toast.makeText(getApplicationContext(), R.string.login_page_failed, Toast.LENGTH_LONG).show();
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
