package com.example.kingdenis.landscaperecord;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerLogAdapter extends RecyclerView.Adapter {
    private static final String TAG = "selected for work";
    protected List<LogActivity> logs;

    public RecyclerLogAdapter(List<LogActivity> logs) {
        this.logs = logs;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.log_item, parent, false);
        return new ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return logs.size();
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView log;


        public ListViewHolder(View view) {
            super(view);
            log = (TextView) view.findViewById(R.id.log_item_text);
        }

        public void bindView(int position) {
            log.setText(logs.get(position).toString());

        }

    }
}
