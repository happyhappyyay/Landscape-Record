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

public class RecyclerViewUserAdapter extends RecyclerView.Adapter {

    private List<LogActivity> logs;

    public RecyclerViewUserAdapter(List<LogActivity> logs) {
        this.logs = sortLogsByModifiedTime(logs);
    }

    private List<LogActivity> sortLogsByModifiedTime(List<LogActivity> logs) {
        Collections.sort(logs, new Comparator<LogActivity>() {
            public int compare(LogActivity log1, LogActivity log2) {
                if (log1.getModifiedTime() > log2.getModifiedTime()) return -1;
                if (log1.getModifiedTime() < log2.getModifiedTime()) return 1;
                return 0;
            }
        });
        return logs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new RecyclerViewUserAdapter.ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewUserAdapter.ListViewHolder) holder).bindView(position);
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
        public TextView logItem;


        public ListViewHolder(View view) {
            super(view);
            logItem = view.findViewById(R.id.log_item_text);
        }

        public void bindView(int position) {
            logItem.setText(logs.get(position).toString());
        }
    }
}
