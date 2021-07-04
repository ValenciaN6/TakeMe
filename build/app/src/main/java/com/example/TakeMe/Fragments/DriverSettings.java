package com.example.TakeMe.Fragments;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.sbag.R;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.ValueDependentColor;
import com.jjoe64.graphview.series.BarGraphSeries;
import com.jjoe64.graphview.series.DataPoint;

import org.eazegraph.lib.charts.PieChart;
import org.eazegraph.lib.models.PieModel;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import lecho.lib.hellocharts.model.PieChartData;
import lecho.lib.hellocharts.model.SliceValue;

public class DriverSettings extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String ACTION_GRAPH = "getSymptomsCounter.php";

    String [] symptosArray ;
    String [] provinceArray = {"Eastern Cape","Free State","Gauteng","KwaZulu-Natal","Limpopo","Mpumalanga","Northern Cape","North West","Western Cape"};
    private RequestQueue mRequestQueue;
    private String MyPREFERENCES = "321qwe";
    private String host = "http://hlulanimab.co.za/thermo/";
    private StringRequest mStringRequest;
    private GraphView graph;
    private PieChart pieChart ;
    // TODO: Rename and ch ange types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private TextView tvSymptomlink;
    private TextView tvProvincelink;

    public DriverSettings() {
        // Required empty public constructor
    }

    public static DriverSettings newInstance(String param1, String param2) {
        DriverSettings fragment = new DriverSettings();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final   String url = ACTION_GRAPH ;

        try {
            sendAndRequestResponse(url);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_settings, container, false);

        SharedPreferences preferences = this.getActivity().getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        host = preferences.getString("host", "http://192.168.0.116/thermo/");

        print(host);

        graph = (GraphView)view. findViewById(R.id.graph);
        GraphView graphTemp = (GraphView)view. findViewById(R.id.graphTemp);
        tvSymptomlink = (TextView)view.findViewById(R.id.tvSymptomlink);
        tvProvincelink = (TextView)view.findViewById(R.id.tvProvincelink);
        pieChart = view.findViewById(R.id.piechart);

        List<SliceValue> pieData = new ArrayList<>();


        pieData.add(new SliceValue(15, Color.BLUE).setLabel("Q1: $10"));
        pieData.add(new SliceValue(25, Color.GRAY).setLabel("Q2: $4"));
        pieData.add(new SliceValue(10, Color.RED).setLabel("Q3: $18"));
        pieData.add(new SliceValue(60, Color.MAGENTA).setLabel("Q4: $28"));

        PieChartData pieChartData = new PieChartData(pieData);


        LinearLayout linearLayout = (LinearLayout) view.findViewById(R.id.layout);

        // Add textview 1
        TextView textView1 = new TextView(getContext());
        textView1.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT));
        textView1.setText("programmatically created TextView1");
        textView1.setBackgroundColor(0xff66ff66); // hex color 0xAARRGGBB
        textView1.setPadding(20, 20, 20, 20);// in pixels (left, top, right, bottom)
        linearLayout.addView(textView1);

