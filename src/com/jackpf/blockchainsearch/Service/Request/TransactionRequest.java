package com.jackpf.blockchainsearch.Service.Request;

import java.io.IOException;
import java.security.InvalidParameterException;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.ApiPath;
import com.jackpf.blockchainsearch.Entity.RequestResponse;
import com.jackpf.blockchainsearch.Model.RequestInterface;

public class TransactionRequest extends RequestInterface
{
    public TransactionRequest(Object ...params)
    {
        super(params);
    }

    @Override
    public RequestResponse call() throws ParseException, IOException
    {
        if (this.params.length < 1) {
            throw new InvalidParameterException("No transaction specified");
        }
        
        ApiPath path = new ApiPath(BlockchainData.TRANSACTION_URL, (String) this.params[0]);
        JSONObject json = this.blockchain.request(path);
        
        RequestResponse response = new RequestResponse();
        
        response.put("response", json);
        
        // Also get the current block count
        path = new ApiPath(BlockchainData.Q_BLOCKCOUNT_URL);
        response.put("block_count", this.blockchain.rawRequest(path));
        
        return response;
    }
}
