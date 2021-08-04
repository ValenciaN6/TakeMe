package com.example.TakeMe.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.TakeMe.Friend;
import com.example.TakeMe.Request;
import com.example.TakeMe.User;
import com.example.sbag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RequestSettings#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RequestSettings extends Fragment implements SearchView.OnQueryTextListener{

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String MyPREFERENCES = "32145788";
    private RequestQueue mRequestQueue;
    private String ACTION_FRIEND = "getFriend.php?id=";
    private String ACTION_DELETE_FRIEND = "deleteFriend.php?id=";
    private String ACTION_FRIEND_REQUEST = "friendRequest.php?fm=";  //
    private String ACTION_ALL_USERS      = "getallusers.php?id=";
    private StringRequest mStringRequest;
    private String data ="{}";
    private String host ;
    private ListView listView;
    private String ID;
    private EditText edtEmail;

    private ListView listuser;
    private ListViewAdapter adapteruser;
    private SearchView editsearch;
    private ArrayList<User> arrayuserlist = new ArrayList<User>();


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
        View view = inflater.inflate(R.layout.fragment_request_settings, container, false);
        listView= (ListView) view.findViewById(R.id.lsFriend);
        listuser = (ListView)view.findViewById(R.id.listview);
        editsearch = (SearchView)view. findViewById(R.id.search);

        listuser.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                print( "Sending a friend request to " + arrayuserlist.get(position).name);
                sendAndRequestResponse(ACTION_FRIEND_REQUEST + ID + "&em=" + arrayuserlist.get(position).email);


            }
        });


        SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        data = preferences.getString("data", "{}");//"No name defined" is the default value.
        host = preferences.getString("host", "http://192.168.1.33/takeme/");


        JSONArray jjsonArray = null;
        try {
            jjsonArray = new JSONArray(data);
            JSONObject jjsonObject = new JSONObject(jjsonArray.getJSONObject(0).toString());

            ID = jjsonObject.getString("ID");
            sendAndRequestResponse(ACTION_FRIEND + ID);
            ;
        } catch (JSONException e) {
            e.printStackTrace();
        }


        editsearch.setOnQueryTextListener(this);

        // Inflate the layout for this fragment
        return view;
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        String text = newText;
        if( adapteruser != null)
            adapteruser.filter(text);
        return false;
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



            Button btnAction = (Button) convertView.findViewById(R.id.btnDelete);
            final String nID = friends.get(pos).ID+"";
            btnAction.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    sendAndRequestResponse(ACTION_DELETE_FRIEND + nID);

                }
            });

            // returns the view for the current row
            return convertView;
        }
    }


    public class ListViewAdapter extends BaseAdapter {

        // Declare Variables

        Context mContext;
        LayoutInflater inflater;
        private List<User> animalNamesList = null;
        private ArrayList<User> arraylist;


        public ListViewAdapter(Context context, List<User> animalNamesList ) {
            mContext = context;
            this.animalNamesList = animalNamesList;
            inflater = LayoutInflater.from(mContext);
            this.arraylist = new ArrayList<User>();
            this.arraylist.addAll(animalNamesList);
        }

        public class ViewHolder {
            TextView name;
           // TextView name;
        }

        @Override
        public int getCount() {
            return animalNamesList.size();
        }

        @Override
        public User getItem(int position) {
            return animalNamesList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        public View getView(final int position, View view, ViewGroup parent) {
            final ViewHolder holder;
            if (view == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.list_view_items, null);
                // Locate the TextViews in listview_item.xml
                holder.name = (TextView) view.findViewById(R.id.name);
                view.setTag(holder);
            } else {
                holder = (ViewHolder) view.getTag();
            }
            // Set the results into TextViews
            holder.name.setText(animalNamesList.get(position).name + " " + animalNamesList.get(position).surname  + " "
                    + animalNamesList.get(position).email );
            return view;
        }

        // Filter Class
        public void filter(String charText) {

            if(animalNamesList != null && arraylist != null) {
                charText = charText.toLowerCase(Locale.getDefault());

                animalNamesList.clear();
                if (charText.length() == 0) {
                    animalNamesList.addAll(arraylist);
                } else {
                    for (User wp : arraylist) {
                        if (wp.name.toLowerCase(Locale.getDefault()).contains(charText)) {
                            animalNamesList.add(wp);
                        }
                    }
                }
            }
            notifyDataSetChanged();
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

               // print(response);

                if(response != null ) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if( jsonObject.getString("result").equals("done")) {

                            if(jsonObject.getString("datatype").equals("allusers")) {

                                for(int x = 0 ; x < jsonObject.getJSONArray("data").length() ; x++){

                                    User  user = new User(
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("ID"),
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("Name") ,
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("Surname"),
                                            jsonObject.getJSONArray("data").getJSONObject(x).getString("Email")


                                    );

                                    arrayuserlist.add(user);
                                }

                                adapteruser = new ListViewAdapter(getContext(), arrayuserlist);

                                listuser.setAdapter(adapteruser);

                            }else
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

                                RequestSettings.CustomListAdapter adapter = new CustomListAdapter(getContext(), itemsArrayList);

                                listView.setAdapter(adapter);


                            }else
                            if(jsonObject.getString("datatype").equals("deleteFriend")){
                                //  notification.get(pos).ID
                                sendAndRequestResponse(ACTION_FRIEND + ID);
                            }else
                            if(jsonObject.getString("datatype").equals("friendrequest")){
                                //  notification.get(pos).ID
                                print("Request sent");
                            }

                        }else {
                            if(!jsonObject.getString("error").equals("-1"))
                            print(jsonObject.getString("error"));
                        }

                        if(jsonObject.getString("datatype").equals("friend")) {

                            sendAndRequestResponse(ACTION_ALL_USERS + ID);
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