package com.jackpf.blockchainsearch.View;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Interface.UIInterface;
import com.jackpf.blockchainsearch.Service.QRCode;
import com.jackpf.blockchainsearch.Service.Utils;

public class AddressActionUI extends UIInterface
{
	private Activity activity;
	
	private View loadingView;
	
	private ViewPager viewPager;
	private TabsPagerAdapter tabAdapter;
	private ActionBar actionBar;
	Fragment[] tabs = {new OverviewFragment(), new TransactionsFragment()};
	private final String[] tabTitles = {"Overview", "Transactions"};
	
	public AddressActionUI(Context context)
	{
		super(context);
		
		activity = (Activity) context;
	}
	
	public void initialise()
	{
		// Initilization
		viewPager = (ViewPager) activity.findViewById(R.id.content);
		actionBar = activity.getActionBar();
		tabAdapter = new TabsPagerAdapter(((FragmentActivity) context).getSupportFragmentManager());

		viewPager.setAdapter(tabAdapter);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
		    public void onPageSelected(int position) { actionBar.setSelectedNavigationItem(position); }
		    public void onPageScrolled(int arg0, float arg1, int arg2) {}
		    public void onPageScrollStateChanged(int arg0) {}
		});

		// Adding Tabs
		for (String tab : tabTitles) {
			actionBar.addTab(actionBar.newTab().setText(tab).setTabListener(new TabListener()));
		}
	}
	
	public void preUpdate()
	{
		loadingView = activity.findViewById(R.id.loading);
	}
	
	public void update()
	{
		loadingView.setVisibility(View.GONE);
		
		final JSONObject json = (JSONObject) vars.get("response");
		
		actionBar.setSubtitle(json.get("address").toString());
		
		// Overview fragment
		View overviewFragment = activity.findViewById(R.id.content_overview);
		
		((TextView) overviewFragment.findViewById(R.id._address_final_balance)).setText(Utils.btcFormat((Long) json.get("final_balance")));
		((TextView) overviewFragment.findViewById(R.id._address_total_received)).setText(Utils.btcFormat((Long) json.get("total_received")));
		((TextView) overviewFragment.findViewById(R.id._address_total_sent)).setText(Utils.btcFormat((Long) json.get("total_sent")));
		((TextView) overviewFragment.findViewById(R.id._address_no_transactions)).setText(json.get("n_tx").toString());
		
		ImageView qrCode = (ImageView) overviewFragment.findViewById(R.id._address_qr_code);
		try {
		    qrCode.setImageDrawable(new BitmapDrawable(context.getResources(), QRCode.create(json.get("address").toString(), 256)));
		} catch (Exception e) {
		    error(e);
		}
		
		// Transactions fragment
		View transactionsFragment = activity.findViewById(R.id.content_transactions);
		JSONArray txs = (JSONArray) json.get("txs");
		LayoutInflater inflater = activity.getLayoutInflater();
		TableLayout transactionsTable = (TableLayout) transactionsFragment.findViewById(R.id._address_transactions);
		
		int i = 0;
		transactionsTable.removeAllViews(); // Get rid of rows in case we're updating
		for (Object o : txs) {
			final JSONObject tx = (JSONObject) o;
			
			TableRow tr = (TableRow) inflater.inflate(R.layout._address_transactions_row, null);

			((TextView) tr.findViewById(R.id.hash)).setText(tx.get("hash").toString());

			Object bc = vars.get("block_count"), bh = tx.get("block_height");
			int blockCount = Integer.parseInt(bc.toString());
			int blockHeight = bh == null ? blockCount - 1 : Integer.parseInt(bh.toString());
			
			int confirmations = blockCount - blockHeight + 1;
			((ImageView) tr.findViewById(R.id.confirmations)).setImageDrawable(new BitmapDrawable(context.getResources(), Utils.drawConfirmationsArc(confirmations, 3, Color.parseColor("#F06699CC"), Color.parseColor("#60666666"), 24)));
			
			Object r = tx.get("result");
			long result = Long.parseLong(r.toString());
			TextView resultTextView = (TextView) tr.findViewById(R.id.amount);
			if (result > 0) {
				resultTextView.setTextColor(Color.GREEN);
			} else if (result < 0) {
				resultTextView.setTextColor(Color.RED);
			}
			resultTextView.setText(Utils.btcFormat(result).replace("-", ""));
			
			if (i % 2 == 1) {
				// This should be from style really
				tr.setBackgroundColor(Color.parseColor("#f9f9f9"));
			}
			
			transactionsTable.addView(tr);
			
			i++;
		}
		
		activity.findViewById(R.id.content).setVisibility(View.VISIBLE);
	}
	
	public void error(Exception e)
	{
		e.printStackTrace();
		
		Toast.makeText(
			context.getApplicationContext(),
			context.getString(R.string.text_exception, e.getMessage()),
			Toast.LENGTH_SHORT
		).show();
	}
	
	protected class TabListener implements android.app.ActionBar.TabListener
	{
		@Override
		public void onTabReselected(Tab arg0, android.app.FragmentTransaction tx) { }

		@Override
		public void onTabSelected(Tab tab, android.app.FragmentTransaction arg1)
		{
			viewPager.setCurrentItem(tab.getPosition());
		}

		@Override
		public void onTabUnselected(Tab arg0, android.app.FragmentTransaction tx) { }
	}
	
	protected class TabsPagerAdapter extends FragmentPagerAdapter
	{
		public TabsPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int index)
		{
			return tabs[index];
		}

		@Override
		public int getCount()
		{
			return tabs.length;
		}
	}
	
	protected static class OverviewFragment extends Fragment
	{
		public OverviewFragment()
		{
			super();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout._address_overview, container, false);
			return rootView;
		}
	}
	
	protected static class TransactionsFragment extends Fragment
	{
		public TransactionsFragment()
		{
			super();
		}
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
		{
			View rootView = inflater.inflate(R.layout._address_transactions, container, false);
			return rootView;
		}
	}
}