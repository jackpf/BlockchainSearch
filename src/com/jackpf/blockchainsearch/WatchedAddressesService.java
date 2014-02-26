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
import com.jackpf.blockchainsearch.Service.Utils;

import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class WatchedAddressesService extends Service
{
    private final IBinder binder = new ServiceBinder();
    
    private final WebSocketConnection socket = new WebSocketConnection();
    
    private final String TAG = this.getClass().getName();
    
    public final static String EXTRA_ACTION = "action";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        if ("stop".equals(intent.getStringExtra(EXTRA_ACTION))) {
            stopService(new Intent(this, WatchedAddressesService.class));
            return 0;
        }
        
        updateNotification("Connecting to service...");
        
        final String[] addresses = {"1LuckyR1fFHEsXYyx5QK4UFzv3PEAepPMK", "14qFqPmoVABQHTT7Sn9qmW6wYN4jf3w7dA", "1LuckyG4tMMZf64j6ea7JhCz7sDpk6vdcS"};
        
        try {
            socket.connect(BlockchainData.WS_URL, new WebSocketHandler() {
                @Override
                public void onOpen() {
                   WatchedAddressesService.this.updateNotification("Watching addresses!");
                   
                   for (int i = 0; i < addresses.length; i++) {
                       Cmd cmd = new Cmd(Cmd.ADDR_SUB);
                       cmd.setParam("addr", addresses[i]);
                       
                       socket.sendTextMessage(cmd.toString());
                   }
                }
     
                @Override
                public void onTextMessage(String payload) {
                   JSONParser parser = new JSONParser();
                   try {
                       JSONObject obj = (JSONObject) parser.parse(payload);
                       JSONObject tx = (JSONObject) obj.get("x");
                       for (int i = 0; i < addresses.length; i++) {
                           Utils.ProcessedTransaction processed = Utils.processTransaction(addresses[i], tx);
                           if (processed.getAmount() != 0) {
                               String text = String.format(
                                   "%s %s %s %s",
                                   processed.getAmount() > 0 ? "Received" : "Sent",
                                   Utils.btcFormat(processed.getAmount()).replace("-", ""),
                                   processed.getAmount() > 0 ? "from" : "to",
                                   processed.getAddress()
                               );
                               WatchedAddressesService.this.newNotification(addresses[i], text);
                           }
                       }
                   } catch (ParseException e) {
                       e.printStackTrace();
                   }
                }
     
                @Override
                public void onClose(int code, String reason) {
                   WatchedAddressesService.this.updateNotification("Connection lost");
                }
            });
        } catch (WebSocketException e) {
            Log.d(TAG, e.toString());
        }
        
        return Service.START_STICKY;
    }
    
    @Override
    public IBinder onBind(Intent intent)
    {
        return binder;
    }
    
    @Override
    public void onDestroy()
    {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        for (int i = 0; i < builders.size(); i++) {
            nm.cancel(i);
        }
    }
    
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
            Intent stopIntent = new Intent(this, WatchedAddressesService.class);
            stopIntent.putExtra(EXTRA_ACTION, "stop");
            PendingIntent stopPendingIntent = PendingIntent.getService(this, 0, stopIntent, PendingIntent.FLAG_ONE_SHOT);
            PendingIntent contentPendingIntent = PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            
            builders.add(new NotificationCompat.Builder(this)
                .setContentTitle(title)
                //.setContentText("Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .addAction(R.drawable.ic_menu_refresh, "Stop", stopPendingIntent)
                .setContentIntent(contentPendingIntent));
        } else {
            builders.get(ID).setContentTitle(title);
        }
        
        nm.notify(ID, builders.get(ID).build());
    }
    
    private void newNotification(String title, String text)
    {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        
        builders.add(new NotificationCompat.Builder(this)
            .setContentTitle(title)
            .setContentText(text)
            .setSmallIcon(R.drawable.ic_launcher)
            .setAutoCancel(false)
            .setOnlyAlertOnce(true)
            .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT)));
        
        nm.notify(builders.size() - 1, builders.get(builders.size() - 1).build());
    }
    
    private class Cmd
    {
        public final static String ADDR_SUB = "addr_sub", UNCONFIRMED_SUB = "unconfirmed_sub";
        
        private JSONObject cmd = new JSONObject();
        
        public Cmd(String cmd)
        {
            setParam("op", cmd);
        }
        
        public void setParam(String key, String value)
        {
            this.cmd.put(key, value);
        }
        
        public String toString()
        {
            return cmd.toString();
        }
    }
}
