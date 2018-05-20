package com.example.kingdenis.landscaperecord;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class NewContact extends AppCompatActivity {
    private EditText firstNameText, lastNameText, emailText, businessText, addressText, cityText,
            phoneText, weekDay;
    private Customer customer;
    private AppDatabase db;
    private static final String TAG = "Customer List";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);
        firstNameText = findViewById(R.id.contact_first_name_text);
        lastNameText = findViewById(R.id.contact_last_name_text);
        emailText = findViewById(R.id.contact_email_text);
        businessText = findViewById(R.id.contact_business_text);
        addressText = findViewById(R.id.contact_address_text);
        cityText = findViewById(R.id.contact_city_text);
        phoneText = findViewById(R.id.contact_phone_number_text);
        weekDay = findViewById(R.id.day_of_week);
        db = AppDatabase.getAppDatabase(this);
    }

    public void addNewCustomer(View view) {
        if (!firstNameText.getText().toString().isEmpty() & !lastNameText.getText().toString().isEmpty()
                & !addressText.getText().toString().isEmpty()) {
            customer = new Customer(firstNameText.getText().toString(), lastNameText.getText().
                    toString(), addressText.getText().toString());

            if(!emailText.getText().toString().isEmpty()) {
                customer.setCustomerEmail(emailText.getText().toString());
            }

            if(!businessText.getText().toString().isEmpty()) {
                customer.setCustomerBusiness(businessText.getText().toString());
            }

            if(!cityText.getText().toString().isEmpty()) {
                customer.setCustomerCity(emailText.getText().toString());
            }

            if(!phoneText.getText().toString().isEmpty()) {
                customer.setCustomerPhoneNumber(phoneText.getText().toString());
            }

            if(!weekDay.getText().toString().isEmpty()) {
                customer.setCustomerDay(weekDay.getText().toString());
            }


            insertCustomer();
            Log.d(TAG, customer.toString());
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


}
