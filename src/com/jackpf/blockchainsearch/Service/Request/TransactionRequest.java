package com.jackpf.blockchainsearch.Service.Request;

import java.security.InvalidParameterException;

import org.json.simple.JSONObject;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.ApiPath;
import com.jackpf.blockchainsearch.Entity.RequestResponse;
import com.jackpf.blockchainsearch.Interface.RequestInterface;

public class TransactionRequest extends RequestInterface
{
	public TransactionRequest(Object ...params)
	{
		super(params);
	}
	
	public RequestResponse call() throws Exception
	{
		if (this.params.length < 1) {
			throw new InvalidParameterException("No transaction specified");
		}
		
		ApiPath path = new ApiPath(BlockchainData.TRANSACTION_URL, (String) this.params[0]);
		JSONObject json = this.blockchain.request(path);
		
		RequestResponse response = new RequestResponse();
		
		response.put("response", json);
        
        // Also get the current block count
        path = new ApiPath(BlockchainData.BLOCKCOUNT_URL);
        response.put("block_count", this.blockchain.rawRequest(path));
		
		return response;
	}
}
