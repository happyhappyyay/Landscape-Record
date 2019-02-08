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
import com.happyhappyyay.landscaperecord.activity.EditCustomer;
import com.happyhappyyay.landscaperecord.database_interface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;
import java.util.Locale;

public class CustomerViewPagerAdapter extends PagerAdapter implements DatabaseAccess<Customer> {

    private static final String TAG = "MyPagerAdapter";
    private List<Customer> customers;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private Customer customer;

    public CustomerViewPagerAdapter(Context context, List<Customer> customers) {
        mContext = context;
        this.customers = customers;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

        //Abstract method in PagerAdapter
        @Override
        public int getCount() {
            return customers.size();
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
            View view = mLayoutInflater.inflate(R.layout.view_customer, container, false);
            // Retrieve a TextView from the inflated View, and update it's text
            final Customer customer = customers.get(position);
            this.customer = customer;
            if(customer.getCustomerBusiness() != null) {
                TextView businessName = view.findViewById(R.id.view_customer_business);
                businessName.setText(customer.getCustomerBusiness());
            }
            TextView customerTitle = view.findViewById(R.id.view_customer_name);
            TextView customerName = view.findViewById(R.id.view_customer_full_name);
            TextView customerAddress = view.findViewById(R.id.view_customer_full_address);
            TextView dayText = view.findViewById(R.id.view_customer_day);
            TextView phoneText = view.findViewById(R.id.view_customer_phone_number);
            TextView emailText = view.findViewById(R.id.view_customer_email);
            TextView mileageText = view.findViewById(R.id.view_customer_mileage);
            customerTitle.setText(customer.getName());
            dayText.setText(customer.getCustomerDay());
            if(customer.getCustomerPhoneNumber() != null) {
                phoneText.setText(customer.getCustomerPhoneNumber());
            }
            if(customer.getCustomerEmail() != null) {
                emailText.setText(customer.getCustomerEmail());
            }
            if(customer.getCustomerMileage() != null) {
                if(customer.getCustomerMileage() > 0) {
                    mileageText.setText(String.format(Locale.US, "%.2f", customer.getCustomerMileage()));
                }
            }
            customerName.setText(customer.getFullName());
            customerAddress.setText(customer.concatenateFullAddress());
            ImageView backArrow = view.findViewById(R.id.view_customer_back_arrow);
            ImageView forwardArrow = view.findViewById(R.id.view_customer_forward_arrow);
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
                ImageButton editCustomer = view.findViewById(R.id.view_customer_edit_image_button);
                editCustomer.setVisibility(View.VISIBLE);
                editCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(mContext, EditCustomer.class);
                        intent.putExtra("CUSTOMER_ID", customer.getCustomerId());
                        mContext.startActivity(intent);
                    }
                });
                ImageView deleteCustomer = view.findViewById(R.id.view_customer_delete_image);
                deleteCustomer.setVisibility(View.VISIBLE);
                deleteCustomer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        customers.remove(customer);
                                        deleteCustomer(customer);
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:
                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                        builder.setMessage("Are you sure you want to delete customer " + customer.getName() + " ?")
                                .setPositiveButton("Yes", dialogClickListener)
                                .setNegativeButton("No", dialogClickListener).show();
                    }
                });
            }

            RecyclerView recyclerView = view.findViewById(R.id.view_customer_recycler);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            final RecyclerViewCustomerAdapter adapter = new RecyclerViewCustomerAdapter(mContext, customer);
            recyclerView.setAdapter(adapter);
            return view;
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
            Customer customer = (Customer) ((View) object).getTag();
            int position = customers.indexOf(customer);
            if (position >= 0) {
                // The current data matches the data in this active fragment, so let it be as it is.
                return position;
            } else {
                // Returning POSITION_NONE means the current data does not match the data this fragment is showing right now.  Returning POSITION_NONE constant will force the fragment to redraw its view layout all over again and show new data.
                return POSITION_NONE;
            }
        }

    private void updateCustomersSelection(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    private void deleteCustomer(Customer customer) {
            Util.deleteObject(this, Util.CUSTOMER_REFERENCE, customer);
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public String createLogInfo() {
        return customer.getName();
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        updateCustomersSelection(customers);
    }
}