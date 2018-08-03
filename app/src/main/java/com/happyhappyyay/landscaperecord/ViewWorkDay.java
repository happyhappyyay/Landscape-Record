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
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewWorkDay extends AppCompatActivity {
    private CalendarView calendarView;
    private static AppDatabase db;
    private WorkDay workDay;
    private TextView name;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager layoutManager;
    private RecyclerViewWorkDayAdapter adapter;
    private List<Customer> customers;
    private List<User> users;
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
        recyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
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
                String TAG = "Gadriel";
                Log.d(TAG, date + " Gadriel");
                workDay = null;
                findWorkDayByDate(date);
                calendarPosition = date;
            }
        });
        findWorkDayByDate(calendarPosition);
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
                if (tempWorkDay != null) { workDay = tempWorkDay; }
                users = db.userDao().getAllUsers();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                List<String> userWithHours = new ArrayList<>();
                List<String> customerWithServices = new ArrayList<>();
                if (workDay != null) {
                    List<Service> services = workDay.getServices();
                    if (users != null) {
                        int[][] userHour = workDay.getUserHourReference();
                        userWithHours = checkArrayAgainstUserHours(userHour);
                    }
                    if (services != null) {
                        customerWithServices = convertCustomerServicesToString(services);
                    }
                }
                else {
                    noWorkDayMessage();
                }
                adapter = new RecyclerViewWorkDayAdapter(userWithHours, customerWithServices);
                recyclerView.setAdapter(adapter);
            }
        }.execute(date);
    }

    private List<String> checkArrayAgainstUserHours(int[][] userHour) {
        List<String> usersAndHours = new ArrayList<>();
        for (int i = 1; i < userHour.length;i++) {
            for (int j = 0; j < userHour[i].length; j++) {
                String userAndHours = users.get(userHour[0][j]).getName() + " : " + userHour[1][j];
                usersAndHours.add(userAndHours);
            }
        }
        return usersAndHours;
    }

    public List<String> convertCustomerServicesToString(List<Service> services) {
        List<String> customerServices = new ArrayList<>();
        for (Service s: services) {
            String serviceModified = s.getServices();
            String tempServiceModified = "";
            int endServicePosition;
            int startServicePosition = 0;
//          check for substring separator "#*#", leaves last separator off;
            for (int i = 0; i < serviceModified.length() - 2; i++) {
                if (serviceModified.substring(i,i+3).equals("#*#")) {
                        endServicePosition = i;
                    tempServiceModified = tempServiceModified + serviceModified.substring(startServicePosition, endServicePosition) + ", ";
                    startServicePosition = i+3;
                }
            }
//          Remove comma from the end
            if (tempServiceModified.length() > 2) {
                tempServiceModified = tempServiceModified.substring(0, tempServiceModified.length()-2);
            }
            String customerService = s.getCustomerName() + System.getProperty ("line.separator") + tempServiceModified;
            customerServices.add(customerService);
        }
        return customerServices;
    }

    public static class FindWorkDay extends AsyncTask<String, Void, WorkDay> {

        @Override
        protected WorkDay doInBackground(String... params) {
            return db.workDayDao().findWorkDayByDate(params[0]);
        }

    }
    }


