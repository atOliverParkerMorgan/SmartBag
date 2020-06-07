package com.example.ontime.ui.Add;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;
import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

public class AddFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // create view
        final View view = inflater.inflate(R.layout.fragment_add, parent, false);

        // init database
        FeedReaderDbHelperSubjects dbHelperForSubject = new FeedReaderDbHelperSubjects(getContext());
        SQLiteDatabase dbForSubject = dbHelperForSubject.getReadableDatabase();
        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projectionSubject = {
                BaseColumns._ID,
                FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TITLE,
                FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_MONDAY,
                FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TUESDAY,
                FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_WEDNESDAY,
                FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_FRIDAY,
                FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_SATURDAY,
                FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_SUNDAY
        };
        // subset is initialized in switch statement
        String selectionSubject = null;
        String[] selectionArgsSubject = {"true"};

        // get day of the week
        String dayOfTheWeek;
        Calendar calendar = Calendar.getInstance();

        switch (calendar.getTime().toString().substring(0,2)){
            case "Mo":
                selectionSubject = FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_MONDAY + " = ?";
                dayOfTheWeek = "Monday";
                break;
            case "Tu":
                selectionSubject = FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TUESDAY + " = ?";
                dayOfTheWeek = "Tuesday";
                break;
            case "We":
                selectionSubject = FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_WEDNESDAY + " = ?";
                dayOfTheWeek = "Wednesday";
                break;
            case "Th":
                selectionSubject = FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_THURSDAY + " = ?";
                dayOfTheWeek = "Thursday";
                break;
            case "Fr":
                selectionSubject = FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_FRIDAY + " = ?";
                dayOfTheWeek = "Friday";
                break;
            case "Sa":
                selectionSubject = FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_SATURDAY + " = ?";
                dayOfTheWeek = "Saturday";
                break;
            case "Su":
                selectionSubject = FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_SUNDAY + " = ?";
                dayOfTheWeek = "Sunday";
                break;
        }

        // How you want the results sorted in the resulting Cursor
        String sortOrderSubject =
                FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TITLE + " DESC";

        Cursor cursorSubject = dbForSubject.query(
                FeedReaderDbHelperSubjects.FeedEntry.TABLE_NAME,   // The table to query
                projectionSubject,             // The array of columns to return (pass null to get all)
                selectionSubject,              // The columns for the WHERE clause
                selectionArgsSubject,          // The values for the WHERE clause
                null,                   // don't group the rows
                null,                   // don't filter by row groups
                sortOrderSubject               // The sort order
        );

        // get all the subjects that have today marked as true
        List<String> subjectNames = new ArrayList<>();
        while(cursorSubject.moveToNext()) {
            String subject = cursorSubject.getString(
                    cursorSubject.getColumnIndexOrThrow(FeedReaderDbHelperSubjects.FeedEntry.COLUMN_NAME_TITLE));
            subjectNames.add(subject);
        }
        cursorSubject.close();




        final RecyclerView ItemsToAddRecycleView = view.findViewById(R.id.ItemsToAdd);
        ItemsToAddRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // this is data fro recycler view
        List<Item> itemsDataItemsToAdd = new ArrayList<>();

        // init items database
        FeedReaderDbHelperItems dbHelperForItems = new FeedReaderDbHelperItems(getContext());
        SQLiteDatabase dbForItems = dbHelperForItems.getReadableDatabase();
        String[] projectionItems = {
                BaseColumns._ID,
                FeedReaderDbHelperItems.FeedEntry.COLUMN_NAME_TITLE,
                FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE,

        };

        // loop through all relevant subjects
        for(String subject: subjectNames){
            String selectionItems = FeedReaderDbHelperItems.FeedEntry.COLUMN_SUBJECT_TITLE + " = ?";
            String[] selectionArgsItems = {subject}; // find items for the given subject
            String sortOrderItems =
                    FeedReaderDbHelperItems.FeedEntry.COLUMN_NAME_TITLE + " DESC";

            Cursor cursorItems = dbForItems.query(
                    FeedReaderDbHelperItems.FeedEntry.TABLE_NAME,   // The table to query
                    projectionItems,             // The array of columns to return (pass null to get all)
                    selectionItems,              // The columns for the WHERE clause
                    selectionArgsItems,          // The values for the WHERE clause
                    null,                   // don't group the rows
                    null,                   // don't filter by row groups
                    sortOrderItems               // The sort order
            );
            while(cursorItems.moveToNext()) {
                String itemName = cursorItems.getString(
                        cursorItems.getColumnIndexOrThrow(FeedReaderDbHelperItems.FeedEntry.COLUMN_NAME_TITLE));
                itemsDataItemsToAdd.add(new Item(itemName,subject));

            }
        }

        // 3. create an adapter
        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToAdd,(byte) 1);
        // 4. set adapter
        ItemsToAddRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set itemAdd animator to DefaultAnimator
        ItemsToAddRecycleView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }

}