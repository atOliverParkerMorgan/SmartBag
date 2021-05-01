package com.olivermorgan.ontime.main.ui.Add;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.olivermorgan.ontime.main.Activities.MainActivity;
import com.olivermorgan.ontime.main.Adapter.Item;
import com.olivermorgan.ontime.main.Adapter.MyListAdapter;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperItems;
import com.olivermorgan.ontime.main.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import uk.co.deanwild.materialshowcaseview.MaterialShowcaseSequence;
import uk.co.deanwild.materialshowcaseview.ShowcaseConfig;

public class AddFragment extends Fragment {

    private static void loadRecyclerViewer(final Context context, int spinnerIndex, View view, final Activity activity, boolean doNotShow, boolean tomorrowOff) {
        // no items
        TextView noItems = view.findViewById(R.id.noItemsTextAdd);
        noItems.setAlpha(1.0f);

        List<List<String>> subjectsAccordingToSpinner;
        if (spinnerIndex == 0) {
            subjectsAccordingToSpinner = FeedReaderDbHelperSubjects.getContent(context, false);
        } else if (spinnerIndex == 1 && !tomorrowOff) {
            subjectsAccordingToSpinner = FeedReaderDbHelperSubjects.getContent(context, -1);
        } else {
            if (tomorrowOff)
                subjectsAccordingToSpinner = FeedReaderDbHelperSubjects.getContent(context, spinnerIndex + 1);
            else
                subjectsAccordingToSpinner = FeedReaderDbHelperSubjects.getContent(context, spinnerIndex);
        }

        // init database
        final List<String> subjectNames = new ArrayList<>();
        for (List<String> list : subjectsAccordingToSpinner) {
            subjectNames.add(list.get(0));
        }


        final RecyclerView ItemsToAddRecycleView = view.findViewById(R.id.ItemsToAdd);
        ItemsToAddRecycleView.setLayoutManager(new LinearLayoutManager(activity));


        // pomocné pole
        List<Item> inMyBag = new ArrayList<>(); // pole co mám v batohu
        List<Item> itemsDataItemsToAdd = new ArrayList<>(); // pole co si přidat do

        // získat pole co mám v batohu z databáze
        final List<String[]> myBagItems = FeedReaderDbHelperItems.getItemsInBag(context);

        // projít všechny položky v batohu
        for (String[] item : myBagItems) {
            // přidat novou položku do pole
            inMyBag.add(new Item(item[0], item[1], FeedReaderDbHelperItems.isInBag(context, item[0]), FeedReaderDbHelperItems.getType(context, item[0]), context));
        }
        // logika pro zpracování daného dne
        if (doNotShow || spinnerIndex != 0) {

            // kalkulace => co potřebuji do batohu přidat - co mám v batohu
            for (String subject : subjectNames) {
                // získat položky předmětů z databáze
                final List<String> itemNames = FeedReaderDbHelperItems.getContent(context, subject);
                for (String item : itemNames) {

                    // zkontrolovat jestli položka není už v batohu
                    boolean found = false;
                    for (Item itemInBag : inMyBag) {
                        if (itemInBag.getItemName().equals(item) && itemInBag.getSubjectName().equals(subject)) {
                            found = true;
                            break;
                        }
                    }
                    // přidat položky do databáze
                    if (!found) {
                        itemsDataItemsToAdd.add(new Item(item, subject, FeedReaderDbHelperItems.isInBag(context, item), FeedReaderDbHelperItems.getType(context, item), context));
                    }
                }

            }
        }


        // 3. create an adapter
        MyListAdapter mAdapterItemsToAdd = new MyListAdapter(itemsDataItemsToAdd, (byte) 1, view, true, false, true, activity);
        // 4. set adapter
        ItemsToAddRecycleView.setAdapter(mAdapterItemsToAdd);
        // 5. set itemAdd animator to DefaultAnimator
        ItemsToAddRecycleView.setItemAnimator(new DefaultItemAnimator());

        if (itemsDataItemsToAdd.size() > 0) {
            noItems.setAlpha(0.0f);

        } else {
            noItems.setText(R.string.noItemsInAdd);
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // create view
        final View mainView = inflater.inflate(R.layout.fragment_add, parent, false);
        // set title
        ((MainActivity) requireActivity()).getSupportActionBar().setTitle(R.string.to_add_text);

        // tutorial
        final String SHOWCASE_ID = "firstTutorial1";
        // sequence example
        ShowcaseConfig config = new ShowcaseConfig();
        config.setDelay(200); // half second between each showcase view

        MaterialShowcaseSequence sequence = new MaterialShowcaseSequence(getActivity(), SHOWCASE_ID);

        sequence.setConfig(config);

        sequence.addSequenceItem(mainView.findViewById(R.id.daySpinner),
                requireActivity().getResources().getString(R.string.clickHereToSelectDay), requireActivity().getResources().getString(R.string.Next));

        sequence.start();

        // Spinner logic
        int spinnerIndex = SharedPrefs.getInt(getContext(), SharedPrefs.SPINNER);

        // show weekend

        boolean weekendOnBoolean = SharedPrefs.getBoolean(getContext(), SharedPrefs.WEEKEND_ON);
        Calendar calendar = Calendar.getInstance();
        final boolean doNotShow = !((weekendOnBoolean && calendar.getTime().toString().startsWith("Sa"))
                || (weekendOnBoolean && calendar.getTime().toString().startsWith("Su")));

        Spinner spinner = mainView.findViewById(R.id.daySpinner);
        ArrayAdapter<CharSequence> adapter;

        boolean tomorrowOff = false;
        if (weekendOnBoolean && calendar.getTime().toString().startsWith("Sa")) {
            // Create an ArrayAdapter using the string array and a default spinner layout
            adapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.daySpinnerWithoutWeekendAndTomorrow, android.R.layout.simple_spinner_item);
            tomorrowOff = true;
        } else if (weekendOnBoolean) {
            adapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.daySpinnerWithoutWeekend, android.R.layout.simple_spinner_item);
        } else {
            adapter = ArrayAdapter.createFromResource(requireContext(),
                    R.array.daySpinner, android.R.layout.simple_spinner_item);
        }


        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(spinnerIndex);

        final boolean finalTomorrowOff = tomorrowOff;
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
                SharedPrefs.setInt(getContext(), SharedPrefs.SPINNER, pos);
                // reload recyclerViewer
                loadRecyclerViewer(getContext(), pos, mainView, getActivity(), doNotShow, finalTomorrowOff);

            }

            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        return mainView;
    }


}