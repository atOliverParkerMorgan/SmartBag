package com.example.ontime.ui.notifications;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.ontime.R;
import com.example.ontime.ui.Item;
import com.example.ontime.ui.MyListAdapter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RemoveFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_remove, parent, false);

         final RecyclerView recyclerViewItemsToRemove =  view.findViewById(R.id.ItemsToRemove);
         recyclerViewItemsToRemove.setLayoutManager(new LinearLayoutManager(getActivity()));
         // this is data fro recycler view
         Item[] defaultItemsDataItemsToRemove = { new Item("Indigo"),
                 new Item("Red"),
                 new Item("Blue"),
                 new Item("Green"),
                 new Item("Amber"),
                 new Item("Deep Orange")};
         List<Item> itemsDataItemsToRemove = new ArrayList<>(Arrays.asList(defaultItemsDataItemsToRemove));
         // 3. create an adapter
         MyListAdapter mAdapterItemsToRemove = new MyListAdapter(itemsDataItemsToRemove);
         // 4. set adapter
         recyclerViewItemsToRemove.setAdapter(mAdapterItemsToRemove);
         // 5. set item animator to DefaultAnimator
         recyclerViewItemsToRemove.setItemAnimator(new DefaultItemAnimator());

        return view;
    }
}