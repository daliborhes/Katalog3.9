package com.example.dalibor.katalog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class SelectedContact extends AppCompatActivity {

    private TextView tvContact;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_selected_contact);

        tvContact = findViewById(R.id.contact_data);
        tvContact.setText(getIntent().getStringExtra("ContactData"));

    }
}
