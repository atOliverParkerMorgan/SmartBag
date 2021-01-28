package com.olivermorgan.ontime.main.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;
import com.olivermorgan.ontime.main.ui.Add.AddFragment;
import com.olivermorgan.ontime.main.ui.Bag.BagFragment;
import com.olivermorgan.ontime.main.ui.Overview.OverviewFragment;
import com.olivermorgan.ontime.main.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import java.io.Serializable;
import java.util.Objects;

public class MainActivity extends AppCompatActivity{

    public static int userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences userPreferences = Objects.requireNonNull(this.getSharedPreferences("userId", android.content.Context.MODE_PRIVATE));
        userId = userPreferences.getInt("key", (int) System.currentTimeMillis());

         if(userPreferences.getBoolean("first", true)) {
             startActivity(new Intent(this, IntroActivity.class));
             SharedPreferences.Editor edit = userPreferences.edit();
             edit.putBoolean("first", false);
             edit.apply();
         }
        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_main);

        String PREFS_NAME = "MyPrefsFile";
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);


        if (settings.getBoolean("my_first_time", true)) {


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
        Serializable fragment = getIntent().getSerializableExtra("Fragment");
        if(fragment!=null) {
            if ("add".equals(fragment)) {
                navView.setSelectedItemId(R.id.navigation_add);
                getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                        new AddFragment()).commit();
            } else if ("remove".equals(fragment)) {
                navView.setSelectedItemId(R.id.navigation_remove);
                getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                        new RemoveFragment()).commit();
            } else if ("bag".equals(fragment)) {
                navView.setSelectedItemId(R.id.navigation_bag);
                getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                        new BagFragment()).commit();
            } else if ("overview".equals(fragment)) {
                navView.setSelectedItemId(R.id.navigation_overview);
                getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                        new OverviewFragment()).commit();
            }
        }


    }
    // navigation
    @SuppressLint("NonConstantResourceId")
    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {

                Fragment selectedFragment = null;
                switch (menuItem.getItemId()){
                    case R.id.navigation_add:
                        selectedFragment = new AddFragment();
                        break;
                    case R.id.navigation_remove:
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
            };

    @Override
    protected void onResume() {
        super.onResume();
    }

}
