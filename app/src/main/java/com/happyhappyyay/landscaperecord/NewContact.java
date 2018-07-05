package com.happyhappyyay.landscaperecord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NewContact extends AppCompatActivity {
    private static final String TAG = "Customer List";
    private EditText firstNameText, lastNameText, emailText, businessText, addressText, cityText,
            phoneText, weekDay;
    private Spinner stateSpinner, daySpinner;
    private Customer customer;
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        Toolbar myToolbar = findViewById(R.id.new_contact_toolbar);
        setSupportActionBar(myToolbar);
        firstNameText = findViewById(R.id.contact_first_name_text);
        lastNameText = findViewById(R.id.contact_last_name_text);
        emailText = findViewById(R.id.contact_email_text);
        businessText = findViewById(R.id.contact_business_text);
        addressText = findViewById(R.id.contact_address_text);
        cityText = findViewById(R.id.contact_city_text);
        phoneText = findViewById(R.id.contact_phone_number_text);
        stateSpinner = findViewById(R.id.contact_state_spinner);
        daySpinner = findViewById(R.id.contact_day_spinner);
        db = AppDatabase.getAppDatabase(this);
    }

    public void addNewCustomer(View view) {
        if (!firstNameText.getText().toString().isEmpty() & !lastNameText.getText().toString().isEmpty()
                & !addressText.getText().toString().isEmpty()) {
            customer = new Customer(firstNameText.getText().toString(), lastNameText.getText().
                    toString(), addressText.getText().toString());

            if (!emailText.getText().toString().isEmpty()) {
                customer.setCustomerEmail(emailText.getText().toString());
            }

            if (!businessText.getText().toString().isEmpty()) {
                customer.setCustomerBusiness(businessText.getText().toString());
            }

            if (!cityText.getText().toString().isEmpty()) {
                customer.setCustomerCity(emailText.getText().toString());
            }

            if (!phoneText.getText().toString().isEmpty()) {
                customer.setCustomerPhoneNumber(phoneText.getText().toString());
            }

            if(daySpinner.getSelectedItem() != null) {
                customer.setCustomerDay(daySpinner.getSelectedItem().toString());
            }

            if(stateSpinner.getSelectedItem() != null) {
                customer.setCustomerState(stateSpinner.getSelectedItem().toString());
            }

            insertCustomer();
        }

    }

    private void insertCustomer() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.customerDao().insert(customer);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getApplicationContext(), "Customer account for " + customer.toString() +
                        " created.", Toast.LENGTH_LONG).show();
                finish();
            }
        }.execute();
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
