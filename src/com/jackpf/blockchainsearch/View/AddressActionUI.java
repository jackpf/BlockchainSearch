package com.jackpf.blockchainsearch.View;

import java.util.Date;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.ocpsoft.prettytime.PrettyTime;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.jackpf.blockchainsearch.AddressActivity;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.TransactionActivity;
import com.jackpf.blockchainsearch.Entity.PersistedAddresses;
import com.jackpf.blockchainsearch.Interface.UIInterface;
import com.jackpf.blockchainsearch.Service.QRCode;
import com.jackpf.blockchainsearch.Service.Utils;

public class AddressActionUI extends UIInterface
{
    private Activity activity;
    
    private View loadingView;
    
    private ViewPager viewPager;
    private TabsPagerAdapter tabAdapter;
    private ActionBar actionBar;
    Fragment[] tabs = {new OverviewFragment(), new TransactionsFragment()};
    private final String[] tabTitles = {"Overview", "Transactions"};
    
    public AddressActionUI(Context context)
    {
        super(context);
        
        activity = (Activity) context;
    }
    
    public void initialise()
    {
        // Initilization
        viewPager = (ViewPager) activity.findViewById(R.id.content);
        actionBar = activity.getActionBar();
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
            actionBar.addTab(actionBar.newTab().setText(tab).setTabListener(new TabListener()));
        }
    }
    
    public void preUpdate()
    {
        loadingView = activity.findViewById(R.id.loading);
    }
    
    public void update()
    {
        loadingView.setVisibility(View.GONE);
        
        final JSONObject json = (JSONObject) vars.get("response");
        
        actionBar.setSubtitle(json.get("address").toString());
        
        // Overview fragment
        View overviewFragment = activity.findViewById(R.id.content_overview);

        ((TextView) overviewFragment.findViewById(R.id._address_address)).setText(json.get("address").toString());
        ((TextView) overviewFragment.findViewById(R.id._address_final_balance)).setText(Utils.btcFormat((Long) json.get("final_balance")));
        ((TextView) overviewFragment.findViewById(R.id._address_total_received)).setText(Utils.btcFormat((Long) json.get("total_received")));
        ((TextView) overviewFragment.findViewById(R.id._address_total_sent)).setText(Utils.btcFormat((Long) json.get("total_sent")));
        ((TextView) overviewFragment.findViewById(R.id._address_no_transactions)).setText(json.get("n_tx").toString());
        
        ImageView qrCode = (ImageView) overviewFragment.findViewById(R.id._address_qr_code);
        try {
            qrCode.setImageDrawable(new BitmapDrawable(context.getResources(), QRCode.create(json.get("address").toString(), 256)));
        } catch (Exception e) {
            error(e);
        }
        
        // Transactions fragment
        ListView txList = (ListView) activity.findViewById(R.id.content_transactions);
        
        final ArrayAdapter<JSONArray> adapter = new ArrayAdapter<JSONArray>(context, (JSONArray) vars.get("transactions"));
        txList.setAdapter(adapter);
        txList.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(context, AddressActivity.class);
                intent.putExtra(AddressActivity.EXTRA_SEARCH, ((JSONObject) adapter.getItem(position)).get("addr").toString());
                context.startActivity(intent);
            }
        });
        txList.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final String txHash = ((JSONObject) adapter.getItem(position)).get("hash").toString();
                
                PopupMenu menu = new PopupMenu(context, view);
                activity.getMenuInflater().inflate(R.menu._address_transaction, menu.getMenu());
                menu.setOnMenuItemClickListener(new OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item)
                    {
                        Intent intent = new Intent(context, TransactionActivity.class);
                        intent.putExtra(TransactionActivity.EXTRA_SEARCH, txHash);
                        context.startActivity(intent);
                        return true;
                    }
                });
                menu.show();
                
                return true;
            }
        });
        
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
    
    public void promptPersistAddress(final String address, final PersistedAddresses addresses, final MenuItem saveMenuItem)
    {
        final EditText input = new EditText(context);
        input.setSingleLine();
        
        new AlertDialog.Builder(context)
            .setTitle("Enter a name for this address")
            .setView(input)
            .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button)
                {
                    String name = input.getText().toString();
                    
                    if (name.equals("")) {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_address_empty_name), Toast.LENGTH_SHORT).show();
                    } else if (addresses.hasName(name)) {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_address_name_exists), Toast.LENGTH_SHORT).show();
                    } else {
                        addresses.add(name, address);
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_address_saved), Toast.LENGTH_SHORT).show();
                        saveMenuItem.setIcon(R.drawable.ic_menu_save_tinted);
                    }
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button) { }
            })
            .show();
    }
    
    public void promptRemoveAddress(final String address, final PersistedAddresses addresses, final MenuItem saveMenuItem)
    {
        new AlertDialog.Builder(context)
            .setTitle("Do you want to unsave this address?")
            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button)
                {
                    addresses.remove(address);
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_address_unsaved), Toast.LENGTH_SHORT).show();
                    saveMenuItem.setIcon(R.drawable.ic_menu_save);
                }
            })
            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button) { }
            })
            .show();
    }
    
    /**
     * Tab swipe listener
     * Sets the current tab when tabs are swept
     */
    protected class TabListener implements android.app.ActionBar.TabListener
    {
        public void onTabReselected(Tab arg0, android.app.FragmentTransaction tx) { }
        public void onTabUnselected(Tab arg0, android.app.FragmentTransaction tx) { }
        @Override
        public void onTabSelected(Tab tab, android.app.FragmentTransaction arg1)
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
    private class ArrayAdapter<T extends List> extends BaseAdapter
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
            Object time = tx.get("time");
            if (time != null) {
                ((TextView) row.findViewById(R.id.date)).setText(new PrettyTime().format(new Date(Long.parseLong(time.toString()) * 1000L)));
            }
            
            //Confirmations image
            Object bc = vars.get("block_count"), bh = tx.get("block_height");
            int confirmations = 0;
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
                            3,
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
            resultTextView.setTextColor(result > 0 ? Color.GREEN : Color.RED);
            resultTextView.setText(Utils.btcFormat(result).replace("-", ""));

            return row;
        }
    }
}