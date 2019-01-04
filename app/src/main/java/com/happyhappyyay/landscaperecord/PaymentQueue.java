package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import java.util.ArrayList;
import java.util.List;

import static com.happyhappyyay.landscaperecord.TimeReporting.ADAPTER_POSITION;

public class PaymentQueue extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatabaseAccess<Customer> {
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerServicePaymentAdapter adapter;
    private List<Customer> customers;
    private int adapterPosition;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_queue);
        customers = new ArrayList<>();
        Toolbar myToolbar = findViewById(R.id.payment_queue_toolbar);
        setSupportActionBar(myToolbar);
        recyclerView = findViewById(R.id.payment_queue_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        progressBar = findViewById(R.id.payment_queue_progress_bar);
        if(savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
        }
        progressBar.setVisibility(View.VISIBLE);
        findAllUsers();
    }

    private void findAllUsers(){
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
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
        progressBar.setVisibility(View.INVISIBLE);
    }

    private void populateSpinner(List<Customer> customers) {
        String[] arraySpinner = new String[customers.size()];
        int pos = adapterPosition;
        for (int i = 0; i < customers.size(); i++) {
            arraySpinner[i] = customers.get(i).getCustomerAddress();
        }

        Spinner s = findViewById(R.id.payment_queue_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);
        s.setSelection(pos);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        Customer customer = customers.get(i);
        adapterPosition = i;
        adapter = new RecyclerServicePaymentAdapter(customer);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }
}
