package com.example.TakeMe;
import android.Manifest;
import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sbag.R;
import com.example.TakeMe.ui.login.LoginActivity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class NotificationService extends Service{
    static Intent intent;

    private int Reminder = 0;
    private static Context context;
    private boolean start=false;

    private Thread t;
    private Handler handler = new Handler();

    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private NotificationManager nm;

    private Location location;

    private String id;
    private String host;
    private String MyPREFERENCES                 = "321qwe" ;
    private String ACTION_GETLOCATION            = "getlocation.php?id=";
    private String ACTION_UPDATE_LOCATION        = "updatelocation.php?id=";
    private String ID_SPECIAL_OFFER_NOTIFICATION = "1234";

    private Location currentLooation;

    public  static void ServiceBegin(Context contex){
        intent = new Intent(contex,NotificationService.class);
        context = contex;
        contex.startService(intent);
    }

    public NotificationService(){
    }

    public NotificationService(Context contex){
        context = contex;
    }

    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        return null;
    }

    public void Destroy() {
        stopSelf();
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // TODO Auto-generated method stub
        return super.onStartCommand(intent, flags, startId);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void onCreate() {
        // TODO Auto-generated method stub
        super.onCreate();
        nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        id = sharedpreferences.getString("id","0");

        host = sharedpreferences.getString("host","http://192.168.0.116/thermo/");

        //start = true;
        t = new Thread(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Reminder++;

                        if(id != null)
                        if(!id.equals("0"))
                          sendAndRequestResponse(ACTION_GETLOCATION + id);

                        if(Reminder >= 10) {
                            Reminder = 0;
                            setNotification(R.drawable.sanitize, "Reminder", "Sanitize your hand and update temperature", LoginActivity.class);
                        }
                        handler.postDelayed(this,5000);
                    }
                },1);
            }
        });
        t.start();

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            return;
        }
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 500, 1, locationListener);


        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

         locationListener = new MyLocationListener();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)context , new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 1);
             }

            return;
        }
         locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 200, 1, locationListener);

    }
/*
    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

          print(loc.getLongitude() + "");
        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }*/

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

           start = true;
            currentLooation = loc;

            sendAndRequestResponse(ACTION_UPDATE_LOCATION + id
                                     + "&lon=" + currentLooation.getLongitude()
                                     + "&lat=" + currentLooation.getLatitude());




        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private void sendAndRequestResponse(String url) {

       // print(host);

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        url = host + url;


        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response != null ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if( jsonObject.getString("result").equals("done")) {

                            JSONArray jsonArray = jsonObject.getJSONArray("data");

                            location = new Location("");

                             if(start) {

                                 for(int x = 0; x < jsonArray.length() ; x++) {
                                    location.setLongitude(jsonArray.getJSONObject(x).getDouble("lon"));
                                    location.setLatitude(jsonArray.getJSONObject(x).getDouble("lat"));
                                    Float distance = location.distanceTo(currentLooation);
                                    if (distance <= 5 ){
                                      //  print("distance:" + distance);
                                        setNotification(R.drawable.social_distancing,"Social Distancing","Stay 1m away from the next person",LoginActivity.class);
                                        break;
                                    }

                                }
                            }

                        }else {

                        }


                    } catch (JSONException e) { e.printStackTrace(); }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                Log.i("Error123","Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }

    private void setNotification(int icon,String title, String mssg, Class pendclass){
        Intent intent = new Intent(getApplicationContext(), pendclass /* LoginActivity.class*/);
        PendingIntent pendIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, 0);
        showSmallNotification(icon, title , mssg , "2020/02/01" , pendIntent);
    }


    private void showSmallNotification( int icon, String title, String message, String timeStamp, PendingIntent resultPendingIntent){
        String CHANNEL_ID = "1234";
        String CHANNEL_NAME = "Notification";

        // I removed one of the semi-colons in the next line of code
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();

        inboxStyle.addLine(message);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // I would suggest that you use IMPORTANCE_DEFAULT instead of IMPORTANCE_HIGH
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
                // .setWhen(getTimeMilliSec(timeStamp))
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

    private void print(String stringJson) {
        Toast.makeText(getApplicationContext(),stringJson,Toast.LENGTH_LONG).show();
    }



}
