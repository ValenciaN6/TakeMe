package com.example.TakeMe.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.TakeMe.MapsActivity;
import com.example.TakeMe.Request;
import com.example.sbag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestNotification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestNotification extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RequestQueue mRequestQueue;
    private String ACTION_NOTIFICATION = "getNotification.php?id=";
    private String ACTION_DELETE_NOTIFICATION = "deleteRequest.php?id=";
    private String ACTION_ACCEPT_FRIEND = "acceptFriend.php?id=";
    private StringRequest mStringRequest;
    private String data ="{}";
    private String host ;
    private ListView listView;
    private String ID;
    private Button btnAction;
    private String MyPREFERENCES = "32145788";
    private int request = -1;
    private String [] status  = {"Waiting" , "Accepted","Collected" , "Delivered","Completed"};
    private String [] bntstep = {"DELETE"  , "",""    , "" ,"DELETE"};
    private String [] statusA   = {""       , "TRACK"   ,"TRACK" , "",""};

    public RequestNotification() {
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


        host ="";


        SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE);
        data = preferences.getString("data", "{}");//"No name defined" is the default value.
        host = preferences.getString("host", "http://192.168.1.33/takeme/");

        JSONArray jjsonArray = null;
        try {
            jjsonArray = new JSONArray(data);
            JSONObject jjsonObject = new JSONObject(jjsonArray.getJSONObject(0).toString());

            ID = jjsonObject.getString("ID");

            sendAndRequestResponse(ACTION_NOTIFICATION + ID);
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Inflate the layout for this fragment
        return view;
    }

    public class CustomListAdapter extends BaseAdapter {
        private Context context; //context
        private ArrayList<Request> notification; //data source of the list adapter

        //public constructor
        public CustomListAdapter(Context context, ArrayList<Request> notification) {
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

            com.example.TakeMe.Request currentItem = (com.example.TakeMe.Request) getItem(position);
            // get the TextView for item name and item description
            TextView tvOwner = (TextView) convertView.findViewById(R.id.tvOwner);
            TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
            TextView tvResponse = (TextView) convertView.findViewById(R.id.tvResponse);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);

            tvOwner.setText(notification.get(position).requester);
            tvType.setText(notification.get(position).type);
            tvResponse.setText(status[notification.get(position).status] + notification.get(position).NextOfKinName);
            tvDate.setText(notification.get(position).date);

            final String nID = notification.get(position).ID + "";
            final String driverID = notification.get(position).toID + "";

            final Button btnDelete = (Button) convertView.findViewById(R.id.btnDelete);
            btnAction = (Button) convertView.findViewById(R.id.btnAction);
            btnAction.setText(statusA[notification.get(position).status]);


            btnDelete.setText(bntstep[notification.get(position).status]);
            btnAction.setText(statusA[notification.get(position).status]);


            if (statusA[notification.get(position).status].equals("")) {
                btnAction.setVisibility(View.INVISIBLE);
            }

            if (notification.get(position).type.equals("Friend request")){
                btnAction.setVisibility(View.INVISIBLE);
                if(notification.get(position).toID.equals(ID)){

                    if(notification.get(position).status == 0){
                        btnDelete.setText("Accept");
                    }else {
                        btnDelete.setText("DELETE");
                    }
                }else{
                    btnDelete.setText("DELETE");
                }

             }else {

            }

            if(notification.get(position).toID.equals(ID)){

                if(notification.get(position).status == 0){
                    btnDelete.setText("Accept");
                }else {
                    btnDelete.setText("DELETE");
                }
            }

            if(btnDelete.getText().toString().equals("")){
                btnDelete.setVisibility(View.INVISIBLE);
            }

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(btnDelete.getText().toString() .toUpperCase(). equals("ACCEPT")){

                        print("Accepting Request....");
                        sendAndRequestResponse(ACTION_ACCEPT_FRIEND + nID);
                    }else if(btnDelete.getText().toString().toUpperCase() . equals("DELETE")){
                        print("Deleting Request....");
                        sendAndRequestResponse(ACTION_DELETE_NOTIFICATION + nID);
                    }

                }
            });

            //

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { //driverID

                    if(btnAction.getText().toString().toString().toUpperCase().equals("TRACK")) {

                        SharedPreferences.Editor editor = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                        editor.putString ("driverID", driverID );
                        editor.commit();
                        Intent intentf = new Intent(getContext(), MapsActivity.class);

                        startActivity(intentf);



                    }


                }
            });




