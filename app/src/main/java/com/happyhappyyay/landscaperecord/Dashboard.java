package com.happyhappyyay.landscaperecord;

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
import java.util.Locale;

public class Dashboard extends AppCompatActivity {
    private TextView userLoggedIn, checkInTime;
    private Authentication authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        authentication = Authentication.getAuthentication(this);
        checkInTime = findViewById(R.id.dashboard_checked_in_time_text);
        userLoggedIn = findViewById(R.id.dashboard_username);
        userLoggedIn.setText(authentication.getUser().toString());
        Toolbar myToolbar = findViewById(R.id.dashboard_toolbar);
        setSupportActionBar(myToolbar);
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
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.menu_dashboard).setEnabled(false);
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        return Util.toolbarItemSelection(this, item);
    }

    @Override
    protected void onResume(){
        super.onResume();
        setNotifications();
    }
}
