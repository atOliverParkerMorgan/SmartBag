package com.example.ontime.Activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

public class EditSubject extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final String subject = (String) getIntent().getSerializableExtra("Subject");

    }
}