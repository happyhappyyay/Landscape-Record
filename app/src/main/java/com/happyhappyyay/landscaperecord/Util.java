package com.happyhappyyay.landscaperecord;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.happyhappyyay.landscaperecord.OnlineDatabase.LOG;
import static com.happyhappyyay.landscaperecord.OnlineDatabase.convertFromObjectToDocument;

public class Util {
    public static final Customer CUSTOMER_REFERENCE = new Customer();
    public static final LogActivity LOG_REFERENCE = new LogActivity();
    public static final WorkDay WORK_DAY_REFERENCE = new WorkDay(retrieveStringCurrentDate());
    public static final User USER_REFERENCE = new User();
    public static final String DELIMITER = "|";

    private static final String TAG = "selected";

    public static boolean toolbarItemSelection(Context context, MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_contact:
                gotToContacts(context);
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

    public static Authentication getAuthentication() {
       return Authentication.getAuthentication();
    }

    private static boolean hasOnlineDatabaseEnabled(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean("pref_settings_database_usage", false);
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

    private static void gotToContacts(Context context) {
        Intent intent = new Intent(context, ViewContacts.class);
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
    private static <T extends DatabaseObjects> void populateSpinner(PopulateSpinner populateSpinner, List<T> objects) {
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

    public static int retrieveMonthFromLong(Long time) {
        DateFormat dateFormat = new SimpleDateFormat("MM", Locale.US);
        String monthString = dateFormat.format(new Date(time));
        return Integer.parseInt(monthString);
    }

    public static String convertLongToStringDateTime(long time) {
        DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm", Locale.US);
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

    public static List<String> removeCustomerServicesStopCharacters(List<Service> services) {
        List<String> customerServices = new ArrayList<>();
        for (Service s: services) {
            String serviceString = s.getServices();
            String serviceStringWithoutSeparators = "";
            int endServicePosition;
            int startServicePosition = 0;

            for (int i = 0; i < serviceString.length() - 2; i++) {
                if (serviceString.substring(i,i+3).equals("#*#")) {
                    endServicePosition = i;
                    serviceStringWithoutSeparators = serviceStringWithoutSeparators + serviceString.substring(startServicePosition, endServicePosition) + ", ";
                    startServicePosition = i+3;
                }
            }

            if (serviceStringWithoutSeparators.length() > 2) {
                serviceStringWithoutSeparators = serviceStringWithoutSeparators.substring(0, serviceStringWithoutSeparators.length()-2);
            }
            String customerService = s.convertEndTimeToDateString() + ": "  + s.getCustomerName() +
                    System.getProperty ("line.separator") + serviceStringWithoutSeparators;
            customerServices.add(customerService);
        }
        return customerServices;
    }

    public static <T extends DatabaseObjects<T>> void findAllObjects(final DatabaseAccess<T> access, final DatabaseObjects<T> object)
    {
        new AsyncTask<Void, Void, List<T>>() {
            List<T> dbObjs;
            @Override
            protected List<T> doInBackground(Void... Voids) {
                if(hasOnlineDatabaseEnabled(access.getContext())) {
                    try {
                        OnlineDatabase db = OnlineDatabase.getOnlineDatabase(access.getContext());
                        dbObjs = object.retrieveAllClassInstancesFromDatabase(db);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(dbObjs == null) {
                    AppDatabase db = AppDatabase.getAppDatabase(access.getContext());
                    dbObjs = object.retrieveAllClassInstancesFromDatabase(db);
                }
                return dbObjs;
            }

            @Override
            protected void onPostExecute(List<T> objects) {
                access.onPostExecute(objects);
            }
        }.execute();
    }

    public static <T extends DatabaseObjects<T>> void deleteObject(final DatabaseAccess<T> access, final DatabaseObjects<T> object, final T objectToDelete)
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... Voids) {
                Authentication authentication = Util.getAuthentication();
                LogActivityType logType = findLogTypeInt(access,object);
                LogActivity log = new LogActivity(authentication.getUser().getName(), access.createLogInfo(), LogActivityAction.DELETE.ordinal(), logType.ordinal());
                if(hasOnlineDatabaseEnabled(access.getContext())) {
                    try {
                        OnlineDatabase db = OnlineDatabase.getOnlineDatabase(access.getContext());
                        object.deleteClassInstanceFromDatabase(db, objectToDelete);
                        if(!(object instanceof WorkDay)){
                            if(access.createLogInfo() != null) {
                                db.getMongoDb().getCollection(LOG).insertOne(convertFromObjectToDocument(log));
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppDatabase db = AppDatabase.getAppDatabase(access.getContext());
                if(!(object instanceof WorkDay)){
                    if(access.createLogInfo() != null) {
                        db.logDao().insert(log);
                    }
                }
                object.deleteClassInstanceFromDatabase(db, objectToDelete);
                return null;
            }

            @Override
            protected void onPostExecute(Void aVoid) {
                access.onPostExecute(null);
            }
        }.execute();
    }

    public static <T extends DatabaseObjects<T>> void updateObject(final DatabaseAccess<T> access, final DatabaseObjects<T> object, final T objectToUpdate)
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... Voids) {
                Authentication authentication = Util.getAuthentication();
                LogActivityType logType = findLogTypeInt(access,object);
                LogActivity log = new LogActivity(authentication.getUser().getName(), access.createLogInfo(), LogActivityAction.UPDATE.ordinal(), logType.ordinal());
                if(hasOnlineDatabaseEnabled(access.getContext())) {
                    try {
                        OnlineDatabase db = OnlineDatabase.getOnlineDatabase(access.getContext());
                        object.updateClassInstanceFromDatabase(db, objectToUpdate);
                        if(!(object instanceof WorkDay)){
                            if(access.createLogInfo() != null) {
                                db.getMongoDb().getCollection(LOG).insertOne(convertFromObjectToDocument(log));
                            }
                        }

                    }catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppDatabase db = AppDatabase.getAppDatabase(access.getContext());
                if(!(object instanceof WorkDay)){
                    if(access.createLogInfo() != null) {
                        db.logDao().insert(log);
                    }
                }
                object.updateClassInstanceFromDatabase(db, objectToUpdate);
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                access.onPostExecute(null);
            }

        }.execute();
    }

    public static <T extends DatabaseObjects<T>> void insertObject(final DatabaseAccess<T> access, final DatabaseObjects<T> object, final T objectToInsert)
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... Voids) {
                Authentication authentication = Util.getAuthentication();
                LogActivityType logType = findLogTypeInt(access,object);
                LogActivity log = new LogActivity(authentication.getUser().getName(), access.createLogInfo(), LogActivityAction.ADD.ordinal(), logType.ordinal());
                if(hasOnlineDatabaseEnabled(access.getContext())) {
                    try {
                        OnlineDatabase db = OnlineDatabase.getOnlineDatabase(access.getContext());
                        object.insertClassInstanceFromDatabase(db, objectToInsert);
                        if(!(object instanceof WorkDay)){
                            if(access.createLogInfo() != null) {
                                db.getMongoDb().getCollection(LOG).insertOne(convertFromObjectToDocument(log));
                            }
                        }
                    }
                    catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                AppDatabase db = AppDatabase.getAppDatabase(access.getContext());
                object.insertClassInstanceFromDatabase(db, objectToInsert);
                if(!(object instanceof WorkDay)){
                    if(access.createLogInfo() != null) {
                        db.logDao().insert(log);
                    }
                }

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                access.onPostExecute(null);
            }
        }.execute();
    }

    public static <T extends DatabaseObjects<T>> void findObjectByID(final DatabaseAccess<T> access, final DatabaseObjects<T> object, final int IDToFind)
    {
        new AsyncTask<Void, Void, T>() {
            T dbObj;
            @Override
            protected T doInBackground(Void... Voids) {
                if(hasOnlineDatabaseEnabled(access.getContext())) {
                    try {
                        OnlineDatabase db = OnlineDatabase.getOnlineDatabase(access.getContext());
                        dbObj = object.retrieveClassInstanceFromDatabaseID(db, IDToFind);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (dbObj == null) {
                    AppDatabase db = AppDatabase.getAppDatabase(access.getContext());
                    dbObj = object.retrieveClassInstanceFromDatabaseID(db, IDToFind);
                }
                return dbObj;
            }
            @Override
            protected void onPostExecute(T object) {
                List<T> objects = new ArrayList<>();
                objects.add(object);
                access.onPostExecute(objects);
            }
        }.execute();
    }

    public static <T extends DatabaseObjects<T>> void findObjectByString(final DatabaseAccess<T> access, final DatabaseObjects<T> object, final String stringToFind)
    {
        new AsyncTask<Void, Void, T>() {
            T dbObj;
            @Override
            protected T doInBackground(Void... Voids) {
                if(hasOnlineDatabaseEnabled(access.getContext())) {
                    try {
                        OnlineDatabase db = OnlineDatabase.getOnlineDatabase(access.getContext());
                        dbObj = object.retrieveClassInstanceFromDatabaseString(db, stringToFind);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if(dbObj == null) {
                    AppDatabase db = AppDatabase.getAppDatabase(access.getContext());
                    dbObj = object.retrieveClassInstanceFromDatabaseString(db, stringToFind);
                }
                return dbObj;
            }
            @Override
            protected void onPostExecute(T object) {
                List<T> objects = new ArrayList<>();
                objects.add(object);
                access.onPostExecute(objects);
            }
        }.execute();
    }

    public static <T extends DatabaseObjects<T>> void enactMultipleDatabaseOperationsPostExecute(final MultiDatabaseAccess<T> access)
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... Voids) {
                access.accessDatabaseMultipleTimes();
                access.createCustomLog();
                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                List<T> objects = new ArrayList<>();
                access.onPostExecute(objects);
            }
        }.execute();
    }

    public static void enactMultipleDatabaseOperations(final MultiDatabaseAccess access)
    {
        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(Void... Voids) {
                access.accessDatabaseMultipleTimes();
                access.createCustomLog();
                return null;
            }
        }.execute();
    }

    public static <T extends DatabaseObjects<T>> LogActivityType findLogTypeInt(DatabaseAccess<T> access, DatabaseObjects<T> object) {
        LogActivityType logType = LogActivityType.USER;
        if(access instanceof TimeReporting || access instanceof HourOperations) {
            logType = LogActivityType.HOURS;
        }
        else if (access instanceof ReceivePayment) {
            logType = LogActivityType.PAYMENT;
        }

        if (object instanceof Customer) {
                logType = LogActivityType.CUSTOMER;
            }
        return logType;
    }


}
