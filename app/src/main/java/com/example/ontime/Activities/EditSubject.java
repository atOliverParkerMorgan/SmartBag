package com.example.ontime.Activities;

import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;
import com.example.ontime.ui.Add.AddFragment;
import com.example.ontime.ui.Bag.BagFragment;
import com.example.ontime.ui.Overview.OverviewFragment;
import com.example.ontime.ui.Remove.RemoveFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;

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
        EditText title = findViewById(R.id.editSubjectName);
        title.setText(subject);

        // create a view holder for this layout
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.editItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        // delete and save buttons
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

//                FeedReaderDbHelperItems dbHelperForItems = new FeedReaderDbHelperItems(getApplicationContext());
//                for(String itemName : FeedReaderDbHelperItems.getContent(getApplicationContext(), subject)){
//
//                }

                if( !FeedReaderDbHelperSubjects.deleteSubject(subject, getApplicationContext())){
                    Toast.makeText(getApplicationContext(),"an Error Occurred in the database", Toast.LENGTH_LONG);
                }else{
                    Toast.makeText(getApplicationContext(),subject+" was successfully deleted", Toast.LENGTH_LONG);
                    Intent i = new Intent(EditSubject.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });

        // loop through all relevant subjects
        List<Item> itemsDataItemsToEdit = new ArrayList<>();
        final List<String> itemNames = FeedReaderDbHelperItems.getContent(this, subject);
        for(String item: itemNames){
            itemsDataItemsToEdit.add(new Item(item, subject));
        }


        // 3. create an adapter
        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToEdit,(byte) -2, findViewById(android.R.id.content));
        // 4. set adapter
        viewHolder.editItemsRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set itemAdd animator to DefaultAnimator
        viewHolder.editItemsRecycleView.setItemAnimator(new DefaultItemAnimator());
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
        }
    }
}