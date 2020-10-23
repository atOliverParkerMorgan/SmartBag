package com.example.ontime.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
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
import java.util.Objects;

public class AddItem extends AppCompatActivity {
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
        setContentView(R.layout.activity_add_items);
        // Get subject from AddFragment
        final String subject = (String) getIntent().getSerializableExtra("Subject");

        // create a view holder for this layout
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.addedItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));

        // create an adapter
        final MyListAdapter mAdapterItemsToAdd = new MyListAdapter(new ArrayList<Item>(), (byte)-1, findViewById(android.R.id.content).getRootView(), false, false, false);

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
                    for(Item item: mAdapterItemsToAdd.getItems()){
                        if(item.getItemName().equals(viewHolder.itemName.getText().toString())){
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        // this is data for recycler view

                        mAdapterItemsToAdd.add(new Item(viewHolder.itemName.getText().toString(), subject, FeedReaderDbHelperItems.isInBag(getApplicationContext(), viewHolder.itemName.getText().toString())));


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

                if( mAdapterItemsToAdd.getItems().size()==0) {
                    Toast.makeText(v.getContext(),"You must add at least one item",Toast.LENGTH_LONG).show();
                }else {

                    // Insert the new row, returning the primary key value of the new row
                    // Insert the new row, returning the primary key value of the new row

                    // -1 means an Error has occurred
                    if ( !FeedReaderDbHelperItems.write(v.getContext(),  getIntent(),  mAdapterItemsToAdd.getItems())|| !FeedReaderDbHelperSubjects.write(v.getContext(), getIntent(), subject)) {
                        Toast.makeText(v.getContext(), "An error as occurred in the database report this issue",
                                Toast.LENGTH_LONG).show();
                    }
                    // go back to main activity
                    Intent i = new Intent(AddItem.this, MainActivity.class);
                    i.putExtra("Fragment","bag");
                    startActivity(i);
                }
            }
        });


        viewHolder.discard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                // go back to main activity
                Intent i = new Intent(AddItem.this, MainActivity.class);
                i.putExtra("Fragment","bag");
                startActivity(i);
            }
        });

        viewHolder.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    public class ViewHolder{
        EditText itemName;
        Button addItems;
        Button discard;
        Button create;
        ImageButton back;
        RecyclerView addedItemsRecycleView;

        ViewHolder(){
            back = findViewById(R.id.imageButtonBack);
            addItems = findViewById(R.id.addItem);
            itemName = findViewById(R.id.editItem);
            discard = findViewById(R.id.discard);
            create = findViewById(R.id.create);
            addedItemsRecycleView = findViewById(R.id.addedItemsRecycleView);
        }
    }
}
