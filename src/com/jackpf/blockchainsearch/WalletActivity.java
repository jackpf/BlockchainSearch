package com.jackpf.blockchainsearch;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jackpf.blockchainsearch.Entity.Wallets;
import com.jackpf.blockchainsearch.Model.UIInterface;
import com.jackpf.blockchainsearch.Service.Request.WalletRequest;
import com.jackpf.blockchainsearch.View.WalletActivityUI;

public class WalletActivity extends SherlockFragmentActivity
{
    public static final String EXTRA_WALLET = "wallet";
    
    protected UIInterface ui;
    protected String searchText;
    protected NetworkThread thread;
    protected Wallets wallets;
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
        
        wallets = new Wallets(this);

        ui = new WalletActivityUI(this);
        ui.initialise();
        
        searchText = getIntent().getStringExtra(EXTRA_WALLET);
        
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
    protected void refresh()
    {
        if (thread instanceof NetworkThread) {
            thread.cancel(true);
        }
        
        // Restore wallets again in case an address has been added
        wallets.restore();
        
        thread = new NetworkThread(
            this,
            new WalletRequest(wallets.get(searchText), page),
            ui
        );
        
        thread.execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportMenuInflater().inflate(R.menu.wallet, menu);
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
