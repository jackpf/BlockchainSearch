package com.jackpf.blockchainsearch.View;

import org.joda.time.DateTime;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.jackpf.blockchainsearch.AddressActivity;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.TransactionActivity;
import com.jackpf.blockchainsearch.Model.UIInterface;
import com.jackpf.blockchainsearch.Service.Utils;

public class TransactionActivityUI extends UIInterface
{
    private SherlockActivity activity;
    
    private View loadingView;
    
    public TransactionActivityUI(Context context)
    {
        super(context);
        
        activity = (SherlockActivity) context;
    }
    
    public void initialise()
    {
        
    }
    
    public void preUpdate()
    {
        loadingView = activity.findViewById(R.id.loading);
    }
    
    public void update()
    {
        loadingView.setVisibility(View.GONE);
        
        final JSONObject json = (JSONObject) vars.get("response");
        LayoutInflater inflater = activity.getLayoutInflater();
        
        activity.getSupportActionBar().setSubtitle(json.get("hash").toString());
        
        View transactionView = activity.findViewById(R.id._transaction_table);

        ((TextView) transactionView.findViewById(R.id._transaction_hash)).setText(json.get("hash").toString());
        ((TextView) transactionView.findViewById(R.id._transaction_index)).setText(json.get("tx_index").toString());
        ((TextView) transactionView.findViewById(R.id._transaction_size)).setText(json.get("size").toString() + " bytes");
        ((TextView) transactionView.findViewById(R.id._transaction_double_spend)).setText(json.get("double_spend").toString());
        Object bh = json.get("block_height");
        int blockHeight = 0, blockCount = Integer.parseInt(vars.get("block_count").toString());
        if (bh != null) {
            blockHeight = Integer.parseInt(bh.toString());
        } else {
            blockHeight = blockCount + 1;
        }
        ((TextView) transactionView.findViewById(R.id._transaction_block_height)).setText(Integer.toString(blockHeight));
        ((TextView) transactionView.findViewById(R.id._transaction_confirmations)).setText(Integer.toString(blockCount - blockHeight + 1));
        
        Object t = json.get("time");
        if (t != null) {
            DateTime dt = new DateTime((Long) t * 1000L);
            ((TextView) transactionView.findViewById(R.id._transaction_date)).setText(dt.toString("dd-MM-yyyy h:m:s"));
        }
        
        ((TextView) transactionView.findViewById(R.id._transaction_relayed_by)).setText(json.get("relayed_by").toString());
        
        String f = "<font color=\"blue\"><u>%s</u></font>: %s";
        
        LinearLayout inputView = (LinearLayout) transactionView.findViewById(R.id._transaction_inputs);
        inputView.removeAllViews();
        for (Object _in : (JSONArray) json.get("inputs")) {
            JSONObject in = (JSONObject) _in;
            JSONObject prev = (JSONObject) in.get("prev_out");

            final String address = prev != null ? prev.get("addr").toString() : "No inputs (new coins)";
            TextView tv = (TextView) inflater.inflate(R.layout._transaction_io, null);
            tv.setText(Html.fromHtml(String.format(f, prev.get("addr").toString(), Utils.btcFormat((Long) prev.get("value"), context))));
            inputView.addView(tv);
            tv.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddressActivity.class);
                    intent.putExtra(TransactionActivity.EXTRA_SEARCH, address);
                    context.startActivity(intent);
                }
            });
        }
        
        LinearLayout outputView = (LinearLayout) transactionView.findViewById(R.id._transaction_outputs);
        outputView.removeAllViews();
        for (Object _out : (JSONArray) json.get("out")) {
            JSONObject out = (JSONObject) _out;
            
            final String address = out.get("addr").toString();
            TextView tv = (TextView) inflater.inflate(R.layout._transaction_io, null);
            tv.setText(Html.fromHtml(String.format(f, address, Utils.btcFormat((Long) out.get("value"), context))));
            outputView.addView(tv);
            tv.setOnClickListener(new OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(context, AddressActivity.class);
                    intent.putExtra(TransactionActivity.EXTRA_SEARCH, address);
                    context.startActivity(intent);
                }
            });
        }
        
        activity.findViewById(R.id.content).setVisibility(View.VISIBLE);
    }
    
    public void error(Exception e)
    {
        e.printStackTrace();
    }
}