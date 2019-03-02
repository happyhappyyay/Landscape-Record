package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;


public class AddCustomer extends AppCompatActivity implements DatabaseAccess<Customer> {
    private EditText firstNameText, lastNameText, emailText, businessText, addressText, cityText,
            phoneText;
    private Spinner stateSpinner, daySpinner;
    private Customer customer;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_customer);
        Toolbar myToolbar = findViewById(R.id.new_contact_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        firstNameText = findViewById(R.id.contact_first_name_text);
        lastNameText = findViewById(R.id.contact_last_name_text);
        emailText = findViewById(R.id.contact_email_text);
        businessText = findViewById(R.id.contact_business_text);
        addressText = findViewById(R.id.contact_address_text);
        cityText = findViewById(R.id.contact_city_text);
        phoneText = findViewById(R.id.contact_phone_number_text);
        stateSpinner = findViewById(R.id.contact_state_spinner);
        daySpinner = findViewById(R.id.contact_day_spinner);
        progressBar = findViewById(R.id.new_contact_progress_bar);
    }

    public void addNewCustomer(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            if (!firstNameText.getText().toString().isEmpty() & !lastNameText.getText().toString().isEmpty()
                    & !addressText.getText().toString().isEmpty()) {
                customer = new Customer(firstNameText.getText().toString(), lastNameText.getText().
                        toString(), addressText.getText().toString());

                if (!emailText.getText().toString().isEmpty()) {
                    customer.setEmail(emailText.getText().toString());
                }

                if (!businessText.getText().toString().isEmpty()) {
                    customer.setBusiness(businessText.getText().toString());
                }

                if (!cityText.getText().toString().isEmpty()) {
                    customer.setCity(cityText.getText().toString());
                }

                if (!phoneText.getText().toString().isEmpty()) {
                    customer.setPhone(phoneText.getText().toString());
                }

                if (daySpinner.getSelectedItem() != null) {
                    customer.setDay(daySpinner.getSelectedItem().toString());
                }

                if (stateSpinner.getSelectedItem() != null) {
                    customer.setState(stateSpinner.getSelectedItem().toString());
                }

                insertCustomer();
            }
        }
    }

    private void insertCustomer() {
        progressBar.setVisibility(View.VISIBLE);
        Util.insertObject(this, Util.CUSTOMER_REFERENCE, customer);
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
        return customer.getName();
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        Toast.makeText(getApplicationContext(), "Customer account for " + customer.getName() +
                " created.", Toast.LENGTH_LONG).show();
        finish();
    }
}
