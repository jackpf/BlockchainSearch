package com.jackpf.blockchainsearch.View;

import java.net.InetAddress;
import java.net.UnknownHostException;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Interface.UIInterface;

public class TransactionActionUI extends UIInterface
{
	private Activity activity;
	
	private View loadingView;
	
	public TransactionActionUI(Context context)
	{
		super(context);
		
		activity = (Activity) context;
	}
	
	public void initialise()
	{
		
	}
	
	public void preUpdate()
	{
		loadingView = activity.findViewById(R.id.loading);
	}
	
	public void update()
	{
		loadingView.setVisibility(View.GONE);
		
		final JSONObject json = (JSONObject) vars.get("response");
		
		activity.getActionBar().setSubtitle(json.get("hash").toString());
		
		View transactionView = activity.findViewById(R.id._transaction_table);

		((TextView) transactionView.findViewById(R.id._transaction_hash)).setText(json.get("hash").toString());
		((TextView) transactionView.findViewById(R.id._transaction_index)).setText(json.get("tx_index").toString());
		((TextView) transactionView.findViewById(R.id._transaction_date)).setText(json.get("time").toString());
		((TextView) transactionView.findViewById(R.id._transaction_size)).setText(json.get("size").toString() + " bytes");
		((TextView) transactionView.findViewById(R.id._transaction_double_spend)).setText(json.get("double_spend").toString());
		Object bh = json.get("block_height");
		int blockHeight = 0, blockCount = Integer.parseInt(vars.get("block_count").toString());
		if (bh != null) {
		    blockHeight = Integer.parseInt(bh.toString());
		} else {
		    blockHeight = blockCount + 1;
		}
        ((TextView) transactionView.findViewById(R.id._transaction_block_height)).setText(Integer.toString(blockHeight));
        ((TextView) transactionView.findViewById(R.id._transaction_confirmations)).setText(Integer.toString(blockCount - blockHeight + 1));
		
		String relayedBy = json.get("relayed_by").toString();
		try {
			relayedBy = String.format("%s (%s)", InetAddress.getByName(relayedBy).toString(), relayedBy);
		} catch (UnknownHostException e) { }
		((TextView) transactionView.findViewById(R.id._transaction_relayed_by)).setText(relayedBy);
		
		activity.findViewById(R.id.content).setVisibility(View.VISIBLE);
	}
	
	public void error(Exception e)
	{
		e.printStackTrace();
	}
}