package com.example.ontime.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditSubject extends AppCompatActivity {
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
        TextView title = findViewById(R.id.subjectText);
        title.setText(subject);

        // create a view holder for this layout
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.editItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        // delete and save buttons
        viewHolder.deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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




    }
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