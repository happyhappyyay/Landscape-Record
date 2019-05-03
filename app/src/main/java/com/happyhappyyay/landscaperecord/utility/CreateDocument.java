package com.happyhappyyay.landscaperecord.utility;


import android.content.Context;
import android.content.SharedPreferences;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.Toast;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.pojo.Customer;
import com.happyhappyyay.landscaperecord.pojo.Service;

import java.io.File;
import java.io.FileNotFoundException;
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
/*
Only works when opening with Word
*/
public class CreateDocument {

    public CreateDocument() {
    }

    public void createADocument(Context context, Customer customer, List<Service> services) {
        String state = Environment.getExternalStorageState();
        if(Environment.MEDIA_MOUNTED.equals(state)) {
            File root = Environment.getExternalStorageDirectory();
            File dir = new File(root.getAbsolutePath() + File.separator + context.getString(R.string.create_document_container));
            if (!dir.exists()) {
                dir.mkdir();
            }
            String monthYear = new SimpleDateFormat("MM-yy", Locale.US).format(new Date(System.currentTimeMillis()));
            File txtFile = new File(dir, customer.getName().toUpperCase() + " " + monthYear + context.getString(R.string.create_document_invoice_ext));
            try {
                PrintWriter writer = new PrintWriter(txtFile);
                writer.println(createBillingDocument(customer, services, context));
                writer.close();
                Toast.makeText(context, context.getString(R.string.create_document_success) +" " + customer.getName(), Toast.LENGTH_LONG).show();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                Toast.makeText(context, context.getString(R.string.create_document_fail), Toast.LENGTH_LONG).show();
                throw new RuntimeException();
            }
        }
    }

    private String createBillingDocument(Customer customer, List<Service> services, Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String companyName = prefs.getString("pref_key_company", context.getString(R.string.create_document_company));
        String personalMessage = prefs.getString("pref_key_personal_message", context.getString(R.string.create_document_thanks));
        IDocument myDoc = new Document2004();
        myDoc.addEle(Heading1.with(Util.retrieveStringCurrentDate()).withStyle().align(HeadingStyle.Align.CENTER).create());
        myDoc.addEle(Heading1.with(customer.getName()).withStyle().align(HeadingStyle.Align.CENTER).create());
        myDoc.addEle(Heading1.with(customer.concatenateFullAddress()).withStyle().align(HeadingStyle.Align.CENTER).create());
        if (customer.getBusiness() != null) {
            myDoc.addEle(Heading1.with(context.getString(R.string.billing_view_pager_attention) + customer.getFirst() + " " + customer.getLast()).
                    withStyle().align(HeadingStyle.Align.CENTER).create());
            myDoc.addEle(BreakLine.times(4).create());
        }
        else {
            myDoc.addEle(BreakLine.times(6).create());
        }
        for (int i = 0; i < services.size(); i++){
            myDoc.addEle(Paragraph.with(services.get(i).convertEndTimeToDateString() + ": " + services.get(i).getServices()).withStyle().align(ParagraphStyle.Align.LEFT).create());
            myDoc.addEle(BreakLine.times(1).create());
            myDoc.addEle(Paragraph.with(services.get(i).getManHours() * 30 + context.getString(R.string.money)).withStyle().align(ParagraphStyle.Align.RIGHT).create());
            myDoc.addEle(BreakLine.times(1).create());
        }
        myDoc.addEle(Paragraph.with(personalMessage).withStyle().align(ParagraphStyle.Align.CENTER).create());
        myDoc.addEle(new BreakLine(2));
        myDoc.addEle(Paragraph.with(companyName).withStyle().align(ParagraphStyle.Align.CENTER).create());

        return myDoc.getContent();
    }
}
