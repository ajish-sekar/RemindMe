package com.example.android.remindme;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private Location loc;
    private Boolean accessLocation = true;
    private double lat;
    private double lng;
    LatLng currlocation;
    private boolean flag =false;
    private LatLng marker;
    private MarkerOptions markerOptions;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        marker = new LatLng(0,0);

            try {


                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            flag = true;

                            currlocation = new LatLng(location.getLatitude(), location.getLongitude());
                            marker = currlocation;
                            mMap.addMarker(new MarkerOptions().position(currlocation));
                            mMap.moveCamera(CameraUpdateFactory.newLatLng(currlocation));


                        } else {
                            Log.v("Test", "GPS not working");
                            currlocation = new LatLng(-31.952854, 115.857342);
                        }
                    }
                });
            }catch (SecurityException e){
                Log.v("Error", "Permission Denied");
            }

        if(!flag){
            Log.v("Test","This is not possible");
            currlocation = new LatLng(-33.87365, 151.20689);

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button button = (Button) findViewById(R.id.select_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Intent output = new Intent();
                output.putExtra("latitude",marker.latitude);
                output.putExtra("longitude",marker.longitude);
                setResult(RESULT_OK, output);
                finish();
            }
        });
    }



    @Override
    public void onMapReady(final GoogleMap googleMap) {
        mMap = googleMap;





        mMap.moveCamera(CameraUpdateFactory.zoomTo(17));
        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                googleMap.clear();
                marker=latLng;
                googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }


}
