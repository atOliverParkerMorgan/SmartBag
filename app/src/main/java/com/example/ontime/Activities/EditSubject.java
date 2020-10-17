package com.example.ontime.Activities;

import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperMyBag;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditSubject extends AppCompatActivity {

    public static boolean firstViewOfActivity = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences preferences = Objects.requireNonNull(this.getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE));
        boolean darkModeOn = preferences.getBoolean("Mode", false);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_subject_edit);

        final String subject = (String) getIntent().getSerializableExtra("subjectName");
        final EditText title = findViewById(R.id.editSubjectName);
        title.setText(subject);

        // setting up switches

        String[] daysOfTheWeek = FeedReaderDbHelperSubjects.getDaysOfSubjects(getApplicationContext(), subject);
        final Switch mon = findViewById(R.id.mondaySwitchEdit);
        final Switch tue = findViewById(R.id.tuesdaySwitchEdit);
        final Switch wed = findViewById(R.id.wednesdaySwitchEdit);
        final Switch thu = findViewById(R.id.thursdaySwitchEdit);
        final Switch fri = findViewById(R.id.fridaySwitchEdit);
        final Switch sat = findViewById(R.id.saturdaySwitchEdit);
        final Switch sun = findViewById(R.id.sundaySwitchEdit);

        // hide weekend
        SharedPreferences preferencesWeekendOn = Objects.requireNonNull(this.getSharedPreferences("WeekendOn", android.content.Context.MODE_PRIVATE));
        boolean weekendOnBoolean = preferencesWeekendOn.getBoolean("Mode", true);

        if(weekendOnBoolean){
            ((ViewManager) sat.getParent()).removeView(sat);
            ((ViewManager) sun.getParent()).removeView(sun);
        }else{
            ConstraintLayout constraintLayout = findViewById(R.id.parent);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.clone(constraintLayout);
            constraintSet.connect(R.id.editItemsRecycleView,ConstraintSet.TOP,R.id.sundaySwitchEdit,ConstraintSet.BOTTOM,64);
            constraintSet.applyTo(constraintLayout);

        }
        
        Switch[] daysOfWeek = new Switch[]{mon,tue,wed,thu,fri,sat,sun};
        for (int i = 0; i < daysOfWeek.length; i++) {
            daysOfWeek[i].setChecked(daysOfTheWeek[i].equals("true"));
        }


        // create a view holder for this layout
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.editItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));



        // loop through all relevant subjects
        final List<Item> itemsDataItemsToEdit = new ArrayList<>();
        final List<String> itemNames = FeedReaderDbHelperItems.getContent(this, subject);
        for(String item: itemNames){
            itemsDataItemsToEdit.add(new Item(item, subject));
        }


        // 3. create an adapter
        final MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToEdit, (byte) -10, findViewById(android.R.id.content), false, true);
        // 4. set adapter
        viewHolder.editItemsRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set itemAdd animator to DefaultAnimator

        // delete and save buttons
        viewHolder.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    FeedReaderDbHelperSubjects.edit(v.getContext(), subject, title.getText().toString(),
                            mon.isChecked(), tue.isChecked(), wed.isChecked(), thu.isChecked(),
                            fri.isChecked(), sat.isChecked(), sun.isChecked());
                    // item logic

                    // get items pre edit
                    List<String> oldList = FeedReaderDbHelperItems.getContent(v.getContext(), subject);
                    // -1 means an Error has occurred
                    FeedReaderDbHelperItems.delete(v.getContext(), subject);

                    Intent i = new Intent(EditSubject.this, MainActivity.class);
                    i.putExtra("Fragment","overview");

                    i.putExtra("putInToBag", false );
                    if ( !FeedReaderDbHelperItems.write(v.getContext(),  i, mAdapterItemsToAdd.getItems())) {
                        Toast.makeText(v.getContext(), "Annnn error as occurred in the database report this issue",
                                Toast.LENGTH_LONG).show();
                    }



                    startActivity(i);

                }catch (android.database.sqlite.SQLiteException e){
                    System.err.println(e);
                    Toast.makeText(v.getContext(), "An 2 error as occurred in the database report this issue",
                            Toast.LENGTH_LONG).show();
                }


            }
        });


        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                FeedReaderDbHelperSubjects.deleteSubject(subject, getApplicationContext());
                Intent i = new Intent(EditSubject.this, MainActivity.class);
                i.putExtra("Fragment","overview");
                startActivity(i);

            }
        });
//        BottomNavigationView navView = findViewById(R.id.nav_view);
//        navView.setSelectedItemId(R.id.navigation_overview);
//        navView.setOnNavigationItemSelectedListener(navListener);
//        //I added this if statement to keep the selected fragment when rotating the device
//        if (savedInstanceState == null && !firstViewOfActivity) {
//            getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment,
//                    new AddFragment()).commit();
//        }



    }

//    // navigation
//    public BottomNavigationView.OnNavigationItemSelectedListener navListener =
//            new BottomNavigationView.OnNavigationItemSelectedListener() {
//                @Override
//                public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
//                    if(firstViewOfActivity){
//                        // remove buttons
//                        View v = (View) findViewById(R.id.Delete);
//                        ((ViewManager)v.getParent()).removeView(v);
//                        v = (View) findViewById(R.id.Save);
//                        ((ViewManager)v.getParent()).removeView(v);
//                        v = (View) findViewById(R.id.scrollView);
//                        ((ViewManager)v.getParent()).removeView(v);
//                    }
//
//                    firstViewOfActivity = false;
//
//
//                    Fragment selectedFragment = null;
//                    switch (menuItem.getItemId()){
//                        case R.id.navigation_add:
//                            selectedFragment = new AddFragment();
//                            break;
//                        case R.id.navigation_remove:
//                            selectedFragment = new RemoveFragment();
//                            break;
//                        case R.id.navigation_bag:
//                            selectedFragment = new BagFragment();
//                            break;
//                        case R.id.navigation_overview:
//                            selectedFragment = new OverviewFragment();
//                            break;
//                    }
//
//
//                    getSupportFragmentManager().beginTransaction().replace(R.id.HostFragment, Objects.requireNonNull(selectedFragment)).commit();
//                    return true;
//                }
//            };

    class ViewHolder {
        EditText itemName;
        Button saveButton;
        Button deleteButton;

        RecyclerView editItemsRecycleView;

        ViewHolder() {
            saveButton = findViewById(R.id.Save);
            deleteButton = findViewById(R.id.Delete);
            itemName = findViewById(R.id.editItem);
            editItemsRecycleView = findViewById(R.id.editItemsRecycleView);
            editItemsRecycleView.setNestedScrollingEnabled(false);
        }
    }
}