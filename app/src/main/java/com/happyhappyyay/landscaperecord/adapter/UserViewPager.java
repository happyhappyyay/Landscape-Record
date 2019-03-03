package com.happyhappyyay.landscaperecord.adapter;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.activity.EditUser;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserViewPager extends PagerAdapter implements DatabaseAccess<User> {

    private static final String TAG = "MyPagerAdapter";
    private List<User> users;
    private List<LogActivity> logs;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private User user;

    public UserViewPager(Context context, List<User> users, List<LogActivity> logs) {
        mContext = context;
        this.users = users;
        this.logs = logs;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        @Override
        public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position) {
            View view = retrieveView(container,position);

            container.addView(view);
            Log.i(TAG, "instantiateItem() [position: " + position + "]" + " childCount:" + container.getChildCount());

            return view;
        }

        private View retrieveView(final ViewGroup container, final int position) {
            View view = mLayoutInflater.inflate(R.layout.view_user, container, false);
            final User user = users.get(position);
            List<LogActivity> userLogs = retrieveLogsForUser(user);
            this.user = user;
            if(user.isAdmin()) {
                TextView adminView = view.findViewById(R.id.view_user_admin);
                adminView.setVisibility(View.VISIBLE);
            }
            TextView userFirstName = view.findViewById(R.id.view_user_first_name);
            TextView userLastName = view.findViewById(R.id.view_user_last_name);
            TextView userPassword = view.findViewById(R.id.view_user_password);
            TextView userHours = view.findViewById(R.id.view_user_hours);
            TextView username = view.findViewById(R.id.view_user_name);
            username.setText(user.toString());
            userFirstName.setText(user.getFirst());
            userLastName.setText(user.getLast());
            userPassword.setText(user.getPassword());
            userHours.setText(String.format(Locale.US, "%.2f",user.getHours()));

            ImageView backArrow = view.findViewById(R.id.view_user_back_arrow);
            ImageView forwardArrow = view.findViewById(R.id.view_user_forward_arrow);
            if(position - 1 >= 0) {
                backArrow.setVisibility(View.VISIBLE);
            }
            else {
                backArrow.setVisibility(View.INVISIBLE);
            }

            if(position + 1 < getCount()) {
                forwardArrow.setVisibility(View.VISIBLE);
            }
            else {
                forwardArrow.setVisibility(View.INVISIBLE);
            }
            boolean admin = Authentication.getAuthentication().getUser().isAdmin();
            if(admin) {
                ImageButton editUser = view.findViewById(R.id.view_user_edit_image_button);
                editUser.setVisibility(View.VISIBLE);
                editUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, EditUser.class);
                        intent.putExtra("USER_ID", user.getId());
                        mContext.startActivity(intent);
                    }
                });
                ImageView deleteUser = view.findViewById(R.id.view_user_delete_image);
                deleteUser.setVisibility(View.VISIBLE);
                deleteUser.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        users.remove(user);
                                        deleteUser(user);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage(mContext.getString(R.string.customer_view_pager_delete) + " " + user.getName() + " ?")
                                .setPositiveButton(mContext.getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(mContext.getString(R.string.no), dialogClickListener).show();
                    }
                });
            }

            RecyclerView recyclerView = view.findViewById(R.id.view_user_recycler);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            final RecyclerViewUser adapter = new RecyclerViewUser(userLogs);
            recyclerView.setAdapter(adapter);
            return view;
        }

        private List<LogActivity> retrieveLogsForUser(User user) {
            List<LogActivity> userLogs = new ArrayList<>();
            for(LogActivity l: logs) {
                if (l.getUsername().equals(user.getName())) {
                    userLogs.add(l);
                }
            }
            return userLogs;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            Log.i(TAG, "destroyItem() [position: " + position + "]" + " childCount:" + container.getChildCount());
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            User user = (User) ((View) object).getTag();
            int position = users.indexOf(user);
            if (position >= 0) {
                return position;
            } else {
                return POSITION_NONE;
            }
        }

    private void updateUsersSelection(List<User> users) {
        this.users = users;
        notifyDataSetChanged();
    }

    private void deleteUser(User user) {
            Util.deleteObject(this, Util.USER_REFERENCE, user);
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public String createLogInfo() {
        return user.getName();
    }

    @Override
    public void onPostExecute(List<User> databaseObjects) {
        updateUsersSelection(users);
    }
}