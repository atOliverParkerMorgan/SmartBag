package com.example.ontime.ui.Add;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.Activities.AddSubject;
import com.example.ontime.Activities.Settings;
import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyListAdapter;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperItems;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

public class AddFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {

        // create view
        final View view = inflater.inflate(R.layout.fragment_add, parent, false);
        // no items
        TextView noItems = view.findViewById(R.id.noItemsTextAdd);
        noItems.setAlpha(1.0f);

        // init database
        final List<String> subjectNames = new ArrayList<>();
        for (List<String> list: FeedReaderDbHelperSubjects.getContent(getContext(), false)) {
           subjectNames.add(list.get(0));
        }


        final RecyclerView ItemsToAddRecycleView = view.findViewById(R.id.ItemsToAdd);
        ItemsToAddRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));

        // Toolbar

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

        // this is data for recycler view
        List<Item> inMyBag = new ArrayList<>();
        List<Item> itemsDataItemsToAdd = new ArrayList<>();

        final List<String[]> myBagItems = FeedReaderDbHelperItems.getItemsInBag(getContext());

        //a
        for (String[] item : myBagItems) {
            inMyBag.add(new Item(item[0], item[1], FeedReaderDbHelperItems.isInBag(getContext(), item[0])));
        }
        SharedPreferences preferencesWeekendOn = Objects.requireNonNull(Objects.requireNonNull(getActivity()).getSharedPreferences("WeekendOn", android.content.Context.MODE_PRIVATE));
        boolean weekendOnBoolean = preferencesWeekendOn.getBoolean("Mode", true);
        Calendar calendar = Calendar.getInstance();
        boolean doNotShow = !((weekendOnBoolean&&calendar.getTime().toString().substring(0, 2).equals("Sa"))
                ||(weekendOnBoolean&&calendar.getTime().toString().substring(0, 2).equals("Su")));

        if(doNotShow) {
            // loop through all relevant subjects
            for (String subject : subjectNames) {

                final List<String> itemNames = FeedReaderDbHelperItems.getContent(getContext(), subject);
                for (String item : itemNames) {

                    // checking if item isn't already in bag
                    boolean found = false;
                    for (Item itemInBag : inMyBag) {
                        if (itemInBag.getItemName().equals(item) && itemInBag.getSubjectName().equals(subject)) {
                            found = true;
                            break;
                        }
                    }
                    if (!found) {
                        itemsDataItemsToAdd.add(new Item(item, subject,  FeedReaderDbHelperItems.isInBag(getContext(), item)));
                    }
                }

            }
        }



        // 3. create an adapter
        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToAdd,(byte) 1,view, true, false);
        // 4. set adapter
        ItemsToAddRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set itemAdd animator to DefaultAnimator
        ItemsToAddRecycleView.setItemAnimator(new DefaultItemAnimator());

        //instructions logic
        if(!doNotShow) noItems.setText(R.string.weekendText);

        if(itemsDataItemsToAdd.size()>0){
            noItems.setAlpha(0.0f);
        }else{
            TextView instructions = view.findViewById(R.id.instructionsAdd);
            instructions.setAlpha(0.0f);
        }

        return view;
    }

    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // TODO Add your menu entries here
        super.onCreateOptionsMenu(menu, inflater);
        MenuItem fav;
        fav = menu.add("add");
        fav.setIcon(R.drawable.ic_to_add);

    }

}