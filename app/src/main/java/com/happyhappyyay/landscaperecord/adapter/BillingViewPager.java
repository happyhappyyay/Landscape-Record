package com.happyhappyyay.landscaperecord.adapter;

import android.content.Context;
import android.content.DialogInterface;
import androidx.annotation.NonNull;
import androidx.viewpager.widget.PagerAdapter;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.utility.CreateDocument;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class BillingViewPager extends PagerAdapter implements DatabaseAccess<Customer> {

    private static final String TAG = "MyPagerAdapter";
    private List<Customer> customers;
    private Context mContext;
    private LayoutInflater mLayoutInflater;
    private int monthSelection;
    private AppCompatActivity activity;

    public BillingViewPager(AppCompatActivity activity, List<Customer> customers, int monthSelection) {
        mContext = activity.getApplicationContext();
        this.customers = customers;
        this.activity = activity;
        mLayoutInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.monthSelection = monthSelection;
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
            View view = mLayoutInflater.inflate(R.layout.billing_preview, container, false);
            TextView businessName = view.findViewById(R.id.billing_header_business);
            TextView customerName = view.findViewById(R.id.billing_header_name);
            TextView customerAddress = view.findViewById(R.id.billing_header_address);
            TextView companyName = view.findViewById(R.id.billing_header_company_name);
            companyName.setText(Util.retrieveCompanyName(mContext));
            TextView dateText = view.findViewById(R.id.billing_header_date);
            dateText.setText(Util.retrieveStringCurrentDate());
            TextView personalMessage = view.findViewById(R.id.billing_preview_personal_message);
            personalMessage.setText(Util.retrievePersonalMessage(mContext));
            final Customer customer = customers.get(position);
            String name = customer.createFullName();
            if(customer.getBusiness() != null) {
                businessName.setText(customer.getBusiness());
                businessName.setVisibility(View.VISIBLE);
                name = mContext.getString(R.string.billing_view_pager_attention) + name;
            }
            customerName.setText(name);
            customerAddress.setText(customer.getAddress());
            ImageView backArrow = view.findViewById(R.id.billing_preview_back_arrow);
            ImageView forwardArrow = view.findViewById(R.id.billing_preview_forward_arrow);
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

            RecyclerView recyclerView = view.findViewById(R.id.billing_preview_recycler);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(mContext);
            recyclerView.setLayoutManager(layoutManager);
            final RecyclerServicePricing adapter = new RecyclerServicePricing(customers.get(position), activity, monthSelection);
            recyclerView.setAdapter(adapter);
            Button button = view.findViewById(R.id.billing_preview_confirm);
            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final List<Service> services = adapter.getServices();
                    if(customer.hasUnfinishedServicesForMonth(monthSelection)) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Util.verifyStoragePermissions(activity);
                                        CreateDocument createDocument = new CreateDocument();
                                        try {
                                            createDocument.createADocument(mContext, customer, services);
                                            List<Service> services = adapter.getPricedServices();
                                            for(Service s: services) {
                                                s.setPriced(true);
                                            }
                                            customer.updateServices(services);
                                            customers.remove(position);
                                            updateCustomer(customer);
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(mContext.getString(R.string.billing_view_pager_in_progress))
                                .setPositiveButton(mContext.getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(mContext.getString(R.string.no), dialogClickListener).show();
                    }
                    else if(checkForZeroPricedItems(adapter.getPricedServices())) {
                        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                switch (which) {
                                    case DialogInterface.BUTTON_POSITIVE:
                                        Util.verifyStoragePermissions(activity);
                                        CreateDocument createDocument = new CreateDocument();
                                        try {
                                            createDocument.createADocument(mContext, customer, services);
                                            List<Service> services = adapter.getPricedServices();
                                            for(Service s: services) {
                                                s.setPriced(true);
                                            }
                                            customer.updateServices(services);
                                            customers.remove(position);
                                            updateCustomer(customer);
                                        }
                                        catch (Exception e) {
                                            e.printStackTrace();
                                        }
                                        break;

                                    case DialogInterface.BUTTON_NEGATIVE:

                                        break;
                                }
                            }
                        };

                        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
                        builder.setMessage(mContext.getString(R.string.billing_view_pager_zeros))
                                .setPositiveButton(mContext.getString(R.string.yes), dialogClickListener)
                                .setNegativeButton(mContext.getString(R.string.no), dialogClickListener).show();
                    }
                    else {
                        Util.verifyStoragePermissions(activity);
                        CreateDocument createDocument = new CreateDocument();
                        try {
                            createDocument.createADocument(mContext, customer, services);
                            for(Service s: services) {
                                s.setPriced(true);
                            }
                            customers.remove(position);
                            updateCustomer(customer);
                        }
                        catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            });
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

    private boolean checkForZeroPricedItems(List<Service> services){
        for(Service s: services){
            if(s.getPrice() == 0){
                return true;
            }
        }
        return false;
    }

    public void updateCustomersSelection(List<Customer> customers) {
        this.customers = customers;
        notifyDataSetChanged();
    }

    public void updateMonthAndCustomerSelection(int monthSelection, List<Customer> customers) {
        this.monthSelection = monthSelection;
        this.customers = customers;
        notifyDataSetChanged();
    }

    private void updateCustomer(Customer customer) {
            Util.updateObject(this, Util.CUSTOMER_REFERENCE, customer);
    }

    @Override
    public Context getContext() {
        return mContext;
    }

    @Override
    public String createLogInfo() {
        return null;
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        updateCustomersSelection(customers);
    }
}