package com.example.ontime.ui.dashboard;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.Editable;
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
import com.example.ontime.ui.FeedReaderDbHelper;

import java.util.Objects;


public class AddItemsFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add_subject, container, false);
        final ViewHolder viewHolder = new ViewHolder(view);



        viewHolder.addItems.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String s = viewHolder.subjectName.getText().toString();
                if(!s.equals("")) {
                    // DataBase work
                    FeedReaderDbHelper dbHelper = new FeedReaderDbHelper(getContext());
                    // Gets the data repository in write mode
                    SQLiteDatabase db = dbHelper.getWritableDatabase();

                    // Create a new map of values, where column names are the keys
                    ContentValues values = new ContentValues();
                    values.put(FeedReaderDbHelper.FeedEntry.COLUMN_NAME_TITLE, s);
                    values.put(FeedReaderDbHelper.FeedEntry.COLUMN_NAME_MONDAY, viewHolder.monday.toString());
                    values.put(FeedReaderDbHelper.FeedEntry.COLUMN_NAME_TEUSDAY, viewHolder.tuesday.toString());
                    values.put(FeedReaderDbHelper.FeedEntry.COLUMN_NAME_WEDNESDAY, viewHolder.wednesday.toString());
                    values.put(FeedReaderDbHelper.FeedEntry.COLUMN_NAME_THURSDAY, viewHolder.thursday.toString());
                    values.put(FeedReaderDbHelper.FeedEntry.COLUMN_NAME_FRIDAY, viewHolder.friday.toString());
                    values.put(FeedReaderDbHelper.FeedEntry.COLUMN_NAME_SATURDAY, viewHolder.saturday.toString());
                    values.put(FeedReaderDbHelper.FeedEntry.COLUMN_NAME_SUNDAY, viewHolder.sunday.toString());


                    // Insert the new row, returning the primary key value of the new row
                    long newRowId = db.insert(FeedReaderDbHelper.FeedEntry.TABLE_NAME, null, values);

                    Intent i = new Intent(Objects.requireNonNull(getActivity()).getApplicationContext(), AddItem.class);
                    Toast.makeText(v.getContext(), Long.toString(newRowId),
                            Toast.LENGTH_LONG).show();

                    startActivity(i);
                }else {
                    Toast.makeText(v.getContext(), "To add an subject write some text into the text field.",
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