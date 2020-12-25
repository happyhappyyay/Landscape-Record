package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.RecyclerQuickSheet;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;

import static com.happyhappyyay.landscaperecord.activity.HourOperations.DATE_STRING;

public class QuickSheet extends AppCompatActivity implements DatabaseAccess<Customer> {

    private RecyclerView recyclerView;
    private RecyclerQuickSheet adapter;
    private Spinner daySpinner;
    private List<Customer> allCustomers;
    private EditText startDateText;
    private EditText endDateText;
    private String startDateString;
    private String endDateString;
    private ProgressBar progressBar;
    private final String DATE_STRING_END = "End date string";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        allCustomers = new ArrayList<>();
        setContentView(R.layout.activity_quick_sheet);
        Toolbar myToolbar = findViewById(R.id.quick_sheet_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        endDateString = startDateString = Util.retrieveStringCurrentDate();
        recyclerView = findViewById(R.id.quick_sheet_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState != null) {
            endDateString = savedInstanceState.getString(DATE_STRING_END);
            startDateString = savedInstanceState.getString(DATE_STRING);
        }
        progressBar = findViewById(R.id.quick_sheet_progress_bar);
        daySpinner = findViewById(R.id.quick_sheet_day_spinner);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                List<Customer> customersByDay = new ArrayList<>();
                if (position != 0) {
                    for (Customer c : allCustomers) {
                        if (c.getDay() != null) {
                            if (c.getDay().equals(daySpinner.getSelectedItem().toString())) {
                                customersByDay.add(c);
                            }
                        }
                    }
                    updateCustomers(customersByDay);
                } else {
                    updateCustomers(allCustomers);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }
        });
        startDateText = findViewById(R.id.quick_sheet_start_date_text);
        startDateText.setText(startDateString);
        startDateText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                String customDateString = startDateText.getText().toString();
                if(adapter != null) {
                    adapter.setStartDateString(customDateString);
                    startDateString = customDateString;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        endDateText = findViewById(R.id.quick_sheet_end_date_text);
        endDateText.setText(endDateString);
        endDateText.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable s) {
                if(adapter != null) {
                    String customDateString = endDateText.getText().toString();
                    adapter.setEndDateString(customDateString);
                    endDateString = customDateString;
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
            }
        });

        getCustomers();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(DATE_STRING_END, endDateString);
        outState.putString(DATE_STRING, startDateString);

        super.onSaveInstanceState(outState);
    }

    public void updateCustomers(List<Customer> customers) {
        adapter = new RecyclerQuickSheet(customers, this, startDateString, endDateString);
        recyclerView.setAdapter(adapter);
    }

    private void getCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_service).setEnabled(false);
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
        return null;
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        allCustomers = databaseObjects;
        updateCustomers(databaseObjects);
        progressBar.setVisibility(View.INVISIBLE);
    }
}
