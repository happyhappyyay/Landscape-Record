package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MainMenu extends AppCompatActivity {

    static AppDatabase db;
    private Button adminToolsButton;
    private Authentication authentication;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);
        Toolbar myToolbar = findViewById(R.id.main_menu_toolbar);
        setSupportActionBar(myToolbar);
        db = AppDatabase.getAppDatabase(this);
        authentication = Authentication.getAuthentication(this);
        user = authentication.getUser();
        adminToolsButton = findViewById(R.id.admin_tools_button);
        adminToolsButton.setVisibility(!user.isAdmin() ? View.GONE : View.VISIBLE);
    }

    public void startTimeReporting(View view) {
        Intent intent = new Intent(this, TimeReporting.class);
        startActivity(intent);
    }

    public void startPaymentReporting(View view) {
        Intent intent = new Intent(this, Payment.class);
        startActivity(intent);
    }

    public void startJobReporting(View view) {
        Intent intent = new Intent(this, JobServices.class);
        startActivity(intent);
    }

    public void startAdminTools(View view) {
        Intent intent = new Intent(this, AdminTools.class);
        startActivity(intent);
    }

    public void startNewContact(View view) {
        Intent intent = new Intent(this, NewContact.class);
        startActivity(intent);
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
