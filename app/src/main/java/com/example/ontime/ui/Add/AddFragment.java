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
        final List<String> subjectNames = FeedReaderDbHelperSubjects.getContent(getContext());

        final RecyclerView ItemsToAddRecycleView = view.findViewById(R.id.ItemsToAdd);
        ItemsToAddRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // this is data fro recycler view
        List<Item> itemsDataItemsToAdd = new ArrayList<>();



        // loop through all relevant subjects
        for(String subject: subjectNames){
            final List<String> itemNames = FeedReaderDbHelperItems.getContent(getContext(), subject);
            for(String item: itemNames){
                itemsDataItemsToAdd.add(new Item(item,subject));
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