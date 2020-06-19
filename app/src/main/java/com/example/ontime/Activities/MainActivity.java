package com.example.ontime.Activities;

import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.ontime.R;
import com.example.ontime.ui.Add.AddFragment;
import com.example.ontime.ui.Bag.BagFragment;
import com.example.ontime.ui.Overview.OverviewFragment;
import com.example.ontime.ui.Remove.RemoveFragment;
import com.example.ontime.ui.Settings.Settings;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

public class MainActivity extends AppCompatActivity{

    /** indicates how to behave if the service is killed */
    int mStartMode;

    /** interface for clients that bind */
    IBinder mBinder;

    /** indicates whether onRebind should be used */
    boolean mAllowRebind;
    private final String PREFS_NAME = "MyPrefsFile";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = getApplicationContext().getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE);
        boolean darkMode = preferences.getBoolean("Mode", false);
        if(darkMode){
            setTheme(R.style.DARK);
        }else{
            setTheme(R.style.LIGHT);
        }

        setContentView(R.layout.activity_main);
        Configuration configuration = getResources().getConfiguration();
        int currentNightMode = configuration.uiMode & Configuration.UI_MODE_NIGHT_MASK;
        switch (currentNightMode) {
            case Configuration.UI_MODE_NIGHT_NO:
                // Night mode is not active, we're using the light theme
                break;
            case Configuration.UI_MODE_NIGHT_YES:
                // Night mode is active, we're using dark theme
                break;
        }



        TextView textView = findViewById(R.id.Title);
        textView.setText("TO ADD");


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

                            Toolbar toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.green));
                            selectedFragment = new AddFragment();
                            break;
                        case R.id.navigation_remove:
                            textView = findViewById(R.id.Title);
                            textView.setText(getResources().getText(R.string.to_remove_text));


                            toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.red));
                            selectedFragment = new RemoveFragment();
                            break;
                        case R.id.navigation_bag:
                            textView = findViewById(R.id.Title);
                            textView.setText(getResources().getText(R.string.to_in_bag));

                            toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            selectedFragment = new BagFragment();
                            break;
                        case R.id.navigation_overview:
                            textView = findViewById(R.id.Title);
                            textView.setText(getResources().getText(R.string.title_overview));

                            toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            selectedFragment = new OverviewFragment();
                            break;
                        case R.id.title_overview_button_settings:
                            textView = findViewById(R.id.Title);
                            textView.setText(getResources().getText(R.string.settings));

                            toolbar = findViewById(R.id.toolbar);
                            toolbar.setBackgroundColor(getResources().getColor(R.color.colorAccent));
                            selectedFragment = new Settings();
                            break;


                    }

                    assert selectedFragment != null;
                    getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, selectedFragment).commit();
                    return true;
                }
    };







}
