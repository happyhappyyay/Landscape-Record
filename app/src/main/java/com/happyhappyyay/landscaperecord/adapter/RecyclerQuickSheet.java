package com.happyhappyyay.landscaperecord.adapter;

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

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.activity.TimeReporting;
import com.happyhappyyay.landscaperecord.enums.LogActivityAction;
import com.happyhappyyay.landscaperecord.enums.LogActivityType;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RecyclerQuickSheet extends RecyclerView.Adapter implements MultiDatabaseAccess<Customer> {
    private String startDateString;
    private String endDateString;
    private List<Customer> customers;
    private Customer customer;
    private Context context;
    private Service service;

    public RecyclerQuickSheet(List<Customer> customers, Context context, String startDateString,
                              String endDateString) {
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
    public void createCustomLog() {
        String userName = Authentication.getAuthentication().getUser().getName();
        LogActivity log = new LogActivity(userName, customer.getName(), LogActivityAction.ADD.ordinal(),
                LogActivityType.SERVICES.ordinal());
        if(Util.hasOnlineDatabaseEnabledAndValid(context)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(context);
                Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
            } catch (Exception e) {
                e.printStackTrace();

            }
        }
        AppDatabase db = AppDatabase.getAppDatabase(context);
        Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
    }

    private long retrieveStartTime(long startTime) {
        return startTimeEqualsSameDay(startTime)? Util.retrieveLongCurrentDate():startTime;
    }

    private boolean startTimeEqualsSameDay(long startTime) {
        return startTime == Util.convertStringDateToMilliseconds(Util.retrieveStringCurrentDate());
    }

    private long retrieveEndTime(long endTime) {
        return endTimeEqualsSameDay(endTime)? Util.retrieveLongCurrentDate():endTime;
    }

    private boolean endTimeEqualsSameDay(long endTime) {
        return endTime == Util.convertStringDateToMilliseconds(Util.retrieveStringCurrentDate());
    }

    private boolean startAndEndDateMatch(long startTime, long endTime) {
        return startTime == endTime;
    }

    @Override
    public Context getContext() {
        return context;
    }

    private void updateCustomer() {
        Util.enactMultipleDatabaseOperations(this);
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
        if (Util.hasOnlineDatabaseEnabledAndValid(context)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(context);
                databaseAccessMethod(db);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AppDatabase db = AppDatabase.getAppDatabase(context);
        databaseAccessMethod(db);
    }

    private void databaseAccessMethod(DatabaseOperator db) {
        WorkDay workDay;
        Util.CUSTOMER_REFERENCE.updateClassInstanceFromDatabase(db, customer);
        if (service != null) {
            if(service.getEndTime() > 0){
                LogActivity log = new LogActivity(Authentication.getAuthentication().getUser().getName(),
                        customer.getName(), LogActivityAction.COMPLETED.ordinal(), LogActivityType.SERVICES.ordinal());
                log.setObjId(customer.getId());
                Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
            }
            WorkDay tempWorkDay = Util.WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseString(db, endDateString);
            if (tempWorkDay != null) {
                workDay = tempWorkDay;
                workDay.addServices(service);
                Util.WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(db, workDay);
            } else {
                workDay = new WorkDay(endDateString);
                workDay.addServices(service);
                Util.WORK_DAY_REFERENCE.insertClassInstanceFromDatabase(db, workDay);
                LogActivity log = new LogActivity(Authentication.getAuthentication().getUser().getName(),
                        endDateString, LogActivityAction.ADD.ordinal(), LogActivityType.WORKDAY.ordinal());
                log.setId(workDay.getId());
                Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
            }
        }
    }

    private class ListViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        private Button jobActionButton;
        private CheckBox checkBox1, checkBox2, checkBox3;
        List<CheckBox> checkBoxes;


        private ListViewHolder(View view) {
            super(view);
            name = view.findViewById(R.id.quick_sheet_address_text);
            jobActionButton = view.findViewById(R.id.quick_sheet_button);
            jobActionButton.setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (Util.checkDateFormat(endDateString) & Util.checkDateFormat(startDateString)) {
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
                                    startTime = retrieveStartTime(startDate.getTime());
                                    endTime = retrieveEndTime(endDate.getTime());
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (endTime < startTime) {
                                    flagDifferentEndTime = true;
                                }
                                String servicesString = updateCheckBoxes();
                                customer = customers.get(getAdapterPosition());
                                List<Service> services = customer.getServices();
                                int serviceListPosition = 0;
//                            check for service pause and date then set inputs to services string
                                for (int i = 0; i < services.size(); i++) {
                                    Service s = services.get(i);
                                    if (s.convertStartTimeToDateString().equals(startDateString)) {
                                        tempService = s;
                                        if (!s.checkCompleted()) {
                                            serviceListPosition = i;
                                        }
                                    }
                                }
//                            create new service if could not find existing service
                                if (tempService == null) {
                                    jobActionButton.setText(context.getString(R.string.quick_sheet_item_finish));
                                    tempService = new Service();
                                    tempService.setStartTime(startTime);
                                    tempService.setServices(servicesString);
                                    tempService.setCustomerName(customer.getName());
                                    tempService.setUsername(Authentication.getAuthentication().getUser().getName());
                                    tempService.setMi(customer.getMi() != null?
                                            customer.getMi():0);
                                    customer.addService(tempService);
                                    updateCustomer();
                                }
//                            otherwise update existing service
                                else {
                                    if (!jobActionButton.getText().toString().equals("✓")) {
                                        if (!flagDifferentEndTime) {
                                            jobActionButton.setText("✓");
                                            String tempServiceString = tempService.getServices();
                                            for (CheckBox c : checkBoxes) {
                                                String checkBoxText = c.getText().toString();
                                                int startIndex = tempServiceString.indexOf(checkBoxText);
                                                if (startIndex != -1) {
                                                    int endIndex = startIndex + checkBoxText.length() + Util.DELIMITER.length();
                                                    String tempServicePreString = tempServiceString.substring(0, startIndex);
                                                    String tempServicePostString = tempServiceString.substring(endIndex);
                                                    tempServiceString = tempServicePreString + tempServicePostString;
                                                }
                                            }
                                            tempService.setServices(servicesString + tempServiceString);
                                            tempService.setEndTime(endTime);
                                            tempService.setUsername(Authentication.getAuthentication().getUser().getName());
                                            tempService.setMi(customer.getMi() != null?
                                                    customer.getMi():0);
                                            if(startAndEndDateMatch(startTime, endTime)) {
                                                tempService.setManHours((tempService.getEndTime()
                                                        - tempService.getStartTime()) / TimeReporting.MILLISECONDS_TO_HOURS);
                                            }
                                            customer.updateService(tempService, serviceListPosition);
                                            service = tempService;
                                            updateCustomer();
                                        } else {
                                            Toast.makeText(context,
                                                    context.getString(R.string.quick_sheet_date_error),
                                                    Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                }
                            } else {
                                Toast.makeText(context,
                                        context.getString(R.string.incorrect_date_format),
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
            );
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
            String quickSheetItem = sharedPref.getString(Util.retrieveStringFromResources(R.string.pref_key_quick_sheet_item1,
                    context), context.getString(R.string.lawn_services_cut));
            String quickSheetItem1 = sharedPref.getString(Util.retrieveStringFromResources(R.string.pref_key_quick_sheet_item2,
                    context), context.getString(R.string.lawn_services_spray_grass));
            String quickSheetItem2 = sharedPref.getString(Util.retrieveStringFromResources(R.string.pref_key_quick_sheet_item3,
                    context), context.getString(R.string.lawn_services_prune));
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
            name.setText(customer.getAddress());
            boolean existingService = false;
            if (customer.getServices() != null) {
                List<Service> services = customer.getServices();
                for (Service s : services) {
                    if (s.convertStartTimeToDateString().equals(startDateString)) {
                        existingService = true;
                        if (s.checkCompleted()) {
                            jobActionButton.setText("✓");
                        } else {
                            jobActionButton.setText(context.getString(R.string.quick_sheet_item_finish));
                        }

                        for (CheckBox c : checkBoxes) {
                            if (s.getServices().contains(c.getText().toString())) {
                                c.setChecked(true);
                            } else {
                                c.setChecked(false);
                            }
                        }
                    }
                    if (!existingService) {
                        jobActionButton.setText(context.getString(R.string.quick_sheet_item_start));
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
}