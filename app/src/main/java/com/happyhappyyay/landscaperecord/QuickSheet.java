package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static com.happyhappyyay.landscaperecord.HourOperations.DATE_STRING;

public class QuickSheet extends AppCompatActivity implements DatabaseAccess<Customer>{

    private RecyclerView recyclerView;
    private RecyclerQuickSheetAdapter adapter;
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
        endDateString = startDateString = Util.retrieveStringCurrentDate();
        recyclerView = findViewById(R.id.quick_sheet_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
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
                        if (c.getCustomerDay() != null) {
                            if (c.getCustomerDay().equals(daySpinner.getSelectedItem().toString())) {
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
                // your code here
            }

        });
        startDateText = findViewById(R.id.quick_sheet_start_date_text);
        startDateText.setText(startDateString);
        startDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String customDateString = startDateText.getText().toString();
                    if(Util.checkDateFormat(customDateString)) {
                        adapter.setStartDateString(customDateString);
                        startDateString = customDateString;
                        adapter.notifyDataSetChanged();

                    }
                    else if (customDateString.equals("") || customDateString.equals(" ")){
                        adapter.setStartDateString(startDateString);
                        startDateText.setText(startDateString);
                        startDateString = customDateString;
                        adapter.notifyDataSetChanged();
                    }
                    else {
                        startDateText.setText("");
                        Toast.makeText(QuickSheet.this,
                                "Date format incorrect. Please reenter the date.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        endDateText = findViewById(R.id.quick_sheet_end_date_text);
        endDateText.setText(endDateString);
        endDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        String customDateString = endDateText.getText().toString();
                        if(Util.checkDateFormat(customDateString)) {
                            adapter.setEndDateString(customDateString);
                            endDateString = customDateString;
                            adapter.notifyDataSetChanged();
                        }
                        else if (customDateString.equals("") || customDateString.equals(" ")){
                            adapter.setEndDateString(startDateString);
                            endDateText.setText(startDateString);
                            endDateString = customDateString;
                            adapter.notifyDataSetChanged();
                        }
                        else {
                            endDateText.setText("");
                            Toast.makeText(QuickSheet.this,
                                    "Date format incorrect. Please reenter the date.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
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
        adapter = new RecyclerQuickSheetAdapter(customers, this, startDateString, endDateString);
        recyclerView.setAdapter(adapter);
    }

    private void getCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
//        new AsyncTask<Void, Void, List<Customer>>() {
//            @Override
//            protected List<Customer> doInBackground(Void... voids) {
//                return db.customerDao().getAllCustomers();
//            }
//
//            @Override
//            protected void onPostExecute(List<Customer> customers) {
//                allCustomers = customers;
//                updateCustomers(customers);
//            }
//        }.execute();
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
