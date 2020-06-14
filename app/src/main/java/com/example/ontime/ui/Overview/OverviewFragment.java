package com.example.ontime.ui.Overview;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ontime.Adapter.Item;
import com.example.ontime.DataBaseHelpers.FeedReaderDbHelperSubjects;
import com.example.ontime.R;

import java.util.List;

public class OverviewFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // create view
        final View view = inflater.inflate(R.layout.fragment_overview, parent, false);
        TableLayout table = (TableLayout) view.findViewById(R.id.mainTable);
        for (int i = 1; i < 8; i++) {
            TableRow row = new TableRow(getContext());

            row.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            row.setPadding(0,0,0,0);

            boolean atLeastOne = false;
            for (List<String> list : FeedReaderDbHelperSubjects.getContent(getContext(), true)) {
                Item item = new Item("null", list.get(0));

                // days of the week logic

                if (list.get(i).equals("true")) {
                    atLeastOne = true;
                    Button button = new Button(getContext());
                    button.setMinimumWidth(0);
                    button.setMinimumHeight(0);
                    button.setBackgroundResource(R.drawable.rounded_textview_default_borders);
                    button.setGravity(Gravity.CENTER);
                    button.setText(item.getNameInitialsOfSubject());
                    button.setTextColor(Color.WHITE);
                    button.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                    button.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                    button.setTextSize(30);

                    row.addView(button);


                }

            }
            if(!atLeastOne){
                TextView padding = new TextView(getContext());
                padding.setMinimumWidth(0);
                padding.setMinimumHeight(0);
                padding.setBackgroundResource(R.drawable.rounded_textview_padding);
                padding.setGravity(Gravity.CENTER);
                padding.setTextColor(Color.WHITE);
                padding.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
                padding.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
                padding.setText("");
                row.addView(padding);

            }
            table.addView(row);

        }
        table.requestLayout();     // Not sure if this is needed.

        return view;
    }

}
