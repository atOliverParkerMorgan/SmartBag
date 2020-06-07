package com.example.ontime.ui.Add;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.R;
import com.example.ontime.ui.Item;
import com.example.ontime.ui.MyListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddFragment extends Fragment{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add, parent, false);

        final RecyclerView ItemsToAddRecycleView = view.findViewById(R.id.ItemsToAdd);
        ItemsToAddRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // this is data fro recycler view
        Item[] defaultItemsDataItemsToAdd = { new Item("Indigo", "TEST"),
                new Item("Red", "TEST"),
                new Item("Blue", "TEST"),
                new Item("Green", "TEST"),
                new Item("Amber", "TEST"),
                new Item("Deep Orange", "TEST")};
        List<Item> itemsDataItemsToAdd = new ArrayList<>(Arrays.asList(defaultItemsDataItemsToAdd));

        // 3. create an adapter
        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToAdd,(byte) 1);
        // 4. set adapter
        ItemsToAddRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set itemAdd animator to DefaultAnimator
        ItemsToAddRecycleView.setItemAnimator(new DefaultItemAnimator());


        return view;
    }

}