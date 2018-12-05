package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.List;

public class ViewCustomer extends AppCompatActivity implements DatabaseAccess<Customer> {
    private int customerID;
    private TextView customerFullName, customerDisplayName, customerFullAddress, customerBusiness,
    customerEmail, customerPhoneNumber, customerDay, customerMileage;
    private Customer customer;

    private final String CUSTOMER_ID = "Customer ID";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_customer);
        customerFullName = findViewById(R.id.view_customer_full_name);
        customerFullAddress = findViewById(R.id.view_customer_full_address);
        customerBusiness = findViewById(R.id.view_customer_business);
        customerPhoneNumber = findViewById(R.id.view_customer_phone_number);
        customerEmail = findViewById(R.id.view_customer_email);
        customerMileage = findViewById(R.id.view_customer_mileage);
        customerDay = findViewById(R.id.view_customer_day);
        customerDisplayName = findViewById(R.id.view_customer_name);
        Authentication authentication = Authentication.getAuthentication();
        Intent intent = getIntent();
        customerID = intent.getIntExtra("CUSTOMER_ID", 0);
        if (savedInstanceState != null) {
            customerID = savedInstanceState.getInt(CUSTOMER_ID);
        }

        findCustomer(customerID);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(CUSTOMER_ID, customerID);
        super.onSaveInstanceState(outState);
    }

    public void onEditCustomer(View view) {
        Intent intent = new Intent(this, EditCustomer.class);
        intent.putExtra("CUSTOMER_ID", customerID);
        startActivity(intent);
    }

    public void onDeleteCustomer(View view) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteCustomer(customer);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete customer " + customer.getName() + " ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }

    private void findCustomer(Integer customerID) {
        Util.findObjectByID(this, Util.CUSTOMER_REFERENCE,customerID);
//        new AsyncTask<Integer, Void, Customer>() {
//            @Override
//            protected Customer doInBackground(Integer... integers) {
//                return db.customerDao().findCustomerByID(integers[0]);
//            }
//
//            @Override
//            protected void onPostExecute(Customer customer) {
//                String fullName = customer.getCustomerFirstName() + " " + customer.getCustomerLastName();
//                customerFullName.setText(fullName);
//                customerDisplayName.setText(customer.getName());
//                customerBusiness.setText(customer.getCustomerBusiness());
//                customerDay.setText(customer.getCustomerDay());
//                customerPhoneNumber.setText(customer.getCustomerPhoneNumber());
//                customerEmail.setText(customer.getCustomerEmail());
//                customerMileage.setText("0");
//                customerFullAddress.setText(customer.concatenateFullAddress());
//                ViewCustomer.this.customer = customer;
//            }
//        }.execute(customerID);
    }

    private void populateTextFields(Customer customer) {
        String fullName = customer.getCustomerFirstName() + " " + customer.getCustomerLastName();
        customerFullName.setText(fullName);
        customerDisplayName.setText(customer.getName());
        customerBusiness.setText(customer.getCustomerBusiness());
        customerDay.setText(customer.getCustomerDay());
        customerPhoneNumber.setText(customer.getCustomerPhoneNumber());
        customerEmail.setText(customer.getCustomerEmail());
        customerMileage.setText("0");
        customerFullAddress.setText(customer.concatenateFullAddress());
    }

    private void deleteCustomer(Customer customer) {
        Util.deleteObject(this, Util.CUSTOMER_REFERENCE, customer);
//        new AsyncTask<Customer, Void, Void>() {
//            @Override
//            protected Void doInBackground(Customer... params) {
//                Customer customer = params[0];
//                LogActivity log = new LogActivity(authentication.getUser().getName(), customer.getName(),1, 1);
//                db.logDao().insert(log);
//                db.customerDao().deleteCustomer(customer);
//                finish();
//                return null;
//            }
//        }.execute(customer);
    }

    @Override
    protected void onResume(){
        super.onResume();
        findCustomer(customerID);
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
    public void onPostExecute (List<Customer> databaseObjects) {
        if(databaseObjects != null) {
            customer = databaseObjects.get(0);
            populateTextFields(customer);
        }else {
            finish();
        }
    }
}
