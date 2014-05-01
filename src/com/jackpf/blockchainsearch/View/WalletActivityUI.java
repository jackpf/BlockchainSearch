package com.jackpf.blockchainsearch.View;

import java.util.ArrayList;
import java.util.Map;

import android.content.Context;
import android.view.View;

import com.jackpf.blockchainsearch.R;

public class WalletActivityUI extends AddressActivityUI
{
    public WalletActivityUI(Context context)
    {
        super(context);
    }
    
    @Override
    public void update()
    {
        setLoading(false);
        
        Map.Entry<String, ArrayList<String>> wallet = (Map.Entry<String, ArrayList<String>>) vars.get("wallet");
        actionBar.setSubtitle(wallet.getKey());
        
        // Update fragments
        for (UpdatableFragment tab : tabs) {
            tab.update(vars);
        }
        
        activity.findViewById(R.id.content).setVisibility(View.VISIBLE);
    }
}
