package com.example.TakeMe;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.SearchView;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String MyPREFERENCES = "321qwe";
    private SearchView searchView;
    private TextView tvStates;
    private String location;
    private Location gpslocation;
    private String ACTION_GET_USERTEMP = "getSymptoms.php?";
    private String cUsers              = "";
    private String cUserTemp = "Loading.... \n try again later!";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private String host ;
    private int iNotSafe ;
    private double dSum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        host = prefs.getString("host", "http://192.168.0.116/thermo/");

        searchView = (SearchView)findViewById(R.id.sv_location);
        tvStates   = (TextView) findViewById(R.id.tvStates);

        tvStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(MapsActivity.this)
                        .setTitle("User Temperature")
                        .setMessage(cUserTemp)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                String clocation = searchView.getQuery().toString();

                List<Address> addressList = null;

                if(location != null || !location.equals("")){

                    Geocoder geocoder = new Geocoder(MapsActivity.this );
                    try {
                        addressList = geocoder.getFromLocationName(clocation, 1);

                        if(addressList.size() > 0) {
                            Address address = addressList.get(0);
                            String addresse = address.getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
                            String city = address.getLocality();
                            String state = address.getAdminArea();
                            String country = address.getCountryName();
                            String postalCode = address.getPostalCode();
                            String knownName = address.getFeatureName();

                           // address.
                            String data = "city:"   + city + "\n"
                                        + "state:"   + state + "\n"
                                        + "address:" + addresse + "\n"
                                        + "country:"    + country + "\n"
                                        + "knownName:"    + knownName + "\n"
                                        + "postalCode:"    + postalCode + "\n"
                                        + "Loc: " + address.getLatitude() + address.getLongitude();
                          //  tvStates.setText(data);

                            LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                            drawCircle(latLng);
                            mMap.addMarker(new MarkerOptions().position(latLng).title(clocation) .icon(BitmapDescriptorFactory.fromResource(R.drawable.location)));
                            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16.0f));

                            Location gpslocationD;
                            gpslocation = new Location("");
                            gpslocation.setLatitude(address.getLatitude());
                            gpslocation.setLongitude(address.getLongitude());

                            gpslocationD = new Location("");

                            try {
                                JSONObject jsonObject = new JSONObject(location);
                                if( jsonObject.getString("result").equals("done")) {

                                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                                    int counter = 0;
                                    Double maxTemp = 0.0, minTemp = 100000.0;


                                    String cData = "";
                                    int iUsers = 0;
                                    cUserTemp = "";
                                    iNotSafe    = 0;
                                    for(int x = 0; x < jsonArray.length() ; x++) {


                                        addressList = geocoder.getFromLocation(jsonArray.getJSONObject(x).getDouble("lat"),
                                                jsonArray.getJSONObject(x).getDouble("lon"),1);

                                        gpslocationD.setLatitude(jsonArray.getJSONObject(x).getDouble("lat"));
                                        gpslocationD.setLongitude(jsonArray.getJSONObject(x).getDouble("lon"));
                                        Float distance = gpslocationD.distanceTo(gpslocation);


                                        if(distance <= 200){

                                            dSum += jsonArray.getJSONObject(x).getDouble("mtemp");

                                            if(jsonArray.getJSONObject(x).getDouble("mtemp")>= 27.8)
                                                iNotSafe++;

                                            if(jsonArray.getJSONObject(x).getDouble("mtemp")  < minTemp){
                                                minTemp =  jsonArray.getJSONObject(x).getDouble("mtemp") ;

                                            }

                                            if(jsonArray.getJSONObject(x).getDouble("mtemp") > maxTemp){
                                                maxTemp = jsonArray.getJSONObject(x).getDouble("mtemp") ;
                                            }

                                            iUsers++;
                                            cUserTemp = cUserTemp
                                                      + iUsers
                                                      +  "          "
                                                      +  jsonArray.getJSONObject(x).getDouble("mtemp") + "\n"
                                                      ;

                                            cUsers += jsonArray.getJSONObject(x).getDouble("id") + ",";
                                            cData = cData + jsonArray.getJSONObject(x).getDouble("lat") + ":"+
                                                    jsonArray.getJSONObject(x).getDouble("lon")  + "\n" ;
                                            counter++;

                                        }
                                            //address.getAddressLine(0);
                                    }

                                    String status = "Safe";
                                    if(maxTemp >= 37.8)
                                        status = "Not Safe";

                                    if(minTemp > 1000.0)
                                        minTemp = 0.0;
                                    
                                    cData   ="Status                    :" + status + "\n"
                                            + "People                   :" + counter + "\n"
                                            + "Average                 :" + (dSum/counter) + "°C\n"
                                            + "Temp > 37.8°C     :" + iNotSafe + "\n"
                                            + "Minimum Temp   :" + minTemp + "°C\n"
                                            + "Maximum Temp  :" + maxTemp + "°C\n";

                                    tvStates.setText(cData);
                                }else {
                                }
                            } catch (JSONException | IOException e) { e.printStackTrace(); }

                        }else print("Can't find " + location);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });


    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(300);

        // Border color of the circle
        circleOptions.strokeColor(Color.BLACK);

        // Fill color of the circle
        circleOptions.fillColor(0x30ff0000);

        // Border width of the circle
        circleOptions.strokeWidth(2);

        // Adding the circle to the GoogleMap
        mMap.addCircle(circleOptions);

    }
    private void print(String response) {
        Toast.makeText(getApplicationContext(),response,Toast.LENGTH_LONG).show();
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
                            cUsers = jsonObject.getString("data");
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        SharedPreferences prefs = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        //host = prefs.getString("host", "http://192.168.0.116/thermo/");
        String log = prefs.getString("lon", "0");//"No name defined" is the default value.
        String lat = prefs.getString("lat", "0"); //0 is the default value.
        location = prefs.getString("location", "{}");

        createMarker(Double.parseDouble(lat), Double.parseDouble(log),"Me","", R.drawable.thermometer);

        try {
            JSONObject jsonObject = new JSONObject(location);
            if( jsonObject.getString("result").equals("done")) {

                JSONArray jsonArray = jsonObject.getJSONArray("data");

                for(int x = 0; x < jsonArray.length() ; x++) {

                    List<Address> addressList = null;
                    Geocoder geocoder = new Geocoder(MapsActivity.this );
                    addressList = geocoder.getFromLocation(jsonArray.getJSONObject(x).getDouble("lat"),
                            jsonArray.getJSONObject(x).getDouble("lon"),1);

                    Address address = addressList.get(0);
                    String addresse = address.getAddressLine(0);

                    createMarker(jsonArray.getJSONObject(x).getDouble("lat"),
                                 jsonArray.getJSONObject(x).getDouble("lon"),
                                 jsonArray.getJSONObject(x).getDouble("mtemp") + "°C"
                                 ,
                         "",
                                 R.drawable.virus);
                }

            }else {

            }


        } catch (JSONException | IOException e) { e.printStackTrace(); }


    }

    protected Marker createMarker(double latitude, double longitude, String title, String snippet, int iconResID) {

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(latitude, longitude), 10.0f));
        return mMap.addMarker(new MarkerOptions()
                .position(new LatLng(latitude, longitude))
                .anchor(0.5f, 0.5f)
                .title(title)
                .snippet(snippet)
                .icon(BitmapDescriptorFactory.fromResource(iconResID))
        );
    }
}
