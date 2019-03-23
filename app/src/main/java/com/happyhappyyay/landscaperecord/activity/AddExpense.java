package com.happyhappyyay.landscaperecord.activity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.enums.LogActivityAction;
import com.happyhappyyay.landscaperecord.enums.LogActivityType;
import com.happyhappyyay.landscaperecord.interfaces.DatabaseOperator;
import com.happyhappyyay.landscaperecord.interfaces.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.pojo.Expense;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.WorkDay;
import com.happyhappyyay.landscaperecord.utility.AppDatabase;
import com.happyhappyyay.landscaperecord.utility.Authentication;
import com.happyhappyyay.landscaperecord.utility.OnlineDatabase;
import com.happyhappyyay.landscaperecord.utility.Util;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class AddExpense extends AppCompatActivity implements MultiDatabaseAccess<Expense>, AdapterView.OnItemSelectedListener {
    private int paymentPosition, adapterPosition;
    private EditText number, date, type;
    private Spinner spinner;
    private CheckBox checkBox;
    private Button button;
    private Expense expense;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_expense);
        checkBox = findViewById(R.id.add_expense_check);
        button = findViewById(R.id.add_expense_add);
        date = findViewById(R.id.add_expense_date);
        date.setText(Util.retrieveStringCurrentDate());
        number = findViewById(R.id.add_expense_number);
        spinner = findViewById(R.id.add_expense_spinner);
        type = findViewById(R.id.add_expense_type);
        Toolbar myToolbar = findViewById(R.id.add_expense_toolbar);
        setSupportActionBar(myToolbar);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        changeAddTypeVisibility();
        populateSpinner();
    }

    public void checkClick(View view) {
        number.setVisibility(View.VISIBLE);
        number.setHint(R.string.view_expenses_check);
        paymentPosition = 3;
    }

    public void debitClick(View view) {
        number.setVisibility(View.VISIBLE);
        if(paymentPosition == 3) {
            number.setHint(R.string.view_expenses_four);
        }
        paymentPosition = 2;
    }

    public void creditClick(View view) {
        number.setVisibility(View.VISIBLE);
        if(paymentPosition == 3) {
            number.setHint(R.string.view_expenses_four);
        }
        paymentPosition = 1;
    }

    public void cashClick(View view) {
        number.setVisibility(View.INVISIBLE);
        paymentPosition = 0;
    }

    public void onSubmit (View view){
        EditText name = findViewById(R.id.add_expense_name);
        EditText price = findViewById(R.id.add_expense_price);
        String numberText = number.getText().toString();
        int pType = 0;
        boolean error = false;
        if(paymentPosition != 0) {
            if(paymentPosition != 3) {
                if(numberText.length() != 4 && numberText.length() != 0) {
                    Toast.makeText(this, R.string.add_expense_four_error, Toast.LENGTH_LONG).show();
                    error = true;
                }
            }
        }
        if(name.getText().toString().isEmpty() && !error){
            Toast.makeText(this, R.string.add_expense_four_error, Toast.LENGTH_LONG).show();
            error = true;

        }
        if(!Util.checkDateFormat(date.getText().toString()) && !error){
            Toast.makeText(this, R.string.incorrect_date_format, Toast.LENGTH_LONG).show();
            error = true;
        }
        if(!error){
            switch (paymentPosition) {
                case 0:
                    pType = R.string.payment_cash;
                    break;
                case 1:
                    pType = R.string.activity_add_expense_credit;
                    break;
                case 2:
                    pType = R.string.activity_add_expense_debit;
                    break;
                case 3:
                    pType = R.string.payment_check;
                    break;
            }
            if(pType==0) {
                error = true;
            }
        }

        if(!error){
            try {
                double amount = Double.parseDouble(price.getText().toString());
                expense = new Expense(name.getText().toString(), Util.convertStringDateToMilliseconds(date.getText().toString()), amount,
                        spinner.getSelectedItem().toString(), getString(pType) + " " + numberText);
                Util.enactMultipleDatabaseOperationsPostExecute(this);
            }
            catch(Exception e){
                e.printStackTrace();
                Toast.makeText(this, R.string.add_expense_correct_double_format, Toast.LENGTH_LONG).show();
            }

        }
    }

    private void changeAddTypeVisibility() {
        if(checkBox.isChecked()) {
            type.setVisibility(View.VISIBLE);
            button.setVisibility(View.VISIBLE);
        }
        else {
            type.setVisibility(View.GONE);
            button.setVisibility(View.GONE);
        }
    }

    public void onCheckClick(View view){
        changeAddTypeVisibility();
    }

    public void addClick(View view) {
        String typeText = type.getText().toString();
        if(!typeText.isEmpty()){
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
            Set<String> strings = sharedPref.getStringSet(getString(R.string.pref_key_expense_type), new HashSet<String>());
            strings.add(typeText);
            sharedPref.edit().putStringSet(getString(R.string.pref_key_expense_type), strings).apply();
            populateSpinner();
            checkBox.setChecked(false);
            changeAddTypeVisibility();
        }
    }

    private void populateSpinner() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        Set<String> strings = sharedPref.getStringSet(getString(R.string.pref_key_expense_type), new HashSet<String>());
        String[] arraySpinner = new String[strings.size()];
        int count = 0;
        for (String temp : strings) {
            arraySpinner[count] = temp;
            count++;
        }

        int pos = 0;

        int layoutReference = R.layout.spinner_item;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this,
                layoutReference, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(this);
        spinner.setSelection(pos);
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String createLogInfo() {
        return expense.getName();
    }

    @Override
    public void onPostExecute(List<Expense> databaseObjects) {
        Toast.makeText(this, R.string.add_expense_add, Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        adapterPosition = i;
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void accessDatabaseMultipleTimes() {
        if(Util.hasOnlineDatabaseEnabledAndValid(this)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
                databaseAccessMethod(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AppDatabase db = AppDatabase.getAppDatabase(this);
        databaseAccessMethod(db);
    }

    private void databaseAccessMethod(DatabaseOperator db) {
        WorkDay workDay;
        String name = Authentication.getAuthentication().getUser().getName();
        Util.EXPENSE_REFERENCE.insertClassInstanceFromDatabase(db,expense);
        WorkDay tempWorkDay = Util.WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseString(db, Util.retrieveStringCurrentDate());
        if (tempWorkDay != null) {
            workDay = tempWorkDay;
        } else {
            workDay = new WorkDay(Util.retrieveStringCurrentDate());
            Util.WORK_DAY_REFERENCE.insertClassInstanceFromDatabase(db, workDay);
            LogActivity log = new LogActivity(name,
                    "Workday: " + Util.retrieveStringCurrentDate(), LogActivityAction.ADD.ordinal(),
                    LogActivityType.WORKDAY.ordinal());
            log.setObjId(workDay.getId());
            Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db,log);
        }
        workDay.addExpense(name, expense);
        Util.WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(db, workDay);
    }

    @Override
    public void createCustomLog() {
        if(Util.hasOnlineDatabaseEnabledAndValid(this)) {
            try {
                OnlineDatabase db = OnlineDatabase.getOnlineDatabase(this);
                customLogMethod(db);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        AppDatabase db = AppDatabase.getAppDatabase(this);
        customLogMethod(db);
    }

    private void customLogMethod(DatabaseOperator db) {
        LogActivity log = new LogActivity(Authentication.getAuthentication().getUser().getName(),
                expense.getName(), LogActivityAction.ADD.ordinal(),
                LogActivityType.EXPENSE.ordinal());
        log.setObjId(expense.getId());
        Util.LOG_REFERENCE.insertClassInstanceFromDatabase(db,log);
    }
}
