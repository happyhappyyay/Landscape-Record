package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.pref_settings);
        Preference button = findPreference(getString(R.string.pref_key_import_contacts));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                performFileSearch();
                return true;
            }
        });
    }

    public void performFileSearch() {

        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, "Select a file"), 123);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode==RESULT_OK) {
            Uri selectedFile = data.getData();
            CSVReadWrite csvReadWrite = new CSVReadWrite(selectedFile, getActivity());
            List<Customer> customers = csvReadWrite.getCustomers();
            insertCustomers(customers.toArray(new Customer[customers.size()]));
            Toast.makeText(getActivity(), customers.size() + " contacts added", Toast.LENGTH_SHORT).show();

        }
    }

    private void insertCustomers(Customer... customers) {
        for(final Customer c: customers) {
            DatabaseAccess<Customer> customerDatabaseAccess = new DatabaseAccess<Customer>() {
                @Override
                public Context getContext() {
                    return getActivity();
                }

                @Override
                public String createLogInfo() {
                    return "IMPORT " + c.getName();
                }

                @Override
                public void onPostExecute(List<Customer> databaseObjects) {

                }
            };
            Util.insertObject(customerDatabaseAccess,Util.CUSTOMER_REFERENCE, c);        }
    }
}
