package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
import java.util.Locale;

public class TimeReporting extends AppCompatActivity implements MultiDatabaseAccess<User> {
    public final static double MILLISECONDS_TO_HOURS = 3600000;
    final int MAX_NUMBER_OF_ATTEMPTED_HOURS = 16;
    final static String ADAPTER_POSITION = "adapter position";
    private long startTime;
    private boolean checkedIn;
    private int adapterPosition;
    private User user;
    private int currentHours;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_reporting);
        Toolbar myToolbar = findViewById(R.id.time_reporting_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        if (user != null) {
            if (user.getStartTime() > 0) {
                startTime = user.getStartTime();
                checkedIn = true;
            } else {
                checkedIn = false;
            }
        }

        if (savedInstanceState != null) {
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
        }
        progressBar = findViewById(R.id.time_reporting_progress_bar);
        findAllUsers();
    }

    private void resetStartTime() {
        user.setStartTime(0);
    }

    private void addAccumulatedHours() {
        double newHours = (System.currentTimeMillis() - startTime) / MILLISECONDS_TO_HOURS;
        user.setHours(user.getHours() + newHours);
        currentHours = (int)Math.round(newHours);
    }

    public void createCheckIn(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            boolean authenticatedUser = false;
            if (user.getId().equals(Authentication.getAuthentication().getUser().getId())) {
                authenticatedUser = true;
            }
            long currentTime = System.currentTimeMillis();
            double hours = (currentTime - startTime) / MILLISECONDS_TO_HOURS;
            boolean startTimeChange = false;
            if (!checkedIn) {
                startTime = currentTime;
                user.setStartTime(startTime);
                Toast.makeText(getApplicationContext(), getString(R.string.time_reporting_checked_in),
                        Toast.LENGTH_LONG).show();
                startTimeChange = true;
            } else {
                if (hours >= MAX_NUMBER_OF_ATTEMPTED_HOURS) {
                    Toast.makeText(getApplicationContext(), getString(R.string.time_reporting_max_error),
                            Toast.LENGTH_LONG).show();
                    resetStartTime();
                    startTimeChange = true;
                }
            }
            if (startTimeChange) {
                updateUser(authenticatedUser);
            }
        }
    }

    public void createCheckOut(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            boolean authenticatedUser = false;
            if (user.getId().equals(Authentication.getAuthentication().getUser().getId())) {
                authenticatedUser = true;
            }
            double currentTime = System.currentTimeMillis();
            double hours = ((currentTime - startTime) / MILLISECONDS_TO_HOURS);

            if (checkedIn) {
                if (hours >= MAX_NUMBER_OF_ATTEMPTED_HOURS) {

                    DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            switch (which) {
                                case DialogInterface.BUTTON_POSITIVE:
                                    addAccumulatedHours();
                                    break;

                                case DialogInterface.BUTTON_NEGATIVE:
                                    Toast.makeText(getApplicationContext(), getString(R.string.time_reporting_notify_admin_error),
                                            Toast.LENGTH_LONG).show();
                                    break;
                            }
                        }
                    };

                    AlertDialog.Builder builder = new AlertDialog.Builder(this);
                    builder.setMessage(getString(R.string.time_reporting_exceeds_likely))
                            .setPositiveButton(getString(R.string.yes),
                            dialogClickListener)
                            .setNegativeButton(getString(R.string.no), dialogClickListener).show();
                } else {
                    addAccumulatedHours();
                    Toast.makeText(getApplicationContext(), getString(R.string.time_reporting_checked_out)
                            + " " + (String.format(Locale.US, "%.2f",hours)) + " " + user.toString(),
                            Toast.LENGTH_LONG).show();
                }
                resetStartTime();
                updateUser(authenticatedUser);
            }
        }
    }

    private void updateCheckInStatus() {
        if (user != null) {
            if (user.getStartTime() > 0) {
                startTime = user.getStartTime();
                checkedIn = true;
            } else {
                checkedIn = false;
            }
        }
    }

    private void updateUser(boolean authenticatedUser) {
        if(authenticatedUser) {
            Authentication.getAuthentication().setUser(user);
        }
        progressBar.setVisibility(View.VISIBLE);
        Util.enactMultipleDatabaseOperationsPostExecute(this);
    }

    private void findAllUsers() {
        progressBar.setVisibility(View.VISIBLE);
        Util.findAllObjects(this, Util.USER_REFERENCE);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        super.onSaveInstanceState(outState);
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
                databaseAccessMethod(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AppDatabase db = AppDatabase.getAppDatabase(this);
        databaseAccessMethod(db);
    }

    private void databaseAccessMethod(DatabaseOperator db) {
        WorkDay workDay;
        Util.USER_REFERENCE.updateClassInstanceFromDatabase(db, user);
        WorkDay tempWorkDay = Util.WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseString(db, Util.retrieveStringCurrentDate());
        if (tempWorkDay != null) {
            workDay = tempWorkDay;
        } else {
            workDay = new WorkDay(Util.retrieveStringCurrentDate());
            Util.WORK_DAY_REFERENCE.insertClassInstanceFromDatabase(db, workDay);
            LogActivity log = new LogActivity(Authentication.getAuthentication().getUser().getName(),
                    "Workday: " + Util.retrieveStringCurrentDate(), LogActivityAction.ADD.ordinal(),
                    LogActivityType.WORKDAY.ordinal());
            log.setObjId(workDay.getId());
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db,log);
        }
        workDay.addUserHourReference(user.toString(), currentHours);
        Util.WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(db, workDay);
        }

    @Override
    public void createCustomLog() {
        try {
            OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
            customLogMethod(db);
        } catch(Exception e) {
            AppDatabase db = AppDatabase.getAppDatabase(this);
            customLogMethod(db);
        }
    }

    private void customLogMethod(DatabaseOperator db) {
        LogActivity log;
        updateCheckInStatus();
        if(checkedIn) {
            log = new LogActivity(Authentication.getAuthentication().getUser().getName(), user.getName(),
                    LogActivityAction.CHECKED_IN.ordinal(), LogActivityType.USER.ordinal());
        }
        else {
            log = new LogActivity(Authentication.getAuthentication().getUser().getName(), user.getName(),
                    LogActivityAction.CHECKED_OUT.ordinal(), LogActivityType.USER.ordinal());
        }
        Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db, log);
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

                public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                    if (usersInside != null) {
                        user = usersInside.get(pos);
                        adapterPosition = pos;
                    } else {
                        usersInside = databaseObjects;
                        user = usersInside.get(adapterPosition);
                        parent.setSelection(adapterPosition);
                    }
                    startTime = user.getStartTime();
                    updateCheckInStatus();
                }

                public void onNothingSelected(AdapterView<?> parent) {
                }
            };
            Spinner spinner = findViewById(R.id.time_reporting_spinner);
            Util.populateSpinner(spinner, listener, this, databaseObjects, true);
        }
        progressBar.setVisibility(View.INVISIBLE);
    }
}



