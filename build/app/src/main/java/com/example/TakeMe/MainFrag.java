package com.example.TakeMe;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.net.Uri;

import com.android.volley.RequestQueue;
import com.example.TakeMe.Fragments.DriverDashboad;
import com.example.TakeMe.Fragments.DriverSettings;
import com.example.TakeMe.Fragments.DriverNotification;
import com.example.TakeMe.Fragments.RequestDashboad;
import com.example.TakeMe.Fragments.RequestNotification;
import com.example.TakeMe.Fragments.RequestSettings;
import com.example.TakeMe.ui.login.LoginActivity;
import com.example.sbag.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainFrag extends AppCompatActivity implements DriverSettings.OnFragmentInteractionListener, DriverNotification.OnFragmentInteractionListener{
    private Fragment fragment  ;
    private BottomNavigationView navView ;
    private String MyPREFERENCES = "32145788";
    private RequestQueue mRequestQueue;
    private String data;
    private String UserType = "";

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {


            if (UserType.equals("2")) {
                switch (item.getItemId()) {
                    case R.id.navigation_settings:
                        fragment = new DriverSettings();
                        setTitle(R.string.title_settings);
                        break;
                    case R.id.navigation_dashboard:
                        fragment = new DriverDashboad();
                        setTitle(R.string.title_dashboard);
                        break;
                    case R.id.navigation_update:
                        fragment = new DriverNotification();
                        setTitle("Request");
                        break;
                }
            }else if(UserType.equals("1")) {
                switch (item.getItemId()) {
                    case R.id.navigation_settings:
                        fragment = new RequestSettings();
                        setTitle(R.string.title_settings);
                        break;
                    case R.id.navigation_dashboard:
                        fragment = new RequestDashboad();
                        setTitle(R.string.title_dashboard);
                        break;
                    case R.id.navigation_update:
                        fragment = new RequestNotification();
                        setTitle("Request");
                        break;
                }
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.contain,fragment).commit();
            navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            return true;
        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {

            Intent intent = new Intent(getApplicationContext() , LoginActivity.class);  // MainFrag().getClass();
            SharedPreferences.Editor editor = getSharedPreferences(MyPREFERENCES, MODE_PRIVATE).edit();
            editor.clear();
            editor.commit();
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            NotificationService.ServiceStop(getApplication());
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frag);

        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        //Toolbar myToolbar = (Toolbar) findViewById(R.id.my_toolbar);
       // setSupportActionBar(myToolbar);
        //BottomNavigationView bottomNavigationView = (BottomNavigationView)findViewById(R.id.nav_view);
        //bottomNavigationView.getMenu().removeItem(R.id.navigation_settings);
        //Menu menu = (Menu)findViewById(R.id.nav_view);
        data = preferences.getString("data", "{}");//"No name defined" is the default value.
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            UserType = jsonObject.getString("UserType");
            NotificationService.ServiceBegin(getApplication());

        } catch (JSONException e) {
            e.printStackTrace();
        }


        navView = findViewById(R.id.nav_view);

        if(UserType.equals("2")) {
            fragment = new DriverDashboad();
            navView.getMenu().removeItem(R.id.navigation_settings);
            setTitle(R.string.title_dashboard);
        }else if(UserType.equals("1")){
            fragment = new RequestDashboad() ;
            setTitle(R.string.title_dashboard);
        }
        setTitleColor(R.color.tittle);


        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);


        getSupportFragmentManager().beginTransaction().replace(R.id.contain,fragment).commit();
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    protected void onPause() {
        super.onPause();
        NotificationService.ServiceStop(getApplication());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        NotificationService.ServiceStop(getApplication());
    }
}
