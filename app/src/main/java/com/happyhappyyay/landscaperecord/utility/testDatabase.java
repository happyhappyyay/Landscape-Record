package com.happyhappyyay.landscaperecord.utility;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.happyhappyyay.landscaperecord.R;
import com.happyhappyyay.landscaperecord.pojo.User;

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
