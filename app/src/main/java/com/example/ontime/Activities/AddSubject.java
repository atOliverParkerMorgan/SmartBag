package com.example.ontime.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.R;
import com.example.ontime.ui.Add.AddFragment;
import com.example.ontime.ui.Bag.BagFragment;
import com.example.ontime.ui.Overview.OverviewFragment;
import com.example.ontime.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.Objects;


public class AddSubject extends AppCompatActivity {
    public static boolean firstViewOfActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = Objects.requireNonNull(this.getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE));
        boolean darkModeOn = preferences.getBoolean("Mode", true);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activivty_add_subject);

        final ViewHolder viewHolder = new ViewHolder();
        // hide weekend
        SharedPreferences preferencesWeekendOn = Objects.requireNonNull(this.getSharedPreferences("WeekendOn", android.content.Context.MODE_PRIVATE));
        boolean weekendOnBoolean = preferencesWeekendOn.getBoolean("Mode", true);

        if(weekendOnBoolean){
            ((ViewManager) viewHolder.saturday.getParent()).removeView(viewHolder.saturday);
            ((ViewManager) viewHolder.sunday.getParent()).removeView(viewHolder.sunday);
        }

        viewHolder.addItems.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = viewHolder.subjectName.getText().toString();
                if(s.equals("")) {
                    Toast.makeText(v.getContext(), "To add a subject write some text into the text field (Mathematics).",
                            Toast.LENGTH_LONG).show();
                }
                else if(FeedReaderDbHelperItems.subjectExists(getApplicationContext(), s)){
                    Toast.makeText(v.getContext(), "The subject "+s+" already exists. Pick an unique name",
                    Toast.LENGTH_LONG).show();
                }else if(s.length()>25){
                    Toast.makeText(v.getContext(), "The subject name is too long. The maximum length is 25 characters",
                            Toast.LENGTH_LONG).show();
                }

                else if(!(viewHolder.monday.isChecked()||
                        viewHolder.tuesday.isChecked()||viewHolder.wednesday.isChecked()||viewHolder.thursday.isChecked()||
                        viewHolder.friday.isChecked()||viewHolder.saturday.isChecked()||viewHolder.sunday.isChecked())){
                    Toast.makeText(v.getContext(), "You have to choose at least one day of the week",
                            Toast.LENGTH_LONG).show();
                }

                else{

                    Intent i = new Intent(AddSubject.this, AddItem.class);
                    i.putExtra("Subject", s);
                    i.putExtra("Monday",Boolean.toString(viewHolder.monday.isChecked()));
                    i.putExtra("Tuesday",Boolean.toString(viewHolder.tuesday.isChecked()));
                    i.putExtra("Wednesday",Boolean.toString(viewHolder.wednesday.isChecked()));
                    i.putExtra("Thursday",Boolean.toString(viewHolder.thursday.isChecked()));
                    i.putExtra("Friday",Boolean.toString(viewHolder.friday.isChecked()));
                    i.putExtra("Saturday",Boolean.toString(viewHolder.saturday.isChecked()));
                    i.putExtra("Sunday",Boolean.toString(viewHolder.sunday.isChecked()));
                    i.putExtra("putInToBag",viewHolder.putInToBag.isChecked());

                    startActivity(i);
                }
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
                    if(firstViewOfActivity){
                        // remove button
                        View v = (View) findViewById(R.id.add_items);
                        ((ViewManager)v.getParent()).removeView(v);
                    }

                    firstViewOfActivity = false;


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


                    getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, Objects.requireNonNull(selectedFragment)).commit();
                    return true;
                }
            };



    public class ViewHolder{
        final Switch monday;
        final Switch tuesday;
        final Switch wednesday;
        final Switch thursday;
        final Switch friday;
        final Switch saturday;
        final Switch sunday;
        final Switch putInToBag;

        EditText subjectName;
        Button addItems;

        ViewHolder(){
            monday = findViewById(R.id.mondayTextView);
            tuesday = findViewById(R.id.Tuesday);
            wednesday = findViewById(R.id.Wednesday);
            thursday = findViewById(R.id.Thursday);
            friday = findViewById(R.id.Friday);
            saturday = findViewById(R.id.Saturday);
            sunday = findViewById(R.id.Sunday);

            putInToBag = findViewById(R.id.addToBag);
            addItems = findViewById(R.id.add_items);
            subjectName = findViewById(R.id.editSubject);
        }
    }
}