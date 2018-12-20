package com.happyhappyyay.landscaperecord;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import java.util.List;

public class testDatabase extends AppCompatActivity {
    List<User> users;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_database);
        textView = findViewById(R.id.textView50);
        MongoAccess m = new MongoAccess(Util.CUSTOMER_REFERENCE,this);
    }
}
