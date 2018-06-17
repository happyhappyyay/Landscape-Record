package com.example.kingdenis.landscaperecord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import android.widget.Spinner;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class JobServices extends AppCompatActivity implements FragmentListener, AdapterView.OnItemSelectedListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_services);
        viewPager = findViewById(R.id.job_services_view_pager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        tabLayout = findViewById(R.id.job_services_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        Toolbar myToolbar = findViewById(R.id.my_toolbar);
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
        findAllCustomers();
//TODO: Resize for text input

    }

    private void setupViewPager(ViewPager viewPager) {
        fragAdapter = new FragmentPageAdapter(getSupportFragmentManager());
        fragAdapter.addFragment(new LawnServices(), ServiceType.LAWN_SERVICES.toString());
        fragAdapter.addFragment(new LandscapeServices(), ServiceType.LANDSCAPING_SERVICES.toString());
        fragAdapter.addFragment(new SnowServices(), ServiceType.SNOW_SERVICES.toString());
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
            if (service == null) {
                service = new Service();
            }
            LawnServices lawnServices = (LawnServices)
                    fragAdapter.getItem(fragAdapter.getPosition(ServiceType.LAWN_SERVICES.toString()));
            LandscapeServices landscapeServices = (LandscapeServices)
                    fragAdapter.getItem(fragAdapter.getPosition(ServiceType.LANDSCAPING_SERVICES.toString()));
            SnowServices snowServices = (SnowServices)
                    fragAdapter.getItem(fragAdapter.getPosition(ServiceType.SNOW_SERVICES.toString()));
            List<Material> materials = landscapeServices.getMaterials();

            if (!date.getText().toString().isEmpty()) {
                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                String dateString = date.getText().toString();
                try {
                    Date date = dateFormat.parse(dateString);
                    time = date.getTime();
                } catch (Exception e) {
                    e.printStackTrace();
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
            customer.addService(service);
            updateCustomer();
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

    private void findAllCustomers() {
        new AsyncTask<Void, Void, List<Customer>>() {
            @Override
            protected List<Customer> doInBackground(Void... voids) {
                allCustomers = db.customerDao().getAllCustomers();
                return allCustomers;
            }

            @Override
            protected void onPostExecute(List<Customer> customers) {
                populateSpinner(customers);
            }
        }.execute();
    }

    private void updateCustomer() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.customerDao().updateCustomer(customer);
                return null;
            }

            @Override
            protected void onPostExecute(Void AVoid) {
                Toast.makeText(getApplicationContext(), customer.getName() + " " + services, Toast.LENGTH_LONG).show();
                finish();
            }
        }.execute();
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
