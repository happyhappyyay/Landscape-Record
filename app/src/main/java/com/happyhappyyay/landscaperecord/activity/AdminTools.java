package com.happyhappyyay.landscaperecord.activity;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.utility.Util;

public class AdminTools extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tools);
        Toolbar myToolbar = findViewById(R.id.admin_tools_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
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

    public void startViewWorkday(View view) {
        Intent intent = new Intent(this, ViewWorkDay.class);
        startActivity(intent);
    }

    public void startLogView(View view) {
        Intent intent = new Intent(this, ViewActivityLogs.class);
        startActivity(intent);
    }

    public void startSettings(View view) {
        Intent intent = new Intent(this, Settings.class);
        startActivity(intent);
    }
}
