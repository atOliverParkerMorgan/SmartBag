package com.olivermorgan.ontime.main.Activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.ViewManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;

import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import java.util.Objects;


public class AddSubjectOrOther extends AppCompatActivity {
    public static boolean firstViewOfActivity = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.activivty_add_subject);
        TextView title = findViewById(R.id.toolbar_title);

        EditText input = findViewById(R.id.editSubject);
        TextView textView = findViewById(R.id.addToBag);

        Item helper = new Item((String) getIntent().getSerializableExtra("name"), getString(R.string.subject), false, null, this);
        Button mark = findViewById(R.id.mark);

        final String type = (String) getIntent().getSerializableExtra("type");
        final String nameType = (String) getIntent().getSerializableExtra("name");


        switch(Objects.requireNonNull(type)){
            case "subject":
                mark.setBackgroundResource(R.drawable.circle_default);
                break;
            case "snack":
                mark.setBackgroundResource(R.drawable.square_default);
                input.setHint(R.string.snack_hint);
                textView.setText(R.string.add_to_bag_snack);
                helper = new Item(nameType, getString(R.string.snack), false, null, this);
                break;
            case "pencilCase":
                mark.setBackgroundResource(R.drawable.hexagon_default);
                input.setHint(R.string.pencilCase_hint);
                textView.setText(R.string.add_to_bag_pencilCase);
                helper = new Item(nameType, getString(R.string.pencilCase), false, null, this);
                break;
        }

        title.setText(helper.getItemName());
        mark.setText(helper.getNameInitialsOfSubject());


        final ViewHolder viewHolder = new ViewHolder();

        Toolbar toolbar = findViewById(R.id.toolbarAddSubject);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());


        // main logic thread
        new Thread(()-> {
            boolean weekendOnBoolean = SharedPrefs.getBoolean(this, SharedPrefs.WEEKEND_ON);

            if (weekendOnBoolean) {
                ((ViewManager) viewHolder.saturday.getParent()).removeView(viewHolder.saturday);
                ((ViewManager) viewHolder.sunday.getParent()).removeView(viewHolder.sunday);

                // logic for
                ConstraintLayout constraintLayout = findViewById(R.id.parent);
                ConstraintSet constraintSet = new ConstraintSet();
                constraintSet.clone(constraintLayout);
                constraintSet.connect(R.id.add_items, ConstraintSet.TOP, R.id.Friday, ConstraintSet.BOTTOM, 64);
                constraintSet.applyTo(constraintLayout);
            }
        }).start();

        viewHolder.addItems.setOnClickListener(v -> {
            String s = viewHolder.subjectName.getText().toString();
            if(s.equals("")) {
                Toast.makeText(v.getContext(), R.string.nothingInSubjectFiled,
                        Toast.LENGTH_LONG).show();
            }
            else if(FeedReaderDbHelperItems.subjectExists(getApplicationContext(), s)){
                Toast.makeText(v.getContext(), R.string.mainTitle+" "+s+" "+R.string.alreadyExist,
                Toast.LENGTH_LONG).show();
            }else if(s.length()>25){
                Toast.makeText(v.getContext(), R.string.tooLong,
                        Toast.LENGTH_LONG).show();
            }

            else if(!(viewHolder.monday.isChecked()||
                    viewHolder.tuesday.isChecked()||viewHolder.wednesday.isChecked()||viewHolder.thursday.isChecked()||
                    viewHolder.friday.isChecked()||viewHolder.saturday.isChecked()||viewHolder.sunday.isChecked())){
                Toast.makeText(v.getContext(), R.string.mustChooseAtLeastOneDay,
                        Toast.LENGTH_LONG).show();
            }

            else{

                Intent i = new Intent(AddSubjectOrOther.this, AddItem.class);
                i.putExtra("Subject", s);
                i.putExtra("type", type);
                i.putExtra("name", nameType);
                i.putExtra("Monday",Boolean.toString(viewHolder.monday.isChecked()));
                i.putExtra("Tuesday",Boolean.toString(viewHolder.tuesday.isChecked()));
                i.putExtra("Wednesday",Boolean.toString(viewHolder.wednesday.isChecked()));
                i.putExtra("Thursday",Boolean.toString(viewHolder.thursday.isChecked()));
                i.putExtra("Friday",Boolean.toString(viewHolder.friday.isChecked()));
                i.putExtra("Saturday",Boolean.toString(viewHolder.saturday.isChecked()));
                i.putExtra("Sunday",Boolean.toString(viewHolder.sunday.isChecked()));
                i.putExtra("putInToBag",viewHolder.putInToBag.isChecked());

                startActivity(i);
            }
        });



    }




    public class ViewHolder{
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch monday;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch tuesday;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch wednesday;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch thursday;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch friday;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch saturday;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch sunday;
        @SuppressLint("UseSwitchCompatOrMaterialCode")
        Switch putInToBag;

        EditText subjectName;
        Button addItems;

        ViewHolder(){
            monday = findViewById(R.id.mondayTextView);
            tuesday = findViewById(R.id.Tuesday);
            wednesday = findViewById(R.id.Wednesday);
            thursday = findViewById(R.id.Thursday);
            friday = findViewById(R.id.Friday);
            saturday = findViewById(R.id.Saturday);
            sunday = findViewById(R.id.Sunday);

            putInToBag = findViewById(R.id.addToBag);
            addItems = findViewById(R.id.add_items);
            subjectName = findViewById(R.id.editSubject);
        }
    }
}