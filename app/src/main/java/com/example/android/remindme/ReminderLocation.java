package com.example.android.remindme;

import android.content.Intent;
import android.location.Location;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

public class ReminderLocation extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Intent intent;
    private int flag=0;
    private ArrayList<Reminder> reminders;
    private DataBaseHandler db;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private LatLng currlocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminder_location);
        db = new DataBaseHandler(this);
        intent = getIntent();
        flag = intent.getIntExtra("Value",0);
        if(flag==1){
            reminders=db.getAllReminder();
        }
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        try {


            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    if (location != null) {

                        currlocation = new LatLng(location.getLatitude(), location.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(currlocation).title("Current Location").icon(BitmapDescriptorFactory.fromResource(R.drawable.mapmarker)));
                        if(flag==1) {
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currlocation));
                        }

                    } else {
                        Log.v("Test", "GPS not working");
                        currlocation = new LatLng(-31.952854, 115.857342);
                    }
                }
            });
        }catch (SecurityException e){
            Log.v("Error", "Permission Denied");
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        if(flag==0) {
            LatLng loc = new LatLng(intent.getDoubleExtra("LAT", 0), intent.getDoubleExtra("LONG", 0));
            mMap.addMarker(new MarkerOptions().position(loc).title(intent.getStringExtra("Note")));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(loc));
        }else
        {   LatLng loc2 = new LatLng(0,0);
            for(int i=0;i<reminders.size();i++){
                loc2 = new LatLng(reminders.get(i).getMlat(),reminders.get(i).getMlng());
                mMap.addMarker(new MarkerOptions().position(loc2).title(reminders.get(i).getMnote()));
            }
        }
    }
}
