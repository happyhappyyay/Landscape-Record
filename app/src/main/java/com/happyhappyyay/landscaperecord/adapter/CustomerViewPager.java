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
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;
import java.util.Locale;

public class CustomerViewPager extends PagerAdapter implements DatabaseAccess<Customer> {

    private static final String TAG = "MyPagerAdapter";
    private List<Customer> customers;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private Customer customer;

    public CustomerViewPager(Context context, List<Customer> customers) {
        mContext = context;
        this.customers = customers;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

        @Override
        public int getCount() {
            return customers.size();
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
            View view = mLayoutInflater.inflate(R.layout.view_customer, container, false);
            final Customer customer = customers.get(position);
            this.customer = customer;
            if(customer.getBusiness() != null) {
                TextView businessName = view.findViewById(R.id.view_customer_business);
                businessName.setText(customer.getBusiness());
            }
            TextView customerTitle = view.findViewById(R.id.view_customer_name);
            TextView customerName = view.findViewById(R.id.view_customer_full_name);
            TextView customerAddress = view.findViewById(R.id.view_customer_full_address);
            TextView dayText = view.findViewById(R.id.view_customer_day);
            TextView phoneText = view.findViewById(R.id.view_customer_phone_number);
            TextView emailText = view.findViewById(R.id.view_customer_email);
            TextView mileageText = view.findViewById(R.id.view_customer_mileage);
            customerTitle.setText(customer.getName());
            dayText.setText(customer.getDay());
            if(customer.getPhone() != null) {
                phoneText.setText(customer.getPhone());
            }
            if(customer.getEmail() != null) {
                emailText.setText(customer.getEmail());
            }
            if(customer.getMi() != null) {
                if(customer.getMi() > 0) {
                    mileageText.setText(String.format(Locale.US, "%.2f", customer.getMi()));
                }
            }
            customerName.setText(customer.createFullName());
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
                        intent.putExtra("CUSTOMER_ID", customer.getId());
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
                        builder.setMessage(mContext.getString(R.string.customer_view_pager_delete) + " " +customer.getName() + " ?")
                                .setPositiveButton(mContext.getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(mContext.getString(R.string.no), dialogClickListener).show();
                    }
                });
            }

            RecyclerView recyclerView = view.findViewById(R.id.view_customer_recycler);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            final RecyclerViewCustomer adapter = new RecyclerViewCustomer(mContext, customer);
            recyclerView.setAdapter(adapter);
            return view;
        }

        @Override
        public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
            container.removeView((View) object);
            Log.i(TAG, "destroyItem() [position: " + position + "]" + " childCount:" + container.getChildCount());
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            Customer customer = (Customer) ((View) object).getTag();
            int position = customers.indexOf(customer);
            if (position >= 0) {
                return position;
            } else {
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