package com.example.healthcare.Fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.example.healthcare.R;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link com.example.healthcare.Fragments.Dashboad.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link com.example.healthcare.Fragments.Dashboad#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Dashboad extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String[] symptoms = { "Suresh Dasari", "Rohini Alavala", "Trishika Dasari", "Praveen Alavala", "Madav Sai", "Hamsika Yemineni"};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private ListView mListView;
    private ArrayAdapter aAdapter;
    private TextView tvDate, tvAutoTemp,tvManualTemp;

    private String temp = "0,0";

    public Dashboad() {
        // Required empty public constructor
    }


    public static com.example.healthcare.Fragments.Dashboad newInstance(String param1, String param2) {
        com.example.healthcare.Fragments.Dashboad fragment = new com.example.healthcare.Fragments.Dashboad();
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
        View view = inflater.inflate(R.layout.fragment_dashboad, container, false);

        mListView      = (ListView)view.findViewById(R.id.lvSymptoms);
        tvDate         = (TextView)view.findViewById(R.id.tvDate);
        tvAutoTemp     = (TextView)view.findViewById(R.id.tvAutoTemp);
        tvManualTemp   = (TextView)view.findViewById(R.id.tvManualTemp);
        aAdapter       = new ArrayAdapter(getContext(), R.layout.list_layout, R.id.list_content, symptoms);

        update();
        mListView.setAdapter(aAdapter);
        // Inflate the layout for this fragment
        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    private void update(){

        Date today = new Date(); // Calendar.getInstance().getTime();
        DateFormat df = new SimpleDateFormat("EEEE  d yyyy");

        tvDate.setText(df.format(today));

        String[] dataToList = temp.split(",");
        tvManualTemp.setText(dataToList[0] + "°C");
        tvAutoTemp.setText(dataToList[1] + "°C");

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
}
