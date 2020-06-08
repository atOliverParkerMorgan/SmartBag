package com.example.ontime.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.ontime.R;



public class AddSubject extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activivty_add_subject);

        final ViewHolder viewHolder = new ViewHolder();

        viewHolder.addItems.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = viewHolder.subjectName.getText().toString();
                if(!(viewHolder.monday.isChecked()||viewHolder.monday.isChecked()||
                        viewHolder.tuesday.isChecked()||viewHolder.wednesday.isChecked()||viewHolder.thursday.isChecked()||
                        viewHolder.friday.isChecked()||viewHolder.saturday.isChecked()||viewHolder.sunday.isChecked())){
                    Toast.makeText(v.getContext(), "You have to choose at least one day of the week",
                            Toast.LENGTH_LONG).show();
                }

                else if(!s.equals("")) {

                    Intent i = new Intent(AddSubject.this, AddItem.class);
                    i.putExtra("Subject", s);
                    i.putExtra("Monday",Boolean.toString(viewHolder.monday.isChecked()));
                    i.putExtra("Tuesday",Boolean.toString(viewHolder.tuesday.isChecked()));
                    i.putExtra("Wednesday",Boolean.toString(viewHolder.wednesday.isChecked()));
                    i.putExtra("Thursday",Boolean.toString(viewHolder.thursday.isChecked()));
                    i.putExtra("Friday",Boolean.toString(viewHolder.friday.isChecked()));
                    i.putExtra("Saturday",Boolean.toString(viewHolder.saturday.isChecked()));
                    i.putExtra("Sunday",Boolean.toString(viewHolder.sunday.isChecked()));


                    startActivity(i);
                }else {
                    Toast.makeText(v.getContext(), "To add a subject write some text into the text field (Mathematics).",
                            Toast.LENGTH_LONG).show();
                }

            }
        });

    }
    public class ViewHolder{
        final Switch monday;
        final Switch tuesday;
        final Switch wednesday;
        final Switch thursday;
        final Switch friday;
        final  Switch saturday;
        final  Switch sunday;
        EditText subjectName;
        Button addItems;

        ViewHolder(){
            monday = findViewById(R.id.Monday);
            tuesday = findViewById(R.id.Tuesday);
            wednesday = findViewById(R.id.Wednesday);
            thursday = findViewById(R.id.Thursday);
            friday = findViewById(R.id.Friday);
            saturday = findViewById(R.id.Saturday);
            sunday = findViewById(R.id.Sunday);
            addItems = findViewById(R.id.add_items);
            subjectName = findViewById(R.id.editSubject);
        }
    }
}