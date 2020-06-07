package com.example.ontime.ui.AddSubject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ontime.AddItem;
import com.example.ontime.R;

import java.util.Objects;


public class AddSubjectFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_subject, container, false);
        final ViewHolder viewHolder = new ViewHolder(view);



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

                    Intent i = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), AddItem.class);
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

        return view;
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

        ViewHolder(View view){
            monday = view.findViewById(R.id.Monday);
            tuesday = view.findViewById(R.id.Tuesday);
            wednesday = view.findViewById(R.id.Wednesday);
            thursday = view.findViewById(R.id.Thursday);
            friday = view.findViewById(R.id.Friday);
            saturday = view.findViewById(R.id.Saturday);
            sunday = view.findViewById(R.id.Sunday);
            addItems = view.findViewById(R.id.add_items);
            subjectName = view.findViewById(R.id.editSubject);
        }
    }
}