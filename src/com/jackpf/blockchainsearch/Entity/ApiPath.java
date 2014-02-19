package com.jackpf.blockchainsearch.Entity;

import com.jackpf.blockchainsearch.Service.Blockchain;

public class ApiPath
{
	private String path;
	
	public ApiPath(String url, Object ...params)
	{
		path = String.format(
			Blockchain.BLOCKCHAIN_URL + "/" + url,
			params
		);
	}
	
	public String getPath()
	{
		return path;
	}
}
