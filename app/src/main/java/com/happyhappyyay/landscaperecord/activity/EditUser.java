package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.User;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.List;

public class EditUser extends AppCompatActivity implements DatabaseAccess<User> {
    private User user;
    private TextView usernameText;
    private EditText firstName, lastName, nickname;
    private CheckBox adminBox;
    private String logInfo;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        Toolbar myToolbar = findViewById(R.id.edit_user_toolbar);
        setSupportActionBar(myToolbar);
        boolean admin = Authentication.getAuthentication().getUser().isAdmin();
        if(getSupportActionBar() != null & admin) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        usernameText = findViewById(R.id.edit_user_username);
        firstName = findViewById(R.id.edit_user_first_name);
        lastName = findViewById(R.id.edit_user_last_name);
        nickname = findViewById(R.id.edit_user_nickname);
        adminBox = findViewById(R.id.edit_user_admin_box);
        adminBox.setVisibility(admin? View.VISIBLE: View.INVISIBLE);
        progressBar = findViewById(R.id.edit_user_progress_bar);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("USER_ID");
        findUser(stringExtra);
    }

    private void findUser(String userID) {
        Util.findObjectByID(this, Util.USER_REFERENCE, userID);
    }

    public void onSubmit(View view) {
        if(progressBar.getVisibility() == View.INVISIBLE) {
            String firstName = this.firstName.getText().toString();
            String lastName = this.lastName.getText().toString();
            String nickname = this.nickname.getText().toString();

            if (!firstName.isEmpty() & !lastName.isEmpty()) {
                user.setFirst(firstName);
                user.setLast(lastName);
                user.setName(firstName +" " + lastName);
                user.setAdmin(adminBox.isChecked());
            }
            if (!nickname.isEmpty()) {
                user.setNickname(nickname);
            }
            logInfo = user.getName();
            progressBar.setVisibility(View.VISIBLE);
            Util.updateObject(this, Util.USER_REFERENCE, user);
            finish();
        }
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
