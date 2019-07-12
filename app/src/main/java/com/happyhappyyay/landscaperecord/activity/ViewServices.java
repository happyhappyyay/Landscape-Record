package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.RecyclerViewServices;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

import static com.happyhappyyay.landscaperecord.activity.HourOperations.DATE_STRING;
import static com.happyhappyyay.landscaperecord.activity.TimeReporting.ADAPTER_POSITION;

public class ViewServices extends AppCompatActivity implements AdapterView.OnItemSelectedListener, DatabaseAccess<Customer> {

    private RecyclerViewServices adapter;
    private RadioButton allCheckBox, inProgressCheckBox, completedCheckBox, customerCheckBox, endDateBox,
    startDateBox;
    private List<Customer> customers;
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
        Toolbar myToolbar = findViewById(R.id.view_services_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        allCheckBox = findViewById(R.id.view_services_all_box);
        allCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewByPosition = 0;
                adapter.setViewBy(viewByPosition);
                searchByDate = false;
            }
        });
        inProgressCheckBox = findViewById(R.id.view_services_in_progress_box);
        inProgressCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewByPosition = 1;
                adapter.setViewBy(viewByPosition);

            }
        });

        completedCheckBox = findViewById(R.id.view_services_completed_box);
        completedCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                viewByPosition = 2;
                adapter.setViewBy(viewByPosition);
            }
        });

        startDateBox = findViewById(R.id.view_services_start_date_box);
        startDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByPosition = 0;
                if (spinner.getVisibility() != View.INVISIBLE) {
                    spinner.setVisibility(View.INVISIBLE);
                }
                adapter.setSortBy(sortByPosition);
            }
        });
        endDateBox = findViewById(R.id.view_services_end_date_box);
        endDateBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByPosition = 1;
                if (spinner.getVisibility() != View.INVISIBLE) {
                    spinner.setVisibility(View.INVISIBLE);
                }
                adapter.setSortBy(sortByPosition);
            }
        });

        customerCheckBox = findViewById(R.id.view_services_customer_box);
        customerCheckBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sortByPosition = 2;
                if (spinner.getVisibility() == View.INVISIBLE) {
                    spinner.setVisibility(View.VISIBLE);
                }
                if(customer != null) {
                    adapter.setCustomer(customer);
                }
                adapter.setSortBy(sortByPosition);
            }
        });

        if (savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
            dateString = savedInstanceState.getString(DATE_STRING);
            searchByDate = savedInstanceState.getBoolean(DATE_SEARCH);
            sortByPosition = savedInstanceState.getInt(SORT_SEARCH);
            viewByPosition = savedInstanceState.getInt(VIEW_SEARCH);
            convertIntToRadioButtonClick();
        }
        else {
            Intent intent = getIntent();
            viewByPosition = intent.getIntExtra("VIEW_POSITION",0);
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
                        Toast.makeText(ViewServices.this, R.string.incorrect_date_format, Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        Button search = findViewById(R.id.view_services_search_button);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(progressBar.getVisibility() == View.INVISIBLE) {
                    if (!Util.checkDateFormat(dateString)) {
                        dateText.setText(Util.retrieveStringCurrentDate());
                        Toast.makeText(ViewServices.this,
                                R.string.incorrect_date_format,
                                Toast.LENGTH_SHORT).show();
                        searchByDate = false;
                    } else {
                        searchByDate = true;
                        adapter.filterServicesByDate(dateString);
                        adapter.notifyDataSetChanged();
                    }
                }
            }
        });
        spinner = findViewById(R.id.view_services_spinner);
        if (sortByPosition == 2) {
            spinner.setVisibility(View.VISIBLE);
        }
        else {
            spinner.setVisibility(View.INVISIBLE);
        }
        progressBar = findViewById(R.id.view_services_progress_bar);
        getCustomers();
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
                completedCheckBox.setChecked(true);
                break;
        }
    }

    private void populateSpinner(List<Customer> customers) {
        String[] arraySpinner = new String[customers.size()];
        for (int i = 0; i < customers.size(); i++) {
            arraySpinner[i] = customers.get(i).getAddress();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        if (!customers.isEmpty()) {
            spinner.setSelection(adapterPosition);
            customer = customers.get(adapterPosition);
            this.adapter.setCustomer(customer);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            customer = customers.get(position);
            adapter.setCustomer(customer);
            adapterPosition = position;
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
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

        RecyclerView recyclerView = findViewById(R.id.view_services_recycler_view);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        adapter = new RecyclerViewServices(customers, this, viewByPosition, sortByPosition);
        if(sortByPosition == 2) {
            adapter.setCustomer(customer);
        }
        recyclerView.setAdapter(adapter);

        populateSpinner(customers);
        progressBar.setVisibility(View.INVISIBLE);
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
    protected void onResume() {
        super.onResume();
        if(adapter != null) {
            getCustomers();
        }
    }
}
