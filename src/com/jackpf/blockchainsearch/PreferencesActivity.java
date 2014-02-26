package com.jackpf.blockchainsearch;

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
        
        addPreferencesFromResource(R.xml.preferences);
        
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(this);
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
}
