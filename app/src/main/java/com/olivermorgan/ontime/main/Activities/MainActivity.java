package com.olivermorgan.ontime.main.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.widget.Toolbar;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olivermorgan.ontime.main.BakalariAPI.Login;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.AppSingleton;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.RozvrhAPI;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.RozvrhWrapper;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.Utils;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.Rozvrh;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.RozvrhDen;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.RozvrhHodina;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;
import com.olivermorgan.ontime.main.ui.Add.AddFragment;
import com.olivermorgan.ontime.main.ui.Bag.BagFragment;
import com.olivermorgan.ontime.main.ui.Overview.OverviewFragment;
import com.olivermorgan.ontime.main.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;


import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;

import org.joda.time.LocalDate;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executor;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences userPreferences = Objects.requireNonNull(this.getSharedPreferences("userId", android.content.Context.MODE_PRIVATE));

        if (userPreferences.getBoolean("first", true)) {
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
            startActivity(new Intent(this, IntroActivity.class));
            SharedPreferences.Editor edit = userPreferences.edit();
            edit.putBoolean("first", false);
            edit.apply();

        }
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        LoadBag loadBag = new LoadBag(this, this);
        loadBag.getRozvrh(0, false);

        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }


        BottomNavigationView navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(navListener);

        // I added this if statement to keep the selected fragment when rotating the device
        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
                    new AddFragment()).commit();

        }
        Serializable fragment = getIntent().getSerializableExtra("Fragment");
        if (fragment != null) {
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

                assert selectedFragment != null;
                getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, selectedFragment).commit();
                return true;
            };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.top_nav_menu, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.subject) {
            AddSubject.firstViewOfActivity = true;
            Intent intent = new Intent(this, AddSubject.class);
            this.startActivity(intent);
            return true;
        }else if(item.getItemId() == R.id.settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            this.startActivity(intent);
            return true;
        }
        return true;
    }

    private Login login = null;
    public Login getLogin() {
        if (login == null){
            login = new Login(this.getApplicationContext());
        }
        return login;
    }


    public static void showAlert(Context context, String message){
        AlertDialog.Builder alert = new AlertDialog.Builder(context);
        alert.setMessage(message);
        alert.setPositiveButton(android.R.string.ok, (dialog, which) -> {
            dialog.cancel();
        });
    }





}
