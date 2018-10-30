package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Dashboard extends AppCompatActivity implements MultiDatabaseAccess<User> {
    private TextView checkInTime, paymentNotification, paymentNotificationText, billingNotification,
            billingNotificationText, hoursNotification, hoursNotificationText,
            notificationNotification, checkedInNotification, inProgressNotification;
    private Authentication authentication;
    private List<User> users;
    private List<Customer> customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        User user = Authentication.getAuthentication(this).getUser();
        TextView userLoggedIn;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        authentication = Authentication.getAuthentication(this);
        checkInTime = findViewById(R.id.dashboard_checked_in_time_text);
        paymentNotification = findViewById(R.id.dashboard_payments_notification);
        paymentNotificationText = findViewById(R.id.dashboard_payments_notification_text);
        paymentNotification.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        paymentNotificationText.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        billingNotification = findViewById(R.id.dashboard_billing_notification);
        billingNotificationText = findViewById(R.id.dashboard_billing_notification_text);
        billingNotification.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        billingNotificationText.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        hoursNotification = findViewById(R.id.dashboard_hours_notification);
        hoursNotificationText = findViewById(R.id.dashboard_hours_notification_text);
        hoursNotification.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        hoursNotificationText.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        notificationNotification = findViewById(R.id.dashboard_notifications_notification);
        checkedInNotification = findViewById(R.id.dashboard_users_in_notification);
        inProgressNotification = findViewById(R.id.dashboard_in_progress_notification);
        userLoggedIn = findViewById(R.id.dashboard_username);
        userLoggedIn.setText(authentication.getUser().toString());
        Toolbar myToolbar = findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(myToolbar);
        Util.enactMultipleDatabaseOperationsPostExecute(this);
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(false);
    }

    public void startMainMenu(View view) {
        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
    }

    public void startUserSettings(View view) {
        Intent intent = new Intent(getApplicationContext(), UserSettings.class);
        startActivity(intent);
    }

    private void setNotifications(){
        String checkedInDateTime = "Checked in at: " + Util.convertLongToStringDateTime(authentication.getUser().getStartTime());
        if (authentication.getUser().getStartTime() != 0) {
            checkInTime.setText(checkedInDateTime);
        }
        else {
            checkInTime.setText("");
        }
        Util.enactMultipleDatabaseOperationsPostExecute(this);
    }

    private void createNotificationsFromData() {
        int counterVariable = 0;
        int anotherCounterVariable = 0;
        for (User u: users) {
            if(u.getStartTime() > 0) {
                counterVariable++;
            }

            anotherCounterVariable += u.getHours();
        }
        checkedInNotification.setText(String.format(Locale.US, "%d", counterVariable));
        if(counterVariable > 0) {
            checkedInNotification.setTextColor(RED);
        }
        else {
            checkedInNotification.setTextColor(GREEN);
        }
        if(Authentication.getAuthentication(this).getUser().isAdmin()) {
            hoursNotification.setText(String.format(Locale.US, "%d", anotherCounterVariable));
            if(anotherCounterVariable > 0) {
                hoursNotification.setTextColor(RED);
            }
            else {
                hoursNotification.setTextColor(GREEN);
            }
        }
        counterVariable = 0;
        anotherCounterVariable = 0;

        for (Customer c: customers) {
            for(Service s: c.getCustomerServices()) {
                if(s.isPause()) {
                    counterVariable++;
                    }
            }
        }
        inProgressNotification.setText(String.format(Locale.US, "%d", counterVariable));
        if(counterVariable > 0) {
            inProgressNotification.setTextColor(RED);
        }
        else {
            inProgressNotification.setTextColor(GREEN);
        }

    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_dashboard).setEnabled(false);
        return true;
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
    protected void onResume(){
        super.onResume();
        setNotifications();
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        AppDatabase db = AppDatabase.getAppDatabase(this);
        users = Util.USER_REFERENCE.retrieveAllClassInstancesFromDatabase(db);
        customers = Util.CUSTOMER_REFERENCE.retrieveAllClassInstancesFromDatabase(db);
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
    public void onPostExecute(List<User> databaseObjects) {
        createNotificationsFromData();
    }
}
