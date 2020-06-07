package com.example.ontime.ui.Remove;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperMyBag;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;
import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // create view
        final View view = inflater.inflate(R.layout.fragment_remove, parent, false);

        // init database
        final List<String> subjectNames = FeedReaderDbHelperSubjects.getContent(getContext());

        final RecyclerView ItemsToRemoveRecycleView = view.findViewById(R.id.ItemsToRemove);
        ItemsToRemoveRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // this is data for recycler view
        List<Item> inMyBag = new ArrayList<>();
        List<Item> itemsDataItemsToRemove = new ArrayList<>();
        for(String subject: subjectNames) {
            final List<String> myBagItems = FeedReaderDbHelperMyBag.getContent(getContext(), subject);
            for (String item : myBagItems) {
                inMyBag.add(new Item(item, subject));
            }
        }


        // loop through all relevant subjects
        for(String subject: subjectNames){

            final List<String> itemNames = FeedReaderDbHelperItems.getContent(getContext(), subject);
            for(String item: itemNames){

                // checking if item isn't already in bag
                boolean found = false;
                for (Item itemInBag: inMyBag){
                    if(itemInBag.getItemName().equals(item) && itemInBag.getSubjectName().equals(subject)){
                        found = true;
                        break;
                    }
                }
                if(found) {
                    itemsDataItemsToRemove.add(new Item(item, subject));
                }
            }

        }



        // 3. create an adapter
        MyListAdapter mAdapterItemsToRemove = new MyListAdapter(itemsDataItemsToRemove,(byte) 0);
        // 4. set adapter
        ItemsToRemoveRecycleView.setAdapter(mAdapterItemsToRemove);
        // 5. set item to remove animator to DefaultAnimator
        ItemsToRemoveRecycleView.setItemAnimator(new DefaultItemAnimator());

        return view;
    }
}