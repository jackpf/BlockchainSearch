package com.jackpf.blockchainsearch;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jackpf.blockchainsearch.Entity.Addresses;
import com.jackpf.blockchainsearch.Entity.Wallets;
import com.jackpf.blockchainsearch.Model.UIInterface;
import com.jackpf.blockchainsearch.Service.Request.AddressRequest;
import com.jackpf.blockchainsearch.View.AddressActivityUI;

@SuppressWarnings("deprecation") // Legacy clipboard manager warnings
public class AddressActivity extends SherlockFragmentActivity
{
    public final static String EXTRA_SEARCH = "search";
    
    protected UIInterface ui;
    protected String searchText;
    protected NetworkThread thread;
    protected Addresses persistedAddresses;
    protected MenuItem saveMenuItem;
    
    /**
     * Current page of transactions we're on
     */
    protected int page = 1;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeManager.setTheme(this);
        
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.activity_address);
        
        persistedAddresses = new Addresses(this);

        ui = new AddressActivityUI(this);
        ui.initialise();
        
        searchText = getSearchText(getIntent());
        
        refresh();
    }
    
    protected String getSearchText(Intent intent)
    {
        if (Intent.ACTION_VIEW.equals(intent.getAction()) && intent.getData() != null) {
            List<String> segments = intent.getData().getPathSegments();
            if (segments.size() > 1) {
                return segments.get(1);
            }
        }
        
        return getIntent().getStringExtra(EXTRA_SEARCH);
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
    protected void refresh()
    {
        if (thread instanceof NetworkThread) {
            thread.cancel(true);
        }
        
        thread = new NetworkThread(
            this,
            new AddressRequest(searchText, page),
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
            saveMenuItem.setIcon(android.R.drawable.ic_menu_delete);
        }
        
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportMenuInflater().inflate(R.menu.address, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                return MenuManager.back(this);
            case R.id.action_refresh:
                page = 1; // Reset page
                Toast.makeText(getApplicationContext(), getString(R.string.text_refreshing), Toast.LENGTH_SHORT).show();
                refresh();
                return true;
            case R.id.action_save:
                if (!persistedAddresses.has(searchText)) {
                    Helpers.promptPersistAddress(this, searchText, persistedAddresses, saveMenuItem, "", null);
                } else {
                    Helpers.promptRemoveAddress(this, searchText, persistedAddresses, saveMenuItem, null);
                }
                return true;
            case R.id.action_add:
                Helpers.promptAddToWallet(this, searchText, new Wallets(this), null);
                return true;
            case R.id.action_copy:
                ClipboardManager cm = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                cm.setText(searchText);
                Toast.makeText(getApplicationContext(), getString(R.string.text_copied), Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_settings:
                return MenuManager.openSettings(this);
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    public void onNextPage(View v)
    {
        page++;
        refresh();
    }
}
