package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoDatabase;

import java.util.List;

public class FirstGlance extends AppCompatActivity implements MultiDatabaseAccess<User> {
    private ConstraintLayout userLayout;
    private ConstraintLayout databaseLayout;
    private TextView importDb;
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
        importDb = findViewById(R.id.first_glance_import);
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
            String testButtonText = testButton.getText().toString();
            String testText = getString(R.string.first_glance_test);
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
                        testButton.setText(getString(R.string.first_glance_start));
                        user = new User();
                        user.setName("NEW_DEVICE");
                        progressBar.setVisibility(View.INVISIBLE);
                        editor = sharedPref.edit();
                        editor.putBoolean(Util.retrieveStringFromResources(R.string.pref_key_database_usage, this), true);
                        editor.putString(Util.retrieveStringFromResources(R.string.pref_key_database_name,this), dbNameText);
                        editor.putString(Util.retrieveStringFromResources(R.string.pref_key_database_uri, this), dbURIText);
                        editor.apply();
                        Toast.makeText(this, getString(R.string.first_glance_success), Toast.LENGTH_SHORT).show();

                    } catch (Exception e) {
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(this, getString(R.string.first_glance_check_connection), Toast.LENGTH_LONG).show();
                        resetDatabaseSettings();
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(this, getString(R.string.first_glance_complete_information), Toast.LENGTH_LONG).show();
                }

            } else {
                progressBar.setVisibility(View.VISIBLE);
                Authentication.getAuthentication().setUser(user);
                Util.findAllObjects(this, Util.USER_REFERENCE);
            }
        }
    }

    private  void resetDatabaseSettings() {
        editor = sharedPref.edit();
        editor.putString(Util.retrieveStringFromResources(R.string.pref_key_database_name,this), " ");
        editor.putString(Util.retrieveStringFromResources(R.string.pref_key_database_uri, this), " ");
        editor.putBoolean(Util.retrieveStringFromResources(R.string.pref_key_database_usage, this), false);
        editor.apply();
        OnlineDatabase.getOnlineDatabase(this).resetDatabaseInstance();
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
            if(!firstNameText.isEmpty() & !lastNameText.isEmpty() & passwordText.length() > 5) {
                user = new User(firstNameText,lastNameText,passwordText);
                user.setAdmin(true);
                EditText nickname = findViewById(R.id.first_glance_nickname);
                String nicknameText = nickname.getText().toString();
                if(!nicknameText.isEmpty()){
                    user.setNickname(nicknameText);
                }
                confirmFirstUserSetup(user, nickname);
            }
            else {
                Toast.makeText(this, getString(R.string.first_glance_required_fields), Toast.LENGTH_LONG).show();
            }
        }
    }

    private void confirmDatabaseSetup(){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        progressBar.setVisibility(View.VISIBLE);
                        importDb.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(), getString(R.string.first_glance_settings_saved), Toast.LENGTH_LONG).show();
                        Util.enactMultipleDatabaseOperationsPostExecute(FirstGlance.this);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        Toast.makeText(getApplicationContext(), getString(R.string.first_glance_retry_connection), Toast.LENGTH_LONG).show();
                        resetDatabaseSettings();
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.first_glance_existing_warning)).setPositiveButton(R.string.yes, dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }

    private void confirmFirstUserSetup(User user, EditText nickname){
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        insertUser();
                        Toast.makeText(getApplicationContext(), getString(R.string.first_glance_settings_saved), Toast.LENGTH_LONG).show();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("You have entered username: " + user.getFirst() + " " +
                user.getLast() + " password: " + user.getPassword() + " nickname: " + nickname.getText().toString() +
                ". Your username is your first name 'space' your last name. Is this information correct?").
                setPositiveButton(getString(R.string.yes), dialogClickListener)
                .setNegativeButton(getString(R.string.no), dialogClickListener).show();
    }
    private void insertUser() {
        progressBar.setVisibility(View.VISIBLE);
        Util.insertObject(this, Util.USER_REFERENCE, user);
    }

    private void finishActivity() {
        Intent intent = new Intent(this, LoginPage.class);
        intent.putExtra(Util.retrieveStringFromResources(R.string.pref_key_after_first_time, this), true);
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
                Toast.makeText(this, getString(R.string.first_glance_no_users), Toast.LENGTH_SHORT).show();
                Toast.makeText(this,getString(R.string.first_glance_retry_connection),Toast.LENGTH_LONG).show();
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

    @Override
    public void accessDatabaseMultipleTimes() {
        if(OnlineDatabase.connectionIsValid(this)){
            Util.updateDatabases(this);
        }
    }

    @Override
    public void createCustomLog() {
    }
}
