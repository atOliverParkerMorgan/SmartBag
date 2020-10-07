package com.example.ontime.ui.Bag;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.Activities.AddSubject;
import com.example.ontime.Adapter.Item;
import com.example.ontime.Adapter.MyBagAdapter;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperMyBag;
import com.example.ontime.R;
import com.example.ontime.Activities.Settings;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class BagFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                            final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_in_bag, container, false);

        // no items
        TextView noItems = view.findViewById(R.id.noItemsInBag);
        noItems.setAlpha(1.0f);

        // floating button logic add item
        FloatingActionButton floatingButtonAddItem = view.findViewById(R.id.floatingActionButton);
        floatingButtonAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting add Subject first to falls to avoid error
                AddSubject.firstViewOfActivity = true;
                Intent intent = new Intent(getActivity(), AddSubject.class);
                startActivity(intent);
            }
        });

        // floating button logic settings
        FloatingActionButton floatingButtonSettings = view.findViewById(R.id.floatingActionButtonSettings);
        floatingButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), Settings.class);
                startActivity(intent);
            }
        });

        // init database
        final List<String[]> subjectNames = FeedReaderDbHelperMyBag.getContent(getContext());
        final RecyclerView ItemsInBagRecycleView = view.findViewById(R.id.RecycleViewYourBag);
        ItemsInBagRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Item> inMyBag = new ArrayList<>();

        // this is data for recycler view

        final List<String[]> myBagItems = FeedReaderDbHelperMyBag.getContent(getContext());
        for (String[] item : myBagItems) {
            inMyBag.add(new Item(item[0], item[1]));
        }

        // loop through all relevant subjects

        // create an adapter
        MyBagAdapter mAdapterItemsToAdd = new MyBagAdapter(inMyBag);
        // set adapter
        ItemsInBagRecycleView.setAdapter(mAdapterItemsToAdd);
        // set itemAdd animator to DefaultAnimator
        ItemsInBagRecycleView.setItemAnimator(new DefaultItemAnimator());

        //instructions logic
        if(myBagItems.size()>0){
            noItems.setAlpha(0.0f);
        }else{
            TextView instructions = view.findViewById(R.id.instructions_bag);
            instructions.setAlpha(0.0f);
        }

        return view;
    }
}
