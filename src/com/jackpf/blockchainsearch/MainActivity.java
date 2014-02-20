package com.jackpf.blockchainsearch;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.jackpf.blockchainsearch.Service.Blockchain;
import com.jackpf.blockchainsearch.View.AddressActionUI;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
	    getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
	    getActionBar().setTitle(getString(R.string.activity_main_title));
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	public void onSubmit(View v)
	{
		TextView searchTextView = (TextView) findViewById(R.id.search);
		String searchText = searchTextView.getText().toString();
		
		Intent intent = new Intent(this, AddressActivity.class);
		intent.putExtra(AddressActivity.EXTRA_SEARCH, searchText);
		startActivity(intent);
	}
}
