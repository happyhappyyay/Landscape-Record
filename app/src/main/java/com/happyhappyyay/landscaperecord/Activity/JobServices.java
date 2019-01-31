package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseOperator;
import com.happyhappyyay.landscaperecord.DatabaseInterface.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.Enum.LogActivityAction;
import com.happyhappyyay.landscaperecord.Enum.LogActivityType;
import com.happyhappyyay.landscaperecord.Fragment.LandscapeServices;
import com.happyhappyyay.landscaperecord.Fragment.LandscapingMaterials;
import com.happyhappyyay.landscaperecord.Fragment.LandscapingOther;
import com.happyhappyyay.landscaperecord.Fragment.LawnServices;
import com.happyhappyyay.landscaperecord.Fragment.SnowServices;
import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.POJO.LogActivity;
import com.happyhappyyay.landscaperecord.POJO.Service;
import com.happyhappyyay.landscaperecord.POJO.WorkDay;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.AppDatabase;
import com.happyhappyyay.landscaperecord.Utility.Authentication;
import com.happyhappyyay.landscaperecord.Utility.ExistingService;
import com.happyhappyyay.landscaperecord.Utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.ArrayList;
import java.util.List;

import static com.happyhappyyay.landscaperecord.Activity.HourOperations.DATE_STRING;
import static com.happyhappyyay.landscaperecord.Activity.TimeReporting.ADAPTER_POSITION;

public class JobServices extends AppCompatActivity implements AdapterView.OnItemSelectedListener, MultiDatabaseAccess<Customer> {

    private ViewPager viewPager;
    private FragmentPageAdapter fragAdapter;
    private TabLayout tabLayout;
    private Spinner accountSpinner;
    private Spinner daySpinner;
    private String services;
    private List<Customer> allCustomers;
    private List<Customer> sortedCustomers;
    private Customer customer;
    private EditText date, manHours;
    public static final String MAN_HOURS = "Man Hours";
    private Service service;
    private WorkDay workDay;
    private LawnServices lawnServices;
    private LandscapeServices landscapeServices;
    private SnowServices snowServices;
    private int adapterPosition = Adapter.NO_SELECTION;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String dateString = Util.retrieveStringCurrentDate();
        String manHoursString = "";
        Bundle b = getIntent().getBundleExtra("bundle");
        if(b != null) {
            service = b.getParcelable("SERVICE");
        }
        if(savedInstanceState != null) {
            lawnServices = (LawnServices) getSupportFragmentManager().getFragment(savedInstanceState, "LAWN");
            landscapeServices = (LandscapeServices) getSupportFragmentManager().getFragment(savedInstanceState, "LANDSCAPE");
            landscapeServices.setLandscapingMaterials((LandscapingMaterials) (getSupportFragmentManager().getFragment(savedInstanceState, "MATERIALS")));
            landscapeServices.setLandscapingOther((LandscapingOther) (getSupportFragmentManager().getFragment(savedInstanceState, "OTHER")));
            snowServices = (SnowServices) getSupportFragmentManager().getFragment(savedInstanceState, "SNOW");
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
            dateString = savedInstanceState.getString(DATE_STRING);
            manHoursString = savedInstanceState.getString(MAN_HOURS);

        }
        else {
            lawnServices = new LawnServices();
            landscapeServices = new LandscapeServices();
            snowServices = new SnowServices();
        }

