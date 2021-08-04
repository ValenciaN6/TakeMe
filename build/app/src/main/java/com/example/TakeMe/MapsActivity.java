package com.example.TakeMe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sbag.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String MyPREFERENCES = "32145788";
    private TextView tvStates;
    private Thread t;
    private Handler handler = new Handler();
    private String location;
    private Location gpslocation;
    private String ACTION_TRACK = "track.php?pid=";
    private String cUsers              = "";
    private String cUserTemp = "Loading.... \n try again later!";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String driverID ;
    private String host ;
    private String ID;
    private int iNotSafe ;
    private double dSum = 0;
    private boolean isMapReady = false;
    private  Polyline polyline;
    private String route = "";
    private String directionUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

    //    print(url);
     //   print(url);
       // sendAndRequestResponse(url);

        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        host = prefs.getString("host", "");
        driverID = prefs.getString("driverID", "17");
        String data = prefs.getString("data", "{}");//"No name defined" is the default value.
        tvStates   = (TextView) findViewById(R.id.tvStates);
       // tvStates.setText("..");


        t = new Thread(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getContext() != null)

                            sendAndRequestResponse(ACTION_TRACK + ID + "&did=" + driverID);
                        handler.postDelayed(this,15000);
                    }
                },1);
            }
        });

        JSONArray jjsonArray = null;
        try {
            jjsonArray = new JSONArray(data);
            JSONObject jjsonObject = new JSONObject(jjsonArray.getJSONObject(0).toString());

            ID = jjsonObject.getString("ID");
            t.start();
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }

       // t.start();

    }


    private void print(String response) {
        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
        Log.i("infor:" , response);
    }

    private  String getUrl(LatLng origin, LatLng destine){

        String str_origin = "origin=" + origin.latitude + ","+ origin.longitude;
        String str_dest   = "destination=" + destine.latitude + ","+ destine.longitude;
        String mode       = "mode=driving";
        String parameters = str_origin + "&" + str_dest + "&" + mode;
        String url = "https://maps.googleapis.com/maps/api/directions/json?" + parameters + "&key=AIzaSyAHtgurSlANT1iA1H5o3L_-XWlf_BVB438" ;

                return url;

    }

    private void sendAndRequestResponse(String url) {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        url = host + url;

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response != null ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);

                        if( jsonObject.getString("result").equals("done")) {
                            if(jsonObject.getString("datatype").equals("track")) {

                                mMap.clear();
                                JSONArray jsonArray = jsonObject.getJSONArray("data");
                                Double driverLat = Double.parseDouble(jsonArray.getJSONObject(0).getString("Latitude"));
                                Double driverLon = Double.parseDouble(jsonArray.getJSONObject(0).getString("Longitude"));

                                Double patientLat = Double.parseDouble(jsonArray.getJSONObject(0).getString("lat"));
                                Double patientLon = Double.parseDouble(jsonArray.getJSONObject(0).getString("lon"));

                                createMarker(driverLat,driverLon,"Driver","", R.drawable.ambulance);
                                createMarker(patientLat,patientLon,"Me","", R.drawable.patient);

                                directionUrl = getUrl(new LatLng(patientLat,patientLon),
                                                    new LatLng(driverLat,driverLon));

                                FetchMyData process = new FetchMyData();
                                process.execute();


                            }
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {
                    print("Can not connect to server (◕︵◕)" );
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

 private void  drawpath(){

        if(isMapReady){
            try {

route = route.replace("null" , "");


                if(polyline != null)
                    polyline.remove();
                JSONObject jsonObject1 = new JSONObject(route);
                // polyline.remove();
                PolylineOptions polylineOptions = new PolylineOptions();
                JSONArray arr = null;
                JSONObject distance = null;
                JSONObject duration = null;

                arr = jsonObject1.getJSONArray("routes");


                arr = arr.getJSONObject(0).getJSONArray("legs");
                distance = arr.getJSONObject(0).getJSONObject("distance");
                duration = arr.getJSONObject(0).getJSONObject("duration");
                arr = arr.getJSONObject(0).getJSONArray("steps");

                tvStates.setText("Distance:" + distance.getString("text") +
                                "\n" +
                                "Duration:" + duration.getString("text")
                                 );

                for (int i = 0; i < arr.length(); i++)
                {
                    polylineOptions.color(Color.RED);
                    polylineOptions.width(12);

                    JSONObject poly = arr.getJSONObject(i).getJSONObject("polyline");
                    String st_polyline = poly.getString("points");

                    List<LatLng> list = decodePoly(st_polyline);
                    for(int x = 0; x< list.size() ; x++) {
                        polylineOptions.add(list.get(x));

                    }
                    polylineOptions.geodesic(true);
                }

                polyline=mMap.addPolyline(polylineOptions);
                // polyline.remove();

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

 }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;



       isMapReady = true;


    }

    private List<LatLng> decodePoly(String encoded) {

        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = encoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = encoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            LatLng p = new LatLng((((double) lat / 1E5)),
                    (((double) lng / 1E5)));
            poly.add(p);
        }

        return poly;
    }
    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 13.0f));
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(iconResID))
        );
    }


    public class FetchMyData extends AsyncTask<Void,Void,Void> {
        String result;
        @Override
        protected Void doInBackground(Void... voids) {
            try {
                URL url = new URL(directionUrl);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    result = result + line;
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            route = result;
            drawpath();
        }
    }


}
