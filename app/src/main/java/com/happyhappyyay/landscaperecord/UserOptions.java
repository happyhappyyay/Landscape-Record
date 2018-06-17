package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class UserOptions extends AppCompatActivity {
    private AppDatabase db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_options);
        db = AppDatabase.getAppDatabase(this);
    }

    public void startAddUser(View view) {
        Intent intent = new Intent(this, AddUser.class);
        startActivity(intent);
    }

    public void startViewUser(View view) {
        Intent intent = new Intent(this, ViewUsers.class);
        startActivity(intent);
    }

    public void startDeleteUser(View view) {
        Intent intent = new Intent(this, DeleteUser.class);
        startActivity(intent);
    }
}
