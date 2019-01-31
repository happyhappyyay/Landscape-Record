package com.happyhappyyay.landscaperecord.Adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.POJO.Service;
import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.Utility.Authentication;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class RecyclerViewCustomerAdapter extends RecyclerView.Adapter {
    private final int PAYMENT = 0, SERVICE = 1, TITLE = 2;
    private final String TITLE_TEXT = "TITLE";
    private final boolean ADMIN = Authentication.getAuthentication().getUser().isAdmin();
    private List<Object> objects;
    private Customer customer;

    public RecyclerViewCustomerAdapter(Customer customer) {
        this.customer = customer;
        objects = createObjectList();
    }

    private List<Object> createObjectList() {
        List<Service> services = customer.getCustomerServices();
        List<Object> objects = new ArrayList<>();
        if(services.size() > 0) {
            objects.add(TITLE_TEXT);
            services = sortServicesByStartTime(services);
            objects.addAll(services);
        }
        if(ADMIN) {
            List<String> paymentStrings = customer.getPayment().retrieveAllPaymentReceipts();
            if(paymentStrings.size() > 0) {
                objects.add(TITLE_TEXT);
                objects.addAll(paymentStrings);
            }
        }
        return objects;
    }

    private List<Service> sortServicesByStartTime(List<Service> services) {
        Collections.sort(services, new Comparator<Service>() {
            public int compare(Service service1, Service service2) {
                if (service1.getStartTime() > service2.getStartTime()) return -1;
                if (service1.getStartTime() < service2.getStartTime()) return 1;
                if (service1.getEndTime() > service2.getEndTime()) return -1;
                if (service1.getEndTime() < service2.getEndTime()) return 1;
                return 0;
            }
        });
        return services;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case PAYMENT:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_customer_payment_item, parent, false);
                return new RecyclerViewCustomerAdapter.PaymentViewHolder(view);
            case SERVICE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_customer_service_item, parent, false);
                return new RecyclerViewCustomerAdapter.ServiceViewHolder(view);
            case TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_customer_title_item, parent, false);
                return new RecyclerViewCustomerAdapter.TitleViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_pricing_recycler_item, parent, false);
                return new RecyclerViewCustomerAdapter.ServiceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case SERVICE:
                ServiceViewHolder vh1 = (ServiceViewHolder) holder;
                vh1.bindView(position);
                break;
            case PAYMENT:
                PaymentViewHolder vh2 = (PaymentViewHolder) holder;
                vh2.bindView(position);
                break;
            case TITLE:
                TitleViewHolder vh3 = (TitleViewHolder) holder;
                vh3.bindView(position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        Object object = objects.get(position);
        if (object instanceof String) {
            if(object.equals(TITLE_TEXT) ) {
                return TITLE;
            }
            return PAYMENT;
        } else if (object instanceof Service) {
            return SERVICE;
        }
        return -1;
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        public TitleViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.view_customer_title_title);
        }

        public void bindView(int position) {
            if(objects.get(position + 1) instanceof Service) {
                title.setText("SERVICES");
            }
            else {
                title.setText("PAYMENTS");
            }
        }
    }

    private class PaymentViewHolder extends RecyclerView.ViewHolder {
        TextView dateText, amountText, typeText;

        public PaymentViewHolder(View view) {
            super(view);
            dateText = view.findViewById(R.id.view_customer_payment_date);
            amountText = view.findViewById(R.id.view_customer_payment_amount);
            typeText = view.findViewById(R.id.view_customer_pament_type);
        }

        public void bindView(int position) {
            String paymentString = (String) objects.get(position);
            String date = paymentString.substring(0,paymentString.indexOf(":"));
            String amount = paymentString.substring(paymentString.indexOf("$")+1, paymentString.indexOf("C") -1);
            String type = paymentString.substring(paymentString.indexOf("C"));
            dateText.setText(date);
            amountText.setText(amount);
            typeText.setText(type);
        }
    }

    private class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText, dateText, priceText, paidText, stateText;

        public ServiceViewHolder(View view) {
            super(view);
            serviceText = view.findViewById(R.id.view_customer_service_name);
            dateText = view.findViewById(R.id.view_customer_service_date);
            priceText = view.findViewById(R.id.view_customer_service_price);
            paidText = view.findViewById(R.id.view_customer_service_paid);
            if(ADMIN) {
                priceText.setVisibility(View.VISIBLE);
                paidText.setVisibility(View.VISIBLE);
            }
            stateText = view.findViewById(R.id.view_customer_service_state);
        }

        public void bindView(final int position) {
            Service service = (Service) objects.get(position);
            if (service != null) {
                    serviceText.setText(service.getServices());
                    dateText.setText(service.retrieveStartToEndString());
                    priceText.setText(String.format(Locale.US, "%.2f", customer.getPayment().returnServicePrice(service.getServices())));
                    if(service.isPaid()) {
                        paidText.setText("Paid");
                    }
                    else {
                        paidText.setText("Unpaid");
                    }
                    if(service.getEndTime() > 0) {
                        stateText.setText("Completed");
                        stateText.setTextColor(Color.RED);
                    }
                    else {
                        stateText.setText("In-Progress");
                        stateText.setTextColor(Color.GREEN);
                    }
            }
        }
    }
}
