package com.example.TakeMe.Fragments;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sbag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;


public class DriverNotification extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private static final int MY_PERMISSIONS_REQUEST_SEND_SMS =0;
    private String phoneNo;
    private String message;
    private boolean sendsms = false;
    private String mParam1;
    private String mParam2;
    private RequestQueue mRequestQueue;
    private String ACTION_NOTIFICATION = "getNotificationD.php?id=";
    private String ACTION_DELETE_NOTIFICATION = "deleteRequest.php?id=";
    private String ACTION_UPDATE_REQUEST = "acceptDriverRequest.php?tID=";
    private String ACTION_COMPLETE_REQUEST = "completerequest.php?id=";
    private StringRequest mStringRequest;
    private String data ="{}";
    private String host ;
    private ListView listView;
    private String ID;
    private Button btnAction;
    private String MyPREFERENCES = "32145788";
    private int request = -1;
    private double latitude = 0;
    private double longitude = 0;

    private String [] status  = {"Waiting" , "Accepted","Collected" , "Delivered","Completed"};
    private String [] bntstep = {"ACCEPT"  , "COLLECTED","DELIVERED"    , "COMPLETED" ,"DELETE"};
    private String [] statusA   = {""       , "NAVIGATE"   ,"HOSPITALS" , "NAVIGATE","NAVIGATE"};
    public DriverNotification() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestNotification.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestNotification newInstance(String param1, String param2) {
        RequestNotification fragment = new RequestNotification();
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

        View view = inflater.inflate(R.layout.fragment_request_notification, container, false);
        listView= (ListView ) view.findViewById(R.id.lsNotification);

        SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        data = preferences.getString("data", "{}");//"No name defined" is the default value.
        host = preferences.getString("host", "http://192.168.1.33/takeme/");

       // sendSMSMessage("0670026063" , "sms");

        JSONArray jjsonArray = null;
        try {
            jjsonArray = new JSONArray(data);
            JSONObject jjsonObject = new JSONObject(jjsonArray.getJSONObject(0).toString());

            ID = jjsonObject.getString("ID");
            latitude = jjsonObject.getDouble("Latitude");
            longitude = jjsonObject.getDouble("Longitude");

            sendAndRequestResponse(ACTION_NOTIFICATION + ID);
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Inflate the layout for this fragment
        return view;
    }

    public interface OnFragmentInteractionListener {
    }

    public class CustomListAdapter extends BaseAdapter {
        private Context context; //context
        private ArrayList<com.example.TakeMe.Request> notification; //data source of the list adapter
        //public constructor
        public CustomListAdapter(Context context, ArrayList<com.example.TakeMe.Request> notification) {
            this.context = context;
            this.notification = notification;
        }

        @Override
        public int getCount() {
            return notification.size(); //returns total of items in the list
        }

        @Override
        public Object getItem(int position) {
            return notification.get(position); //returns list item at the specified position
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, final ViewGroup parent) {
            // inflate the layout for each list row
            if (convertView == null) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.notification, parent, false);
            }

            // get current item to be displayed
            com.example.TakeMe.Request currentItem = (com.example.TakeMe.Request) getItem(position);
            // get the TextView for item name and item description
            TextView tvOwner = (TextView) convertView.findViewById(R.id.tvOwner);
            TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
            TextView tvResponse = (TextView) convertView.findViewById(R.id.tvResponse);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);

            tvOwner.setText(notification.get(position).requester);
            tvType.setText(notification.get(position).type );
            tvResponse.setText(status[notification.get(position).status]);
            tvDate.setText(notification.get(position).date);

            final String nID = notification.get(position).ID+"";

            final Button btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
            btnAction = (Button) convertView.findViewById(R.id.btnAction);
            btnAction.setText(statusA[notification.get(position).status]);

            if(notification.get(position).status == 0){
                btnAction.setVisibility(View.INVISIBLE);
            }else {
                btnAction.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if(notification.get(position).status == 2)   {

                            final String url = "https://www.google.com/maps/search/hospital+near+me/@"
                                    + notification.get(position).latitude + ","
                                    + notification.get(position).longitude;
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                            startActivity(intent);

                        }else {
                            Uri navigation = Uri.parse("google.navigation:q=" + notification.get(position).latitude + "," +
                                    notification.get(position).longitude + "");
                            Intent navigationIntent = new Intent(Intent.ACTION_VIEW, navigation);
                            navigationIntent.setPackage("com.google.android.apps.maps");
                            startActivity(navigationIntent);
                        }

                    }
                });

            }

            btnDelete.setText(bntstep[notification.get(position).status ]);
            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    switch (notification.get(position).status) {
                        case 0:
                            //accept
                            sendAndRequestResponse(ACTION_UPDATE_REQUEST + ID + "&st=1&nID=" + nID);
                            break;
                        case 1:
                            sendAndRequestResponse(ACTION_UPDATE_REQUEST + ID + "&st=2&nID=" + nID);

                            break;
                        case 2:
                            sendAndRequestResponse(ACTION_UPDATE_REQUEST + ID + "&st=3&nID=" + nID);
                            break;
                        case 3:
                            sendsms = true;
                            sendAndRequestResponse(ACTION_UPDATE_REQUEST + ID + "&st=4&nID=" + nID);
                            break;
                        case 4:

                            sendAndRequestResponse(ACTION_DELETE_NOTIFICATION + nID);

                            break;
                    }

                }
            });


          /*  if(tvResponse.getText().toString().equals("Completed"))
                btnDelete.setText("Delete");

            if(tvResponse.getText().toString().equals("Waiting"))
                btnDelete.setVisibility(View.GONE);

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    if(btnDelete.getText().toString().equals("DONE")){
                        print("Completing Request...");
                        sendAndRequestResponse(ACTION_COMPLETE_REQUEST + nID + "&to=" + ID);
                    }else {
                        print("Delete Request...");
                        sendAndRequestResponse(ACTION_DELETE_NOTIFICATION + nID);
                    }
                }
            });*/

            // returns the view for the current row
            return convertView;
        }
    }

    protected void sendSMSMessage(String pn, String mx) {
        phoneNo = pn;
        message = mx;

        if (ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.SEND_SMS)
                != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                    Manifest.permission.SEND_SMS)) {
                sendMySMS();
            } else {
                ActivityCompat.requestPermissions(getActivity(),
                        new String[]{Manifest.permission.SEND_SMS},
                        MY_PERMISSIONS_REQUEST_SEND_SMS);
            }
        }else {
            sendMySMS();
        };
    }

    private void sendMySMS(){
        SmsManager smsManager = SmsManager.getDefault();
        smsManager.sendTextMessage(phoneNo, null, message, null, null);
        Toast.makeText(getContext(), "SMS sent.",
                Toast.LENGTH_LONG).show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String permissions[], int[] grantResults) {
        print(requestCode + "[" + permissions.toString() + "[" + grantResults);

        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_SEND_SMS: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    sendMySMS();
                } else {
                    Toast.makeText(getContext(),
                            "SMS faild, please try again.", Toast.LENGTH_LONG).show();
                    return;
                }
            }
        }

    }


    private void print(String position) {
        Toast.makeText(getContext(),position, Toast.LENGTH_SHORT).show();
    }

    private void sendAndRequestResponse(String url) {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(getContext());
        url = host + url;

        //String Request initialized
        mStringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response != null ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if( jsonObject.getString("result").equals("done")) {



                            if(jsonObject.getString("datatype").equals("notification")){

                                ArrayList<com.example.TakeMe.Request> itemsArrayList = new ArrayList<>(1); // calls function to get items list
                                request = -1;

                                Location gpslocationD;
                                gpslocationD = new Location("");
                                gpslocationD.setLatitude(latitude);
                                gpslocationD.setLongitude(longitude);

                                //JSONObject jsonObject = new JSONObject(dt);
                                JSONArray jsonArray = jsonObject.getJSONArray("data");

                                listView.setAdapter(null);
                                for(int x = 0 ; x < jsonObject.getJSONArray("data").length() ; x++) {

                                    Location gpslocation = new Location("");
                                    gpslocation.setLatitude(jsonArray.getJSONObject(x).getDouble("Latitude"));
                                    gpslocation.setLongitude(jsonArray.getJSONObject(x).getDouble("Longitude"));
                                    Float distance = ((gpslocation.distanceTo(gpslocationD)) / 1000) + 0.5f;

                                    if (distance <= 10) {

                                        String requester = "";

                                        String ipStatus = jsonArray.getJSONObject(x).getString("NSTATUS");
                                       // String driver = jsonArray.getJSONObject(x).getString("DNAME");
                                        String requesterN = jsonArray.getJSONObject(x).getString("RNAME");
                                       // String step= jsonArray.getJSONObject(x).getString("STEP");
                                        String fromN = jsonArray.getJSONObject(x).getString("PName");

                                       // status = jsonArray.getJSONObject(x).getString("NSTATUS");

                                        requester = requesterN + " requested for " + fromN;

                                        com.example.TakeMe.Request request = new com.example.TakeMe.Request(
                                                requester,
                                                "Ambulance Request",
                                                Integer.parseInt(ipStatus),
                                                jsonArray.getJSONObject(x).getString("Date"),
                                                jsonArray.getJSONObject(x).getString("NOTID"),
                                                jsonArray.getJSONObject(x).getDouble("Latitude"),
                                                jsonArray.getJSONObject(x).getDouble("Longitude"),
                                                "",
                                                jsonArray.getJSONObject(x).getString("NextOfKinCell")

                                        );

                                        if(request.status == 4 && sendsms){
                                            Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
                                            List<Address> addresses = null;
                                            try {
                                                addresses = geocoder.getFromLocation(request.latitude,  request.longitude, 1);
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }

                                           // addresses.get(0).

                                            String place = "";
                                            for (int i = 0 ; i < addresses.size() ; i++){
                                                place += addresses.get(i).getAddressLine(0) + ","+addresses.get(i).getAddressLine(1)+ ","+addresses.get(i).getAddressLine(3);
                                            }

                                            //String place = ( cityName + stateName + countryName).replace("," , "\r\n");
                                            place = place.replace("null", "");

                                            if(request.NextOfKinCell.length() != 10)
                                                print("Can't send SMS, invalid cell number:" + request.NextOfKinCell);
                                            else
                                            sendSMSMessage   (request.NextOfKinCell, fromN + " was delivered to " + place );

                                            sendsms = false;
                                        }

                                        itemsArrayList.add(request);
                                    }
                                }

                                CustomListAdapter adapter = new CustomListAdapter(getContext(), itemsArrayList);

                                listView.setAdapter(adapter);


                            }else if(jsonObject.getString("datatype").equals("deleteRequest")){
                                //  notification.get(pos).ID
                                if(request > -1)
                                    listView.setAdapter(null);
                                sendAndRequestResponse(ACTION_NOTIFICATION + ID);
                            }else if(jsonObject.getString("datatype").equals("acceptdriver")){
                                //  notification.get(pos).ID
                                sendAndRequestResponse(ACTION_NOTIFICATION + ID);
                            }

                        }else {
                            if(!jsonObject.getString("error").equals("-1"))
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


}
