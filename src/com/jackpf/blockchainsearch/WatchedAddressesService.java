package com.jackpf.blockchainsearch;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Binder;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jackpf.blockchainsearch.Data.BlockchainData;
import com.jackpf.blockchainsearch.Entity.PersistedAddresses;
import com.jackpf.blockchainsearch.Entity.SocketCmd;
import com.jackpf.blockchainsearch.Service.Utils;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class WatchedAddressesService extends Service
{
    /**
     * Service binder
     */
    private final IBinder binder = new ServiceBinder();
    
    /**
     * Web socket
     */
    private final WebSocketConnection socket = new WebSocketConnection();
    
    /**
     * Log tag
     */
    private final String TAG = this.getClass().getName();

    /**
     * Start service
     * 
     * @param intent
     * @param flags
     * @param startId
     * @return
     */
    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        updateNotification("Connecting to service", "Please wait...");
        
        final String[] addresses = new PersistedAddresses(this).getAll().values().toArray(new String[]{});
        
        try {
            socket.connect(BlockchainData.WS_URL, new WebSocketHandler() {
                @Override
                public void onOpen() {
                   WatchedAddressesService.this.updateNotification("Watching addresses!", "Monitoring " + addresses.length + " addresse" + (addresses.length > 1 ? "s" : ""));
                   
                   // Subscribe to addresses
                   for (int i = 0; i < addresses.length; i++) {
                       SocketCmd cmd = new SocketCmd(SocketCmd.ADDR_SUB);
                       cmd.setParam("addr", addresses[i]);
                       
                       socket.sendTextMessage(cmd.toString());
                   }
                }
     
                @Override
                public void onTextMessage(String payload) {
                   JSONParser parser = new JSONParser();
                   try {
                       // Get the transaction
                       JSONObject obj = (JSONObject) parser.parse(payload);
                       JSONObject tx = (JSONObject) obj.get("x");
                       if (tx != null) {
                           // Check which of our watched addresses the transaction involves
                           for (int i = 0; i < addresses.length; i++) {
                               Utils.ProcessedTransaction processed = Utils.processTransaction(addresses[i], tx);
                               // If the transaction amount for this address is not 0, this address was involved
                               if (processed.getAmount() != 0) {
                                   String text = String.format(
                                       "%s %s %s %s",
                                       processed.getAmount() > 0 ? "Received" : "Sent",
                                       Utils.btcFormat(processed.getAmount(), WatchedAddressesService.this).replace("-", ""),
                                       processed.getAmount() > 0 ? "from" : "to",
                                       processed.getAddress()
                                   );
                                   
                                   Intent intent = new Intent(WatchedAddressesService.this, AddressActivity.class);
                                   intent.putExtra(AddressActivity.EXTRA_SEARCH, addresses[i]);
                                   
                                   WatchedAddressesService.this.newNotification(addresses[i], text, intent);
                               }
                           }
                       }
                   } catch (ParseException e) {
                       Log.d(TAG, e.toString());
                   }
                }
     
                @Override
                public void onClose(int code, String reason) {
                   Log.d(TAG, code + ": " + reason);
                }
            });
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
        }
        
        return Service.START_STICKY;
    }
    
    /**
     * On bind
     * 
     * @param intent
     * @return
     */
    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }
    
    /**
     * On destroy
     */
    @Override
    public void onDestroy()
    {
        // Remove any notifications
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        for (int i = 0; i < builders.size(); i++) {
            nm.cancel(i);
        }
        
        // Disconnect the web socket
        socket.disconnect();
    }
    
    /**
     * Service binder
     */
    public class ServiceBinder extends Binder
    {
        public WatchedAddressesService getService()
        {
            return WatchedAddressesService.this;
        }
    }
    
    ArrayList<NotificationCompat.Builder> builders = new ArrayList<NotificationCompat.Builder>();
    private void updateNotification(String title, String text)
    {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int ID = 0;
        
        if (builders.size() == ID) {
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, PreferencesActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            
            builders.add(new NotificationCompat.Builder(this)
                .setContentTitle(title)
                .setContentText(text)
                .setSmallIcon(R.drawable.ic_notification)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentPendingIntent));
        } else {
            builders.get(ID)
                .setContentTitle(title)
                .setContentText(text);
        }
        
        nm.notify(ID, builders.get(ID).build());
    }
    
    private void newNotification(String title, String text, Intent intent)
    {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        builders.add(new NotificationCompat.Builder(this)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_notification)
            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
            .setAutoCancel(true)
            .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)));
        
        if (PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.pref_notification_key), Boolean.parseBoolean(getString(R.string.pref_notification_default)))) {
            builders.get(builders.size() - 1).setDefaults(Notification.DEFAULT_ALL);
        }
        
        nm.notify(builders.size() - 1, builders.get(builders.size() - 1).build());
    }
}
