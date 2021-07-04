package com.example.TakeMe;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.sbag.R;
import com.example.TakeMe.ui.login.LoginActivity;

public class Notification extends Service {

    MediaPlayer myPlayer;
    private int c;
    private Handler handler = new Handler();
    private Thread t;
    private String ID_SPECIAL_OFFER_NOTIFICATION = "1234";
    final static String ACTION = "NotifyServiceAction";
    final static String STOP_SERVICE_BROADCAST_KEY="StopServiceBroadcastKey";
    final static int RQS_STOP_SERVICE = 1;


    public Notification() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        t = new Thread(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {


                        handler.postDelayed(this,10000);

                        c++;
                        print("" + c);

                        Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                        PendingIntent pendIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
                        showSmallNotification(R.drawable.thermo, "lol" , "lol" , "2020/02/01" , pendIntent);

                    }
                },1);
            }
        }); t.start();


        return START_STICKY;
    }

    public class ServiceReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context arg0, Intent arg1) {

            int rqs = arg1.getIntExtra(STOP_SERVICE_BROADCAST_KEY, 0);

            if (rqs == RQS_STOP_SERVICE){
                stopSelf();
                ((NotificationManager) getSystemService(NOTIFICATION_SERVICE))
                        .cancelAll();
            }
        }
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw null;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

    }

    private void print(String position) {
        Toast.makeText(getApplicationContext(),position, Toast.LENGTH_SHORT).show();
    }

    private void showSmallNotification( int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent){
        String CHANNEL_ID = "1234";
        String CHANNEL_NAME = "Notification";

        // I removed one of the semi-colons in the next line of code
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
            channel.enableVibration(true);
            channel.setLightColor(Color.BLUE);
            channel.enableLights(true);
            channel.setShowBadge(true);
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(getApplicationContext(), CHANNEL_ID)
                .setVibrate(new long[]{0, 100})
                .setPriority(android.app.Notification.PRIORITY_MAX)
                .setLights(Color.BLUE, 3000, 3000)
                .setAutoCancel(true)
                .setContentTitle(title)
                .setContentIntent(resultPendingIntent)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(inboxStyle)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(), icon))
                .setContentText(message);
        // Removed .build() since you use it below...no need to build it twice

        // Don't forget to set the ChannelID!!
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            notificationBuilder.setChannelId(ID_SPECIAL_OFFER_NOTIFICATION);
        }

        notificationManager.notify(CHANNEL_ID, 1, notificationBuilder.build());
    }


}
