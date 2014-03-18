package com.jackpf.blockchainsearch.View;

import java.util.HashMap;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;

import com.jackpf.blockchainsearch.AddressActivity;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.TransactionActivity;
import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Service.Utils;
import com.jackpf.blockchainsearch.View.AddressActivityUI.UpdatableFragment;

public class TransactionsFragment extends UpdatableFragment
{
    private JSONArray transactions = new JSONArray();
    private ArrayAdapter<JSONArray> transactionsAdapter;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout._address_transactions, container, false);
    }
    
    public void update(HashMap<String, Object> vars)
    {
        JSONObject json = (JSONObject) vars.get("response");
        if ((Integer) vars.get("page") == 1) {
            transactions.clear();
        }
        transactions.addAll((JSONArray) vars.get("transactions"));
        
        ListView txList = (ListView) getActivity().findViewById(R.id.content_transactions);
        
        // Display the load more footer view?
        View footerView = ((LayoutInflater) getActivity().getSystemService(getActivity().LAYOUT_INFLATER_SERVICE)).inflate(R.layout._address_transactions_footer, null, false);
        if ((Integer) vars.get("page") < Math.ceil(Double.valueOf(json.get("n_tx").toString()) / BlockchainData.TX_PER_PAGE) && txList.getFooterViewsCount() == 0) {
            txList.addFooterView(footerView);
        } else {
            txList.removeFooterView(footerView);
        }
        
        if (transactionsAdapter == null) {
            transactionsAdapter = new ArrayAdapter<JSONArray>(getActivity(), transactions);
            txList.setAdapter(transactionsAdapter);
            
            txList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String addr = ((JSONObject) transactionsAdapter.getItem(position)).get("addr").toString();
                    if (Utils.validAddress(addr)) {
                        Intent intent = new Intent(getActivity(), AddressActivity.class);
                        intent.putExtra(AddressActivity.EXTRA_SEARCH, addr);
                        getActivity().startActivity(intent);
                    }
                }
            });
            
            txList.setOnItemLongClickListener(new OnItemLongClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final String txHash = ((JSONObject) transactionsAdapter.getItem(position)).get("hash").toString();
                    
                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        PopupMenu menu = new PopupMenu(getActivity(), view);
                        getActivity().getMenuInflater().inflate(R.menu._address_transaction, menu.getMenu());
                        menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(android.view.MenuItem item) {
                                if (Utils.validTransaction(txHash)) {
                                    Intent intent = new Intent(getActivity(), TransactionActivity.class);
                                    intent.putExtra(TransactionActivity.EXTRA_SEARCH, txHash);
                                    getActivity().startActivity(intent);
                                }
                                return true;
                            }
                        });
                        menu.show();
                    } else {
                        AlertDialog menu = new AlertDialog.Builder(getActivity())
                        .setTitle("Menu")
                        .setSingleChoiceItems(new String[]{getActivity().getString(R.string.action_transaction_view)}, 0, new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Intent intent = new Intent(getActivity(), TransactionActivity.class);
                                intent.putExtra(TransactionActivity.EXTRA_SEARCH, txHash);
                                getActivity().startActivity(intent);
                                dialog.dismiss();
                            }
                        })
                        .setNeutralButton("Cancel", null) // don't need to do anything but dismiss here
                        .create();
                        menu.show();
                    }
                    
                    return true;
                }
            });
        } else {
            transactionsAdapter.notifyDataSetChanged();
        }
    }
    
    private class ArrayAdapter<T extends List<?>> extends BaseAdapter
    {
        private final Context context;
        private final T objects;
        private final LayoutInflater inflater;

        public ArrayAdapter(Context context, T objects)
        {
            this.context = context;
            this.objects = objects;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public Object getItem(int position)
        {
            return objects.get(position);
        }

        public long getItemId(int position)
        {
            return position;
        }

        public int getCount()
        {
            return objects.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent)
        {
            View row;

            if (convertView == null) {
                row = inflater.inflate(R.layout._address_transactions_item, parent, false);
            } else {
                row = convertView;
            }
            
            JSONObject tx = (JSONObject) getItem(position);
            
            // Address
            ((TextView) row.findViewById(R.id.hash)).setText(tx.get("addr").toString());
            
            //Date
            ((TextView) row.findViewById(R.id.date)).setText(tx.get("prettytime").toString());
            
            //Confirmations image
            int targetConfirmations = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_confirmations_key), context.getString(R.string.pref_confirmations_default)));
            int confirmations = Integer.parseInt(tx.get("confirmations").toString());
            ((ImageView) row.findViewById(R.id.confirmations))
                .setImageDrawable(
                    new BitmapDrawable(
                        context.getResources(),
                        Utils.drawConfirmationsArc(
                            confirmations,
                            targetConfirmations,
                            context.getResources().getColor(R.color.confirmations1),
                            context.getResources().getColor(R.color.confirmations2),
                            24
                        )
                    )
                );
            
            // Amount
            Object r = tx.get("result");
            long result = Long.parseLong(r.toString());
            TextView resultTextView = (TextView) row.findViewById(R.id.amount);
            resultTextView.setTextColor(context.getResources().getColor(result > 0 ? R.color.value_positive : R.color.value_negative));
            resultTextView.setText(Utils.btcFormat(result, context).replace("-", ""));

            return row;
        }
    }
}