        allCustomers = new ArrayList<>();
        setContentView(R.layout.activity_job_services);
        viewPager = findViewById(R.id.job_services_view_pager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.job_services_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        Toolbar myToolbar = findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(myToolbar);
        accountSpinner = findViewById(R.id.job_services_account_spinner);
        daySpinner = findViewById(R.id.job_services_day_spinner);
        date = findViewById(R.id.job_services_date_text);
        date.setText(dateString);
        manHours = findViewById(R.id.job_services_man_hours_text);
        manHours.setText(manHoursString);
        progressBar = findViewById(R.id.job_services_progress_bar);
        services = "";
        if(service != null) {
            ExistingService.getExistingService().setServices(service.getServices());
            ExistingService.getExistingService().setMaterials(service.getMaterials());
        }
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if(service == null) {
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
                    } else {
                        populateSpinner(allCustomers);
                        sortedCustomers = allCustomers;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    private void setupViewPager(ViewPager viewPager) {
        fragAdapter = new FragmentPageAdapter(getSupportFragmentManager());
        if(lawnServices == null) {
            fragAdapter.addFragment(new LawnServices(), "LAWN");
        }
        else {
            fragAdapter.addFragment(lawnServices, "LAWN");
        }

        if(landscapeServices == null) {
            fragAdapter.addFragment(new LandscapeServices(), "LANDSCAPE");
        }
        else {
            fragAdapter.addFragment(landscapeServices, "LANDSCAPE");
        }

        if(snowServices == null) {
            fragAdapter.addFragment(new SnowServices(), "SNOW");
        }
        else {
            fragAdapter.addFragment(snowServices, "SNOW");
        }

        viewPager.setAdapter(fragAdapter);
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

    public void onSubmitButton(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            if (service == null) {
                service = new Service();
            }
            if (customer != null) {
                if (Util.checkDateFormat(date.getText().toString())) {
                    long time = System.currentTimeMillis();

                    if (!date.getText().toString().isEmpty()) {
                        if (Util.checkDateFormat(date.getText().toString())) {
                            time = Util.convertStringDateToMilliseconds(date.getText().toString());
                        }
                    }

                    if (lawnServices.getView() != null) {
                        services += lawnServices.markedCheckBoxes();
                    }

                    if (landscapeServices.getView() != null) {
                        services += landscapeServices.markedCheckBoxes();
                        service.setMaterials(landscapeServices.getMaterials());
                    }
                    if (snowServices.getView() != null) {
                        services += snowServices.markedCheckBoxes();
                    }
                    service.setServices(services);
                    service.setCustomerName(customer.getName());
                    if(!manHours.getText().toString().isEmpty()) {
                        try {
                            double numberOfManHours = Double.parseDouble(manHours.getText().toString());
                            service.setManHours(numberOfManHours);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (service.getStartTime() == 0) {
                        service.setStartTime(time);
                        customer.addService(service);
                    } else {
                        service.setEndTime(time);
                        service.setPause(false);
                        customer.updateService(service);
                    }
                    progressBar.setVisibility(View.VISIBLE);
                    Util.enactMultipleDatabaseOperations(this);

                } else {
                    Toast.makeText(getApplicationContext(), "Incorrect date format please use mm/dd/yyyy.", Toast.LENGTH_LONG).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "No customer selected. Please select or create a customer.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void populateSpinner(List<Customer> customers) {
        String[] arraySpinner = new String[customers.size()];
        for (int i = 0; i < customers.size(); i++) {
            arraySpinner[i] = customers.get(i).getCustomerAddress();
        }


        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        accountSpinner.setAdapter(adapter);
        accountSpinner.setOnItemSelectedListener(this);
        accountSpinner.setSelection(adapterPosition);
        if (!customers.isEmpty()) {
            customer = customers.get(0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (service == null) {
            customer = allCustomers.get(position);
            adapterPosition = position;
        }
        else {
            for (int i = 0; i < allCustomers.size(); i++) {
                if(allCustomers.get(i).getName().equals(service.getCustomerName())) {
                    accountSpinner.setSelection(adapterPosition = i);
                    customer = allCustomers.get(i);
                    break;
                }
                if(i == allCustomers.size()) {
                    accountSpinner.setSelection(Adapter.IGNORE_ITEM_VIEW_TYPE);
                }
            }
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
            if (service == null) {
                if (sortedCustomers.size() > 0) customer = sortedCustomers.get(0);
            }
    }

    private void updateWorkDay(DatabaseOperator db) {
        WorkDay tempWorkDay = Util.WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseString(db, Util.convertLongToStringDate(service.getStartTime()));
        if (tempWorkDay != null) {
            workDay = tempWorkDay;
            workDay.addServices(service);
            Util.WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(db, workDay);

        }
        else {
            workDay = new WorkDay(Util.convertLongToStringDate(service.getStartTime()));
            workDay.addServices(service);
            Util.WORK_DAY_REFERENCE.insertClassInstanceFromDatabase(db, workDay);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_service).setEnabled(false);
        return true;
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
//                if(service == null) {
                    populateSpinner(allCustomers);
//                }
//                else {
//                    for(int i = 0; i < allCustomers.size();i++) {
//                        if (allCustomers.get(i).getName().equals(service.getCustomerName())) {
//                            List<Customer> selectedCustomer = new ArrayList<>();
//                            selectedCustomer.add(allCustomers.get(i));
//                            populateSpinner(selectedCustomer);
//                            customer = selectedCustomer.get(0);
//                        }
//                    }
//                }
        }
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        if(Util.hasOnlineDatabaseEnabled(this)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
                Util.CUSTOMER_REFERENCE.updateClassInstanceFromDatabase(db, customer);
                updateWorkDay(db);
            } catch (Exception e) {
                AppDatabase db = AppDatabase.getAppDatabase(this);
                Util.CUSTOMER_REFERENCE.updateClassInstanceFromDatabase(db, customer);
                updateWorkDay(db);
            }
        }
        else {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            Util.CUSTOMER_REFERENCE.updateClassInstanceFromDatabase(db, customer);
            updateWorkDay(db);
        }
    }

    @Override
    public void createCustomLog() {
        Authentication authentication = Authentication.getAuthentication();
        LogActivity log = new LogActivity(authentication.getUser().getName(), customer.getName(), LogActivityAction.valueOf("UPDATE").ordinal(), LogActivityType.valueOf("CUSTOMER").ordinal());
        log.setObjId(customer.getId());
        try {
            OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
            finish();
        } catch (Exception e) {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
            finish();
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        getSupportFragmentManager().putFragment(outState, "LAWN", lawnServices);
        getSupportFragmentManager().putFragment(outState, "LANDSCAPE", landscapeServices);
        getSupportFragmentManager().putFragment(outState, "MATERIALS", landscapeServices.getLandscapingMaterials());
        getSupportFragmentManager().putFragment(outState, "OTHER", landscapeServices.getLandscapingOther());
        getSupportFragmentManager().putFragment(outState, "SNOW", snowServices);
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        outState.putString(DATE_STRING, manHours.getText().toString());
        outState.putString(MAN_HOURS, manHours.getText().toString());
        super.onSaveInstanceState(outState);
    }

    private class FragmentPageAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        public FragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return fragmentTitle.get(position);
        }

        public int getPosition(String title) {
            String lowercaseTitle = title.toLowerCase();
            int titlePosition = 0;
            for (int i = 0; i < fragmentTitle.size(); i++) {
                if (fragmentTitle.get(i).toLowerCase().equals(lowercaseTitle)) {
                    titlePosition = i;
                }
            }
            return titlePosition;
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            fragmentTitle.add(title);
        }
    }
    @Override
    public void onBackPressed() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        JobServices.super.onBackPressed();
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        break;
                }
            }
        };

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to leave this page without submitting job information?"
        ).setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();    }
}
