package com.example.grasstrimmer.Model;

import android.content.Context;
import android.content.SharedPreferences;

public class Helpers {
    public static void addToPreferences(String key, String value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GrassTrimmerPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
        editor.commit();
    }

    public static String getFromPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GrassTrimmerPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getString(key, "");
    }

    public static void addIntToPreferences(String key, Integer value, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GrassTrimmerPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.apply();
        editor.commit();
    }

    public static Integer getIntFromPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("GrassTrimmerPreferences", Context.MODE_PRIVATE);
        return sharedPreferences.getInt(key, 0);
    }

    public static void removeFromPreferences(String key, Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences("GrassTrimmerPreferences", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.apply();
        editor.commit();
    }
}
