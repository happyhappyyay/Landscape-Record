package com.happyhappyyay.landscaperecord.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecyclerViewUser extends RecyclerView.Adapter {

    private List<LogActivity> logs;

    public RecyclerViewUser(List<LogActivity> logs) {
        this.logs = sortLogsByModifiedTime(logs);
    }

    private List<LogActivity> sortLogsByModifiedTime(List<LogActivity> logs) {
        Collections.sort(logs, new Comparator<LogActivity>() {
            public int compare(LogActivity log1, LogActivity log2) {
                return Long.compare(log2.getModifiedTime(), log1.getModifiedTime());
            }
        });
        return logs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new RecyclerViewUser.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewUser.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView logItem;

        private ListViewHolder(View view) {
            super(view);
            logItem = view.findViewById(R.id.log_item_text);
        }
        public void bindView(int position) {
            logItem.setText(logs.get(position).toString());
        }
    }
}
