package com.happyhappyyay.landscaperecord.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Util;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecyclerServicePaymentAdapter extends Adapter {
    protected Customer customer;

    public RecyclerServicePaymentAdapter(Customer customer) {
        String TAG = "Initialize";
        Log.d(TAG, "start adapter");
        this.customer = customer;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_payment_item, parent, false);
        return new RecyclerServicePaymentAdapter.ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerServicePaymentAdapter.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return customer.getCustomerServices().size() + customer.getPayment().getPaymentReceiptAmount().size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView lineItemText, totalText;
        public List<String> payments;


        public ListViewHolder(View view) {
            super(view);
            lineItemText = view.findViewById(R.id.service_payment_item_item);
            totalText = view.findViewById(R.id.service_payment_item_total);
            payments = customer.getPayment().retrieveAllPaymentReceipts();
            payments.addAll(customer.retrieveServicesWithPrices());
            payments = sortStringsByDate(payments);
        }

        private List<String> sortStringsByDate(List<String> strings) {
            Collections.sort(strings, new Comparator<String>() {
                public int compare(String service1, String service2) {
                    if (Util.convertStringDateToMilliseconds(service1.substring(0,9)) > Util.convertStringDateToMilliseconds(service2.substring(0,9))) return -1;
                    if (Util.convertStringDateToMilliseconds(service1.substring(0,9)) < Util.convertStringDateToMilliseconds(service2.substring(0,9))) return 1;
                    return 0;
                }});
            return strings;
        }

        public void bindView(int position) {
            String amount = "";
            String servicePayment = payments.get(position).substring(11);
                if (servicePayment.substring(0, 1).equals("$")) {
                    amount = servicePayment;
                    servicePayment = payments.get(position).substring(0,10) + " PAYMENT";
                    lineItemText.setTextColor(Color.GREEN);
                    totalText.setTextColor(Color.GREEN);
                }
                else {
                    servicePayment = payments.get(position);
                    for(int i = 0; i < servicePayment.length(); i++) {
                        if(servicePayment.substring(i,i+1).equals("$")) {
                            amount = servicePayment.substring(i);
                            servicePayment = servicePayment.substring(0, i);
                        }
                    }
                    lineItemText.setTextColor(Color.BLACK);
                    totalText.setTextColor(Color.BLACK);
                }
            lineItemText.setText(servicePayment);
            totalText.setText(amount);
        }
    }
}
