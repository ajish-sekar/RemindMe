package com.example.android.remindme;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.gms.internal.add;

import java.util.ArrayList;

import static junit.runner.Version.id;

public class MainActivity extends AppCompatActivity {
    Reminder rem;
    DataBaseHandler db;
    ArrayList<Reminder> reminders;
    ReminderAdapter adapter;
    ListView listView;
    RecyclerView recyclerView;
    LinearLayoutManager linearLayoutManager;
    ReminderRecylerAdapter reminderRecylerAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        scheduleAlarm();
       // Button add = (Button) findViewById(R.id.add_btn);
        //Button check = (Button) findViewById(R.id.check_btn);
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.add_rem);
        db = new DataBaseHandler(this);
        recyclerView = (RecyclerView) findViewById(R.id.recyler_view);

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},123);

        }

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isNetworkAvailable()) {
                    Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                    intent.putExtra("Key", 0);
                    startActivity(intent);
                }else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setTitle("Note");
                    builder.setMessage("Please connect to the Internet before adding a reminder");
                    builder.setNeutralButton("Got it!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    builder.show();
                }
            }
        });
        /* add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, Main2Activity.class);
                intent.putExtra("Key",0);
                startActivity(intent);
            }
        });

        check.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {




               /* Intent intent = new Intent(MainActivity.this, Main3Activity.class);
                startActivity(intent);
                if(reminders.size()>0) {
                    db.deleteReminder(reminders.get(0));
                    reminders.remove(0);
                    reminderRecylerAdapter.notifyDataSetChanged();

                }
            }
        }); */
        reminders = db.getAllReminder();
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);
        if(reminders!=null) {
            //adapter = new ReminderAdapter(this, reminders);
            //listView = (ListView) findViewById(R.id.listview);
            //listView.setAdapter(adapter);
            //registerForContextMenu(listView);
            reminderRecylerAdapter = new ReminderRecylerAdapter(this,reminders);

            recyclerView.setAdapter(reminderRecylerAdapter);


        }else {
            cancelAlarm();
        }

        setRecyclerViewItemTouchListener();

    }

    private void setRecyclerViewItemTouchListener() {


        ItemTouchHelper.SimpleCallback itemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder viewHolder1) {

                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int swipeDir) {

                int position = viewHolder.getAdapterPosition();
                db.deleteReminder(reminders.get(position));
                reminders.remove(position);
                recyclerView.getAdapter().notifyItemRemoved(position);
            }


        };

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    @Override
    protected void onResume() {
        super.onResume();
        reminders = db.getAllReminder();
        if (reminders!=null) {
            scheduleAlarm();
            /*adapter = new ReminderAdapter(this, reminders);
            listView = (ListView) findViewById(R.id.listview);
            listView.setAdapter(adapter);*/
            reminderRecylerAdapter = new ReminderRecylerAdapter(this,reminders);

            recyclerView.setAdapter(reminderRecylerAdapter);
        }else{
            cancelAlarm();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode==123){
            if (grantResults.length>0 && grantResults[0]== PackageManager.PERMISSION_GRANTED){

            }else {
                finish();
            }

        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.show_menu:
                Intent intent = new Intent(MainActivity.this,ReminderLocation.class);
                intent.putExtra("Value",1);
                startActivity(intent);
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }



    public void scheduleAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        long firstMillis = System.currentTimeMillis();
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 60000, pIntent);
    }

    public void cancelAlarm() {
        Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);
        final PendingIntent pIntent = PendingIntent.getBroadcast(this, AlarmReceiver.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
        alarm.cancel(pIntent);
    }


}



