package com.olivermorgan.ontime.main.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.olivermorgan.ontime.main.R;

public class SuccessfulLogin extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_successful_login);
        Button next = findViewById(R.id.buttonNext);
        next.setOnClickListener(v -> {
            startActivity(new Intent(this, MainActivity.class));
            finish();
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });

    }
}
