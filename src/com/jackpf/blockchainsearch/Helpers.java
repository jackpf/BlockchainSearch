package com.jackpf.blockchainsearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.actionbarsherlock.view.ActionMode;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.jackpf.blockchainsearch.Entity.Addresses;
import com.jackpf.blockchainsearch.Entity.Wallets;

public class Helpers
{
    /**
     * Context menu action mode
     */
    public static ActionMode mActionMode;
    
    /**
     * Add a long click triggered context menu to a listview
     * 
     * @param list
     * @param m
     * @param callback
     */
    public static void addContextMenu(final ListView list, final int m, final ContextMenuCallback callback)
    {
        final ActionMode.Callback mActionModeCallback = new ActionMode.Callback()
        {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                MenuInflater inflater = mode.getMenuInflater();
                inflater.inflate(m, menu);
                return true;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false; // Return false if nothing is done
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                if (callback.onActionItemClicked(mode, item)) {
                    mode.finish();
                }
                
                return true;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {
                mActionMode = null;
                list.setItemChecked(-1, true);
            }
        };
        
        list.setOnItemLongClickListener(new OnItemLongClickListener() {
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                if (mActionMode != null) {
                    return false;
                }

                mActionMode = callback.startActionMode(mActionModeCallback);
                list.setItemChecked(position, true);
                return true;
            }
        });
    }
    
    /**
     * Context menu callback interface
     */
    public interface ContextMenuCallback
    {
        public ActionMode startActionMode(ActionMode.Callback callback);
        public boolean onActionItemClicked(ActionMode mode, MenuItem item);
    }
    
    /**
     * Prompt callback interface
     */
    public interface PromptCallback
    {
        public void callback();
    }
    
    /**
     * Prompt to save an address
     * Edits an address if passed name exists
     * 
     * @param context
     * @param address
     * @param addresses
     * @param saveMenuItem
     * @param name
     * @param callback
     */
    public static void promptPersistAddress(final Context context, final String address, final Addresses addresses, final MenuItem saveMenuItem, String name, final PromptCallback callback)
    {
        final EditText input = new EditText(context);
        input.setSingleLine();
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setText(name);
        
        final Map.Entry<String, String> existing = addresses.get(name);
        
        new AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.text_address_save))
            .setView(input)
            .setPositiveButton(context.getString(R.string.action_save), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button) {
                    String name = input.getText().toString();
                    
                    if (!address.equals(name)) {
                        if (name.equals("")) {
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_empty_name), Toast.LENGTH_SHORT).show();
                        } else if (addresses.hasKey(name)) {
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_name_exists), Toast.LENGTH_SHORT).show();
                        } else {
                            if (existing != null) {
                                addresses.removeByValue(existing.getValue());
                            }
                            addresses.add(name, address);
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_address_saved), Toast.LENGTH_SHORT).show();
                            
                            if (saveMenuItem != null) {
                                saveMenuItem.setIcon(android.R.drawable.ic_menu_delete);
                            }
                        }
                    }
                    
                    if (callback != null) {
                        callback.callback();
                    }
                }
            })
            .setNegativeButton(context.getString(R.string.action_cancel), null)
            .show();
    }
    
    /**
     * Prompts confirm dialog to remove an address
     * 
     * @param context
     * @param address
     * @param addresses
     * @param saveMenuItem
     * @param callback
     */
    public static void promptRemoveAddress(final Context context, final String address, final Addresses addresses, final MenuItem saveMenuItem, final PromptCallback callback)
    {
        new AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.text_address_unsave))
            .setPositiveButton(context.getString(R.string.action_yes), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button)
                {
                    addresses.removeByValue(address);
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_address_unsaved), Toast.LENGTH_SHORT).show();
                    
                    if (saveMenuItem != null) {
                        saveMenuItem.setIcon(android.R.drawable.ic_menu_save);
                    }
                    
                    if (callback != null) {
                        callback.callback();
                    }
                }
            })
            .setNegativeButton(context.getString(R.string.action_no), null)
            .show();
    }
    
    /**
     * Prompt to create a wallet
     * 
     * @param context
     * @param wallets
     * @param callback
     */
    public static void promptCreateWallet(final Context context, final Wallets wallets, final PromptCallback callback)
    {
        final EditText input = new EditText(context);
        input.setSingleLine();
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        
        new AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.text_wallet_save))
            .setView(input)
            .setPositiveButton(context.getString(R.string.action_save), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button) {
                    String name = input.getText().toString();
                    
                    if (name.equals("")) {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_empty_name), Toast.LENGTH_SHORT).show();
                    } else if (wallets.hasKey(name)) {
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_name_exists), Toast.LENGTH_SHORT).show();
                    } else {
                        wallets.add(name, new ArrayList<String>());
                        Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_wallet_saved), Toast.LENGTH_SHORT).show();
                        
                        if (callback != null) {
                            callback.callback();
                        }
                    }
                }
            })
            .setNegativeButton(context.getString(R.string.action_cancel), null)
            .show();
    }
    
    /**
     * Prompt to add an address to a wallet
     * 
     * @param context
     * @param address
     * @param wallets
     * @param callback
     */
    public static void promptAddToWallet(final Context context, final String address, final Wallets wallets, final PromptCallback callback)
    {
        final Spinner input = new Spinner(context);
        ArrayList<String> walletKeys = new ArrayList<String>();
        for (Map.Entry<String, ArrayList<String>> entry : wallets.getAll().entrySet()) {
            walletKeys.add(entry.getKey());
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, walletKeys);
        input.setAdapter(adapter);
        
        new AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.text_wallet_add))
            .setView(input)
            .setPositiveButton(context.getString(R.string.action_save), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button) {
                    String name = (String) input.getSelectedItem();
                    
                    ArrayList<String> addresses = wallets.get(name).getValue();
                    addresses.add(address);
                    wallets.add(name, addresses);
                    
                    Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_wallet_added), Toast.LENGTH_SHORT).show();
                    
                    if (callback != null) {
                        callback.callback();
                    }
                }
            })
            .setNegativeButton(context.getString(R.string.action_cancel), null)
            .show();
    }
    
    /**
     * Prompt to edit a wallet
     * 
     * @param context
     * @param wallet
     * @param wallets
     * @param callback
     */
    public static void promptEditWallet(final Context context, final String wallet, final Wallets wallets, final PromptCallback callback)
    {
        LinearLayout view = new LinearLayout(context);
        view.setOrientation(LinearLayout.VERTICAL);
        
        final EditText input = new EditText(context);
        input.setSingleLine();
        input.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
        input.setText(wallet);
        
        view.addView(input);
        
        final Map.Entry<String, ArrayList<String>> existing = wallets.get(wallet);
        
        // Add all addresses as checkboxes
        final HashMap<String, CheckBox> checkboxes = new HashMap<String, CheckBox>();
        for (String address : existing.getValue()) {
            CheckBox checkbox = new CheckBox(context);
            checkbox.setText(address);
            checkboxes.put(address, checkbox);
            view.addView(checkbox);
        }
        
        new AlertDialog.Builder(context)
            .setTitle(context.getString(R.string.text_wallet_edit))
            .setView(view)
            .setPositiveButton(context.getString(R.string.action_save), new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int button) {
                    String name = input.getText().toString();
                    // Edit wallet name
                    if (!wallet.equals(name)) {
                        if (name.equals("")) {
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_empty_name), Toast.LENGTH_SHORT).show();
                        } else if (wallets.hasKey(name)) {
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_name_exists), Toast.LENGTH_SHORT).show();
                        } else {
                            wallets.add(name, existing.getValue());
                            wallets.remove(existing.getKey());
                            
                            Toast.makeText(context.getApplicationContext(), context.getString(R.string.text_wallet_saved), Toast.LENGTH_SHORT).show();
                        }
                    }
                    
                    // Check for any checked addresses and remove
                    for (Map.Entry<String, CheckBox> entry : checkboxes.entrySet()) {
                        if (entry.getValue().isChecked()) {
                            ArrayList<String> addresses = existing.getValue();
                            addresses.remove(addresses.indexOf(entry.getKey()));
                            wallets.add(existing.getKey(), addresses);
                        }
                    }
                    
                    if (callback != null) {
                        callback.callback();
                    }
                }
            })
            .setNegativeButton(context.getString(R.string.action_cancel), null)
            .show();
    }
}
