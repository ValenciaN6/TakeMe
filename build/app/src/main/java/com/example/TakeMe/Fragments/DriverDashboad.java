package com.example.TakeMe.Fragments;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.TakeMe.MapsActivity;
import com.example.sbag.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.content.Context.MODE_PRIVATE;


public class DriverDashboad extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String MyPREFERENCES = "32145788";
    private String data;
    private String[] symptoms = { "Suresh Dasari", "Rohini Alavala", "Trishika Dasari", "Praveen Alavala", "Madav Sai", "Hamsika Yemineni"};
    private SharedPreferences.Editor editor;
    private String [] weightlist = {};
    private String mParam1;
    private String mParam2;

    private Thread t;
    private boolean ondotimer = true;
    private Handler handler = new Handler();

    private OnFragmentInteractionListener mListener;
    private ListView mListView;
    private ArrayAdapter aAdapter;
    private TextView tvDate, tvAutoTemp,tvManualTemp;
    private Button  btnMapDoctors;
    private ProgressBar weightBar;
    TextView tvStatus;

    private String temp = "0,0";
    private String ACTION_GETUSER = "getUser.php?id=";
    private String host ;
    private StringRequest mStringRequest;
    private RequestQueue mRequestQueue;
    private String id;
    private String ACTION_GETLOCATION = "getlocation.php?id=";

    public DriverDashboad() {
        // Required empty public constructor
    }

    public static DriverDashboad newInstance(String param1, String param2) {
        DriverDashboad fragment = new DriverDashboad();
        Bundle args = new Bundle();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public void onPause() {
        super.onPause();
        ondotimer = false;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_dashboad, container, false);

        ondotimer      = true;
        mListView      = (ListView)view.findViewById(R.id.lvSymptoms);
        tvDate         = (TextView)view.findViewById(R.id.tvDate);
        tvStatus       = (TextView)view.findViewById(R.id.tvStatus);
        tvAutoTemp     = (TextView)view.findViewById(R.id.tvAutoTemp);
        tvManualTemp          = (TextView)view.findViewById(R.id.tvManualTemp);
        Button  btnMap        = (Button)view.findViewById(R.id.btnMapMembers);
        btnMapDoctors = (Button)view.findViewById(R.id.btnMapDoctors);
        weightBar = (ProgressBar)view.findViewById(R.id.progressBar2);

        editor =  this.getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                if(weightlist.length > -1)
                print("[" + weightlist[position] + "]");
            }
        });

        btnMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            if(id != null)
                if(!id.equals("0"))
                    sendAndRequestResponse(ACTION_GETLOCATION + id);


            }
        });


        SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        data = preferences.getString("data", "{}");//"No name defined" is the default value.
        host = preferences.getString("host", "http://192.168.1.33/takeme/");
        id = preferences.getString("id", "0");
        try {
            JSONArray jjsonArray = new JSONArray(data);
            JSONObject jjsonObject = new JSONObject(jjsonArray.getJSONObject(0).toString());
             ;

             print(jjsonObject.getString("UserType"));


            update();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        t = new Thread(new Runnable() {
            @Override
            public void run() {

            handler.postDelayed(new Runnable() {
                @Override
                public void run() {

                    if(ondotimer) {
                        String url = ACTION_GETUSER + id;
                        sendAndRequestResponse(url);
                    }
               handler.postDelayed(this,10000);
                }
                },1);
            }
        });
        t.start();

        return view;
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

                        if(jsonObject.getString("datatype").equals("user")) { //

                            JSONArray jsonArray = jsonObject.getJSONArray("data");
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                            editor.putString("lon", jsonArray.getJSONObject(0).getString("lon") );
                            editor.putString("lat", jsonArray.getJSONObject(0).getString("lat") );
                            editor.putString ("data", response );
                            data = response;
                            editor.apply();



                            final String url = "https://www.google.com/maps/search/doctors+near+me/@"
                                    + jsonArray.getJSONObject(0).getDouble("lon") + ","
                                    + jsonArray.getJSONObject(0).getDouble("lon")
                                    + ",10z/data=!3m1!4b1";

                            //if(btnMapDoctors.getVisibility()!= View.VISIBLE)
                            btnMapDoctors.setVisibility(View.VISIBLE);

                            btnMapDoctors.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                                    startActivity(intent);
                                }
                            });


                            data = response;
                            update();
                        }else if(jsonObject.getString("datatype").equals("location")){
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                            editor.putString("location", response);
                            editor.apply();

                            Intent intent = new Intent( getContext(), MapsActivity.class );
                            startActivity(intent);

                        }

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


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }


    private void update() throws JSONException {

        Date today = new Date(); // Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("EEEE  d yyyy");

        JSONObject jsonObject;
        jsonObject = new JSONObject(data);
        if( jsonObject.getString("result").equals("done")) {

            if(jsonObject.getString("smptoms").length() > 2){
                symptoms = jsonObject.getString("smptoms").split(",");
                aAdapter       = new ArrayAdapter(getContext(), R.layout.list_layout, R.id.list_content, symptoms);

                mListView.setAdapter(aAdapter);

            }
            JSONArray jsonData = jsonObject.getJSONArray("data");
            tvManualTemp.setText(jsonData.getJSONObject(0).getString("mtemp") + "°C");
            tvAutoTemp.setText(jsonData.getJSONObject(0).getString("atemp") + "°C");

            if(jsonObject.getString("weight").length()> 2)
                weightlist = jsonObject.getString("weight").split(",");

            int sumWeight = 0;

            if(((jsonData.getJSONObject(0).getDouble( "mtemp")
                 +
                 jsonData.getJSONObject(0).getDouble( "atemp")
                 ) /
                  2
                ) >= 38){
                sumWeight = 3;
            }


            for(int x = 0; x < weightlist.length ; x++){
                sumWeight = sumWeight + Integer.parseInt(weightlist[x]);
            }

            if(sumWeight>=10)
                tvStatus.setText("YES");
            else
                tvStatus.setText("NO");
            weightBar.setProgress(sumWeight);

        }

        tvDate.setText(df.format(today));

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

  /*  private void sendAndRequestResponse(String url) {

        mRequestQueue = Volley.newRequestQueue(getContext());

        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                if(response != null ) {

                    JSONObject jsonObject;
                    try {
                        jsonObject = new JSONObject(response);
                        if( jsonObject.getString("result").equals("done")) {
                            SharedPreferences.Editor editor = getActivity().getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                            editor.putString ("data", response );
                            editor.apply();
                            data = response;

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }




                    try {

                        update();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }else {

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
*/

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void print(String position) {
        Toast.makeText(getContext(),position, Toast.LENGTH_SHORT).show();
    }
}
