package com.jackpf.blockchainsearch;

import java.util.HashMap;

import org.json.simple.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.jackpf.blockchainsearch.Interface.RequestInterface;
import com.jackpf.blockchainsearch.Interface.UIInterface;

public class NetworkThread extends AsyncTask<String, Void, Void>
{
	private final static int ARGC = 2;
	
	private Context context;
	private RequestInterface request;
	private UIInterface ui;
	HashMap<String, Object> vars = new HashMap<String, Object>();
	Exception e = null;
	
	public NetworkThread(Context context, RequestInterface request, UIInterface ui)
	{
		this.context = context;
		this.request = request;
		this.ui = ui;
	}

	@Override
	protected void onPreExecute()
	{
		ui.preUpdate();
	}

	@Override
    protected Void doInBackground(String... params)
    {
		try {
			JSONObject response = request.call().getResponse();
			
			vars.put("response", response);
		} catch (Exception e) {
			this.e = e;
		}

		return null;
    }
    
	@Override
    protected void onPostExecute(Void _void)
	{
		ui.setVars(vars);

		if (e == null) {
			ui.update();
		} else {
			ui.error(e);
		}
	}
}
