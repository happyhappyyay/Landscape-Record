package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;

import java.util.ArrayList;
import java.util.List;

public class ServicePricing extends AppCompatActivity implements DatabaseAccess<Customer>{
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_pricing);
        recyclerView = findViewById(R.id.service_pricing_recycler);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        Util.findAllObjects(this, Util.CUSTOMER_REFERENCE);
    }

    private List<Customer> findCustomersWithUnpricedServices(List<Customer> customers) {
        List<Customer> customersWithUnpricedServices = new ArrayList<>();
        for(Customer c: customers) {
            for(int i = 0; i < c.getCustomerServices().size(); i++) {
                if(!c.getCustomerServices().get(i).isPriced()) {
                    customersWithUnpricedServices.add(c);
                    break;
                }
            }
        }
        return customersWithUnpricedServices;
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
        RecyclerServicePricingAdapter adapter = new RecyclerServicePricingAdapter(findCustomersWithUnpricedServices(databaseObjects), this);
        recyclerView.setAdapter(adapter);
    }
}
