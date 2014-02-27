package com.jackpf.blockchainsearch;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class PreferencesActivity extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener
{
    @Override
    @SuppressWarnings("deprecation")
    public void onCreate(Bundle savedInstanceState)
    {        
        super.onCreate(savedInstanceState);
        
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        
        syncServiceStates(prefs);
        
        addPreferencesFromResource(R.xml.preferences);
        
        prefs.registerOnSharedPreferenceChangeListener(this);
    }
    
    @Override
    public void onSharedPreferenceChanged(SharedPreferences preferences, String key)
    {
        String waPrefKey = getString(R.string.pref_watch_addresses_key);
        if (key.equals(waPrefKey)) {
            if (preferences.getBoolean(waPrefKey, false)) {
                startService(new Intent(this, WatchedAddressesService.class));
            } else {
                stopService(new Intent(this, WatchedAddressesService.class));
            }
        }
    }
    
    private void syncServiceStates(SharedPreferences prefs)
    {
        SharedPreferences.Editor editor = prefs.edit();
        
        String waKey = getString(R.string.pref_watch_addresses_key);
        if (serviceIsRunning(WatchedAddressesService.class)) {
            editor.putBoolean(waKey, true);
        } else {
            editor.putBoolean(waKey, false);
        }
        
        editor.commit();
    }
    
    private boolean serviceIsRunning(Class<?> c)
    {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (c.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }
}
