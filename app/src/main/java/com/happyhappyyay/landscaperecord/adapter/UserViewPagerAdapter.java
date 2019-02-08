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
import com.happyhappyyay.landscaperecord.database_interface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class UserViewPagerAdapter extends PagerAdapter implements DatabaseAccess<User> {

    private static final String TAG = "MyPagerAdapter";
    private List<User> users;
    private List<LogActivity> logs;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private User user;

    public UserViewPagerAdapter(Context context, List<User> users, List<LogActivity> logs) {
        mContext = context;
        this.users = users;
        this.logs = logs;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

        //Abstract method in PagerAdapter
        @Override
        public int getCount() {
            return users.size();
        }

        //Abstract method in PagerAdapter

        /**
         * @return true if the value returned from {@link #instantiateItem(ViewGroup, int)} is the
         * same object as the {@link View} added to the {@link android.support.v4.view.ViewPager}.
         */
        @Override
        public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
            return object == view;
        }

        /**
         * Instantiate the {@link View} which should be displayed at {@code position}. Here we
         * inflate a layout from the apps resources and then change the text view to signify the position.
         */
        @Override
        public @NonNull Object instantiateItem(@NonNull ViewGroup container, int position) {
            // Inflate a new layout from our resources
            // Retrieve a TextView from the inflated View, and update it's text
            View view = retrieveView(container,position);
            // Add the newly created View to the ViewPager
            container.addView(view);
            Log.i(TAG, "instantiateItem() [position: " + position + "]" + " childCount:" + container.getChildCount());
            // Return the View
            return view;
        }

        private View retrieveView(final ViewGroup container, final int position) {
            // Inflate a new layout from our resources
            View view = mLayoutInflater.inflate(R.layout.view_user, container, false);
            // Retrieve a TextView from the inflated View, and update it's text
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
            userFirstName.setText(user.getFirstName());
            userLastName.setText(user.getLastName());
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
                        intent.putExtra("USER_ID", user.getUserId());
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
                        builder.setMessage("Are you sure you want to delete user " + user.getName() + " ?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });
            }

            RecyclerView recyclerView = view.findViewById(R.id.view_user_recycler);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            final RecyclerViewUserAdapter adapter = new RecyclerViewUserAdapter(userLogs);
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

        /**
         * Destroy the item from the {@link android.support.v4.view.ViewPager}. In our case this is simply removing the
         * {@link View}.
         */
        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            Log.i(TAG, "destroyItem() [position: " + position + "]" + " childCount:" + container.getChildCount());
        }

        /**
         * This method is only gets called when we invoke {@link #notifyDataSetChanged()} on this adapter.
         * Returns the index of the currently active fragments.
         * There could be minimum two and maximum three active fragments(suppose we have 3 or more  fragments to show).
         * If there is only one fragment to show that will be only active fragment.
         * If there are only two fragments to show, both will be in active state.
         * PagerAdapter keeps left and right fragments of the currently visible fragment in ready/active state so that it could be shown immediate on swiping.
         * Currently Active Fragments means one which is currently visible one is before it and one is after it.
         *
         * @param object Active Fragment reference
         * @return Returns the index of the currently active fragments.
         */
        @Override
        public int getItemPosition(@NonNull Object object) {
            User user = (User) ((View) object).getTag();
            int position = users.indexOf(user);
            if (position >= 0) {
                // The current data matches the data in this active fragment, so let it be as it is.
                return position;
            } else {
                // Returning POSITION_NONE means the current data does not match the data this fragment is showing right now.  Returning POSITION_NONE constant will force the fragment to redraw its view layout all over again and show new data.
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