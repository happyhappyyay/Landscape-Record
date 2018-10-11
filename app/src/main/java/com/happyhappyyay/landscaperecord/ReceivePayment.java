package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

public class ReceivePayment extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatabaseAccess<Customer> {

    private int adapterPosition;
    private int pos;
    private List<Customer> customers;
    private Customer customer;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_payment);
        Toolbar myToolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(myToolbar);
        db = AppDatabase.getAppDatabase(this);
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

    private void populateSpinner(List<Customer> customers) {
        String[] arraySpinner = new String[customers.size()];
        int pos = adapterPosition;
        for (int i = 0; i < customers.size(); i++) {
            arraySpinner[i] = customers.get(i).getName();
        }

        Spinner s = findViewById(R.id.receive_payment_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);
        s.setSelection(pos);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (customers != null) {
            Toast.makeText(adapterView.getContext(),
                    "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                    Toast.LENGTH_SHORT).show();
            customer = customers.get(pos);
            adapterPosition = pos;
        } else {
            adapterView.setSelection(adapterPosition);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    private void findAllCustomers() {
//        new AsyncTask<Void, Void, Void>() {
////            @Override
////            protected Void doInBackground(Void... voids) {
////                customers = db.customerDao().getAllCustomers();
////
////                return null;
////            }
////        }.execute();
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public void setObjectsToAccessor(List<Customer> databaseObjects) {
        customers.addAll(databaseObjects);
        populateSpinner(customers);
    }
}
