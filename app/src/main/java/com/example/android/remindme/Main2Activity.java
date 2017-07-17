package com.example.android.remindme;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static android.icu.lang.UCharacter.GraphemeClusterBreak.L;
import static android.icu.lang.UCharacter.GraphemeClusterBreak.T;
import static com.example.android.remindme.R.id.location;

public class Main2Activity extends AppCompatActivity {

    Reminder rem;
    Double lng;
    Double lat;
    DataBaseHandler db;
    int freq=0;
    int action = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        db = new DataBaseHandler(this);
        ArrayList<String> strings = new ArrayList<>();
        strings.add("Once");
        strings.add("Always");
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(Main2Activity.this,R.layout.support_simple_spinner_dropdown_item,strings);
        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        arrayAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinner.setAdapter(arrayAdapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                freq=position;
                Log.v("Freq","freq"+freq);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ArrayList<String> stringsAction = new ArrayList<>();
        stringsAction.add("Notify");
        stringsAction.add("Set Phone to Vibrate Mode");
        ArrayAdapter<String> arrayAdapterAction = new ArrayAdapter<String>(Main2Activity.this,R.layout.support_simple_spinner_dropdown_item,stringsAction);
        Spinner spinnerAction = (Spinner) findViewById(R.id.spinner_action);
        arrayAdapterAction.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        spinnerAction.setAdapter(arrayAdapterAction);
        spinnerAction.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                action=position;


            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button choose = (Button) findViewById(R.id.choose_loc);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent  = new Intent(Main2Activity.this,MapsActivity.class);
                startActivityForResult(intent, 101);
            }
        });

        Button save = (Button) findViewById(R.id.save_btn);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText noteText = (EditText) findViewById(R.id.reminder_add);
                TextView locText = (TextView) findViewById(R.id.location_add);

                if(noteText.getText().toString().equals("") || locText.getText().toString().equals("")){
                    AlertDialog.Builder builder = new AlertDialog.Builder(Main2Activity.this);
                    builder.setTitle("Warning");
                    builder.setMessage("Please fill in all the details");
                    builder.setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                    return;
                }
                else {
                    noteText = (EditText) findViewById(R.id.reminder_add);
                    String note = noteText.getText().toString();
                    String address = locText.getText().toString();
                    rem = new Reminder(note,lat,lng,address,freq,1,action);
                    db.addReminder(rem);
                    Intent back = new Intent(Main2Activity.this, MainActivity.class);
                    startActivity(back);

                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==101 && resultCode==RESULT_OK){
            lat = data.getDoubleExtra("latitude",0);
            lng = data.getDoubleExtra("longitude",0);
            GetAddress getAddress = new GetAddress();
            getAddress.latitude = lat;
            getAddress.longitude = lng;
            getAddress.execute();

        }
    }

    private class GetAddress extends AsyncTask<Void, Void, Void>{
        public Double latitude;
        public Double longitude;
        public String location_text;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.VISIBLE);
            TextView textView = (TextView) findViewById(R.id.location_add);
            textView.setVisibility(View.GONE);
        }

        @Override
        protected Void doInBackground(Void... params) {
            Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());
            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
            } catch (IOException ioException) {

                Log.e("Error", "ERROR", ioException);
            } catch (IllegalArgumentException illegalArgumentException) {


                Log.e("Error", "errorMessage" + ". " + "Latitude = " + latitude + ", Longitude = " + longitude, illegalArgumentException);
            }


            if (addresses == null || addresses.size() == 0) {
                location_text = latitude + ", " + longitude;

            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
                location_text = TextUtils.join(", ", addressFragments);

            }
                return null;
            }


        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
            progressBar.setVisibility(View.GONE);
            TextView textView = (TextView) findViewById(R.id.location_add);
            textView.setVisibility(View.VISIBLE);
            textView.setText(location_text);

        }
    }
}
