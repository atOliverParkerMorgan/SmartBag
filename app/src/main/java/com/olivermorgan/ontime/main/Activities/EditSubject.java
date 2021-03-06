package com.olivermorgan.ontime.main.Activities;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.MyListAdapter;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;
import java.util.ArrayList;
import java.util.List;

public class EditSubject extends AppCompatActivity {

    @SuppressLint("UseCompatLoadingForDrawables")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activity_subject_edit);

        final String subject = (String) getIntent().getSerializableExtra("subjectName");
        final EditText title = findViewById(R.id.editSubjectName);
        title.setText(subject);

        // setting up switches

        String[] daysOfTheWeek = FeedReaderDbHelperSubjects.getDaysOfSubjects(getApplicationContext(), subject);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        final Switch mon = findViewById(R.id.mondaySwitchEdit);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        final Switch tue = findViewById(R.id.tuesdaySwitchEdit);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        final Switch wed = findViewById(R.id.wednesdaySwitchEdit);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        final Switch thu = findViewById(R.id.thursdaySwitchEdit);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        final Switch fri = findViewById(R.id.fridaySwitchEdit);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        final Switch sat = findViewById(R.id.saturdaySwitchEdit);
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        final Switch sun = findViewById(R.id.sundaySwitchEdit);

        // hide weekend
        boolean weekendOnBoolean = SharedPrefs.getBoolean(this, SharedPrefs.WEEKEND_ON);

        if (weekendOnBoolean) {
            ((ViewManager) sat.getParent()).removeView(sat);
            ((ViewManager) sun.getParent()).removeView(sun);
        } else {
            ConstraintLayout constraintLayout = findViewById(R.id.parent);
            ConstraintSet constraintSet = new ConstraintSet();
            constraintSet.connect(R.id.sundaySwitchEdit, ConstraintSet.BOTTOM, R.id.textView, ConstraintSet.TOP, 64);
            constraintSet.applyTo(constraintLayout);

        }

        Switch[] daysOfWeek = new Switch[]{mon, tue, wed, thu, fri, sat, sun};
        for (int i = 0; i < daysOfWeek.length; i++) {
            daysOfWeek[i].setChecked(daysOfTheWeek[i].equals("true"));
        }


        // create a view holder for this layout
        final ViewHolder viewHolder = new ViewHolder();
        viewHolder.editItemsRecycleView.setLayoutManager(new LinearLayoutManager(this));


        // loop through all relevant subjects
        final List<Item> itemsDataItemsToEdit = new ArrayList<>();
        final List<String> itemNames = FeedReaderDbHelperItems.getContent(this, subject);
        for (String item : itemNames) {
            itemsDataItemsToEdit.add(new Item(item, subject, FeedReaderDbHelperItems.isInBag(getApplicationContext(), item), this));
        }


        // 3. create an adapter
        final MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToEdit, (byte) -10, findViewById(android.R.id.content), false, true, false, this);
        // 4. set adapter
        viewHolder.editItemsRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set itemAdd animator to DefaultAnimator

        // delete and save buttons
        viewHolder.saveButton.setOnClickListener(v -> {
            try {
                if(subject==null){
                    Toast.makeText(v.getContext(), getResources().getString(R.string.anError),
                            Toast.LENGTH_LONG).show();
                }
                else if(subject.equals("")) {
                    Toast.makeText(v.getContext(),  getResources().getString(R.string.nothingInSubjectFiled),
                            Toast.LENGTH_LONG).show();
                }
                else if(FeedReaderDbHelperItems.subjectExists(getApplicationContext(), subject) && !subject.equals(viewHolder.subjectName.getText().toString())){
                    Toast.makeText(v.getContext(), getResources().getString(R.string.subject)+viewHolder.subjectName.getText().toString()+getResources().getString(R.string.alreadyExist),
                            Toast.LENGTH_LONG).show();
                }else if(subject.length()>25){
                    Toast.makeText(v.getContext(), getResources().getString(R.string.tooLong),
                            Toast.LENGTH_LONG).show();
                }

                else if(!(mon.isChecked()||
                        tue.isChecked()||wed.isChecked()||thu.isChecked()||
                        fri.isChecked()||sat.isChecked()||sun.isChecked())){
                    Toast.makeText(v.getContext(), getResources().getString(R.string.mustChooseAtLeastOneDay),
                            Toast.LENGTH_LONG).show();
                }else {
                    FeedReaderDbHelperItems.deleteSubject(v.getContext(), subject);
                    FeedReaderDbHelperSubjects.edit(v.getContext(), subject, title.getText().toString(),
                            mon.isChecked(), tue.isChecked(), wed.isChecked(), thu.isChecked(),
                            fri.isChecked(), sat.isChecked(), sun.isChecked());
                    // item logic

                    // get items pre edit
                    // -1 means an Error has occurred
                    FeedReaderDbHelperSubjects.edit(v.getContext(), subject, viewHolder.subjectName.getText().toString());

                    Intent i = new Intent(EditSubject.this, MainActivity.class);
                    i.putExtra("Fragment", "overview");

                    i.putExtra("putInToBag", "null");
                    if (!FeedReaderDbHelperItems.write(v.getContext(), i, itemsDataItemsToEdit, viewHolder.subjectName.getText().toString())) {
                        Toast.makeText(v.getContext(), getResources().getString(R.string.databaseError),
                                Toast.LENGTH_LONG).show();
                    }

                    startActivity(i);
                }

            } catch (android.database.sqlite.SQLiteException e) {
                Toast.makeText(v.getContext(), getResources().getString(R.string.databaseError),
                        Toast.LENGTH_LONG).show();
            }


        });

        viewHolder.deleteButton.setOnClickListener(v -> {
            AlertDialog.Builder alert = new AlertDialog.Builder(v.getContext());
            alert.setTitle(getResources().getString(R.string.deleteSubject));
            alert.setMessage(getResources().getString(R.string.areYouSureDelete) + subject + " ?");
            alert.setPositiveButton(android.R.string.yes, (dialog, which) -> {
                FeedReaderDbHelperSubjects.deleteSubject(subject, this);
                Intent i = new Intent(EditSubject.this, MainActivity.class);
                i.putExtra("Fragment", "overview");
                startActivity(i);
            });
            alert.setNegativeButton(android.R.string.no, (dialog, which) -> {
                // close dialog
                dialog.cancel();
            });
            alert.show();


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
                    itemsDataItemsToEdit.add(new Item(viewHolder.itemName.getText().toString(), subject, false, this));

                    // create an adapter
                    @SuppressLint("CutPasteId") MyListAdapter mAdapterItemsToAdd1 = new MyListAdapter(itemsDataItemsToEdit, (byte) -10, findViewById(android.R.id.content), false, true, false, EditSubject.this);
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
        EditText subjectName;
        EditText itemName;
        Button saveButton;
        Button deleteButton;
        Button addItems;
        RecyclerView editItemsRecycleView;

        ViewHolder() {
            addItems = findViewById(R.id.addItem);
            subjectName = findViewById(R.id.editSubjectName);
            saveButton = findViewById(R.id.Save);
            deleteButton = findViewById(R.id.Delete);
            itemName = findViewById(R.id.editItem);
            editItemsRecycleView = findViewById(R.id.editItemsRecycleView);
            editItemsRecycleView.setNestedScrollingEnabled(false);
        }
    }
}