package com.jackpf.blockchainsearch.View;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jackpf.blockchainsearch.AddressActivity;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.TransactionActivity;
import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.BtcStats;
import com.jackpf.blockchainsearch.Model.UIInterface;
import com.jackpf.blockchainsearch.Service.QRCode;
import com.jackpf.blockchainsearch.Service.Utils;

public class AddressActivityUI extends UIInterface
{
    private SherlockFragmentActivity activity;
    
    private View loadingView;
    
    private ViewPager viewPager;
    private TabsPagerAdapter tabAdapter;
    private ActionBar actionBar;
    Fragment[] tabs = {new OverviewFragment(), new TransactionsFragment()};
    private final String[] tabTitles = {"Overview", "Transactions"};
    
    private JSONArray transactions = new JSONArray();
    private ArrayAdapter<JSONArray> transactionsAdapter;
    
    public AddressActivityUI(Context context)
    {
        super(context);
        
        activity = (SherlockFragmentActivity) context;
    }
    
    public void initialise()
    {
        // Initilization
        viewPager = (ViewPager) activity.findViewById(R.id.content);
        actionBar = activity.getSupportActionBar();
        tabAdapter = new TabsPagerAdapter(((FragmentActivity) context).getSupportFragmentManager());

        viewPager.setAdapter(tabAdapter);
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            public void onPageSelected(int position) { actionBar.setSelectedNavigationItem(position); }
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            public void onPageScrollStateChanged(int arg0) {}
        });

        // Adding Tabs
        for (String tab : tabTitles) {
            actionBar.addTab(actionBar.newTab().setText(tab).setTabListener(new TabSwipeListener()));
        }
    }
    
    private void setLoading(boolean loading)
    {
        Button nextPageButton = (Button) activity.findViewById(R.id._address_transactions_next_page);
        if (nextPageButton != null) {
            nextPageButton.setEnabled(!loading);
            if (loading) {
                nextPageButton.setText(context.getString(R.string.text_loading));
            } else {
                nextPageButton.setText(context.getString(R.string.text_load_more));
            }
        }
    }
    
    public void preUpdate()
    {
        loadingView = activity.findViewById(R.id.loading);
        setLoading(true);
    }
    
    @SuppressWarnings("unchecked")
    public void update()
    {
        setLoading(false);
        loadingView.setVisibility(View.GONE);
        
        final JSONObject json = (JSONObject) vars.get("response");
        
        actionBar.setSubtitle(json.get("address").toString());
        
        // Overview fragment
        final View overviewFragment = activity.findViewById(R.id.content_overview);

        ((TextView) overviewFragment.findViewById(R.id._address_address)).setText(json.get("address").toString());
        ((TextView) overviewFragment.findViewById(R.id._address_final_balance)).setText(Utils.btcFormat((Long) json.get("final_balance"), context));
        ((TextView) overviewFragment.findViewById(R.id._address_total_received)).setText(Utils.btcFormat((Long) json.get("total_received"), context));
        ((TextView) overviewFragment.findViewById(R.id._address_total_sent)).setText(Utils.btcFormat((Long) json.get("total_sent"), context));
        ((TextView) overviewFragment.findViewById(R.id._address_no_transactions)).setText(json.get("n_tx").toString());
        
        ImageView qrCode = (ImageView) overviewFragment.findViewById(R.id._address_qr_code);
        try {
            qrCode.setImageDrawable(new BitmapDrawable(context.getResources(), QRCode.create(json.get("address").toString(), 256)));
        } catch (Exception e) {
            error(e);
        }
        
        // Currency conversion
        BtcStats stats = BtcStats.getInstance();
        stats.update(new BtcStats.UpdateListener() {
           @Override
           public void update(BtcStats stats, IOException e) {
               String currencyChoice = PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_currency_key), context.getString(R.string.pref_currency_default));
               if (!currencyChoice.equals("None")) {
                   double btc = Double.valueOf(json.get("final_balance").toString()) / BlockchainData.CONVERSIONS[0];
                   double converted = btc * Double.valueOf(stats.getExchangeValues().get(currencyChoice).get("last").toString());
                   String text = String.format(
                       Locale.getDefault(),
                       "%s (\u2248 %s%.2f)",
                       Utils.btcFormat((Long) json.get("final_balance"), context),
                       stats.getExchangeValues().get(currencyChoice).get("symbol").toString(),
                       converted
                   );
                   ((TextView) overviewFragment.findViewById(R.id._address_final_balance)).setText(text);
               }
           }
        });
        
        // Transactions fragment
        if ((Integer) vars.get("page") == 1) {
            transactions.clear();
        }
        transactions.addAll((JSONArray) vars.get("transactions"));
        
        ListView txList = (ListView) activity.findViewById(R.id.content_transactions);
        
        // Display the load more footer view?
        View footerView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout._address_transactions_footer, null, false);
        if ((Integer) vars.get("page") < Math.ceil(Double.valueOf(json.get("n_tx").toString()) / BlockchainData.TX_PER_PAGE) && txList.getFooterViewsCount() == 0) {
            txList.addFooterView(footerView);
        } else {
            txList.removeFooterView(footerView);
        }
        
        if (transactionsAdapter == null) {
            transactionsAdapter = new ArrayAdapter<JSONArray>(context, transactions);
            txList.setAdapter(transactionsAdapter);
            
            txList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String addr = ((JSONObject) transactionsAdapter.getItem(position)).get("addr").toString();
                    if (Utils.validAddress(addr)) {
                        Intent intent = new Intent(context, AddressActivity.class);
                        intent.putExtra(AddressActivity.EXTRA_SEARCH, addr);
                        context.startActivity(intent);
                    }
                }
            });
            
            txList.setOnItemLongClickListener(new OnItemLongClickListener() {
                @TargetApi(Build.VERSION_CODES.HONEYCOMB)
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    final String txHash = ((JSONObject) transactionsAdapter.getItem(position)).get("hash").toString();
                    
                    if (android.os.Build.VERSION.SDK_INT >= 11) {
                        PopupMenu menu = new PopupMenu(context, view);
                        activity.getMenuInflater().inflate(R.menu._address_transaction, menu.getMenu());
                        menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                            @Override
                            public boolean onMenuItemClick(android.view.MenuItem item) {
                                if (Utils.validTransaction(txHash)) {
                                    Intent intent = new Intent(context, TransactionActivity.class);
                                    intent.putExtra(TransactionActivity.EXTRA_SEARCH, txHash);
                                    context.startActivity(intent);
                                }
                                return true;
                            }
                        });
                        menu.show();
                    } else {
                        AlertDialog menu = new AlertDialog.Builder(context)
                        .setTitle("Menu")
                        .setSingleChoiceItems(new String[]{context.getString(R.string.action_transaction_view)}, 0, new android.content.DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int item) {
                                Intent intent = new Intent(context, TransactionActivity.class);
                                intent.putExtra(TransactionActivity.EXTRA_SEARCH, txHash);
                                context.startActivity(intent);
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
        
        activity.findViewById(R.id.content).setVisibility(View.VISIBLE);
    }
    
    public void error(Exception e)
    {
        e.printStackTrace();
        
        Toast.makeText(
            context.getApplicationContext(),
            context.getString(R.string.text_exception, e.getMessage()),
            Toast.LENGTH_SHORT
        ).show();
    }
    
    /**
     * Tab swipe listener
     * Sets the current tab when tabs are swept
     */
    protected class TabSwipeListener implements TabListener
    {
        public void onTabReselected(Tab arg0, FragmentTransaction tx) { }
        public void onTabUnselected(Tab arg0, FragmentTransaction tx) { }
        @Override
        public void onTabSelected(Tab tab, FragmentTransaction arg1)
        {
            viewPager.setCurrentItem(tab.getPosition());
        }
    }
    
    /**
     * Fragment adapter
     * Provides a fragment for a given index
     */
    protected class TabsPagerAdapter extends FragmentPagerAdapter
    {
        public TabsPagerAdapter(FragmentManager fm)
        {
            super(fm);
        }

        @Override
        public Fragment getItem(int index)
        {
            return tabs[index];
        }

        @Override
        public int getCount()
        {
            return tabs.length;
        }
    }
    
    /**
     * Overview fragment
     */
    protected static class OverviewFragment extends Fragment
    {
        public OverviewFragment()
        {
            super();
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout._address_overview, container, false);
        }
    }
    
    /**
     * Transactions fragment
     */
    protected static class TransactionsFragment extends Fragment
    {
        public TransactionsFragment()
        {
            super();
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            return inflater.inflate(R.layout._address_transactions, container, false);
        }
    }
    
    /**
     * Transactions ListView array adapter
     * 
     * @param <T>
     */
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
            Object bc = vars.get("block_count"), bh = tx.get("block_height");
            int confirmations = 0;
            int targetConfirmations = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(context).getString(context.getString(R.string.pref_confirmations_key), context.getString(R.string.pref_confirmations_default)));
            if (bh != null) {
                int blockCount = Integer.parseInt(bc.toString());
                int blockHeight = Integer.parseInt(bh.toString());
                confirmations = blockCount - blockHeight + 1;
            }
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