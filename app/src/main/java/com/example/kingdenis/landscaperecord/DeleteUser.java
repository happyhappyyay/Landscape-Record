package com.example.kingdenis.landscaperecord;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import java.util.List;

public class DeleteUser extends AppCompatActivity implements AdapterView.OnItemSelectedListener {
    private AppDatabase db;
    private Authentication authentication;
    private User user;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        db = AppDatabase.getAppDatabase(this);
        authentication = Authentication.getAuthentication(this);
        findAllUsers();
    }

    private void populateSpinner(List<User> users) {
        String[] arraySpinner = new String[users.size()];
        int pos = 0;
        for (int i = 0; i < users.size(); i++) {
            arraySpinner[i] = users.get(i).getName();
            if(users.get(i).getName().equals(authentication.getUser().getName())) {
                pos = i;
            }
        }

        Spinner s = (Spinner) findViewById(R.id.delete_user_spinner);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(this);
        s.setSelection(pos);
    }
//TODO: try static
    private void deleteUser(User user) {
        new AsyncTask<User, Void, Void>() {
            @Override
            protected Void doInBackground(User... params) {
                User user = params[0];
                db.userDao().deleteUser(user);
                finish();
                return null;
            }
        }.execute(user);
    }
    private void findAllUsers() {
        new AsyncTask<Void, Void, List<User>>() {
            @Override
            protected List<User> doInBackground(Void... voids) {
                users = db.userDao().getAllUsers();
                return users;
            }

            @Override
            protected void onPostExecute(List<User> users) {
                populateSpinner(users);
            }
        }.execute();
    }
    public void onDeleteUser(View view) {
        if (user != null) {
            DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    switch (which){
                        case DialogInterface.BUTTON_POSITIVE:
                            deleteUser(user);
                            break;

                        case DialogInterface.BUTTON_NEGATIVE:
                            break;
                    }
                }
            };

            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Are you sure you want to delete user " + user.getName() + " ?")
                    .setPositiveButton("Yes", dialogClickListener)
                    .setNegativeButton("No", dialogClickListener).show();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        user = users.get(position);
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
