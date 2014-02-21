package com.jackpf.blockchainsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;

import com.jackpf.blockchainsearch.Interface.UIInterface;
import com.jackpf.blockchainsearch.Service.Request.TransactionRequest;
import com.jackpf.blockchainsearch.View.TransactionActionUI;

public class TransactionActivity extends Activity
{
	public final static String EXTRA_SEARCH = "search";
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
		setContentView(R.layout.activity_address);

		UIInterface ui = new TransactionActionUI(this);
		
		ui.initialise();
		
		Intent intent = getIntent();
		String searchText = intent.getStringExtra(EXTRA_SEARCH);
		
		new NetworkThread(
			this,
			new TransactionRequest(searchText),
			ui
		).execute();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.address, menu);
		
		return true;
	}
}
