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
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.TakeMe.Request;
import com.example.sbag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
    private StringRequest mStringRequest;
    private String data ="{}";
    private String host ;
    private ListView listView;
    private String ID;

    private String MyPREFERENCES = "32145788";

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
        listView= (ListView                ) view.findViewById(R.id.lsNotification);


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
            sendAndRequestResponse(ACTION_NOTIFICATION + "9");
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
        private int pos = 0;

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
        public View getView(int position, View convertView, final ViewGroup parent) {
            // inflate the layout for each list row
            if (convertView == null) {
                convertView = LayoutInflater.from(context).
                        inflate(R.layout.notification, parent, false);
            }

            // get current item to be displayed
            Request currentItem = (Request) getItem(position);

            // get the TextView for item name and item description
            TextView tvOwner = (TextView) convertView.findViewById(R.id.tvOwner);
            TextView tvType = (TextView) convertView.findViewById(R.id.tvType);
            TextView tvResponse = (TextView) convertView.findViewById(R.id.tvResponse);
            TextView tvDate = (TextView) convertView.findViewById(R.id.tvDate);

            tvOwner.setText(notification.get(position).requester);
            tvType.setText(notification.get(position).type);
            tvResponse.setText(notification.get(position).response);
            tvDate.setText(notification.get(position).date);
            pos = position;

            Button btnView = (Button) convertView.findViewById(R.id.btnView);

            btnView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    print("View notification");
                }
            });

            Button btnAction = (Button) convertView.findViewById(R.id.btnAction);

            btnAction.setText(notification.get(position).action);

            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(notification.get(pos).action.equals("DELETE")){
                        print("Deleting this notification");
                    }
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


                             if(jsonObject.getString("datatype").equals("notification")){


                                 ArrayList<Request> itemsArrayList = new ArrayList<>(1); // calls function to get items list

                                for(int x = 0 ; x < jsonObject.getJSONArray("data").length() ; x++){

                                    String status = "";
                                    if (jsonObject.getJSONArray("data").getJSONObject(x).getString("NSTATUS").equals("Accepted")) {
                                         status = jsonObject.getJSONArray("data").getJSONObject(x).getString("DNAME") + " Accepted";
                                    }
                                    else {
                                         status = jsonObject.getJSONArray("data").getJSONObject(x).getString("NSTATUS");
                                    }

                                    Request  request = new Request(
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("RNAME") +
                                                    " requested for " +
                                                    jsonObject.getJSONArray("data").getJSONObject(x).getString("PName"),
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("NTYPE"),
                                            status,
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("Date"),
                                            "Delete",
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("NOTID")






                                    );

                                    itemsArrayList.add(request);
                                }

                                 CustomListAdapter adapter = new CustomListAdapter(getContext(), itemsArrayList);

                                 listView.setAdapter(adapter);


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



                   /*     items = new CharSequence[jsonObject.getJSONArray("data").length()];
                                itemsID = new String[jsonObject.getJSONArray("data").length()];
                                 {"result":"done","data":[{"0":"5","NOTID":"5","1":"9","PID":"9","2":"request","NTYPE":"request",
                                 "3":"1","NSTATUS":"1","4":"2021-06-19 13:52:59","Date":"2021-06-19 13:52:59","5":"babawam",
                                 "PName":"babawam","6":"Gogo","RNAME":"Gogo","7":null,"DNAME":null},{"0":"6","NOTID":"6","1":"9",
                                 "PID":"9","2":"request","NTYPE":"request","3":"waiting","NSTATUS":"waiting","4":"2021-06-19 15:57:55"
                                 ,"Date":"2021-06-19 15:57:55","5":"babawam","PName":"babawam","6":"Gogo","RNAME":"Gogo","7":"","DNAME":"
                                 "}],"datatype":"notification"}*/