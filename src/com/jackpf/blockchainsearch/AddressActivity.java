package com.jackpf.blockchainsearch;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.Toast;

import com.jackpf.blockchainsearch.Entity.PersistedAddresses;
import com.jackpf.blockchainsearch.Interface.UIInterface;
import com.jackpf.blockchainsearch.Service.Request.AddressRequest;
import com.jackpf.blockchainsearch.View.AddressActionUI;

public class AddressActivity extends FragmentActivity
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
		setContentView(R.layout.activity_address);
		
		getActionBar().setHomeButtonEnabled(true);
	    getActionBar().setDisplayHomeAsUpEnabled(true);

		ui = new AddressActionUI(this);
		searchText = getIntent().getStringExtra(EXTRA_SEARCH);
		
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
		thread = new NetworkThread(
			this,
			new AddressRequest(searchText),
			ui
		);
		
		thread.execute();
	}
	
	private void persistAddress()
	{
		PersistedAddresses addresses = new PersistedAddresses(this);
		addresses.add(searchText);
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
		    	Toast.makeText(getApplicationContext(), getString(R.string.text_saving), Toast.LENGTH_SHORT).show();
		    	persistAddress();
		    	return true;
		    default:
		        return super.onOptionsItemSelected(item);
	    }
	}
}
