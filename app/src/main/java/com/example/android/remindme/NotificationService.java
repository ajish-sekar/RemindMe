package com.example.android.remindme;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;

import static android.os.Build.VERSION_CODES.N;
import static com.example.android.remindme.R.id.location;

/**
 * Created by Ajish on 16-07-2017.
 */

public class NotificationService extends IntentService {
    private FusedLocationProviderClient fusedLocationProviderClient;
    LatLng currlocation;
    DataBaseHandler db;
    private ArrayList<Integer> positions;
    ArrayList<Reminder> reminders;
    public NotificationService(){
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        db = new DataBaseHandler(this);
        reminders = db.getAllReminder();
        LocationRequest mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {


            fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {

                @Override
                public void onSuccess(Location location) {
                    if (location != null) {
                        currlocation = new LatLng(location.getLatitude(), location.getLongitude());
                        positions  = new ArrayList<>();

                        for(int i=0;i<reminders.size();i++) {
                            double lat = reminders.get(i).getMlat();
                            double lng = reminders.get(i).getMlng();
                            float[] results = new float[1];
                            Location.distanceBetween(currlocation.latitude, currlocation.longitude, lat, lng, results);
                            if(reminders.get(i).getMwhen()==0) {
                                if (results[0] <= reminders.get(i).getMradius() && reminders.get(i).getMrun() == 1) {
                                    positions.add(i);
                                    if (reminders.get(i).getMfreq() == 0) {

                                        reminders.get(i).setMrun(0);
                                        db.update(reminders.get(i));
                                    }
                                }
                            }else{
                                if (results[0] >= reminders.get(i).getMradius() && reminders.get(i).getMrun() == 1) {
                                    positions.add(i);
                                    if (reminders.get(i).getMfreq() == 0) {

                                        reminders.get(i).setMrun(0);
                                        db.update(reminders.get(i));
                                    }
                                }
                            }
                        }

                        for (int i=0;i<positions.size();i++) {

                            if (reminders.get(positions.get(i)).getMaction() == 0) {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this);
                                builder.setSmallIcon(R.mipmap.ic_launcher);
                                builder.setContentTitle("Reminder");
                                builder.setContentText(reminders.get(positions.get(i)).getMnote());
                                Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                                builder.setSound(alarmSound);
                                builder.setLights(Color.BLUE, 3000, 3000);

                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
                                notificationManager.notify(i, builder.build());
                            }else if(reminders.get(positions.get(i)).getMaction()==1){
                                AudioManager audioManager = (AudioManager) getBaseContext().getSystemService(Context.AUDIO_SERVICE);
                                audioManager.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
                            }else {
                                NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationService.this);
                                builder.setSmallIcon(R.mipmap.ic_launcher);
                                builder.setContentTitle("Reminder");
                                builder.setContentText(reminders.get(positions.get(i)).getMnote());
                                //Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                                Uri sound = Uri.parse("android.resource://"+getPackageName()+"/"+R.raw.alarm);
                                builder.setSound(sound);
                                builder.setLights(Color.BLUE, 3000, 3000);
                                Notification notification  = builder.build();
                                notification.flags = Notification.FLAG_INSISTENT;
                                NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);

                                notificationManager.notify(i, notification);
                            }
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
    }
}
