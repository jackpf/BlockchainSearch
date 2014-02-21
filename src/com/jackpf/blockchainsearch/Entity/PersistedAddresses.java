package com.jackpf.blockchainsearch.Entity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;

public class PersistedAddresses extends ArrayList<String>
{
	private final static String FILENAME = "addresses.json";
	
	private Context context;
	
	public PersistedAddresses(Context context)
	{
		this.context = context;
		
		restore();
	}
	
	@Override
	public boolean add(String e)
	{
		boolean r = super.add(e);
		save();
		return r;
	}
	
	protected void save()
	{
		try {
			FileOutputStream out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			out.write(new JSONArray(this).toString().getBytes());
			out.close();
		} catch (FileNotFoundException e) { } catch (IOException e) { }
	}
	
	protected void restore()
	{
		try {
			FileInputStream in = context.openFileInput(FILENAME);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
		    
		    StringBuilder sb = new StringBuilder();
		    String line;
		    while ((line = br.readLine()) != null) {
		        sb.append(line);
		    }
		    
		    JSONArray json = new JSONArray(sb.toString());
		    
		    for (int i = 0; i < json.length(); i++) {
		    	this.add(json.getString(i));
	    	}
		} catch (FileNotFoundException e) { } catch (IOException e) { } catch (JSONException e) { }
	}
}
