package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class AdminTools extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tools);
        Toolbar myToolbar = findViewById(R.id.admin_tools_toolbar);
        setSupportActionBar(myToolbar);
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

    public void startUserOptions(View view) {
        Intent intent = new Intent(this, ViewUsers.class);
        startActivity(intent);
    }

    public void startViewServices(View view) {
        Intent intent = new Intent(this, ViewServices.class);
        startActivity(intent);
    }

    public void startHourOperations(View view) {
        Intent intent = new Intent(this, HourOperations.class);
        startActivity(intent);
    }

    public void startLogView(View view) {
        Intent intent = new Intent(this, ViewActivityLogs.class);
        startActivity(intent);
    }

    public void startPaymentQueue(View view) {

    }
}
