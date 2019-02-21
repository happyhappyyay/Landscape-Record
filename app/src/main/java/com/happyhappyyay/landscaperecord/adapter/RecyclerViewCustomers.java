package com.happyhappyyay.landscaperecord.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.activity.ViewCustomer;
import com.happyhappyyay.landscaperecord.pojo.Customer;

import java.util.List;

public class RecyclerViewCustomers extends Adapter {
    protected List<Customer> customers;
    private Context context;

    public RecyclerViewCustomers(Context context, List<Customer> customers) {
        this.customers = customers;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.customer_item, parent, false);
        return new RecyclerViewCustomers.ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewCustomers.ListViewHolder) holder).bindView(position);
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
        int customerPosToPass;

        private ListViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.contact_item_name);
            customerAddress = view.findViewById(R.id.contact_item_address);
            customerDay = view.findViewById(R.id.contact_item_day);
            constraintLayout = view.findViewById(R.id.contact_item_layout);
            constraintLayout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            customerPosToPass = getAdapterPosition();
                            Intent intent = new Intent(context, ViewCustomer.class);
                            intent.putExtra("ADAPTER_POSITION", customerPosToPass);
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
