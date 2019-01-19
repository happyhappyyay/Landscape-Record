package com.happyhappyyay.landscaperecord.Utility;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.happyhappyyay.landscaperecord.DatabaseInterface.DatabaseObjects;
import com.happyhappyyay.landscaperecord.POJO.Customer;
import com.happyhappyyay.landscaperecord.POJO.Material;
import com.happyhappyyay.landscaperecord.POJO.Service;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;

import org.bson.Document;

import java.util.ArrayList;
import java.util.List;

import static com.mongodb.client.model.Filters.eq;

public class MongoAccess {

    private String dbUsername;
    private String dbUserPassword;
    private String dbName;
    private String dbCollection;
    private String apiKey;
    private Context context;
    private Customer customer;
    public MongoAccess(DatabaseObjects object, Context context) {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        dbUsername = sharedPref.getString("pref_key_username", "");
        apiKey = sharedPref.getString("pref_key_api_key", "");
        dbUserPassword = sharedPref.getString("pref_key_username_password", "");
        dbName = sharedPref.getString("pref_key_dbname","");
        dbCollection = "customer";
        this.context = context;
        new GetData().execute();
    }

    public String getDbAddress() {
        return String.format("https:api.mlab.com/api/1.databases/%s/collections/%s/%s?apiKey=%s",
                dbName, dbCollection, dbUsername, apiKey);
    }

    public String getAddressAPI() {
        return String.format("https:api.mlab.com/api/1.databases/%s/collections/%s?apiKey=%s",
                dbName, dbCollection, dbUsername, apiKey);
    }
    class GetData extends AsyncTask<Void, Void, Void> {
        long time1;
        long time2;

        @Override
        protected Void doInBackground(Void... aVoid) {

//            Converting POJO to MongoDB Document
//
//            Create Gson object
//            Gson gson=new Gson();
//
//            Convert POJO to JSON
//            String json = gson.toJson(POJO);
//
//            Write this to a document
//            FileWriter writer = new FileWriter("file.json");
//            writer.write(json);
//            writer.close();
//
//
//
//            Converting MongoDB Document  back to POJO
//
//            Create Gson object
//            Gson gson=new Gson();
//
//            Convert it back to POJO
//            BufferedReader br = new BufferedReader(new FileReader("file.json"));
//            Data POJO = gson.fromJson(br,POJO.class);
//            System.out.println(POJO);
//            MongoClientURI uri = new MongoClientURI("");
//            MongoClient mongoClient = new MongoClient(uri);
//            MongoDatabase db = mongoClient.getDatabase(");
            MongoDatabase db = OnlineDatabase.getOnlineDatabase(context).getMongoDb();
            MongoCollection<Document> collection = db.getCollection("Document");
            List<Document> documents;
            time1 = System.currentTimeMillis();
            documents = collection.find(eq("customerFirstName", "Jorge")).into(new ArrayList<Document>());
            Log.d("SEARCH", "search for: " + documents.size());
            int count = 0;
            Gson gson=new Gson();
            for(Document d: documents) {
                if(count < 1) {
                    count++;
                    String s = d.toJson();
                    customer = gson.fromJson(s, Customer.class);


                }
            }
            time2 = System.currentTimeMillis();
            Customer customer = new Customer();
            customer.setCustomerFirstName("Jorge");
            customer.setCustomerLastName("Moo");
            Service service = new Service();
            service.setServices("So much food prep");
            Material material = new Material("wood","true",true);
            service.addMaterial(material);
            customer.addService(service);
            String json = gson.toJson(customer);

            Document document = Document.parse(json);

            collection.insertOne(document);

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
        Toast.makeText(context, "" +(time2 - time1), Toast.LENGTH_LONG).show();

        }
    }
}
