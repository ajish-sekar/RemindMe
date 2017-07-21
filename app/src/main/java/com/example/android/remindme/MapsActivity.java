package com.example.android.remindme;

import android.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.Map;

import static com.example.android.remindme.R.id.map;

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
    private Circle circle;
    private int radius = 200;
    private SeekBar seekBar;
    private int check = 0;
    private Intent intent;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        marker = new LatLng(0,0);
        intent = getIntent();
        check = intent.getIntExtra("flag",0);

            try {


                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        if (location != null) {
                            flag = true;
                            if(check==0) {
                                currlocation = new LatLng(location.getLatitude(), location.getLongitude());
                                marker = currlocation;
                                loc = location;
                                mMap.addMarker(new MarkerOptions().position(currlocation));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(currlocation));
                                circle = mMap.addCircle(new CircleOptions().center(currlocation).radius(radius).strokeColor(Color.parseColor("#0d47a1")).fillColor(0x550000ff));
                            }
                            else{
                                LatLng latLng = new LatLng(intent.getDoubleExtra("latitude",0),intent.getDoubleExtra("longitude",0));
                                marker = latLng;
                                mMap.addMarker(new MarkerOptions().position(latLng));
                                mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
                                circle = mMap.addCircle(new CircleOptions().center(latLng).radius(intent.getIntExtra("radius",200)).strokeColor(Color.parseColor("#0d47a1")).fillColor(0x550000ff));
                                seekBar.setProgress(radius/10);
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

        if(!flag){
            Log.v("Test","This is not possible");
            currlocation = new LatLng(-33.87365, 151.20689);

        }


        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(map);
        mapFragment.getMapAsync(this);

        Button button = (Button) findViewById(R.id.select_btn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
                Intent output = new Intent();
                output.putExtra("latitude",marker.latitude);
                output.putExtra("longitude",marker.longitude);
                output.putExtra("Radius",radius);
                setResult(RESULT_OK, output);
                finish();
            }
        });

        seekBar = (SeekBar) findViewById(R.id.seekBar);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                radius = 10*progress;
                circle.setRadius(radius);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        PlaceAutocompleteFragment autocompleteFragment = (PlaceAutocompleteFragment)
                getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {
                mMap.clear();
                marker= place.getLatLng();
                circle= mMap.addCircle(new CircleOptions().center(place.getLatLng()).radius(200).strokeColor(Color.parseColor("#0d47a1")).fillColor(0x550000ff));
                seekBar.setProgress(20);
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(place.getLatLng()));
            }

            @Override
            public void onError(Status status) {
                Log.i("Error", "An error occurred: " + status);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu3, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.select:
                Intent output = new Intent();
                output.putExtra("latitude",marker.latitude);
                output.putExtra("longitude",marker.longitude);
                setResult(RESULT_OK, output);
                finish();
                return true;


            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
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
                circle= googleMap.addCircle(new CircleOptions().center(latLng).radius(200).strokeColor(Color.parseColor("#0d47a1")).fillColor(0x550000ff));
                seekBar.setProgress(20);
                googleMap.addMarker(new MarkerOptions().position(latLng));
            }
        });
    }





}
