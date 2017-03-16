package com.kke.android.opener;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteCursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.app.Activity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;
import android.widget.EditText;
import android.database.*;

public class MainActivity extends Activity implements View.OnClickListener{

    private View view;
    public static String TableName = "GarageOpener"; //Name of SQL Table
    public static SQLiteDatabase UserPreferences; //SQL Table Container

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Context context = getApplicationContext();
        setContentView(R.layout.activity_main);
        try{
            UserPreferences = this.openOrCreateDatabase("Krofft",MODE_PRIVATE,null); //Open if exists, otherwise create

            UserPreferences.execSQL("CREATE TABLE IF NOT EXISTS " + TableName + " (host VARCHAR, port VARCHAR, dataText VARCHAR);"); //create if nonexistent

        }catch(Error e){
            CharSequence text = e.toString();
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
        }
        Button btnsend = (Button) findViewById(R.id.buttonSend);
        Button btnset = (Button) findViewById(R.id.buttonSet);
        btnsend.setOnClickListener(this);
        btnset.setOnClickListener(this);

    }

     @Override
     public void onResume() {
         super.onResume();

//         SharedPreferences settings = getSharedPreferences(0);
//         SharedPreferences.Editor editor = settings.edit();
//         editor.putString("host", host);
//         editor.putString("port", port);
//         editor.putString("dataText", dataText);
     }



    @Override
     public void onClick(View v) {
         switch (v.getId()) {
             case R.id.buttonSend:
                 sendData(v);
                 break;

             case R.id.buttonSet:
                 editParams(view);
                 break;
         }
     }


   public void sendData(View view) {
       Context context = getApplicationContext();
       //---------------------------PUT THIS IN A FUNCTION THAT RETURNS A STRUCTURE WITH ATTRIBUTES HOST,PORT,DATATEXT----------------------//
       // That structure would then be able to be accessed. This is entirely optional. The code works fine, but is still in a debugging     //
       // state. Thus there has been no refactoring done at all. Alot of code can be removed/compressed into functions or a structures      //
       Cursor prefs = getQuery(); //Gets SQL Query (All results from table)s
       int column1 = prefs.getColumnIndex("host"); //get the column index of host
       int column2 = prefs.getColumnIndex("port"); //get column index of port
       int column3 = prefs.getColumnIndex("dataText"); //unused?
       String host = null; //String host
       String port = null; //String port
       String dataText = null; //

       prefs.moveToFirst(); //Move back to beginning of table
       if(prefs != null){ //Ensure the table isnt empty
           host = prefs.getString(column1); //Get Value at Row/Col
           port = prefs.getString(column2);
           dataText = prefs.getString(column3);
       }
       //----------------------------------------------------------------------------------------------------------------------------------//
        if (!host.matches("\\b(?:\\d{1,3}\\.){3}\\d{1,3}\\b")) {
            CharSequence text = "Error: Invalid IP Address";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        if (!port.matches("^(6553[0-5]|655[0-2]\\d|65[0-4]\\d\\d|6[0-4]\\d{3}|[1-5]\\d{4}|[1-9]\\d{0,3}|0)$")) {
            CharSequence text = "Error: Invalid Port Number";
            Toast toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
            toast.show();
            return;
        }
        String uriString = "udp://" + host + ":" + port + "/";
            uriString += Uri.encode(dataText);

        Uri uri = Uri.parse(uriString);
        Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
        intent.addFlags(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP);
        intent.addCategory(Intent.CATEGORY_DEFAULT);

        startActivity(intent);
    }

        /** Called when the user taps the Set Params button */

        public void editParams(View view) {
            Intent intent = new Intent(this, Main2Activity.class);
            startActivity(intent);
        }

    public static Cursor getQuery(){ //Returns SQL Query cursor object
        return UserPreferences.rawQuery("SELECT * FROM " + TableName , null);
    }

    public void dropDb() { //For dropping database (if you want to add a button to "Clear all settings")
        this.deleteDatabase("Krofft.db");
    }
}

