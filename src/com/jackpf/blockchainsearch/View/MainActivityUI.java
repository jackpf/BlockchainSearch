package com.jackpf.blockchainsearch.View;

import java.util.Arrays;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Model.UIInterface;
import com.sherlock.navigationdrawer.compat.SherlockActionBarDrawerToggle;

public class MainActivityUI extends UIInterface
{
    private SherlockFragmentActivity activity;
    private FragmentManager fragmentManager;
    public SherlockActionBarDrawerToggle drawerToggle;
    private ListView drawerList;
    private static int lastFragment = 0;
    Fragment[] fragments = {new SearchFragment(), new SavedAddressesFragment(), new WalletsFragment()};
    
    public MainActivityUI(Context context)
    {
        super(context);
        
        activity = (SherlockFragmentActivity) context;
    }
    
    public void update() { }
    public void preUpdate() { }
    public void error(Exception e) { }
    
    public void initialise()
    {
        if (fragmentManager == null) {
            fragmentManager = ((FragmentActivity) context).getSupportFragmentManager();
            drawerList = (ListView) activity.findViewById(R.id.drawer);
            final DrawerLayout drawerLayout = (DrawerLayout) activity.findViewById(R.id.drawer_layout);
    
            drawerList.setAdapter(new android.widget.ArrayAdapter<String>(
                context,
                R.layout._drawer_list_item,
                context.getResources().getStringArray(R.array.drawer_list_titles)
            ));
            
            drawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    drawerList.setItemChecked(position, true);
                    fragmentManager.beginTransaction().replace(R.id.content, fragments[position], fragments[position].getClass().getName()).commit();
                    lastFragment = position;
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
        }

        fragmentManager.beginTransaction().replace(R.id.content, fragments[lastFragment]).commit();
        drawerList.setItemChecked(lastFragment, true);
    }
}