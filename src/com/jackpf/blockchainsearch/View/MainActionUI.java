package com.jackpf.blockchainsearch.View;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;

import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Interface.UIInterface;

public class MainActionUI extends UIInterface
{
	private Activity activity;
	
	public ActionBarDrawerToggle drawerToggle;
	
	Fragment[] fragments = {new SearchFragment(), new SavedAddressesFragment()};
	
	public MainActionUI(Context context)
	{
		super(context);
		
		activity = (Activity) context;
	}
	
	public void update() { }
	public void preUpdate() { }
	public void error(Exception e) { }
	
	public void initialise()
	{
		final DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
        final ListView drawerList = (ListView) activity.findViewById(R.id.drawer);
        final FragmentManager fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();

        drawerList.setAdapter(new ArrayAdapter<String>(
    		context,
            R.layout._drawer_list_item,
            context.getResources().getStringArray(R.array.drawer_list_titles)
        ));
        
        drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View view, int position, long id)
            {
                drawerList.setItemChecked(position, true);
                fragmentManager.beginTransaction().replace(R.id.content, fragments[position]).commit();
                drawerLayout.closeDrawer(drawerList);
            }
	    });
        
        drawerToggle = new ActionBarDrawerToggle(
        	activity,                   /* Host Activity */
            drawerLayout,          /* DrawerLayout object */
            R.drawable.ic_navigation_drawer, /* Nav drawer icon to replace 'Up' caret */
            R.string.drawer_open,   /* "Open drawer" description */
            R.string.drawer_close   /* "Close drawer" description */
        ) {
            @Override
            public void onDrawerClosed(View drawerView) {
            	activity.invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                activity.invalidateOptionsMenu(); // calls onPrepareOptionsMenu()
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
            View rootView = inflater.inflate(R.layout._main_search, container, false);
            
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
            View rootView = inflater.inflate(R.layout._main_saved_addresses, container, false);
            
            // Saved addresses
            
            return rootView;
        }
    }
}