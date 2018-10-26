package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class EditUser extends AppCompatActivity implements DatabaseAccess<User> {
    private User user;
    private TextView usernameText;
    private EditText firstName, lastName, nickname;
    private CheckBox adminBox;
    private String logInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        usernameText = findViewById(R.id.edit_user_username);
        firstName = findViewById(R.id.edit_user_first_name);
        lastName = findViewById(R.id.edit_user_last_name);
        nickname = findViewById(R.id.edit_user_nickname);
        adminBox = findViewById(R.id.edit_user_admin_box);
        Intent intent = getIntent();
        int intExtra = intent.getIntExtra("USER_ID", 0);
        findUser(intExtra);
    }

    private void findUser(int userID) {
        Util.findObjectByID(this, Util.USER_REFERENCE, userID);
//        new AsyncTask<String, Void, User>() {
//            @Override
//            protected User doInBackground(String... string) {
//
//                return db.userDao().findUserByName(string[0]);
//            }
//
//            @Override
//            protected void onPostExecute(User user) {
//                EditUser.this.user = user;
//                usernameText.setText(user.getName());
//                if(user.isAdmin()) {
//                    adminBox.setChecked(true);
//                }
//            }
//        }.execute(username);
    }

    public void onSubmit(View view) {
        String firstName = this.firstName.getText().toString();
        String lastName = this.lastName.getText().toString();
        String nickname = this.nickname.getText().toString();

        if (!firstName.isEmpty() & !lastName.isEmpty())
        {
            user.setName(firstName + " " + lastName);
            user.setAdmin(adminBox.isChecked());
        }
        if(!nickname.isEmpty()) {
            user.setNickname(nickname);
        }
        logInfo = user.getName();

        Util.updateObject(this, Util.USER_REFERENCE, user);
        finish();
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return logInfo;
    }

    @Override
    public void onPostExecute(List<User> databaseObjects) {
        if(databaseObjects != null) {
            user = databaseObjects.get(0);
            usernameText.setText(user.getName());
            if(user.isAdmin()) {
                adminBox.setChecked(true);
            }
        }
    }

}
