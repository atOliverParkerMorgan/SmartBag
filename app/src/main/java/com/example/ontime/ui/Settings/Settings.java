package com.example.ontime.ui.Settings;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.ontime.R;

import java.util.Objects;

public class Settings extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // create view
        final View view = inflater.inflate(R.layout.fragment_settings, parent, false);
        SharedPreferences preferences = Objects.requireNonNull(getContext()).getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE);
        boolean darkModeOn = preferences.getBoolean("Mode", false);
        if (darkModeOn) {
            Objects.requireNonNull(getActivity()).setTheme(R.style.DARK);
        } else {
            Objects.requireNonNull(getActivity()).setTheme(R.style.LIGHT);
        }


        Switch darkMode = view.findViewById(R.id.darkModeSwitch);
        darkMode.setChecked(darkModeOn);
        darkMode.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                //declare the shardPreferences variable..
                SharedPreferences sp = Objects.requireNonNull(getContext()).getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE);

                // to save data you have to call the editor
                SharedPreferences.Editor edit = sp.edit();

                //save the value same as putExtras using keyNamePair
                edit.putBoolean("Mode", isChecked);

                //when done save changes.
                edit.apply();

                if (isChecked) {
                    Toast.makeText(getContext(), "Dark mode is on", Toast.LENGTH_LONG).show();
                    getContext().setTheme(R.style.DARK);



                } else {
                    Toast.makeText(getContext(), "Light mode is on", Toast.LENGTH_LONG).show();
                    getContext().setTheme(R.style.LIGHT);
                }
            }
        });


        return view;
    }
}
