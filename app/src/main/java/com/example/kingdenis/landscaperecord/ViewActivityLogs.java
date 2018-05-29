package com.example.kingdenis.landscaperecord;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewActivityLogs extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerLogAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_logs);
        recyclerView = (RecyclerView) findViewById(R.id.view_logs_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        db = AppDatabase.getAppDatabase(this);
        getLogs();
    }

    private void getLogs() {
        new AsyncTask<Void, Void, List<LogActivity>>() {
            @Override
            protected List<LogActivity> doInBackground(Void... voids) {
                return db.logDao().getAllLogs();
            }

            @Override
            protected void onPostExecute(List<LogActivity> logs) {
                adapter = new RecyclerLogAdapter(logs);
                recyclerView.setAdapter(adapter);
            }
        }.execute();
    }
}
