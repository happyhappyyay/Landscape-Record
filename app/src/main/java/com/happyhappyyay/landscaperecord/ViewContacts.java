package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

public class ViewContacts extends AppCompatActivity implements DatabaseAccess<Customer>{
    private RecyclerView recyclerView;
    private RecyclerViewCustomersAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase db;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);
        Toolbar myToolbar = findViewById(R.id.view_contacts_toolbar);
        setSupportActionBar(myToolbar);
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
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
        //        new AsyncTask<Void, Void, List<Customer>>() {
//            @Override
//            protected List<Customer> doInBackground(Void... voids) {
//                return db.customerDao().getAllCustomers();
//            }
//
//            @Override
//            protected void onPostExecute(List<Customer> customers) {
//
//                adapter = new RecyclerViewCustomersAdapter(ViewContacts.this, customers);
//                recyclerView.setAdapter(adapter);
//
//            }
//        }.execute();
    }

    @Override
    protected void onResume(){
        super.onResume();
        getCustomers();
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_contact).setEnabled(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return Util.toolbarItemSelection(this, item);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setObjectsToAccessor(List<Customer> databaseObjects) {
        adapter = new RecyclerViewCustomersAdapter(ViewContacts.this, databaseObjects);
        recyclerView.setAdapter(adapter);
    }
}
