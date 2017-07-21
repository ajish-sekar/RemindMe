package com.example.android.remindme;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Ajish on 16-07-2017.
 */

public class ReminderAdapter extends ArrayAdapter<Reminder> {
    public ReminderAdapter(Context context, ArrayList<Reminder> reminders){
        super(context,0, reminders);
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View listItemView = convertView;
        if(listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.list_item, parent, false);
        }
        final Reminder currentreminder = getItem(position);

        TextView note  = (TextView) listItemView.findViewById(R.id.reminder);
        note.setText(currentreminder.getMnote());

        TextView location = (TextView) listItemView.findViewById(R.id.location);
        location.setText(currentreminder.getMaddress());
        Log.v("Test", "" + currentreminder.getMlat());

        if(currentreminder.getMrun()==0) {
            TextView status = (TextView) listItemView.findViewById(R.id.status);
            status.setText("Completed");
        }



        return listItemView;
    }
}
