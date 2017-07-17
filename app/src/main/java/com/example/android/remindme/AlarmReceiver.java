package com.example.android.remindme;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

/**
 * Created by Ajish on 16-07-2017.
 */

public class AlarmReceiver extends BroadcastReceiver {
    public static final int REQUEST_CODE = 123;
    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context,NotificationService.class);
        context.startService(i);
    }
}
