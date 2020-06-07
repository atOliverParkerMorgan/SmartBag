package com.example.ontime;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
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

                        // 3. create an adapter
                        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToAdd, (byte)-1);
                        // 4. set adapter
                        viewHolder.addedItemsRecycleView.setAdapter(mAdapterItemsToAdd);
                        // 5. set itemAdd animator to DefaultAnimator
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
                // adding to database
                // DataBase work
                FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(v.getContext());
                FeedReaderDbHelperItems dbHelperForItems = new FeedReaderDbHelperItems(v.getContext());

                // Gets the data repository in write mode
                SQLiteDatabase dbForSubject = dbHelperForSubject.getWritableDatabase();
                SQLiteDatabase dbForItems = dbHelperForItems.getWritableDatabase();

                // Create a new map of values, where column names are the keys
                ContentValues valuesForSubject = new ContentValues();
                valuesForSubject.put(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TITLE, subject);
                valuesForSubject.put(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_MONDAY, (String) getIntent().getSerializableExtra("Monday"));
                valuesForSubject.put(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TUESDAY, (String) getIntent().getSerializableExtra("Tuesday"));
                valuesForSubject.put(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_WEDNESDAY, (String) getIntent().getSerializableExtra("Wednesday"));
                valuesForSubject.put(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_THURSDAY, (String) getIntent().getSerializableExtra("Thursday"));
                valuesForSubject.put(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_FRIDAY, (String) getIntent().getSerializableExtra("Friday"));
                valuesForSubject.put(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_SATURDAY, (String) getIntent().getSerializableExtra("Saturday"));
                valuesForSubject.put(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_SUNDAY, (String) getIntent().getSerializableExtra("Sunday"));

                // adding data to table
                ContentValues valuesForItems = new ContentValues();
                for(Item item: defaultItemsDataItemsToAdd){
                    valuesForItems.put(FeedReaderDbHelperItems.FeedEntry.COLUMN_NAME_TITLE,item.getItemName());
                    valuesForItems.put(FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE, subject);
                }

                // Insert the new row, returning the primary key value of the new row
                long newRowId = dbForSubject.insert(FeedReaderDbHelperSubjects.FeedEntry.TABLE_NAME, null, valuesForSubject);

                // Insert the new row, returning the primary key value of the new row
                long newRowId2 = dbForItems.insert(FeedReaderDbHelperItems.FeedEntry.TABLE_NAME, null, valuesForItems);

                // -1 means an Error has occurred
                if(newRowId==-1||newRowId2==-1){
                    Toast.makeText(v.getContext(), "An error as occurred in the database report this issue",
                            Toast.LENGTH_LONG).show();
                }
                // go back to main activity
                Intent i = new Intent(AddItem.this, MainActivity.class);
                startActivity(i);


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
