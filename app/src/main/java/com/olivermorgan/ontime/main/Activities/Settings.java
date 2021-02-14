package com.olivermorgan.ontime.main.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;


import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;
import com.olivermorgan.ontime.main.ui.Add.AddFragment;
import com.olivermorgan.ontime.main.ui.Bag.BagFragment;
import com.olivermorgan.ontime.main.ui.Overview.OverviewFragment;
import com.olivermorgan.ontime.main.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import java.util.Objects;



public class Settings extends AppCompatActivity{
    private static boolean firstViewOfActivity = true;
    TextView title;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create view

        boolean darkModeOn = SharedPrefs.getDarkMode(this);

        boolean weekendOnBoolean = SharedPrefs.getBoolean(this,SharedPrefs.WEEKEND_ON);

        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_settings);

        ImageView login = findViewById(R.id.bakalariLoginButton);
        login.setOnClickListener(v ->{
                Intent i = new Intent(Settings.this, LoginActivity.class);
                i.putExtra("buttonName",getText(R.string.app_intro_next_button));
                startActivity(i);
                });
        title = findViewById(R.id.LoginTitle);

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch darkMode = findViewById(R.id.darkModeSwitch);
        darkMode.setChecked(darkModeOn);
        darkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

            SharedPrefs.setBoolean(getApplicationContext(), SharedPrefs.DARK_MODE, isChecked);

            if (isChecked) {
                Toast.makeText(getApplicationContext(), "Dark mode is on", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(),"Light mode is on", Toast.LENGTH_SHORT).show();

            }
            Intent intent = new Intent(getApplicationContext(), Settings.class);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });

        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch weekendOn = findViewById(R.id.deleteSundayAndSaturdaySwitch);
        weekendOn.setChecked(!weekendOnBoolean);
        weekendOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
            //declare the shardPreferences variable..
            // if due is set to Saturday or Sunday and do not show Weekend is checked set due to Today.
            if(!isChecked) {

                if(SharedPrefs.getInt(getApplicationContext(), SharedPrefs.SPINNER)== 8 || SharedPrefs.getInt(getApplicationContext(), SharedPrefs.SPINNER)== 7 ) {
                    SharedPrefs.setInt(getApplicationContext(), SharedPrefs.SPINNER, 0);
                }
            }
            SharedPrefs.setBoolean(getApplicationContext(),SharedPrefs.WEEKEND_ON, !isChecked);


            if (isChecked) {
                Toast.makeText(getApplicationContext(), R.string.willShowWeek, Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getApplicationContext(),R.string.willShowWeekNot, Toast.LENGTH_SHORT).show();

            }
            weekendOn.setChecked(!SharedPrefs.getBoolean(this,SharedPrefs.WEEKEND_ON));
        });


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setSelectedItemId(R.id.navigation_bag);
        navView.setOnNavigationItemSelectedListener(navListener);
        //I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null && !firstViewOfActivity) {
            getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                    new AddFragment()).commit();
        }
    }


    // navigation
    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                    title.setVisibility(View.GONE);
                    firstViewOfActivity = false;
                    Fragment selectedFragment = null;
                    switch (menuItem.getItemId()){
                        case R.id.navigation_add:
                            firstViewOfActivity = true;
                            selectedFragment = new AddFragment();
                            break;
                        case R.id.navigation_remove:
                            firstViewOfActivity = true;
                            selectedFragment = new RemoveFragment();
                            break;
                        case R.id.navigation_bag:
                            firstViewOfActivity = true;
                            selectedFragment = new BagFragment();
                            break;
                        case R.id.navigation_overview:
                            firstViewOfActivity = true;
                            selectedFragment = new OverviewFragment();
                            break;
                    }


                    getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, Objects.requireNonNull(selectedFragment)).commit();
                    return true;
                }
            };
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
