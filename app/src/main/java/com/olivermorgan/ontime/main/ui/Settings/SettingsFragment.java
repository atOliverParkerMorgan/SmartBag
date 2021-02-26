package com.olivermorgan.ontime.main.ui.Settings;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;


import com.olivermorgan.ontime.main.Activities.LoginActivity;
import com.olivermorgan.ontime.main.Activities.MainActivity;
import com.olivermorgan.ontime.main.BakalariAPI.Login;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import java.util.Objects;


public class SettingsFragment extends Fragment{


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup parent, Bundle savedInstanceState) {
        // create view
        super.onCreate(savedInstanceState);
        final View mainView = inflater.inflate(R.layout.fragment_settings, parent, false);
        // doesn't use default toolbar
        Objects.requireNonNull(((MainActivity) requireActivity()).getSupportActionBar()).hide();
        Toolbar toolbar = mainView.findViewById(R.id.toolbarSettings);
        toolbar.setNavigationOnClickListener(v -> {
            Intent intent = new Intent(getActivity(), MainActivity.class);
            // if the database is updated while activities are changed => app crashes
            // this prevents it
            // checkout MainActivity
            intent.putExtra("dontUpdateDatabase", true);
            startActivity(intent);
            requireActivity().finish();
            requireActivity().overridePendingTransition(R.anim.fade_out, R.anim.fade_in);

        });

        boolean darkModeOn = SharedPrefs.getDarkMode(getContext());

        boolean weekendOnBoolean = SharedPrefs.getBoolean(getContext(),SharedPrefs.WEEKEND_ON);


        // is logged in
        new Thread(()-> {

            ImageView loginButton = mainView.findViewById(R.id.bakalariLoginButton);
            Login login = new Login(getContext());
            if (login.isLoggedIn()) {
                TextView name = mainView.findViewById(R.id.bakalari);
                name.setText(SharedPrefs.getString(getContext(), SharedPrefs.NAME));
                loginButton.setImageResource(R.drawable.ic_log_out);
                loginButton.setOnClickListener(v -> {
                    login.logout();
                    restart();
                });


            } else {

                loginButton.setOnClickListener(v -> {
                    Intent i = new Intent(getActivity(), LoginActivity.class);
                    i.putExtra("buttonName", getText(R.string.app_intro_next_button));
                    startActivity(i);
                });
            }}).start();

            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch darkMode = mainView.findViewById(R.id.darkModeSwitch);
            darkMode.setChecked(darkModeOn);
            darkMode.setOnCheckedChangeListener((buttonView, isChecked) -> {

                new Thread(()->  SharedPrefs.setBoolean(getContext(), SharedPrefs.DARK_MODE, isChecked)).start();
                if (isChecked) {
                    Toast.makeText(getContext(), "Dark mode is on", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(), "Light mode is on", Toast.LENGTH_SHORT).show();

                }
                restart();


            });

            @SuppressLint("UseSwitchCompatOrMaterialCode") Switch weekendOn = mainView.findViewById(R.id.deleteSundayAndSaturdaySwitch);
            weekendOn.setChecked(!weekendOnBoolean);
            weekendOn.setOnCheckedChangeListener((buttonView, isChecked) -> {
                //declare the shardPreferences variable..
                // if due is set to Saturday or Sunday and do not show Weekend is checked set due to Today.
                new Thread(()-> {
                    if (!isChecked) {

                        if (SharedPrefs.getInt(getContext(), SharedPrefs.SPINNER) == 8 || SharedPrefs.getInt(getContext(), SharedPrefs.SPINNER) == 7) {
                            SharedPrefs.setInt(getContext(), SharedPrefs.SPINNER, 0);
                        }
                    }
                    SharedPrefs.setBoolean(getContext(), SharedPrefs.WEEKEND_ON, !isChecked);
                }).start();


                if (isChecked) {
                    Toast.makeText(getContext(), R.string.willShowWeek, Toast.LENGTH_SHORT).show();

                } else {
                    Toast.makeText(getContext(), R.string.willShowWeekNot, Toast.LENGTH_SHORT).show();

                }
                weekendOn.setChecked(!SharedPrefs.getBoolean(getContext(), SharedPrefs.WEEKEND_ON));
            });


        return mainView;
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        inflater.inflate(R.menu.top_nav_menu_back, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    private void restart(){
        Intent intent = new Intent(getActivity(), MainActivity.class);
        intent.putExtra("fromSettings", true);
        startActivity(intent);
        requireActivity().finish();
        requireActivity().overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
    }


}
