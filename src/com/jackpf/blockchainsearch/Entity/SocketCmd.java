package com.jackpf.blockchainsearch.Entity;

import org.json.simple.JSONObject;

public class SocketCmd
{
    /**
     * Commands
     */
    public final static String
        ADDR_SUB        = "addr_sub",
        UNCONFIRMED_SUB = "unconfirmed_sub"
    ;
    
    /**
     * Command
     */
    private JSONObject cmd = new JSONObject();
    
    /**
     * Constructor
     * 
     * @param cmd
     */
    public SocketCmd(String cmd)
    {
        setParam("op", cmd);
    }
    
    /**
     * Set command parameter
     * 
     * @param key
     * @param value
     */
    @SuppressWarnings("unchecked")
    public void setParam(String key, String value)
    {
        this.cmd.put(key, value);
    }
    
    /**
     * Get command as a string
     */
    public String toString()
    {
        return cmd.toString();
    }
}
