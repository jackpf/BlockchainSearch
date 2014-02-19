package com.jackpf.blockchainsearch;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;

import com.jackpf.blockchainsearch.Service.Blockchain;
import com.jackpf.blockchainsearch.View.AddressActionUI;

public class MainActivity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		
		return true;
	}
	
	public void onSubmit(View v)
	{
		Blockchain bc = new Blockchain();
		
		TextView searchTextView = (TextView) findViewById(R.id.search);
		String searchText = searchTextView.getText().toString();
		
		new NetworkThread(this, new AddressActionUI(this, findViewById(R.id.content))).execute(Blockchain.ADDRESS_URL, searchText);
	}
}
