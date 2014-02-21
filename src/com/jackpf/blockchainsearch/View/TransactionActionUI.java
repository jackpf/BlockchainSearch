package com.jackpf.blockchainsearch.View;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.view.View;

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
		
		activity.findViewById(R.id.content).setVisibility(View.VISIBLE);
	}
	
	public void error(Exception e)
	{
		e.printStackTrace();
	}
}