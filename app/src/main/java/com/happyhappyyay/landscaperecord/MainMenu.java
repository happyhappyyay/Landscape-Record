package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class MainMenu extends AppCompatActivity {

    private TextView checkedInButton, checkedOutButton;
    private Authentication authentication;
    private User user;

    static final String SETTINGS_USER = "pref_key_username";
    static final String SETTINGS_DB = "pref_key_dbname";
    static final String SETTINGS_API_KEY = "pref_key_api_key";
    static final String SETTINGS_API_SECRET = "pref_key_api_password";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView adminToolsButton;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        Toolbar myToolbar = findViewById(R.id.main_menu_toolbar);
        authentication = Authentication.getAuthentication();
        user = authentication.getUser();
        setSupportActionBar(myToolbar);
        adminToolsButton = findViewById(R.id.admin_tools_button);
        adminToolsButton.setVisibility(!user.isAdmin() ? View.GONE : View.VISIBLE);
        checkedInButton = findViewById(R.id.main_menu_full);
        checkedOutButton = findViewById(R.id.main_menu_empty);

        checkInButtonVisibility();
    }

    private void checkInButtonVisibility() {
        authentication = Authentication.getAuthentication();
        user = authentication.getUser();
        checkedOutButton.setVisibility(user.getStartTime() <= 0 ? View.GONE : View.VISIBLE);
        checkedInButton.setVisibility(user.getStartTime() <= 0 ? View.VISIBLE : View.GONE);
    }

    public void startTimeReporting(View view) {
        Intent intent = new Intent(this, TimeReporting.class);
        startActivity(intent);
    }

    public void startPaymentReporting(View view) {
        Intent intent = new Intent(this, ReceivePayment.class);
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
        Intent intent = new Intent(this, ViewContacts.class);
        startActivity(intent);
    }

    public void startViewWorkDay(View view) {
        Intent intent = new Intent(this, ViewWorkDay.class);
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

    @Override
    protected void onResume(){
        super.onResume();
        checkInButtonVisibility();
    }
}
