package com.example.ontime.ui.Bag;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.ontime.AddItem;
import com.example.ontime.R;
import com.example.ontime.ui.AddSubject.AddSubjectFragment;

import java.util.Objects;

public class BagFragment extends Fragment {
    public View onCreateView(@NonNull LayoutInflater inflater,
                            final ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_in_bag, container, false);
        final BagFragment.ViewHolder viewHolder = new BagFragment.ViewHolder(view);

        return view;
    }
    public class ViewHolder{
        EditText subjectName;
        Button addItems;

        ViewHolder(android.view.View view){

        }
    }

}
