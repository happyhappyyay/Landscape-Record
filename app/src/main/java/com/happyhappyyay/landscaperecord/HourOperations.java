package com.happyhappyyay.landscaperecord;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import static com.happyhappyyay.landscaperecord.TimeReporting.ADAPTER_POSITION;

public class HourOperations extends AppCompatActivity implements PopulateSpinner,
        AdapterView.OnItemSelectedListener, MultiDatabaseAccess {

    private final int VIEW_ID = R.id.hour_operations_spinner;
    private EditText hours;
    private Authentication authentication;
    private List<User> users;
    private User user;
    private int adapterPosition;
    private EditText dateText;
    private String dateString = Util.retrieveStringCurrentDate();
    private int logActivityReference;
    public static final String DATE_STRING = "String of date";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_operations);
        Toolbar myToolbar = findViewById(R.id.hour_operations_toolbar);
        setSupportActionBar(myToolbar);
        hours = findViewById(R.id.hour_operations_hours_text);
        authentication = Authentication.getAuthentication();
        if (savedInstanceState != null) {
            // Restore value of members from saved state
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
        Util.UserAccountsSpinner task = new Util.UserAccountsSpinner() {
            @Override
            protected void onPostExecute(List<User> dbUsers) {
                users = dbUsers;
            }
        };
        task.execute(this);
    }

    public void payHours(View view) {
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

    public void addHours(View view) {
        double payHours = hourCheck();
        if (payHours > 0) {
            user.setHours(user.getHours() + payHours);
            Toast.makeText(getApplicationContext(), payHours + " hours added for " +
                    user.getName(), Toast.LENGTH_LONG).show();
            logActivityReference = 0;
            updateUser();
        }

    }

    public void removeHours(View view) {
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
                Toast.makeText(getApplicationContext(), "Hours removed: " + payHours + " exceeds " +
                        "hours recorded for work: " + user.getHours(), Toast.LENGTH_LONG).show();
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
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int getViewID() {
        return VIEW_ID;
    }

    @Override
    public AdapterView.OnItemSelectedListener getItemSelectedListener() {
        return this;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        outState.putString(DATE_STRING, dateString);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (users != null) {
            user = users.get(position);
            adapterPosition = position;
        }
        else {
            parent.setSelection(adapterPosition);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void updateUser() {
        Util.enactMultipleDatabaseOperations(this);

//        new AsyncTask<Integer, Void, Void>() {
//            @Override
//            protected Void doInBackground(Integer... integers) {
//                int payLogReference = LogActivityAction.valueOf("PAY").ordinal();
//                int deleteLogReference = LogActivityAction.valueOf("DELETE").ordinal();
//                if (!integers[0].equals(payLogReference)) {
//                    boolean isNewWorkDay = false;
//                    WorkDay workDay = db.workDayDao().findWorkDayByDate(dateString);
//                    int numberOfHours = (int) Math.round(Double.parseDouble(hours.getText().toString()));
//                    if (workDay == null) {
//                        isNewWorkDay = true;
//                        workDay = new WorkDay(dateString);
//                    }
//                    if (integers[0].equals(deleteLogReference)) {
//                        numberOfHours = -numberOfHours;
//                    }
//
//                    workDay.addUserHourReference(user.toString(), numberOfHours);
//
//                    if (isNewWorkDay) {
//                        db.workDayDao().insert(workDay);
//                    }
//                    else {
//                        db.workDayDao().updateWorkDay(workDay);
//                    }
//
//                }
//
//                LogActivity log = new LogActivity(authentication.getUser().getName(), user.getName() + " " + hours.getText().toString() + " for " + dateString, logActivityReference, 3);
//                db.logDao().insert(log);
//                db.userDao().updateUser(user);
//                return null;
//            }
//
//            @Override
//            protected void onPostExecute(Void aVoid) {
//                hours.setText("");
//
//                }
//        }.execute(logActivityReference);
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
            }
            else {
                Util.WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(db, workDay);
            }

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
        try {
            OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
            Util.USER_REFERENCE.updateClassInstanceFromDatabase(db, user);
            updateWorkDay(db);
        }catch (Exception e) {
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
    public void onPostExecute(List databaseObjects) {
    }
}
