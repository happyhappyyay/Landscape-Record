package com.happyhappyyay.landscaperecord.Utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.Activity.Dashboard;
import com.happyhappyyay.landscaperecord.Activity.HourOperations;
import com.happyhappyyay.landscaperecord.Activity.LoginPage;
import com.happyhappyyay.landscaperecord.Activity.QuickSheet;
import com.happyhappyyay.landscaperecord.Activity.ReceivePayment;
import com.happyhappyyay.landscaperecord.Activity.Settings;
import com.happyhappyyay.landscaperecord.Activity.TimeReporting;
import com.happyhappyyay.landscaperecord.Activity.ViewCustomers;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseObjects;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseOperator;
import com.happyhappyyay.landscaperecord.DatabaseInterface.MultiDatabaseAccess;
import com.happyhappyyay.landscaperecord.Enum.LogActivityAction;
import com.happyhappyyay.landscaperecord.Enum.LogActivityType;
import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.POJO.LogActivity;
import com.happyhappyyay.landscaperecord.POJO.Service;
import com.happyhappyyay.landscaperecord.POJO.User;
import com.happyhappyyay.landscaperecord.POJO.WorkDay;
import com.happyhappyyay.landscaperecord.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;

import static com.happyhappyyay.landscaperecord.Utility.OnlineDatabase.LOG;
import static com.happyhappyyay.landscaperecord.Utility.OnlineDatabase.convertFromObjectToDocument;

public class Util {
    public static final Customer CUSTOMER_REFERENCE = new Customer();
    public static final LogActivity LOG_REFERENCE = new LogActivity();
    public static final WorkDay WORK_DAY_REFERENCE = new WorkDay();
    public static final User USER_REFERENCE = new User();
    public static final String DELIMITER = "|";
    public static final String INSERT = "Insert";
    public static final String UPDATE = "Update";
    public static final String DELETE = "Delete";

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

    public static boolean hasOnlineDatabaseEnabled(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getBoolean(retrieveStringFromResources(R.string.pref_key_database_usage, context), false);
    }

    public static String retrieveCompanyName(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(retrieveStringFromResources(R.string.pref_key_company_name, context), "Enter Company Name in Settings");
    }

    public static String retrievePersonalMessage(Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        return sharedPref.getString(retrieveStringFromResources(R.string.pref_key_personal_message, context), "Enter Personal Message in Settings");
    }

    public static String retrieveStringFromResources(int resId, Context context) {
        return context.getResources().getString(resId);
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
        Authentication.getAuthentication().setUser(null);
        Toast.makeText(context.getApplicationContext(), "Logged out! ", Toast.LENGTH_LONG).show();
    }

