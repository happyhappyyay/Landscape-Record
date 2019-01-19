package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ProgressBar;

import com.happyhappyyay.landscaperecord.Adapter.RecyclerLogAdapter;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.LogActivity;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.List;

public class ViewActivityLogs extends AppCompatActivity implements DatabaseAccess<LogActivity> {

    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        RecyclerView.LayoutManager layoutManager;
        setContentView(R.layout.activity_view_logs);
        recyclerView = findViewById(R.id.view_logs_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.view_logs_progress_bar);
        getLogs();
    }

    private void getLogs() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.LOG_REFERENCE);
//        new AsyncTask<Void, Void, List<LogActivity>>() {
//            @Override
//            protected List<LogActivity> doInBackground(Void... voids) {
//                return db.logDao().getAllLogs();
//            }
//
//            @Override
//            protected void onPostExecute(List<LogActivity> logs) {
//                adapter = new RecyclerLogAdapter(logs);
//                recyclerView.setAdapter(adapter);
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
    public void onPostExecute(List<LogActivity> databaseObjects) {
        RecyclerLogAdapter adapter;
        adapter = new RecyclerLogAdapter(databaseObjects);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
