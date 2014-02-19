package com.jackpf.blockchainsearch.View;

import java.util.HashMap;
import java.util.Locale;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Interface.UIInterface;

public class AddressActionUI extends UIInterface
{
	private HashMap<String, Object> vars;
	private Activity activity;
	
	private View loadingView;
	
	public AddressActionUI(Context context)
	{
		super(context);
		
		activity = (Activity) context;
	}
	
	public void setVars(HashMap<String, Object> vars)
	{
		this.vars = vars;
	}
	
	public void preUpdate()
	{
		loadingView = activity.findViewById(R.id.loading);
	}
	
	public void update()
	{
		loadingView.setVisibility(View.GONE);
		
		final JSONObject json = (JSONObject) vars.get("response");
		
		TextView title = (TextView) activity.findViewById(R.id._address_title);
		title.setText(json.get("address").toString());
		
		((TextView) activity.findViewById(R.id._address_final_balance)).setText(btcFormat((Long) json.get("final_balance")));
		((TextView) activity.findViewById(R.id._address_total_received)).setText(btcFormat((Long) json.get("total_received")));
		((TextView) activity.findViewById(R.id._address_total_sent)).setText(btcFormat((Long) json.get("total_sent")));
		((TextView) activity.findViewById(R.id._address_no_transactions)).setText(json.get("n_tx").toString());
		
		((LinearLayout) activity.findViewById(R.id.content)).setVisibility(View.VISIBLE);
	}
	
	private String btcFormat(Long i)
	{
		return String.format(Locale.getDefault(), "%s%.8f", "\u0E3F", i / Math.pow(8, 10));
	}
	
	public void error(Exception e)
	{
		
	}
}