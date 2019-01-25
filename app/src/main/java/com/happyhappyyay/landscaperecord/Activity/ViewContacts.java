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

import com.happyhappyyay.landscaperecord.Adapter.RecyclerViewCustomersAdapter;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.List;

public class ViewContacts extends AppCompatActivity implements DatabaseAccess<Customer> {
    private RecyclerView recyclerView;
    private RecyclerViewCustomersAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_contacts);
        Toolbar myToolbar = findViewById(R.id.view_contacts_toolbar);
        setSupportActionBar(myToolbar);
        recyclerView = findViewById(R.id.view_customers_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.view_contacts_progress_bar);
        getCustomers();
    }

    public void onAddClick(View view) {
            Intent intent = new Intent(this, NewContact.class);
            startActivity(intent);
    }

    private void getCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    @Override
    protected void onResume(){
        super.onResume();
        getCustomers();
    }

    public void startReceivePayment(View view) {
        Intent intent = new Intent(this, ReceivePayment.class);
        startActivity(intent);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_contact).setEnabled(false);
        return true;
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

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return null;
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        adapter = new RecyclerViewCustomersAdapter(ViewContacts.this, databaseObjects);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
