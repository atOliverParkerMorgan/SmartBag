package com.example.ontime.ui.Remove;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
import java.util.List;

public class RemoveFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // create view
        final View view = inflater.inflate(R.layout.fragment_remove, parent, false);

        // no items
        TextView noItems = view.findViewById(R.id.noItemsTextRemove);
        noItems.setAlpha(1.0f);

        // init database
        // get subject names that aren't for today
        final List<String> subjectNames = new ArrayList<>();
        for (List<String> list: FeedReaderDbHelperSubjects.getContent(getContext(), true)) {
            boolean found = false;
            for (List<String> list2: FeedReaderDbHelperSubjects.getContent(getContext(), false)) {
                if(list.equals(list2)){
                    found = true;
                    break;
                }
            }
            if(!found) {
                subjectNames.add(list.get(0));
            }
        }

        final RecyclerView ItemsToRemoveRecycleView = view.findViewById(R.id.ItemsToRemove);
        ItemsToRemoveRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // this is data for recycler view
        List<Item> inMyBag = new ArrayList<>();
        List<Item> itemsDataItemsToRemove = new ArrayList<>();

        // adding all of my items in bag to list
        final List<String[]> myBagItems = FeedReaderDbHelperMyBag.getContent(getContext());
        for (String[] item : myBagItems) {
            inMyBag.add(new Item(item[0], item[1]));
        }

        // loop through all relevant subjects
        for(String subject: subjectNames){

            // get the items that i need for today
            final List<String> itemsNotForToday = FeedReaderDbHelperItems.getContent(getContext(), subject);
            for (Item itemInBag: inMyBag){

                boolean inBag = false;
                String foundItem = "";
                for(String item: itemsNotForToday){
                    foundItem = item;
                    if (itemInBag.getItemName().equals(item) && itemInBag.getSubjectName().equals(subject)) {

                        inBag = true;
                        break;
                    }

                }
                if(inBag) {
                    itemsDataItemsToRemove.add(new Item(foundItem,subject));
                }


            }

        }

        // 3. create an adapter
        MyListAdapter mAdapterItemsToRemove = new MyListAdapter(itemsDataItemsToRemove,(byte) 0, view);
        // 4. set adapter
        ItemsToRemoveRecycleView.setAdapter(mAdapterItemsToRemove);
        // 5. set item to remove animator to DefaultAnimator
        ItemsToRemoveRecycleView.setItemAnimator(new DefaultItemAnimator());

        //instructions logic
        if(itemsDataItemsToRemove.size()>0){
            noItems.setAlpha(0.0f);
        }else{
            TextView instructions = view.findViewById(R.id.instructionsRemove);
            instructions.setAlpha(0.0f);
        }

        return view;
    }
}