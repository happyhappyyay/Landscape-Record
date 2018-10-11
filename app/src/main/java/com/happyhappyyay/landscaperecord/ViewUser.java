package com.happyhappyyay.landscaperecord;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import static com.happyhappyyay.landscaperecord.TimeReporting.ADAPTER_POSITION;

public class ViewUser extends AppCompatActivity {
    private int userID;
    private TextView name, password, hours;
    private AppDatabase db;
    private Authentication authentication;
    private User user;
    private final String USER_ID = "User ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        db = AppDatabase.getAppDatabase(this);
        name = findViewById(R.id.view_user_name);
        password = findViewById(R.id.view_user_password);
        hours = findViewById(R.id.view_user_hours);
        authentication = Authentication.getAuthentication(this);
        Intent intent = getIntent();
        userID = intent.getIntExtra("USER_ID", 0);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            userID = savedInstanceState.getInt(USER_ID);
        }
        findUser(userID);
    }

    //    TODO: make static
    private void findUser(Integer userId) {
        new AsyncTask<Integer, Void, User>() {
            @Override
            protected User doInBackground(Integer... integers) {

                return db.userDao().findUserByID(integers[0]);
            }

            @Override
            protected void onPostExecute(User user) {
                if (user != null) {
                    name.setText(user.getName());
                    password.setText(user.getPassword());
                    hours.setText(Double.toString(user.getHours()));
                    ViewUser.this.user = user;
                } else {
                    Toast.makeText(getApplicationContext(), "Could not find user", Toast.LENGTH_LONG).show();
                }
            }
        }.execute(userId);
    }

    //TODO: try static
    private void deleteUser(User user) {
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... params) {
                User user = params[0];
                LogActivity log = new LogActivity(authentication.getUser().getName(), user.getName(),1, 0);
                db.logDao().insert(log);
                db.userDao().deleteUser(user);
                finish();
                return null;
            }
        }.execute(user);
    }

    public void onDeleteUser(View view) {
        if (user != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteUser(user);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete user " + user.getName() + " ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    public void onEditUser(View view) {
        Intent intent = new Intent(this, EditUser.class);
        intent.putExtra("USER_REFERENCE", user.getName());
        startActivity(intent);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(USER_ID, userID);
        super.onSaveInstanceState(outState);
    }
}
