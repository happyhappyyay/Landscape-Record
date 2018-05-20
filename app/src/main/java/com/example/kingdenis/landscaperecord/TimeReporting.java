package com.example.kingdenis.landscaperecord;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import java.util.List;

public class TimeReporting extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
PopulateSpinner{
    private Authentication authentication;
    private AppDatabase db;
    private double startTime;
    private boolean checkedIn;
    private static final String TAG = "User Hour";
    Context context;
    private List<User> users;
    private User user;

    private final int VIEW_ID = R.id.time_reporting_spinner;;

    final static double MILLISECONDS_TO_HOURS = 3600000;
    final static int MAX_NUMBER_OF_ATTEMPTED_HOURS = 16;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_reporting);
        db = AppDatabase.getAppDatabase(this);
        authentication = Authentication.getAuthentication(this);
        user = authentication.getUser();
        if(user != null) {
            if (user.getStartTime() > 0) {
                startTime = user.getStartTime();
                checkedIn = true;
            } else {
                checkedIn = false;
            }
        }
        context = this;
        //TODO: implement interface to remove on post execute?
        Util.UserAccountsSpinner task = new Util.UserAccountsSpinner() {
            @Override
            protected void onPostExecute(List<User> dbUsers) {
                users = dbUsers;
            }
        };
        task.execute(this);


    }
    private void resetStartTime() {
        user.setStartTime(0);
    }
    private void addAccumulatedHours() {
        double newHours = (System.currentTimeMillis() - startTime) / MILLISECONDS_TO_HOURS;
        user.setHours(user.getHours() + newHours);
    }

    public void createCheckIn(View view) {
        long currentTime = System.currentTimeMillis();
        double hours = (currentTime - startTime) / MILLISECONDS_TO_HOURS;
        boolean startTimeChange = false;
        updateCheckInStatus();
        if (!checkedIn) {
            startTime = currentTime;
            user.setStartTime(startTime);
            Toast.makeText(getApplicationContext(), "Checked in!", Toast.LENGTH_LONG).show();
            startTimeChange = true;
        }
        else {
            if (hours >= MAX_NUMBER_OF_ATTEMPTED_HOURS) {
                Toast.makeText(getApplicationContext(), "User never checked out. Notify admin" +
                        " account to manually add hours.", Toast.LENGTH_LONG).show();
                resetStartTime();
                startTimeChange = true;
            }
        }
        if (startTimeChange) {
            updateUser();
        }

    }

    public void createCheckOut(View view) {
        double currentTime = System.currentTimeMillis();
        updateCheckInStatus();
        double hours = ((currentTime - startTime) / MILLISECONDS_TO_HOURS);

        if (checkedIn) {
            if (hours >= MAX_NUMBER_OF_ATTEMPTED_HOURS) {

                DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which){
                            case DialogInterface.BUTTON_POSITIVE:
                                addAccumulatedHours();
                                break;

                            case DialogInterface.BUTTON_NEGATIVE:
                                Toast.makeText(getApplicationContext(), "Notify admin" +
                                        " account to manually add hours.", Toast.LENGTH_LONG).show();
                                break;
                        }
                    }
                };

                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("The number of hours reported exceeds the likely amount. Is " +
                        hours + " hours correct?").setPositiveButton("Yes", dialogClickListener)
                        .setNegativeButton("No", dialogClickListener).show();
            }
            else {
                addAccumulatedHours();
                    Toast.makeText(getApplicationContext(), "Checked out! " + (hours) + user.toString(), Toast.LENGTH_LONG).show();
            }
            resetStartTime();
            updateUser();

        }
    }
    private void updateCheckInStatus() {
        if(user != null) {
            if (user.getStartTime() > 0) {
                startTime = user.getStartTime();
                checkedIn = true;
            } else {
                checkedIn = false;
            }
        }
    }

//    private void populateSpinner(List<User> users) {
//        String[] arraySpinner = new String[users.size()];
//        int pos = 0;
//        for (int i = 0; i < users.size(); i++) {
//            arraySpinner[i] = users.get(i).getName();
//            if(users.get(i).getName().equals(authentication.getUser().getName())) {
//                pos = i;
//            }
//        }
//
//        Spinner s = (Spinner) findViewById(R.id.time_reporting_spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.spinner_item, arraySpinner);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        s.setAdapter(adapter);
//        s.setOnItemSelectedListener(this);
//        s.setSelection(pos);
//    }

    public void onItemSelected(AdapterView<?> parent, View view, int pos,long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(pos).toString(),
                Toast.LENGTH_SHORT).show();
        user = users.get(pos);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        user = authentication.getUser();
    }


    private void updateUser() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.userDao().updateUser(user);
                return null;
            }
        }.execute();
    }
    private void findAllUsers() {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                users = db.userDao().getAllUsers();
                return null;
            }
        }.execute();
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
}
