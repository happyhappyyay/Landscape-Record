package com.happyhappyyay.landscaperecord.adapter;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.activity.ViewUser;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class RecyclerViewUsers extends Adapter {
    protected List<User> users;
    private Context context;

    public RecyclerViewUsers(Context context, List<User> users) {
        this.users = users;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new RecyclerViewUsers.ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((RecyclerViewUsers.ListViewHolder) holder).bindView(position);
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

        private ListViewHolder(View view) {
            super(view);
            username = view.findViewById(R.id.user_item_name);
            userHours = view.findViewById(R.id.user_item_hours);
            userCheckIn = view.findViewById(R.id.user_item_check_in);
            constraintLayout = view.findViewById(R.id.user_item_constraint_layout);
            constraintLayout.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            int posToPass = getAdapterPosition();
                            Intent intent = new Intent(context, ViewUser.class);
                            intent.putExtra("ADAPTER_POSITION", posToPass);
                            context.startActivity(intent);
                        }
                    });
        }

        public void bindView(int position) {
            User user = users.get(position);
            String hoursUnpaid = Math.round(user.getHours()) + " " +context.getString(R.string.hrs);
            String checkInText = context.getString(R.string.recycler_view_users_not_checked_in);
            if (user.getStartTime() > 0) {
                checkInText = context.getString(R.string.check_in_text) + " " + Util.convertLongToStringDateTime(user.getStartTime());
            }

            username.setText(user.toString());
            userHours.setText(hoursUnpaid);
            userCheckIn.setText(checkInText);
        }
    }
}
