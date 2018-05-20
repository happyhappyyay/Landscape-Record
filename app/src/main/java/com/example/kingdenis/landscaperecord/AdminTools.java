package com.example.kingdenis.landscaperecord;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class AdminTools extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tools);
    }

    public void startUserOptions(View view) {
        Intent intent = new Intent (this, UserOptions.class);
        startActivity(intent);
    }

    public void startHourOperations(View view) {
        Intent intent = new Intent (this, HourOperations.class);
        startActivity(intent);

    }
}
