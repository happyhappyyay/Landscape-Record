package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

public class ViewUsers extends AppCompatActivity implements DatabaseAccess<User> {
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        RecyclerView.LayoutManager layoutManager;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        Toolbar myToolbar = findViewById(R.id.view_users_toolbar);
        setSupportActionBar(myToolbar);
        recyclerView = findViewById(R.id.view_users_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        getUsers();

    }

    public void onAddClick(View view) {
        Intent intent = new Intent(this, AddUser.class);
        startActivity(intent);
    }

    private void getUsers() {
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
    }
}
