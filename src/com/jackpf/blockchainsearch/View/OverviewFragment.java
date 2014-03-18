package com.jackpf.blockchainsearch.View;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;

import org.json.simple.JSONObject;

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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout._address_overview, container, false);
    }
    
    public void update(HashMap<String, Object> vars)
    {
        final JSONObject json = (JSONObject) vars.get("response");
        
        ((TextView) getActivity().findViewById(R.id._address_address)).setText(json.get("address").toString());
        ((TextView) getActivity().findViewById(R.id._address_final_balance)).setText(Utils.btcFormat((Long) json.get("final_balance"), getActivity()));
        ((TextView) getActivity().findViewById(R.id._address_total_received)).setText(Utils.btcFormat((Long) json.get("total_received"), getActivity()));
        ((TextView) getActivity().findViewById(R.id._address_total_sent)).setText(Utils.btcFormat((Long) json.get("total_sent"), getActivity()));
        ((TextView) getActivity().findViewById(R.id._address_no_transactions)).setText(json.get("n_tx").toString());
        
        ImageView qrCode = (ImageView) getActivity().findViewById(R.id._address_qr_code);
        qrCode.setImageDrawable(new BitmapDrawable(getActivity().getResources(), QRCode.create(json.get("address").toString(), 256)));
        
        // Currency conversion
        BtcStats stats = BtcStats.getInstance();
        stats.update(new BtcStats.UpdateListener() {
           @Override
           public void update(BtcStats stats, IOException e) {
               String currencyChoice = PreferenceManager.getDefaultSharedPreferences(getActivity()).getString(getActivity().getString(R.string.pref_currency_key), getActivity().getString(R.string.pref_currency_default));
               if (!currencyChoice.equals("None")) {
                   double btc = Double.valueOf(json.get("final_balance").toString()) / BlockchainData.CONVERSIONS[0];
                   double converted = btc * Double.valueOf(stats.getExchangeValues().get(currencyChoice).get("last").toString());
                   String text = String.format(
                       Locale.getDefault(),
                       "%s (\u2248 %s%.2f)",
                       Utils.btcFormat((Long) json.get("final_balance"), getActivity()),
                       stats.getExchangeValues().get(currencyChoice).get("symbol").toString(),
                       converted
                   );
                   ((TextView) getActivity().findViewById(R.id._address_final_balance)).setText(text);
               }
           }
        });
    }
}
