package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.BillingViewPager;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;

import static com.happyhappyyay.landscaperecord.activity.TimeReporting.ADAPTER_POSITION;

public class BillCreation extends AppCompatActivity implements DatabaseAccess<Customer>, AdapterView.OnItemSelectedListener {
    protected static String MONTH_POSITION = "Month position";
    List<Customer> customers;
    private int monthSelectionPosition;
    private int customerSelectionPosition;
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private BillingViewPager adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        final int ZERO_POSITION = 1;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill_creation);
        Toolbar myToolbar = findViewById(R.id.bill_creation_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        viewPager = findViewById(R.id.service_pricing_view_pager);
        Spinner monthSpinner = findViewById(R.id.service_pricing_months_spinner);
        monthSelectionPosition = Util.retrieveMonthFromLong(Util.retrieveLongCurrentDate());
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        if(savedInstanceState != null) {
            monthSelectionPosition = savedInstanceState.getInt(MONTH_POSITION);
            customerSelectionPosition = savedInstanceState.getInt(ADAPTER_POSITION);
        }
        monthSpinner.setSelection(monthSelectionPosition-ZERO_POSITION);
        progressBar = findViewById(R.id.services_pricing_progress_bar);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapter != null) {
                    monthSelectionPosition = i+ZERO_POSITION;
                    adapter.updateMonthAndCustomerSelection(monthSelectionPosition, createUnpricedCustomersForMonthList(customers));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        findAllCustomers();
    }

    private void findAllCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    private void populateSpinner(List<Customer> customers) {
        final int ZERO_POSITION = 1;
        final String ALL_CUSTOMERS = "All Customers";
        String[] arraySpinner = new String[customers.size() + ZERO_POSITION];
        int pos = customerSelectionPosition;
        for (int i = 0; i < customers.size() + ZERO_POSITION; i++) {
            if(i != 0) {
                arraySpinner[i] = customers.get(i-ZERO_POSITION).getAddress();
            }
            else {
                arraySpinner[i] = ALL_CUSTOMERS;

            }
        }

        Spinner s = findViewById(R.id.service_pricing_customer_spinner);
        ArrayAdapter<String> adapterCustomer = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapterCustomer);
        s.setOnItemSelectedListener(this);
        s.setSelection(pos);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        final int ZERO_POSITION = 1;
        if(i != 0) {
            Customer customer = customers.get(i-ZERO_POSITION);
            List<Customer> customerSelection = new ArrayList<>();
            if(customer.hasUnpricedServicesForMonth(monthSelectionPosition)) {
                customerSelection.add(customer);
            }
            customerSelectionPosition = i;
            adapter.updateCustomersSelection(customerSelection);
        }
        else {
            List<Customer> unpricedCustomers = createUnpricedCustomersForMonthList(customers);
            adapter.updateCustomersSelection(unpricedCustomers);
        }
    }

    private List<Customer> createUnpricedCustomersForMonthList(List<Customer> customers) {
        List<Customer> customersWithUnpricedServices = new ArrayList<>();

        for(Customer c: customers) {
            if(c.hasUnpricedServicesForMonth(monthSelectionPosition)) {
                customersWithUnpricedServices.add(c);
            }
        }
        return customersWithUnpricedServices;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
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
        customers = databaseObjects;
        populateSpinner(customers);
        adapter = new BillingViewPager(this, createUnpricedCustomersForMonthList(customers), monthSelectionPosition);
        viewPager.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(MONTH_POSITION, monthSelectionPosition);
        outState.putInt(ADAPTER_POSITION, customerSelectionPosition);
        super.onSaveInstanceState(outState);
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



