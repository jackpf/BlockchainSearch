package com.jackpf.blockchainsearch.Service.Request;

import java.security.InvalidParameterException;

import org.json.simple.JSONObject;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.ApiPath;
import com.jackpf.blockchainsearch.Entity.RequestResponse;
import com.jackpf.blockchainsearch.Interface.RequestInterface;

public class AddressRequest extends RequestInterface
{
	public AddressRequest(Object ...params)
	{
		super(params);
	}
	
	public RequestResponse<JSONObject> call() throws Exception
	{
		if (this.params.length < 1) {
			throw new InvalidParameterException("No address specified");
		}
		
		ApiPath path = new ApiPath(BlockchainData.ADDRESS_URL, (String) this.params[0]);
		return new RequestResponse<JSONObject>(this.blockchain.request(path));
	}
}
