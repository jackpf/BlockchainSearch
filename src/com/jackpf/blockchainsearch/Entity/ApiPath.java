package com.jackpf.blockchainsearch.Entity;

import java.net.URLEncoder;

import com.jackpf.blockchainsearch.Data.BlockchainData;

/**
 * ApiPath class
 */
public class ApiPath
{
    /**
     * Path
     */
    private String path;
    
    /**
     * Constructor
     * 
     * @param url
     * @param params
     */
    public ApiPath(String url, Object ...params)
    {
        for (int i = 0; i < params.length; i++) {
            if (params[i] instanceof String) {
                params[i] = URLEncoder.encode((String) params[i]);
            }
        }
        
        path = String.format(
            BlockchainData.BLOCKCHAIN_URL + "/" + url,
            params
        );
    }
    
    /**
     * Get path
     * 
     * @return
     */
    public String getPath()
    {
        return path;
    }
}
