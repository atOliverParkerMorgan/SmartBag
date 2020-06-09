package com.example.ontime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;
import com.example.ontime.R;

import java.util.ArrayList;
import java.util.List;

public class AddItem extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get subject from AddFragment
        final String subject = (String) getIntent().getSerializableExtra("Subject");
        // Item List
        final List<Item> defaultItemsDataItemsToAdd = new ArrayList<>();

        setContentView(R.layout.add_items);
        // create a view holder for this layout
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.addedItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        // Add items this onclick listener add an item to the recycle viewer
        viewHolder.addItems.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // getting the editText input
                String text = viewHolder.itemName.getText().toString();
                // the user has to input some text
                if(text.equals("")){
                    Toast.makeText(v.getContext(), "To add an item write some text into the text field (Textbook).",
                            Toast.LENGTH_LONG).show();
                }else {
                    // also the item cannot already be in the recycle viewer
                    boolean found = false;
                    for(Item item: defaultItemsDataItemsToAdd){
                        if(item.getItemName().equals(viewHolder.itemName.getText().toString())){
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        // this is data fro recycler view
                        defaultItemsDataItemsToAdd.add(new Item(viewHolder.itemName.getText().toString(), subject));
                        List<Item> itemsDataItemsToAdd = new ArrayList<>(defaultItemsDataItemsToAdd);

                        // create an adapter
                        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToAdd, (byte)-1);
                        // set adapter
                        viewHolder.addedItemsRecycleView.setAdapter(mAdapterItemsToAdd);
                        // set itemAdd animator to DefaultAnimator
                        viewHolder.addedItemsRecycleView.setItemAnimator(new DefaultItemAnimator());

                        viewHolder.itemName.setText("");

                    }else{
                        Toast.makeText(v.getContext(), "You have already added this item.",
                                Toast.LENGTH_LONG).show();
                    }

                }

            }
        });

        viewHolder.create.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                if(defaultItemsDataItemsToAdd.size()==0) {
                    Toast.makeText(v.getContext(),"You must add at least one item",Toast.LENGTH_LONG).show();
                }else {

                    // Insert the new row, returning the primary key value of the new row
                    // Insert the new row, returning the primary key value of the new row

                    // -1 means an Error has occurred
                    if ( !FeedReaderDbHelperItems.write(v.getContext(),  getIntent(), defaultItemsDataItemsToAdd)|| !FeedReaderDbHelperSubjects.write(v.getContext(), getIntent(), subject)) {
                        Toast.makeText(v.getContext(), "An error as occurred in the database report this issue",
                                Toast.LENGTH_LONG).show();
                    }
                    // go back to main activity
                    Intent i = new Intent(AddItem.this, MainActivity.class);
                    startActivity(i);
                }


            }
        });

        viewHolder.discard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // go back to main activity
                Intent i = new Intent(AddItem.this, MainActivity.class);
                startActivity(i);
            }
        });



    }
    public class ViewHolder{

        EditText itemName;
        Button addItems;
        Button discard;
        Button create;
        RecyclerView addedItemsRecycleView;

        ViewHolder(){
            addItems = findViewById(R.id.add_item);
            itemName = findViewById(R.id.editItem);
            discard = findViewById(R.id.discard);
            create = findViewById(R.id.create);
            addedItemsRecycleView = findViewById(R.id.addedItemsRecycleView);
        }
    }
}
