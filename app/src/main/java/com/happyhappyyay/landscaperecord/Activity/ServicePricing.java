package com.happyhappyyay.landscaperecord.Activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;
import android.widget.Spinner;

import com.happyhappyyay.landscaperecord.Adapter.BillingViewPagerAdapter;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.ArrayList;
import java.util.List;

import static com.happyhappyyay.landscaperecord.Activity.TimeReporting.ADAPTER_POSITION;

public class ServicePricing extends AppCompatActivity implements DatabaseAccess<Customer>, AdapterView.OnItemSelectedListener {
    protected static String MONTH_POSITION = "Month position";
    List<Customer> customers;
    private int monthSelectionPosition;
    private int customerSelectionPosition;
    private ProgressBar progressBar;
    private ViewPager viewPager;
    private BillingViewPagerAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_pricing);
        viewPager = findViewById(R.id.service_pricing_view_pager);
        Spinner monthSpinner = findViewById(R.id.service_pricing_months_spinner);
        monthSelectionPosition = Util.retrieveMonthFromLong(Util.retrieveLongCurrentDate());
        if(savedInstanceState != null) {
            monthSelectionPosition = savedInstanceState.getInt(MONTH_POSITION);
            customerSelectionPosition = savedInstanceState.getInt(ADAPTER_POSITION);
        }
        monthSpinner.setSelection(monthSelectionPosition);
        progressBar = findViewById(R.id.services_pricing_progress_bar);
        monthSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(adapter != null) {
                    monthSelectionPosition = i;
                    adapter.updateMonthSelection(monthSelectionPosition);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        findAllCustomers();
    }

    private void findAllCustomers() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    private void populateSpinner(List<Customer> customers) {
        final int ZERO_POSITION = 1;
        final String ALL_CUSTOMERS = "All Customers";
        String[] arraySpinner = new String[customers.size() + ZERO_POSITION];
        int pos = customerSelectionPosition;
        for (int i = 0; i < customers.size() + ZERO_POSITION; i++) {
            if(i != 0) {
                arraySpinner[i] = customers.get(i-ZERO_POSITION).getCustomerAddress();
            }
            else {
                arraySpinner[i] = ALL_CUSTOMERS;

            }
        }

        Spinner s = findViewById(R.id.service_pricing_customer_spinner);
        ArrayAdapter<String> adapterCustomer = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, arraySpinner);
        adapterCustomer.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapterCustomer);
        s.setOnItemSelectedListener(this);
        s.setSelection(pos);
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        final int ZERO_POSITION = 1;
        if(i != 0) {
            Customer customer = customers.get(i-ZERO_POSITION);
            List<Customer> customerSelection = new ArrayList<>();
            customerSelection.add(customer);
            customerSelectionPosition = i;
            adapter.updateCustomersSelection(customerSelection);
        }
        else {
            adapter.updateCustomersSelection(customers);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

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
        populateSpinner(customers);
        adapter = new BillingViewPagerAdapter(this, createUnpricedCustomersForMonthList(customers), monthSelectionPosition);
        viewPager.setAdapter(adapter);
        progressBar.setVisibility(View.INVISIBLE);
    }
    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(MONTH_POSITION, monthSelectionPosition);
        outState.putInt(ADAPTER_POSITION, customerSelectionPosition);
        super.onSaveInstanceState(outState);
    }

    private List<Customer> createUnpricedCustomersForMonthList(List<Customer> customers) {
        List<Customer> customersWithUnpricedServices = new ArrayList<>();

            for(Customer c: customers) {
                if(c.hasUnpricedServicesForMonth(monthSelectionPosition)) {
                    customersWithUnpricedServices.add(c);
                }
            }
            return customersWithUnpricedServices;
        }

    private class FragmentStatePageAdapter extends FragmentStatePagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> fragmentTitle = new ArrayList<>();

        public FragmentStatePageAdapter(FragmentManager fm) {
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



