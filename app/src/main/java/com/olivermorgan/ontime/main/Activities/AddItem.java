package com.olivermorgan.ontime.main.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.MyListAdapter;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;
import java.util.ArrayList;
import java.util.Objects;

public class AddItem extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_add_items);

        Toolbar toolbar = findViewById(R.id.toolbarAddItems);
        toolbar.setNavigationOnClickListener(v ->{
        Intent i = new Intent(AddItem.this, WhatToAdd.class);
        startActivity(i);});

        EditText input = findViewById(R.id.editItem);
        TextView title = findViewById(R.id.toolbar_title);
        Button mark = findViewById(R.id.mark2);

        final String type = (String) getIntent().getSerializableExtra("type");

        Item helper = new Item(getString(R.string.title_add_item), type, false, null, this);

        switch(Objects.requireNonNull(type)){
            case "subject":
                mark.setBackgroundResource(R.drawable.circle_default);
                break;
            case "snack":
                mark.setBackgroundResource(R.drawable.square_default);
                input.setHint(R.string.snack_hint);
                helper = new Item(getString(R.string.title_add_food),type, false, null, this);;
                break;
            case "pencilCase":
                mark.setBackgroundResource(R.drawable.hexagon_default);
                input.setHint(R.string.pencilCase_hint);
                helper = new Item(getString(R.string.title_add_pens),type , false, null, this);;

                break;
        }

        title.setText(helper.getItemName());
        mark.setText(helper.getNameInitialsOfSubject());


        // Get subject from AddFragment
        final String subject = (String) getIntent().getSerializableExtra("Subject");


        // create a view holder for this layout
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.addedItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        // create an adapter
        final MyListAdapter mAdapterItemsToAdd = new MyListAdapter(new ArrayList<>(), (byte)-1, findViewById(android.R.id.content).getRootView(), false, false, false,this );

        // Add items this onclick listener add an item to the recycle viewer
        viewHolder.addItems.setOnClickListener(v -> {
            // getting the editText input
            String text = viewHolder.itemName.getText().toString();
            // the user has to input some text


            if(text.equals("")){
                Toast.makeText(v.getContext(), getResources().getString(R.string.nothingInSubjectFiled),
                        Toast.LENGTH_LONG).show();
            }else {
                // also the item cannot already be in the recycle viewer
                boolean found = false;
                for(Item item: mAdapterItemsToAdd.getItems()){
                    if(item.getItemName().equals(viewHolder.itemName.getText().toString())){
                        found = true;
                        break;
                    }
                }
                if(!found) {
                    // this is data for recycler view

                    mAdapterItemsToAdd.add(new Item(viewHolder.itemName.getText().toString(), subject, FeedReaderDbHelperItems.isInBag(getApplicationContext(), viewHolder.itemName.getText().toString()), type,this));


                    // set adapter
                    viewHolder.addedItemsRecycleView.setAdapter(mAdapterItemsToAdd);
                    // set itemAdd animator to DefaultAnimator
                    viewHolder.addedItemsRecycleView.setItemAnimator(new DefaultItemAnimator());

                    viewHolder.itemName.setText("");

                }else{
                    Toast.makeText(v.getContext(), R.string.itemAlreadyAdded,
                            Toast.LENGTH_LONG).show();
                }

            }

        });

        viewHolder.create.setOnClickListener(v -> {

            if( mAdapterItemsToAdd.getItems().size()==0) {
                Toast.makeText(v.getContext(),R.string.atLeastOneItem,Toast.LENGTH_LONG).show();
            }else {

                // Insert the new row, returning the primary key value of the new row

                // false means an Error has occurred
                if ( !FeedReaderDbHelperItems.write(v.getContext(),  getIntent(),  mAdapterItemsToAdd.getItems())|| !FeedReaderDbHelperSubjects.write(v.getContext(), getIntent(), subject, type)) {
                    Toast.makeText(v.getContext(), R.string.databaseError,
                            Toast.LENGTH_LONG).show();
                }
                // go back to main activity
                Intent i = new Intent(AddItem.this, MainActivity.class);
                i.putExtra("Fragment","bag");
                startActivity(i);
            }
        });


        viewHolder.discard.setOnClickListener(v -> {
            // go back to main activity
            Intent i = new Intent(AddItem.this, MainActivity.class);
            i.putExtra("Fragment","bag");
            startActivity(i);
        });


    }

    public class ViewHolder{
        EditText itemName;
        Button addItems;
        Button discard;
        Button create;
        RecyclerView addedItemsRecycleView;

        ViewHolder(){
            addItems = findViewById(R.id.addItem);
            itemName = findViewById(R.id.editItem);
            discard = findViewById(R.id.discard);
            create = findViewById(R.id.create);
            addedItemsRecycleView = findViewById(R.id.addedItemsRecycleView);
        }
    }
}
