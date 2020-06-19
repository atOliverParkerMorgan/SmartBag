package com.example.ontime.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.ontime.R;
import com.example.ontime.ui.Add.AddFragment;
import com.example.ontime.ui.Bag.BagFragment;
import com.example.ontime.ui.Overview.OverviewFragment;
import com.example.ontime.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final String PREFS_NAME = "MyPrefsFile";

        TextView textView = findViewById(R.id.Title);
        textView.setText("TO ADD");

        ImageButton imageButton = findViewById(R.id.settings);
        imageButton.setBackgroundColor(getResources().getColor(R.color.green));

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setBackgroundColor(getResources().getColor(R.color.green));

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);


        if (settings.getBoolean("my_first_time", true)) {
            //the app is being launched for first time, do something
            Log.d("Comments", "First time");


            // record the fact that the app has been started at least once
            settings.edit().putBoolean("my_first_time", false).apply();
        }


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                    new AddFragment()).commit();
        }

    }
    // navigation
    private BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.navigation_add:
                            TextView textView = findViewById(R.id.Title);
                            textView.setText(getResources().getText(R.string.to_add_text));

                            ImageButton imageButton = findViewById(R.id.settings);
                            imageButton.setBackgroundColor(getResources().getColor(R.color.green));

                            Toolbar toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                            selectedFragment = new AddFragment();
                            break;
                        case R.id.navigation_remove:
                            textView = findViewById(R.id.Title);
                            textView.setText(getResources().getText(R.string.to_remove_text));

                            imageButton = findViewById(R.id.settings);
                            imageButton.setBackgroundColor(getResources().getColor(R.color.red));

                            toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                            selectedFragment = new RemoveFragment();
                            break;
                        case R.id.navigation_bag:
                            textView = findViewById(R.id.Title);
                            textView.setText(getResources().getText(R.string.to_in_bag));
                            imageButton = findViewById(R.id.settings);
                            imageButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                            toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            selectedFragment = new BagFragment();
                            break;
                        case R.id.navigation_overview:
                            textView = findViewById(R.id.Title);
                            textView.setText(getResources().getText(R.string.title_overview));
                            imageButton = findViewById(R.id.settings);
                            imageButton.setBackgroundColor(getResources().getColor(R.color.colorAccent));

                            toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            selectedFragment = new OverviewFragment();
                            break;

                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, selectedFragment).commit();
                    return true;
                }
    };







}
