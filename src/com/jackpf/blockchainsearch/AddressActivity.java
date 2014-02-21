package com.jackpf.blockchainsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jackpf.blockchainsearch.Interface.UIInterface;
import com.jackpf.blockchainsearch.Service.Request.AddressRequest;
import com.jackpf.blockchainsearch.View.AddressActionUI;

public class AddressActivity extends FragmentActivity
{
	public final static String EXTRA_SEARCH = "search";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_address);

		UIInterface ui = new AddressActionUI(this);
		
		ui.initialise();
		
		Intent intent = getIntent();
		String searchText = intent.getStringExtra(EXTRA_SEARCH);
		
		new NetworkThread(
			this,
			new AddressRequest(searchText),
			ui
		).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.address, menu);
		
		return true;
	}
	
	/**
	 * On transaction table row click
	 * 
	 * @param v
	 */
	public void onClick(View v)
	{
		Intent intent = new Intent(this, TransactionActivity.class);
		intent.putExtra(TransactionActivity.EXTRA_SEARCH, ((TextView) v.findViewById(R.id.hash)).getText().toString());
		startActivity(intent);
	}
}
