package com.olivermorgan.ontime.main.Activities;

import android.content.Intent;
import android.os.Bundle;
import java.util.ArrayList;
import java.util.List;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.WhatToAddAdapter;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

public class WhatToAdd extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_what_to_add);
        List<Item> types = new ArrayList<Item>(){};
        types.add(new Item(getString(R.string.subject),getString(R.string.subject),false,null,this));
        types.add(new Item(getString(R.string.snack),getString(R.string.snack),false,null,this));
        types.add(new Item(getString(R.string.pencilCase),getString(R.string.pencilCase),false,null,this));

        final RecyclerView ItemsInBagRecycleView = findViewById(R.id.RecycleViewYourBag);
        ItemsInBagRecycleView.setLayoutManager(new LinearLayoutManager(this));
        ItemsInBagRecycleView.setAdapter(new WhatToAddAdapter(types, this));
        ItemsInBagRecycleView.setItemAnimator(new DefaultItemAnimator());


        Toolbar toolbar = findViewById(R.id.toolbarWhatToAdd);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            // if the database is updated while activities are changed => app crashes
            // this prevents it
            // checkout MainActivity
            intent.putExtra("dontUpdateDatabase", true);
            startActivity(intent);
            finish();
            overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
        });

        super.onCreate(savedInstanceState);
    }
}