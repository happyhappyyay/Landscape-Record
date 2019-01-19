package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.User;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Authentication;
import com.happyhappyyay.landscaperecord.Utility.Util;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import java.util.List;

public class FirstGlance extends AppCompatActivity implements DatabaseAccess<User> {
    private ConstraintLayout userLayout;
    private ConstraintLayout databaseLayout;
    private ProgressBar progressBar;
    private User user;
    private SharedPreferences.Editor editor;
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_glance);
        userLayout = findViewById(R.id.first_glance_layout_user);
        databaseLayout = findViewById(R.id.first_glance_layout_database);
        progressBar = findViewById(R.id.first_glance_progress_bar);
        TextView mongoText = findViewById(R.id.first_glance_mongo_text);
        mongoText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progressBar.getVisibility() == View.INVISIBLE) {
                    userLayout.setVisibility(View.GONE);
                    databaseLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        TextView userText = findViewById(R.id.first_glance_user_text);
        userText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (progressBar.getVisibility() == View.INVISIBLE) {
                    databaseLayout.setVisibility(View.GONE);
                    userLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        sharedPref = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
    }

    public void onDatabaseSubmit(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            Button testButton = findViewById(R.id.first_glance_database_button);
            String testButtonText = testButton.getText().toString().toLowerCase();
            String testText = "test";
            if (testButtonText.equals(testText)) {
                EditText databaseName = findViewById(R.id.first_glance_database_name);
                String dbNameText = databaseName.getText().toString();
                EditText databaseUri = findViewById(R.id.first_glance_database_uri);
                String dbURIText = databaseUri.getText().toString();
                if (!dbNameText.isEmpty() & !dbURIText.isEmpty()) {
                    progressBar.setVisibility(View.VISIBLE);
                    try {
                        MongoClientURI uri = new MongoClientURI(dbURIText);
                        MongoClient mongoClient = new MongoClient(uri);
                        MongoDatabase db = mongoClient.getDatabase(dbNameText);
                        user = new User();
                        testButton.setText(getString(R.string.first_glance_start));
                        progressBar.setVisibility(View.INVISIBLE);
                        editor = sharedPref.edit();
                        editor.putBoolean("pref_settings_database_usage", true);
                        editor.putString("pref_key_dbname", dbNameText);
                        editor.putString("pref_key_database_uri", dbURIText);
                        editor.apply();
                        Toast.makeText(this, "Connection was a success.", Toast.LENGTH_SHORT).show();


                    } catch (Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, "Could not connect. Check internet connection is established. Also, check that the database name and uri are correct.", Toast.LENGTH_LONG).show();
                        resetDatabaseSettings();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, "Please complete the above fields to test the database connection.", Toast.LENGTH_LONG).show();
                }

            } else {
                progressBar.setVisibility(View.VISIBLE);
                Util.findAllObjects(this, Util.USER_REFERENCE);
            }
        }
    }

    private  void resetDatabaseSettings() {
        editor = sharedPref.edit();
        editor.putString("pref_key_dbname", " ");
        editor.putString("pref_key_database_uri", " ");
        editor.putBoolean("pref_settings_database_usage", false);
        editor.apply();
        Button testButton = findViewById(R.id.first_glance_database_button);
        testButton.setText(getString(R.string.first_glance_test));
    }

    public void onUserSubmit(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            EditText firstName = findViewById(R.id.first_glance_first_name);
            String firstNameText = firstName.getText().toString();
            EditText lastName = findViewById(R.id.first_glance_last_name);
            String lastNameText = lastName.getText().toString();
            EditText password = findViewById(R.id.first_glance_password);
            String passwordText = password.getText().toString();
            if(!firstNameText.isEmpty() & !lastNameText.isEmpty() & !passwordText.isEmpty()) {
                user = new User();
                user.setFirstName(firstNameText);
                user.setLastName(lastNameText);
                user.setPassword(passwordText);
                user.setName(firstNameText + " " + lastNameText);
                user.setAdmin(true);
                EditText nickname = findViewById(R.id.first_glance_nickname);
                String nicknameText = nickname.getText().toString();
                if(!nicknameText.isEmpty()){
                    user.setNickname(nicknameText);
                }
                confirmFirstUserSetup(user, nickname);
            }
            else {
                Toast.makeText(this, "First name, last name, and password are required fields.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void confirmDatabaseSetup(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        Toast.makeText(getApplicationContext(), "Settings saved. Please log in using your existing username and password.", Toast.LENGTH_LONG).show();
                        finishActivity();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getApplicationContext(), "Create a first user. Enable database usage and provide your database settings under settings to setup database.", Toast.LENGTH_LONG).show();
                        resetDatabaseSettings();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("If you do not have an existing user account in this database, you will be" +
                " unable to access this application. Do you still wish to continue with providing " +
                "these settings?").setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    private void confirmFirstUserSetup(User user, EditText nickname){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        insertUser();
                        Toast.makeText(getApplicationContext(), "Settings saved. Please log in using your new username and password.", Toast.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have entered username: " + user.getFirstName() + " " +
                user.getLastName() + " password: " + user.getPassword() + " nickname: " + nickname.getText().toString() +
                ". Your username is your first name 'space' your last name. Is this information correct?").
                setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }
    private void insertUser() {
        progressBar.setVisibility(View.VISIBLE);
        Util.insertObject(this, Util.USER_REFERENCE, user);
    }

    private void finishActivity() {
        Intent intent = new Intent(this, LoginPage.class);
        intent.putExtra("after_first_time", true);
        startActivity(intent);
    }


    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
    Authentication authentication = Authentication.getAuthentication();
    authentication.setUser(user);
    return user.getName();
    }

    @Override
    public void onPostExecute(List<User> databaseObjects) {
        if(databaseObjects != null) {
            if(!databaseObjects.isEmpty()) {
                progressBar.setVisibility(View.INVISIBLE);
                confirmDatabaseSetup();
            }
            else {
                Toast.makeText(this, "No users found in this database.", Toast.LENGTH_SHORT).show();
                Toast.makeText(this,"Create a first user. Enable database usage and provide your database settings under settings to setup database.",Toast.LENGTH_LONG).show();
                resetDatabaseSettings();
            }
        }
        else {
            finishActivity();
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }
}
