package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class BillingViewPagerAdapter extends PagerAdapter {

    private static final String TAG = "MyPagerAdapter";
    private List<Customer> customers;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int monthSelection;

    public BillingViewPagerAdapter(Context context, List<Customer> customers, int monthSelection) {
        mContext = context;
        this.customers = customers;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.monthSelection = monthSelection;
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
            RecyclerView recyclerView = view.findViewById(R.id.billing_preview_recycler);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerServicePricingAdapter adapter = new RecyclerServicePricingAdapter(customers.get(position), mContext, monthSelection);
            recyclerView.setAdapter(adapter);
            // Add the newly created View to the ViewPager
            container.addView(view);
            Log.i(TAG, "instantiateItem() [position: " + position + "]" + " childCount:" + container.getChildCount());
            // Return the View
            return view;
        }

        private View retrieveView(ViewGroup container, int position) {
            // Inflate a new layout from our resources
            View view = mLayoutInflater.inflate(R.layout.billing_preview, container, false);
            // Retrieve a TextView from the inflated View, and update it's text
            TextView businessName = view.findViewById(R.id.billing_header_business);
            TextView customerName = view.findViewById(R.id.billing_header_name);
            TextView customerAddress = view.findViewById(R.id.billing_header_address);
            TextView companyName = view.findViewById(R.id.billing_header_company_name);
            companyName.setText(Util.retrieveCompanyName(mContext));
            TextView dateText = view.findViewById(R.id.billing_header_date);
            dateText.setText(Util.retrieveStringCurrentDate());
            TextView personalMessage = view.findViewById(R.id.billing_preview_personal_message);
            personalMessage.setText(Util.retrievePersonalMessage(mContext));
            Customer customer = customers.get(position);
            String name = customer.getFullName();
            if(customer.getCustomerBusiness() != null) {
                businessName.setVisibility(View.VISIBLE);
                name = "ATTN: " + name;
            }
            customerName.setText(name);
            customerAddress.setText(customer.getCustomerAddress());
            Button button = view.findViewById(R.id.billing_preview_confirm);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(mContext, "BUTTON", Toast.LENGTH_SHORT).show();
                }
            });
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

    public void updateCustomersSelection(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    public void updateMonthSelection(int monthSelection) {
        this.monthSelection = monthSelection;
        notifyDataSetChanged();
    }
    }