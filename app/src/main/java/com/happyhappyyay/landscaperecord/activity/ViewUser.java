package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.UserViewPager;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;

public class ViewUser extends AppCompatActivity implements MultiDatabaseAccess<User> {
    private final String ADAPTER_POS = "Adapter Position";
    private ViewPager pager;
    private int adapterPosition;
    private UserViewPager adapter;
    private ProgressBar progressBar;
    private List<LogActivity> logs;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_user);
        Toolbar myToolbar = findViewById(R.id.view_user_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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
        adapter = new UserViewPager(this, users, logs);
        pager.setAdapter(adapter);
        pager.setCurrentItem(adapterPosition);
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        if(Util.hasOnlineDatabaseEnabledAndValid(this)) {
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
