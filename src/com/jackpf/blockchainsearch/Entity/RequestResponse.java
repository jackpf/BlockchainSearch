package com.jackpf.blockchainsearch.Entity;

/**
 * Request response
 * 
 * @param <T>
 */
public class RequestResponse<T>
{
	/**
	 * Response object
	 */
	private T response;
	
	/**
	 * Constructor
	 * 
	 * @param response
	 */
	public RequestResponse(T response)
	{
		this.response = response;
	}
	
	/**
	 * Get response
	 * 
	 * @return
	 */
	public T getResponse()
	{
		return response;
	}
}
