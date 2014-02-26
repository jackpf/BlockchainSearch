package com.jackpf.blockchainsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.jackpf.blockchainsearch.Service.Utils;
import com.jackpf.blockchainsearch.View.MainActionUI;

public class MainActivity extends FragmentActivity
{
    private /*UIInterface*/ MainActionUI ui;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        
        getActionBar().setTitle(getString(R.string.activity_main_title));
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        
        setContentView(R.layout.activity_main);
        
        ui = new MainActionUI(this);
        ui.initialise();
        
        startService(new Intent(this, WatchedAddressesService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        if (ui.drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * On search button click
     * 
     * @param v
     */
    public void onSubmit(View v)
    {
        TextView searchTextView = (TextView) findViewById(R.id.search);
        String searchText = searchTextView.getText().toString();
        
        if (Utils.validAddress(searchText)) {
            Intent intent = new Intent(this, AddressActivity.class);
            intent.putExtra(AddressActivity.EXTRA_SEARCH, searchText);
            startActivity(intent);
        } else if (Utils.validTransaction(searchText)) {
            Intent intent = new Intent(this, TransactionActivity.class);
            intent.putExtra(TransactionActivity.EXTRA_SEARCH, searchText);
            startActivity(intent);
        } else {
            Toast.makeText(
                getApplicationContext(),
                getString(R.string.text_invalid_input),
                Toast.LENGTH_SHORT
            ).show();
            
            return;
        }
    }
}
