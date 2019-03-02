package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class AddUser extends AppCompatActivity implements DatabaseAccess<User> {
    private EditText firstName, lastName, password, hours, nickname;
    private User user;
    private CheckBox admin;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar myToolbar = findViewById(R.id.add_user_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        firstName = findViewById(R.id.add_user_first);
        lastName = findViewById(R.id.add_user_last);
        nickname = findViewById(R.id.add_user_nickname);
        password = findViewById(R.id.add_user_pass);
        hours = findViewById(R.id.add_user_hours);
        admin = findViewById(R.id.add_user_admin_button);
        progressBar = findViewById(R.id.add_user_progress_bar);
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

    public void addNewUser(View view) {
        boolean error = false;
        if(progressBar.getVisibility() == View.INVISIBLE) {
            if (!firstName.getText().toString().isEmpty() & !lastName.getText().toString().isEmpty()) {
                if (password.getText().toString().length() > 5) {
                    user = new User(firstName.getText().toString(),lastName.getText().toString(),password.getText().toString());
                    if (hours.getText().toString().isEmpty()) {
                        user.setHours(0);
                    } else {
                            try {
                                user.setHours(Double.parseDouble(hours.getText().toString()));

                            } catch (Exception e) {
                                Toast.makeText(getApplicationContext(), getString(R.string.add_user_hour_input_error), Toast.LENGTH_LONG).show();
                                hours.setText("");
                                error = true;
                            }
                    }
                    user.setAdmin(admin.isChecked());
                    user.setNickname(nickname.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.add_user_password_input_error), Toast.LENGTH_LONG).show();
                    error = true;
                    password.setText("");
                }

                if (!error) {
                    insertUser();
                }
            }
        }
    }

    private void insertUser() {
        progressBar.setVisibility(View.VISIBLE);
        Util.insertObject(this, Util.USER_REFERENCE, user);
        Toast.makeText(getApplicationContext(), "User account for " + user.getName() +
                " created.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return user.getName();
    }

    @Override
    public void onPostExecute(List databaseObjects) {

    }
}
