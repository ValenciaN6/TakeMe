package com.example.TakeMe.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

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
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestDashboad#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestDashboad extends Fragment implements OnMapReadyCallback {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private GoogleMap mMap;

    private Thread t;
    private CharSequence[] items;

    private Handler handler = new Handler();
    private String ACTION_GET_AMBULANCE = "getambulance.php";
    private String ACTION_GETFRIENDS = "getfriend.php?id=";
    private String ACTION_REQUEST = "request.php?";
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private int count = 0;
    private String data ="{}";
    private String host ;
    private String ID;
    private String MyPREFERENCES = "32145788";
    private FloatingActionButton btnRequest;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String[] itemsID;

    public RequestDashboad() {
        // Required empty public constructor
    }

    public void setmapData(String dt){
        mMap.clear();
        //print(data);
        try {
            JSONArray jjsonArray = new JSONArray(data);
            JSONObject jjsonObject = new JSONObject(jjsonArray.getJSONObject(0).toString());
            ;

            createMarker(jjsonObject.getDouble("Latitude"), jjsonObject.getDouble("Longitude"),"Me","", R.drawable.accident);

            LatLng latLng = new LatLng(jjsonObject.getDouble("Latitude"), jjsonObject.getDouble("Longitude"));
            drawCircle(latLng);
            if(!dt.equals("")) {



                JSONObject jsonObject = new JSONObject(dt);
                JSONArray jsonArray = jsonObject.getJSONArray("data");

                for (int x = 0; x < jsonArray.length(); x++) {
                    createMarker(jsonArray.getJSONObject(x).getDouble("Latitude"),
                            jsonArray.getJSONObject(x).getDouble("Longitude"),
                            jsonArray.getJSONObject(x).getString("NumberPlate") + " : "
                                    + jsonArray.getJSONObject(x).getString("Description")
                            ,
                            "",
                            R.drawable.ambulance);
                }
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    // TODO: Rename and change types and number of parameters
    public static RequestDashboad newInstance(String param1, String param2) {
        RequestDashboad fragment = new RequestDashboad();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_request_dashboad, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment)this.getChildFragmentManager()
               .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        btnRequest = (FloatingActionButton)view.findViewById(R.id.btnRequest);
        data = preferences.getString("data", "{}");//"No name defined" is the default value.
        host = preferences.getString("host", "http://192.168.1.33/takeme/");


        JSONArray jjsonArray = null;
        try {
            jjsonArray = new JSONArray(data);
            JSONObject jjsonObject = new JSONObject(jjsonArray.getJSONObject(0).toString());

            ID = jjsonObject.getString("ID");
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }



        btnRequest.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {


                // = {"Red", "Green", "Blue"};

                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
                dialogBuilder.setTitle("Who do you want to request for?");
                dialogBuilder.setItems(items, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        // Do anything you want here
                        print("Requesting and ambulance for " + items[which]);
                        sendAndRequestResponse(ACTION_REQUEST + "fm=" + itemsID[which] + "&to=" + ID + "&tp=request");
                    }
                });

                dialogBuilder.create().show();
                count = 0;

                return false;
            }
        });

        btnRequest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
              count++;

              if(count >= 5)
              {
                  print("Requesting an ambulance...."); //fm=6&to=6&tp=request
                  sendAndRequestResponse(ACTION_REQUEST + "fm=" + ID + "&to=" + ID + "&tp=request");
                  count = 0;
              }
            }
        });

        t = new Thread(new Runnable() {
            @Override
            public void run() {

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(getContext() != null)

                        sendAndRequestResponse(ACTION_GET_AMBULANCE);
                        handler.postDelayed(this,10000);
                    }
                },1);
            }
        });
        t.start();

        //

        sendAndRequestResponse(ACTION_GETFRIENDS + ID );
        return view;
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

    private void sendAndRequestResponse(String url) {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(getContext());
        url = host + url;
        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response != null ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if( jsonObject.getString("result").equals("done")) {

                            if(jsonObject.getString("datatype").equals("request")) { //
                                print("Request was sent");

                            }else

                            if(jsonObject.getString("datatype").equals("ambulance")) { //
                               setmapData(response);

                            }else if(jsonObject.getString("datatype").equals("friend")){

                                items = new CharSequence[jsonObject.getJSONArray("data").length()];
                                itemsID = new String[jsonObject.getJSONArray("data").length()];


                                for(int x = 0 ; x < jsonObject.getJSONArray("data").length() ; x++){
                                    items[x] = jsonObject.getJSONArray("data").getJSONObject(x).getString("Email");
                                    itemsID[x]  = jsonObject.getJSONArray("data").getJSONObject(x).getString("ID");
                                }


                              //  print(jsonObject.getJSONArray("data").getJSONObject(0).toString());

                               /* SharedPreferences.Editor editor = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                                editor.putString("location", response);
                                editor.apply();

                                Intent intent = new Intent( getContext(), MapsActivity.class );
                                startActivity(intent);*/



                            }

                        }else {
                            print(jsonObject.getString("error"));
                        }
                    } catch (JSONException e) { e.printStackTrace(); }

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { Log.i("Error123","Error :" + error.toString());
            }
        });

        mRequestQueue.add(mStringRequest);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        setmapData("");
        //
    }

    private void drawCircle(LatLng point){

        // Instantiating CircleOptions to draw a circle around the marker
        CircleOptions circleOptions = new CircleOptions();

        // Specifying the center of the circle
        circleOptions.center(point);

        // Radius of the circle
        circleOptions.radius(40000);

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
        Toast.makeText(getContext(),response,Toast.LENGTH_LONG).show();
    }
}