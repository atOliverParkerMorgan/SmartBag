package com.example.ontime.Activities;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.Adapter.Item;
import com.example.ontime.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EditItem extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);

        final String subject = (String) getIntent().getSerializableExtra("Subject");
        List<Item> defaultItemDataItemsToEdit = new ArrayList<>();

        SharedPreferences preferences = Objects.requireNonNull(this.getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE));
        boolean darkModeOn = preferences.getBoolean("Mode", false);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.edit_item);

        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.addedItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));


    }
    public static class ViewHolder{

        EditText itemName;
        Button addItems;
        Button discard;
        Button create;
        RecyclerView addedItemsRecycleView;

        ViewHolder(){
//            addItems = findViewById(R.id.add_item);
//            itemName = findViewById(R.id.editItem);
//            discard = findViewById(R.id.discard);
//            create = findViewById(R.id.create);
//            addedItemsRecycleView = findViewById(R.id.addedItemsRecycleView);
        }
    }
}
