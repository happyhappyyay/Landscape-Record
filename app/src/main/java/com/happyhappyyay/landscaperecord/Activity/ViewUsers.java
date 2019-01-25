package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.Adapter.RecyclerViewUsersAdapter;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.User;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Authentication;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.List;

public class ViewUsers extends AppCompatActivity implements DatabaseAccess<User> {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView.LayoutManager layoutManager;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        User user = Authentication.getAuthentication().getUser();
        Toolbar myToolbar = findViewById(R.id.user_settings_toolbar);
        setSupportActionBar(myToolbar);
        TextView hoursText = findViewById(R.id.view_users_hours_text);
        hoursText.setVisibility(!user.isAdmin() ? View.GONE : View.VISIBLE);
        recyclerView = findViewById(R.id.view_users_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.view_users_progress_bar);
        getUsers();

    }

    public void onAddClick(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            Intent intent = new Intent(this, AddUser.class);
            startActivity(intent);
        }
    }

    public void startHourOperations(View view) {
        Intent intent = new Intent(this, HourOperations.class);
        startActivity(intent);
    }

    private void getUsers() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.USER_REFERENCE);
//        new AsyncTask<Void, Void, List<User>>() {
//            @Override
//            protected List<User> doInBackground(Void... voids) {
//                return db.userDao().getAllUsers();
//            }
//
//            @Override
//            protected void onPostExecute(List<User> users) {
//
//                adapter = new RecyclerViewUsersAdapter(ViewUsers.this, users);
//                recyclerView.setAdapter(adapter);
//
//            }
//        }.execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
        getUsers();
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
        RecyclerViewUsersAdapter adapter;
        adapter = new RecyclerViewUsersAdapter(this, databaseObjects);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
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
