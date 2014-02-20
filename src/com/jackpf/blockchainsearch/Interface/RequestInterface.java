package com.jackpf.blockchainsearch.Interface;

import org.json.simple.JSONObject;

import com.jackpf.blockchainsearch.Entity.RequestResponse;
import com.jackpf.blockchainsearch.Service.Blockchain;

public abstract class RequestInterface
{
	protected Object[] params;
	protected Blockchain blockchain = new Blockchain();
	
	public RequestInterface(Object ...params)
	{
		this.params = params;
		
	}
	
	public abstract RequestResponse<JSONObject> call() throws Exception;
}