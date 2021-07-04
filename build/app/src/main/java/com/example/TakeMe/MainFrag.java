package com.example.TakeMe;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.net.Uri;
import com.example.TakeMe.Fragments.DriverDashboad;
import com.example.TakeMe.Fragments.DriverSettings;
import com.example.TakeMe.Fragments.DriverNotification;
import com.example.TakeMe.Fragments.RequestDashboad;
import com.example.TakeMe.Fragments.RequestNotification;
import com.example.TakeMe.Fragments.RequestSettings;
import com.example.sbag.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.MenuItem;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainFrag extends AppCompatActivity implements DriverSettings.OnFragmentInteractionListener, DriverDashboad.OnFragmentInteractionListener, DriverNotification.OnFragmentInteractionListener{
    private Fragment fragment  ;
    private BottomNavigationView navView ;
    private String MyPREFERENCES = "32145788";
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
                        setTitle(R.string.title_update);
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
                        setTitle(R.string.title_update);
                        break;
                }
            }

            getSupportFragmentManager().beginTransaction().replace(R.id.contain,fragment).commit();
            navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
            return true;
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_frag);

        SharedPreferences preferences = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);

        data = preferences.getString("data", "{}");//"No name defined" is the default value.
        try {
            JSONArray jsonArray = new JSONArray(data);
            JSONObject jsonObject = jsonArray.getJSONObject(0);
            UserType = jsonObject.getString("UserType");

        } catch (JSONException e) {
            e.printStackTrace();
        }


        navView = findViewById(R.id.nav_view);
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(UserType.equals("2")) {
            fragment = new DriverDashboad();
            setTitle(R.string.title_dashboard);
        }else if(UserType.equals("1")){
            fragment = new RequestDashboad() ;
            setTitle(R.string.title_dashboard);
        }
        setTitleColor(R.color.tittle);


        getSupportFragmentManager().beginTransaction().replace(R.id.contain,fragment).commit();
        navView.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

}
