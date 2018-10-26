package com.happyhappyyay.landscaperecord;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.util.List;

import static com.happyhappyyay.landscaperecord.TimeReporting.ADAPTER_POSITION;

public class DeleteUser extends AppCompatActivity implements AdapterView.OnItemSelectedListener,
        PopulateSpinner {
    private AppDatabase db;
    private Authentication authentication;
    private User user;
    private int adapterPosition;
    private List<User> users;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delete_user);
        Toolbar myToolbar = findViewById(R.id.delete_user_toolbar);
        setSupportActionBar(myToolbar);
        db = AppDatabase.getAppDatabase(this);
        authentication = Authentication.getAuthentication(this);
        if (savedInstanceState != null) {
            // Restore value of members from saved state
            adapterPosition = savedInstanceState.getInt(ADAPTER_POSITION);
        }

        Util.UserAccountsSpinner task = new Util.UserAccountsSpinner() {
            @Override
            protected void onPostExecute(List<User> dbUsers) {
                users = dbUsers;
            }
        };
        task.execute(this);
//        TODO: Recreate spinner util method
    }

    //    private void populateSpinner(List<User> users) {
//        String[] arraySpinner = new String[users.size()];
//        int pos = 0;
//        for (int i = 0; i < users.size(); i++) {
//            arraySpinner[i] = users.get(i).getName();
//            if(users.get(i).getName().equals(authentication.getUser().getName())) {
//                pos = i;
//            }
//        }
//
//        Spinner s = (Spinner) findViewById(R.id.delete_user_spinner);
//        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
//                R.layout.spinner_item, arraySpinner);
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        s.setAdapter(adapter);
//        s.setOnItemSelectedListener(this);
//        s.setSelection(pos);
//    }


    //    private void findAllUsers() {
//        new AsyncTask<Void, Void, List<User>>() {
//            @Override
//            protected List<User> doInBackground(Void... voids) {
//                users = db.userDao().getAllUsers();
//                return users;
//            }
//
//            @Override
//            protected void onPostExecute(List<User> users) {
//                populateSpinner(users);
//            }
//        }.execute();
//    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (users != null) {
            user = users.get(position);
            adapterPosition = position;
        } else {
            parent.setSelection(adapterPosition);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(ADAPTER_POSITION, adapterPosition);

        // call superclass to save any view hierarchy
        super.onSaveInstanceState(outState);
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
        return R.id.delete_user_spinner;
    }

    @Override
    public AdapterView.OnItemSelectedListener getItemSelectedListener() {
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.items, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return Util.toolbarItemSelection(this, item);
    }
}
