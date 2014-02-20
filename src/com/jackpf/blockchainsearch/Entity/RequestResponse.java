package com.jackpf.blockchainsearch.Entity;

public class RequestResponse<T>
{
	private T response;
	
	public RequestResponse(T response)
	{
		this.response = response;
	}
	
	public T getResponse()
	{
		return response;
	}
}
