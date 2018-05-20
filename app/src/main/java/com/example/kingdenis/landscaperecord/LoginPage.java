package com.example.kingdenis.landscaperecord;

import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class LoginPage extends AppCompatActivity {

    private AppDatabase db;
    private EditText username;
    private EditText password;
    private Authentication authentication;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);
        db = AppDatabase.getAppDatabase(this);
        authentication = authentication.getAuthentication(this);
        username = findViewById(R.id.login_page_username);
        password = findViewById(R.id.login_page_password);
    }

    public void attemptLogin(View view) {
        if (!username.getText().toString().isEmpty()) {
            loginUser();
        }

//        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
//        startActivity(intent);

//        try {
//            // Simulate network access.
//            Thread.sleep(500);
//        } catch (InterruptedException e) {
//
//        }
    }

    public void skipLogin(View view) {
        Intent intent = new Intent(getApplicationContext(), MainMenu.class);
        startActivity(intent);
    }

    private void loginUser() {
        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... voids) {

                return db.userDao().findUserByName(username.getText().toString());
            }

            protected void onPostExecute(User user) {
                boolean authorized = false;
                if (user != null) {
                    if(user.getPassword().equals(password.getText().toString())) {
                        authorized = true;
                        authentication.setUser(user);
                        Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                        startActivity(intent);
                    }
                }
                if(!authorized) {
                    username.setText("");
                    password.setText("");
                    Toast.makeText(getApplicationContext(), "Could not login with the provided " +
                            "credentials", Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }
}
