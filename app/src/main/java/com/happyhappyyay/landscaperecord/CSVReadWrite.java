package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import au.com.bytecode.opencsv.CSVReader;

/**
 * Reading the CSV File
 * Delimiter is comma
 * Start reading from line 1
 */

public class CSVReadWrite {
    private Uri csvFileUri;
    private List<Customer> customers;
    private Context context;

    public CSVReadWrite(Uri csvFileUri, Context context) {
        this.csvFileUri = csvFileUri;
        this.context = context;
        customers = new ArrayList<>();
        csvRead();
    }

    private void csvRead() {

        try
        {
            InputStream inputStream = context.getContentResolver().openInputStream(csvFileUri);

            CSVReader csvReader = new CSVReader(new InputStreamReader(inputStream),',','"',1);

            String[] customerInformation;

            while((customerInformation = csvReader.readNext())!=null)
            {
                //Save the employee details in Employee object
                Customer customer = new Customer(customerInformation[0],
                        customerInformation[1],customerInformation[2]);
                customer.setCustomerCity(customerInformation[3]);
                customer.setCustomerState(customerInformation[4]);
                customer.setCustomerPhoneNumber(customerInformation[5]);
                customer.setCustomerEmail(customerInformation[6]);
                customer.setCustomerBusiness(customerInformation[7]);
                customer.setCustomerDay(customerInformation[8]);
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
