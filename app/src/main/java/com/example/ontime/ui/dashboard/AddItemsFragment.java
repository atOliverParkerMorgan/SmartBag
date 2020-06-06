package com.example.ontime.ui.dashboard;

import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import com.example.ontime.R;


public class AddItems extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_additem, container, false);
        final ViewHolder viewHolder = new ViewHolder(view);



        viewHolder.addItems.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                boolean m = viewHolder.monday.isChecked();
                Editable s = viewHolder.subjectName.getText();
                if(m) {
                    Toast.makeText(getActivity(), s.toString(), Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getActivity(), "FAIL", Toast.LENGTH_LONG).show();
                }
            }
        });

        return view;
    }
    public class ViewHolder{
        final Switch monday;
        final Switch tuesday;
        final Switch wednesday;
        final Switch thursday;
        final Switch friday;
        final  Switch saturday;
        final  Switch sunday;
        EditText subjectName;
        Button addItems;

        ViewHolder(View view){
            monday = view.findViewById(R.id.Monday);
            tuesday = view.findViewById(R.id.Tuesday);
            wednesday = view.findViewById(R.id.Wednesday);
            thursday = view.findViewById(R.id.Thursday);
            friday = view.findViewById(R.id.Friday);
            saturday = view.findViewById(R.id.Saturday);
            sunday = view.findViewById(R.id.Sunday);
            addItems = view.findViewById(R.id.add_items);
            subjectName = view.findViewById(R.id.editSubject);
        }
    }
}