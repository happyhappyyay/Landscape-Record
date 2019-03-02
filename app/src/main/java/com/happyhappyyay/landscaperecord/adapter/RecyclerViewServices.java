package com.happyhappyyay.landscaperecord.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.activity.JobServices;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecyclerViewServices extends Adapter {
    private List<Customer> customers;
    private Customer customer;
    private List<Service> services;
    private int viewBy;
    private int sortBy;
    private Context context;

    public RecyclerViewServices(List<Customer> customers, Context context, int viewBy, int sortBy) {
        this.customers = customers;
        this.context = context;
        this.viewBy = viewBy;
        this.sortBy = sortBy;
        services = createServicesList();
    }

    private List<Service> createServicesList(){
        final int ALL = 0;
        final int IN_PROGRESS = 1;
        final int COMPLETED = 2;
        final int CUSTOMER = 2;

        List<Service> services = new ArrayList<>();
        List<Customer> customers = this.customers;
        if(sortBy == CUSTOMER) {
            if (customer != null) {
                customers = new ArrayList<>();
                customers.add(customer);
            }
        }
        switch (viewBy) {
            case ALL:
                for(Customer c: customers) {
                    services.addAll(c.getServices());
                }
                break;
            case IN_PROGRESS:
                for(Customer c: customers) {
                    for(Service s: c.getServices()) {
                        if (s.getEndTime() <= 0) {
                            services.add(s);
                        }
                    }
                }
                break;
            case COMPLETED:
                for(Customer c: customers) {
                    for(Service s: c.getServices()) {
                        if (s.getEndTime() > 0) {
                            services.add(s);
                        }
                    }
                }
                break;

        }
        return sortServicesList(services);
    }

    private List<Service> sortServicesList(List<Service> services) {
        final int START = 0;
        final int END = 1;
        switch (sortBy) {
            case START:
                services = sortServicesByStartTime(services);
                break;
            case END:
                services = sortServicesByEndTime(services);
                break;
            default:
                services = sortServicesByEndTime(services);
                break;
        }
        return services;
    }

    public void filterServicesByDate(String date){
        services = createServicesList();
        List<Service> tempServices = new ArrayList<>();
        final int START = 0;
        final int END = 1;
        for (Service s: services) {
            if(sortBy == START) {
                if(Util.convertLongToStringDate(s.getStartTime()).equals(date)) {
                    tempServices.add(s);
                }
            }
            else if (sortBy == END){
                if(Util.convertLongToStringDate(s.getEndTime()).equals(date)) {
                    tempServices.add(s);
                }
            }
            else {
                if(Util.convertLongToStringDate(s.getStartTime()).equals(date) ||
                        Util.convertLongToStringDate(s.getEndTime()).equals(date)) {
                    tempServices.add(s);
                }
            }
        }
        services = tempServices;
    }

    private List<Service> sortServicesByEndTime(List<Service> services) {
        Collections.sort(services, new Comparator<Service>() {
            public int compare(Service service1, Service service2) {
                int moveInList = Long.compare(service2.getEndTime(), service1.getEndTime());
                if (moveInList != 0) {return moveInList;
                }
                else {
                    return Long.compare(service2.getStartTime(), service1.getStartTime());
                }
            }});
        return services;
    }

    private List<Service> sortServicesByStartTime(List<Service> services) {
        Collections.sort(services, new Comparator<Service>() {
            public int compare(Service service1, Service service2) {
                int moveInList = Long.compare(service2.getStartTime(), service1.getStartTime());
                if (moveInList != 0) {return moveInList;
                }
                else {
                    return Long.compare(service2.getEndTime(), service1.getEndTime());
                }
            }});
        return services;
    }

    public void setViewBy(int viewBy) {
        this.viewBy = viewBy;
        services = createServicesList();
        notifyDataSetChanged();
    }

    public void setSortBy(int sortBy) {
        this.sortBy = sortBy;
        services = createServicesList();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_services_item, parent, false);
        return new RecyclerViewServices.ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewServices.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
        services = createServicesList();
        notifyDataSetChanged();
    }

    public List<Service> getServices() {
        return services;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView customerText, dateText, stateText, usernameText, manHoursText, mileageText,
        servicesText, editServices, priceText, paidText, idText;


        private ListViewHolder(View view) {
            super(view);
            customerText = view.findViewById(R.id.view_services_customer_text);
            dateText = view.findViewById(R.id.view_services_date_text);
            stateText = view.findViewById(R.id.view_services_progress_state_text);
            usernameText = view.findViewById(R.id.view_services_username_text);
            manHoursText = view.findViewById(R.id.view_services_man_hours_text);
            mileageText = view.findViewById(R.id.view_services_mileage_text);
            servicesText = view.findViewById(R.id.view_services_services_text);
            editServices = view.findViewById(R.id.view_services_item_edit);
            priceText = view.findViewById(R.id.view_services_price);
            paidText = view.findViewById(R.id.view_services_item_paid);
            idText = view.findViewById(R.id.view_services_item_service_id);
        }

        public void bindView(int position) {
            boolean admin = Authentication.getAuthentication().getUser().isAdmin();
            final Service service = services.get(position);

            String startToEndDate = service.convertStartTimeToDateString() + " - " + service.convertEndTimeToDateString();
            String manHoursString = Double.toString(service.getManHours()) + context.getString(R.string.hrs);
            String mileageString = Double.toString(service.getMi()) + context.getString(R.string.mi);
            String services = service.getServices();
            String price = context.getString(R.string.money) + service.getPrice();
            String paid = context.getString(R.string.money) + service.getAmountPaid() + "/";
            String id = context.getString(R.string.recycler_view_services_service_id) + service.getId();
            customerText.setText(service.getCustomerName());
            dateText.setText(startToEndDate);
            stateText.setText(service.checkCompleted() ? context.getString(R.string.view_services_completed):context.getString(R.string.view_services_in_progress)
                    );
            stateText.setTextColor(service.checkCompleted() ? Color.RED : Color.GREEN );
            usernameText.setText(service.getUsername());
            manHoursText.setText(manHoursString);
            mileageText.setText(mileageString);
            servicesText.setText(services);
            if(admin) {
                priceText.setText(price);
                paidText.setText(paid);
            }
            else {
                priceText.setText("");
                paidText.setText("");
            }
            idText.setText(id);
            editServices.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Bundle b = new Bundle();
                    b.putParcelable("SERVICE", service);
                    Intent intent = new Intent(context, JobServices.class);
                    intent.putExtra("bundle", b);
                    context.startActivity(intent);

                }
            });

        }
    }
}
