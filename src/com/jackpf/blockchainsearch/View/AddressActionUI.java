package com.jackpf.blockchainsearch.View;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Interface.UIInterface;

public class AddressActionUI extends UIInterface
{
	private HashMap<String, Object> vars;
	
	public AddressActionUI(Context context, View rootView)
	{
		super(context, rootView);
	}
	
	public void setVars(HashMap<String, Object> vars)
	{
		this.vars = vars;
	}
	
	public void update()
	{
		final Activity activity			= (Activity) context;
		final LinearLayout rootLayout	= (LinearLayout) rootView;
		final LayoutInflater inflater	= activity.getLayoutInflater();
		final JSONObject json			= (JSONObject) vars.get("response");
		
		View layout = inflater.inflate(R.layout._address, null);
		
		TextView title = (TextView) layout.findViewById(R.id._address_title);
		title.setText(json.get("address").toString());
		
		int[]		keys	= {R.id._address_final_balance, R.id._address_total_received, R.id._address_total_sent, R.id._address_no_transactions};
		String[]	values	= {"final_balance", "total_received", "total_sent", "n_tx"};

		for (int i = 0; i < keys.length; i++) {
			((TextView) layout.findViewById(keys[i])).setText(json.get(values[i]).toString());
		}
		
		rootLayout.addView(layout);
	}
	
	public void error(Exception e)
	{
		
	}
}