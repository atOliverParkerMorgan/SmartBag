package com.example.ontime.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;

import com.example.ontime.R;
import com.example.ontime.ui.Add.AddFragment;
import com.example.ontime.ui.Bag.BagFragment;
import com.example.ontime.ui.Overview.OverviewFragment;
import com.example.ontime.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

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
                            selectedFragment = new AddFragment();
                            break;
                        case R.id.navigation_notifications:
                            selectedFragment = new RemoveFragment();
                            break;
                        case R.id.navigation_bag:
                            selectedFragment = new BagFragment();
                            break;
                        case R.id.navigation_overview:
                            selectedFragment = new OverviewFragment();
                            break;

                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, selectedFragment).commit();
                    return true;
                }
    };







}
