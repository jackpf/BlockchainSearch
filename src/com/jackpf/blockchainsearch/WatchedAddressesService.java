package com.jackpf.blockchainsearch;

import java.util.ArrayList;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.jackpf.blockchainsearch.Data.BlockchainData;
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
        updateNotification("Connecting to service...");
        
        final String[] addresses = {
            "1LuckyR1fFHEsXYyx5QK4UFzv3PEAepPMK",
            "1LuckyG4tMMZf64j6ea7JhCz7sDpk6vdcS",
            "1bonesBjs3DQUbx4wxPQwrbwCkNjWtLB4",
            "1bonesUhqtbLAGKWZuawCzsYqmYWEgPwH",
            "1bonesPdRYS91Mq9arbiUratHy2J5gDut",
            "1bones5gF1HJeiexQus6UtvhU4EUD4qfj",
            "1dice5wwEZT2u6ESAdUGG6MHgCpbQqZiy"
        };
        
        try {
            socket.connect(BlockchainData.WS_URL, new WebSocketHandler() {
                @Override
                public void onOpen() {
                   WatchedAddressesService.this.updateNotification("Watching addresses!");
                   
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
    private void updateNotification(String title)
    {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int ID = 0;
        
        if (builders.size() == ID) {
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, PreferencesActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            
            builders.add(new NotificationCompat.Builder(this)
                .setContentTitle(title)
                //.setContentText("Text")
                .setSmallIcon(R.drawable.ic_notification)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(contentPendingIntent));
        } else {
            builders.get(ID).setContentTitle(title);
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
            .setAutoCancel(true)
            .setOnlyAlertOnce(true)
            .setContentIntent(PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)));
        
        nm.notify(builders.size() - 1, builders.get(builders.size() - 1).build());
    }
}
