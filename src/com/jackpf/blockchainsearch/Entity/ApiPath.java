package com.jackpf.blockchainsearch.Entity;

import com.jackpf.blockchainsearch.Data.BlockchainData;

/**
 * ApiPath class
 */
public class ApiPath
{
	/**
	 * Path
	 */
	private String path;
	
	/**
	 * Constructor
	 * 
	 * @param url
	 * @param params
	 */
	public ApiPath(String url, Object ...params)
	{
		path = String.format(
			BlockchainData.BLOCKCHAIN_URL + "/" + url,
			params
		);
	}
	
	/**
	 * Get path
	 * 
	 * @return
	 */
	public String getPath()
	{
		return path;
	}
}
