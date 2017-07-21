package com.example.android.remindme;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import static android.media.CamcorderProfile.get;
import static com.example.android.remindme.R.id.reminder;

/**
 * Created by Ajish on 18-07-2017.
 */

public class ReminderRecylerAdapter extends RecyclerView.Adapter<ReminderRecylerAdapter.ReminderViewHolder> {
    private Context context;
    private ArrayList<Reminder> reminders;
    private int pos = 0;

    public class ReminderViewHolder extends RecyclerView.ViewHolder{
        private TextView note,location,status,options;


        public ReminderViewHolder(View view){
            super(view);
            note = (TextView) view.findViewById(R.id.reminder_card);
            location  = (TextView) view.findViewById(R.id.location_card);
            status = (TextView) view.findViewById(R.id.status_card);
            options = (TextView) view.findViewById(R.id.options);
        }
    }

    public ReminderRecylerAdapter(Context context, ArrayList<Reminder> reminders){
        this.context = context;
        this.reminders = reminders;
    }

    @Override
    public ReminderViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_card, parent, false);

        return new ReminderViewHolder(itemView);

    }

    @Override
    public void onBindViewHolder(final ReminderViewHolder holder, final int position) {
        Reminder rem = reminders.get(position);
        holder.note.setText(rem.getMnote());
        holder.location.setText(rem.getMaddress());

        if(rem.getMrun()==0){
            holder.status.setText("Completed");
        }
        else {
            holder.status.setText("");
        }

        holder.options.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(context, holder.options);
                popupMenu.inflate(R.menu.card_menu);
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch(item.getItemId()){
                            case R.id.show:
                                Intent intent = new Intent(context,ReminderLocation.class);
                                intent.putExtra("Value",0);
                                intent.putExtra("LAT",reminders.get(position).getMlat());
                                intent.putExtra("LONG",reminders.get(position).getMlng());
                                intent.putExtra("Note",reminders.get(position).getMnote());
                                context.startActivity(intent);
                                break;
                            case R.id.edit:
                                Intent intent1 = new Intent(context,Main2Activity.class);
                                intent1.putExtra("Key",1);
                                intent1.putExtra("Location",reminders.get(position));
                                context.startActivity(intent1);
                        }
                        return false;
                    }

                });
                popupMenu.show();
            }
        });


    }
        @Override
    public int getItemCount() {
        return reminders.size();
    }
}
