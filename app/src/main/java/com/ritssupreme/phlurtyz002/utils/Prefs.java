package com.ritssupreme.phlurtyz002.utils;

import android.content.Context;
import android.content.SharedPreferences;

public class Prefs {

    private static final String PREFS_SETTINGS = "SETTINGS";
    private static final String FIRST_TIME = "firstTime";


    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    public Prefs(Context context) {
        preferences = context.getSharedPreferences(PREFS_SETTINGS, Context.MODE_PRIVATE);
    }

    public Prefs(Context context, String name) {
        preferences = context.getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    public Prefs open() {
        editor = preferences.edit();
        return this;
    }

    public Prefs putInt(String key, int value) {
        editor.putInt(key, value);
        return this;
    }

    public int getInt(String key) {
        return preferences.getInt(key, 0);
    }

    public Prefs putBoolean(String key, boolean value) {
        editor.putBoolean(key, value);
        return this;
    }

    public boolean getBoolean(String key) {
        return preferences.getBoolean(key, false);
    }

    public Prefs putString(String key, String value) {
        editor.putString(key, value);
        return this;
    }

    public String getString(String key) {
        return preferences.getString(key, "");
    }

    public void close() {
        editor.apply();
    }

    public Boolean isFirstTime() {

        if (preferences.getBoolean(FIRST_TIME, true)) {

            editor.putBoolean(FIRST_TIME, false);

            editor.commit();

            return true;

        } else {

            return false;
        }
    }
}
