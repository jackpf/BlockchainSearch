package com.jackpf.blockchainsearch;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.Window;

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

		AddressActionUI addressUI = new AddressActionUI(this);
		
		addressUI.initialise();
		
		Intent intent = getIntent();
		String searchText = intent.getStringExtra(EXTRA_SEARCH);
		
		new NetworkThread(
			this,
			new AddressRequest(searchText),
			addressUI
		).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.address, menu);
		
		return true;
	}
}
