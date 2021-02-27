package com.olivermorgan.ontime.main.ui.Remove;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import com.olivermorgan.ontime.main.Activities.MainActivity;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;

import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.MyListAdapter;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class RemoveFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // create view
        final View mainView = inflater.inflate(R.layout.fragment_remove, parent, false);

        // set title
        ((MainActivity)getActivity()).getSupportActionBar().setTitle(R.string.to_remove_text);

        // Spinner logic
        int spinnerIndex = SharedPrefs.getInt(getContext(), SharedPrefs.SPINNER);
        // show weekend
        boolean weekendOnBoolean = SharedPrefs.getBoolean(getContext(), SharedPrefs.WEEKEND_ON);
        Calendar calendar = Calendar.getInstance();
        final boolean doNotShow = !((weekendOnBoolean&& calendar.getTime().toString().startsWith("Sa"))
                ||(weekendOnBoolean&& calendar.getTime().toString().startsWith("Su")));

        boolean tomorrowOff = false;
        Spinner spinner = (Spinner) mainView.findViewById(R.id.daySpinner);
        ArrayAdapter<CharSequence> adapter;
        if(weekendOnBoolean && calendar.getTime().toString().startsWith("Sa")){
            // Create an ArrayAdapter using the string array and a default spinner layout
            adapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.daySpinnerWithoutWeekendAndTomorrow, android.R.layout.simple_spinner_item);
            tomorrowOff = true;
        }else if(weekendOnBoolean){
            adapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.daySpinnerWithoutWeekend, android.R.layout.simple_spinner_item);
        }else {
            adapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.daySpinner, android.R.layout.simple_spinner_item);
        }

        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerIndex);

        final boolean finalTomorrowOff = tomorrowOff;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
            {
                SharedPrefs.setInt(getContext(), SharedPrefs.SPINNER, pos);
                loadRecyclerViewer(getContext(), pos, mainView, getActivity(), doNotShow, finalTomorrowOff);
            }

            public void onNothingSelected(AdapterView<?> parent)
            {

            }
        });


        return mainView;
    }

    private static void loadRecyclerViewer(final Context context, int spinnerIndex, View view, final Activity activity, boolean doNotShow, boolean tomorrowOff) {
        // no items
        TextView noItems = view.findViewById(R.id.noItemsTextRemove);
        noItems.setAlpha(1.0f);

        List<List<String>> subjectsAccordingToSpinner;
        if(spinnerIndex == 0) {
            subjectsAccordingToSpinner = FeedReaderDbHelperSubjects.getContent(context, false);
        }else if(spinnerIndex == 1 && !tomorrowOff){
            subjectsAccordingToSpinner = FeedReaderDbHelperSubjects.getContent(context, -1);
        }else {
            if(tomorrowOff) subjectsAccordingToSpinner = FeedReaderDbHelperSubjects.getContent(context, spinnerIndex+1);
            else subjectsAccordingToSpinner = FeedReaderDbHelperSubjects.getContent(context, spinnerIndex);
        }

        // get subject names that aren't for today
        final List<String> subjectNames = new ArrayList<>();
        if(doNotShow || spinnerIndex!=0) {
            for (List<String> list : FeedReaderDbHelperSubjects.getContent(context, true)) {
                boolean found = false;
                for (List<String> list2 : subjectsAccordingToSpinner) {
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

        final RecyclerView ItemsToRemoveRecycleView = view.findViewById(R.id.ItemsToRemove);
        ItemsToRemoveRecycleView.setLayoutManager(new LinearLayoutManager(activity));

        // this is data for recycler view
        List<Item> inMyBag = new ArrayList<>();
        List<Item> itemsDataItemsToRemove = new ArrayList<>();
        if(doNotShow || spinnerIndex != 0) {
            // adding all of my items in bag to list
            final List<String[]> myBagItems = FeedReaderDbHelperItems.getItemsInBag(context);
            for (String[] item : myBagItems) {
                inMyBag.add(new Item(item[0], item[1],  FeedReaderDbHelperItems.isInBag(context, item[0])));
            }


            // loop through all relevant subjects

            for (String subject : subjectNames) {

                // get the items that i need for today
                final List<String> itemsNotForToday = FeedReaderDbHelperItems.getContent(context, subject);
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
                        itemsDataItemsToRemove.add(new Item(foundItem, subject, FeedReaderDbHelperItems.isInBag(context,foundItem)));
                    }


                }

            }
        }

        // 3. create an adapter
        MyListAdapter mAdapterItemsToRemove = new MyListAdapter(itemsDataItemsToRemove,(byte) 0, view, true, false, true, activity);
        // 4. set adapter
        ItemsToRemoveRecycleView.setAdapter(mAdapterItemsToRemove);
        // 5. set item to remove animator to DefaultAnimator
        ItemsToRemoveRecycleView.setItemAnimator(new DefaultItemAnimator());

        if(itemsDataItemsToRemove.size()>0){
            noItems.setAlpha(0.0f);
        }else{
            noItems.setText(R.string.noItemsInRemove);
        }
    }
}