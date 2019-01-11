package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class ViewWorkDay extends AppCompatActivity implements MultiDatabaseAccess<WorkDay> {
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
    private int timeSpanChoice;
    private ProgressBar progressBar;
    final static String TIME_SPAN_CHOICE = "time span choice";
    final static String CALENDAR_POSITION = "calendar position";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_work_day);
        Toolbar myToolbar = findViewById(R.id.view_work_day_toolbar);
        setSupportActionBar(myToolbar);
        calendarView = findViewById(R.id.view_work_activity_calendar);
        db = AppDatabase.getAppDatabase(this);
        recyclerView = findViewById(R.id.view_work_day_recyclerview);
        layoutManager = new LinearLayoutManager(this);
        dayButton = findViewById(R.id.view_work_day_day_button);
        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSpanChoice = 0;
                findWorkDayByDate();
            }
        });
        weekButton = findViewById(R.id.view_work_day_week_button);
        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSpanChoice = 1;
                findWorkDayByDate();
            }
        });
        monthButton = findViewById(R.id.view_work_day_month_button);
        monthButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSpanChoice = 2;
                findWorkDayByDate();
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        if (savedInstanceState != null) {
            calendarPosition = savedInstanceState.getString(CALENDAR_POSITION);
            timeSpanChoice = savedInstanceState.getInt(TIME_SPAN_CHOICE);
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
                findWorkDayByDate();
                calendarPosition = date;
            }
        });
        progressBar = findViewById(R.id.view_work_day_progress_bar);
        findWorkDayByDate();
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CALENDAR_POSITION, calendarPosition);
        outState.putInt(TIME_SPAN_CHOICE, timeSpanChoice);
        super.onSaveInstanceState(outState);
    }

    private void noWorkDayMessage() {
        Toast.makeText(getApplicationContext(), "No information available for this date", Toast.LENGTH_SHORT).show();
    }

    private void findWorkDayByDate() {
        progressBar.setVisibility(View.VISIBLE);
        Util.enactMultipleDatabaseOperationsPostExecute(this);
//        new AsyncTask<String, Void, Void>() {
//            @Override
//            protected Void doInBackground(String... sDate) {
//
//                switch(timeSpanChoice) {
//                    case 0:
//                        WorkDay selectedWorkDay = db.workDayDao().findWorkDayByDate(sDate[0]);
//                        if (selectedWorkDay != null) {
//                            workDay = selectedWorkDay;
//                        }
//                        break;
//                    case 1:
//                        List<WorkDay> selectedWorkWeek = db.workDayDao().findWorkWeekByTime(Util.convertStringToFirstDayOfWeekMilli(sDate[0]));
//
//                        if (selectedWorkWeek != null) {
//                            workDays = selectedWorkWeek;
//                        }
//                        break;
//                    case 2:
//                        List<WorkDay> selectedWorkMonth = db.workDayDao().findWorkMonthByTime(Util.convertStringToFirstDayOfMonthMilli(sDate[0]));
//
//                        if (selectedWorkMonth != null) {
//                            workDays = selectedWorkMonth;
//                        }
//                }
//                users = db.userDao().getAllUsers();
//                return null;
//            }
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                setupViewWorkDayAdapter();
//            }
//        }.execute(date);
    }

    private void setupViewWorkDayAdapter() {
        List<String> userWithHours = new ArrayList<>();
        List<String> customerWithServices = new ArrayList<>();
        List<Service> services = new ArrayList<>();

        if (workDay != null) {
            services = workDay.getServices();
            userWithHours = createStringFromUserHourReferences(workDay);
            if (services != null) {
                customerWithServices = removeCustomerServicesStopCharacters(services);
            }
        }
        else if (workDays != null) {
            for (WorkDay w: workDays) {
                services.addAll(w.getServices());
            }
            userWithHours = createStringFromUserHourReferences(workDays);
            if (!services.isEmpty()) {
                customerWithServices = removeCustomerServicesStopCharacters(services);
            }
        }
        else {
            noWorkDayMessage();
        }

        adapter = new RecyclerViewWorkDayAdapter(userWithHours, customerWithServices);
        recyclerView.setAdapter(adapter);
        workDay = null;
        workDays = null;
    }

    public List<String> createStringFromUserHourReferences(WorkDay workDay) {
        List<String> usersAndHours = new ArrayList<>();
        List<String> userReferences = workDay.getUserReference();
        List<Integer> userHours = workDay.getHours();

        for(int i = 0; i < workDay.getHours().size(); i++) {
            String userAndHours = userReferences.get(i) + " : " + userHours.get(i);
            usersAndHours.add(userAndHours);
        }

        return usersAndHours;
    }

    public List<String> createStringFromUserHourReferences(List<WorkDay> workDays) {
        List<String> usersAndHours = new ArrayList<>();
        List<Integer> userHours = new ArrayList<>();
        List<String> userReferences = new ArrayList<>();
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
            String userAndHours = userReferences.get(i) + " : " + userHours.get(i);
            usersAndHours.add(userAndHours);
        }
        return usersAndHours;
    }

    public List<String> removeCustomerServicesStopCharacters(List<Service> services) {
        List<String> customerServices = new ArrayList<>();
        for (Service s: services) {
            String serviceString = s.getServices();
            String serviceStringWithoutSeparators = "";
            int endServicePosition;
            int startServicePosition = 0;

            for (int i = 0; i < serviceString.length(); i++) {
                if (serviceString.substring(i,i+1).equals("|")) {
                    endServicePosition = i;
                    serviceStringWithoutSeparators += serviceString.substring(startServicePosition, endServicePosition) + ", ";
                    startServicePosition = i+1;
                }
            }

            if (serviceStringWithoutSeparators.length() > 2) {
                serviceStringWithoutSeparators = serviceStringWithoutSeparators.substring(0, serviceStringWithoutSeparators.length()-2);
            }
            String customerService = s.getCustomerName() + ":" +
                    System.getProperty ("line.separator") + serviceStringWithoutSeparators;
            customerServices.add(customerService);
        }
        return customerServices;
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        AppDatabase db = AppDatabase.getAppDatabase(this);
        switch(timeSpanChoice) {
            case 0:
                WorkDay selectedWorkDay = Util.WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseString(db, calendarPosition);
                if (selectedWorkDay != null) {
                    workDay = selectedWorkDay;
                }
                break;
            case 1:
                List<WorkDay> selectedWorkWeek = Util.WORK_DAY_REFERENCE.retrieveClassInstancesFromDatabaseByWeek(db, Util.convertStringToFirstDayOfWeekMilli(calendarPosition));

                if (selectedWorkWeek != null) {
                    workDays = selectedWorkWeek;
                }
                break;
            case 2:
                List<WorkDay> selectedWorkMonth = Util.WORK_DAY_REFERENCE.retrieveClassInstancesFromDatabaseByMonth(db, Util.convertStringToFirstDayOfMonthMilli(calendarPosition));

                if (selectedWorkMonth != null) {
                    workDays = selectedWorkMonth;
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void createCustomLog() {

    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return null;
    }

    @Override
    public void onPostExecute(List<WorkDay> databaseObjects) {
        setupViewWorkDayAdapter();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Util.toolbarItemSelection(this, item);
    }
}


