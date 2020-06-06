package com.example.ontime.ui.home;

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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class HomeFragment extends Fragment{

    //ThingsAdapter adapter;
    //FragmentActivity listener;

    // This event fires 1st, before creation of fragment or any views
    // The onAttach method is called when the Fragment instance is associated with an Activity.
    // This does not mean the Activity is fully initialized.
   // @Override
   // public void onAttach(Context context) {
   //     super.onAttach(context);
   //     if (context instanceof Activity){
   //         this.listener = (FragmentActivity) context;
   //     }
   // }

    // This event fires 2nd, before views are created for the fragment
    // The onCreate method is called when the Fragment instance is being created, or re-created.
    // Use onCreate for any standard setup that does not require the activity to be fully created

    // The onCreateView method is called when Fragment should create its View object hierarchy,
    // either dynamically or via XML layout inflation.
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_add, parent, false);

        final RecyclerView ItemsToAddRecycleView = (RecyclerView) view.findViewById(R.id.ItemsToAdd);
        ItemsToAddRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        // this is data fro recycler view
        Item[] defaultItemsDataItemsToAdd = { new Item("Indigo"),
                new Item("Red"),
                new Item("Blue"),
                new Item("Green"),
                new Item("Amber"),
                new Item("Deep Orange")};
        List<Item> itemsDataItemsToAdd = new ArrayList<>(Arrays.asList(defaultItemsDataItemsToAdd));

        // 3. create an adapter
        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToAdd);
        // 4. set adapter
        ItemsToAddRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set item animator to DefaultAnimator
        ItemsToAddRecycleView.setItemAnimator(new DefaultItemAnimator());

        final RecyclerView recyclerViewItemsToRemove = (RecyclerView) view.findViewById(R.id.ItemsToRemove);
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

    // This event is triggered soon after onCreateView().
    // onViewCreated() is only called if the view returned from onCreateView() is non-null.
    // Any view setup should occur here.  E.g., view lookups and attaching view listeners.
   // @Override
   // public void onViewCreated(View view, Bundle savedInstanceState) {
   //     super.onViewCreated(view, savedInstanceState);
   //     //ListView lv = (ListView) view.findViewById(R.id.lvSome);
   //     //lv.setAdapter(adapter);
   // }

    // This method is called when the fragment is no longer connected to the Activity
    // Any references saved in onAttach should be nulled out here to prevent memory leaks.
   // @Override
   // public void onDetach() {
   //     super.onDetach();
   //     this.listener = null;
   // }

    // This method is called after the parent Activity's onCreate() method has completed.
    // Accessing the view hierarchy of the parent activity must be done in the onActivityCreated.
    // At this point, it is safe to search for activity View objects by their ID, for example.
   // @Override
   // public void onActivityCreated(Bundle savedInstanceState) {
   //     super.onActivityCreated(savedInstanceState);
   // }
}