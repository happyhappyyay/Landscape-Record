package com.happyhappyyay.landscaperecord;

import android.app.Activity;
import android.content.Context;
import android.content.CursorLoader;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

public class Util {
    private static final String TAG = "selected";

    public static boolean toolbarItemSelection(Context context, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                goToNewContact(context);
                return true;
            case R.id.menu_add_service:
                goToQuickSheet(context);
                return true;
            case R.id.menu_dashboard:
                goToDashboard(context);
                return true;
            case R.id.menu_settings:
                goToSettings(context);
                return true;
            case R.id.menu_logout:
                goToLogout(context);
                return true;
            default:
                return false;
        }
    }

    private static void goToDashboard(Context context) {
        Intent intent = new Intent(context, Dashboard.class);
        context.startActivity(intent);
    }

    private static void goToSettings(Context context) {
        Intent intent = new Intent(context, Settings.class);
        context.startActivity(intent);
    }

    private static void goToLogout(Context context) {
        Intent intent = new Intent(context, LoginPage.class);
        context.startActivity(intent);
        Toast.makeText(context.getApplicationContext(), "Logged out! ", Toast.LENGTH_LONG).show();
    }

    private static void goToNewContact(Context context) {
        Intent intent = new Intent(context, NewContact.class);
        context.startActivity(intent);
    }

    private static void goToQuickSheet(Context context) {
        Intent intent = new Intent(context, QuickSheet.class);
        context.startActivity(intent);
    }

    public static boolean checkDateFormat(String date) {
        try {
            int month = Integer.parseInt(date.substring(0, 2));
            int day = Integer.parseInt(date.substring(3, 5));
            int year = Integer.parseInt(date.substring(6, 10));
            if (month > 0 & month < 13 & day > 0 & day < 31 & year > 2000 & year < 2100) {
                Calendar calendar = new GregorianCalendar(year, month, day);
                 if (day <= calendar.getActualMaximum(Calendar.DAY_OF_MONTH)) {
                     return true;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;

    }

    /* Loads spinner with list items and sets initial item of list */
    private static <T extends SpinnerObjects> void populateSpinner(PopulateSpinner populateSpinner, List<T> objects) {
        Authentication authentication = populateSpinner.getAuthentication();
        Activity activity = populateSpinner.getActivity();
        int viewID = populateSpinner.getViewID();
        AdapterView.OnItemSelectedListener listener = populateSpinner.getItemSelectedListener();

        String[] arraySpinner = new String[objects.size()];
        int pos = 0;
        for (int i = 0; i < objects.size(); i++) {
            arraySpinner[i] = objects.get(i).getName();
            if (objects.get(i).getName().equals(authentication.getUser().getName())) {
                pos = i;
            }
        }

        Spinner s = activity.findViewById(viewID);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(activity,
                R.layout.spinner_item, arraySpinner);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        s.setAdapter(adapter);
        s.setOnItemSelectedListener(listener);
        s.setSelection(pos);
    }

    //Asynchronous task for Users
    public static class UserAccountsSpinner extends AsyncTask<PopulateSpinner, Void, List<User>> {

        @Override
        protected List<User> doInBackground(PopulateSpinner... params) {
            Context context = params[0].getActivity().getApplicationContext();
            AppDatabase db = AppDatabase.getAppDatabase(context);
            List<User> users = db.userDao().getAllUsers();
            populateSpinner(params[0], users);
            return users;
        }

    }

    public static String retrieveStringCurrentDate() {
        return new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date(System.currentTimeMillis()));
    }

    public static long retrieveLongCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        Date date;
        try {
            date = dateFormat.parse(retrieveStringCurrentDate());
            return date.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static long convertStringDateToMilliseconds(String sDate) {
        long currentDateAsTime = 0;
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);

        try {
            Date date = dateFormat.parse(sDate);
            currentDateAsTime = date.getTime();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        return currentDateAsTime;
    }

    public static String convertLongToStringDate(long time) {

        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
        return dateFormat.format(new Date(time));
    }

    public static long convertStringToFirstDayOfWeekMilli(String dateString) {
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = dateFormat.parse(dateString);
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_WEEK, cal.getFirstDayOfWeek());
            return cal.getTimeInMillis();
        }
        catch (Exception e) {
            return 0;
        }
    }

    public static long convertStringToFirstDayOfMonthMilli(String dateString) {
        Calendar cal = Calendar.getInstance();
        try {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy", Locale.US);
            Date date = dateFormat.parse(dateString);
            cal.setTime(date);
            cal.set(Calendar.DAY_OF_MONTH, cal.getFirstDayOfWeek());
            return cal.getTimeInMillis();
        }
        catch (Exception e) {
            return 0;
        }
    }
    public static String getRealPathFromURI(Uri contentUri, Context context) {
        String[] proj = {MediaStore.Images.Media.DATA};
        CursorLoader loader = new CursorLoader(context, contentUri, proj, null, null, null);
        Cursor cursor = loader.loadInBackground();
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }

}
