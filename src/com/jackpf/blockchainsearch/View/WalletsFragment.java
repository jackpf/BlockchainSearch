package com.jackpf.blockchainsearch.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jackpf.blockchainsearch.Helpers;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Entity.Wallets;

public class WalletsFragment extends SherlockFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // For our action bar menu icon
        setHasOptionsMenu(true);
        
        return inflater.inflate(R.layout._main_wallets, container, false);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        
        // Wallets
        final ListView walletsList = (ListView) getActivity().findViewById(R.id._main_wallets);

        Wallets wallets = new Wallets(getActivity());
        ArrayList<Map.Entry<String, ArrayList<String>>> al = new ArrayList<Map.Entry<String, ArrayList<String>>>();
        for (Map.Entry<String, ArrayList<String>> entry : wallets.getAll().entrySet()) {
            al.add(entry);
        }
        
        final ArrayAdapter<ArrayList<Map.Entry<String, ArrayList<String>>>> adapter = new ArrayAdapter<ArrayList<Map.Entry<String, ArrayList<String>>>>(getActivity(), al);
        walletsList.setAdapter(adapter);
        
        final SherlockFragmentActivity activity = getSherlockActivity();
        walletsList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Bit dodgy, but don't do anything if we're in select mode
                if (Helpers.mActionMode != null) {
                    return;
                }
                
                // TODO
                //Intent intent = new Intent(activity, AddressActivity.class);
                //intent.putExtra(AddressActivity.EXTRA_SEARCH, ((Map.Entry<String, ArrayList<String>>) adapter.getItem(position)).getValue().toString());
                //activity.startActivity(intent);
            }
        });
        
        Helpers.addContextMenu(walletsList, R.menu._main_context_menu, new Helpers.ContextMenuCallback() {
            @Override
            public ActionMode startActionMode(ActionMode.Callback callback) {
                return activity.startActionMode(callback);
            }
            
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Map.Entry<String, ArrayList<String>> wallet = (Map.Entry<String, ArrayList<String>>) adapter.getItem(walletsList.getCheckedItemPosition());
                switch (item.getItemId()) {
                    case R.id.action_edit:
                        // TODO
                        //Helpers.promptPersistAddress(getActivity(), address.getValue(), new Addresses(activity), null, address.getKey(), new Helpers.PromptCallback() {
                        //    public void callback() {
                        //        onResume(); // Rebuild the list
                        //    }
                        //});
                        return true;
                    case R.id.action_delete:
                        // TODO
                        new Wallets(activity).removeByKey(wallet.getKey());
                        onResume(); // Rebuild the list
                        return true;
                }
                
                return false;
            }
        });
    }
    
    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater)
    {
        inflater.inflate(R.menu._main_wallets, menu);
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_add:
                Helpers.promptCreateWallet(getActivity(), new Wallets(getActivity()), new Helpers.PromptCallback() {
                    public void callback() {
                        onResume(); // Rebuild the list
                    }
                });
                return true;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Wallets ListView array adapter
     * 
     * @param <T>
     */
    private class ArrayAdapter<T extends List<?>> extends BaseAdapter
    {
        private final T objects;
        private final LayoutInflater inflater;

        public ArrayAdapter(Context context, T objects) {
            this.objects = objects;

            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public Object getItem(int position) {
            return objects.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public int getCount() {
            return objects.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row;

            if (convertView == null) {
                row = inflater.inflate(R.layout._main_wallet_item, parent, false);
            } else {
                row = convertView;
            }
            
            Map.Entry<String, ArrayList<String>> wallet = (Map.Entry<String, ArrayList<String>>) getItem(position);

            ((TextView) row.findViewById(R.id.name)).setText(wallet.getKey());
            
            String addresses = "";
            for (String address : wallet.getValue()) {
                addresses += (!addresses.equals("") ? "\n" : "") + address;
            }
            ((TextView) row.findViewById(R.id.addresses)).setText(addresses);

            return row;
        }
    }
}