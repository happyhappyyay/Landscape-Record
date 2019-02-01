package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;

import com.happyhappyyay.landscaperecord.Adapter.UserViewPagerAdapter;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseOperator;
import com.happyhappyyay.landscaperecord.DatabaseInterface.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.LogActivity;
import com.happyhappyyay.landscaperecord.POJO.User;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.AppDatabase;
import com.happyhappyyay.landscaperecord.Utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.ArrayList;
import java.util.List;

public class ViewUser extends AppCompatActivity implements MultiDatabaseAccess<User> {
    private final String ADAPTER_POS = "Adapter Position";
    private ViewPager pager;
    private int adapterPosition;
    private UserViewPagerAdapter adapter;
    private ProgressBar progressBar;
    private List<LogActivity> logs;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        pager = findViewById(R.id.view_user_view_pager);
        progressBar = findViewById(R.id.view_user_progress_bar);
        users = new ArrayList<>();
        logs = new ArrayList<>();
        if (savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POS);
        }
        else {
            Intent intent = getIntent();
            adapterPosition = intent.getIntExtra("ADAPTER_POSITION",0);
        }

        retrieveUsersAndLogs();
    }

    private void retrieveUsersAndLogs() {
        progressBar.setVisibility(View.VISIBLE);
        Util.enactMultipleDatabaseOperationsPostExecute(this);
    }

//    private void deleteUser() {
//        if(progressBar.getVisibility() == View.INVISIBLE) {
//            if (!user.equals(Authentication.getAuthentication().getUser())) {
//                progressBar.setVisibility(View.VISIBLE);
//                Util.deleteObject(this, Util.USER_REFERENCE, user);
//            } else {
//                Toast.makeText(getApplicationContext(), "Cannot delete logged in Admin", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
//
//    public void onDeleteUser(View view) {
//        if (user != null) {
//            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    switch (which) {
//                        case DialogInterface.BUTTON_POSITIVE:
//                            deleteUser();
//                            break;
//
//                        case DialogInterface.BUTTON_NEGATIVE:
//                            break;
//                    }
//                }
//            };
//
//            AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setMessage("Are you sure you want to delete user " + user.getName() + " ?")
//                    .setPositiveButton("Yes", dialogClickListener)
//                    .setNegativeButton("No", dialogClickListener).show();
//        }
//    }
//
//    public void onEditUser(View view) {
//        if(progressBar.getVisibility() == View.INVISIBLE) {
//            Intent intent = new Intent(this, EditUser.class);
//            intent.putExtra("USER_ID", user.getUserId());
//            startActivity(intent);
//        }
//    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(adapter != null) {
            outState.putInt(ADAPTER_POS, pager.getCurrentItem());
        }
        super.onSaveInstanceState(outState);
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
        adapter = new UserViewPagerAdapter(this, users, logs);
        pager.setAdapter(adapter);
        pager.setCurrentItem(adapterPosition);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        if(Util.hasOnlineDatabaseEnabled(this)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
                databaseAccessMethod(db);
            } catch (Exception e) {
                AppDatabase db = AppDatabase.getAppDatabase(this);
                databaseAccessMethod(db);
            }
        }
        else {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            databaseAccessMethod(db);
        }
    }
    private void databaseAccessMethod(DatabaseOperator db) {
        users = Util.USER_REFERENCE.retrieveAllClassInstancesFromDatabase(db);
        logs = Util.LOG_REFERENCE.retrieveAllClassInstancesFromDatabase(db);
    }

    @Override
    public void createCustomLog() {

    }
}
