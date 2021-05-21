package com.olivermorgan.ontime.main.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.MyListAdapter;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.RozvrhDen;
import com.olivermorgan.ontime.main.BakalariAPI.rozvrh.items.RozvrhHodina;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.olivermorgan.ontime.main.Logic.LoadBag;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import java.util.ArrayList;
import java.util.List;

public class EditSubjectLoggedin extends AppCompatActivity {

    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_subject_edit_loggedin);

        final String subject = (String) getIntent().getSerializableExtra("subjectName");
        final TextView title = findViewById(R.id.editSubjectName);
        title.setText(subject);

        // setting up switches

        // create a view holder for this layout
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.editItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));


        // loop through all relevant subjects
        final List<Item> itemsDataItemsToEdit = new ArrayList<>();
        final List<String> itemNames = FeedReaderDbHelperItems.getContent(this, subject);
        for (String item : itemNames) {
            itemsDataItemsToEdit.add(new Item(item, subject, FeedReaderDbHelperItems.isInBag(getApplicationContext(), item), FeedReaderDbHelperSubjects.getType(this, subject), this));
        }


        // 3. create an adapter
        final MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToEdit, (byte) -10, findViewById(android.R.id.content), false, true, false, this);
        // 4. set adapter
        viewHolder.editItemsRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set itemAdd animator to DefaultAnimator

        TextView info = findViewById(R.id.info);
        String teacher = getIntent().getStringExtra("teacher").equals("")?"":getResources().getString(R.string.Teacher)+" "+getIntent().getStringExtra("teacher")+"\n";
        String topic = getIntent().getStringExtra("topic").equals("")?"":getResources().getString(R.string.Topic)+" "+getIntent().getStringExtra("topic")+"\n";
        String time = getIntent().getStringExtra("time1").equals("")?"":getResources().getString(R.string.Time)+" "+getIntent().getStringExtra("time1")+" - "+getIntent().getStringExtra("time2")+"\n";
        info.setText(teacher+topic+time);





        // delete and save buttons
        viewHolder.saveButton.setOnClickListener(v -> {
            try {
                // get items pre edit
                FeedReaderDbHelperItems.deleteSubject(v.getContext(), subject);
                Intent i = new Intent(EditSubjectLoggedin.this, MainActivity.class);
                i.putExtra("Fragment", "overview");

                i.putExtra("putInToBag", "null");
                if (!FeedReaderDbHelperItems.write(v.getContext(), i, itemsDataItemsToEdit, viewHolder.subjectName.getText().toString())) {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.databaseError),
                            Toast.LENGTH_LONG).show();
                }

                startActivity(i);
            } catch (android.database.sqlite.SQLiteException e) {
                Toast.makeText(v.getContext(), R.string.databaseError,
                        Toast.LENGTH_LONG).show();
            }


        });

        viewHolder.addItems.setOnClickListener(v -> {
            // getting the editText input
            String text = viewHolder.itemName.getText().toString();
            // the user has to input some text
            if (text.equals("")) {
                Toast.makeText(v.getContext(), getResources().getString(R.string.nothingInSubjectFiled),
                        Toast.LENGTH_LONG).show();
            } else {
                // also the item cannot already be in the recycle viewer
                boolean found = false;
                for (Item item : itemsDataItemsToEdit) {
                    if (item.getItemName().equals(viewHolder.itemName.getText().toString())) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    // this is data for recycler view
                    itemsDataItemsToEdit.add(new Item(viewHolder.itemName.getText().toString(), subject, false, FeedReaderDbHelperSubjects.getType(this, subject), this));

                    // create an adapter
                    @SuppressLint("CutPasteId") MyListAdapter mAdapterItemsToAdd1 = new MyListAdapter(itemsDataItemsToEdit, (byte) -10, findViewById(android.R.id.content), false, true, false, EditSubjectLoggedin.this);
                    // set adapter
                    viewHolder.editItemsRecycleView.setAdapter(mAdapterItemsToAdd1);
                    // set itemAdd animator to DefaultAnimator
                    viewHolder.editItemsRecycleView.setItemAnimator(new DefaultItemAnimator());

                    viewHolder.itemName.setText("");

                } else {
                    Toast.makeText(v.getContext(), getResources().getString(R.string.itemAlreadyAdded),
                            Toast.LENGTH_LONG).show();
                }

            }

        });
    }

    class ViewHolder {
        TextView subjectName;
        EditText itemName;
        Button saveButton;

        Button addItems;
        RecyclerView editItemsRecycleView;

        ViewHolder() {
            addItems = findViewById(R.id.addItem);
            subjectName = findViewById(R.id.editSubjectName);
            saveButton = findViewById(R.id.Save);
            itemName = findViewById(R.id.editItem);
            editItemsRecycleView = findViewById(R.id.editItemsRecycleView);
            editItemsRecycleView.setNestedScrollingEnabled(false);
        }
    }
}