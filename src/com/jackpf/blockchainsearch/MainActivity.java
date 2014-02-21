package com.jackpf.blockchainsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jackpf.blockchainsearch.Entity.PersistedAddresses;
import com.jackpf.blockchainsearch.View.MainActionUI;

public class MainActivity extends Activity
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
		
		Intent intent = new Intent(this, AddressActivity.class);
		intent.putExtra(AddressActivity.EXTRA_SEARCH, searchText);
		startActivity(intent);
	}
}
