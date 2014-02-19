package com.jackpf.blockchainsearch;

import java.util.HashMap;

import org.json.simple.JSONObject;

import android.content.Context;
import android.os.AsyncTask;

import com.jackpf.blockchainsearch.Entity.ApiPath;
import com.jackpf.blockchainsearch.Interface.UIInterface;
import com.jackpf.blockchainsearch.Service.Blockchain;

public class NetworkThread extends AsyncTask<String, Void, Void>
{
	private final static int ARGC = 2;
	
	private Context context;
	private UIInterface ui;
	HashMap<String, Object> vars = new HashMap<String, Object>();
	Exception e = null;
	
	public NetworkThread(Context context, UIInterface ui)
	{
		this.context = context;
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
		if (params.length < ARGC) {
			throw new IllegalArgumentException("Too few arguments");
		}
		
		String	url		= params[0],
				query	= params[1];
		
		Blockchain bc = new Blockchain();
		
		try {
			JSONObject response = bc.request(new ApiPath(url, query));
			
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
