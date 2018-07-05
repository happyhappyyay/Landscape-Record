package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
}
