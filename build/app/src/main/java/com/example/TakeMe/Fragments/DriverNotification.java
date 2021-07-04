package com.example.TakeMe.Fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DriverNotification.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DriverNotification#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DriverNotification extends Fragment {

   // private boolean ondotimer = true;
    private int temp;
    private String id;
    private String data;
    private SeekBar seekBar;
    private String host;
    private String MyPREFERENCES = "321qwe";
    private String ACTION_UPADATE_TEMP = "updateMtemp.php?user=";
    private String ACTION_ADD_DELETE_SYMPTOM = "addDeletSymptom.php?user=";
    private TextView tvTemp;
    private RequestQueue mRequestQueue;
    private StringRequest mStringRequest;
    private SymptomAdapter mAdapter;
    private OnFragmentInteractionListener mListener;

    public DriverNotification() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static DriverNotification newInstance(String param1, String param2) {
        DriverNotification fragment = new DriverNotification();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SharedPreferences sharedpreferences;
        sharedpreferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        id   = sharedpreferences.getString("id","0");
        host = sharedpreferences.getString("host","http://192.168.0.116/thermo/");
       // ondotimer = true;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_update, container, false);
        tvTemp = (TextView)view.findViewById(R.id.tvTemp);

        SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        data = preferences.getString("data", "");//"No name defined" is the default value
        seekBar = (SeekBar) view.findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress,
                                          boolean fromUser) {
                temp = progress;
                tvTemp.setText(progress + "°C");
                String url = ACTION_UPADATE_TEMP + id + "&temp=" +  progress;
                sendAndRequestResponse(url);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) { }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) { }
        });

        // Inflate the layout for this fragment

        String sList = "1,Fever;2,Dry cough;3,Tiredness;4,Aches and pains;5,Sore throat;6,Diarrhoea;7,Conjunctivitis;8,Headache;9,Loss of taste or smell;10,A rash on skin or discolouration of fingers or to;11,Difficulty breathing or shortness of breath;12,Chest pain or pressure;13,Loss of speech or movement;14,Blue fart";
        String [] sArray = sList.split(";");
        ListView listView = (ListView)view.findViewById(R.id.lvSymptoms);

        String [] Exissymptoms = new String[0];
        try {
            JSONObject jsonObject;
            jsonObject = new JSONObject(data);

            JSONArray jsonArray = jsonObject.getJSONArray("data");
            temp = Integer.parseInt( jsonArray.getJSONObject(0).getString("mtemp"));

            seekBar.setProgress(temp);
            tvTemp.setText(temp + "°C");

            if( jsonObject.getString("result").equals("done")) {

                Exissymptoms = jsonObject.getString("smptoms").split(",");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ArrayList<Symptom> Symptoms = new ArrayList<Symptom>();
        for(int x = 0; x < sArray.length ; x++) {
            String [] dataArray = sArray[x].split(",");
            boolean check = false;
            for(int i = 0; i < Exissymptoms.length ; i++){
                if (dataArray[1].toUpperCase().equals(Exissymptoms[i].toUpperCase())){
                    check = true;
                }

            }
            Symptoms.add(new Symptom(dataArray[1] , dataArray[0] , check));
        }

        mAdapter = new SymptomAdapter(getContext(),Symptoms);
        listView.setAdapter(mAdapter);

        return view;
    }

    public class SymptomAdapter extends ArrayAdapter<Symptom> {

        private Context mContext;
        private List<Symptom> SymptomsList = new ArrayList<>();

        public SymptomAdapter(@NonNull Context context,  ArrayList<Symptom> list) {
            super(context, 0 , list);
            mContext = context;
            SymptomsList = list;
        }

        @NonNull
        @Override
        public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            View listItem = convertView;
            if(listItem == null)
                listItem = LayoutInflater.from(mContext).inflate(R.layout.list_layout_s,parent,false);

            Symptom currentSymptom = SymptomsList.get(position);

            final Switch aSwitch = (Switch) listItem.findViewById(R.id.switch1);
            TextView textView = (TextView) listItem.findViewById(R.id.tvDsc);

            aSwitch.setChecked(SymptomsList.get(position).exist);
            aSwitch.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    String url = ACTION_ADD_DELETE_SYMPTOM + id +
                                 "&symp=" +
                                 SymptomsList.get(position).ID +
                                 "&action=" +
                                 aSwitch.isChecked() ;
                    sendAndRequestResponse(url);
                }
            });
            textView.setText(SymptomsList.get(position).mName);
            
            return listItem;
        }
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

    private void print(String s) {
        Toast.makeText(getContext(),s, Toast.LENGTH_SHORT).show();
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public class Symptom {

        private String mName;
        private String ID;
        private boolean exist;

        // Constructor that is used to create an instance of the Symptom object
        public Symptom( String mName, String id, boolean exist) {

            this.mName = mName;
            this.ID = id;
            this.exist = exist;
        }

        public String getmName() {
            return mName;
        }

        public void setmName(String mName) {
            this.mName = mName;
        }

        public boolean getExist() {
            return exist;
        }

        public void setExist(boolean exist) {
            this.exist = exist;
        }

        public String getmID() {
            return ID;
        }

    }
}