/*
        pieChart.addPieSlice(
                new PieModel(
                        "R",
                        16,
                        R.color.Blue).setLegendLabel("GP"));


*/

        pieChart.addPieSlice(
                new PieModel("Python", 6, Color.parseColor("#66BB6A"))

        );


        pieChart.addPieSlice(
                new PieModel(
                        "Python",
                        6,
                        Color.parseColor("#66BB6A")));
        pieChart.addPieSlice(
                new PieModel(
                        "C++",
                        21,
                        Color.parseColor("#EF5350")));
        pieChart.addPieSlice(
                new PieModel(
                        "Java",
                        2,
                        Color.parseColor("#29B6F6")));
        // initGraphP(graphTemp);
        pieChart.startAnimation();

        return view;
    }

    private void print(String position) {
        Toast.makeText(getContext(),position, Toast.LENGTH_SHORT).show();
    }
    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(getContext(), Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            String country = addresses.get(0).getCountryName();

            strAdd = "address:" + address + "\n"
                   + "state:" + state + "\n"
                    + "zip:" + zip + "\n"
                    + "country:" + country + "\n";

            print("city:" + city);
        } catch (Exception e) {
            e.printStackTrace();
           //
        }
        return strAdd;
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

                        if( jsonObject.getString("datatype").equals("symptomsCounter")){
                            symptosArray = jsonObject.getString("symptoms").split(",");
                            String [] counterArray = jsonObject.getString("count").split(",");
                            initGraph(graph, counterArray, jsonObject.getInt("max"));

                            tvSymptomlink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String symp = "";
                                    //initGraph(graph);/
                                    for(int x = 0; x< symptosArray.length ; x++){
                                        symp = symp + x + "=" + symptosArray[x] + "\n";
                                    }

                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Symptoms")
                                            .setMessage(symp)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                }
                            });

                            tvProvincelink.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    String prov = "";

                                    for(int x = 0; x< provinceArray.length ; x++){
                                        prov = prov + x + "=" + provinceArray[x] + "\n";
                                    }

                                    new AlertDialog.Builder(getContext())
                                            .setTitle("Province")
                                            .setMessage(prov)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int which) {

                                                }
                                            })
                                            .show();
                                }
                            });

                        }


                    }else {

                    }


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




    public void initGraphP(GraphView graph) {

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(50);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(9);

        // enable scaling
        graph.getViewport().setScalable(true);


        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(new DataPoint[] {
                new DataPoint(0, 0),
                new DataPoint(1, 25),
                new DataPoint(2, 20),
                new DataPoint(3, 35),
                new DataPoint(4, 28),
                new DataPoint(5, 0),
                new DataPoint(6, 0),
                new DataPoint(7, 0),
                new DataPoint(8, 0),

        });
        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return  super.formatLabel(value, isValueX);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) + "°C";
                }
            }
        });

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {

                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(1);
        series.setAnimated(true);

        // draw values on top
        series.setDrawValuesOnTop(true);

        series.setValuesOnTopColor(Color.RED);
        //series.setValuesOnTopSize(50);

        // legend
        // series.setTitle("foo");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }

    public void initGraph(GraphView graph , String [] counter, int max) {

        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(0);
        graph.getViewport().setMaxY(max + (max/2));

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(0);
        graph.getViewport().setMaxX(counter.length);

        // enable scaling
        graph.getViewport().setScalable(true);
        DataPoint[] dataPoints = new DataPoint[counter.length];
        for(int x = 0 ; x < counter.length ; x++){
            dataPoints[x] = new DataPoint(x, Integer.parseInt(counter[x]));
        }

        BarGraphSeries<DataPoint> series = new BarGraphSeries<DataPoint>(dataPoints);
        graph.addSeries(series);

        graph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter() {
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if (isValueX) {
                    // show normal x values
                    return  super.formatLabel(value, isValueX);
                } else {
                    // show currency for y values
                    return super.formatLabel(value, isValueX) ;
                }
            }
        });

        // styling
        series.setValueDependentColor(new ValueDependentColor<DataPoint>() {
            @Override
            public int get(DataPoint data) {

                return Color.rgb((int) data.getX()*255/4, (int) Math.abs(data.getY()*255/6), 100);
            }
        });

        series.setSpacing(1);
        series.setAnimated(true);

        // draw values on top
        series.setDrawValuesOnTop(true);

        series.setValuesOnTopColor(Color.RED);
        //series.setValuesOnTopSize(50);

        // legend
       // series.setTitle("foo");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
}

  /*  public void initGraph(GraphView graph) {
        // first series is a line
        DataPoint[] points = new DataPoint[100];
        for (int i = 0; i < points.length; i++) {
            points[i] = new DataPoint(i, Math.sin(i*0.5) * 20*(Math.random()*10+1));
        }
        LineGraphSeries<DataPoint> series = new LineGraphSeries<>(points);

        // set manual X bounds
        graph.getViewport().setYAxisBoundsManual(true);
        graph.getViewport().setMinY(-200);
        graph.getViewport().setMaxY(200);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(4);
        graph.getViewport().setMaxX(80);

        // enable scaling
        graph.getViewport().setScalable(true);

        series.setTitle("Random Curve");

        graph.addSeries(series);

        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }*/
   /* public void initGraph(GraphView graph) {
        BarGraphSeries<DataPoint> series = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -2),
                new DataPoint(1, 5),
                new DataPoint(2, 3),
                new DataPoint(3, 2),
                new DataPoint(4, 6)
        });
        series.setSpacing(30);
        series.setAnimated(true);
        graph.addSeries(series);

        BarGraphSeries<DataPoint> series2 = new BarGraphSeries<>(new DataPoint[] {
                new DataPoint(0, -5),
                new DataPoint(1, 3),
                new DataPoint(2, 4),
                new DataPoint(3, 4),
                new DataPoint(4, 1)
        });
        series2.setColor(Color.RED);
        series2.setSpacing(30);
        series2.setAnimated(true);
        graph.addSeries(series2);

        graph.getViewport().setXAxisBoundsManual(true);
        graph.getViewport().setMinX(-2);
        graph.getViewport().setMaxX(6);

        // legend
        series.setTitle("foo");
        series2.setTitle("bar");
        graph.getLegendRenderer().setVisible(true);
        graph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);
    }
*/
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
