package com.example.TakeMe;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;


public class myDbAdapter {
    myDbHelper myhelper;
    public myDbAdapter(Context context)
    {
        myhelper = new myDbHelper(context);
    }

    public long insertData(String name,  String surname, String phone , String email,  String pass)
    {
        SQLiteDatabase dbb = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME, name);
        contentValues.put(myDbHelper.SURNAME, surname);
        contentValues.put(myDbHelper.PHONE, phone);
        contentValues.put(myDbHelper.EMAIL, email);
        contentValues.put(myDbHelper.MyPASSWORD, pass);

        long id = dbb.insert(myDbHelper.TABLE_NAME, null , contentValues);
        return id;
    }

    public String getData()
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] columns = {myDbHelper.UID,myDbHelper.NAME,myDbHelper.SURNAME,myDbHelper.PHONE,myDbHelper.EMAIL,myDbHelper.MyPASSWORD};
        Cursor cursor =db.query(myDbHelper.TABLE_NAME,columns,null,null,null,null,null);
        StringBuffer buffer= new StringBuffer();
        while (cursor.moveToNext())
        {
            int cid =cursor.getInt(cursor.getColumnIndex(myDbHelper.UID));
            String name =cursor.getString(cursor.getColumnIndex(myDbHelper.NAME));
            String surname =cursor.getString(cursor.getColumnIndex(myDbHelper.SURNAME));
            String phone =cursor.getString(cursor.getColumnIndex(myDbHelper.PHONE));

            String email =cursor.getString(cursor.getColumnIndex(myDbHelper.EMAIL));
            String password =cursor.getString(cursor.getColumnIndex(myDbHelper.MyPASSWORD));
            buffer.append(cid+ "," + name + "," + surname + "," + phone + "," + email + "," + password +";");
        }
        return buffer.toString();
    }

    public  int delete(String uemail)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        String[] whereArgs ={uemail};

        int count =db.delete(myDbHelper.TABLE_NAME ,myDbHelper.EMAIL+" = ?",whereArgs);
        return  count;
    }

    public int updateUser(String oldEmail , String newName , String surname  , String phone  , String email  , String pass)
    {
        SQLiteDatabase db = myhelper.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(myDbHelper.NAME,newName);
        contentValues.put(myDbHelper.SURNAME,surname);
        contentValues.put(myDbHelper.PHONE,phone);
        contentValues.put(myDbHelper.EMAIL,email);
        contentValues.put(myDbHelper.MyPASSWORD,pass);

        String[] whereArgs= {oldEmail};
        int count =db.update(myDbHelper.TABLE_NAME,contentValues, myDbHelper.EMAIL+" = ?",whereArgs );
        return count;
    }

    static class myDbHelper extends SQLiteOpenHelper
    {
        public static final  String SURNAME    = "surname";
        public static final  String EMAIL      = "email" ;
        public static final  String PHONE      = "phone" ;
        private static final String DATABASE_NAME = "myDatabase";    // Database Name
        private static final String TABLE_NAME = "users";   // Table Name
        private static final int    DATABASE_Version = 1;   // Database Version
        private static final String UID="_id";     // Column I (Primary Key)
        private static final String NAME = "Name";    //Column II
        private static final String MyPASSWORD= "Password";    // Column III
        private static final String CREATE_TABLE = "CREATE TABLE "+TABLE_NAME+
                " ("+UID+" INTEGER PRIMARY KEY AUTOINCREMENT, "+NAME+" VARCHAR(255) ,"+SURNAME+" VARCHAR(255) ,"+PHONE+" VARCHAR(255),"+EMAIL+" VARCHAR(255) ,"+ MyPASSWORD+" VARCHAR(225));";
        private static final String DROP_TABLE ="DROP TABLE IF EXISTS "+TABLE_NAME;
        private Context context;

        public myDbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_Version);
            this.context=context;
        }

        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL(CREATE_TABLE);
            } catch (Exception e) {
                e.printStackTrace();
                print(""+e);
            }
        }

        public void print (String s){
           // Toast.makeText(context, s , Toast.LENGTH_SHORT);
            Toast.makeText(context,s,Toast.LENGTH_LONG).show();
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            try {
                //Message.message(context,"OnUpgrade");
                print("OnUpgrade");
                db.execSQL(DROP_TABLE);
                onCreate(db);
            }catch (Exception e) {
                print(""+e);
            }
        }
    }
}