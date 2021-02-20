package com.olivermorgan.ontime.main.BakalariAPI.rozvrh;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.olivermorgan.ontime.main.SharedPrefs;

public class AppSingleton {
    private static final String TAG = AppSingleton.class.getSimpleName();

    @SuppressLint("StaticFieldLeak")
    private static AppSingleton instance;
    @SuppressLint("StaticFieldLeak")
    private static Context ctx;
    private RequestQueue requestQueue;
    private RozvrhAPI rozvrhAPI;


    private AppSingleton(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();
    }

    public static synchronized AppSingleton getInstance(Context context) {
        if (instance == null) {
            instance = new AppSingleton(context.getApplicationContext());
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx);
        }
        return requestQueue;
    }

    public RozvrhAPI getRozvrhAPI() {
        if (rozvrhAPI == null) {
            rozvrhAPI = new RozvrhAPI(getRequestQueue(), ctx.getApplicationContext());
        }
        return rozvrhAPI;
    }



}

