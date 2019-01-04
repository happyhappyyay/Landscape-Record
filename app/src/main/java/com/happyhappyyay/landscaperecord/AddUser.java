package com.happyhappyyay.landscaperecord;

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

import java.util.List;

public class AddUser <T extends DatabaseObjects<T>> extends AppCompatActivity implements DatabaseAccess<User> {
    private EditText firstName, lastName, password, hours, nickname;
    private User user;
    private CheckBox admin;
    private String logInfo;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        Toolbar myToolbar = findViewById(R.id.add_user_toolbar);
        setSupportActionBar(myToolbar);
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
                user = new User();
                user.setName(firstName.getText().toString() + " " + lastName.getText().toString());
                if (password.getText().toString().length() > 5) {
                    user.setPassword(password.getText().toString());
                } else {
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters" +
                            " long", Toast.LENGTH_LONG).show();
                    error = true;
                    password.setText("");
                }
                if (hours.getText().toString().isEmpty()) {
                    user.setHours(0);
                } else {
                    if (!error) {
                        try {
                            user.setHours(Double.parseDouble(hours.getText().toString()));

                        } catch (Exception e) {
                            Toast.makeText(getApplicationContext(), "Hours must be entered as whole" +
                                    " (e.g., 5) or real (e.g., 5.0) numbers", Toast.LENGTH_LONG).show();
                            hours.setText("");
                            error = true;
                        }
                    }

                }
                user.setAdmin(admin.isChecked());
                user.setNickname(nickname.getText().toString());
                if (!error) {
                    insertUser();
                }

            }
        }
    }

    private void insertUser() {
        progressBar.setVisibility(View.VISIBLE);
        logInfo = user.getName();
        Util.insertObject(this, Util.USER_REFERENCE, user);
        Toast.makeText(getApplicationContext(), "User account for " + user.getName() +
                " created.", Toast.LENGTH_LONG).show();
        finish();

//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                LogActivity log = new LogActivity("User", user.getName(),0, 0);
//                db.userDao().insert(user);
//                db.logDao().insert(log);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                Toast.makeText(getApplicationContext(), "User account for " + user.getName() +
//                        " created.", Toast.LENGTH_LONG).show();
//
//
//                finish();
//            }
//        }.execute();
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
