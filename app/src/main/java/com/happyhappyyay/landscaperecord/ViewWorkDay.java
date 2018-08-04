package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.CalendarView;
import android.widget.CalendarView.OnDateChangeListener;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewWorkDay extends AppCompatActivity {
    private CalendarView calendarView;
    private static AppDatabase db;
    private WorkDay workDay;
    private List<WorkDay> workDays;
    private TextView name;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerViewWorkDayAdapter adapter;
    private List<Customer> customers;
    private List<User> users;
    private RadioButton dayButton, weekButton, monthButton;
    private String calendarPosition = Util.retrieveStringCurrentDate();
    final static String CALENDAR_POSITION = "calendar position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_work_day);
        calendarView = findViewById(R.id.view_work_activity_calendar);
        db = AppDatabase.getAppDatabase(this);
        recyclerView = findViewById(R.id.view_work_day_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        dayButton = findViewById(R.id.view_work_day_day_button);
        weekButton = findViewById(R.id.view_work_day_week_button);
        monthButton = findViewById(R.id.view_work_day_month_button);
        recyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState != null) {
            calendarPosition = savedInstanceState.getString(CALENDAR_POSITION);
            calendarView.setDate(Util.convertStringDateToMilliseconds(calendarPosition));
        }
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView CalendarView, int year, int month, int dayOfMonth) {
                String monthString;
                String dayOfMonthString;

                if (month < 9) {
                    monthString = "0" + (month + 1);
                }
                else {
                    monthString = Integer.toString(month + 1);
                }
                if (dayOfMonth <= 9) {
                    dayOfMonthString = "0" + dayOfMonth;
                }
                else
                {
                    dayOfMonthString = Integer.toString(dayOfMonth);
                }
                String date = monthString + "/" + dayOfMonthString + "/" + year;
                workDay = null;
                workDays = null;
                findWorkDayByDate(date);
                calendarPosition = date;
            }
        });
        findWorkDayByDate(calendarPosition);
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CALENDAR_POSITION, calendarPosition);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    private void noWorkDayMessage() {
        Toast.makeText(getApplicationContext(), "No information available for this date", Toast.LENGTH_SHORT).show();
    }

    private void findWorkDayByDate(String date) {
        new AsyncTask<String, Void, Void>() {
            @Override
            protected Void doInBackground(String... sDate) {
                WorkDay tempWorkDay = db.workDayDao().findWorkDayByDate(sDate[0]);
                if (tempWorkDay != null) {
                    if (dayButton.isChecked()) {
                        workDay = tempWorkDay;
                    } else if (weekButton.isChecked()) {
                        List<WorkDay> tempWorkWeek = db.workDayDao().findWorkWeekByTime(tempWorkDay.getWeekInMilli());

                        if (tempWorkWeek != null) {
                            workDays = tempWorkWeek;
                        }
                    } else {
                        List<WorkDay> tempWorkMonth = db.workDayDao().findWorkMonthByTime(tempWorkDay.getMonthInMilli());

                        if (tempWorkMonth != null) {
                            workDays = tempWorkMonth;
                        }
                    }
                }

                users = db.userDao().getAllUsers();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                setupViewWorkDayAdapter();
            }
        }.execute(date);
    }

    private void setupViewWorkDayAdapter() {
        List<String> userWithHours = new ArrayList<>();
        List<String> customerWithServices = new ArrayList<>();
        List<Service> services = new ArrayList<>();

        if (workDay != null) {
            services = workDay.getServices();
            if (users != null) {
                userWithHours = createStringFromUserHourReferences(workDay);
            }
            if (services != null) {
                customerWithServices = convertCustomerServicesToString(services);
            }
        }
        else if (workDays != null) {
            for (WorkDay w: workDays) {
                services.addAll(w.getServices());
            }
            userWithHours = createStringFromUserHourReferences(workDays);
            if (!services.isEmpty()) {
                customerWithServices = convertCustomerServicesToString(services);
            }
        }
        else {
            noWorkDayMessage();
        }
        adapter = new RecyclerViewWorkDayAdapter(userWithHours, customerWithServices);
        recyclerView.setAdapter(adapter);
    }

    public List<String> createStringFromUserHourReferences(WorkDay workDay) {
        List<String> usersAndHours = new ArrayList<>();
        List<Integer> userReferences = workDay.getUserReference();
        List<Integer> userHours = workDay.getHours();

        for(int i = 0; i < workDay.getHours().size(); i++) {
            String userAndHours = users.get(userReferences.get(i)).getName() + " : " + userHours.get(i);
            usersAndHours.add(userAndHours);
        }

        return usersAndHours;
    }

    public List<String> createStringFromUserHourReferences(List<WorkDay> workDays) {
        List<String> usersAndHours = new ArrayList<>();
        List<Integer> userHours = new ArrayList<>();
        List<Integer> userReferences = new ArrayList<>();
        boolean init = false;
        for (WorkDay w: workDays) {
            if (init) {
                for (int i = 0; i < w.getHours().size(); i++) {
                    boolean userAlreadyExists = false;
                    for (int j = 0; j < userReferences.size(); j++) {
                        if (w.getUserReference().get(i).equals(userReferences.get(j))) {
                            userAlreadyExists = true;
                            int existingUserHours = userHours.get(j);
                            int additionalUserHours = w.getHours().get(i);
                            userHours.set(j, existingUserHours + additionalUserHours);
                        }
                    }
                    if (!userAlreadyExists) {
                        userReferences.add(w.getUserReference().get(i));
                        userHours.add(w.getHours().get(i));
                    }
                }
            } else {
                init = true;
                userReferences.addAll(w.getUserReference());
                userHours.addAll(w.getHours());
            }
        }

        for(int i = 0; i < userHours.size(); i++) {
            String userAndHours = users.get(userReferences.get(i)).getName() + " : " + userHours.get(i);
            usersAndHours.add(userAndHours);
        }
        return usersAndHours;
    }

    public List<String> convertCustomerServicesToString(List<Service> services) {
        List<String> customerServices = new ArrayList<>();
        for (Service s: services) {
            String serviceString = s.getServices();
            String serviceStringWithoutSeparators = "";
            int endServicePosition;
            int startServicePosition = 0;

            for (int i = 0; i < serviceString.length() - 2; i++) {
                if (serviceString.substring(i,i+3).equals("#*#")) {
                        endServicePosition = i;
                    serviceStringWithoutSeparators = serviceStringWithoutSeparators + serviceString.substring(startServicePosition, endServicePosition) + ", ";
                    startServicePosition = i+3;
                }
            }

            if (serviceStringWithoutSeparators.length() > 2) {
                serviceStringWithoutSeparators = serviceStringWithoutSeparators.substring(0, serviceStringWithoutSeparators.length()-2);
            }
            String customerService = s.convertEndTimeToDateString() + ": "  + s.getCustomerName() +
                    System.getProperty ("line.separator") + serviceStringWithoutSeparators;
            customerServices.add(customerService);
        }
        return customerServices;
    }
}


