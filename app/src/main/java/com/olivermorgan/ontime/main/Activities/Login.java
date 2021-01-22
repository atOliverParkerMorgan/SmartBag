package com.olivermorgan.ontime.main.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.olivermorgan.ontime.main.R;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

public class Login extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences preferences = Objects.requireNonNull(this.getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE));
        boolean darkModeOn = preferences.getBoolean("Mode", true);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.actvity_bakalari_login);

        final SharedPreferences userPreferences = Objects.requireNonNull(this.getSharedPreferences("login", android.content.Context.MODE_PRIVATE));


        final EditText username = findViewById(R.id.userNameInput);
        final EditText password = findViewById(R.id.passwordInputText);
        final EditText serverAddress = findViewById(R.id.serverAddress);
        username.setText(userPreferences.getString("username",  ""));
        password.setText(userPreferences.getString("password",  ""));
        serverAddress.setText(userPreferences.getString("serverAddress",  ""));

        final Button login = findViewById(R.id.buttonLogin);
        Button back = findViewById(R.id.buttonBack);
        Button searchSchool = findViewById(R.id.buttonSearchSchools);

        searchSchool.setOnClickListener(v -> startActivity(new Intent(Login.this, SchoolsListActivity.class)));

        back.setOnClickListener(v -> startActivity(new Intent(Login.this, Settings.class)));

        login.setOnClickListener(v -> {
            SharedPreferences.Editor edit = userPreferences.edit();
            edit.putString("username", username.getText().toString());
            edit.putString("password", password.getText().toString());
            edit.putString("serverAddress", serverAddress.getText().toString());
            edit.apply();

            try {
                URL obj = new URL(serverAddress.getText().toString()+"?gethx="+username.getText().toString());
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                //add request header
                con.setRequestProperty("User-Agent", "Mozilla/5.0");



            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        final SharedPreferences userPreferences = Objects.requireNonNull(this.getSharedPreferences("login", android.content.Context.MODE_PRIVATE));

        final EditText serverAddress = findViewById(R.id.serverAddress);
        serverAddress.setText(userPreferences.getString("serverAddress",  ""));
    }
}
