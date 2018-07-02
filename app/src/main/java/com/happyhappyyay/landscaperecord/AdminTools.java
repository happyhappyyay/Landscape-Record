package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class AdminTools extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tools);
    }

    public void startUserOptions(View view) {
        Intent intent = new Intent(this, UserOptions.class);
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
}
