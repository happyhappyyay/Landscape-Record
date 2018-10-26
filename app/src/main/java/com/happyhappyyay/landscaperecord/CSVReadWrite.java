package com.happyhappyyay.landscaperecord;

import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
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
