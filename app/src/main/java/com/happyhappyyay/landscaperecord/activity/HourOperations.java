package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
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

import static com.happyhappyyay.landscaperecord.activity.TimeReporting.ADAPTER_POSITION;

public class HourOperations extends AppCompatActivity implements MultiDatabaseAccess <User> {

    private EditText hours;
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
                                "Date format incorrect. Please reenter the date.",
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
        if(progressBarIsInvisible()) {
            double payHours = hourCheck();
            if (payHours > 0) {
                if (user.getHours() - payHours >= 0) {
                    user.setHours(user.getHours() - payHours);
                    Toast.makeText(getApplicationContext(), payHours + " hours paid for " +
                                    user.getName() + ". " + user.getHours() + "remaining.",
                            Toast.LENGTH_LONG).show();
                    logActivityReference = 3;
                    updateUser();
                } else {
                    Toast.makeText(getApplicationContext(), "Hours paid: " + payHours + " exceeds " +
                            "hours recorded for work: " + user.getHours(), Toast.LENGTH_LONG).show();
                }
            }
        }
    }

    public void addHours(View view) {
        if(progressBarIsInvisible()) {
            double payHours = hourCheck();
            if (payHours > 0) {
                user.setHours(user.getHours() + payHours);
                Toast.makeText(getApplicationContext(), payHours + " hours added for " +
                        user.getName(), Toast.LENGTH_LONG).show();
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
                    Toast.makeText(getApplicationContext(), payHours + " hours removed for " +
                                    user.getName() + ". " + user.getHours() + "remaining.",
                            Toast.LENGTH_LONG).show();
                    logActivityReference = 1;
                    updateUser();
                } else {
                    Toast.makeText(getApplicationContext(), payHours + " exceeds " +
                            "hours recorded for work: " + user.getHours(), Toast.LENGTH_LONG).show();
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
        Util.enactMultipleDatabaseOperations(this);
    }

    private void updateWorkDay(DatabaseOperator db) {
        int payLogReference = LogActivityAction.valueOf("PAY").ordinal();
        int deleteLogReference = LogActivityAction.valueOf("DELETE").ordinal();
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
                LogActivity log = new LogActivity(Authentication.getAuthentication().getUser().getName(), dateString, LogActivityAction.ADD.ordinal(), LogActivityType.WORKDAY.ordinal());
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
                AppDatabase db = AppDatabase.getAppDatabase(this);
                Util.USER_REFERENCE.updateClassInstanceFromDatabase(db, user);
                updateWorkDay(db);
            }
        }
        else {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            Util.USER_REFERENCE.updateClassInstanceFromDatabase(db, user);
            updateWorkDay(db);
        }
    }

    @Override
    public void createCustomLog() {
        try {
            OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
            LogActivity log = new LogActivity(user.getName(), user.getName() + " " + hours.getText().toString(), logActivityReference, LogActivityType.HOURS.ordinal());
            log.setObjId(user.getId());
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
            hours.setText("");
        } catch (Exception e) {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            LogActivity log = new LogActivity(user.getName(), user.getName() + " " + hours.getText().toString(), logActivityReference, LogActivityType.HOURS.ordinal());
            log.setObjId(user.getId());
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
            hours.setText("");
        }
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
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            };
            Spinner spinner = findViewById(R.id.hour_operations_spinner);
            Util.populateSpinner(spinner,listener,this,databaseObjects, true);
        }
            progressBar.setVisibility(View.INVISIBLE);

    }
}
