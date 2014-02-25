package com.jackpf.blockchainsearch;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import de.tavendo.autobahn.WebSocketConnection;
import de.tavendo.autobahn.WebSocketException;
import de.tavendo.autobahn.WebSocketHandler;

public class WatchedAddressesService extends Service
{
    private final IBinder binder = new ServiceBinder();
    
    private final WebSocketConnection socket = new WebSocketConnection();
    
    private final String TAG = this.getClass().getName();

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        createNotification("Connecting to service...");
        
        final String ws = "ws://192.168.1.132:9000";
        
        try {
            socket.connect(ws, new WebSocketHandler() {
                @Override
                public void onOpen() {
                   Log.d(TAG, "Status: Connected to " + ws);
                   socket.sendTextMessage("Hello, world!");
                }
     
                @Override
                public void onTextMessage(String payload) {
                   Log.d(TAG, "Got echo: " + payload);
                }
     
                @Override
                public void onClose(int code, String reason) {
                   Log.d(TAG, "Connection lost.");
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
    
    public class ServiceBinder extends Binder
    {
        public WatchedAddressesService getService()
        {
            return WatchedAddressesService.this;
        }
    }
    
    NotificationCompat.Builder builder;
    private void createNotification(String title)
    {
        NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        int ID = 0;
        
        if (builder == null) {
            builder = new NotificationCompat.Builder(this)
                .setContentTitle(title)
                //.setContentText("Text")
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(false)
                .setOngoing(true)
                .setOnlyAlertOnce(true)
                .setContentIntent(PendingIntent.getActivity(this, 0, new Intent(this, MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT));
    
            nm.notify(ID, builder.build());
        } else {
            builder.setContentTitle(title);
            nm.notify(ID, builder.build());
        }
    }
}
