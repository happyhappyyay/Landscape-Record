package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;

import static com.happyhappyyay.landscaperecord.activity.HourOperations.DATE_STRING;
import static com.happyhappyyay.landscaperecord.activity.TimeReporting.ADAPTER_POSITION;

public class ReceivePayment extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatabaseAccess<Customer> {

    public static final String SERVICE_POSITION = "Service adapter Position";
    private String dateString;
    EditText dateStringText;
    private List<Customer> allCustomers;
    private List<Customer> sortedCustomers;
    private EditText checkNumber, paymentAmount;
    private int adapterPosition, dayPosition, servicePosition, groupPosition = -1;
    private Customer customer;
    private List<Service> payableServices;
    private ProgressBar progressBar;

    public static final String DAY_POSITION = "Day Adapter Position";
    public static final String GROUP_POSITION = "Radio Group Position";
    private Spinner daySpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receive_payment);
        checkNumber = findViewById(R.id.receive_payment_check_number);
        paymentAmount = findViewById(R.id.receive_payment_amount);
        paymentAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(!b) {
                    populateSpinner(customer);
                }
            }
        });
        allCustomers = new ArrayList<>();
        payableServices = new ArrayList<>();
        dateStringText = findViewById(R.id.receive_payment_date);
        dateString = Util.retrieveStringCurrentDate();
        Toolbar myToolbar = findViewById(R.id.payment_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
            dateString = savedInstanceState.getString(DATE_STRING);
            dayPosition = savedInstanceState.getInt(DAY_POSITION);
            groupPosition = savedInstanceState.getInt(GROUP_POSITION);
            servicePosition = savedInstanceState.getInt(SERVICE_POSITION);
        }

        checkNumber.setVisibility(groupPosition == 0? View.VISIBLE: View.INVISIBLE);

        dateStringText.setText(dateString);
        dateStringText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    dateString = dateStringText.getText().toString();
                }
            }
        });
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
        progressBar = findViewById(R.id.payment_reporting_progress_bar);
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
        if(progressBar.getVisibility() == View.INVISIBLE) {
            boolean checkType = groupPosition == 0;
            String payment = paymentAmount.getText().toString();
            if (!payment.isEmpty()) {
                double amount = Double.parseDouble(payment);
                if (amount > 0) {
                    if (groupPosition != -1) {
                        if (groupPosition == 0) {
                            String checkNumberText = checkNumber.getText().toString();
                            if (!checkNumberText.isEmpty()) {
                                checkForPaymentMatch(checkType);
                            }
                            else {
                                Toast.makeText(this, "The check number is blank. Please reenter " +
                                        "the check number.", Toast.LENGTH_LONG).show();
                            }



                        } else if (groupPosition == 1) {
                            checkForPaymentMatch(checkType);
                        }
                    }
                } else {
                    Toast.makeText(this, "Please enter an amount greater than 0.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(this, "Please enter in an amount for the payment.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void checkForPaymentMatch(final boolean checkType) {
        final int ZERO_POSITION = 1;
        if(payableServices.size() > 0) {
            Service tempService;
            if(adapterPosition == 0){
                tempService = payableServices.get(adapterPosition);
            }else {
                tempService = payableServices.get(adapterPosition - ZERO_POSITION);
            }
            final Service service = tempService;

            final double priceOfService = customer.getPayment().returnServicePrice(service.getServices());
            if (priceOfService == Double.parseDouble(paymentAmount.getText().toString()) & priceOfService != -1) {
                service.setPaid(true);
                service.setAmountPaid(priceOfService);
                if (checkType) {
                    customer.getPayment().payForServices(Double.parseDouble(paymentAmount.getText().toString()), dateString, checkNumber.getText().toString());
                } else {
                    customer.getPayment().payForServices(Double.parseDouble(paymentAmount.getText().toString()), dateString);
                }
                customer.updateService(service);
                Util.updateObject(this, Util.CUSTOMER_REFERENCE, customer);
            } else {
                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case DialogInterface.BUTTON_POSITIVE:
                                if (checkType) {
                                    customer.getPayment().payForServices(Double.parseDouble(paymentAmount.getText().toString()), dateString, checkNumber.getText().toString());
                                } else {
                                    customer.getPayment().payForServices(Double.parseDouble(paymentAmount.getText().toString()), dateString);
                                }
                                Toast.makeText(getApplicationContext(), "If this is the correct amount, " +
                                        "have admin apply paid status to service.", Toast.LENGTH_LONG).show();
                                service.addServiceAmountPaid(Double.parseDouble(paymentAmount.getText().toString()));
                                if(priceOfService == service.getAmountPaid()) {
                                    service.setPaid(true);
                                }
                                Util.updateObject(ReceivePayment.this, Util.CUSTOMER_REFERENCE, customer);

                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getApplicationContext(), "Please apply payment to the " +
                                        "correct service.", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("The payment amount does not match the selected service. Amount will be applied to the " +
                        "service anyway. Proceed with payment?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
        }
        else {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which) {
                        case DialogInterface.BUTTON_POSITIVE:
                            if (checkType) {
                                customer.getPayment().payForServices(Double.parseDouble(paymentAmount.getText().toString()), dateString, checkNumber.getText().toString());
                            } else {
                                customer.getPayment().payForServices(Double.parseDouble(paymentAmount.getText().toString()), dateString);
                            }
                            Toast.makeText(getApplicationContext(), "Complete service and have admin apply paid status to " +
                                    "service.", Toast.LENGTH_LONG).show();
                            Util.updateObject(ReceivePayment.this, Util.CUSTOMER_REFERENCE, customer);

                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            Toast.makeText(getApplicationContext(), "Complete service and then apply " +
                                    "payment to the correct service " +
                                    "or apply to general account.", Toast.LENGTH_LONG).show();
                            break;
                    }
                }
            };
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("No, finished services found. Credit account with payment?").setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
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
        Spinner s = findViewById(R.id.receive_payment_spinner);
        Util.populateSpinner(s,this, this, customers, false);
    }

    private void populateSpinner(Customer customer) {
        final List<Service> services = customer.getCustomerServices();
        payableServices = new ArrayList<>();
        AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        };
        final int ZERO_POSITION = 1;
        final String GENERAL_PAY = "General Payment";
        for(Service s: services) {
            if(s.isPriced() & !s.isPaid()) {
                payableServices.add(s);
            }
        }
        String[] arraySpinner = new String[payableServices.size() + ZERO_POSITION];
        for (int i = 0; i < payableServices.size()+ZERO_POSITION; i++) {
            if(i != 0) {
                arraySpinner[i] = Util.convertLongToStringDate(payableServices.get(i-ZERO_POSITION).getEndTime()) + ": " +
                        payableServices.get(i-ZERO_POSITION).getServices();
            }
            else {
                arraySpinner[i] = GENERAL_PAY;
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Spinner serviceSpinner = findViewById(R.id.receive_payment_service_spinner);
        serviceSpinner.setAdapter(adapter);
        serviceSpinner.setOnItemSelectedListener(listener);
        serviceSpinner.setSelection(-1);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long l) {
        if (sortedCustomers != null) {
            if(sortedCustomers.size() > 0) {
                customer = sortedCustomers.get(pos);
                populateSpinner(customer);
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
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DATE_STRING, dateString);
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        outState.putInt(DAY_POSITION, dayPosition);
        outState.putInt(GROUP_POSITION, groupPosition);
        outState.putInt(SERVICE_POSITION, servicePosition);

        super.onSaveInstanceState(outState);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return customer.getName() + " PAID " + paymentAmount.getText().toString() + " " + checkNumber.getText().toString();
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
        progressBar.setVisibility(View.INVISIBLE);
    }
}
