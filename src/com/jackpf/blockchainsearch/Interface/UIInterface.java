package com.jackpf.blockchainsearch.Interface;

import java.util.HashMap;

import android.content.Context;
import android.view.View;

public abstract class UIInterface
{
	protected Context context;
	protected View rootView;
	
	public UIInterface(Context context, View rootView)
	{
		this.context = context;
		this.rootView = rootView;
	}
	
	public abstract void setVars(HashMap<String, Object> vars);
	public abstract void update();
	public abstract void error(Exception e);
}
