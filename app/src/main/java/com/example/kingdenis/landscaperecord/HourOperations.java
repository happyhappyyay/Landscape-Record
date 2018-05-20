package com.example.kingdenis.landscaperecord;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class HourOperations extends AppCompatActivity implements PopulateSpinner,
        AdapterView.OnItemSelectedListener {

    private EditText hours;
    private Authentication authentication;
    private List<User> users;
    private User user;
    private AppDatabase db;

    private final int VIEW_ID = R.id.hour_operations_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hour_operations);
        hours = (EditText) findViewById(R.id.hour_operations_hours_text);
        authentication = authentication.getAuthentication(this);
        db = AppDatabase.getAppDatabase(this);
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
        if(payHours > 0) {
            if(user.getHours() - payHours >= 0) {
                user.setHours(user.getHours() - payHours);
                Toast.makeText(getApplicationContext(), payHours + " hours paid for " +
                        user.getName() + ". " + user.getHours() + "remaining.",
                        Toast.LENGTH_LONG).show();
                hours.setText("");
                updateUser();
            }
            else {
                Toast.makeText(getApplicationContext(), "Hours paid: " + payHours + " exceeds " +
                        "hours recorded for work: " + user.getHours(), Toast.LENGTH_LONG).show();
            }
        }
    }

    public void addHours(View view) {
        double payHours = hourCheck();
        if(payHours > 0) {
            user.setHours(user.getHours() + payHours);
            Toast.makeText(getApplicationContext(), payHours + " hours added for " +
                    user.getName(), Toast.LENGTH_LONG).show();
            hours.setText("");
            updateUser();
        }

    }

    public void removeHours(View view) {
//        TODO: Add different logs for remove and pay
        payHours(view);
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
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        user = users.get(position);
    }

    @Override
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
}
