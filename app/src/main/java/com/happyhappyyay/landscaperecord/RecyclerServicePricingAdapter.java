package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class RecyclerServicePricingAdapter extends Adapter implements DatabaseAccess<Customer> {
    protected List<Object> objects;
    protected Context context;
    private final int CUSTOMER = 0, SERVICE = 1;

    public RecyclerServicePricingAdapter(List<Customer> customers, Context context) {
        String TAG = "Initialize";
        Log.d(TAG, "start adapter");
        objects = createObjectList(customers);
        this.context = context;
    }

    private List<Object> createObjectList(List<Customer> customers) {
        List<Object> objects = new ArrayList<>();
        for(int i = 0; i < customers.size(); i++) {
            objects.add(customers.get(i));
            List<Service> services = customers.get(i).getCustomerServices();
            for(int j = 0; j < services.size(); j++) {
                if(!services.get(j).isPriced()) {
                    objects.add(services.get(j));
                }
            }
        }
        return objects;
    }

    private void updateObjectList() {
        List<Object> newObjects = new ArrayList<>();
        for(int i = 0; i < objects.size(); i++) {
            if (objects.get(i) instanceof Customer) {
                Customer customer = (Customer) objects.get(i);
                List<Service> services = customer.getCustomerServices();
                boolean customerWithUnpricedServices = false;
                for(int j = 0; j < services.size(); j++) {
                    if(!services.get(j).isPriced()) {
                        if(!customerWithUnpricedServices) {
                            newObjects.add(customer);
                        }
                        customerWithUnpricedServices = true;
                        newObjects.add(services.get(j));
                    }
                }
            }
        }
        objects = newObjects;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        switch (viewType) {
            case CUSTOMER:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.services_pricing_recycler, parent, false);
                return new RecyclerServicePricingAdapter.CustomerViewHolder(view);
            case SERVICE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_pricing_recycler_item, parent, false);
                return new RecyclerServicePricingAdapter.ServiceViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.service_pricing_recycler_item, parent, false);
                return new RecyclerServicePricingAdapter.ServiceViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case CUSTOMER:
                CustomerViewHolder vh1 = (CustomerViewHolder) holder;
                vh1.bindView(position);
                break;
            case SERVICE:
                ServiceViewHolder vh2 = (ServiceViewHolder) holder;
                vh2.bindView(position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return objects.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        if (objects.get(position) instanceof Customer) {
            return CUSTOMER;
        } else if (objects.get(position) instanceof Service) {
            return SERVICE;
        }
        return -1;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String createLogInfo() {
        return "PRICE SET";
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        updateObjectList();
        notifyDataSetChanged();
    }

    private class CustomerViewHolder extends RecyclerView.ViewHolder {
        TextView customerName;




        public CustomerViewHolder(View view) {
            super(view);
            customerName = view.findViewById(R.id.service_pricing_recycler_text);

        }

        private List<String> sortStringsByDate(List<String> strings) {
            Collections.sort(strings, new Comparator<String>() {
                public int compare(String service1, String service2) {
                    if (Util.convertStringDateToMilliseconds(service1.substring(0,9)) > Util.convertStringDateToMilliseconds(service2.substring(0,9))) return -1;
                    if (Util.convertStringDateToMilliseconds(service1.substring(0,9)) < Util.convertStringDateToMilliseconds(service2.substring(0,9))) return 1;
                    return 0;
                }});
            return strings;
        }

        public void bindView(int position) {
            Customer customer = Util.CUSTOMER_REFERENCE;
            if(objects.get(position) instanceof Customer) {
                customer = (Customer) objects.get(position);
            }
            customerName.setText(customer.getName());
        }
    }

    private class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceText;
        EditText priceText;
        Button priceButton;


        public ServiceViewHolder(View view) {
            super(view);
            serviceText = view.findViewById(R.id.service_pricing_recycler_item_text);
            priceText = view.findViewById(R.id.service_pricing_recycler_item_price_text);
            priceButton = view.findViewById(R.id.service_pricing_recycler_item_set_button);

        }

        public void bindView(final int position) {
            final Service service = (Service) objects.get(position);
            if (service != null) {
                final Customer customer = findCustomerFromObjectsList(position);
                if (customer != null) {
                    String servicesWithDate = Util.convertLongToStringDate(service.getEndTime()) + service.getServices();
                    serviceText.setText(servicesWithDate);
                    Double amount = customer.getPayment().checkServiceForPrice(service.getServices());
                    String price = Double.toString(amount);
                    priceText.setText(price);
                    priceButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Double servicePrice = Double.parseDouble(priceText.getText().toString());
                            if (Double.parseDouble(priceText.getText().toString()) > 0) {
                                customer.getPayment().addServicePrice(service.getServices(), servicePrice);
                                service.setPriced(true);
                                Util.updateObject(RecyclerServicePricingAdapter.this, Util.CUSTOMER_REFERENCE, customer);
                            }
                        }
                    });
                }
            }
        }
        private Customer findCustomerFromObjectsList(int position) {
            for (int i = position; i >= 0; i--) {
                if (objects.get(i) instanceof Customer) {
                    return (Customer) objects.get(i);
                }
            }
            return null;
        }
    }
}
