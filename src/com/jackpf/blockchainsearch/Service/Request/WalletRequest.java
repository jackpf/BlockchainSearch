package com.jackpf.blockchainsearch.Service.Request;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;
import org.ocpsoft.prettytime.PrettyTime;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.ApiPath;
import com.jackpf.blockchainsearch.Entity.RequestResponse;
import com.jackpf.blockchainsearch.Model.RequestInterface;
import com.jackpf.blockchainsearch.Service.Utils;

public class WalletRequest extends RequestInterface
{
    public WalletRequest(Object ...params)
    {
        super(params);
    }
    
    @Override
    public RequestResponse call() throws ParseException, IOException
    {
        if (this.params.length < 1) {
            throw new InvalidParameterException("No wallet specified");
        }
        
        Map.Entry<String, ArrayList<String>> wallet = (Map.Entry<String, ArrayList<String>>) this.params[0];
        
        int page = 1;
        if (this.params.length == 2) {
            page = (Integer) this.params[1];
        }
        int offset = (page - 1) * BlockchainData.TX_PER_PAGE;
        
        RequestResponse requestResponse = new RequestResponse();
        
        String addresses = "";
        for (String address : wallet.getValue()) {
            addresses += address + "|";
        }
        
        ApiPath path = new ApiPath(BlockchainData.WALLET_URL, addresses, offset);
        JSONObject response = this.blockchain.request(path);
        requestResponse.put("response", transform(response));
        
        // Also get the current block count
        path = new ApiPath(BlockchainData.Q_BLOCKCOUNT_URL);
        requestResponse.put("block_count", this.blockchain.rawRequest(path));
        
        // Work out the transactions results
        requestResponse.put("txs", processTransactions(wallet.getValue(), (JSONArray) response.get("txs"), Float.parseFloat(requestResponse.get("block_count").toString())));

        // Let the ui know about stuff
        requestResponse.put("wallet", wallet);
        requestResponse.put("page", page);
        
        return requestResponse;
    }
    
    @SuppressWarnings("unchecked")
    private JSONObject transform(JSONObject response)
    {
        JSONObject wallet = (JSONObject) response.get("wallet");

        response.put("n_tx", wallet.get("n_tx"));
        response.put("total_received", wallet.get("total_received"));
        response.put("total_sent", wallet.get("total_sent"));
        response.put("final_balance", wallet.get("final_balance"));
        
        return response;
    }
    
    @SuppressWarnings("unchecked")
    private JSONArray processTransactions(ArrayList<String> addresses, JSONArray txs, Float blockCount)
    {
        for (Object _tx : txs) {
            JSONObject tx = (JSONObject) _tx;

            Long amount = 0L;
            String addr = null;
            int involvement = 0;
            for (String address : addresses) {
                Utils.ProcessedTransaction processed = Utils.processTransaction(address, tx);
    
                amount += processed.getAmount();
                if (processed.getAmount() != 0) {
                    addr = processed.getAddress();
                    involvement++;
                }
            }
            tx.put("result", involvement == 1 ? amount : Math.abs(amount));
            tx.put("addr", involvement == 1 ? addr : "Internal");
            
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
