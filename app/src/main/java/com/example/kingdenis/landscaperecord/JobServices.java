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
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

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
    private List<Customer> customers;
    private Customer customer;
    private EditText date, manHours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_job_services);
        viewPager = (ViewPager) findViewById(R.id.job_services_view_pager);
        viewPager.setOffscreenPageLimit(2);
        setupViewPager(viewPager);
        tabLayout = (TabLayout) findViewById(R.id.job_services_tab_layout);
        tabLayout.setupWithViewPager(viewPager);
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        accountSpinner = (Spinner) findViewById(R.id.job_services_account_spinner);
        daySpinner = (Spinner) findViewById(R.id.job_services_day_spinner);
        date = (EditText) findViewById(R.id.job_services_date_text);
        manHours = (EditText) findViewById(R.id.job_services_man_hours_text);
        db = AppDatabase.getAppDatabase(this);
        services = "";
        daySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                List<Customer> customersByDay = new ArrayList<>();
                if (position != 0) {
                    for (Customer c : customers) {
                        if (c.getCustomerDay() != null) {
                            if (c.getCustomerDay().equals(daySpinner.getSelectedItem().toString())) {
                                customersByDay.add(c);
                            }
                        }
                    }
                    populateSpinner(customersByDay);
                } else {
                    populateSpinner(customers);
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
        boolean allowServiceCreate = false;
        Service service = new Service();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
        Date convertedDate = new Date();
        LawnServices lawnServices = (LawnServices)
                fragAdapter.getItem(fragAdapter.getPosition(ServiceType.LAWN_SERVICES.toString()));
        LandscapeServices landscapeServices = (LandscapeServices)
                fragAdapter.getItem(fragAdapter.getPosition(ServiceType.LANDSCAPING_SERVICES.toString()));
        SnowServices snowServices = (SnowServices)
                fragAdapter.getItem(fragAdapter.getPosition(ServiceType.SNOW_SERVICES.toString()));
        List<Material> materials = landscapeServices.getMaterials();

        if (!date.getText().toString().isEmpty()) {
            String dateString = date.getText().toString();
            try {
                convertedDate = dateFormat.parse(dateString);
                if (service.getStartDateTime() == null) {
                    service.setStartDateTime(convertedDate);
                } else {
                    service.setEndDateTime(convertedDate);
                }
            } catch (ParseException e) {

                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            Date currentDate = Calendar.getInstance().getTime();
            if (service.getStartDateTime() == null) {
                service.setStartDateTime(currentDate);
            } else {
                service.setEndDateTime(currentDate);
            }


        }


        if (lawnServices.getView() != null) {
            services += lawnServices.markedCheckBoxes();
        }


        if (landscapeServices.getView() != null) {
            services += landscapeServices.markedCheckBoxes();
            if (materials != null) {
                for (Material m : landscapeServices.getMaterials()) {
                    service.addMaterial(m);
                }
            }
        }


        if (snowServices.getView() != null) {
            services += snowServices.markedCheckBoxes();
        }


        finish();
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
        } else {
            customer = new Customer("bob", "barker", "extrodinare");
            customers.add(customer);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        customer = customers.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void findAllCustomers() {
        new AsyncTask<Void, Void, List<Customer>>() {
            @Override
            protected List<Customer> doInBackground(Void... voids) {
                customers = db.customerDao().getAllCustomers();
                return customers;
            }

            @Override
            protected void onPostExecute(List<Customer> customers) {
                populateSpinner(customers);
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
