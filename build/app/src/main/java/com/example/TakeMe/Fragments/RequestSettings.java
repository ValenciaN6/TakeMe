package com.example.TakeMe.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.TakeMe.Friend;
import com.example.TakeMe.Request;
import com.example.sbag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestSettings extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String MyPREFERENCES = "32145788";
    private RequestQueue mRequestQueue;
    private String ACTION_FRIEND = "getFriend.php?id=";
    private String ACTION_DELETE_FRIEND = "deleteFriend.php?id=";
    private StringRequest mStringRequest;
    private String data ="{}";
    private String host ;
    private ListView listView;
    private String ID;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RequestSettings() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RequestSettings.
     */
    // TODO: Rename and change types and number of parameters
    public static RequestSettings newInstance(String param1, String param2) {
        RequestSettings fragment = new RequestSettings();
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
        listView= (ListView) view.findViewById(R.id.lsNotification);


        SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        data = preferences.getString("data", "{}");//"No name defined" is the default value.
        host = preferences.getString("host", "http://192.168.1.33/takeme/");

//print(data);
        JSONArray jjsonArray = null;
        try {
            jjsonArray = new JSONArray(data);
            JSONObject jjsonObject = new JSONObject(jjsonArray.getJSONObject(0).toString());

            ID = jjsonObject.getString("ID");
            print(host);
            sendAndRequestResponse(ACTION_FRIEND + ID);
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Inflate the layout for this fragment
        return view;
    }


    public class CustomListAdapter extends BaseAdapter {
        private Context context; //context
        private ArrayList<Friend> friends; //data source of the list adapter
        private int pos = 0;

        //public constructor
        public CustomListAdapter(Context context, ArrayList<Friend> friends) {
            this.context = context;
            this.friends = friends;
        }

        @Override
        public int getCount() {
            return friends.size(); //returns total of items in the list
        }

        @Override
        public Object getItem(int position) {
            return friends.get(position); //returns list item at the specified position
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, final ViewGroup parent) {
            // inflate the layout for each list row
            if (convertView == null) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.friend, parent, false);
            }

            // get current item to be displayed
            Friend currentItem = (Friend) getItem(position);

            // get the TextView for item name and item description
            TextView tvOwner = (TextView) convertView.findViewById(R.id.tvOwner);
            TextView tvName = (TextView) convertView.findViewById(R.id.tvName);
            TextView tvSurname = (TextView) convertView.findViewById(R.id.tvSurname);

            tvOwner.setText(friends.get(position).Email);
            tvName.setText(friends.get(position).Name);
            tvSurname.setText(friends.get(position).Surname);

            pos = position;



            Button btnAction = (Button) convertView.findViewById(R.id.btnAction);

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendAndRequestResponse(ACTION_DELETE_FRIEND + friends.get(pos).ID);


                }
            });

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

        print(url);
        //String Request initialized
        mStringRequest = new StringRequest(com.android.volley.Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {


                if(response != null ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if( jsonObject.getString("result").equals("done")) {


                            if(jsonObject.getString("datatype").equals("friend")){


                                ArrayList<Friend> itemsArrayList = new ArrayList<>(1); // calls function to get items list

                                for(int x = 0 ; x < jsonObject.getJSONArray("data").length() ; x++){

                                    Friend  friend = new Friend(
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("Name") ,
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("Surname"),

                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("Email"),

                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("ID")

                                    );

                                    itemsArrayList.add(friend);
                                }

                                RequestSettings.CustomListAdapter adapter = new RequestSettings.CustomListAdapter(getContext(), itemsArrayList);

                                listView.setAdapter(adapter);


                            }else if(jsonObject.getString("datatype").equals("deleteFriend")){
                                //  notification.get(pos).ID
                                sendAndRequestResponse(ACTION_FRIEND + ID);
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