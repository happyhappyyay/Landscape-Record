package com.happyhappyyay.landscaperecord.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.database_interface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class RecyclerServicePricingAdapter extends Adapter implements DatabaseAccess<Customer> {
    protected Context context;
    private Customer customer;
    private List<Service> services;

    public RecyclerServicePricingAdapter(Customer customer, Context context, int monthSelection) {
        this.customer = customer;
        this.context = context;
        services = customer.retrieveUnpricedServicesForMonth(monthSelection);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_pricing_recycler_item, parent, false);
                return new RecyclerServicePricingAdapter.ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ServiceViewHolder vh2 = (ServiceViewHolder) holder;
                vh2.bindView(position);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
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
        notifyDataSetChanged();
    }

    public List<Service> getServices() {
        return services;
    }

    private class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText;
        EditText priceText;
        Button priceButton;


        public ServiceViewHolder(View view) {
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
                    priceButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            final Double servicePrice = Double.parseDouble(priceText.getText().toString());
                            if (servicePrice > 0) {
                                if(!amount.equals(servicePrice) & amount > 0) {
                                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            switch (which) {
                                                case DialogInterface.BUTTON_POSITIVE:
                                                    customer.getPayment().addServicePrice(service.getServices(), servicePrice, true);
                                                    break;

                                                case DialogInterface.BUTTON_NEGATIVE:
                                                    customer.getPayment().addServicePrice(service.getServices(), servicePrice);
                                                    break;
                                            }
                                        }
                                    };

                                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                                    builder.setMessage("This price differs from the default ($" +  amount + "). Would you like to set " +
                                            "this price ($" + servicePrice + ") as the new default for this specific customer and service?"
                                    ).setPositiveButton("Yes", dialogClickListener)
                                            .setNegativeButton("No", dialogClickListener).show();
                                }
                                else {
                                    customer.getPayment().addServicePrice(service.getServices(), servicePrice, true);
                                }
                                Util.updateObject(RecyclerServicePricingAdapter.this, Util.CUSTOMER_REFERENCE, customer);
                            }
                        }
                    });
                }
            }
        }
    }
}
