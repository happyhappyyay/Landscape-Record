package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class RecyclerViewUsersAdapter extends Adapter {
    private static final String TAG = "selected for work";
    protected List<User> users;
    private Context context;

    public RecyclerViewUsersAdapter(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new RecyclerViewUsersAdapter.ListViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewUsersAdapter.ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void setCustomers(List<User> users) {
        this.users = users;
    }

    public List<User> getCustomers() {
        return users;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView username, userHours, userCheckIn;
        private ConstraintLayout constraintLayout;
        int userIDToPass;

        public ListViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.user_item_name);
            userHours = view.findViewById(R.id.user_item_hours);
            userCheckIn = view.findViewById(R.id.user_item_check_in);
            constraintLayout = view.findViewById(R.id.user_item_constraint_layout);
            constraintLayout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            userIDToPass = users.get(getAdapterPosition()).getUserId();
                            Intent intent = new Intent(context, ViewUser.class);
                            intent.putExtra("USER_ID", userIDToPass);
                            context.startActivity(intent);
                        }
                    });
        }

        public void bindView(int position) {
            User user = users.get(position);
            String hoursUnpaid = Math.round(user.getHours()) + " hrs";
            String checkInText = "Not checked in";
            if (user.getStartTime() > 0) {
                checkInText = "Checked in " + Util.convertLongToStringDateTime(user.getStartTime());
            }

            username.setText(user.toString());
            userHours.setText(hoursUnpaid);
            userCheckIn.setText(checkInText);
        }

    }
}
