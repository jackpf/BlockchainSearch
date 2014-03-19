package com.jackpf.blockchainsearch.Entity;

import java.util.ArrayList;

import android.content.Context;

import com.jackpf.blockchainsearch.Model.PersistableInterface;

public class Wallets extends PersistableInterface<ArrayList<String>>
{
    public Wallets(Context context)
    {
        super(context);
    }
    
    protected String getFilename()
    {
        return "wallets.dat";
    }
}
