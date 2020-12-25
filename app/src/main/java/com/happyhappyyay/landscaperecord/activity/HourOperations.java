package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.enums.LogActivityAction;
import com.happyhappyyay.landscaperecord.enums.LogActivityType;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;
import java.util.Locale;

import static com.happyhappyyay.landscaperecord.activity.TimeReporting.ADAPTER_POSITION;

public class HourOperations extends AppCompatActivity implements MultiDatabaseAccess <User> {

    private EditText hours;
    private TextView totalHours;
    private User user;
    private int adapterPosition;
    private EditText dateText;
    private String dateString = Util.retrieveStringCurrentDate();
    private int logActivityReference;
    private ProgressBar progressBar;
    public static final String DATE_STRING = "String of date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_operations);
        Toolbar myToolbar = findViewById(R.id.hour_operations_toolbar);
        setSupportActionBar(myToolbar);
        totalHours = findViewById(R.id.hour_operations_num_hours);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        hours = findViewById(R.id.hour_operations_hours_text);
        progressBar = findViewById(R.id.hour_operations_progress_bar);
        if (savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
            dateString = savedInstanceState.getString(DATE_STRING);
        }

        dateText = findViewById(R.id.hour_operations_date_edit_text);
        dateText.setText(dateString);
        dateText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    String customDateString = dateText.getText().toString();
                    if(Util.checkDateFormat(customDateString)) {
                        dateString = customDateString;
                    }
                    else if (customDateString.equals("") || customDateString.equals(" ")){
                        dateText.setText(dateString);
                    }
                    else {
                        dateText.setText("");
                        Toast.makeText(HourOperations.this,
                                getString(R.string.incorrect_date_format),
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        findAllUsers();
    }

    private boolean progressBarIsInvisible() {
        return progressBar.getVisibility() == View.INVISIBLE;
    }

    public void payHours(View view) {
        double ROUNDING_CORRECTION = -0.01;
        if(progressBarIsInvisible()) {
            double payHours = hourCheck();
            if (payHours > 0) {
                if (user.getHours() - payHours >= ROUNDING_CORRECTION) {
                    if(user.getHours() - payHours <= 0){
                        user.setHours(0);
                    }
                    else {
                        user.setHours(user.getHours() - payHours);
                    }
                    Toast.makeText(getApplicationContext(), payHours + " " + getString(R.string.hour_operations_paid_for) +
                                    user.getName() + ". " + (String.format(Locale.US, "%.2f",user.getHours()))
                                    + " " + getString(R.string.hour_operations_remaining),
                            Toast.LENGTH_LONG).show();
                    logActivityReference = 3;
                    updateUser();
                } else {
                    Toast.makeText(getApplicationContext(), getString(R.string.hour_operations_paid) + " " + payHours + " " +
                            getString(R.string.hour_operations_exceeds) + " " + (String.format(Locale.US, "%.2f",user.getHours())),
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void addHours(View view) {
        if(progressBarIsInvisible()) {
            double payHours = hourCheck();
            if (payHours > 0) {
                user.setHours(user.getHours() + payHours);
                Toast.makeText(getApplicationContext(), payHours + " " + getString(R.string.hour_operations_added_for) +
                        " " + user.getName(), Toast.LENGTH_LONG).show();
                logActivityReference = 0;
                updateUser();
            }
        }
    }

    public void removeHours(View view) {
        if(progressBarIsInvisible()) {
            double payHours = hourCheck();
            if (payHours > 0) {
                if (user.getHours() - payHours >= 0) {
                    user.setHours(user.getHours() - payHours);
                    Toast.makeText(getApplicationContext(), payHours + " " + getString(R.string.hour_operations_removed_for) +
                                    " " + user.getName() + ". " + (String.format(Locale.US, "%.2f",user.getHours())) + " " + getString(R.string.hour_operations_remaining),
                            Toast.LENGTH_LONG).show();
                    logActivityReference = 1;
                    updateUser();
                } else {
                    Toast.makeText(getApplicationContext(), payHours + " " + getString(R.string.hour_operations_exceeds)
                            + " " + user.getHours(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    private double hourCheck() {
        double tempHours = 0;
        if (!hours.getText().toString().isEmpty()) {
            try {
                tempHours = Double.parseDouble(hours.getText().toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return tempHours;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        outState.putString(DATE_STRING, dateString);
        super.onSaveInstanceState(outState);
    }

    private void updateUser() {
        progressBar.setVisibility(View.VISIBLE);
        Util.enactMultipleDatabaseOperationsPostExecute(this);
    }

    private void updateWorkDay(DatabaseOperator db) {
        int payLogReference = LogActivityAction.PAY.ordinal();
        int deleteLogReference = LogActivityAction.DELETE.ordinal();
        if (logActivityReference != payLogReference) {
            boolean isNewWorkDay = false;
            WorkDay workDay = Util.WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseString(db, dateString);
            int numberOfHours = (int) Math.round(Double.parseDouble(hours.getText().toString()));
            if (workDay == null) {
                isNewWorkDay = true;
                workDay = new WorkDay(dateString);
            }
            if (logActivityReference == deleteLogReference) {
                numberOfHours = -numberOfHours;
            }

            workDay.addUserHourReference(user.toString(), numberOfHours);

            if (isNewWorkDay) {
                Util.WORK_DAY_REFERENCE.insertClassInstanceFromDatabase(db, workDay);
                LogActivity log = new LogActivity(Authentication.getAuthentication().getUser().getName(),
                        dateString, LogActivityAction.ADD.ordinal(), LogActivityType.WORKDAY.ordinal());
                log.setObjId(workDay.getId());
                Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db,log);
            }
            else {
                Util.WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(db, workDay);
            }
        }
    }

    public void findAllUsers() {
        Util.findAllObjects(this, Util.USER_REFERENCE);
    }

    public void updateTotalHoursText(){
        if(user != null) {
            String totHours = Math.round(user.getHours() * 100) / 100.00 + " hrs";
            totalHours.setText(totHours);
        }
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
    public void accessDatabaseMultipleTimes() {
        if(Util.hasOnlineDatabaseEnabledAndValid(this)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
                Util.USER_REFERENCE.updateClassInstanceFromDatabase(db, user);
                updateWorkDay(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            AppDatabase db = AppDatabase.getAppDatabase(this);
            Util.USER_REFERENCE.updateClassInstanceFromDatabase(db, user);
            updateWorkDay(db);
    }

    @Override
    public void createCustomLog() {
        if(Util.hasOnlineDatabaseEnabledAndValid(this)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
                LogActivity log = new LogActivity(Authentication.getAuthentication().getUser().getName(), user.getName() + " " +
                        hours.getText().toString(), logActivityReference, LogActivityType.HOURS.ordinal());
                log.setObjId(user.getId());
                Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
            AppDatabase db = AppDatabase.getAppDatabase(this);
            LogActivity log = new LogActivity(Authentication.getAuthentication().getUser().getName(), user.getName() + " " +
                    hours.getText().toString(), logActivityReference, LogActivityType.HOURS.ordinal());
            log.setObjId(user.getId());
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
        progressBar.setVisibility(View.INVISIBLE);
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
    public void onPostExecute(final List<User> databaseObjects) {
        if(databaseObjects != null) {
            AdapterView.OnItemSelectedListener listener = new AdapterView.OnItemSelectedListener() {
                List<User> usersInside;
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (usersInside != null) {
                        user = usersInside.get(position);
                        adapterPosition = position;
                    }
                    else {
                        usersInside = databaseObjects;
                        parent.setSelection(adapterPosition);
                        user = usersInside.get(adapterPosition);
                    }
                    updateTotalHoursText();
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };
            Spinner spinner = findViewById(R.id.hour_operations_spinner);
            Util.populateSpinner(spinner,listener,this,databaseObjects, true);
        }
        else{
            hours.setText("");
            updateTotalHoursText();
        }
            progressBar.setVisibility(View.INVISIBLE);
    }
}
