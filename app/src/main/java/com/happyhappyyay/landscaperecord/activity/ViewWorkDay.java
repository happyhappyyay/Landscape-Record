package com.happyhappyyay.landscaperecord.activity;

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
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.adapter.RecyclerViewWorkDay;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ViewWorkDay extends AppCompatActivity implements MultiDatabaseAccess<WorkDay> {
    private WorkDay workDay;
    private List<WorkDay> workDays;
    private RecyclerView recyclerView;
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
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        CalendarView calendarView = findViewById(R.id.view_work_activity_calendar);
        recyclerView = findViewById(R.id.view_work_day_recyclerview);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        RadioButton dayButton = findViewById(R.id.view_work_day_day_button);
        dayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSpanChoice = 0;
                findWorkDayByDate();
            }
        });
        RadioButton weekButton = findViewById(R.id.view_work_day_week_button);
        weekButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                timeSpanChoice = 1;
                findWorkDayByDate();
            }
        });
        RadioButton monthButton = findViewById(R.id.view_work_day_month_button);
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putString(CALENDAR_POSITION, calendarPosition);
        outState.putInt(TIME_SPAN_CHOICE, timeSpanChoice);
        super.onSaveInstanceState(outState);
    }

    private void noWorkDayMessage() {
        Toast.makeText(getApplicationContext(), getString(R.string.view_work_day_no_information), Toast.LENGTH_SHORT).show();
    }

    private void findWorkDayByDate() {
        progressBar.setVisibility(View.VISIBLE);
        Util.enactMultipleDatabaseOperationsPostExecute(this);
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

        RecyclerViewWorkDay adapter = new RecyclerViewWorkDay(userWithHours, customerWithServices);
        recyclerView.setAdapter(adapter);
        workDay = null;
        workDays = null;
    }

    public List<String> createStringFromUserHourReferences(WorkDay workDay) {
        return workDay.retrieveUsersHoursAsString();
    }

    public List<String> createStringFromUserHourReferences(List<WorkDay> workDays) {
        Map<String, Integer> userHours = new HashMap<>();
        for(WorkDay w: workDays) {
            Set< Map.Entry<String, Integer> > mapSet = w.getUserHours().entrySet();
            for (Map.Entry< String, Integer> mapEntry:mapSet)
            {
                String key = mapEntry.getKey();
                Integer value = mapEntry.getValue();
                if(userHours.containsKey(key)) {
                    userHours.put(key,userHours.get(key) + value);
                }
                else {
                    userHours.put(key, value);
                }
            }
        }

        List<String> strings = new ArrayList<>();
        Set< Map.Entry<String, Integer> > mapSet = userHours.entrySet();
        int count = 0;

        for (Map.Entry< String, Integer> mapEntry:mapSet)
        {
            String hourEntry = mapEntry.getKey() + " : " + mapEntry.getValue();
            strings.add(count, hourEntry);
            count++;
        }
        return strings;
    }

    public List<String> removeCustomerServicesStopCharacters(List<Service> services) {
        List<String> customerServices = new ArrayList<>();
        for (Service s: services) {
            String serviceString = s.getServices();
            String serviceStringWithoutSeparators;
            StringBuilder sb = new StringBuilder();
            int endServicePosition;
            int startServicePosition = 0;

            for (int i = 0; i < serviceString.length(); i++) {
                if (serviceString.substring(i,i+1).equals(Util.DELIMITER)) {
                    endServicePosition = i;
                    String tempString = serviceString.substring(startServicePosition, endServicePosition) + ", ";
                    sb.append(tempString);
                    startServicePosition = i+1;
                }
            }
            serviceStringWithoutSeparators = sb.toString();
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
        if(Util.hasOnlineDatabaseEnabledAndValid(this)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
                databaseAccessMethod(db);
            } catch (Exception e) {
                AppDatabase db = AppDatabase.getAppDatabase(this);
                databaseAccessMethod(db);
            }
        }
        else {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            databaseAccessMethod(db);
        }
    }

    public void databaseAccessMethod(DatabaseOperator db) {
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


