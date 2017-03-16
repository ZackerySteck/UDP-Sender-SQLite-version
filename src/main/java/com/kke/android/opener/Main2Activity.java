

package com.kke.android.opener;

        import android.app.Activity;
        import android.content.Context;
        import android.content.Intent;
        import android.content.SharedPreferences;
        import android.net.Uri;
        import android.os.Bundle;
        import android.text.InputType;
        import android.view.View;
        import android.widget.EditText;
        import android.widget.TextView;
        import android.widget.Toast;
        import android.widget.ToggleButton;
        import android.database.*;

public class Main2Activity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        // Restore preferences
        SharedPreferences settings = getPreferences(0);
        ToggleButton toggleButton = ((ToggleButton) findViewById(R.id.toggleButton));
        boolean checked = settings.getBoolean("toggleChecked", false);
        toggleButton.setChecked(checked);
        toggleButton.setVisibility(View.GONE);
        Cursor prefs = MainActivity.getQuery();

        int column1 = prefs.getColumnIndex("host"); //get the column index of host
        int column2 = prefs.getColumnIndex("port"); //get column index of port
        int column3 = prefs.getColumnIndex("dataText"); //unused?
        String host = null; //String host
        String port = null; //String port
        String dataText = null; //

        prefs.moveToFirst(); //Move back to beginning of table
        if(prefs.getCount() > 0){ //Ensure the table isnt empty
            host = prefs.getString(column1); //Get Value at Row/Col
            port = prefs.getString(column2);
            dataText = prefs.getString(column3);
        }

        //Get values from DB on initialization
        EditText editText = (EditText) findViewById(R.id.editTextIP);
        if (checked) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if(host != null){
            editText.setText(host, TextView.BufferType.EDITABLE);
        }else{
            editText.setText(settings.getString("host", ""), TextView.BufferType.EDITABLE);
        }
        editText = (EditText) findViewById(R.id.editTextPort);
        if (checked) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if(port != null){
            editText.setText(port, TextView.BufferType.EDITABLE);
        }else{
            editText.setText(settings.getString("port", ""), TextView.BufferType.EDITABLE);
        }
        editText = (EditText) findViewById(R.id.editTextData);
        if (checked) {
            editText.setInputType(InputType.TYPE_CLASS_TEXT);
        }
        if(dataText != null){
            editText.setText(dataText, TextView.BufferType.EDITABLE);
        }else{
            editText.setText(settings.getString("dataText", ""), TextView.BufferType.EDITABLE);
        }
        /** Set up global variable to pass to MainActivity

        Intent intent = new Intent(Main2Activity.this, MainActivity.class);
        intent.putExtra("host", "host");
        intent.putExtra("port", "port");
        intent.putExtra("datatext", "datatext");
        startActivity(intent);*/
    }

    @Override
    public void onPause() {
        super.onPause();

        // Get current values
        EditText editText = (EditText) findViewById(R.id.editTextIP);
        String host = editText.getText().toString();
        editText = (EditText) findViewById(R.id.editTextPort);
        String port = editText.getText().toString();
        editText = (EditText) findViewById(R.id.editTextData);
        String dataText = editText.getText().toString();

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getPreferences(0);
        SharedPreferences.Editor editor = settings.edit();

        editor.putString("host", host);
        editor.putString("port", port);
        editor.putString("dataText", dataText);
        editor.putBoolean("toggleChecked", ((ToggleButton) findViewById(R.id.toggleButton)).isChecked());

        // Commit the edits!
        editor.commit();
    }

    public void onToggleClicked(View view) {
        boolean on = ((ToggleButton) view).isChecked();


        EditText editTextIp = (EditText) findViewById(R.id.editTextIP);
        EditText editTextPort = (EditText) findViewById(R.id.editTextPort);
        if (on) {
            editTextIp.setInputType(InputType.TYPE_CLASS_TEXT);
            editTextPort.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            editTextIp.setInputType(InputType.TYPE_CLASS_PHONE);
            editTextPort.setInputType(InputType.TYPE_CLASS_PHONE);
        }
    }


    /** Called when the user taps the Done button */
    public void returntoMain(View view) {
        EditText editText = (EditText) findViewById(R.id.editTextIP);
        String host = editText.getText().toString();
        editText = (EditText) findViewById(R.id.editTextPort);
        String port = editText.getText().toString();
        editText = (EditText) findViewById(R.id.editTextData);
        String dataText = editText.getText().toString();

        if(MainActivity.getQuery().getCount()>0){
            Cursor prefs = MainActivity.getQuery();
            prefs.moveToFirst();
            int column1 = prefs.getColumnIndex("host");
            String oldhost = prefs.getString(column1);
            MainActivity.UserPreferences.execSQL("UPDATE " + MainActivity.TableName + " SET host = '"
                    + host + "', port = '" + port
                    + "', dataText = '" + dataText + "' WHERE host = '" + oldhost +"';");
        }else{
            MainActivity.UserPreferences.execSQL("INSERT INTO "
                    + MainActivity.TableName
                    + " (host, port, dataText)"
                    + " VALUES ('" + host + "', '" + port + "','" + dataText + "');");
        }
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
}
