package com.happyhappyyay.landscaperecord.activity;

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

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.RecyclerViewCustomers;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class ViewCustomers extends AppCompatActivity implements DatabaseAccess<Customer> {
    private RecyclerView recyclerView;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customers);
        User user = Authentication.getAuthentication().getUser();
        Toolbar myToolbar = findViewById(R.id.view_contacts_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        TextView services = findViewById(R.id.view_contacts_view_services);
        TextView invoices = findViewById(R.id.view_contacts_create_invoices);
        services.setVisibility(!user.isAdmin() ? View.GONE : View.VISIBLE);
        invoices.setVisibility(!user.isAdmin() ? View.GONE : View.VISIBLE);
        recyclerView = findViewById(R.id.view_customers_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.view_contacts_progress_bar);
        getCustomers();
    }

    public void onAddClick(View view) {
            Intent intent = new Intent(this, AddCustomer.class);
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

    public void startViewServices(View view) {
        Intent intent = new Intent(this, ViewServices.class);
        startActivity(intent);
    }

    public void startBillCreation(View view) {
        Intent intent = new Intent(this, BillCreation.class);
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
        RecyclerViewCustomers adapter = new RecyclerViewCustomers(ViewCustomers.this, databaseObjects);
        recyclerView.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }

}
