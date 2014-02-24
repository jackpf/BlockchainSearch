package com.jackpf.blockchainsearch.Entity;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;

public class PersistedAddresses
{
	private final static String FILENAME = "addresses.json";
	
	private Context context;
	
	HashMap<String, String> addresses = new HashMap<String, String>();
	
	public PersistedAddresses(Context context)
	{
		this.context = context;
		
		restore();
	}
    
    public void add(String address, String name)
    {
        addresses.put(address, name);
        save();
    }
    
    public void remove(String address)
    {
        addresses.remove(address);
        save();
    }
    
    public boolean has(String address)
    {
        return addresses.containsKey(address);
    }
    
    public String get(String address)
    {
        return addresses.get(address);
    }
    
    public HashMap<String, String> getAll()
    {
        return new HashMap<String, String>(addresses);
    }
	
	protected void save()
	{
		try {
			FileOutputStream out = context.openFileOutput(FILENAME, Context.MODE_PRIVATE);
			
			JSONObject obj = new JSONObject();
			
			for (Map.Entry<String, String> entry : addresses.entrySet()) {
			    obj.put(entry.getKey(), entry.getValue());
			}
			
			out.write(obj.toString().getBytes());
			out.close();
		} catch (FileNotFoundException e) { } catch (IOException e) { } catch (JSONException e) { }
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
		    
		    JSONObject obj = new JSONObject(sb.toString());
		    
		    Iterator<?> keys = obj.keys();

	        while(keys.hasNext()) {
	            String key = (String) keys.next();
	            addresses.put(key, (String) obj.get(key));
	        }
	        
		} catch (FileNotFoundException e) { } catch (IOException e) { } catch (JSONException e) { }
	}
}
