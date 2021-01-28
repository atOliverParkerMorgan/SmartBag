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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.olivermorgan.ontime.main.BakalariAPI.BakalariAPI;
import com.olivermorgan.ontime.main.BakalariAPI.LoginData;
import com.olivermorgan.ontime.main.BakalariAPI.TokenData;
import com.olivermorgan.ontime.main.BakalariAPI.TokenGenerator;
import com.olivermorgan.ontime.main.R;
import com.olivermorgan.ontime.main.SharedPrefs;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    final static String TAG = "Baka";

    @Override
    protected void onResume() {
        super.onResume();
        final SharedPreferences userPreferences = Objects.requireNonNull(this.getSharedPreferences("login", android.content.Context.MODE_PRIVATE));

        final EditText serverAddress = findViewById(R.id.serverAddress);
        serverAddress.setText(userPreferences.getString("serverAddress", ""));
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

        final SharedPreferences userPreferences = Objects.requireNonNull(this.getSharedPreferences("login", android.content.Context.MODE_PRIVATE));

        final ProgressBar progressBar = findViewById(R.id.progressBarBakalari);

        final EditText username = findViewById(R.id.userNameInput);
        final EditText password = findViewById(R.id.passwordInputText);
        final EditText serverAddress = findViewById(R.id.serverAddress);
        username.setText(userPreferences.getString("username", ""));
        password.setText(userPreferences.getString("password", ""));
        serverAddress.setText(userPreferences.getString("serverAddress", ""));

        final Button login = findViewById(R.id.buttonLogin);
        Button back = findViewById(R.id.buttonBack);
        Button searchSchool = findViewById(R.id.buttonSearchSchools);

        searchSchool.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, SchoolsListActivity.class)));

        back.setOnClickListener(v -> startActivity(new Intent(LoginActivity.this, Settings.class)));

        login.setOnClickListener(v -> {
            SharedPreferences.Editor edit = userPreferences.edit();
            edit.putString("username", username.getText().toString());
            edit.putString("password", password.getText().toString());
            edit.putString("serverAddress", serverAddress.getText().toString());
            edit.apply();

            try {
                URL obj = new URL(serverAddress.getText().toString() + "?gethx=" + username.getText().toString());
                HttpURLConnection con = (HttpURLConnection) obj.openConnection();
                con.setRequestMethod("GET");
                //add request header
                con.setRequestProperty("User-Agent", "Mozilla/5.0");


            } catch (IOException e) {
                e.printStackTrace();
            }
            // save user input
            final String passwordText = password.getText().toString();
            final String usernameText = username.getText().toString();
            final String urlText = serverAddress.getText().toString();
            final String urlTextValidated = urlText.endsWith("/") ? urlText : urlText + "/";

            // reset all errors
            //usernameInputLayout.setErrorEnabled(false);
            //passwordInputLayout.setErrorEnabled(false);
            //schoolURLInputLayout.setErrorEnabled(false);

            boolean valid = true;

            // no field can be empty and address must start with http:// or https://
            // but cannot equal to http:// or https:// and cannot start with http://? or https://?
            if (usernameText.equals("")) {
                // usernameInputLayout.setError(getString(R.string.error_blank_field));
                valid = false;
            }
            if (passwordText.equals("")) {
                //  passwordInputLayout.setError(getString(R.string.error_blank_field));
                valid = false;
            }
            if (!(urlTextValidated.startsWith("http://") || urlTextValidated.startsWith("https://")) || urlTextValidated.equals("http://") || urlTextValidated.equals("https://") || urlTextValidated.startsWith("http://?/") || urlTextValidated.startsWith("https://?/")) {
                if (urlTextValidated.equals("")) {
                    //      schoolURLInputLayout.setError(getString(R.string.error_blank_field));
                    // } else schoolURLInputLayout.setError(getString(R.string.wrong_adress));
                    //  valid = false;
                }

                // if above conditions are met
                if (valid) {

                    // show progress bar instead of the "Log in" text
                    progressBar.setVisibility(View.VISIBLE);
                    login.setTextColor(getResources().getColor(android.R.color.transparent));

                    login.setClickable(false);

                    // init retrofit
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl(urlTextValidated)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    final BakalariAPI api = retrofit.create(BakalariAPI.class);

                    // get salt, id, type
                    Call<TokenData> tokenDataCall = api.getTokenData(usernameText);
                    tokenDataCall.enqueue(new Callback<TokenData>() {
                        @Override
                        public void onResponse(Call<TokenData> call, Response<TokenData> response) {
                            if (response.body() != null) { // the response body is null if  the address is real but not a Bakaláři one
                                if (response.body().getID() != null) { // getID returns null if the username is wrong but the address is a Bakaláři one

                                    // "Basic ANDR:<token>" in base64
                                    String auth = TokenGenerator.calculateBasicAuth(response.body(), passwordText, usernameText);

                                    // Get users real name + verify that the password is right
                                    final Call<LoginData> loginDataCall = api.getLoginData(auth);
                                    loginDataCall.enqueue(new Callback<LoginData>() {
                                        @Override
                                        public void onResponse(Call<LoginData> call, Response<LoginData> response) {
                                            if (response.body() != null) { // response body is null if the password isn't right
                                                // save login credentials to shared prefs
                                             //   SharedPreferences.Editor editor = preferences.edit();
                                                Log.d(TAG, "onResponse: " + urlText);
                                                Log.d(TAG, "onResponse: " + urlTextValidated);
                                             //   editor.putString("school_url", urlTextValidated);
                                             //   editor.putString("username", usernameText);
                                             //   editor.putString("password", passwordText); // security level over 9000 (who cares about ur Bakaláři password srsly)
                                             //   editor.apply();

                                                // make a toast with the real name obtained from the Bakaláři server
                                                Toast.makeText(LoginActivity.this, getString(R.string.user_logged_in, response.body().getRealName()), Toast.LENGTH_LONG).show();

                                                // login successful - start the main activity, finish this one
                                                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                                                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // WRONG PASSWORD
                                                progressBar.setVisibility(View.GONE);
                                                // loginButton.setTextColor(getResources().getColor(android.R.color.white));
                                                //  loginButton.setClickable(true);
                                                //  passwordInputLayout.setError(getString(R.string.wrong_password));
                                                Log.d(TAG, "onResponse: wrong password");
                                                try {
                                                    Log.d(TAG, "onResponse: " + response.errorBody().string());
                                                } catch (IOException e) {
                                                    e.printStackTrace();
                                                }
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<LoginData> call, Throwable t) {
                                            // INTERNET CONNECTION WAS LOST AFTER FIRST BUT BEFORE THE SECOND REQUEST
                                            // OR IDK SOME WTF ERROR
                                            progressBar.setVisibility(View.GONE);
                                        //    loginButton.setTextColor(getResources().getColor(android.R.color.white));
                                        //    loginButton.setClickable(true);
                                        //    schoolURLInputLayout.setError(getString(R.string.unknown_error_no_internet));
                                            Log.d(TAG, "onFailure: unknown error or no internet connection");
                                        }
                                    });
                                } else {
                                    // WRONG USERNAME
                                    progressBar.setVisibility(View.GONE);
                                //    loginButton.setTextColor(getResources().getColor(android.R.color.white));
                                 //   loginButton.setClickable(true);
                                 //   usernameInputLayout.setError(getString(R.string.wrong_username));
                                    Log.d(TAG, "onResponse: wrong username");
                                }
                            } else {
                                // REAL ADDRESS BUT NOT A BAKALÁŘI ONE
                                progressBar.setVisibility(View.GONE);
                               // loginButton.setTextColor(getResources().getColor(android.R.color.white));
                               // loginButton.setClickable(true);
                               // schoolURLInputLayout.setError(getString(R.string.wrong_adress));
                                Log.d(TAG, "onFailure: not a Bakaláři address");
                            }
                        }

                        @Override
                        public void onFailure(Call<TokenData> call, Throwable t) {
                            // NOT A REAL ADDRESS OR NO INTERNET CONNECTION
                            progressBar.setVisibility(View.GONE);
                           // loginButton.setTextColor(getResources().getColor(android.R.color.white));
                          //  loginButton.setClickable(true);
                           // schoolURLInputLayout.setError(getString(R.string.wrong_address_no_internet));
                            Log.d(TAG, "onFailure: not a real address or no internet connection");
                            Log.d(TAG, "onFailure: " + t.toString());
                        }
                    });
                }


            }
        });

    }
}
