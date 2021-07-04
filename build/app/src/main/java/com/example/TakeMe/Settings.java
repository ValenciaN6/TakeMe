package com.example.TakeMe;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sbag.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;

public class Settings extends AppCompatActivity {

    ListView list;

    EditText edtName,edtSurname,edtPhone, edtEmail, edtPass;
    Button btnAddDelete, btnRegister,btnUpdate;
    String [] data;
    myDbAdapter helper;
    int index = -1;
    private String UserID = "", email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        edtEmail  = (EditText) findViewById(R.id.edtEmail);
        edtName   = (EditText) findViewById(R.id.edtName);
        edtPhone  = (EditText) findViewById(R.id.edtPhone);
        edtPass   = (EditText) findViewById(R.id.edtPass);
        edtSurname= (EditText) findViewById(R.id.edtSurname);

        helper = new myDbAdapter(this);
        btnAddDelete = (Button)findViewById(R.id.btnAdduser);
        btnAddDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            try {
                if (email.isEmpty())
                    addUser();
                else
                    delete();
            }catch (Exception e){
                e.printStackTrace();
            }

            populateList();
            }
        });

        btnUpdate = (Button)findViewById(R.id.btnUpdate);
        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!email.isEmpty())
                    updateUser();
            }
        });

        btnRegister = (Button)findViewById(R.id.btnRegisterFinger);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if(UserID.equals("")){
                print("Please focus a user");
            }else
            {
                print(UserID);

                Date date = new Date();
                String d = "" + date;
                d = d.replace(":" , "");
                d = d.replace("+" , "");
                d = d.replace(" " , "");

                String url = "http://192.168.43.129/addfinger?mv=" + d + "&id=" + UserID;

                try {
                    Settings.HttpGetRequest runner = new Settings.HttpGetRequest();
                    runner.execute(url);
                }catch (Exception e){
                    // print(e.toString());
                    e.printStackTrace();
                }
            }
            }
        });
        populateList();

        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
            index = position;
            //Toast.makeText(getApplicationContext(), "You Clicked at " +web[+ position], Toast.LENGTH_SHORT).show();
            // viewdata();

            if(data.length > 0) {
                String[] infor = data[position].split(",");

                if (infor.length > 4){
                    edtName.setText(infor[1]);
                    edtSurname.setText(infor[2]);
                    edtPhone.setText(infor[3]);
                    edtEmail.setText(infor[4]);
                    edtPass.setText(infor[5]);

                    UserID = infor[0];
                    email = infor[4];

                    btnAddDelete.setText("Delete");
                }
            }
            }
        });
    }

    public boolean emailExist(String emai){


        emai = emai.replace(" " , "");

        if(data == null)
            return false;

        for(int x = 0 ; x < data.length ; x++) {

            if (!data[x].equals(""))
            {
                String[] line = data[x].split(",");


                line[4] = line[4].replace(" ", "").trim();


                if (emai.equals(line[4]))
                    return true;
            }
        }

        return false;
    }
    public void populateList(){
        data = helper.getData().split(";");

        CustomList listAdapter = new
                CustomList(this, data);
        list=(ListView)findViewById(R.id.list);

        list.setAdapter(listAdapter);
    }

    public void delete()
    {

        if(email.isEmpty())
        {
            print("Please select a user you wanna delete");
        }
        else{
            int a= helper.delete(email);
            if(a<=0)
            {
                print("Failed to delete user");
            }
            else
            {
                print("user was deleted");
                edtPass.setText("");
                edtEmail.setText("");
                edtPhone.setText("");
                edtSurname.setText("");
                edtName.setText("");
                this.email = "";
                this.UserID = "";
                btnAddDelete.setText("add user");
            }
        }
    }
    public void addUser()
    {
        String name    = edtName.getText().toString();
        String surname = edtSurname.getText().toString();
        String phone   = edtPhone.getText().toString();
        String email   = edtEmail.getText().toString();
        String pass    = edtPass.getText().toString();

        if( name.isEmpty())
        {
            edtName.setError("Invalid name");

        }else
        if( surname.isEmpty())
        {
            edtSurname.setError("Invalid surname");
        }else
        if(!( phone.length() == 10 || phone.length() == 13))
        {
            edtPhone.setError("Invalid phone number");
        }
        else
        if( email.indexOf("@") < 3 || email.indexOf(".") < email.indexOf("@") || email.length() < 10)
        {
            edtEmail.setError("Invalid email");
        }else
        if(emailExist(edtEmail.getText().toString())) {
            edtEmail.setError("Email Exist");
        }
        else
        if( pass.length() < 5)
        {
            edtPass.setError("Invalid password");
        }
        else
        {
            long id = helper.insertData(name,surname,phone,email,pass);

            if(id<=0)
            {
                print("Failed to add the user");
            } else
            {
                print("User was added");
                edtPass.setText("");
                edtEmail.setText("");
                edtPhone.setText("");
                edtSurname.setText("");
                edtName.setText("");
                this.UserID = "";
                this.email = "";
            }
        }

    }

    public void updateUser()
    {
        String name    = edtName.getText().toString();
        String surname = edtSurname.getText().toString();
        String phone   = edtPhone.getText().toString();
        String email   = edtEmail.getText().toString();
        String pass    = edtPass.getText().toString();

        if( name.isEmpty())
        {
            edtName.setError("Invalid name");
        }else
        if( surname.isEmpty())
        {
            edtSurname.setError("Invalid surname");
        }else
        if(!( phone.length() == 10 || phone.length() == 13))
        {
            edtPhone.setError("Invalid phone number");
        }
        else
        if( email.indexOf("@") < 3 || email.indexOf(".") < email.indexOf("@") || email.length() < 10)
        {
            edtEmail.setError("Invalid email");
        }
        else
        if( pass.length() < 5)
        {
            edtPass.setError("Invalid password");
        }
        else
        {

            long id = helper.updateUser(this.email,name,surname,phone,email,pass);
            if(id<=0)
            {
                print("Failed to update the user");
            } else
            {
                print("User was updated");
                populateList();
                btnAddDelete.setText("add user");

                edtPass.setText("");
                edtEmail.setText("");
                edtPhone.setText("");
                edtSurname.setText("");
                edtName.setText("");
                this.UserID = "";
                this.email = "";
            }
        }
    }

    private void print(String s) {

        Toast.makeText(getApplicationContext(),s,Toast.LENGTH_LONG).show();
    }

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

                String[] res = result.split(":");

                if (res[0].equals("startData")) {

                    String[] data = res[1].split(",");

                }
            }
        }
    }


    public class CustomList extends ArrayAdapter<String>{

        private final Activity context;
        private final String[] data;

        public CustomList(Activity context,
                          String[] data) {
            super(context, R.layout.user_layout, data);
            this.context = context;
            this.data    = data;

        }

        @NonNull
        @Override
        public View getView(int position, View view, ViewGroup parent) {
            LayoutInflater inflater = context.getLayoutInflater();
            View rowView= inflater.inflate(R.layout.user_layout, null, true);

            TextView txtName     = (TextView) rowView.findViewById(R.id.tvName);
            TextView txtSurname  = (TextView) rowView.findViewById(R.id.tvSurname);
            TextView txtPhone    = (TextView) rowView.findViewById(R.id.tvPhone);
            TextView txtEmail    = (TextView) rowView.findViewById(R.id.tvEmail);

            if(data.length > 0){
                String [] infor = data[position].split(",");

                if(infor.length > 4) {
                    txtName.setText(infor[1]);
                    txtSurname.setText(infor[2]);
                    txtPhone.setText(infor[3]);
                    txtEmail.setText(infor[4]);
                }
            }

            return rowView;
        }
    }
}
