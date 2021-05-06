package com.olivermorgan.ontime.main.Fragments.Bag;

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


import com.olivermorgan.ontime.main.Activities.MainActivity;
import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.MyBagAdapter;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;

import com.olivermorgan.ontime.main.R;


import java.util.ArrayList;
import java.util.List;

public class BagFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                            final ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final View view = inflater.inflate(R.layout.fragment_in_bag, container, false);

        // set title
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.in_bag_text);

        // no items
        TextView noItems = view.findViewById(R.id.noItemsInBag);
        noItems.setAlpha(1.0f);

        // init recyclerview
        final RecyclerView ItemsInBagRecycleView = view.findViewById(R.id.RecycleViewYourBag);
        ItemsInBagRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        List<Item> inMyBag = new ArrayList<>();

        // this is data for recycler view

        final List<String[]> myBagItems = FeedReaderDbHelperItems.getItemsInBag(getContext());
        for (String[] item : myBagItems) {
            inMyBag.add(new Item(item[0], item[1], FeedReaderDbHelperItems.isInBag(getContext(), item[0]), FeedReaderDbHelperItems.getType(getContext(), item[0]), getContext()));
        }

        // loop through all relevant subjects

        // create an adapter
        MyBagAdapter mAdapterItemsToAdd = new MyBagAdapter(inMyBag, true, getActivity());
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
            view.findViewById(R.id.view).setVisibility(View.INVISIBLE);
        }

        return view;
    }
}
