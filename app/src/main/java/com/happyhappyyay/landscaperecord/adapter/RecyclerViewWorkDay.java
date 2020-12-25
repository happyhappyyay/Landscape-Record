package com.happyhappyyay.landscaperecord.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.RecyclerView.Adapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class RecyclerViewWorkDay extends Adapter {
    private List<WorkDay> workDays;
    private List<String> strings;
    private final String USER_HOURS= "User Hours";
    private final String SERVICES = "Customer Services";
    private final String PAYMENTS = "Customer Payments";
    private final String EXPENSES = "User Expenses";


    public RecyclerViewWorkDay(List<WorkDay> workDays) {
        this.workDays = workDays;
        strings = createStringsList();
    }

    private Map<String,Integer> combineMaps(Map<String,Integer> collection, Map<String,Integer> addition){
        Set< Map.Entry<String, Integer> > mapSet = addition.entrySet();

        for (Map.Entry< String, Integer> mapEntry:mapSet)
        {
            String userReference = mapEntry.getKey();
            if(collection.containsKey(userReference)) {
                collection.put(userReference,collection.get(userReference) + mapEntry.getValue());
            }
            else {
                collection.put(userReference, mapEntry.getValue());
            }
        }
        return collection;
    }

    private List<String> translateFromMapToList(Map<String,Integer> collection){
        List<String> strings = new ArrayList<>();
        Set< Map.Entry<String, Integer> > mapSet = collection.entrySet();
        int count = 0;

        for (Map.Entry< String, Integer> mapEntry:mapSet)
        {
            String priceEntry = mapEntry.getKey() + " : " + mapEntry.getValue();
            strings.add(count, priceEntry);
            count++;
        }
        return strings;
    }

    private List<String> createStringsList(){
        List<String> temp = new ArrayList<>();
        Map<String,Integer> hours = new HashMap<>();
        List<String> services = new ArrayList<>();
        List<String> payments = new ArrayList<>();
        List<String> expenses = new ArrayList<>();
        for (WorkDay w: workDays) {
            if(hours.size() > 0) {
                Map<String, Integer> tempHours = w.getUserHours();
                hours = combineMaps(hours, tempHours);
            }
            else {
                hours.putAll(w.getUserHours());
            }
            services.addAll(removeCustomerServicesStopCharacters(w.retrieveServicesAsString()));
            payments.addAll(w.retrievePaymentsAsString());
            expenses.addAll(w.retrievePaymentsAsString());
        }
        if(hours.size() > 0) {
            temp.add(USER_HOURS);
            temp.addAll(translateFromMapToList(hours));
        }
        if(services.size() > 0){
            temp.add(SERVICES);
            temp.addAll(services);
        }
        if(payments.size() > 0) {
            temp.add(PAYMENTS);
            temp.addAll(payments);
        }
        if(expenses.size() > 0) {
            temp.add(EXPENSES);
            temp.addAll(expenses);
        }
        return temp;
    }

    public List<String> removeCustomerServicesStopCharacters(List<String> services) {
        List<String> customerServices = new ArrayList<>();
        for (String s: services) {
            String serviceStringWithoutSeparators;
            StringBuilder sb = new StringBuilder();
            int endServicePosition;
            int startServicePosition = 0;

            for (int i = 0; i < s.length(); i++) {
                if (s.substring(i,i+1).equals(Util.DELIMITER)) {
                    endServicePosition = i;
                    String tempString = s.substring(startServicePosition, endServicePosition) + ", ";
                    sb.append(tempString);
                    startServicePosition = i+1;
                }
            }
            serviceStringWithoutSeparators = sb.toString();
            if (serviceStringWithoutSeparators.length() > 2) {
                serviceStringWithoutSeparators = serviceStringWithoutSeparators.substring(0, serviceStringWithoutSeparators.length()-2);
            }
            customerServices.add(serviceStringWithoutSeparators);
        }
        return customerServices;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view;
        final int TITLE = 0;
        final int ITEM = 1;
        switch (viewType) {
            case TITLE:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_customer_title_item, parent, false);
                return new RecyclerViewWorkDay.TitleViewHolder(view);
            case ITEM:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_work_day_item, parent, false);
                return new RecyclerViewWorkDay.ListViewHolder(view);
            default:
                view = LayoutInflater.from(parent.getContext()).inflate(R.layout.view_work_day_item, parent, false);
                return new RecyclerViewWorkDay.ListViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final int TITlE = 0;
        final int ITEM = 1;
        switch (holder.getItemViewType()) {
            case TITlE:
                TitleViewHolder vh1 = (TitleViewHolder) holder;
                vh1.bindView(position);
                break;
            case ITEM:
                ListViewHolder vh2 = (ListViewHolder) holder;
                vh2.bindView(position);
                break;
            default:
                break;
        }
    }

    @Override
    public int getItemCount() {
        return strings.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        final int TITLE = 0;
        final int ITEM = 1;
        if(strings.get(position).equals(USER_HOURS)|
                strings.get(position).equals(PAYMENTS)|
                strings.get(position).equals(SERVICES)|
                strings.get(position).equals(EXPENSES)){
            return TITLE;
        }
        else {
            return ITEM;
        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        private TextView text;


        private ListViewHolder(View view) {
            super(view);
            text = view.findViewById(R.id.view_work_day_item_item);
        }

        public void bindView(int position) {
            text.setText(strings.get(position));
        }
    }

    private class TitleViewHolder extends RecyclerView.ViewHolder {
        TextView title;

        private TitleViewHolder(View view) {
            super(view);
            title = view.findViewById(R.id.view_customer_title_title);
        }

        public void bindView(int position) {
            title.setText(strings.get(position));
        }
    }
}
