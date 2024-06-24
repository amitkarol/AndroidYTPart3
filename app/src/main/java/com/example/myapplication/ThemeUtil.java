package com.example.myapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

public class ThemeUtil {
    private static final String PREFS_NAME = "theme_prefs";
    private static final String KEY_NIGHT_MODE = "night_mode";
    public static final String THEME_CHANGED_ACTION = "com.example.myapplication.THEME_CHANGED";

    public static void applyTheme(Activity activity) {
        SharedPreferences preferences = activity.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isNightMode = preferences.getBoolean(KEY_NIGHT_MODE, false);
        if (isNightMode) {
            activity.setTheme(R.style.NightTheme);
        } else {
            activity.setTheme(R.style.DayTheme);
        }

        // Apply text color and background color change
        View rootView = activity.findViewById(android.R.id.content).getRootView();
        changeAppearance(rootView, isNightMode);
    }

    public static void setNightMode(Context context, boolean isNightMode) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(KEY_NIGHT_MODE, isNightMode);
        editor.apply();

        Intent intent = new Intent(THEME_CHANGED_ACTION);
        context.sendBroadcast(intent);
    }

    public static boolean isNightMode(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        return preferences.getBoolean(KEY_NIGHT_MODE, false);
    }

    public static void changeTextColor(View view, boolean isNightMode) {
        int textColor = isNightMode ? view.getResources().getColor(R.color.white) : view.getResources().getColor(R.color.black);
        int hintTextColor = isNightMode ? view.getResources().getColor(R.color.white) : view.getResources().getColor(R.color.black);

        changeTextColor(view, textColor, hintTextColor);
    }

    public static void changeTextColor(View view, int textColor, int hintTextColor) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                changeTextColor(child, textColor, hintTextColor);
            }
        } else if (view instanceof TextView) {
            ((TextView) view).setTextColor(textColor);
        } else if (view instanceof EditText) {
            ((EditText) view).setTextColor(textColor);
            ((EditText) view).setHintTextColor(hintTextColor);
        }
    }

    public static void changeBackgroundColor(View view, boolean isNightMode) {
        int backgroundColor = isNightMode ? view.getResources().getColor(R.color.darkBackground) : view.getResources().getColor(R.color.white);
        changeBackgroundColor(view, backgroundColor);
    }

    public static void changeBackgroundColor(View view, int backgroundColor) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                changeBackgroundColor(child, backgroundColor);
            }
        } else if (!(view instanceof Button)) {
            view.setBackgroundColor(backgroundColor);
        }
    }

    public static void changeAppearance(View view, boolean isNightMode) {
        int textColor = isNightMode ? view.getResources().getColor(R.color.white) : view.getResources().getColor(R.color.black);
        int hintTextColor = isNightMode ? view.getResources().getColor(R.color.white) : view.getResources().getColor(R.color.black);
        int backgroundColor = isNightMode ? view.getResources().getColor(R.color.darkBackground) : view.getResources().getColor(R.color.white);

        changeAppearance(view, textColor, hintTextColor, backgroundColor);
    }

    public static void changeAppearance(View view, int textColor, int hintTextColor, int backgroundColor) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View child = viewGroup.getChildAt(i);
                changeAppearance(child, textColor, hintTextColor, backgroundColor);
            }
        } else {
            if (view instanceof TextView) {
                ((TextView) view).setTextColor(textColor);
            } else if (view instanceof EditText) {
                ((EditText) view).setTextColor(textColor);
                ((EditText) view).setHintTextColor(hintTextColor);
            }
            if (!(view instanceof Button)) {
                view.setBackgroundColor(backgroundColor);
            }
        }
    }

    public static void applyThemeToRecyclerView(RecyclerView recyclerView, boolean isNightMode) {
        RecyclerView.Adapter adapter = recyclerView.getAdapter();
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
        // Iterate over each child view and apply the theme
        for (int i = 0; i < recyclerView.getChildCount(); i++) {
            View child = recyclerView.getChildAt(i);
            changeAppearance(child, isNightMode);
        }
    }
}