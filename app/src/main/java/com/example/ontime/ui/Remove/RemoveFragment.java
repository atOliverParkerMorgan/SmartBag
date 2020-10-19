package com.example.ontime.ui.Remove;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.Activities.AddSubject;
import com.example.ontime.Activities.Settings;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;
import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

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
        SharedPreferences preferencesWeekendOn = Objects.requireNonNull(Objects.requireNonNull(getActivity()).getSharedPreferences("WeekendOn", android.content.Context.MODE_PRIVATE));
        boolean weekendOnBoolean = preferencesWeekendOn.getBoolean("Mode", true);
        Calendar calendar = Calendar.getInstance();
        boolean doNotShow = !((weekendOnBoolean&&calendar.getTime().toString().substring(0, 2).equals("Sa"))
                ||(weekendOnBoolean&&calendar.getTime().toString().substring(0, 2).equals("Su")));

        if(doNotShow) {
            for (List<String> list : FeedReaderDbHelperSubjects.getContent(getContext(), true)) {
                boolean found = false;
                for (List<String> list2 : FeedReaderDbHelperSubjects.getContent(getContext(), false)) {
                    if (list.equals(list2)) {
                        found = true;
                        break;
                    }
                }
                if (!found) {
                    subjectNames.add(list.get(0));
                }
            }
        }

        // image button logic add item
        ImageButton imageButtonAddSubject = view.findViewById(R.id.addSubjectButton);
        imageButtonAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting add Subject first to falls to avoid error
                AddSubject.firstViewOfActivity = true;
                Intent intent = new Intent(getActivity(), AddSubject.class);
                startActivity(intent);
            }
        });

        // image button logic settings
        ImageButton imageButtonSettings = view.findViewById(R.id.settingsButton);
        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        final RecyclerView ItemsToRemoveRecycleView = view.findViewById(R.id.ItemsToRemove);
        ItemsToRemoveRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // this is data for recycler view
        List<Item> inMyBag = new ArrayList<>();
        List<Item> itemsDataItemsToRemove = new ArrayList<>();
        if(doNotShow) {
            // adding all of my items in bag to list
            final List<String[]> myBagItems = FeedReaderDbHelperItems.getItemsInBag(getContext());
            for (String[] item : myBagItems) {
                inMyBag.add(new Item(item[0], item[1],  FeedReaderDbHelperItems.isInBag(getContext(), item[0])));
            }


            // loop through all relevant subjects

            for (String subject : subjectNames) {

                // get the items that i need for today
                final List<String> itemsNotForToday = FeedReaderDbHelperItems.getContent(getContext(), subject);
                for (Item itemInBag : inMyBag) {

                    boolean inBag = false;
                    String foundItem = "";
                    for (String item : itemsNotForToday) {
                        foundItem = item;
                        if (itemInBag.getItemName().equals(item) && itemInBag.getSubjectName().equals(subject)) {

                            inBag = true;
                            break;
                        }

                    }
                    if (inBag) {
                        itemsDataItemsToRemove.add(new Item(foundItem, subject, FeedReaderDbHelperItems.isInBag(getContext(),foundItem)));
                    }


                }

            }
        }

        // 3. create an adapter
        MyListAdapter mAdapterItemsToRemove = new MyListAdapter(itemsDataItemsToRemove,(byte) 0, view, true, false);
        // 4. set adapter
        ItemsToRemoveRecycleView.setAdapter(mAdapterItemsToRemove);
        // 5. set item to remove animator to DefaultAnimator
        ItemsToRemoveRecycleView.setItemAnimator(new DefaultItemAnimator());

        //instructions logic
        if(!doNotShow) noItems.setText(R.string.weekendText);
        if(itemsDataItemsToRemove.size()>0){
            noItems.setAlpha(0.0f);
        }else{
            TextView instructions = view.findViewById(R.id.instructionsRemove);
            instructions.setAlpha(0.0f);

        }

        return view;
    }
}