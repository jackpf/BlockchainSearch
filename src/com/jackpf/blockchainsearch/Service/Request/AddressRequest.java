package com.jackpf.blockchainsearch.Service.Request;

import java.security.InvalidParameterException;

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
	
	public RequestResponse call() throws Exception
	{
		if (this.params.length < 1) {
			throw new InvalidParameterException("No address specified");
		}
		
		RequestResponse response = new RequestResponse();
		
		ApiPath path = new ApiPath(BlockchainData.ADDRESS_URL, (String) this.params[0]);
		response.put("response", this.blockchain.request(path));
		
		// Also get the current block count
		path = new ApiPath(BlockchainData.BLOCKCOUNT_URL);
		response.put("block_count", this.blockchain.rawRequest(path));
		
		return response;
	}
}
