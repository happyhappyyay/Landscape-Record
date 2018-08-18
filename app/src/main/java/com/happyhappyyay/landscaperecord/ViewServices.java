package com.happyhappyyay.landscaperecord;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Comparator;
import java.util.Locale;

import static com.happyhappyyay.landscaperecord.HourOperations.DATE_STRING;
import static com.happyhappyyay.landscaperecord.TimeReporting.ADAPTER_POSITION;

public class ViewServices extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private RecyclerView recyclerView;
    private RecyclerServiceAdapter adapter;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase db;
    private RadioButton allCheckBox, inProgressCheckBox, completedCheckBox, customerCheckBox, endDateBox,
    startDateBox;
    private List<Customer> customers;
    private List<Customer> modifiedCustomers;
    private List<Service> services;
    private List<Service> modifiedServices;
    private Spinner spinner;
    private TextView dateTextLabel;
    private EditText dateText;
    private Customer customer;
    private String dateString = Util.retrieveStringCurrentDate();
    private Button searchButton;
    private Boolean searchByDate = false;
    private int adapterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_services);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        recyclerView = findViewById(R.id.view_services_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        db = AppDatabase.getAppDatabase(this);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
            dateString = savedInstanceState.getString(DATE_STRING);
        }
        allCheckBox = findViewById(R.id.view_services_all_box);
        inProgressCheckBox = findViewById(R.id.view_services_in_progress_box);
        completedCheckBox = findViewById(R.id.view_services_completed_box);
        customerCheckBox = findViewById(R.id.view_services_customer_box);
        startDateBox = findViewById(R.id.view_services_start_date_box);
        endDateBox = findViewById(R.id.view_services_end_date_box);
        dateText = findViewById(R.id.view_services_date_edit_text);
        dateText.setText(dateString);
        dateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String customDateString = dateText.getText().toString();
                    if(Util.checkDateFormat(customDateString)) {
                        dateString = customDateString;
                    }
                    else if (customDateString.equals("") || customDateString.equals(" ")){
                        dateText.setText(dateString);
                    }
                    else {
                        dateText.setText("");
                        Toast.makeText(ViewServices.this,
                                "Date format incorrect. Please reenter the date.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        dateTextLabel = findViewById(R.id.view_services_date_text_label);
        spinner = findViewById(R.id.view_services_spinner);
        searchButton = findViewById(R.id.view_services_search_button);
        getCustomers();
    }

    public void checkOptionClick(View view) {
        adapter.setServices(getSortedServicesList());
        adapter.notifyDataSetChanged();
    }

    public void checkSearchClick(View view) {
        if (!Util.checkDateFormat(dateString)) {
            if (!dateString.equals("") || !dateString.equals(" ")) {
                dateText.setText("");
                Toast.makeText(this,
                        "Date format incorrect. Please reenter the date.",
                        Toast.LENGTH_SHORT).show();
            }
            searchByDate = false;
        }
        else {
            searchByDate = true;
        }
        if (searchByDate) {
            adapter.setServices(getSortedServicesList());
            adapter.notifyDataSetChanged();
        }
    }

    private void populateSpinner(List<Customer> customers) {
        String[] arraySpinner = new String[customers.size()];
        for (int i = 0; i < customers.size(); i++) {
            arraySpinner[i] = customers.get(i).getCustomerAddress();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(Adapter.NO_SELECTION);
        if (!customers.isEmpty()) {
            customer = customers.get(0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        customer = customers.get(position);
        adapter.setServices(getSortedServicesList());
        adapter.notifyDataSetChanged();
        adapterPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (!customers.isEmpty()) {
            customer = customers.get(0);
        }


    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        outState.putString(DATE_STRING, dateString);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    private List<Service> getDateSortedServices(List<Service> services) {
        List<Service> tempServices = new ArrayList<>();

        if (endDateBox.isChecked()) {
            for (Service s : services) {
                if (s.convertEndTimeToDateString().equals(dateText.getText().toString())) {
                    tempServices.add(s);
                }
            }
        }
        else {
            for (Service s : services) {
                if (s.convertStartTimeToDateString().equals(dateText.getText().toString())) {
                    tempServices.add(s);
                }
            }
        }
        return tempServices;
    }
//  sorting mechanism to return sorted list
    private List<Service> getSortedServicesList() {
        List<Service> services;
        List<Service> tempServices = new ArrayList<>();
//      check general sort method
        if (customerCheckBox.isChecked() & customer != null) {
            services = customer.getCustomerServices();
            if (spinner.getVisibility() != View.VISIBLE) {
                spinner.setVisibility(View.VISIBLE);
            }
        }
        else {
            services = this.services;
            if (startDateBox.isChecked()) {
                if (spinner.getVisibility() != View.INVISIBLE) {
                    spinner.setVisibility(View.INVISIBLE);
                }
            }
            else if (endDateBox.isChecked()){
                if (spinner.getVisibility() != View.INVISIBLE) {
                    spinner.setVisibility(View.INVISIBLE);
                }
//TODO: Check the sorting mechanism, may not be sorting correctly- perfect opportunity to create a test
                services = sortServices(services);
            }
        }
//      check specific sort method
        if (inProgressCheckBox.isChecked()) {
            for (Service s: services) {
                if (s.isPause()) {
                    tempServices.add(s);
                }
            }
            services = tempServices;
        }
        else if (completedCheckBox.isChecked()) {
            for (Service s : services) {
                if (!s.isPause()) {
                    tempServices.add(s);
                }
            }
            services = tempServices;
        }

        if (!dateText.getText().toString().isEmpty()) {
            if (searchByDate) {
                services = getDateSortedServices(services);
            }
        }

        return services;
    }

    private List<Service> sortServices(List<Service> services) {
        Collections.sort(services, new Comparator<Service>() {
            public int compare(Service service1, Service service2) {
                if (service1.getEndTime() > service2.getEndTime()) return -1;
                if (service1.getEndTime() < service2.getEndTime()) return 1;
                if (service1.getStartTime() > service2.getStartTime()) return -1;
                if (service1.getStartTime() < service2.getStartTime()) return 1;
                return 0;
            }});
        return services;
    }

    private void getCustomers() {
        new AsyncTask<Void, Void, List<Service>>() {
            @Override
            protected List<Service> doInBackground(Void... voids) {
                List<Service> services = new ArrayList<>();
                customers = db.customerDao().getAllCustomers();
                for (Customer c: customers) {
                    for(Service s: c.getCustomerServices()) {
                        services.add(s);
                    }
                }
                return services;
            }

            @Override
            protected void onPostExecute(List<Service> passedServices) {
                passedServices = sortServices(passedServices);
                services = passedServices;
                adapter = new RecyclerServiceAdapter(services);
                recyclerView.setAdapter(adapter);
                modifiedServices = services;
                populateSpinner(customers);
            }
        }.execute();
    }
}
