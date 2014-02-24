package com.jackpf.blockchainsearch.Service.Request;

import java.security.InvalidParameterException;

import org.json.simple.JSONArray;
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
	
	public RequestResponse call() throws Exception
	{
		if (this.params.length < 1) {
			throw new InvalidParameterException("No address specified");
		}
		
		String address = (String) this.params[0];
		
		RequestResponse requestResponse = new RequestResponse();
		
		ApiPath path = new ApiPath(BlockchainData.ADDRESS_URL, address);
		JSONObject response = this.blockchain.request(path);
		requestResponse.put("response", response);
		
		// Work out the transactions results
		requestResponse.put("transactions", processTransactions(address, (JSONArray) response.get("txs")));
		
		// Also get the current block count
		path = new ApiPath(BlockchainData.BLOCKCOUNT_URL);
		requestResponse.put("block_count", this.blockchain.rawRequest(path));
		
		return requestResponse;
	}
	
	private JSONArray processTransactions(String address, JSONArray txs)
	{
	    for (Object _tx : txs) {
	        JSONObject tx = (JSONObject) _tx;
	        
	        Long total = 0L;
	        String addrIn = "", addrOut = "";
            
            for (Object _in : (JSONArray) tx.get("inputs")) {
                JSONObject in = (JSONObject) _in;
                JSONObject prev = (JSONObject) in.get("prev_out");
                
                if (address.equals((String) prev.get("addr"))) {
                    total -= Long.parseLong(prev.get("value").toString());
                } else {
                    addrOut = prev.get("addr").toString();
                }
            }
            
            for (Object _out : (JSONArray) tx.get("out")) {
                JSONObject out = (JSONObject) _out;
                
                if (address.equals((String) out.get("addr"))) {
                    total += Long.parseLong(out.get("value").toString());
                } else {
                    addrIn = out.get("addr").toString();
                }
            }

            tx.put("result", total);
            tx.put("addr", total > 0L ? addrOut : addrIn);
	    }
	    
	    return txs;
	}
}
