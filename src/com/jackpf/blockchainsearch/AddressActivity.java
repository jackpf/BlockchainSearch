package com.jackpf.blockchainsearch;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.jackpf.blockchainsearch.Entity.PersistedAddresses;
import com.jackpf.blockchainsearch.Service.Request.AddressRequest;
import com.jackpf.blockchainsearch.View.AddressActionUI;

public class AddressActivity extends FragmentActivity
{
    public final static String EXTRA_SEARCH = "search";
    
    private AddressActionUI ui;
    private String searchText;
    private NetworkThread thread;
    private PersistedAddresses persistedAddresses;
    MenuItem saveMenuItem;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_address);
        
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);
        
        persistedAddresses = new PersistedAddresses(this);

        ui = new AddressActionUI(this);
        
        final Intent intent = getIntent();
        final String action = intent.getAction();

        if (Intent.ACTION_VIEW.equals(action)) {
            final List<String> segments = intent.getData().getPathSegments();
            if (segments.size() > 1) {
                searchText = segments.get(1);
            }
        } else {
            searchText = getIntent().getStringExtra(EXTRA_SEARCH);
        }
        
        ui.initialise();
        
        refresh();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        thread.cancel(true);
    }
    
    /**
     * Runs the network thread and updates the UI
     * Sectioned off since it's called from onCreate and from the refresh button
     */
    private void refresh()
    {
        if (thread instanceof NetworkThread) {
            thread.cancel(true);
        }
        
        thread = new NetworkThread(
            this,
            new AddressRequest(searchText),
            ui
        );
        
        thread.execute();
    }
    
    @Override
    public boolean onPrepareOptionsMenu(Menu menu)
    {
        super.onPrepareOptionsMenu(menu);
        
        saveMenuItem = menu.findItem(R.id.action_save);
        
        if (persistedAddresses.has(searchText)) {
            saveMenuItem.setIcon(R.drawable.ic_menu_saved);
        }
        
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.address, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_refresh:
                Toast.makeText(getApplicationContext(), getString(R.string.text_refreshing), Toast.LENGTH_SHORT).show();
                refresh();
                return true;
            case R.id.action_save:
                if (!persistedAddresses.has(searchText)) {
                    ui.promptPersistAddress(searchText, persistedAddresses, saveMenuItem);
                } else {
                    ui.promptRemoveAddress(searchText, persistedAddresses, saveMenuItem);
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
