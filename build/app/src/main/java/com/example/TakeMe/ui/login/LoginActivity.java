package com.example.TakeMe.ui.login;
import android.app.AlertDialog;
import android.content.Context;
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
import com.example.TakeMe.MapsActivity;
import com.example.TakeMe.NotificationService;
import com.example.TakeMe.Register;
import com.example.sbag.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.Locale;

public class LoginActivity extends AppCompatActivity {

    private ProgressBar loading;
    private String MyPREFERENCES = "32145788";
    private String type = "patient";
    private RequestQueue mRequestQueue;
    private String host = "http://10.0.0.103/takeme/";
    private StringRequest mStringRequest;
    private String ACTION_LOGIN = "login.php?";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        NotificationService.ServiceStop(getApplication());
        Button signIn = (Button)findViewById(R.id.login);
        Button register = (Button)findViewById(R.id.register);
        loading = (ProgressBar)findViewById(R.id.loading);

        String [] UsageType = {"Patient" , "Driver" ,"Company"};

        SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
        editor.putString ("host", host );
        editor.commit();
        
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

        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        String data = preferences.getString("data", "{}");//"No name defined" is the default value.

        if(!data.equals("{}") ){
            Intent intent = new Intent(getApplicationContext() , MainFrag.class);
           // NotificationService.ServiceBegin(getApplication());
            startActivity(intent);
        }


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
                        editor.putString ("UserType", jsonObject.getJSONArray("data").getJSONObject(0).getString("UserType") );
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

        } catch (Exception e) {
            e.printStackTrace();
          //  Log.w("My Current loction address", "Canont get Address!");
        }
        return strAdd;
    }

    private void print(String position) {
        Toast.makeText(getApplicationContext(),position, Toast.LENGTH_SHORT).show();
    }

}
