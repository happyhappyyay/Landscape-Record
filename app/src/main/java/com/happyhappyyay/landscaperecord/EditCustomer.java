package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.List;

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
        Toolbar myToolbar = findViewById(R.id.new_contact_toolbar);
        setSupportActionBar(myToolbar);
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
        progressBar = findViewById(R.id.add_user_progress_bar);
        Intent intent = getIntent();
        customerID = intent.getStringExtra("CUSTOMER_ID");
        findCustomer(customerID);
    }

    public void onSubmitCustomerUpdate(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            if (!customerFirstName.getText().toString().isEmpty() & !customerLastName.getText().toString().isEmpty()
                    & !customerAddress.getText().toString().isEmpty()) {
                customer.setCustomerFirstName(customerFirstName.getText().toString());
                customer.setCustomerLastName(customerLastName.getText().toString());

                if (!customerEmail.getText().toString().isEmpty()) {
                    customer.setCustomerEmail(customerEmail.getText().toString());
                }

                if (!customerBusiness.getText().toString().isEmpty()) {
                    customer.setCustomerBusiness(customerBusiness.getText().toString());
                }

                if (!customerCity.getText().toString().isEmpty()) {
                    customer.setCustomerCity(customerCity.getText().toString());
                }

                if (!customerPhoneNumber.getText().toString().isEmpty()) {
                    customer.setCustomerPhoneNumber(customerPhoneNumber.getText().toString());
                }

                if (daySpinner.getSelectedItem() != null) {
                    customer.setCustomerDay(daySpinner.getSelectedItem().toString());
                }

                if (stateSpinner.getSelectedItem() != null) {
                    customer.setCustomerState(stateSpinner.getSelectedItem().toString());
                }
            }
            updateCustomer();
        }
    }

    private void findCustomer(String customerID) {
        progressBar.setVisibility(View.VISIBLE);
        Util.findObjectByID(this, Util.CUSTOMER_REFERENCE, customerID);
//        new AsyncTask<Integer, Void, Customer>() {
//            @Override
//            protected Customer doInBackground(Integer... integers) {
//                return db.customerDao().findCustomerByID(integers[0]);
//            }
//
//            @Override
//            protected void onPostExecute(Customer customer) {
//                customerFirstName.setText(customer.getCustomerFirstName());
//                customerLastName.setText(customer.getCustomerLastName());
//                customerBusiness.setText(customer.getCustomerBusiness());
//                customerCity.setText(customer.getCustomerCity());
//                customerPhoneNumber.setText(customer.getCustomerPhoneNumber());
//                customerEmail.setText(customer.getCustomerEmail());
//                customerMileage.setText("0");
//                customerAddress.setText(customer.getCustomerAddress());
//                String compareValue = customer.getCustomerDay();
//                ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditCustomer.this, R.array.days_of_the_week, android.R.layout.simple_spinner_item);
//                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                daySpinner.setAdapter(adapter);
//                if (compareValue != null) {
//                    int spinnerPosition = adapter.getPosition(compareValue);
//                    daySpinner.setSelection(spinnerPosition);
//                }
//                String compareStateValue = customer.getCustomerState();
//                ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(EditCustomer.this, R.array.US_states_list, android.R.layout.simple_spinner_item);
//                stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//                stateSpinner.setAdapter(stateAdapter);
//                if (compareStateValue != null) {
//                    int spinnerPosition = stateAdapter.getPosition(compareStateValue);
//                    stateSpinner.setSelection(spinnerPosition);
//                }
//                EditCustomer.this.customer = customer;
//            }
//        }.execute(customerID);
    }

    private void loadCustomerInformation() {
        customerFirstName.setText(customer.getCustomerFirstName());
        customerLastName.setText(customer.getCustomerLastName());
        customerBusiness.setText(customer.getCustomerBusiness());
        customerCity.setText(customer.getCustomerCity());
        customerPhoneNumber.setText(customer.getCustomerPhoneNumber());
        customerEmail.setText(customer.getCustomerEmail());
        customerMileage.setText("0");
        customerAddress.setText(customer.getCustomerAddress());
        String compareValue = customer.getCustomerDay();
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(EditCustomer.this, R.array.days_of_the_week, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        daySpinner.setAdapter(adapter);
        if (compareValue != null) {
            int spinnerPosition = adapter.getPosition(compareValue);
            daySpinner.setSelection(spinnerPosition);
        }
        String compareStateValue = customer.getCustomerState();
        ArrayAdapter<CharSequence> stateAdapter = ArrayAdapter.createFromResource(EditCustomer.this, R.array.US_states_list, android.R.layout.simple_spinner_item);
        stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        stateSpinner.setAdapter(stateAdapter);
        if (compareStateValue != null) {
            int spinnerPosition = stateAdapter.getPosition(compareStateValue);
            stateSpinner.setSelection(spinnerPosition);
        }
    }

    public void docCreate(View view) {
        CreateDocument createDocument = new CreateDocument(this, customer, customer.getCustomerServices());
    }

    private void updateCustomer() {
        logInfo = customer.getName();
        Util.updateObject(this, Util.CUSTOMER_REFERENCE, customer);
        Toast.makeText(getApplicationContext(), "Customer account for " + customer.getName() +
                " updated.", Toast.LENGTH_LONG).show();
        finish();
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                db.customerDao().updateCustomer(customer);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                Toast.makeText(getApplicationContext(), "Customer account for " + customer.toString() +
//                        " updated.", Toast.LENGTH_LONG).show();
//                finish();
//            }
//        }.execute();
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

    }
}
