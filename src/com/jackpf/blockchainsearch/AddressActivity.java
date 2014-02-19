package com.jackpf.blockchainsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

import com.jackpf.blockchainsearch.Service.Blockchain;
import com.jackpf.blockchainsearch.View.AddressActionUI;

public class AddressActivity extends Activity
{
	public final static String EXTRA_SEARCH = "search";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_address);
		
		Intent intent = getIntent();
		String searchText = intent.getStringExtra(EXTRA_SEARCH);
		
		new NetworkThread(
			this,
			new AddressActionUI(this)
		).execute(Blockchain.ADDRESS_URL, searchText);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.address, menu);
		
		return true;
	}

}
