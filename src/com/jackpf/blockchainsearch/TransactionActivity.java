package com.jackpf.blockchainsearch;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.Toast;

import com.jackpf.blockchainsearch.Interface.UIInterface;
import com.jackpf.blockchainsearch.Service.Request.TransactionRequest;
import com.jackpf.blockchainsearch.View.TransactionActionUI;

public class TransactionActivity extends Activity
{
    public final static String EXTRA_SEARCH = "search";
    
    private UIInterface ui;
    private String searchText;
    private NetworkThread thread;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        setContentView(R.layout.activity_transaction);
        
        getActionBar().setHomeButtonEnabled(true);
        getActionBar().setDisplayHomeAsUpEnabled(true);

        ui = new TransactionActionUI(this);
        
        ui.initialise();
        
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
        
        refresh();
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
        getMenuInflater().inflate(R.menu.transaction, menu);
        
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
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
