package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.List;

public class ViewUsers extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewUsersAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        Toolbar myToolbar = findViewById(R.id.view_users_toolbar);
        setSupportActionBar(myToolbar);
        db = AppDatabase.getAppDatabase(this);
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
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                return db.userDao().getAllUsers();
            }

            @Override
            protected void onPostExecute(List<User> users) {

                adapter = new RecyclerViewUsersAdapter(ViewUsers.this, users);
                recyclerView.setAdapter(adapter);

            }
        }.execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
        getUsers();
    }
}
