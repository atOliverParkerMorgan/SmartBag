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
         Item[] defaultItemsDataItemsToRemove = { new Item("Indigo", "TEST"),
                 new Item("Red", "TEST"),
                 new Item("Blue", "TEST"),
                 new Item("Green", "TEST"),
                 new Item("Amber", "TEST"),
                 new Item("Deep Orange", "TEST")};
         List<Item> itemsDataItemsToRemove = new ArrayList<>(Arrays.asList(defaultItemsDataItemsToRemove));
         // 3. create an adapter
         MyListAdapter mAdapterItemsToRemove = new MyListAdapter(itemsDataItemsToRemove, (byte) 0);
         // 4. set adapter
         recyclerViewItemsToRemove.setAdapter(mAdapterItemsToRemove);
         // 5. set itemAdd animator to DefaultAnimator
         recyclerViewItemsToRemove.setItemAnimator(new DefaultItemAnimator());

        return view;
    }
}