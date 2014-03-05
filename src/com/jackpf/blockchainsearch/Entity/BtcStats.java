package com.jackpf.blockchainsearch.Entity;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.ParseException;

import android.os.AsyncTask;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Service.Blockchain;

public class BtcStats
{
    /**
     * Blockchain instance
     */
    private Blockchain blockchain;
    
    /**
     * Last time updated
     */
    private Timestamp updatedAt = new Timestamp(0L);
    
    /**
     * Stats
     */
    private Long blockCount;
    private Long difficulty;
    private Long totalBitcoins;
    private Long nextBlockTime;
    private HashMap<String, JSONObject> exchangeValues = new HashMap<String, JSONObject>();
    
    /**
     * Seconds until stats expire
     */
    private final static int EXPIRE_TIME = 600;
    
    /**
     * Singleton instance
     */
    private static BtcStats instance;
    
    /**
     * Constructor
     */
    private BtcStats()
    {
        blockchain = new Blockchain();
    }
    
    /**
     * Singleton instantiator
     * 
     * @return
     */
    public static BtcStats getInstance()
    {
        if (instance == null) {
            instance = new BtcStats();
        }
        
        return instance;
    }
    /**
     * Public update method
     * 
     * @param listener
     */
    public void update(final UpdateListener listener)
    {
        new UpdateThread(listener).execute();
    }
    
    /**
     * Public getters
     */
    public Long getBlockCount() {
        return blockCount;
    }
    public Long getDifficulty() {
        return difficulty;
    }
    public Long getTotalBitcoins() {
        return totalBitcoins;
    }
    public Long getNextBlockTime() {
        return nextBlockTime;
    }
    public HashMap<String, JSONObject> getExchangeValues() {
        return exchangeValues;
    }
    public Timestamp getUpdatedAt() {
        return updatedAt;
    }
    
    /**
     * Private updaters
     */
    private void updateBlockCount() throws IOException {
        ApiPath path = new ApiPath(BlockchainData.Q_BLOCKCOUNT_URL);
        blockCount = Long.parseLong(blockchain.rawRequest(path));
    }
    private void updateDifficulty() throws IOException {
        ApiPath path = new ApiPath(BlockchainData.Q_DIFFICULTY_URL);
        difficulty = Double.valueOf(blockchain.rawRequest(path)).longValue();
    }
    private void updateTotalBitcoins() throws IOException {
        ApiPath path = new ApiPath(BlockchainData.Q_TOTAL_BTC_URL);
        totalBitcoins = Long.parseLong(blockchain.rawRequest(path));
    }
    private void updateNextBlockTime() throws IOException {
        ApiPath path = new ApiPath(BlockchainData.Q_NEXT_BLOCK_TIME_URL);
        nextBlockTime = Double.valueOf(blockchain.rawRequest(path)).longValue();
    }
    private void updateExchangeValues() throws IOException {
        ApiPath path = new ApiPath(BlockchainData.Q_EXCHANGE_RATE_URL);
        try {
            JSONObject response = blockchain.request(path);
            Iterator<Map.Entry<String, JSONObject>> it = response.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, JSONObject> pairs = it.next();
                exchangeValues.put(pairs.getKey(), pairs.getValue());
            }
        } catch (ParseException e) { }
    }
    
    /**
     * Update task
     */
    private class UpdateThread extends AsyncTask<Void, Void, Void>
    {
        private UpdateListener listener;
        
        private IOException e = null;
        
        public UpdateThread(UpdateListener listener)
        {
            this.listener = listener;
        }
        
        @Override
        public Void doInBackground(Void ...params)
        {
            Long s = (new Timestamp(new Date().getTime()).getTime() - updatedAt.getTime()) / 1000;
            if (s < EXPIRE_TIME) {
                return null;
            }
            
            try {
                updateBlockCount();
                updateDifficulty();
                updateTotalBitcoins();
                updateNextBlockTime();
                updateExchangeValues();
                updatedAt = new Timestamp(new Date().getTime());
            } catch (IOException e) {
                this.e = e;
            }
            
            return null;
        }
        
        @Override
        public void onPostExecute(Void _void)
        {
            listener.update(BtcStats.this, e);
        }
    }
    
    /**
     * Update listener interface
     */
    public interface UpdateListener
    {
        public void update(BtcStats stats, IOException e);
    }
}
