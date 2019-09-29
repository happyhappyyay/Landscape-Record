package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Display;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.enums.LogActivityAction;
import com.happyhappyyay.landscaperecord.enums.LogActivityType;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

public class UserSetting extends AppCompatActivity implements MultiDatabaseAccess<LogActivity> {
    List<LogActivity> logs;
    List<WorkDay> workDays;
    TextView rate;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        Toolbar myToolbar = findViewById(R.id.user_settings_toolbar);
        progressBar = findViewById(R.id.user_settings_progress);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        logs = new ArrayList<>();
        workDays = new ArrayList<>();
        Authentication authentication = Authentication.getAuthentication();
        User user = authentication.getUser();
        TextView username = findViewById(R.id.user_settings_username);
        username.setText(user.getName());
        Util.enactMultipleDatabaseOperationsPostExecute(this);
    }

    private void populateStats(String username) {
        if(Util.hasOnlineDatabaseEnabledAndValid(this)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
                logs = Util.LOG_REFERENCE.retrieveClassInstancesFromActedUser(db, username);
                workDays = Util.WORK_DAY_REFERENCE.retrieveAllClassInstancesFromDatabase(db);
            } catch (Exception e) {
                AppDatabase db = AppDatabase.getAppDatabase(this);
                logs = Util.LOG_REFERENCE.retrieveClassInstancesFromActedUser(db, username);
                workDays = Util.WORK_DAY_REFERENCE.retrieveAllClassInstancesFromDatabase(db);
            }
        }
        else {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            logs = Util.LOG_REFERENCE.retrieveClassInstancesFromActedUser(db, username);
            workDays = Util.WORK_DAY_REFERENCE.retrieveAllClassInstancesFromDatabase(db);
    }
    }

    public void onClick(View view) {
        Intent intent = new Intent(getApplicationContext(), EditUser.class);
        intent.putExtra("USER_ID", Authentication.getAuthentication().getUser().getId());
        startActivity(intent);
    }

    private void setupStats(){
        TextView tHours = findViewById(R.id.user_settings_hours);
        TextView days = findViewById(R.id.user_settings_days);
        TextView customers = findViewById(R.id.user_settings_customers);
        TextView completed = findViewById(R.id.user_settings_completed);
        TextView started = findViewById(R.id.user_settings_started);
        TextView expenses = findViewById(R.id.user_settings_expenses);
        List<Integer> stats = calculateStats();
        double percentRate = (stats.get(6)/100.0)>=1? 1:(stats.get(6)/100.0);
        rate = findViewById(R.id.user_settings_bar);
        tHours.setText(String.format(Locale.US,"%d",stats.get(0)));
        days.setText(String.format(Locale.US,"%d",stats.get(1)));
        customers.setText(String.format(Locale.US,"%d",stats.get(2)));
        completed.setText(String.format(Locale.US,"%d",stats.get(3)));
        started.setText(String.format(Locale.US,"%d",stats.get(4)));
        expenses.setText(NumberFormat.getCurrencyInstance(Locale.US).format(stats.get(5)));
        rate.setText(NumberFormat.getPercentInstance(Locale.US).format(percentRate));
        rate.setBackgroundColor(stats.get(6) >50? Color.GREEN:Color.RED);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        ConstraintLayout.LayoutParams params = (ConstraintLayout.LayoutParams) rate.getLayoutParams();
        params.width = (int) ((size.x -20)*(percentRate));
        rate.setLayoutParams(params);
    }

    private List<Integer> calculateStats(){
        String name = Authentication.getAuthentication().getUser().getName();
        int customers =0;
        int hours = 0;
        int days = 0;
        int completed = 0;
        int started = 0;
        int expenses = 0;
        int payments = 0;
        int rate;
        for(LogActivity l: logs){
            if(l.getLogActivityAction()== LogActivityAction.ADD.ordinal()){
                if(l.getLogActivityType() == LogActivityType.CUSTOMER.ordinal()){
                    customers++;
                }
                else if(l.getLogActivityType() == LogActivityType.SERVICES.ordinal()){
                    started++;
                }
            }
            if(l.getLogActivityAction() == LogActivityAction.COMPLETED.ordinal()){
                if(l.getLogActivityType() == LogActivityType.SERVICES.ordinal()) {
                    completed++;
                }
            }
        }
        rate = started != 0? ((int)((completed/(started*1.0))*100)):completed!=0? 0:100;

        for(WorkDay d: workDays){
            int tempHours = d.hoursForUser(name);
            if(tempHours > 0){
                days++;
            }
            hours+= tempHours;
            expenses+=d.expensesForUser(name);
            payments+=d.numPaymentsForUser(name);
        }
        return new ArrayList<>(Arrays.asList(hours,days,customers,completed,started,expenses,
                rate,payments));
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

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return null;
    }

    @Override
    public void onPostExecute(List<LogActivity> databaseObjects) {
        setupStats();
        progressBar.setVisibility(View.INVISIBLE);
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        populateStats(Authentication.getAuthentication().getUser().getName());
    }

    @Override
    public void createCustomLog() {

    }
}