/*

            btnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendAndRequestResponse(ACTION_DELETE_NOTIFICATION + nID);

                }
            });*/

            // returns the view for the current row
            return convertView;
        }
    }
    private void print(String position) {
        Toast.makeText(getContext(),position, Toast.LENGTH_SHORT).show();
    }

    private void sendAndRequestResponse(String url) {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(getContext());
        url = host + url;
       // print(url);

        //String Request initialized
        mStringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<String>() {
            @Override


            public void onResponse(String response) {
//print(response);
                if(response != null ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if( jsonObject.getString("result").equals("done")) {

                             if(jsonObject.getString("datatype").equals("notification")){

                                 ArrayList<Request> itemsArrayList = new ArrayList<>(1); // calls function to get items list
                                 request = -1;

                                for(int x = 0 ; x < jsonObject.getJSONArray("data").length() ; x++){

                                    String status = "";
                                    String requester = "";
                                    int ipStatus = jsonObject.getJSONArray("data").getJSONObject(x).getInt("NSTATUS");
                                    String driver   = jsonObject.getJSONArray("data").getJSONObject(x).getString("DNAME");
                                    String ipType   = jsonObject.getJSONArray("data").getJSONObject(x).getString("NTYPE");
                                    String requesterN     = jsonObject.getJSONArray("data").getJSONObject(x).getString("RNAME");
                                    String to       = jsonObject.getJSONArray("data").getJSONObject(x).getString("TNAME");
                                    String toID       = jsonObject.getJSONArray("data").getJSONObject(x).getString("TOID");
                                    String fromN  = jsonObject.getJSONArray("data").getJSONObject(x).getString("PName");

                                   // status = jsonObject.getJSONArray("data").getJSONObject(x).getString("NSTATUS");
                                    String statuslabel = "";

                                   // print(ipType);

                                    if(ipType.equals("Ambulance request")){
                                        requester = requesterN + " requested for " + fromN;
                                        if(ipStatus == 0){

                                        }else {
                                            statuslabel = " by " + driver;
                                         }


                                    }else if(ipType.equals("Friend request")) {
                                        requester = "To " + to;

                                        if(ipStatus == 0){

                                        }else {
                                            statuslabel = " by " + to;
                                        }

                                    }


/*
                                    if(ipType.equals("Ambulance request")){
                                        requester = requesterN + " requested for " + fromN;

                                        if (ipStatus.equals("Accepted")) {
                                            status = driver + " Accepted";

                                        }else {
                                            action = "";
                                        }

                                    }else if(ipType.equals("Friend request")) {

                                        requester = "To " + to;
                                        action = "";


                                        if (ipStatus.equals("Accepted")) {
                                            status = to + " Accepted";

                                        }

                                        if(toID.equals(ID)){
                                         //   action = "ACCEPT";
                                            requester = "From " + fromN;
                                            action = "ACCEPT";

                                            if (ipStatus.equals("Accepted")) {
                                                status = "I Accepted";
                                                action = "";
                                            }
                                        }
                                    }
*/
                                    double lat = 0, lon = 0;
                                    if(!jsonObject.getJSONArray("data").getJSONObject(x).getString("Latitude").equals("null")){

                                        lat =  jsonObject.getJSONArray("data").getJSONObject(x).getDouble("Latitude");
                                        lon =  jsonObject.getJSONArray("data").getJSONObject(x).getDouble("Longitude");
                                    }


                                    Request  request = new Request(
                                            requester,
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("NTYPE"),
                                            ipStatus,
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("Date"),
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("NOTID"),
                                            lat,
                                            lon,
                                            statuslabel,
                                            "",
                                            toID

                                    );

                                    itemsArrayList.add(request);
                                }

                                 CustomListAdapter adapter = new CustomListAdapter(getContext(), itemsArrayList);

                                 listView.setAdapter(adapter);


                            }else if(jsonObject.getString("datatype").equals("deleteRequest")){
                              //  notification.get(pos).ID
                                 if(request > -1)
                                     listView.setAdapter(null);
                                 sendAndRequestResponse(ACTION_NOTIFICATION + ID);
                            }else if(jsonObject.getString("datatype").equals("acceptfriend")){
                                 //  notification.get(pos).ID
                                 sendAndRequestResponse(ACTION_NOTIFICATION + ID);
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


}
