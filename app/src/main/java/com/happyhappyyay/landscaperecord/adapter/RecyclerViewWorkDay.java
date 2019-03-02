package com.happyhappyyay.landscaperecord.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;

import java.util.List;

public class RecyclerViewWorkDay extends Adapter {
    protected List<String> services;
    protected List<String> users;

    public RecyclerViewWorkDay(List<String> users, List<String> services) {
        this.services = services;
        this.users = users;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_work_day_item, parent, false);
        return new RecyclerViewWorkDay.ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewWorkDay.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        if (users.size() > services.size() ) {
            return users.size();
        }
        return services.size();
    }

    public void setServices(List<String> services) {
        this.services = services;
    }

    public List<String> getServices() {
        return services;
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
        private TextView userText, serviceText;


        private ListViewHolder(View view) {
            super(view);
            serviceText = view.findViewById(R.id.view_work_day_item_customer);
            userText = view.findViewById(R.id.view_work_day_item_user);
        }

        public void bindView(int position) {
            if(users.size() > position) {
                String username = users.get(position);
                userText.setText(username);
            }
            if(services.size() > position) {
                String customerServices = services.get(position);
                serviceText.setText(customerServices);
            }
        }

    }
}
