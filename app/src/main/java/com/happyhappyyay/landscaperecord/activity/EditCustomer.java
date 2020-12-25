package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;
import java.util.Locale;

public class EditCustomer extends AppCompatActivity implements DatabaseAccess<Customer> {
    private EditText customerFirstName, customerLastName, customerEmail, customerBusiness, customerAddress,
            customerCity, customerPhoneNumber, customerMileage;
    private Spinner stateSpinner, daySpinner;
    private Customer customer;
    private String logInfo;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String customerID;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_customer);
        Toolbar myToolbar = findViewById(R.id.edit_customer_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        customerFirstName = findViewById(R.id.contact_first_name_text);
        customerLastName = findViewById(R.id.contact_last_name_text);
        customerEmail = findViewById(R.id.contact_email_text);
        customerBusiness = findViewById(R.id.contact_business_text);
        customerAddress = findViewById(R.id.contact_address_text);
        customerCity = findViewById(R.id.contact_city_text);
        customerMileage = findViewById(R.id.new_contact_mileage);
        customerPhoneNumber = findViewById(R.id.contact_phone_number_text);
        stateSpinner = findViewById(R.id.contact_state_spinner);
        daySpinner = findViewById(R.id.contact_day_spinner);
        progressBar = findViewById(R.id.edit_customer_progress_bar);
        Intent intent = getIntent();
        customerID = intent.getStringExtra("CUSTOMER_ID");
        findCustomer(customerID);
    }

    public void onSubmitCustomerUpdate(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            if (!customerFirstName.getText().toString().isEmpty() & !customerLastName.getText().toString().isEmpty()
                    & !customerAddress.getText().toString().isEmpty()) {
                customer.setFirst(customerFirstName.getText().toString());
                customer.setLast(customerLastName.getText().toString());

                if (!customerEmail.getText().toString().isEmpty()) {
                    customer.setEmail(customerEmail.getText().toString());
                }

                if (!customerBusiness.getText().toString().isEmpty()) {
                    customer.setBusiness(customerBusiness.getText().toString());
                }

                if (!customerCity.getText().toString().isEmpty()) {
                    customer.setCity(customerCity.getText().toString());
                }

                if (!customerPhoneNumber.getText().toString().isEmpty()) {
                    customer.setPhone(customerPhoneNumber.getText().toString());
                }

                if (daySpinner.getSelectedItem() != null) {
                    customer.setDay(daySpinner.getSelectedItem().toString());
                }

                if (stateSpinner.getSelectedItem() != null) {
                    customer.setState(stateSpinner.getSelectedItem().toString());
                }
            }
            updateCustomer();
        }
    }

    private void findCustomer(String customerID) {
        progressBar.setVisibility(View.VISIBLE);
        Util.findObjectByID(this, Util.CUSTOMER_REFERENCE, customerID);
    }

    private void loadCustomerInformation() {
        customerFirstName.setText(customer.getFirst());
        customerLastName.setText(customer.getLast());
        customerBusiness.setText(customer.getBusiness());
        customerCity.setText(customer.getCity());
        customerPhoneNumber.setText(customer.getPhone());
        customerEmail.setText(customer.getEmail());
        customerMileage.setText(customer.getMi() != null? String.format(Locale.US, "%.2f",customer.getMi()):"0");
        customerAddress.setText(customer.getAddress());
        String compareValue = customer.getDay();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditCustomer.this, R.array.days_of_the_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            daySpinner.setSelection(spinnerPosition);
        }
        String compareStateValue = customer.getState();
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(EditCustomer.this, R.array.US_states_list, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        if (compareStateValue != null) {
            int spinnerPosition = stateAdapter.getPosition(compareStateValue);
            stateSpinner.setSelection(spinnerPosition);
        }
    }

    private void updateCustomer() {
        logInfo = customer.getName();
        Util.updateObject(this, Util.CUSTOMER_REFERENCE, customer);
        Toast.makeText(getApplicationContext(), "Customer account for " + customer.getName() +
                " updated.", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return logInfo;
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        if(databaseObjects != null) {
            customer = databaseObjects.get(0);
            loadCustomerInformation();
        }
        progressBar.setVisibility(View.INVISIBLE);
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
