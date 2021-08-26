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
    private String MyPREFERENCES                 = "32145788" ;
    private String ACTION_GETLOCATION            = "getLocation.php?id=";
    private String ACTION_UPDATE_LOCATION        = "updateLocation.php?id=";
    private String ID_SPECIAL_OFFER_NOTIFICATION = "1236";

    private Location currentLooation;
    private String UserType;
    private double ader;

    public  static void ServiceBegin(Context contex){
        intent = new Intent(contex,NotificationService.class);
        context = contex;
        contex.startService(intent);

    }

    public static  void ServiceStop(Context contex){
        intent = new Intent(contex,NotificationService.class);
        context = contex;
        context.stopService(intent);
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

        host = sharedpreferences.getString("host","http://192.168.1.33/takeme/");
        UserType = sharedpreferences.getString("UserType","1");


        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions((Activity) context, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            return;
        }
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 5000, 1, locationListener);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

         locationListener = new MyLocationListener();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            if(ContextCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)!=PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions((Activity)context , new String[]{Manifest.permission.ACCESS_FINE_LOCATION }, 1);
             }

            return;
        }
         locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 2000, 1, locationListener);

    }


    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

           start = true;
            currentLooation = loc;
            SharedPreferences sharedpreferences;
            sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            id = sharedpreferences.getString("id","0");

           // print("lon:"  + (currentLooation.getLongitude() + ader));

            sendAndRequestResponse(ACTION_UPDATE_LOCATION + id
                                     + "&lon=" + (currentLooation.getLongitude() )
                                     + "&lat=" + currentLooation.getLatitude()
                                     + "&tp="  + UserType);

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }

    private void sendAndRequestResponse(String url) {

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        url = host + url;

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if (response != null) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getString("result").equals("done")) {

                            if(jsonObject.getString("datatype").equals("updatelocation")) {

                                SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                                editor.putString ("data", jsonObject.getJSONArray("data").toString() );
                                editor.apply();
                            }

                            }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
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

    private void print(String stringJson) {
        Toast.makeText(getApplicationContext(),stringJson,Toast.LENGTH_LONG).show();
    }



}
