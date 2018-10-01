package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.List;

public class ViewContacts extends AppCompatActivity {
    private RecyclerView recyclerView;
    private RecyclerViewCustomersAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);
        db = AppDatabase.getAppDatabase(this);
        recyclerView = findViewById(R.id.view_customers_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        getCustomers();
    }

    public void onAddClick(View view) {
        Intent intent = new Intent(this, NewContact.class);
        startActivity(intent);
    }

    private void getCustomers() {
        new AsyncTask<Void, Void, List<Customer>>() {
            @Override
            protected List<Customer> doInBackground(Void... voids) {
                return db.customerDao().getAllCustomers();
            }

            @Override
            protected void onPostExecute(List<Customer> customers) {

                adapter = new RecyclerViewCustomersAdapter(ViewContacts.this, customers);
                recyclerView.setAdapter(adapter);

            }
        }.execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
        getCustomers();
    }
}
