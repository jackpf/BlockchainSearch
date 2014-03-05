package com.jackpf.blockchainsearch.Service.Request;

import java.security.InvalidParameterException;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.ApiPath;
import com.jackpf.blockchainsearch.Entity.RequestResponse;
import com.jackpf.blockchainsearch.Interface.RequestInterface;
import com.jackpf.blockchainsearch.Service.Utils;

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
        
        int page = 1;
        if (this.params.length == 2) {
            page = (Integer) this.params[1];
        }
        int offset = (page - 1) * BlockchainData.TX_PER_PAGE;
        
        RequestResponse requestResponse = new RequestResponse();
        
        ApiPath path = new ApiPath(BlockchainData.ADDRESS_URL, address, offset);
        JSONObject response = this.blockchain.request(path);
        requestResponse.put("response", response);
        
        // Work out the transactions results
        requestResponse.put("transactions", processTransactions(address, (JSONArray) response.get("txs")));

        // Also get the current block count
        path = new ApiPath(BlockchainData.Q_BLOCKCOUNT_URL);
        requestResponse.put("block_count", this.blockchain.rawRequest(path));
        
        requestResponse.put("page", page);
        
        return requestResponse;
    }
    
    @SuppressWarnings("unchecked")
    private JSONArray processTransactions(String address, JSONArray txs)
    {
        for (Object _tx : txs) {
            JSONObject tx = (JSONObject) _tx;

            Utils.ProcessedTransaction processed = Utils.processTransaction(address, (JSONObject) _tx);

            tx.put("result", processed.getAmount());
            tx.put("addr", processed.getAddress());
        }
        
        return txs;
    }
}
