package com.jackpf.blockchainsearch.View;

import java.util.HashMap;

import org.json.simple.JSONObject;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.Tab;
import com.actionbarsherlock.app.ActionBar.TabListener;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.jackpf.blockchainsearch.R;
import com.jackpf.blockchainsearch.Model.UIInterface;

public class AddressActivityUI extends UIInterface
{
    private SherlockFragmentActivity activity;
    
    private View loadingView;
    
    private ViewPager viewPager;
    private TabsPagerAdapter tabAdapter;
    private ActionBar actionBar;
    UpdatableFragment[] tabs = {new OverviewFragment(), new TransactionsFragment()};
    private final String[] tabTitles = {"Overview", "Transactions"};
    
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
    
    public void update()
    {
        setLoading(false);
        loadingView.setVisibility(View.GONE);
        
        final JSONObject json = (JSONObject) vars.get("response");
        
        actionBar.setSubtitle(json.get("address").toString());
        
        // Update fragments
        for (UpdatableFragment tab : tabs) {
            tab.update(vars);
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
    
    public static abstract class UpdatableFragment extends Fragment
    {
        public abstract void update(HashMap<String, Object> vars);
    }
}