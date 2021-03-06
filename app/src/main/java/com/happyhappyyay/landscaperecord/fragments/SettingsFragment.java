package com.happyhappyyay.landscaperecord.fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.CSVReadWrite;
import com.happyhappyyay.landscaperecord.utility.Loading;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;


public class SettingsFragment extends PreferenceFragment implements MultiDatabaseAccess<Customer> {
    private CSVReadWrite csvReadWrite;
    private List<Customer> customers;
    private List<Object> objects;
    private Preference preferenceLoad;

    private static List<Object> databaseAccessMethod(DatabaseOperator db){
        List<Object> objects = new ArrayList<>();
        objects.addAll(Util.CUSTOMER_REFERENCE.retrieveAllClassInstancesFromDatabase(db));
        objects.addAll(Util.LOG_REFERENCE.retrieveAllClassInstancesFromDatabase(db));
        objects.addAll(Util.USER_REFERENCE.retrieveAllClassInstancesFromDatabase(db));
        objects.addAll(Util.WORK_DAY_REFERENCE.retrieveAllClassInstancesFromDatabase(db));
        return objects;
    }

    public void performFileSearch() {
        Intent intent = new Intent()
                .setType("*/*")
                .setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(Intent.createChooser(intent, getString(R.string.settings_fragment_select_file)), 123);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_settings);
        csvReadWrite = new CSVReadWrite();
        Preference button = findPreference(getString(R.string.pref_key_import_contacts));
        button.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preferenceLoad == null) {
                    performFileSearch();
                }
                return true;
            }
        });
        Preference customerButton = findPreference(getString(R.string.pref_key_customer_export));
        customerButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preferenceLoad == null) {
                    Util.verifyStoragePermissions(getActivity());
                    csvReadWrite.csvWrite(CSVReadWrite.CUSTOMER_DATA, SettingsFragment.this, objects);
                }
                return true;
            }
        });
        Preference userButton = findPreference(getString(R.string.pref_key_user_export));
        userButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preferenceLoad == null) {
                    Util.verifyStoragePermissions(getActivity());
                    csvReadWrite.csvWrite(CSVReadWrite.EMPLOYEE_DATA, SettingsFragment.this, objects);
                }
                return true;
            }
        });
        Preference logButton = findPreference(getString(R.string.pref_key_log_export));
        logButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preferenceLoad == null) {
                    Util.verifyStoragePermissions(getActivity());
                    csvReadWrite.csvWrite(CSVReadWrite.LOG_DATA, SettingsFragment.this, objects);
                }
                return true;
            }
        });
        Preference servicesButton = findPreference(getString(R.string.pref_key_service_export));
        servicesButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preferenceLoad == null) {
                    Util.verifyStoragePermissions(getActivity());
                    csvReadWrite.csvWrite(CSVReadWrite.SERVICES_DATA, SettingsFragment.this, objects);
                }
                return true;
            }
        });
        Preference allButton = findPreference(getString(R.string.pref_key_all_export));
        allButton.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                if(preferenceLoad == null) {
                    Util.verifyStoragePermissions(getActivity());
                    csvReadWrite.csvWrite(CSVReadWrite.ALL_DATA, SettingsFragment.this, objects);
                }
                return true;
            }
        });
        Util.enactMultipleDatabaseOperations(this);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==123 && resultCode==RESULT_OK) {
            Uri selectedFile = data.getData();
            csvReadWrite.csvRead(selectedFile, getActivity());
            preferenceLoad = new Loading(getActivity());
            ((PreferenceCategory)findPreference(getString(R.string.pref_key_import_category))).addPreference(preferenceLoad);
            customers = csvReadWrite.getCustomers();
            insertCustomers(customers);
        }
    }

    private void insertCustomers(List<Customer> customers) {
            Util.insertObjects(this,Util.CUSTOMER_REFERENCE, customers);
    }

    @Override
    public Context getContext() {
        return getActivity();
    }

    @Override
    public String createLogInfo() {
        return "";
    }

    @Override
    public void onPostExecute(List<Customer> databaseObjects) {
        Toast.makeText(getActivity(), customers.size() + " " + getString(R.string.settings_fragment_contacts_add),
                Toast.LENGTH_SHORT).show();
        ((PreferenceCategory)findPreference(getString(R.string.pref_key_import_category))).removePreference(preferenceLoad);
        getActivity().finish();
    }

    @Override
    public void accessDatabaseMultipleTimes() {
        if(Util.hasOnlineDatabaseEnabledAndValid(getActivity())) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(getActivity());
                objects = databaseAccessMethod(db);
            } catch (Exception e) {
                AppDatabase db = AppDatabase.getAppDatabase(getActivity());
                objects = databaseAccessMethod(db);
            }
        }
        else {
            AppDatabase db = AppDatabase.getAppDatabase(getActivity());
            objects = databaseAccessMethod(db);
        }
    }

    @Override
    public void createCustomLog() {
    }
}