    private static void gotToContacts(Context context) {
        Intent intent = new Intent(context, ViewCustomers.class);
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
                AppDatabase adb = AppDatabase.getAppDatabase(access.getContext());
                Authentication authentication = Util.getAuthentication();
                LogActivityType logType = findLogTypeInt(access,object);
                LogActivity log = new LogActivity(authentication.getUser().getName(), access.createLogInfo(), LogActivityAction.DELETE.ordinal(), logType.ordinal());
                log.setObjId(objectToDelete.getId());
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
//                if(!(object instanceof WorkDay)){
                    if(access.createLogInfo() != null) {
                        adb.logDao().insert(log);
                    }
//                }
                object.deleteClassInstanceFromDatabase(adb, objectToDelete);
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
                AppDatabase adb = AppDatabase.getAppDatabase(access.getContext());
                Authentication authentication = Util.getAuthentication();
                LogActivityType logType = findLogTypeInt(access,object);
                LogActivity log = new LogActivity(authentication.getUser().getName(), access.createLogInfo(), LogActivityAction.UPDATE.ordinal(), logType.ordinal());
                log.setObjId(objectToUpdate.getId());
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
//                if(!(object instanceof WorkDay)){
                    if(access.createLogInfo() != null) {
                        adb.logDao().insert(log);
                    }
//                }
                object.updateClassInstanceFromDatabase(adb, objectToUpdate);
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
                AppDatabase adb = AppDatabase.getAppDatabase(access.getContext());
                LogActivity log = new LogActivity();
                if(access.createLogInfo() != null) {
                    Authentication authentication = Util.getAuthentication();
                    LogActivityType logType = findLogTypeInt(access, object);
                    log = new LogActivity(authentication.getUser().getName(), access.createLogInfo(), LogActivityAction.ADD.ordinal(), logType.ordinal());
                    log.setObjId(objectToInsert.getId());
                }
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

                object.insertClassInstanceFromDatabase(adb, objectToInsert);
//                if(!(object instanceof WorkDay)){
                    if(access.createLogInfo() != null) {
                        adb.logDao().insert(log);
                    }
//                }

                return null;
            }
            @Override
            protected void onPostExecute(Void aVoid) {
                access.onPostExecute(null);
            }
        }.execute();
    }

    public static <T extends DatabaseObjects<T>> void findObjectByID(final DatabaseAccess<T> access, final DatabaseObjects<T> object, final String IDToFind)
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

    public static void updateDatabases(Context context) {
            OnlineDatabase od = OnlineDatabase.getOnlineDatabase(context);
            AppDatabase ad = AppDatabase.getAppDatabase(context);
            LogActivity logO = updateDatabase(od, ad);
            LogActivity logU = updateDatabase(ad, od);
            logO.setModifiedTime(logU.getModifiedTime());
            LOG_REFERENCE.insertClassInstanceFromDatabase(od, logO);
            LOG_REFERENCE.insertClassInstanceFromDatabase(ad, logU);
    }

    public static LogActivity updateDatabase(DatabaseOperator originalDB, DatabaseOperator updatingDB) {
        int deleteCount = 0;
        int insertCount = 0;
        int updateCount = 0;
        int logCount = 0;
        List<LogActivity> logs = LOG_REFERENCE.retrieveClassInstancesAfterModifiedTimeWithType(updatingDB, 0, LogActivityType.DATABASE.ordinal());
        long newestLogTime = 0;
        if(logs.size() > 0) {
            int count = 0;
            for (int i = 0; i < logs.size(); i++) {
                Log.d(TAG, "updateDatabase: " + count);
                count++;
                long logModifiedTime = logs.get(i).getModifiedTime();
                if (logModifiedTime > newestLogTime) {
                    newestLogTime = logModifiedTime;
                }
            }
        }

        logs = LOG_REFERENCE.retrieveClassInstancesAfterModifiedTime(originalDB, newestLogTime);


        for (int i = 0; i < logs.size(); i++) {
            String logObjId = logs.get(i).getObjId();
            if (logs.get(i).getLogActivityAction() == LogActivityAction.ADD.ordinal()) {
                switch (logs.get(i).getLogActivityType()) {
                    case 0:
                        User userO = USER_REFERENCE.retrieveClassInstanceFromDatabaseID(originalDB, logObjId);
                        User userU = USER_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, logObjId);
                        if (userO != null & userU == null) {
                            USER_REFERENCE.insertClassInstanceFromDatabase(updatingDB, userO);
                            insertCount++;
                        }
                        break;
                    case 1:
                        Customer customerO = CUSTOMER_REFERENCE.retrieveClassInstanceFromDatabaseID(originalDB, logObjId);
                        Customer customerU = CUSTOMER_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, logObjId);
                        Log.d("DATABASE", "updateDatabase: customer add");
                        if (customerO != null & customerU == null) {
                            CUSTOMER_REFERENCE.insertClassInstanceFromDatabase(updatingDB, customerO);
                            insertCount++;
                        }
                        break;
                    case 7:
                        WorkDay workDayO = WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseID(originalDB, logObjId);
                        WorkDay workDayU = WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, logObjId);
                        if (workDayO != null & workDayU ==null) {
                            WORK_DAY_REFERENCE.insertClassInstanceFromDatabase(updatingDB, workDayO);
                            insertCount++;
                        }
                        break;
                }
            } else if (logs.get(i).getLogActivityAction() == LogActivityAction.DELETE.ordinal()) {
                switch (logs.get(i).getLogActivityType()) {
                    case 0:
                        User userO = USER_REFERENCE.retrieveClassInstanceFromDatabaseID(originalDB, logObjId);
                        User userU = USER_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, logObjId);
                        if (userO == null & userU != null) {
                            USER_REFERENCE.deleteClassInstanceFromDatabase(updatingDB, userU);
                            deleteCount++;
                        }
                        break;
                    case 1:
                        Customer customerO = CUSTOMER_REFERENCE.retrieveClassInstanceFromDatabaseID(originalDB, logObjId);
                        Customer customerU = CUSTOMER_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, logObjId);
                        if (customerO == null & customerU != null) {
                            CUSTOMER_REFERENCE.deleteClassInstanceFromDatabase(updatingDB, customerU);
                            deleteCount++;
                            Log.d("DATABASE", "updateDatabase: customer delete");
                        }
                        break;
                    case 7:
                        WorkDay workDayO = WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseID(originalDB, logObjId);
                        WorkDay workDayU = WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, logObjId);
                        if (workDayO == null & workDayU != null) {
                            WORK_DAY_REFERENCE.deleteClassInstanceFromDatabase(updatingDB, workDayU);
                            deleteCount++;
                        }
                        break;
                }
            }
            if(logs.get(i).getLogActivityType() == LogActivityType.DATABASE.ordinal()) {
                LogActivity logO = LOG_REFERENCE.retrieveClassInstanceFromDatabaseID(originalDB, logs.get(i).getLogId());
                LogActivity logU = LOG_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, logs.get(i).getLogId());
                if (logO != null & logU == null) {
                    LOG_REFERENCE.insertClassInstanceFromDatabase(updatingDB, logO);
                    logCount++;
                }
            }
        }

        List<DatabaseObjects> databaseObjects = new ArrayList<>();
        List<User> users = USER_REFERENCE.retrieveClassInstancesAfterModifiedTime(originalDB, newestLogTime);
        List<Customer> customers = CUSTOMER_REFERENCE.retrieveClassInstancesAfterModifiedTime(originalDB, newestLogTime);
        List<WorkDay> workDays = WORK_DAY_REFERENCE.retrieveClassInstancesAfterModifiedTime(originalDB, newestLogTime);
        Log.d(TAG, "updateDatabase: 10292922" +users.size() + " " + customers.size() + " " + workDays.size());
        databaseObjects.addAll(users);
        databaseObjects.addAll(customers);
        databaseObjects.addAll(workDays);

        for(int i = 0; i < databaseObjects.size(); i++) {
            DatabaseObjects databaseObject = databaseObjects.get(i);
            if(databaseObject instanceof User) {
                User user = USER_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, databaseObject.getId());
                if(user != null) {
                    databaseObject.setModifiedTime(System.currentTimeMillis());
                    USER_REFERENCE.updateClassInstanceFromDatabase(updatingDB, (User) databaseObject);
                    updateCount++;
                }
            }
            else if(databaseObject instanceof Customer) {
                Customer customer = CUSTOMER_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, databaseObject.getId());
                if(customer != null) {
                    databaseObject.setModifiedTime(System.currentTimeMillis());
                    CUSTOMER_REFERENCE.updateClassInstanceFromDatabase(updatingDB, (Customer) databaseObject);
                    updateCount++;
                }

            } else if (databaseObject instanceof WorkDay) {
                WorkDay workDay = WORK_DAY_REFERENCE.retrieveClassInstanceFromDatabaseID(updatingDB, databaseObject.getId());
                if(workDay != null) {
                    databaseObject.setModifiedTime(System.currentTimeMillis());
                    WORK_DAY_REFERENCE.updateClassInstanceFromDatabase(updatingDB, (WorkDay) databaseObject);
                    updateCount++;
                }
            }
        }


        return new LogActivity(Authentication.getAuthentication().getUser().getName(), "ADD:"
                + insertCount + " UPDATE:" + updateCount + " DELETE:" + deleteCount + " LOG:" + logCount,
                LogActivityAction.UPDATE.ordinal(), LogActivityType.DATABASE.ordinal());

        }
}
