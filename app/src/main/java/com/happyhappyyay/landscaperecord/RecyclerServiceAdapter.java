package com.happyhappyyay.landscaperecord;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerServiceAdapter extends Adapter {
    private static final String TAG = "selected for work";
    protected List<Service> services;

    public RecyclerServiceAdapter(List<Service> services) {
        this.services = services;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_services_item, parent, false);
        return new RecyclerServiceAdapter.ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerServiceAdapter.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return services.size();
    }

    public void setServices(List<Service> services) {
        this.services = services;
    }

    public List<Service> getServices() {
        return services;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView customerText, dateText, stateText, usernameText, manHoursText, mileageText,
        servicesText;


        public ListViewHolder(View view) {
            super(view);
            customerText = view.findViewById(R.id.view_services_customer_text);
            dateText = view.findViewById(R.id.view_services_date_text);
            stateText = view.findViewById(R.id.view_services_progress_state_text);
            usernameText = view.findViewById(R.id.view_services_username_text);
            manHoursText = view.findViewById(R.id.view_services_man_hours_text);
            mileageText = view.findViewById(R.id.view_services_mileage_text);
            servicesText = view.findViewById(R.id.view_services_services_text);
        }

        public void bindView(int position) {
            Service service = services.get(position);
            customerText.setText(service.getCustomerName());
            dateText.setText(service.convertStartTimeToDateString() + " - " + service.convertEndTimeToDateString());
            stateText.setText(service.isPause() ? "In-Progress": "Completed");
            stateText.setTextColor(service.isPause() ? Color.GREEN : Color.RED);
            usernameText.setText(service.getUsername());
            manHoursText.setText(Double.toString(service.getManHours()) + "hrs");
            mileageText.setText(Double.toString(service.getMileage()) + "mi");
            servicesText.setText(service.getServices());

        }

    }
}
