package com.jackpf.blockchainsearch;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.jackpf.blockchainsearch.Lib.IntentIntegrator;
import com.jackpf.blockchainsearch.Lib.IntentResult;
import com.jackpf.blockchainsearch.Service.Utils;
import com.jackpf.blockchainsearch.View.MainActivityUI;

public class MainActivity extends SherlockFragmentActivity
{
    private /*UIInterface*/ MainActivityUI ui;
    
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        ThemeManager.setTheme(this);
        
        super.onCreate(savedInstanceState);
        
        getSupportActionBar().setTitle(getString(R.string.activity_main_title));
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        
        setContentView(R.layout.activity_main);
        
        ui = new MainActivityUI(this);
    }
    
    @Override
    protected void onResume()
    {
        super.onResume();
        
        // Initialise on resume to keep the side tabs in sync
        ui.initialise();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getSupportMenuInflater().inflate(R.menu.main, menu);
        
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return MenuManager.openSettings(this);
            default:
                // Nav drawer
                if (ui.drawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
            break;
        }
        
        return super.onOptionsItemSelected(item);
    }
    
    /**
     * Process input and launch relevant activity
     * 
     * @param searchText
     */
    protected void processSearchText(String searchText)
    {
        if (Utils.validAddress(searchText)) {
            Intent intent = new Intent(this, AddressActivity.class);
            intent.putExtra(AddressActivity.EXTRA_SEARCH, searchText);
            startActivity(intent);
        } else if (Utils.validTransaction(searchText)) {
            Intent intent = new Intent(this, TransactionActivity.class);
            intent.putExtra(TransactionActivity.EXTRA_SEARCH, searchText);
            startActivity(intent);
        } else {
            Toast.makeText(
                getApplicationContext(),
                getString(R.string.text_invalid_input),
                Toast.LENGTH_SHORT
            ).show();
        }
    }
    
    /**
     * On search button click
     * 
     * @param v
     */
    public void onSubmit(View v)
    {
        TextView searchTextView = (TextView) findViewById(R.id.search);
        String searchText = searchTextView.getText().toString();
        
        processSearchText(searchText);
    }
    
    /**
     * On qr scan button click
     * 
     * @param v
     */
    public void onQrScan(View v)
    {
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.initiateScan();
    }
    
    /**
     * On qr scan result
     * 
     * @param requestCode
     * @param resultCode
     * @param intent
     */
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        
        if (scanResult.getContents() != null) {
            processSearchText(scanResult.getContents());
        } else {
            Toast.makeText(
                getApplicationContext(),
                getString(R.string.text_no_qrcode),
                Toast.LENGTH_SHORT
            ).show();
        }
    }
}
