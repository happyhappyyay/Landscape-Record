package com.example.kingdenis.landscaperecord;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

import static com.example.kingdenis.landscaperecord.TimeReporting.ADAPTER_POSITION;

public class HourOperations extends AppCompatActivity implements PopulateSpinner,
        AdapterView.OnItemSelectedListener {

    private final int VIEW_ID = R.id.hour_operations_spinner;
    private EditText hours;
    private Authentication authentication;
    private List<User> users;
    private User user;
    private AppDatabase db;
    private int adapterPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_operations);
        hours = (EditText) findViewById(R.id.hour_operations_hours_text);
        authentication = Authentication.getAuthentication(this);
        db = AppDatabase.getAppDatabase(this);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
        }
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
                updateUser(3);
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
            updateUser(0);
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
                updateUser(1);
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

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (users != null) {
            Toast.makeText(parent.getContext(),
                    "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                    Toast.LENGTH_SHORT).show();
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

    private void updateUser(final Integer logActivityReference) {
        new AsyncTask<Integer, Void, Void>() {
            @Override
            protected Void doInBackground(Integer... integers) {
                LogActivity log = new LogActivity(authentication.getUser().getName(), user.getName() + " " + hours.getText().toString(), logActivityReference, 3);
                db.logDao().insert(log);
                db.userDao().updateUser(user);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                hours.setText("");

                }
        }.execute();
    }
}
