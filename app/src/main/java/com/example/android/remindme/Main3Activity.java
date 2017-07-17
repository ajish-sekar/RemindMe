package com.example.android.remindme;

import android.app.Notification;
import android.app.NotificationManager;
import android.graphics.Color;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.messaging.RemoteMessage;

import java.util.ArrayList;
import java.util.List;

public class Main3Activity extends AppCompatActivity {

    private FusedLocationProviderClient fusedLocationProviderClient;
    LatLng currlocation;
    DataBaseHandler db;
    ArrayList<Reminder> reminders;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3);
        db = new DataBaseHandler(this);
        reminders = db.getAllReminder();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {


            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currlocation = new LatLng(location.getLatitude(), location.getLongitude());
                        Notify task = new Notify();
                        task.execute();

                    } else {
                        Log.v("Test", "GPS not working");
                        currlocation = new LatLng(-31.952854, 115.857342);
                    }
                }
            });
        }catch (SecurityException e){
            Log.v("Error", "Permission Denied");
        }

    }

    private class Notify extends AsyncTask<Void, Void, Void>{

        private ArrayList<Integer> positions;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.prog);
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            ProgressBar progressBar = (ProgressBar) findViewById(R.id.prog);
            progressBar.setVisibility(View.GONE);
            String note = "";
            for (int i=0;i<positions.size();i++){
                note += reminders.get(positions.get(i)).getMnote()+"\n";
                NotificationCompat.Builder builder = new NotificationCompat.Builder(Main3Activity.this);
                builder.setSmallIcon(R.mipmap.ic_launcher);
                builder.setContentTitle("Reminder");
                builder.setContentText(reminders.get(positions.get(i)).getMnote());
                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                builder.setSound(alarmSound);
                builder.setLights(Color.BLUE,3000,3000);


                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                notificationManager.notify(i,builder.build());
            }
            TextView textView = (TextView) findViewById(R.id.notify);
            textView.setText(note);
        }

        @Override
        protected Void doInBackground(Void... params) {

            positions  = new ArrayList<>();

            for(int i=0;i<reminders.size();i++){
                double lat = reminders.get(i).getMlat();
                double lng = reminders.get(i).getMlng();
                float[] results = new float[1];
                Location.distanceBetween(currlocation.latitude,currlocation.longitude,lat,lng,results);
                if(results[0]<500.0){
                    positions.add(i);
                }
            }
            return null;
        }
    }

}
