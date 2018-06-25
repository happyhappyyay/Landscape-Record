package com.happyhappyyay.landscaperecord;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class QuickSheet extends AppCompatActivity {

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private AppDatabase db;
    private RecyclerQuickSheetAdapter adapter;
    private Spinner daySpinner;
    private List<Customer> allCustomers;
    private List<Customer> sortedCustomers;
    private EditText startDateText;
    private EditText endDateText;
    private String dateString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quick_sheet);
        dateString = new SimpleDateFormat("MM/dd/yyyy").format(new Date(System.currentTimeMillis()));
        recyclerView = findViewById(R.id.quick_sheet_recycler_view);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        db = AppDatabase.getAppDatabase(this);
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
        startDateText.setText(dateString);
        startDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String customDateString = startDateText.getText().toString();
                    if(Util.checkDateFormat(customDateString)) {
                        adapter.setStartDateString(customDateString);
                        adapter.notifyDataSetChanged();
                    }
                    else if (customDateString.equals("") || customDateString.equals(" ")){
                        adapter.setStartDateString(dateString);
                        startDateText.setText(dateString);
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
        endDateText.setText(dateString);
        endDateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String customDateString = endDateText.getText().toString();
                    if(Util.checkDateFormat(customDateString)) {
                        adapter.setEndDateString(customDateString);
                    }
                    else if (customDateString.equals("") || customDateString.equals(" ")){
                        adapter.setEndDateString(dateString);
                        endDateText.setText(dateString);
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

    private void checkDateFormat(String customDateString) {
        if (Util.checkDateFormat(customDateString)) {
            adapter.setStartDateString(customDateString);
            adapter.notifyDataSetChanged();
        }
        else if (customDateString.equals("") || customDateString.equals(" ")) {
            adapter.setStartDateString(dateString);
            startDateText.setText(dateString);
            adapter.notifyDataSetChanged();
        }
        else {
            startDateText.setText("");
            Toast.makeText(this,
                    "Date format incorrect. Please reenter the date.",
                    Toast.LENGTH_SHORT).show();
        }
    }

    public void updateCustomers(List<Customer> customers) {
        adapter = new RecyclerQuickSheetAdapter(customers, this);
        recyclerView.setAdapter(adapter);
    }

    private void getCustomers() {
        new AsyncTask<Void, Void, List<Customer>>() {
            @Override
            protected List<Customer> doInBackground(Void... voids) {
                return db.customerDao().getAllCustomers();
            }

            @Override
            protected void onPostExecute(List<Customer> customers) {
                allCustomers = customers;
                updateCustomers(customers);
            }
        }.execute();
    }
}
