package com.jackpf.blockchainsearch;

import android.app.Activity;
import android.content.Intent;

public class MenuManager
{
    /**
     * Open settings activity
     * 
     * @param context
     * @return
     */
    public static boolean openSettings(Activity context)
    {
        Intent intent = new Intent(context, PreferencesActivity.class);
        context.startActivity(intent);
        
        return true;
    }
    
    /**
     * Finish activity
     * 
     * @param context
     * @return
     */
    public static boolean back(Activity context)
    {
        context.finish();
        
        return true;
    }
}
