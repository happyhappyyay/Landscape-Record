package com.happyhappyyay.landscaperecord.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Service;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import word.api.interfaces.IDocument;
import word.w2004.Document2004;
import word.w2004.elements.BreakLine;
import word.w2004.elements.Heading1;
import word.w2004.elements.Paragraph;
import word.w2004.style.HeadingStyle;
import word.w2004.style.ParagraphStyle;

public class CreateDocument {

    private static final String BILLING_DIRECTORY = "Landscape Billing";
    SharedPreferences prefs;

    public CreateDocument(Context context, Customer customer, List<Service> services) {
        prefs = PreferenceManager.getDefaultSharedPreferences(context);
    createADocument(context, customer, services);

    }

    public File getPublicAlbumStorageDir() {
        // Get the directory for the user's public pictures directory.
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DOCUMENTS), "");
        if (!file.mkdirs()) {
            Log.e("TAG", "Directory not created");
        }
        return file;
    }

    public void createDirectory(Context context) {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() +"/Billing" );
            if(!dir.exists()) {
                dir.mkdir();
            }
            File txtFile = new File(dir, "Billing.txt");
            String message = Util.retrieveLongCurrentDate() + "tine";
            try {
                FileOutputStream fileOutputStream = new FileOutputStream(txtFile);
                fileOutputStream.write(message.getBytes());
                fileOutputStream.close();

            }
            catch(FileNotFoundException e) {

            }
            catch (IOException e) {

            }
        }
//        try {
//            FileOutputStream fileOutputStream = context.openFileOutput("hello_wo4le", 0);
//            fileOutputStream.write("doooo".getBytes());
//            fileOutputStream.close();
//            Toast.makeText(context, "created", Toast.LENGTH_LONG).show();
//        }
//        catch (Exception e){
//
//        }
//        File mediaStorageDir = new File(Environment.getExternalStorageDirectory() + BILLING_DIRECTORY + "/" , "dox.txt");
//        try {
//            FileOutputStream fOut = new FileOutputStream(mediaStorageDir);
//            OutputStreamWriter osw = new OutputStreamWriter(fOut);
//
//            // Write the string to the file
//            osw.write("dawg");
//
//            /* ensure that everything is
//             * really written out and close */
//            osw.flush();
//            osw.close();
//        }
//        catch (Exception e) {
//
//        }
//
//
//        if (!mediaStorageDir.exists()) {
//            if (!mediaStorageDir.mkdirs()) {
//                Log.d("App", "failed to create directory");
//            }
//        }
    }

    public void createADocument(Context context, Customer customer, List<Service> services) {
        final String BILLING_DOC_NAME = "Billing Report ";
        String state = Environment.getExternalStorageState();

        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + "/Billing");
            if (!dir.exists()) {
                dir.mkdir();
            }
            String monthYear = new SimpleDateFormat("MM-yy", Locale.US).format(new Date(System.currentTimeMillis()));
            File txtFile = new File(dir, customer.getName().toUpperCase() + " " + monthYear + " Invoice.doc");
            try {
                PrintWriter writer = new PrintWriter(txtFile);
                writer.println(createBillingDocument(customer, services));
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, "Failed to create billing document", Toast.LENGTH_LONG).show();
            }
            Toast.makeText(context, "Billing document created for " + customer.getName(), Toast.LENGTH_LONG).show();


        }
    }

    private String createBillingDocument(Customer customer, List<Service> services) {

// then you use
        String companyName = prefs.getString("pref_key_company", "company");
        IDocument myDoc = new Document2004();
        myDoc.addEle(Heading1.with(Util.retrieveStringCurrentDate()).withStyle().align(HeadingStyle.Align.CENTER).create());
        myDoc.addEle(Heading1.with(customer.getName()).withStyle().align(HeadingStyle.Align.CENTER).create());
        myDoc.addEle(Heading1.with(customer.concatenateFullAddress()).withStyle().align(HeadingStyle.Align.CENTER).create());
        if (customer.getCustomerBusiness() != null) {
            myDoc.addEle(Heading1.with("ATTN: " + customer.getCustomerFirstName() + " " + customer.getCustomerLastName()).
                    withStyle().align(HeadingStyle.Align.CENTER).create());
            myDoc.addEle(BreakLine.times(4).create());
        }
        else {
            myDoc.addEle(BreakLine.times(6).create());
        }
        for (int i = 0; i < services.size(); i++){
            myDoc.addEle(Paragraph.with(services.get(i).convertEndTimeToDateString() + ": " + services.get(i).getServices()).withStyle().align(ParagraphStyle.Align.LEFT).create());
            myDoc.addEle(BreakLine.times(1).create());
            myDoc.addEle(Paragraph.with(services.get(i).getManHours() * 30 + "$").withStyle().align(ParagraphStyle.Align.RIGHT).create());
            myDoc.addEle(BreakLine.times(1).create());
        }
        myDoc.addEle(Paragraph.with("Payment due upon receipt. Thank You!").withStyle().align(ParagraphStyle.Align.CENTER).create());
        myDoc.addEle(new BreakLine(2));
        myDoc.addEle(Paragraph.with(companyName).withStyle().align(ParagraphStyle.Align.CENTER).create());

        return myDoc.getContent();
    }
}
