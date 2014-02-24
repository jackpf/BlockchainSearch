package com.jackpf.blockchainsearch.View;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Interface.UIInterface;

public class MainActionUI extends UIInterface
{
	private Activity activity;
	
	public ActionBarDrawerToggle drawerToggle;
	
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
	}
}