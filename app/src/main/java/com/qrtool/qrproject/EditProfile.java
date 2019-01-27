package com.qrtool.qrproject;

import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class EditProfile extends AppCompatActivity {
     TextInputEditText name,email;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        name=(TextInputEditText)findViewById(R.id.name);
        email=(TextInputEditText)findViewById(R.id.email);

        String setname=getIntent().getStringExtra("name");
        String setemail=getIntent().getStringExtra("email");

        name.setText(setname);
        email.setText(setemail);


    }
}
