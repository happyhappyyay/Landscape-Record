package com.example.kingdenis.landscaperecord;

import android.app.Activity;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class ViewUsers extends AppCompatActivity implements PopulateSpinner,
        AdapterView.OnItemSelectedListener {
    private EditText textField;
    private TextView name, password, hours;
    private AppDatabase db;
    private Authentication authentication;
    private List<User> users;
    private User user;
    private final int VIEW_ID = R.id.view_user_spinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);
        db = AppDatabase.getAppDatabase(this);
        name = findViewById(R.id.view_user_name);
        password = findViewById(R.id.view_user_password);
        hours = findViewById(R.id.view_user_hours);
        authentication = Authentication.getAuthentication(this);
        Util.UserAccountsSpinner task = new Util.UserAccountsSpinner() {
            @Override
            protected void onPostExecute(List<User> dbUsers) {
                users = dbUsers;
            }
        };
        task.execute(this);
//        textField.setText(authentication.getUser().getName());
    }

//    TODO: make static
    private void findUser() {
        new AsyncTask<Void, Void, User>() {
            @Override
            protected User doInBackground(Void... voids) {

                return db.userDao().findUserByName(textField.getText().toString());
            }

            @Override
            protected void onPostExecute(User user) {
                if (user != null) {
                    name.setText("a");
                    password.setText(user.getPassword());
                    hours.setText(Double.toString(user.getHours()));
                }
                else {
                    Toast.makeText(getApplicationContext(), "Could not find " + textField.getText()
                            .toString(), Toast.LENGTH_LONG).show();
                }
            }
        }.execute();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        user = users.get(position);
        name.setText(user.getName());
        password.setText(user.getPassword());
        hours.setText(Double.toString(user.getHours()));
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public Authentication getAuthentication() {
        return authentication;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public int getViewID() {
        return VIEW_ID;
    }

    @Override
    public AdapterView.OnItemSelectedListener getItemSelectedListener() {
        return this;
    }
}
