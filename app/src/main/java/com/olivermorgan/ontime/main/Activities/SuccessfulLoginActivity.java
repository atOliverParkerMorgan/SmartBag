package com.olivermorgan.ontime.main.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

public class SuccessfulLoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_successful_login);
        Button next = findViewById(R.id.buttonNext);
        next.setOnClickListener(v -> {
            startActivity(new Intent(this, InfoAfterLoginActivity.class));
            finish();
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });
    }
}
