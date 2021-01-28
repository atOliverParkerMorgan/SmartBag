package com.olivermorgan.ontime.main;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.preference.PreferenceManager;

import java.util.Objects;

public class SharedPrefs {
    //key constants (do not change in future)
    public static final String URL = "url";
    public static final String USERNAME = "username";
    public static final String ACCEESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    public static final String ACCESS_EXPIRES = "access_expires";
    public static final String NAME = "name";
    public static final String TYPE = "type";
    public static final String SENTRY_ID = "sentry_id";

    public static final String REMEMBERED_ROWS = "remembered_rows";
    public static final String REMEMBERED_COLUMNS = "remembered_columns";

    public static final String LAST_VERSION_SEEN = "last_version_seen";
    /**
     * All weird rozvrh before this date won't show any dialog.
     */
    public static final String DISABLE_WTF_ROZVRH_UP_TO_DATE = "disable_wtf_rozvrh_up_to_date";

    public static final String WIDGETS_SETTINGS = "widgets-settings";
    public static String getString(Context context, String name, String key, String _default){
        SharedPreferences userPreferences = Objects.requireNonNull(context.getSharedPreferences(name, android.content.Context.MODE_PRIVATE));
        return userPreferences.getString(key, _default);
    }

    public static boolean getBoolean(Context context, String name, String key, Boolean _default){
        SharedPreferences userPreferences = Objects.requireNonNull(context.getSharedPreferences(name, android.content.Context.MODE_PRIVATE));
        return userPreferences.getBoolean(key, _default);
    }

    public static int getInt(Context context, String name, String key, int _default){
        SharedPreferences userPreferences = Objects.requireNonNull(context.getSharedPreferences(name, android.content.Context.MODE_PRIVATE));
        return userPreferences.getInt(key, _default);
    }

    public static void edit(){

    }

    public static void remove(){

    }

    public static void setString(Context context, String value, String key, String name){
        SharedPreferences.Editor edit = (SharedPreferences.Editor) Objects.requireNonNull(context.getSharedPreferences(name, Context.MODE_PRIVATE));
        SharedPreferences.Editor preferenceManager = PreferenceManager.getDefaultSharedPreferences(context).edit();
        preferenceManager.putString(key, value);
        preferenceManager.apply();
    }

    public static void setBoolean(Context context, Boolean value, String key, String name){
        SharedPreferences.Editor preferenceManager = (SharedPreferences.Editor) Objects.requireNonNull(context.getSharedPreferences(name, Context.MODE_PRIVATE));
        preferenceManager.putBoolean(key, value);
        preferenceManager.apply();
    }

    public static void setInt(Context context, Integer value, String key, String name){
        SharedPreferences.Editor preferenceManager = (SharedPreferences.Editor) Objects.requireNonNull(context.getSharedPreferences(name, Context.MODE_PRIVATE));
        preferenceManager.putInt(key, value);
        preferenceManager.apply();
    }

    public static boolean getDarkMode(Context context){
        SharedPreferences preferences = Objects.requireNonNull(context.getSharedPreferences("DarkMode", android.content.Context.MODE_PRIVATE));
        return preferences.getBoolean("Mode", true);

    }

}
