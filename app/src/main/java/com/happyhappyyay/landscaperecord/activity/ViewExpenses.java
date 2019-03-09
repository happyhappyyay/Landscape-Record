package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.RecyclerViewExpenses;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Expense;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class ViewExpenses extends AppCompatActivity implements DatabaseAccess<Expense> {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_expenses);
        Toolbar myToolbar = findViewById(R.id.view_expenses_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        recyclerView = findViewById(R.id.view_expenses_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.view_expenses_progress_bar);
        getExpenses();
    }

    public void getExpenses() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this,Util.EXPENSE_REFERENCE);
    }

    public void onAddClick(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            Intent intent = new Intent(this, AddExpense.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();
        getExpenses();
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
    public void onPostExecute(List<Expense> databaseObjects) {
        RecyclerViewExpenses adapter = new RecyclerViewExpenses(this, databaseObjects);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
