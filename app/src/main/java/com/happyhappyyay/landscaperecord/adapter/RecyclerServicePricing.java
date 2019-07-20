package com.happyhappyyay.landscaperecord.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class RecyclerServicePricing extends Adapter implements DatabaseAccess<Customer> {
    protected Context context;
    private Customer customer;
    private List<Service> services;
    private List<Service> pricedServices;
    private double total;
    private int adapterPosition;

    public RecyclerServicePricing(Customer customer, Context context, int monthSelection) {
        this.customer = customer;
        this.context = context;
        services = customer.retrieveUnpricedServicesForMonth(monthSelection);
        pricedServices = new ArrayList<>();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        if(viewType == 0) {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_pricing_recycler_item, parent, false);
            return new RecyclerServicePricing.ServiceViewHolder(view);
        }
        else {
            view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_pricing_total, parent, false);
            return new RecyclerServicePricing.TotalViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder.getItemViewType() == 0) {
            ServiceViewHolder vh2 = (ServiceViewHolder) holder;
            vh2.bindView(position);
        }
        else {
            TotalViewHolder vh = (TotalViewHolder) holder;
            vh.bindView();
        }
    }

    @Override
    public int getItemCount() {
        return services.size() + 1;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (position < services.size()){
            return 0;
        }
        return 1;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String createLogInfo() {
        return null;
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        notifyItemChanged(adapterPosition);
    }

    public List<Service> getPricedServices(){
        return pricedServices;
    }

    public List<Service> getServices() {
        return services;
    }

    private class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText;
        EditText priceText;
        Button priceButton;


        private ServiceViewHolder(View view) {
            super(view);
            serviceText = view.findViewById(R.id.service_pricing_recycler_item_text);
            priceText = view.findViewById(R.id.service_pricing_recycler_item_price_text);
            priceButton = view.findViewById(R.id.service_pricing_recycler_item_set_button);

        }

        public void bindView(final int position) {
            final Service service = services.get(position);
            if (service != null) {
                if (customer != null) {
                    String servicesWithDate = Util.convertLongToStringDate(service.getEndTime()) + service.getServices();
                    serviceText.setText(servicesWithDate);
                    final Double amount = customer.getPayment().returnDefaultServicePrice(service.getServices());
                    String price = Double.toString(amount);
                    priceText.setText(price);
                    service.setPrice(amount);
                    pricedServices.add(service);
                    try {
                        total += Double.parseDouble(priceText.getText().toString());
                    }
                    catch (Exception e){
                        e.printStackTrace();
                    }
                    priceText.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void afterTextChanged(Editable s) {
                            try {
                                double amount = Double.parseDouble(priceText.getText().toString());
                                total += amount;
                                notifyItemChanged(getItemCount() - 1);
                                service.setPrice(amount);
                                pricedServices.set(position, service);
                            }
                            catch (Exception e){
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void beforeTextChanged(CharSequence s, int start,
                                                      int count, int after) {
                            try {
                                double amount = Double.parseDouble(priceText.getText().toString());
                                if(total - amount > 0) {
                                    total -= amount;
                                }
                                else{
                                 total = 0;
                                }
                            }
                            catch(Exception e){
                                e.printStackTrace();
                            }

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start,
                                                  int before, int count) {

                        }
                    });
                    priceButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final double servicePrice = Double.parseDouble(priceText.getText().toString());
                            if (servicePrice > 0) {
                                if(amount != 0) {
                                    if (!amount.equals(servicePrice)) {
                                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                switch (which) {
                                                    case DialogInterface.BUTTON_POSITIVE:
                                                        customer.getPayment().addDefaultServicePrice(service.getServices(), servicePrice);
                                                        adapterPosition = position;
                                                        total -= servicePrice;
                                                        Util.updateObject(RecyclerServicePricing.this, Util.CUSTOMER_REFERENCE, customer);
                                                        break;

                                                    case DialogInterface.BUTTON_NEGATIVE:
                                                        break;
                                                }
                                            }
                                        };

                                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                        builder.setMessage("This price differs from the default ($" + amount + "). Would you like to set " +
                                                "this price ($" + servicePrice + ") as the new default for this specific customer and service?"
                                        ).setPositiveButton("Yes", dialogClickListener)
                                                .setNegativeButton("No", dialogClickListener).show();
                                    }
                                }
                                else {
                                    customer.getPayment().addDefaultServicePrice(service.getServices(), servicePrice);
                                    adapterPosition = position;
                                    total -= servicePrice;
                                    Util.updateObject(RecyclerServicePricing.this, Util.CUSTOMER_REFERENCE, customer);
                                }

                            }
                        }
                    });
                }
            }
        }
    }

    private class TotalViewHolder extends RecyclerView.ViewHolder {
        TextView totalText;

        private TotalViewHolder(View view) {
            super(view);
            totalText = view.findViewById(R.id.service_pricing_total_text);
        }

        public void bindView() {
            String text = "Total: " + String.format(Locale.US, "%.2f", total);
            totalText.setText(text);
        }
    }
}
