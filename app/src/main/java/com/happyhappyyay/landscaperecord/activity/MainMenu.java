package com.happyhappyyay.landscaperecord.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

public class MainMenu extends AppCompatActivity {

    private TextView checkedInButton;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        TextView adminToolsButton;

        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main_menu);
        Toolbar myToolbar = findViewById(R.id.main_menu_toolbar);
        user = Authentication.getAuthentication().getUser();
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        adminToolsButton = findViewById(R.id.admin_tools_button);
        adminToolsButton.setVisibility(!user.isAdmin() ? View.GONE : View.VISIBLE);
        TextView userText = findViewById(R.id.main_menu_users);
        userText.setVisibility(!user.isAdmin() ? View.GONE : View.VISIBLE);
        checkedInButton = findViewById(R.id.main_menu_full);
        checkInButtonVisibility();
    }

    private void checkInButtonVisibility() {
        user = Authentication.getAuthentication().getUser();
        checkedInButton.setCompoundDrawablesWithIntrinsicBounds((user.getStartTime() <= 0 ?
                R.drawable.ic_twotone_hourglass_empty_64px : R.drawable.ic_twotone_hourglass_full_64px),
                0, 0, 0);
    }

    public void startTimeReporting(View view) {
        Intent intent = new Intent(this, TimeReporting.class);
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

    public void startViewCustomers(View view) {
        Intent intent = new Intent(this, ViewCustomers.class);
        startActivity(intent);
    }

    public void startViewUsers(View view) {
        Intent intent = new Intent(this, ViewUsers.class);
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
