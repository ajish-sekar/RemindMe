package com.example.android.remindme;

/**
 * Created by Ajish on 16-07-2017.
 */

public class Reminder {
    private String mnote;
    private Double mlat;
    private Double mlng;
    private String maddress;
    private int mfreq;
    private int mrun;
    private int id;
    private int maction;

    public Reminder(String note, Double lat, Double lng, String address, int freq, int run, int action ){
        mnote = note;
        mlat = lat;
        mlng = lng;
        maddress = address;
        mfreq = freq;
        mrun = run;
        maction = action;
    }

    public String getMnote() {
        return mnote;
    }

    public Double getMlat() {
        return mlat;
    }

    public Double getMlng() {
        return mlng;
    }

    public String getMaddress() {
        return maddress;
    }

    public int getMfreq() {
        return mfreq;
    }

    public int getMrun() {
        return mrun;
    }

    public void setMrun(int mrun) {
        this.mrun = mrun;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMaction() {
        return maction;
    }
}
