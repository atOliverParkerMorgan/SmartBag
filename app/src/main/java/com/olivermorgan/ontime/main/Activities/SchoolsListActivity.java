package com.olivermorgan.ontime.main.Activities;


import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.ui.SchoolList.SchoolsListFragment;


public class SchoolsListActivity extends AppCompatActivity {
    public static final String EXTRA_URL = SchoolsListActivity.class.getCanonicalName() + ".url";
    public static final int RESULT_OK = 0;
    public static final int RESULT_CANCEL = 1;
    SchoolsListFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_schools);

        fragment = (SchoolsListFragment) getSupportFragmentManager().findFragmentById(R.id.fragmentSchools);
        fragment.setOnItemClickListener(url -> {
            Intent intent = new Intent();
            intent.putExtra(EXTRA_URL, url);
            setResult(RESULT_OK, intent);
            finish();
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCEL);
        super.onBackPressed();
    }
}