package com.jackpf.blockchainsearch;

import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jackpf.blockchainsearch.Model.UIInterface;
import com.jackpf.blockchainsearch.Service.Request.TransactionRequest;
import com.jackpf.blockchainsearch.View.TransactionActivityUI;

@SuppressWarnings("deprecation") // Legacy clipboard manager warnings
public class TransactionActivity extends SherlockActivity
{
    public final static String EXTRA_SEARCH = "search";
    
    protected UIInterface ui;
    protected String searchText;
    protected NetworkThread thread;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeManager.setTheme(this);
        
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        
        setContentView(R.layout.activity_transaction);

        ui = new TransactionActivityUI(this);
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
            new TransactionRequest(searchText),
            ui
        );
        
        thread.execute();
    }
    
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        
        thread.cancel(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportMenuInflater().inflate(R.menu.transaction, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case android.R.id.home:
                return MenuManager.back(this);
            case R.id.action_refresh:
                Toast.makeText(getApplicationContext(), getString(R.string.text_refreshing), Toast.LENGTH_SHORT).show();
                refresh();
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
}
