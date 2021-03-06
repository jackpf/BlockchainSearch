package com.jackpf.blockchainsearch.Model;

import java.io.IOException;

import org.json.simple.parser.ParseException;

import com.jackpf.blockchainsearch.Entity.RequestResponse;
import com.jackpf.blockchainsearch.Service.Blockchain;

/**
 * Request interface
 */
public abstract class RequestInterface
{
    /**
     * Api call params
     */
    protected Object[] params;
    
    /**
     * Blockchain api service instance
     */
    protected Blockchain blockchain = new Blockchain();
    
    /**
     * Constructor
     * 
     * @param params
     */
    public RequestInterface(Object ...params)
    {
        this.params = params;
    }
    
    /**
     * Perform api call
     * 
     * @return
     * @throws Exception
     */
    public abstract RequestResponse call() throws ParseException, IOException;
}