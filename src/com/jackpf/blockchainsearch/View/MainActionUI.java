package com.jackpf.blockchainsearch.View;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.ocpsoft.prettytime.PrettyTime;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jackpf.blockchainsearch.AddressActivity;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Entity.BtcStats;
import com.jackpf.blockchainsearch.Entity.PersistedAddresses;
import com.jackpf.blockchainsearch.Interface.UIInterface;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

public class MainActionUI extends UIInterface
{
    private SherlockFragmentActivity activity;
    public SherlockActionBarDrawerToggle drawerToggle;
    Fragment[] fragments = {new SearchFragment(), new SavedAddressesFragment()};
    
    public MainActionUI(Context context)
    {
        super(context);
        
        activity = (SherlockFragmentActivity) context;
    }
    
    public void update() { }
    public void preUpdate() { }
    public void error(Exception e) { }
    
    public void initialise()
    {
        final DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        final ListView drawerList = (ListView) activity.findViewById(R.id.drawer);
        final FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

        drawerList.setAdapter(new android.widget.ArrayAdapter<String>(
            context,
            R.layout._drawer_list_item,
            context.getResources().getStringArray(R.array.drawer_list_titles)
        ));
        
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id)
            {
                drawerList.setItemChecked(position, true);
                fragmentManager.beginTransaction().replace(R.id.content, fragments[position]).commit();
                drawerLayout.closeDrawer(drawerList);
            }
        });
        
        drawerToggle = new SherlockActionBarDrawerToggle(
            activity,                   /* Host Activity */
            drawerLayout,          /* DrawerLayout object */
            R.drawable.ic_navigation_drawer, /* Nav drawer icon to replace 'Up' caret */
            R.string.drawer_open,   /* "Open drawer" description */
            R.string.drawer_close   /* "Close drawer" description */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
                activity.supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                activity.supportInvalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }
        };

        // Defer code dependent on restoration of previous instance state.
        // NB: required for the drawer indicator to show up!
        drawerLayout.post(new Runnable() {
            @Override
            public void run() {
                drawerToggle.syncState();
            }
        });

        drawerLayout.setDrawerListener(drawerToggle);
        
        fragmentManager.beginTransaction().replace(R.id.content, fragments[0]).commit();
        drawerList.setItemChecked(0, true);
    }
    
    protected static class SearchFragment extends Fragment
    {
        public SearchFragment()
        {
            super();
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            final View rootView = inflater.inflate(R.layout._main_search, container, false);
            
            BtcStats stats = BtcStats.getInstance();
            stats.update(new BtcStats.UpdateListener() {
               @Override
               public void update(BtcStats stats, IOException e) {
                   String[] values = {
                       Long.toString(stats.getBlockCount()),
                       stats.getNextBlockTime() >= 0 ? "in about " + (Math.abs(stats.getNextBlockTime()) / 60) + " minutes" : "about " + (Math.abs(stats.getNextBlockTime()) / 60) + " minutes ago",
                       Long.toString(stats.getDifficulty()),
                       Long.toString(stats.getTotalBitcoins())
                   };
                   int[] holders = {
                       R.id.stats_block_height,
                       R.id.stats_next_block_time,
                       R.id.stats_difficulty,
                       R.id.stats_total_bitcoins
                   };
                   int[] loaders = {
                       R.id.stats_block_height_loader,
                       R.id.stats_next_block_time_loader,
                       R.id.stats_difficulty_loader,
                       R.id.stats_total_bitcoins_loader
                   };
                   
                   for (int i = 0; i < values.length; i++) {
                       TextView tv = (TextView) rootView.findViewById(holders[i]);
                       ProgressBar pb = (ProgressBar) rootView.findViewById(loaders[i]);

                       pb.setVisibility(View.GONE);
                       tv.setText(values[i]);
                       tv.setVisibility(View.VISIBLE);
                   }
                   
                   ((TextView) rootView.findViewById(R.id.stats_updated_at)).setText("Updated " + new PrettyTime().format(new Date(stats.getUpdatedAt().getTime())));
               }
            });
            
            return rootView;
        }
    }
    
    protected static class SavedAddressesFragment extends Fragment
    {
        public SavedAddressesFragment()
        {
            super();
        }
        
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
        {
            PersistedAddresses persistedAddresses = new PersistedAddresses(getActivity());
            
            View rootView = inflater.inflate(R.layout._main_saved_addresses, container, false);
            
            // Saved addresses
            ListView addressesList = (ListView) rootView.findViewById(R.id._main_saved_addresses);
            
            ArrayList<Map.Entry<String, String>> al = new ArrayList<Map.Entry<String, String>>();
            for (Map.Entry<String, String> entry : persistedAddresses.getAll().entrySet()) {
                al.add(entry);
            }
            
            final ArrayAdapter<ArrayList<Map.Entry<String, String>>> adapter = new ArrayAdapter<ArrayList<Map.Entry<String, String>>>(getActivity(), al);
            addressesList.setAdapter(adapter);
            
            final Activity activity = getActivity();
            addressesList.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    Intent intent = new Intent(activity, AddressActivity.class);
                    intent.putExtra(AddressActivity.EXTRA_SEARCH, ((Map.Entry<String, String>) adapter.getItem(position)).getValue().toString());
                    activity.startActivity(intent);
                }
            });
            
            return rootView;
        }
        
        /**
         * Saved addresses ListView array adapter
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
}