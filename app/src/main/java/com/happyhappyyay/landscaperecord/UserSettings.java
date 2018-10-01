package com.happyhappyyay.landscaperecord;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class UserSettings extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Authentication authentication;
        User user;
        TextView username;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_settings);
        authentication = Authentication.getAuthentication(this);
        user = authentication.getUser();
        username = findViewById(R.id.user_settings_username);
        username.setText(user.getName());
    }
}
