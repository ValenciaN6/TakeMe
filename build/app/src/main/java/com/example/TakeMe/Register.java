package com.example.TakeMe;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.sbag.R;

import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Register extends AppCompatActivity {

    private  String [] typeList = {"Ambulance Driver","Ambulance Requester"};
    private  String userType = typeList[0];
    private  EditText edtEmail,edtName,edtSurname,  edtPass,edtPhone,edtPassc;
    private String username = "",surname = "", email = "", pass = "", phone = "" , longitude="0" , latitude = "0.0", passc, host;
    private String MyPREFERENCES = "321qwe" ,ACTIOB_ADDUSER = "adduser.php?";

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Button btnSubmit = (Button)findViewById(R.id.btnSubmit);
        Button btnPolicy = (Button)findViewById(R.id.btnPolicy);
        Button btnTandC = (Button)findViewById(R.id.btnTandC);

        edtEmail    = (EditText)findViewById(R.id.edtEmail);
        edtName = (EditText)findViewById(R.id.edtname);
        edtSurname = (EditText)findViewById(R.id.edtSurname);
        edtPhone = (EditText)findViewById(R.id.edtPhone);
        edtPass     = (EditText)findViewById(R.id.edtPass);
        edtPassc     = (EditText)findViewById(R.id.edtPassC);

        SharedPreferences sharedpreferences;
        sharedpreferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        host = sharedpreferences.getString("host","");


        btnPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                print("Downloading policy");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(host + "policy_doc/policy.pdf")));

            }
        });

        btnTandC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print("Downloading terms and conditions");
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(host + "policy_doc/terms_conditions.pdf")));

            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {;


                phone    = edtPhone.getText().toString();
                username = edtName.getText().toString();
                surname  = edtSurname.getText().toString();

                pass     = edtPass.getText().toString();
                passc    = edtPassc.getText().toString();
                email    = edtEmail.getText().toString();

              if(!validateName(username)){
                    edtName.setError("Invalid name, the length should be greater than 2");
                }
                else
                if(!validateName(surname)){
                    edtSurname.setError("Invalid username, the length should be greater than 2");
                }else
                if(!validateEmailAddress(email)){
                    edtEmail.setError("Invalid Email!");
                }


                else
                if(phone.length() != 10){
                    edtPhone.setError("A phone number should contain 10 digits");
                }else
                 if(validatPass(pass,passc))
                 {
                    try {
                        String url = ACTIOB_ADDUSER +
                                "nm="     + username     +
                                "&em="    + email    +
                                "&sn="    + surname     +
                                "&pn="    + phone +
                                "&ut="    + userType   +
                                "&pw="    + pass      +

                                "&la="      + latitude +
                                "&lo="      + longitude;

                        print("Registering...");

                        HttpGetRequest runner = new HttpGetRequest();
                        runner.execute(url);
                    }catch (Exception e){
                        e.printStackTrace();
                    }

                }
            }
        });


        Spinner spType = findViewById(R.id.spType);
        spType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                userType = typeList[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter ad = new ArrayAdapter( this, android.R.layout.simple_spinner_item,typeList);
        spType.setAdapter(ad);

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new MyLocationListener();
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION }, 1);
            return;
        }
        locationManager.requestLocationUpdates( LocationManager.GPS_PROVIDER, 500, 1, locationListener);
    }

    private boolean validatPass(String pass, String passc){
        if (pass.length() < 4){
            edtPass.setError("Password should have more than 3 characters");
            return false;
        }

        if (!pass.equals(passc)){
            edtPass.setError("Password should be the same as confirm password");
            return false;
        }
        return true;
    }

    private void print(String position) {
        Toast.makeText(getApplicationContext(),position, Toast.LENGTH_SHORT).show();
    }

    private boolean validateName(String iname){

        return  (iname.length()>= 3);
    }
    private boolean validateEmailAddress(String emailAddress){
        String  expression="^[\\w\\-]([\\.\\w])+[\\w]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        CharSequence inputStr = emailAddress;
        Pattern pattern = Pattern.compile(expression,Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(inputStr);
        return matcher.matches();
    }


    public class HttpGetRequest extends AsyncTask<String, Void, String> {
        public static final String REQUEST_METHOD = "GET";
        public static final int READ_TIMEOUT = 15000;
        public static final int CONNECTION_TIMEOUT = 15000;


        @Override
        protected String doInBackground(String... params){
            String stringUrl = host + params[0];

            Log.d("Host", stringUrl);
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
                //Set our result equal to our stringBuilder
                result = stringBuilder.toString();
            }
            catch(IOException e){
                e.printStackTrace();
                // print(e.getMessage().toString());
                result = null;
            }

            return result;
        }


        protected void onPostExecute(String result){
            super.onPostExecute(result);

            if(result != null ) {
                try {
                    JSONObject jsonObject = new JSONObject(result);
                    if( jsonObject.getString("result").equals("done")){

                        print("User was registered successful (◕‿◕)" );
                        edtEmail.setText("");
                        edtPass.setText("");
                        edtSurname.setText("");
                        edtPhone.setText("");
                        edtName.setText("");
                        edtPassc.setText("");
                    }else {

                        if(jsonObject.getString("error").indexOf("Email") >= 0){
                            edtEmail.setError(jsonObject.getString("error"));
                        }else if(jsonObject.getString("error").indexOf("User") >= 0){
                            edtEmail.setError(jsonObject.getString("error"));
                        }

                        print("Failed to register your user (◕︵◕)" );
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }
    }

    private class MyLocationListener implements LocationListener {

        @Override
        public void onLocationChanged(Location loc) {

           longitude =  loc.getLongitude() + "";
           latitude =  loc.getLatitude() + "";

        }

        @Override
        public void onProviderDisabled(String provider) {}

        @Override
        public void onProviderEnabled(String provider) {}

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    }



}
