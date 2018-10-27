package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JobServices extends AppCompatActivity implements FragmentListener, AdapterView.OnItemSelectedListener, MultiDatabaseAccess<Customer> {

    private static final String TAG = "job button";
    private ViewPager viewPager;
    private FragmentPageAdapter fragAdapter;
    private TabLayout tabLayout;
    private Spinner accountSpinner;
    private Spinner daySpinner;
    private AppDatabase db;
    private String services;
    private String pausedFragmentTitle;
    private List<Customer> allCustomers;
    private List<Customer> sortedCustomers;
    private Customer customer;
    private EditText date, manHours;
    private Service service;
    private WorkDay workDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        service = new Service();
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
        manHours = findViewById(R.id.job_services_man_hours_text);
        db = AppDatabase.getAppDatabase(this);
        services = "";
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
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
                } else {
                    populateSpinner(allCustomers);
                    sortedCustomers = allCustomers;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
                // your code here
            }

        });
        Util.findAllObjects(this,Util.CUSTOMER_REFERENCE);
    }

    private void setupViewPager(ViewPager viewPager) {
        fragAdapter = new FragmentPageAdapter(getSupportFragmentManager());
        fragAdapter.addFragment(new LawnServices(), "LAWN SERVICES");
        fragAdapter.addFragment(new LandscapeServices(), "LANDSCAPING SERVICES");
        fragAdapter.addFragment(new SnowServices(), "SNOW SERVICES");
        viewPager.setAdapter(fragAdapter);
//        lawnFrag.setFragmentListener(this);
//        landFrag.setFragmentListener(this);
//        snowFrag.setFragmentListener(this);
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
    public void checkBoxData(String string) {
        services = string;
    }

    @Override
    public void pausedFragment(ServiceType serviceType) {
        pausedFragmentTitle = serviceType.toString();
    }

    public void onSubmitButton(View view) {
        if (customer != null) {
            long time = System.currentTimeMillis();
            LawnServices lawnServices = (LawnServices)
                    fragAdapter.getItem(fragAdapter.getPosition("LAWN SERVICES"));
            LandscapeServices landscapeServices = (LandscapeServices)
                    fragAdapter.getItem(fragAdapter.getPosition("LANDSCAPING SERVICES"));
            SnowServices snowServices = (SnowServices)
                    fragAdapter.getItem(fragAdapter.getPosition("SNOW SERVICES"));
            List<Material> materials = landscapeServices.getMaterials();

            if (!date.getText().toString().isEmpty()) {
                if(Util.checkDateFormat(date.getText().toString())) {
                    time = Util.convertStringDateToMilliseconds(date.getText().toString());
                }
            }

            if (service.getStartTime() == 0) {
                service.setStartTime(time);
            } else {
                service.setEndTime(time);
                service.setPause(false);
            }

            if (lawnServices.getView() != null) {
                services += lawnServices.markedCheckBoxes();
            }


            if (landscapeServices.getView() != null) {
                services += landscapeServices.markedCheckBoxes();
                if (materials != null) {
                    for (int i = 0; i < materials.size(); i++) {
                        service.addMaterial(materials.get(i));
                    }
                }
            }

            if (snowServices.getView() != null) {
                services += snowServices.markedCheckBoxes();
            }
            service.setServices(services);
            service.setCustomerName(customer.getName());
            customer.addService(service);
            Util.enactMultipleDatabaseOperations(this);
        }
        else {
            Toast.makeText(getApplicationContext(), "No customer selected. Please select or create a customer.", Toast.LENGTH_LONG).show();
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
        accountSpinner.setAdapter(adapter);
        accountSpinner.setOnItemSelectedListener(this);
        accountSpinner.setSelection(Adapter.NO_SELECTION);
        if (!customers.isEmpty()) {
            customer = customers.get(0);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        customer = allCustomers.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        if (!allCustomers.isEmpty()) customer = sortedCustomers.get(0);
    }

//    private void findAllCustomers() {
//        new AsyncTask<Void, Void, List<Customer>>() {
//            @Override
//            protected List<Customer> doInBackground(Void... voids) {
//                allCustomers = db.customerDao().getAllCustomers();
//                return allCustomers;
//            }
//
//            @Override
//            protected void onPostExecute(List<Customer> customers) {
//                populateSpinner(customers);
//            }
//        }.execute();
//    }
//
//    private void updateCustomer() {
//        new AsyncTask<Void, Void, Void>() {
//            @Override
//            protected Void doInBackground(Void... voids) {
//                db.customerDao().updateCustomer(customer);
//                WorkDay tempWorkDay = db.workDayDao().findWorkDayByDate(Util.convertLongToStringDate(service.getStartTime()));
//                if (tempWorkDay != null) {
//                    workDay = tempWorkDay;
//                }
//                else {
//                    workDay = new WorkDay(Util.convertLongToStringDate(service.getStartTime()));
//                    db.workDayDao().insert(workDay);
//                }
//                workDay.addServices(service);
//                db.workDayDao().updateWorkDay(workDay);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void AVoid) {
//                Toast.makeText(getApplicationContext(), customer.getName() + " " + service.getServices(), Toast.LENGTH_LONG).show();
//                finish();
//            }
//        }.execute();
//    }

    private void updateWorkDay() {
        AppDatabase db = AppDatabase.getAppDatabase(this);
        WorkDay tempWorkDay = Util.WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseString(db, Util.convertLongToStringDate(service.getStartTime()));
        if (tempWorkDay != null) {
            workDay = tempWorkDay;
            workDay.addServices(service);
            Util.WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(workDay, db);

        }
        else {
            workDay = new WorkDay(Util.convertLongToStringDate(service.getStartTime()));
            workDay.addServices(service);
            Util.WORK_DAY_REFERENCE.insertClassInstanceFromDatabase(workDay, db);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_add_contact).setEnabled(false);
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
            populateSpinner(allCustomers);
        }
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        AppDatabase db = AppDatabase.getAppDatabase(this);
        Util.CUSTOMER_REFERENCE.updateClassInstanceFromDatabase(customer, db);
        updateWorkDay();
    }

    @Override
    public void createCustomLog() {
        AppDatabase db = AppDatabase.getAppDatabase(this);
        Authentication authentication = Authentication.getAuthentication(this);
        LogActivity log = new LogActivity(authentication.getUser().getName(), customer.getName(), LogActivityAction.valueOf("UPDATE").ordinal(), LogActivityType.valueOf("CUSTOMER").ordinal());
        Util.LOG_REFERENCE.insertClassInstanceFromDatabase(log, db);
        finish();
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
}
