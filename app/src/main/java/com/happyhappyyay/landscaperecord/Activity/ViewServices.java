package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.Adapter.RecyclerServiceAdapter;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.POJO.Service;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.happyhappyyay.landscaperecord.Activity.HourOperations.DATE_STRING;
import static com.happyhappyyay.landscaperecord.Activity.TimeReporting.ADAPTER_POSITION;

public class ViewServices extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatabaseAccess<Customer> {

    private RecyclerServiceAdapter adapter;
    private RadioButton allCheckBox, inProgressCheckBox, customerCheckBox, endDateBox,
    startDateBox;
    private List<Customer> customers;
    private List<Service> services;
    private Spinner spinner;
    private EditText dateText;
    private Customer customer;
    private String dateString = Util.retrieveStringCurrentDate();
    private Boolean searchByDate = false;
    private int adapterPosition;
    private int sortByPosition;
    private int viewByPosition;
    private ProgressBar progressBar;
    private final String DATE_SEARCH = "Searched for date ";
    private final String SORT_SEARCH = "Sorted by ";
    private final String VIEW_SEARCH = "Viewed by ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_services);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        allCheckBox = findViewById(R.id.view_services_all_box);
        inProgressCheckBox = findViewById(R.id.view_services_in_progress_box);
        customerCheckBox = findViewById(R.id.view_services_customer_box);
        startDateBox = findViewById(R.id.view_services_start_date_box);
        endDateBox = findViewById(R.id.view_services_end_date_box);
        if (savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
            dateString = savedInstanceState.getString(DATE_STRING);
            searchByDate = savedInstanceState.getBoolean(DATE_SEARCH);
            sortByPosition = savedInstanceState.getInt(SORT_SEARCH);
            viewByPosition = savedInstanceState.getInt(VIEW_SEARCH);
            convertIntToRadioButtonClick();
        }
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
        spinner = findViewById(R.id.view_services_spinner);
        progressBar = findViewById(R.id.view_services_progress_bar);
        getCustomers();
    }

    public void radioButtonOptionClick(View view) {
        convertRadioButtonClickToInt();
        adapter.setServices(getSortedServicesList());
        adapter.notifyDataSetChanged();
    }

    private void convertRadioButtonClickToInt()  {
        int temporaryViewPosition = viewByPosition;
        int temporarySortPosition = sortByPosition;
        if(allCheckBox.isChecked()) {
            viewByPosition = 0;
        }
        else if (inProgressCheckBox.isChecked()){
            viewByPosition = 1;
        }
        else {
            viewByPosition = 2;
        }

        if (startDateBox.isChecked()) {
            sortByPosition = 0;
        }
        else if (customerCheckBox.isChecked()) {
            sortByPosition = 2;
        }
        else {
            sortByPosition = 1;
        }

        if(temporaryViewPosition != viewByPosition || temporarySortPosition != sortByPosition) {
            searchByDate = false;
        }
    }

    public void convertIntToRadioButtonClick() {
        switch(sortByPosition) {
            case 0:
                startDateBox.setChecked(true);
                break;
            case 1:
                endDateBox.setChecked(true);
                break;
            case 2:
                customerCheckBox.setChecked(true);
                break;
        }

        switch (viewByPosition) {
            case 0:
                allCheckBox.setChecked(true);
                break;
            case 1:
                inProgressCheckBox.setChecked(true);
                break;
            case 2:
                customerCheckBox.setChecked(true);
                break;
        }
    }

    public void checkSearchClick(View view) {
        if(progressBar.getVisibility() == View.VISIBLE) {
            if (!Util.checkDateFormat(dateString)) {
                if (!dateString.equals("") || !dateString.equals(" ")) {
                    dateText.setText("");
                    Toast.makeText(this,
                            "Date format incorrect. Please reenter the date.",
                            Toast.LENGTH_SHORT).show();
                }
                searchByDate = false;
            } else {
                searchByDate = true;
            }
            if (searchByDate) {
                adapter.setServices(getSortedServicesList());
                adapter.notifyDataSetChanged();
            }
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
        spinner.setSelection(adapterPosition);
        if (!customers.isEmpty()) {
            customer = customers.get(adapterPosition);
            this.adapter.setServices(getSortedServicesList());
            this.adapter.notifyDataSetChanged();
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
        outState.putBoolean(DATE_SEARCH, searchByDate);
        outState.putInt(SORT_SEARCH, sortByPosition);
        outState.putInt(VIEW_SEARCH, viewByPosition);

        super.onSaveInstanceState(outState);
    }

    private List<Service> getDateSortedServices(List<Service> services) {
        List<Service> tempServices = new ArrayList<>();

        if (sortByPosition == 1) {
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
        if (sortByPosition == 2 & customer != null) {
            services = customer.getCustomerServices();
            if (spinner.getVisibility() != View.VISIBLE) {
                spinner.setVisibility(View.VISIBLE);
            }
        }
        else {
            services = this.services;
            if (sortByPosition == 0) {
                if (spinner.getVisibility() != View.INVISIBLE) {
                    spinner.setVisibility(View.INVISIBLE);
                }
                services = sortServicesByStartTime(services);
            }
            else if (sortByPosition == 1){
                if (spinner.getVisibility() != View.INVISIBLE) {
                    spinner.setVisibility(View.INVISIBLE);
                }
                services = sortServicesByEndTime(services);
            }
        }
//      check specific sort method
        if (viewByPosition == 1) {
            for (Service s: services) {
                if (s.isPause()) {
                    tempServices.add(s);
                }
            }
            services = tempServices;
        }
        else if (viewByPosition == 2) {
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

    private List<Service> sortServicesByEndTime(List<Service> services) {
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

    private List<Service> sortServicesByStartTime(List<Service> services) {
        Collections.sort(services, new Comparator<Service>() {
            public int compare(Service service1, Service service2) {
                if (service1.getStartTime() > service2.getStartTime()) return -1;
                if (service1.getStartTime() < service2.getStartTime()) return 1;
                if (service1.getEndTime() > service2.getEndTime()) return -1;
                if (service1.getEndTime() < service2.getEndTime()) return 1;
                return 0;
            }});
        return services;
    }

    private void getCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
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
        customers = databaseObjects;
        services = new ArrayList<>();
        for (Customer c : customers) {
            services.addAll(c.getCustomerServices());
        }
        if (viewByPosition == 1) {
            services = sortServicesByEndTime(services);
        } else {
            services = sortServicesByStartTime(services);
        }
        if(adapter == null) {
            RecyclerView recyclerView = findViewById(R.id.view_services_recycler_view);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            adapter = new RecyclerServiceAdapter(services, this);
            recyclerView.setAdapter(adapter);
            populateSpinner(customers);
        } else {
            adapter.setServices(services);
            adapter.notifyDataSetChanged();
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            getCustomers();
        }

    }
}
