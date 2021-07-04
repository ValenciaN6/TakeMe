package com.example.TakeMe.ui.login;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.TakeMe.MainFrag;
import com.example.TakeMe.NotificationService;
import com.example.TakeMe.Register;
import com.example.sbag.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar loading;
    private String MyPREFERENCES = "32145788";
    private String type = "patient";
    private RequestQueue mRequestQueue;
    private String host = "http://192.168.8.175/takeme/";
    private StringRequest mStringRequest;
    private String ACTION_LOGIN = "login.php?";
    private Spinner spnUsageTypeM;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button signIn = (Button)findViewById(R.id.login);
        Button register = (Button)findViewById(R.id.register);
        loading = (ProgressBar)findViewById(R.id.loading);
        TextView more = (TextView)findViewById(R.id.tvMore);
        spnUsageTypeM = (Spinner)findViewById(R.id.spinner);
        String [] UsageType = {"Patient" , "Driver" ,"Company"};



        ArrayAdapter aa = new ArrayAdapter(getApplicationContext(), android.R.layout.simple_spinner_item,UsageType);
        spnUsageTypeM.setAdapter(aa);

        spnUsageTypeM.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String [] tp = {"patient","driver","com"};
                type = tp[i];
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });



        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AlertDialog.Builder(LoginActivity.this)
                        .setTitle("More")
                        .setMessage(R.string.more)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();

            }
        });

        loading.setVisibility(View.GONE);

        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading.setVisibility(View.VISIBLE);
                EditText user = (EditText)findViewById(R.id.username);
                EditText pass = (EditText)findViewById(R.id.password);

                final   String url = ACTION_LOGIN + "em=" + user.getText().toString() + "&ps=" + pass.getText().toString();

                try {
                    sendAndRequestResponse(url);
                }catch (Exception e){
                    e.printStackTrace();
                }

            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext() , Register.class);  // MainFrag().getClass();
                startActivity(intent);

            }
        });
    }



    private void sendAndRequestResponse(String url) {

        //RequestQueue initialized
        mRequestQueue = Volley.newRequestQueue(this);
        url = host + url + "&tp=" + type;
        //String Request initialized
        mStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            if(response != null ) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if( jsonObject.getString("result").equals("done")) {

                        loading.setVisibility(View.GONE);


                        Intent intent = new Intent(getApplicationContext() , MainFrag.class);  // MainFrag().getClass();
                        SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                        editor.putString ("data", jsonObject.getJSONArray("data").toString() );
                        editor.putString ("host", host );
                        editor.putString ("id", jsonObject.getJSONArray("data").getJSONObject(0).getString("ID") );
                        editor.apply();

                       // NotificationService.ServiceBegin(getApplication());
                        startActivity(intent);

                    }else {
                        print("User do not exist (◕︵◕)" );
                        loading.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loading.setVisibility(View.GONE);
                }

            }else {
                print("Can not connect to server (◕︵◕)" );
                loading.setVisibility(View.GONE);
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

    private String getCompleteAddressString(double LATITUDE, double LONGITUDE) {
        String strAdd = "";
        Geocoder geocoder = new Geocoder(LoginActivity.this, Locale.getDefault());
        try {
            List<Address> addresses = geocoder.getFromLocation(LATITUDE, LONGITUDE, 1);
            String address = addresses.get(0).getAddressLine(0);
            String city = addresses.get(0).getLocality();
            String state = addresses.get(0).getAdminArea();
            String zip = addresses.get(0).getPostalCode();
            String country = addresses.get(0).getCountryName();

            /*strAdd = "address:" + address + "\n"
                    + "state:" + state + "\n"
                    + "zip:" + zip + "\n"
                    + "country:" + country + "\n";

            print("city:" + city);*/
        } catch (Exception e) {
            e.printStackTrace();
          //  Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

/*
    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;


        @Override
        protected String doInBackground(String... params){
            String stringUrl = params[0];
            String result = "";

            String inputLine;
            try {
                //Create a URL object holding our url
                URL myUrl = new URL(stringUrl);
                //Create a connection
                HttpURLConnection connection =(HttpURLConnection)
                        myUrl.openConnection();
                //Set methods and timeouts
                connection.setRequestMethod(REQUEST_METHOD);
                connection.setReadTimeout(READ_TIMEOUT);
                connection.setConnectTimeout(CONNECTION_TIMEOUT);

                //Connect to our url
                connection.connect();
                //Create a new InputStreamReader
                InputStreamReader streamReader = new InputStreamReader(connection.getInputStream());
                //Create a new buffered reader and String Builder
                BufferedReader reader = new BufferedReader(streamReader);
                StringBuilder stringBuilder = new StringBuilder();
                //Check if the line we are reading is not null
                while((inputLine = reader.readLine()) != null){
                    stringBuilder.append(inputLine);
                }
                //Close our InputStream and Buffered reader
                reader.close();
                streamReader.close();
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                result = null;
            }

            return result;
        }


        protected void onPostExecute(String result){
            super.onPostExecute(result);

            print(result);

            if(result != null ) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if( jsonObject.getString("result").equals("done")) {

                        loading.setVisibility(View.GONE);

                       Intent intent = new Intent(getApplicationContext() , MainFrag.class);  // MainFrag().getClass();
                        SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
                        editor.putString ("data", result );
                        editor.putString ("host", host );
                        editor.apply();
                       startActivity(intent);

                    }else {
                        print("User do not exist (◕︵◕)" );
                        loading.setVisibility(View.GONE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    loading.setVisibility(View.GONE);
                }


            }else {
                print("Can not connect to server (◕︵◕)" );
                loading.setVisibility(View.GONE);
            }
        }
    }
*/
    private void print(String position) {
        Toast.makeText(getApplicationContext(),position, Toast.LENGTH_SHORT).show();
    }

}
