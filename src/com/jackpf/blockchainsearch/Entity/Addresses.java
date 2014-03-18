package com.jackpf.blockchainsearch.Entity;

import android.content.Context;

import com.jackpf.blockchainsearch.Model.PersistableInterface;

public class Addresses extends PersistableInterface<String>
{
    public Addresses(Context context)
    {
        super(context);
    }
    
    protected String getFilename()
    {
        return "addresses.dat";
    }
}
