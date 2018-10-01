package com.happyhappyyay.landscaperecord;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class EditUser extends AppCompatActivity {
    private AppDatabase db;
    private User user;
    private TextView usernameText;
    private EditText firstName, lastName, nickname;
    private CheckBox adminBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user);
        db = AppDatabase.getAppDatabase(this);
        usernameText = findViewById(R.id.edit_user_username);
        firstName = findViewById(R.id.edit_user_first_name);
        lastName = findViewById(R.id.edit_user_last_name);
        nickname = findViewById(R.id.edit_user_nickname);
        adminBox = findViewById(R.id.edit_user_admin_box);
        Intent intent = getIntent();
        String stringExtra = intent.getStringExtra("USER_REFERENCE");
        findUser(stringExtra);
    }

    private void findUser(String username) {

        new AsyncTask<String, Void, User>() {
            @Override
            protected User doInBackground(String... string) {

                return db.userDao().findUserByName(string[0]);
            }

            @Override
            protected void onPostExecute(User user) {
                EditUser.this.user = user;
                usernameText.setText(user.getName());
                if(user.isAdmin()) {
                    adminBox.setChecked(true);
                }
            }
        }.execute(username);
    }

    public void onSubmit(View view) {
        String firstName = this.firstName.getText().toString();
        String lastName = this.lastName.getText().toString();
        String nickname = this.nickname.getText().toString();

        if (!firstName.isEmpty() & !lastName.isEmpty())
        {
            user.setName(firstName + " " + lastName);
            if (adminBox.isChecked()) {
                user.setAdmin(true);
            } else {
                user.setAdmin(false);
            }

            if(!nickname.isEmpty()) {
//                user.setUsername(nickname);
            }
        }
    }
}
