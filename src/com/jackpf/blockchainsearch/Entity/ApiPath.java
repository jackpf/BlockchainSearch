package com.jackpf.blockchainsearch.Entity;

import com.jackpf.blockchainsearch.Data.BlockchainData;

public class ApiPath
{
	private String path;
	
	public ApiPath(String url, Object ...params)
	{
		path = String.format(
			BlockchainData.BLOCKCHAIN_URL + "/" + url,
			params
		);
	}
	
	public String getPath()
	{
		return path;
	}
}
