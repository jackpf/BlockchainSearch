package com.jackpf.blockchainsearch.View;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
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
import com.actionbarsherlock.view.MenuItem;
import com.jackpf.blockchainsearch.AddressActivity;
import com.jackpf.blockchainsearch.Helpers;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Entity.Addresses;

public class SavedAddressesFragment extends SherlockFragment
{
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout._main_saved_addresses, container, false);
    }
    
    @Override
    public void onResume()
    {
        super.onResume();
        
        // Saved addresses
        final ListView addressesList = (ListView) getActivity().findViewById(R.id._main_saved_addresses);

        Addresses persistedAddresses = new Addresses(getActivity());
        ArrayList<Map.Entry<String, String>> al = new ArrayList<Map.Entry<String, String>>();
        for (Map.Entry<String, String> entry : persistedAddresses.getAll().entrySet()) {
            al.add(entry);
        }
        
        final ArrayAdapter<ArrayList<Map.Entry<String, String>>> adapter = new ArrayAdapter<ArrayList<Map.Entry<String, String>>>(getActivity(), al);
        addressesList.setAdapter(adapter);
        
        final SherlockFragmentActivity activity = getSherlockActivity();
        addressesList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                // Bit dodgy, but don't do anything if we're in select mode
                if (Helpers.mActionMode != null) {
                    return;
                }
                
                Intent intent = new Intent(activity, AddressActivity.class);
                intent.putExtra(AddressActivity.EXTRA_SEARCH, ((Map.Entry<String, String>) adapter.getItem(position)).getValue());
                activity.startActivity(intent);
            }
        });
        
        Helpers.addContextMenu(addressesList, R.menu._main_context_menu, new Helpers.ContextMenuCallback() {
            @Override
            public ActionMode startActionMode(ActionMode.Callback callback) {
                return activity.startActionMode(callback);
            }
            
            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                Map.Entry<String, String> address = (Map.Entry<String, String>) adapter.getItem(addressesList.getCheckedItemPosition());
                switch (item.getItemId()) {
                case R.id.action_edit:
                    Helpers.promptPersistAddress(getActivity(), address.getValue(), new Addresses(activity), null, address.getKey(), new Helpers.PromptCallback() {
                        public void callback() {
                            onResume(); // Rebuild the list
                        }
                    });
                    return true;
                case R.id.action_delete:
                    new Addresses(activity).removeByValue(address.getValue());
                    onResume(); // Rebuild the list
                    return true;
                }
                
                return false;
            }
        });
    }
    
    /**
     * Saved addresses ListView array adapter
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
                row = inflater.inflate(R.layout._main_saved_address_item, parent, false);
            } else {
                row = convertView;
            }
            
            Map.Entry<String, String> savedAddress = (Map.Entry<String, String>) getItem(position);

            ((TextView) row.findViewById(R.id.name)).setText(savedAddress.getKey());
            ((TextView) row.findViewById(R.id.address)).setText(savedAddress.getValue());

            return row;
        }
    }
}