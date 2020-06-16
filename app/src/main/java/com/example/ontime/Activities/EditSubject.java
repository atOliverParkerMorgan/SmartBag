package com.example.ontime.Activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.R;

import java.util.ArrayList;
import java.util.List;

public class EditSubject extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subject_edit);
        final String subject = (String) getIntent().getSerializableExtra("subjectName");
        TextView title = findViewById(R.id.subjectText);
        title.setText(subject);

        RecyclerView recyclerView = findViewById(R.id.ItemsInSubejct);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        List<Item> itemsInSubject = new ArrayList<>();

        final List<String> itemNames = FeedReaderDbHelperItems.getContent(this, subject);


        for(String item: itemNames) {
           itemsInSubject.add(new Item(item,subject));
        }

        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsInSubject,(byte) -1, findViewById(android.R.id.content).getRootView());
        // 4. set adapter
        recyclerView.setAdapter(mAdapterItemsToAdd);

    }
}