package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewCustomersAdapter extends Adapter {
    private static final String TAG = "selected for work";
    protected List<Customer> customers;
    private Context context;

    public RecyclerViewCustomersAdapter( Context context, List<Customer> customers) {
        this.customers = customers;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new RecyclerViewCustomersAdapter.ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewCustomersAdapter.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    public List<Customer> getCustomers() {
        return customers;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView customerName, customerAddress, customerDay;
        private ConstraintLayout constraintLayout;
        int customerIDToPass;

        public ListViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.contact_item_name);
            customerAddress = view.findViewById(R.id.contact_item_address);
            customerDay = view.findViewById(R.id.contact_item_day);
            constraintLayout = view.findViewById(R.id.contact_item_layout);
            constraintLayout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customerIDToPass = customers.get(getAdapterPosition()).getCustomerId();
                            Intent intent = new Intent(context, ViewCustomer.class);
                            intent.putExtra("CUSTOMER_ID", customerIDToPass);
                            context.startActivity(intent);
                        }
                    });
        }

        public void bindView(int position) {
            Customer customer = customers.get(position);
            String customerFullName = customer.getCustomerFirstName() + " " + customer.getCustomerLastName();
            String customerFullAddress = customer.concatenateFullAddress();
            String customerDayOfWeek = customer.getCustomerDay();
            customerName.setText(customerFullName);
            customerAddress.setText(customerFullAddress);
            customerDay.setText(customerDayOfWeek);
        }

    }
}
