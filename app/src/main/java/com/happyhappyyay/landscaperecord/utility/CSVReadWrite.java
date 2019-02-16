package com.happyhappyyay.landscaperecord.utility;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.database_interface.DatabaseAccess;
import com.happyhappyyay.landscaperecord.enums.LogActivityAction;
import com.happyhappyyay.landscaperecord.enums.LogActivityType;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.LogActivity;
import com.happyhappyyay.landscaperecord.pojo.Service;
import com.happyhappyyay.landscaperecord.pojo.User;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

/**
 * Reading the CSV File
 * Delimiter is comma
 * Start reading from line 1
 */

public class CSVReadWrite {
    private List<Customer> customers;
    public static final String EMPLOYEE_DATA = "Employee_Information";
    public static final String CUSTOMER_DATA = "Customer_Information";
    public static final String SERVICES_DATA = "Services_Information";
    public static final String LOG_DATA = "Log_Information";
    public static final String ALL_DATA =  "All Information";


    public CSVReadWrite() {
        customers = new ArrayList<>();
    }

    public void csvWrite(String writeType, DatabaseAccess access, List<Object> databaseObjects) {

        List<String[]> writeInformation = new ArrayList<>();
        File file = new File("");
        switch(writeType) {
            case EMPLOYEE_DATA:
                final Pattern p = Pattern.compile("\\d+");
                String[] headers = {"First Name", "Last Name", "Hours Unpaid", "Total Hours"};
                writeInformation.add(headers);
                List<User> users = new ArrayList<>();
                List<LogActivity> logs = new ArrayList<>();
                for(int i = 0; i < databaseObjects.size(); i++) {
                    Object object = databaseObjects.get(i);
                    if(object instanceof User) {
                        users.add((User)object);
                    }
                    else if(object instanceof LogActivity) {
                        if(((LogActivity) object).getLogActivityType() == LogActivityType.HOURS.ordinal()) {
                            logs.add((LogActivity) object);
                        }
                    }
                }

                for(User u: users) {
                    double hours = 0;
                    for(int i = 0; i<logs.size(); i++) {
                        if(logs.get(i).getObjId().equals(u.getId())) {
                            Matcher m = p.matcher(logs.get(i).getAddInfo());
                            String hourString = "0";
                            if (m.find()) {
                                hourString = m.group(0);
                            }
                            if(logs.get(i).getLogActivityAction() == LogActivityAction.ADD.ordinal()) {
                                hours += Integer.parseInt(hourString);
                            }
                            else if(logs.get(i).getLogActivityAction() == LogActivityAction.DELETE.ordinal()){
                                hours -= Integer.parseInt(hourString);
                            }
                        }
                    }
                    String[] userInfo = {u.getFirstName(), u.getLastName(), String.format(Locale.US,
                            "2%f", u.getHours()), String.format(Locale.US, "2%f", hours) };
                    writeInformation.add(userInfo);
                }
                break;
            case CUSTOMER_DATA:
                headers = new String[]{"First Name", "Last Name", "Business", "Address", "City",
                        "State", "Phone Number", "E-mail", "Paid", "Unpaid"};
                writeInformation.add(headers);
                List<Customer> customers = new ArrayList<>();
                for(int i = 0; i < databaseObjects.size(); i++) {
                    Object object = databaseObjects.get(i);
                    if(object instanceof Customer) {
                        customers.add((Customer)object);
                    }
                }

                for(Customer c: customers) {
                    String[] customerInfo = {c.getCustomerFirstName(), c.getCustomerLastName(),
                            c.getCustomerBusiness()==null? "":c.getCustomerBusiness(),
                            c.getCustomerAddress(), c.getCustomerCity()==null? "":c.getCustomerCity(),
                            c.getCustomerState()==null? "":c.getCustomerState(),
                            c.getCustomerPhoneNumber()==null? "":c.getCustomerPhoneNumber(),
                            c.getCustomerEmail()==null? "":c.getCustomerEmail(),
                            String.format(Locale.US, "%.2f", c.getPayment().getAmountPaid()),
                            String.format(Locale.US, "%.2f", c.getPayment().getAmountRemaining())};
                    writeInformation.add(customerInfo);
                }
                break;
            case SERVICES_DATA:
                headers = new String[]{"Account Name", "Date", "Address", "Service", "Price", "Billed",
                        "Paid", "Man Hours", "Mileage", "Service ID"};
                writeInformation.add(headers);
                customers = new ArrayList<>();
                for(int i = 0; i < databaseObjects.size(); i++) {
                    Object object = databaseObjects.get(i);
                    if(object instanceof Customer) {
                        customers.add((Customer)object);
                    }
                }

                for(Customer c: customers) {
                    for(Service s: c.getCustomerServices()) {
                        String[] serviceInfo = {c.getName(),
                                Util.convertLongToStringDate(s.getStartTime()) + " - " +
                                (s.getEndTime() > 0? Util.convertLongToStringDate(s.getEndTime()):""),
                                c.concatenateFullAddress(),
                                s.getServices(), String.format(Locale.US, "2%f",
                                c.getPayment().returnServicePrice(s.getServices())),
                                s.isPriced()? "Billed": "Not Billed", s.isPaid()? "Paid": "Unpaid",
                                String.format(Locale.US, "2%f", s.getManHours()),
                                String.format(Locale.US, "2%f", c.getCustomerMileage()),
                                String.valueOf(s.getServiceID())};
                        writeInformation.add(serviceInfo);
                    }

                }
                break;
            case LOG_DATA:
                headers = new String[]{"Date", "Employee", "Information"};
                writeInformation.add(headers);
                logs = new ArrayList<>();
                for(int i = 0; i < databaseObjects.size(); i++) {
                    Object object = databaseObjects.get(i);
                    if(object instanceof LogActivity) {
                        logs.add((LogActivity) object);
                    }
                }

                for(LogActivity l: logs) {
                    String[] logInfo = {Util.convertLongToStringDate(l.getModifiedTime()), l.getUsername(), l.getAddInfo()};
                    writeInformation.add(logInfo);
                }
                break;
            case ALL_DATA:
                csvWrite(EMPLOYEE_DATA, access, databaseObjects);
                csvWrite(CUSTOMER_DATA, access, databaseObjects);
                csvWrite(SERVICES_DATA, access,databaseObjects);
                csvWrite(LOG_DATA, access, databaseObjects);
                break;
            default:
                csvWrite(EMPLOYEE_DATA, access, databaseObjects);
                csvWrite(CUSTOMER_DATA, access, databaseObjects);
                csvWrite(SERVICES_DATA, access,databaseObjects);
                csvWrite(LOG_DATA, access, databaseObjects);
                break;
        }

        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Landscape-Record");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String date = Util.retrieveStringCurrentDate().replace("/", "-");
            file = new File(dir, writeType + "_" + date + ".csv");
        }

