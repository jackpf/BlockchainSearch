package com.jackpf.blockchainsearch.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.json.simple.JSONObject;

import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.BtcStats;
import com.jackpf.blockchainsearch.Service.QRCode;
import com.jackpf.blockchainsearch.Service.Utils;
import com.jackpf.blockchainsearch.View.AddressActivityUI.UpdatableFragment;

public class OverviewFragment extends UpdatableFragment
{
    private static Activity activity;
    
    private static View rootView;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        activity = getActivity();
        rootView = inflater.inflate(R.layout._address_overview, container, false);
        
        return rootView;
    }
    
    public void update(HashMap<String, Object> vars)
    {
        final JSONObject json = (JSONObject) vars.get("response");
        
        // Get address/wallet depending on what we're displaying
        String address = vars.get("address") != null ? vars.get("address").toString() : null;
        Map.Entry<String, ArrayList<String>> wallet = null;
        if (address == null) {
            wallet = (Map.Entry<String, ArrayList<String>>) vars.get("wallet");
        }
        
        // Get the display address(s)
        String displayAddress = "";
        if (address != null) {
            displayAddress = address;
        } else {
            displayAddress = wallet.getValue().get(0);
            for (int i = 1; i < wallet.getValue().size(); i++) {
                displayAddress += "\n" + wallet.getValue().get(i);
            }
        }
        ((TextView) rootView.findViewById(R.id._address_address)).setText(displayAddress);
        
        ((TextView) rootView.findViewById(R.id._address_final_balance)).setText(Utils.btcFormat((Long) json.get("final_balance"), activity));
        ((TextView) rootView.findViewById(R.id._address_total_received)).setText(Utils.btcFormat((Long) json.get("total_received"), activity));
        ((TextView) rootView.findViewById(R.id._address_total_sent)).setText(Utils.btcFormat((Long) json.get("total_sent"), activity));
        ((TextView) rootView.findViewById(R.id._address_no_transactions)).setText(json.get("n_tx").toString());
        
        // Only show qr code if we're on a single address
        if (address != null) {
            ImageView qrCode = (ImageView) rootView.findViewById(R.id._address_qr_code);
            qrCode.setImageDrawable(new BitmapDrawable(activity.getResources(), QRCode.create(address, 256)));
        }
        
        // Currency conversion
        BtcStats stats = BtcStats.getInstance();
        stats.update(new BtcStats.UpdateListener() {
           @Override
           public void update(BtcStats stats, IOException e) {
               String currencyChoice = PreferenceManager.getDefaultSharedPreferences(activity).getString(activity.getString(R.string.pref_currency_key), activity.getString(R.string.pref_currency_default));
               if (!currencyChoice.equals("None")) {
                   double btc = Double.valueOf(json.get("final_balance").toString()) / BlockchainData.CONVERSIONS[0];
                   double converted = btc * Double.valueOf(stats.getExchangeValues().get(currencyChoice).get("last").toString());
                   String text = String.format(
                       Locale.getDefault(),
                       "%s (\u2248 %s%.2f)",
                       Utils.btcFormat((Long) json.get("final_balance"), activity),
                       stats.getExchangeValues().get(currencyChoice).get("symbol").toString(),
                       converted
                   );
                   ((TextView) rootView.findViewById(R.id._address_final_balance)).setText(text);
               }
           }
        });
    }
}
