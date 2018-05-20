package com.example.kingdenis.landscaperecord;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class Dashboard extends AppCompatActivity {
    private TextView userLoggedIn;
    private Authentication authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        authentication = authentication.getAuthentication(this);
        userLoggedIn = findViewById(R.id.dashboard_username);
        userLoggedIn.setText(authentication.getUser().getName());
        Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
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

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
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
//        switch (item.getItemId()) {
//            case R.id.menu_dashboard:
//                toolbarActions.goToDashboard();
//                return true;
//            case R.id.menu_settings:
//                toolbarActions.goToSettings();
//                return true;
//            case R.id.menu_logout:
//                  toolbarActions.goToLogout();
//                  return true;
//            default:
//                return super.onOptionsItemSelected(item);
//        }
    }
}
