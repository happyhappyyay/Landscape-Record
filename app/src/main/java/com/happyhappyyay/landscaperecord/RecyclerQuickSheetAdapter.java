package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerQuickSheetAdapter extends RecyclerView.Adapter implements MultiDatabaseAccess<Customer> {
    private String startDateString;
    private String endDateString;
    private List<Customer> customers;
    private Customer customer;
    private Context context;
    private Service service;

    public RecyclerQuickSheetAdapter (List<Customer> customers, Context context, String startDateString, String endDateString) {
        this.customers = customers;
        if (this.customers == null) {
            this.customers = new ArrayList<>();
        }
        this.context = context;
        this.startDateString = startDateString;
        this.endDateString = endDateString;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.quick_sheet_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        return customers.size();
    }

    public void setStartDateString(String startDateString) {
        this.startDateString = startDateString;
    }

    public void setEndDateString(String endDateString) {
        this.endDateString = endDateString;
    }

    public void setCustomers(List<Customer> customers) {
        this.customers = customers;
    }

    @Override
    public Context getContext() {
        return context;
    }

    @Override
    public String createLogInfo() {
        return null;
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {

    }

    @Override
    public void accessDatabaseMultipleTimes() {
        try {
            OnlineDatabase db = OnlineDatabase.getOnlineDatabase(context);
            databaseAccessMethod(db);

        } catch(Exception e) {
            AppDatabase db = AppDatabase.getAppDatabase(context);
            databaseAccessMethod(db);
        }

    }

    private void databaseAccessMethod(DatabaseOperator db) {
        WorkDay workDay;
        Util.CUSTOMER_REFERENCE.updateClassInstanceFromDatabase(db, customer);
        if(service != null) {
            WorkDay tempWorkDay = Util.WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseString(db, endDateString);
            if (tempWorkDay != null) {
                workDay = tempWorkDay;
                workDay.addServices(service);
                Util.WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(db, workDay);
            } else {
                workDay = new WorkDay(endDateString);
                workDay.addServices(service);
                Util.WORK_DAY_REFERENCE.insertClassInstanceFromDatabase(db, workDay);
            }
        }
    }

    @Override
    public void createCustomLog() {
        String userName = Authentication.getAuthentication().getUser().getName();
        LogActivity log = new LogActivity(userName, customer.getName(), LogActivityAction.ADD.ordinal(), LogActivityType.SERVICES.ordinal());
        try {
            OnlineDatabase db = OnlineDatabase.getOnlineDatabase(context);
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
        } catch (Exception e){
            AppDatabase db = AppDatabase.getAppDatabase(context);
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);

        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        private Button jobActionButton;
        private CheckBox checkBox1, checkBox2, checkBox3;
        List<CheckBox> checkBoxes;



        public ListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.quick_sheet_address_text);
            jobActionButton = view.findViewById(R.id.quick_sheet_button);
            jobActionButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            service = null;
                            boolean flagDifferentEndTime = false;
                            long startTime = 0;
                            long endTime = 0;
                            Service tempService = null;
//                            set entered/automatic dates into start and end dates
                                SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
                                try {
                                    Date startDate = dateFormat.parse(startDateString);
                                    Date endDate = dateFormat.parse(endDateString);
                                    startTime = startDate.getTime();
                                    endTime = endDate.getTime();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (endTime < startTime) {
                                    flagDifferentEndTime = true;
                                }
                            String servicesString = updateCheckBoxes();
                            customer = customers.get(getAdapterPosition());
                            List<Service> services = customer.getCustomerServices();
                            int serviceListPosition = 0;
//                            check for service pause and date then set inputs to services string
                            for (int i = 0; i < services.size(); i++) {
                                Service s = services.get(i);
                                if (s.convertStartTimeToDateString().equals(startDateString)) {
                                    tempService = s;
                                    if (s.isPause()) {
                                        serviceListPosition = i;
                                    }
                                }
                            }
//                            create new service if could not find existing service
                            if (tempService == null) {
                                jobActionButton.setText("Finish");
                                tempService = new Service();
                                tempService.setPause(true);
                                tempService.setStartTime(startTime);
                                tempService.setServices(servicesString);
                                tempService.setCustomerName(customer.getName());
                                customer.addService(tempService);
                                updateCustomer();
                            }
//                            otherwise update existing service
                            else {
                                if (!jobActionButton.getText().toString().equals("✓")) {
                                    if (!flagDifferentEndTime) {
                                        jobActionButton.setText("✓");
                                        String tempServiceString = tempService.getServices();
                                        for(CheckBox c: checkBoxes) {
                                            String checkBoxText = c.getText().toString();
                                            int startIndex = tempServiceString.indexOf(checkBoxText);
                                            if(startIndex != -1) {
                                                int endIndex = startIndex + checkBoxText.length() + Util.DELIMITER.length();
                                                String tempServicePreString = tempServiceString.substring(0, startIndex);
                                                String tempServicePostString = tempServiceString.substring(endIndex, tempServiceString.length());
                                                tempServiceString = tempServicePreString + tempServicePostString;
                                            }
                                        }
                                        tempService.setServices(servicesString + tempServiceString);
                                        tempService.setEndTime(endTime);
                                        tempService.setManHours((tempService.getStartTime() - tempService.getEndTime()) / TimeReporting.MILLISECONDS_TO_HOURS );
                                        tempService.setPause(false);
                                        customer.updateService(tempService, serviceListPosition);
                                        service = tempService;
                                        updateCustomer();
                                    } else {
                                        Toast.makeText(context,
                                                "There was an error with the start - end date " +
                                                        "combination. Please check the dates are correct.",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }
                        }
                    }
            );
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String quickSheetItem = sharedPref.getString("pref_key_quick_sheet_item1", "1");
            String quickSheetItem1 = sharedPref.getString("pref_key_quick_sheet_item2", "2");
            String quickSheetItem2 = sharedPref.getString("pref_key_quick_sheet_item3", "3");
            checkBox1 = view.findViewById(R.id.quick_sheet_check_box1);
            checkBox1.setText(quickSheetItem);
            checkBox2 = view.findViewById(R.id.quick_sheet_check_box2);
            checkBox2.setText(quickSheetItem1);
            checkBox3 = view.findViewById(R.id.quick_sheet_check_box3);
            checkBox3.setText(quickSheetItem2);
            checkBoxes = new ArrayList<>(Arrays.asList(checkBox1, checkBox2, checkBox3));
        }

        public void bindView(int position) {
            Customer customer = customers.get(position);
            name.setText(customer.getCustomerAddress());
            boolean existingService = false;
            if (customer.getCustomerServices() != null) {
                List<Service> services = customer.getCustomerServices();
                for (Service s : services) {
                    if (s.convertStartTimeToDateString().equals(startDateString)) {
                        existingService = true;
                        if (s.isPause()) {
                            jobActionButton.setText("Finish");
                        }
                        else {
                            jobActionButton.setText("✓");
                        }

                        for (CheckBox c : checkBoxes) {
                            if (s.getServices().contains(c.getText().toString())) {
                                c.setChecked(true);
                            }
                            else {
                                c.setChecked(false);
                            }
                        }
                    }
                    if (!existingService) {
                        jobActionButton.setText("Start");
                        for (CheckBox c : checkBoxes) {
                            c.setChecked(false);
                        }
                    }
                }
            }
        }
        private String updateCheckBoxes() {
            StringBuilder stringBuilder = new StringBuilder();
            for (CheckBox c : checkBoxes) {
                if (c.isChecked()) {
                    String checkBoxTextForService = c.getText().toString() + Util.DELIMITER;
                    stringBuilder.append(checkBoxTextForService);
                }
            }
            return stringBuilder.toString();
        }
    }



    private void updateCustomer() {
        Util.enactMultipleDatabaseOperations(this);
    }
}
