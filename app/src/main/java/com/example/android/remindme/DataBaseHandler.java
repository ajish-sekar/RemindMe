package com.example.android.remindme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

import static android.provider.Contacts.SettingsColumns.KEY;

/**
 * Created by Ajish on 16-07-2017.
 */

public class DataBaseHandler extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "reminderManager";
    private static final String TABLE_REMINDER = "reminder";
    private static final String KEY_ID = "id";
    private static final String KEY_NOTE = "note";
    private static final String KEY_LAT = "latitude";
    private static final String KEY_LNG = "longitude";
    private static final String KEY_ADDRESS = "address";
    private static final String KEY_FREQ = "frequency";
    private static final String KEY_RUN = "run";
    private static final String KEY_ACTION = "action";
    private static final String KEY_RADIUS = "radius";
    private static final String KEY_WHEN = "remindwhen";

    public DataBaseHandler(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE IF NOT EXISTS " + TABLE_REMINDER + "("
                + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL," + KEY_NOTE + " TEXT NOT NULL,"
                + KEY_LAT + " REAL NOT NULL," + KEY_LNG + " REAL NOT NULL," + KEY_ADDRESS + " TEXT NOT NULL,"+ KEY_FREQ + " INT NOT NULL,"+ KEY_RUN + " INT NOT NULL,"
                + KEY_ACTION + " INT NOT NULL," + KEY_RADIUS + " INT NOT NULL," + KEY_WHEN + " INT NOT NULL" + ")";

        db.execSQL(CREATE_CONTACTS_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_REMINDER);
        onCreate(db);
    }

    public void addReminder(Reminder rem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, rem.getMnote());
        values.put(KEY_LAT, rem.getMlat());
        values.put(KEY_LNG, rem.getMlng());
        values.put(KEY_ADDRESS, rem.getMaddress());
        values.put(KEY_FREQ,rem.getMfreq());
        values.put(KEY_RUN,rem.getMrun());
        values.put(KEY_ACTION,rem.getMaction());
        values.put(KEY_RADIUS,rem.getMradius());
        values.put(KEY_WHEN,rem.getMwhen());
        db.insert(TABLE_REMINDER, null, values);
        db.close();

    }



    public void deleteReminder(Reminder rem){
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_REMINDER, KEY_ID + " = ?",
                new String[] { rem.getId()+"" });
        db.close();

    }

    public ArrayList<Reminder> getAllReminder(){

        ArrayList<Reminder> reminderList = new ArrayList<Reminder>();
        String selectQuery = "SELECT  * FROM " + TABLE_REMINDER;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                String note = cursor.getString(cursor.getColumnIndex(KEY_NOTE));
                Double lat = cursor.getDouble(cursor.getColumnIndex(KEY_LAT));
                Double lng = cursor.getDouble(cursor.getColumnIndex(KEY_LNG));
                int id = cursor.getInt(cursor.getColumnIndex(KEY_ID));
                int freq = cursor.getInt(cursor.getColumnIndex(KEY_FREQ));
                int run = cursor.getInt(cursor.getColumnIndex(KEY_RUN));
                int action = cursor.getInt(cursor.getColumnIndex(KEY_ACTION));
                String address = cursor.getString(cursor.getColumnIndex(KEY_ADDRESS));
                int radius = cursor.getInt(cursor.getColumnIndex(KEY_RADIUS));
                int when = cursor.getInt(cursor.getColumnIndex(KEY_WHEN));
                Reminder rem = new Reminder(note,lat,lng,address,freq,run,action,radius,when);
                rem.setId(id);
                reminderList.add(rem);
            } while (cursor.moveToNext());
        }

        return reminderList;

    }
    public void deleteAll(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM " + TABLE_REMINDER);
    }
    public void update(Reminder rem){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(KEY_NOTE, rem.getMnote());
        values.put(KEY_LAT, rem.getMlat());
        values.put(KEY_LNG, rem.getMlng());
        values.put(KEY_ADDRESS, rem.getMaddress());
        values.put(KEY_FREQ,rem.getMfreq());
        values.put(KEY_RUN,rem.getMrun());
        values.put(KEY_ACTION,rem.getMaction());
        values.put(KEY_RADIUS,rem.getMradius());
        values.put(KEY_WHEN,rem.getMwhen());
        db.update(TABLE_REMINDER,values,KEY_ID+"="+rem.getId(),null);
        db.close();
    }
}

