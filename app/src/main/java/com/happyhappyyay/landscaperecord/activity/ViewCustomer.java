package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.CustomerViewPager;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class ViewCustomer extends AppCompatActivity implements DatabaseAccess<Customer> {
    private final String ADAPTER_POS = "Adapter Position";
    private ViewPager pager;
    private int adapterPosition;
    private CustomerViewPager adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);
        Toolbar myToolbar = findViewById(R.id.view_customer_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        pager = findViewById(R.id.view_customer_view_pager);
        if (savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POS);
        }
        else {
            Intent intent = getIntent();
            adapterPosition = intent.getIntExtra("ADAPTER_POSITION",0);
        }

        retrieveCustomers();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        if(adapter != null) {
            outState.putInt(ADAPTER_POS, pager.getCurrentItem());
        }
        super.onSaveInstanceState(outState);
    }

    private void retrieveCustomers() {
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    @Override
    protected void onResume(){
        super.onResume();
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
    public void onPostExecute (List<Customer> databaseObjects) {
        adapter = new CustomerViewPager(this, databaseObjects);
        pager.setAdapter(adapter);
        pager.setCurrentItem(adapterPosition);
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
