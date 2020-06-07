package com.example.ontime;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.ui.Item;
import com.example.ontime.ui.MyListAdapter;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.security.AccessController.getContext;

public class AddItem extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Item List
        final List<Item> defaultItemsDataItemsToAdd = new ArrayList<>();

        setContentView(R.layout.add_items);
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.addedItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));
        viewHolder.addItems.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                String text = viewHolder.itemName.getText().toString();
                if(text.equals("")){
                    Toast.makeText(v.getContext(), "To add an item write some text into the text field.(Textbook)",
                            Toast.LENGTH_LONG).show();
                }else {

                    boolean found = false;
                    for(Item item: defaultItemsDataItemsToAdd){
                        if(item.getName().equals(viewHolder.itemName.getText().toString())){
                            found = true;
                            break;
                        }
                    }
                    if(!found) {
                        // this is data fro recycler view
                        defaultItemsDataItemsToAdd.add(new Item(viewHolder.itemName.getText().toString()));
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


            }
        });

        viewHolder.discard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
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
