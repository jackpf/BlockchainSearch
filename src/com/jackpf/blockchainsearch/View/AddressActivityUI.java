package com.jackpf.blockchainsearch.View;

import java.util.HashMap;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
    protected SherlockFragmentActivity activity;
    
    protected ViewPager viewPager;
    protected TabsPagerAdapter tabAdapter;
    protected ActionBar actionBar;
    protected UpdatableFragment[] tabs = {new OverviewFragment(), new TransactionsFragment()};
    protected final String[] tabTitles = {"Overview", "Transactions"};
    
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
    
    protected void setLoading(boolean loading)
    {
        // Load more button
        Button nextPageButton = (Button) activity.findViewById(R.id._address_transactions_next_page);
        if (nextPageButton != null) {
            nextPageButton.setEnabled(!loading);
            if (loading) {
                nextPageButton.setText(context.getString(R.string.text_loading));
            } else {
                nextPageButton.setText(context.getString(R.string.text_load_more));
            }
        }
        
        // Hide loading spinner
        if (!loading) {
            activity.findViewById(R.id.loading).setVisibility(View.GONE);
        }
        
        // Refresh spinner
        View refreshView = activity.findViewById(R.id.action_refresh);
        if (refreshView != null) { // Options menu not created yet on the initial load, so this will only trigger on refresh
            if (loading) {
                Animation rotation = AnimationUtils.loadAnimation(context, R.anim.rotate);
                rotation.setRepeatCount(Animation.INFINITE);
                refreshView.startAnimation(rotation);
            } else {
                refreshView.clearAnimation();
            }
        }
    }
    
    public void preUpdate()
    {
        setLoading(true);
    }
    
    public void update()
    {
        setLoading(false);
        
        actionBar.setSubtitle(vars.get("address").toString());
        
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
        public void onTabSelected(Tab tab, FragmentTransaction arg1) {
            viewPager.setCurrentItem(tab.getPosition());
        }
    }
    
    /**
     * Fragment adapter
     * Provides a fragment for a given index
     */
    protected class TabsPagerAdapter extends FragmentPagerAdapter
    {
        public TabsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int index) {
            return tabs[index];
        }

        @Override
        public int getCount() {
            return tabs.length;
        }
    }
    
    public static abstract class UpdatableFragment extends Fragment
    {
        public abstract void update(HashMap<String, Object> vars);
    }
}