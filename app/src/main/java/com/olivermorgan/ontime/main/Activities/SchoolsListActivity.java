package com.olivermorgan.ontime.main.Activities;


import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;
import com.olivermorgan.ontime.main.ui.SchoolList.SchoolsListFragment;

import java.util.Objects;


public class SchoolsListActivity extends AppCompatActivity {
    public static final String EXTRA_URL = SchoolsListActivity.class.getCanonicalName() + ".url";
    public static final int RESULT_OK = 0;
    public static final int RESULT_CANCEL = 1;
    SchoolsListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_schools);

        fragment = (SchoolsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentSchools);
        if (fragment != null) {
            fragment.setOnItemClickListener(url -> {

                SharedPrefs.setString(this, SharedPrefs.URL, url);
                setResult(RESULT_OK, new Intent());
                finish();
            });
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCEL);
        super.onBackPressed();
    }
}