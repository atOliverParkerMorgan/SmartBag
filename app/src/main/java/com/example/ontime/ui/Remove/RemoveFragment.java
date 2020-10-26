package com.example.ontime.ui.Remove;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.Spinner;
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
        final View mainView = inflater.inflate(R.layout.fragment_remove, parent, false);

        // Spinner logic
        final SharedPreferences preferences = requireActivity().getSharedPreferences("Spinner", android.content.Context.MODE_PRIVATE);
        int spinnerIndex = preferences.getInt("Mode", 0);

        // show weekend
        SharedPreferences preferencesWeekendOn = requireActivity().getSharedPreferences("WeekendOn", android.content.Context.MODE_PRIVATE);
        boolean weekendOnBoolean = preferencesWeekendOn.getBoolean("Mode", true);
        Calendar calendar = Calendar.getInstance();
        final boolean doNotShow = !((weekendOnBoolean&&calendar.getTime().toString().substring(0, 2).equals("Sa"))
                ||(weekendOnBoolean&&calendar.getTime().toString().substring(0, 2).equals("Su")));

        boolean tomorrowOff = false;
        Spinner spinner = (Spinner) mainView.findViewById(R.id.daySpinner);
        ArrayAdapter<CharSequence> adapter;
        if(weekendOnBoolean && calendar.getTime().toString().substring(0,2).equals("Sa")){
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
                SharedPreferences.Editor edit = preferences.edit();

                //save the value same as putExtras using keyNamePair
                edit.putInt("Mode", pos);
                //when done save changes.
                edit.apply();
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


        // image button logic add item
        ImageButton imageButtonAddSubject = view.findViewById(R.id.addSubject);
        imageButtonAddSubject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // setting add Subject first to falls to avoid error
                AddSubject.firstViewOfActivity = true;
                Intent intent = new Intent(activity, AddSubject.class);
                context.startActivity(intent);
            }
        });

        // image button logic settings
        ImageButton imageButtonSettings = view.findViewById(R.id.settingsButton);
        imageButtonSettings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(activity, Settings.class);
                context.startActivity(intent);
            }
        });




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