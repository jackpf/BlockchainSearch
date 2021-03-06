package com.jackpf.blockchainsearch.Service.Request;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.Date;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.ocpsoft.prettytime.PrettyTime;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.ApiPath;
import com.jackpf.blockchainsearch.Entity.RequestResponse;
import com.jackpf.blockchainsearch.Model.RequestInterface;
import com.jackpf.blockchainsearch.Service.Utils;

public class AddressRequest extends RequestInterface
{
    public AddressRequest(Object ...params)
    {
        super(params);
    }

    @Override
    public RequestResponse call() throws ParseException, IOException
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
        
        // Get address
        ApiPath path = new ApiPath(BlockchainData.ADDRESS_URL, address, offset);
        JSONObject response = this.blockchain.request(path);
        requestResponse.put("response", response);
        
        // Also get the current block count
        path = new ApiPath(BlockchainData.Q_BLOCKCOUNT_URL);
        requestResponse.put("block_count", this.blockchain.rawRequest(path));
        
        // Work out the transactions results
        requestResponse.put("txs", processTransactions(address, (JSONArray) response.get("txs"), Float.parseFloat(requestResponse.get("block_count").toString())));

        // Let the ui know about stuff
        requestResponse.put("address", address);
        requestResponse.put("page", page);
        
        return requestResponse;
    }
    
    @SuppressWarnings("unchecked")
    private JSONArray processTransactions(String address, JSONArray txs, Float blockCount)
    {
        for (Object _tx : txs) {
            JSONObject tx = (JSONObject) _tx;

            Utils.ProcessedTransaction processed = Utils.processTransaction(address, tx);

            tx.put("result", processed.getAmount());
            tx.put("addr", processed.getAddress());
            
            // Process pretty time here since doing it on the ui thread makes it lag like shit
            Object time = tx.get("time");
            String prettyTime;
            if (time != null) {
                prettyTime = new PrettyTime().format(new Date(Long.parseLong(time.toString()) * 1000L));
            } else {
                prettyTime = "";
            }
            tx.put("prettytime", prettyTime);
            
            // Calculate confirmations
            Object bh = tx.get("block_height");
            int confirmations = 0;
            if (bh != null) {
                float blockHeight = Float.parseFloat(bh.toString());
                confirmations = (int) (blockCount - blockHeight + 1);
            }
            tx.put("confirmations", confirmations);
        }
        
        return txs;
    }
}
