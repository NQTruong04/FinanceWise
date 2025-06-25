package com.example.financewise.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceManager {
    private static final String PREF_NAME = "FinanceWisePrefs";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_IS_FIRST_TIME = "isFirstTime";
    private static PreferenceManager instance;
    private final SharedPreferences sharedPreferences;

    private PreferenceManager(Context context) {
        sharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
    }

    public static synchronized PreferenceManager getInstance(Context context) {
        if (instance == null) {
            instance = new PreferenceManager(context.getApplicationContext());
        }
        return instance;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        sharedPreferences.edit().putBoolean(KEY_IS_LOGGED_IN, isLoggedIn).apply();
    }

    public boolean isLoggedIn() {
        return sharedPreferences.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void setFirstTime(boolean isFirstTime){
        sharedPreferences.edit().putBoolean(KEY_IS_FIRST_TIME, isFirstTime).apply();
    }
    public boolean isFirstTime(){
        return sharedPreferences.getBoolean(KEY_IS_FIRST_TIME, true);
    }
    public void clear() {
        sharedPreferences.edit().clear().apply();
    }
}