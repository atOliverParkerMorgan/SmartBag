package com.example.ontime.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ontime.R;
import com.example.ontime.ui.Add.AddFragment;
import com.example.ontime.ui.Bag.BagFragment;
import com.example.ontime.ui.Overview.OverviewFragment;
import com.example.ontime.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;



public class Settings extends AppCompatActivity {
    private static boolean firstViewOfActivity = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create view

        SharedPreferences preferences = Objects.requireNonNull(this.getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE));
        boolean darkModeOn = preferences.getBoolean("Mode", false);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_settings);

        Switch darkMode = findViewById(R.id.darkModeSwitch);
        darkMode.setChecked(darkModeOn);
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //declare the shardPreferences variable..
                SharedPreferences sp = Objects.requireNonNull(getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE));

                // to save data you have to call the editor
                SharedPreferences.Editor edit = sp.edit();

                //save the value same as putExtras using keyNamePair
                edit.putBoolean("Mode", isChecked);

                //when done save changes.
                edit.apply();

                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "Dark mode is on", Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getApplicationContext(),"Light mode is on", Toast.LENGTH_SHORT).show();

                }
                Intent intent = new Intent(getApplicationContext(), Settings.class);
                startActivity(intent);
                finish();
            }
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
