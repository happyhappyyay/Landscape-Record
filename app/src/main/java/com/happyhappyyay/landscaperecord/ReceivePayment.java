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
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.happyhappyyay.landscaperecord.HourOperations.DATE_STRING;
import static com.happyhappyyay.landscaperecord.TimeReporting.ADAPTER_POSITION;

public class ReceivePayment extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatabaseAccess<Customer> {

    private int adapterPosition, dayPosition, groupPosition = -1;
    private String dateString;
    EditText dateStringText;
    private List<Customer> allCustomers;
    private List<Customer> sortedCustomers;
    private EditText checkNumber, paymentAmount;
    private Spinner daySpinner;
    private Customer customer;

    public static final String DAY_POSITION = "Day Adapter Position";
    public static final String GROUP_POSITION = "Radio Group Position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_payment);
        checkNumber = findViewById(R.id.receive_payment_check_number);
        paymentAmount = findViewById(R.id.receive_payment_amount);
        allCustomers = new ArrayList<>();
        dateStringText = findViewById(R.id.receive_payment_date);
        dateString = Util.retrieveStringCurrentDate();
        Toolbar myToolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(myToolbar);

        if (savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
            dateString = savedInstanceState.getString(DATE_STRING);
            dayPosition = savedInstanceState.getInt(DAY_POSITION);
            groupPosition = savedInstanceState.getInt(GROUP_POSITION);
        }

        checkNumber.setVisibility(groupPosition == 0? View.VISIBLE: View.INVISIBLE);

        dateStringText.setText(dateString);
        daySpinner = findViewById(R.id.receive_payment_day_spinner);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                List<Customer> customersByDay = new ArrayList<>();
                if (position != 0) {
                    for (Customer c : allCustomers) {
                        if (c.getCustomerDay() != null) {
                            if (c.getCustomerDay().equals(daySpinner.getSelectedItem().toString())) {
                                customersByDay.add(c);
                            }
                        }
                    }
                    populateSpinner(customersByDay);
                    sortedCustomers = customersByDay;
                    dayPosition = position;
                } else {
                    populateSpinner(allCustomers);
                    sortedCustomers = allCustomers;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                if (dayPosition > 0) {
                    daySpinner.setSelection(dayPosition);
                }
            }

        });
        findAllCustomers();

    }

    public void checkButtonClicked(View view) {
        checkNumber.setVisibility(View.VISIBLE);
        groupPosition = 0;

    }

    public void cashButtonClicked(View view) {
        checkNumber.setVisibility(View.INVISIBLE);
        groupPosition = 1;
    }

    public void onSubmit(View view) {
        String payment = paymentAmount.getText().toString();
        if (!payment.isEmpty()) {
            Double amount = Double.parseDouble(payment);
            if(amount > 0) {
                if (groupPosition != -1) {
                    if(groupPosition == 0) {
                        String checkNumberText = checkNumber.getText().toString();
                        if(!checkNumberText.isEmpty()) {
                            customer.getPayment().payForServices(amount, dateString, checkNumberText);
                            Toast.makeText(this, customer.getName() +  " " + amount, Toast.LENGTH_LONG).show();
                            Util.updateObject(this, Util.CUSTOMER_REFERENCE, customer);
                        }
                        Toast.makeText(this, "The check number is blank. Please reenter " +
                                "the check number.", Toast.LENGTH_LONG).show();
                    }
                    else if(groupPosition == 1){
                        customer.getPayment().payForServices(amount,dateString);
                        Toast.makeText(this, customer.getName() +  " " + amount, Toast.LENGTH_LONG).show();
                        Util.updateObject(this, Util.CUSTOMER_REFERENCE, customer);

                    }
                }
            }
            else {
                Toast.makeText(this, "Please enter an amount greater than 0.", Toast.LENGTH_LONG).show();
            }
        }
        else {
            Toast.makeText(this, "Please enter in an amount for the payment.", Toast.LENGTH_LONG).show();
        }

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
            arraySpinner[i] = customers.get(i).getCustomerAddress();
        }

        Spinner s = findViewById(R.id.receive_payment_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);
        s.setSelection(pos);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if (sortedCustomers != null) {
            if(sortedCustomers.size() > 0) {
                Toast.makeText(adapterView.getContext(),
                        "OnItemSelectedListener : " + adapterView.getItemAtPosition(pos).toString(),
                        Toast.LENGTH_SHORT).show();
                customer = sortedCustomers.get(pos);
                adapterPosition = pos;
            }
            else {
                adapterPosition = 0;
            }
        } else {
            adapterView.setSelection(adapterPosition);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        if (sortedCustomers.size() > 0) customer = sortedCustomers.get(0);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DATE_STRING, dateString);
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        outState.putInt(DAY_POSITION, dayPosition);
        outState.putInt(GROUP_POSITION, groupPosition);

        super.onSaveInstanceState(outState);
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
        if (databaseObjects != null) {
            allCustomers = databaseObjects;
            sortedCustomers = allCustomers;
            populateSpinner(allCustomers);
        }
        else {
            finish();
        }
    }
}
