package com.happyhappyyay.landscaperecord;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class AddUser extends AppCompatActivity {
    private EditText firstName, lastName, password, hours;
    private Button submit;
    private User user;
    private AppDatabase db;
    private CheckBox admin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);
        firstName = findViewById(R.id.add_user_first);
        lastName = findViewById(R.id.add_user_last);
        password = findViewById(R.id.add_user_pass);
        hours = findViewById(R.id.add_user_hours);
        submit = findViewById(R.id.add_user_submit);
        admin = findViewById(R.id.add_user_admin_button);
        db = AppDatabase.getAppDatabase(this);
    }

    public void addNewUser(View view) {
        boolean error = false;
        if (!firstName.getText().toString().isEmpty() & !lastName.getText().toString().isEmpty()) {
            user = new User();
            user.setName(firstName.getText().toString() + " " + lastName.getText().toString());
            if (password.getText().toString().length() > 5) {
                user.setPassword(password.getText().toString());
            } else {
                if (!error) {
                    Toast.makeText(getApplicationContext(), "Password must be at least 6 characters" +
                            " long", Toast.LENGTH_LONG).show();
                    error = true;
                }
                password.setText("");
            }
            if (hours.getText().toString().isEmpty()) {
                user.setHours(0);
            } else {
                if (!error) {
                    try {
                        user.setHours(Double.parseDouble(hours.getText().toString()));

                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), "Hours must be entered as whole" +
                                " (e.g., 5) or real (e.g., 5.0) numbers", Toast.LENGTH_LONG).show();
                        hours.setText("");
                        error = true;
                    }
                }

            }
            if (admin.isChecked()) {
                user.setAdmin(true);
            } else {
                user.setAdmin(false);
            }
            if (!error) {
                insertUser();
            }

        }

    }

    private void insertUser() {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... voids) {
                LogActivity log = new LogActivity("User", user.getName(),0, 0);
                db.userDao().insert(user);
                db.logDao().insert(log);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                Toast.makeText(getApplicationContext(), "User account for " + user.getName() +
                        " created.", Toast.LENGTH_LONG).show();


                finish();
            }
        }.execute();
    }
}
