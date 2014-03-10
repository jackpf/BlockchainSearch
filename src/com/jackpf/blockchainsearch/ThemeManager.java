package com.jackpf.blockchainsearch;

import android.app.Activity;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class ThemeManager
{
    public static void setTheme(Activity context)
    {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        int theme = Integer.parseInt(prefs.getString(context.getString(R.string.pref_theme_key), context.getString(R.string.pref_theme_default)));
        
        switch (theme) {
            case 0:
                context.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light_DarkActionBar);
            break;
            case 1:
                context.setTheme(com.actionbarsherlock.R.style.Theme_Sherlock_Light);
            break;
        }
    }
}
