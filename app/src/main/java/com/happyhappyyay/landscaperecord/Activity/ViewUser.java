package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.User;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Authentication;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.List;
import java.util.Locale;

public class ViewUser extends AppCompatActivity implements DatabaseAccess<User> {
    private String userID;
    private TextView name, password, hours;
    private User user;
    private ProgressBar progressBar;
    private final String USER_ID = "User ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        name = findViewById(R.id.view_user_name);
        password = findViewById(R.id.view_user_password);
        hours = findViewById(R.id.view_user_hours);
        Intent intent = getIntent();
        userID = intent.getStringExtra("USER_ID");
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            userID = savedInstanceState.getString(USER_ID);
        }
        progressBar = findViewById(R.id.view_user_progress_bar);
        findUser(userID);
    }

    private void findUser(String userId) {
        progressBar.setVisibility(View.VISIBLE);
        Util.findObjectByID(this, Util.USER_REFERENCE, userId);
    }

    private void deleteUser() {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            if (!user.equals(Authentication.getAuthentication().getUser())) {
                progressBar.setVisibility(View.VISIBLE);
                Util.deleteObject(this, Util.USER_REFERENCE, user);
            } else {
                Toast.makeText(getApplicationContext(), "Cannot delete logged in Admin", Toast.LENGTH_LONG).show();
            }
        }
    }

    public void onDeleteUser(View view) {
        if (user != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteUser();
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
        if(progressBar.getVisibility() == View.INVISIBLE) {
            Intent intent = new Intent(this, EditUser.class);
            intent.putExtra("USER_ID", user.getUserId());
            startActivity(intent);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(USER_ID, userID);
        super.onSaveInstanceState(outState);
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
    public void onPostExecute(List<User> databaseObjects) {
        if(databaseObjects != null) {
            user = databaseObjects.get(0);
            if (user != null) {
                name.setText(user.getName());
                password.setText(user.getPassword());
                hours.setText( String.format(Locale.US, "%.2f", user.getHours()));
            } else {
                Toast.makeText(getApplicationContext(), "Could not find user", Toast.LENGTH_LONG).show();
            }
        }
        else {
            finish();
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}