        try {
            FileWriter outputFile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputFile);
            writer.writeAll(writeInformation);
            writer.close();
        }
        catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(access.getContext(), "Data export failure. Please try again.", Toast.LENGTH_LONG).show();
        }
    }

    public void csvRead(Uri csvFileUri, Context context) {

        try
        {
            InputStream inputStream = context.getContentResolver().openInputStream(csvFileUri);

            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream),',','"',1);

            String[] customerInformation;

            while((customerInformation = csvReader.readNext())!=null)
            {
                Customer customer = new Customer(customerInformation[0],
                        customerInformation[1],customerInformation[2]);

                if(customerInformation[3] != null) {
                    if (!customerInformation[3].equals("")) {
                        customer.setCustomerCity(customerInformation[3]);
                    }
                }

                if(customerInformation[4] != null) {
                    if (!customerInformation[4].equals("")) {
                        customer.setCustomerCity(customerInformation[4]);
                    }
                }

                if(customerInformation[5] != null) {
                    if (!customerInformation[5].equals("")) {
                        customer.setCustomerCity(customerInformation[5]);
                    }
                }

                if(customerInformation[6] != null) {
                    if (!customerInformation[6].equals("")) {
                        customer.setCustomerCity(customerInformation[6]);
                    }
                }

                if(customerInformation[7] != null) {
                    if (!customerInformation[7].equals("")) {
                        customer.setCustomerCity(customerInformation[7]);
                    }
                }

                if(customerInformation[8] != null) {
                    if (!customerInformation[8].equals("")) {
                        customer.setCustomerCity(customerInformation[8]);
                    }
                }

                customers.add(customer);
            }

        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(context, "Error importing contacts", Toast.LENGTH_SHORT).show();
        }
    }

    public List<Customer> getCustomers() {
        return customers;
    }
}
