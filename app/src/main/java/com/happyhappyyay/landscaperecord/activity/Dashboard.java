package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.database_interface.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;
import java.util.Locale;

import static android.graphics.Color.GREEN;
import static android.graphics.Color.RED;

public class Dashboard extends AppCompatActivity implements MultiDatabaseAccess<User> {
    private TextView checkInTime, paymentNotification, billingNotification,
            hoursNotification, notificationNotification, checkedInNotification, inProgressNotification;
    private List<User> users;
    private List<Customer> customers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        User user = Authentication.getAuthentication().getUser();
        TextView userLoggedIn;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        checkInTime = findViewById(R.id.dashboard_checked_in_time_text);
        paymentNotification = findViewById(R.id.dashboard_payments_notification);
        TextView paymentNotificationText = findViewById(R.id.dashboard_payments_notification_text);
        paymentNotification.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        paymentNotificationText.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        billingNotification = findViewById(R.id.dashboard_billing_notification);
        TextView billingNotificationText = findViewById(R.id.dashboard_billing_notification_text);
        billingNotification.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        billingNotificationText.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        hoursNotification = findViewById(R.id.dashboard_hours_notification);
        TextView hoursNotificationText = findViewById(R.id.dashboard_hours_notification_text);
        hoursNotification.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        hoursNotificationText.setVisibility(!user.isAdmin() ? View.INVISIBLE : View.VISIBLE);
        notificationNotification = findViewById(R.id.dashboard_notifications_notification);
        checkedInNotification = findViewById(R.id.dashboard_users_in_notification);
        inProgressNotification = findViewById(R.id.dashboard_in_progress_notification);
        userLoggedIn = findViewById(R.id.dashboard_username);
        userLoggedIn.setText(Authentication.getAuthentication().getUser().toString());
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
        Intent intent = new Intent(getApplicationContext(), UserSetting.class);
        startActivity(intent);
    }

    public void startBillCreation(View view) {
        Intent intent = new Intent(getApplicationContext(), BillCreation.class);
        startActivity(intent);
    }

    public void startPayments(View view) {
        Intent intent = new Intent(getApplicationContext(), ReceivePayment.class);
        startActivity(intent);
    }

    public void startManageUsers(View view) {
        Intent intent = new Intent(getApplicationContext(), ViewUsers.class);
        startActivity(intent);
    }

    public void startViewServices(View view) {
        Intent intent = new Intent(getApplicationContext(), ViewServices.class);
        startActivity(intent);
    }

    public void startTimeReporting(View view) {
        Intent intent = new Intent(getApplicationContext(), TimeReporting.class);
        startActivity(intent);
    }


    private void setNotifications(){
        String checkedInDateTime = "Checked in at: " + Util.convertLongToStringDateTime(Authentication.getAuthentication().getUser().getStartTime());
        if (Authentication.getAuthentication().getUser().getStartTime() != 0) {
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
        if(Authentication.getAuthentication().getUser().isAdmin()) {
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
