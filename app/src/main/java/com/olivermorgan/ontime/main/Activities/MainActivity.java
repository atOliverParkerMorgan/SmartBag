package com.olivermorgan.ontime.main.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.appcompat.widget.Toolbar;
import com.olivermorgan.ontime.main.BakalariAPI.Login;
import com.olivermorgan.ontime.main.Logic.LoadBag;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;
import com.olivermorgan.ontime.main.ui.Add.AddFragment;
import com.olivermorgan.ontime.main.ui.Bag.BagFragment;
import com.olivermorgan.ontime.main.ui.Overview.OverviewFragment;
import com.olivermorgan.ontime.main.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.olivermorgan.ontime.main.ui.Settings.SettingsFragment;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import java.util.Objects;


public class MainActivity extends AppCompatActivity {
    Toolbar toolbar;
    @SuppressLint("StaticFieldLeak")
    public static LoadBag loadBag;
    private Login login;
    private boolean hideConnectionStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.DARK_Launcher);

        SharedPreferences userPreferences = Objects.requireNonNull(this.getSharedPreferences("userId", android.content.Context.MODE_PRIVATE));

        if (userPreferences.getBoolean("first", true)) {
            SharedPrefs.setInt(this, "weekIndex", 0);
            SharedPrefs.setBoolean(this, SharedPrefs.WEEKEND_ON, true);

            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
            startActivity(new Intent(this, IntroActivity.class));
            SharedPreferences.Editor edit = userPreferences.edit();
            edit.putBoolean("first", false);
            edit.apply();

        }
        // informs
        boolean activityIsBeingRestartedFromOverView = "overview".equals(getIntent().getSerializableExtra("Fragment"));
        // informs activity that the them is being reset
        boolean fromSettings = getIntent().getBooleanExtra("fromSettings", false);
        // prevents database from updating
        boolean backSettings = getIntent().getBooleanExtra("dontUpdateDatabase", false);

        String language = SharedPrefs.getBoolean(this, "Language") ? "cs" : "en";
        SettingsFragment.setLocale(this, language);

        login = new Login(this);
        if (login.isLoggedIn()) SharedPrefs.setBoolean(this, SharedPrefs.WEEKEND_ON, true);

        if (login.isLoggedIn() && !activityIsBeingRestartedFromOverView && !fromSettings && !backSettings) {
            loadBag = new LoadBag(this, this);
            loadBag.getRozvrh(SharedPrefs.getInt(this, "weekIndex"));
        }


        super.onCreate(savedInstanceState);

        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }


        setContentView(R.layout.activity_main);


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);
        if (darkModeOn) {
            navView.setBackgroundColor(getResources().getColor(R.color.barColorDark));
        } else {
            navView.setBackgroundColor(getResources().getColor(R.color.barColorLight));
        }


        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // I added this if statement to keep the selected fragment when rotating the device
        if (fromSettings)
            getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, new SettingsFragment()).commit();
        else {
            if (savedInstanceState == null) {

                getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                        new AddFragment()).commit();

            }

        }
        // check if start activity should be overview
        if (activityIsBeingRestartedFromOverView) {
            // show tool bar

            SharedPrefs.setBoolean(getApplicationContext(), "updateTableInThread", false);
            setSupportActionBar(toolbar);
            Objects.requireNonNull(getSupportActionBar()).show();

            navView.setSelectedItemId(R.id.navigation_overview);
            getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                    new OverviewFragment()).commit();


            getIntent().getSerializableExtra("Fragment");

        } else
            SharedPrefs.setBoolean(getApplicationContext(), "updateTableInThread", true);



    }

    // navigation
    @SuppressLint("NonConstantResourceId")
    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
            menuItem -> {

                // show tool bar
                setSupportActionBar(toolbar);
                Objects.requireNonNull(getSupportActionBar()).show();

                Fragment selectedFragment = null;
                switch (menuItem.getItemId()) {
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


                getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, Objects.requireNonNull(selectedFragment)).commit();
                return true;
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_main_menu, menu);
        if (login.isLoggedIn()) {
            MenuItem item = menu.findItem(R.id.mainTitle);
            item.setVisible(false);
        }
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.mainTitle) {
            AddSubjectOrOther.firstViewOfActivity = true;
            Intent intent = new Intent(this, WhatToAdd.class);
            this.startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.settings) {
            toolbar.setTitle(getString(R.string.settings));

            getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, new SettingsFragment()).commit();
            return true;
        }
        return true;
    }


    public static void showAlert(Context context, String message, String text) {
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setTitle(message);
        alert.setMessage(text);
        alert.setPositiveButton(android.R.string.ok, (dialog, which) -> dialog.cancel());
        alert.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    public static boolean isConnected(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null &&
                activeNetwork.isConnectedOrConnecting();
    }


}
