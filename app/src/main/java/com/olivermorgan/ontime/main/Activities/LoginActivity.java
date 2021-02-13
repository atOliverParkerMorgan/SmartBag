package com.olivermorgan.ontime.main.Activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olivermorgan.ontime.main.BakalariAPI.BakalariAPI;
import com.olivermorgan.ontime.main.BakalariAPI.Login;
import com.olivermorgan.ontime.main.BakalariAPI.LoginResponse;
import com.olivermorgan.ontime.main.BakalariAPI.UserResponse;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import org.joda.time.LocalDateTime;
import org.joda.time.format.ISODateTimeFormat;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    final static String TAG = "Baka";


    @Override
    protected void onResume() {
        super.onResume();
        ((EditText)findViewById(R.id.userNameInput)).setText(SharedPrefs.getString(this,SharedPrefs.USERNAME));
        ((EditText)findViewById(R.id.passwordInputText)).setText(SharedPrefs.getString(this,SharedPrefs.PASSWORD));
        ((EditText)findViewById(R.id.serverAddress)).setText(SharedPrefs.getString(this,SharedPrefs.URL));
    }


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        boolean darkModeOn = SharedPrefs.getDarkMode(this);
        if (darkModeOn) {
            setTheme(R.style.DARK);
        } else {
            setTheme(R.style.LIGHT);
        }
        setContentView(R.layout.actvity_bakalari_login);

        final ProgressBar progressBar = findViewById(R.id.progressBarBakalari);

        final EditText username = findViewById(R.id.userNameInput);
        final EditText password = findViewById(R.id.passwordInputText);
        final EditText serverAddress = findViewById(R.id.serverAddress);

        username.setText(SharedPrefs.getString(this,SharedPrefs.USERNAME ));
        password.setText(SharedPrefs.getString(this,SharedPrefs.PASSWORD ));
        serverAddress.setText(SharedPrefs.getString(this,SharedPrefs.URL));


        final Button login = findViewById(R.id.buttonLogin);
        Button back = findViewById(R.id.buttonBack);
        Button searchSchool = findViewById(R.id.buttonSearchSchools);

        searchSchool.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SchoolsListActivity.class)));

        back.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, Settings.class)));

        login.setOnClickListener(v -> {
            progressBar.setVisibility(View.VISIBLE);
            login.setVisibility(View.INVISIBLE);
            back.setVisibility(View.INVISIBLE);
            login.setClickable(false);
            back.setClickable(false);

            // save user input
           final String passwordText = password.getText().toString();
           final String usernameText = username.getText().toString();
           final String urlText = serverAddress.getText().toString();
           final String urlTextValidated = urlText.endsWith("/") ? urlText : urlText + "/";
            SharedPrefs.setString(this,SharedPrefs.USERNAME, usernameText );
            SharedPrefs.setString(this, SharedPrefs.PASSWORD, passwordText);
            SharedPrefs.setString(this, SharedPrefs.URL, urlText);

           Login loginLogic = new Login(this);

           loginLogic.getLogin().login(urlTextValidated,usernameText,passwordText, (code) -> {
                if (code == Login.SUCCESS) {
                    Intent intent = new Intent(this, SuccessfulLogin.class);
                    startActivity(intent);
                    finish();
                    overridePendingTransition(R.anim.fade_out, R.anim.fade_in);
                    return;
                }
                login.setEnabled(true);
                progressBar.setVisibility(View.GONE);
                if (code == Login.WRONG_LOGIN) {
                    resetButton(progressBar,login, back);
                    username.setError(getText(R.string.invalid_login));
                    password.setError(getText(R.string.invalid_login));
                }
                if (code == Login.SERVER_UNREACHABLE) {
                    resetButton(progressBar,login, back);
                    serverAddress.setError(getText(R.string.sever_unreachable));
                }
                if (code == Login.UNEXPECTER_RESPONSE) {
                    resetButton(progressBar,login, back);
                    serverAddress.setError(getText(R.string.unexpected_response));
                }
                if (code == Login.ROZVRH_DISABLED) {
                    resetButton(progressBar,login, back);
                    serverAddress.setError(getText(R.string.disabled_servises));

                }

            });



        });

    }

    public void resetButton(ProgressBar progressBar, Button login, Button back){
        progressBar.setVisibility(View.INVISIBLE);
        login.setVisibility(View.VISIBLE);
        back.setVisibility(View.VISIBLE);
        login.setClickable(true);
        back.setClickable(true);
    }

}