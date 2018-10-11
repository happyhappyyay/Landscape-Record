package com.happyhappyyay.landscaperecord;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Toast;

import com.cloudant.sync.documentstore.DocumentStore;
import com.cloudant.sync.documentstore.DocumentStoreNotOpenedException;
import com.cloudant.sync.replication.Replicator;
import com.cloudant.sync.replication.ReplicatorBuilder;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

public class TimeReporting extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        PopulateSpinner {
    final static double MILLISECONDS_TO_HOURS = 3600000;
    final static int MAX_NUMBER_OF_ATTEMPTED_HOURS = 16;
    final static String ADAPTER_POSITION = "adapter position";
    private static final String TAG = "WorkDay";
    private final int VIEW_ID = R.id.time_reporting_spinner;
    private Authentication authentication;
    private AppDatabase db;
    private long startTime;
    private boolean checkedIn, recreated;
    private int adapterPosition;
    private List<User> users;
    private User user;
    private WorkDay workDay;
    private int currentHours;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_reporting);
        Toolbar myToolbar = findViewById(R.id.time_reporting_toolbar);
        setSupportActionBar(myToolbar);
        db = AppDatabase.getAppDatabase(this);
        authentication = Authentication.getAuthentication(this);
        user = authentication.getUser();
        if (user != null) {
            if (user.getStartTime() > 0) {
                startTime = user.getStartTime();
                checkedIn = true;
            } else {
                checkedIn = false;
            }
        }

        // Check whether we're recreating a previously destroyed instance
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
        }

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
        currentHours = (int)Math.round(newHours);
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
        } else {
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
                        switch (which) {
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
            } else {
                addAccumulatedHours();
                Toast.makeText(getApplicationContext(), "Checked out! " + (hours) + user.toString(), Toast.LENGTH_LONG).show();
            }
            resetStartTime();
            updateUser();

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

    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        if (users != null) {
            user = users.get(pos);
            adapterPosition = pos;
        } else {
            parent.setSelection(adapterPosition);
        }
    }

    public void onNothingSelected(AdapterView<?> parent) {

    }

//    private void updateDatabase() {
//        File path = getApplicationContext().getDir("documentstores", Context.MODE_PRIVATE);
//        URI uri = null;
//        try {
////            URI("https", apiKey + ":" + apiSecret, host, 443, "/" + dbName, null, null)
////                    .username("yourAPIKey")
////                    .password("yourAPIKeyPassphrase")
////                    .build();
//            uri = new URI("https://" + MainMenu.SETTINGS_API_KEY +":" + MainMenu.SETTINGS_API_SECRET
//                    + "@" + MainMenu.SETTINGS_USER + ".cloudant.com/" + MainMenu.SETTINGS_DB);
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        }
//        DocumentStore ds = null;
//        try {
//            ds = DocumentStore.getInstance(new File(path, "my_datastore"));
//        } catch (DocumentStoreNotOpenedException e) {
//            e.printStackTrace();
//        }
//
//
//// Replicate from the local to remote database
//        Replicator replicator = ReplicatorBuilder.push().from(ds).to(uri).build();
//
//// Fire-and-forget (there are easy ways to monitor the state too)
//        replicator.start();
//    }


    private void updateUser() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                db.userDao().updateUser(user);
                Log.d(TAG, Util.retrieveStringCurrentDate());
                WorkDay tempWorkDay = db.workDayDao().findWorkDayByDate(Util.retrieveStringCurrentDate());
                if (tempWorkDay != null) {
                    workDay = tempWorkDay;
                    Log.d(TAG, "workday exists");
                }
                else {
                    workDay = new WorkDay(Util.retrieveStringCurrentDate());
                    db.workDayDao().insert(workDay);
                    Log.d(TAG, "workday null");
                }
                workDay.addUserHourReference(user.toString(), currentHours);
                db.workDayDao().updateWorkDay(workDay);
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
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ADAPTER_POSITION, adapterPosition);
        Log.d(TAG, Integer.toString(adapterPosition));

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Util.toolbarItemSelection(this, item);
    }
}



