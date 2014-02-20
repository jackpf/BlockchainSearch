package com.jackpf.blockchainsearch.Interface;

import java.util.HashMap;

import android.content.Context;
import android.view.View;

public abstract class UIInterface
{
	protected Context context;
	protected HashMap<String, Object> vars;
	
	public UIInterface(Context context)
	{
		this.context = context;
	}
	
	public void setVars(HashMap<String, Object> vars)
	{
		this.vars = vars;
	}
	
	public abstract void initialise();
	public abstract void preUpdate();
	public abstract void update();
	public abstract void error(Exception e);
}
