package com.example.ana.criminalintent;


import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

/**
 * Created by ana on 1/23/2017.
 */

public class Crime {
    private UUID mId;
    private String mTitle;
    private Date mDate;
    private Date mTime;
    private boolean mSolved;
    private String mSuspect;

    public Crime() {
// Generate unique identifier
        this(UUID.randomUUID());
    }

    public Crime(UUID id) {
        mId = id;
        mDate = new Date();
        mTime = new Date();
    }

    public UUID getId() {
        return mId;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String title) {
        mTitle = title;
    }

    public Date getDate() {
        return mDate;
    }

    public void setDate(Date date) {
        mDate = date;
    }

    public Date getTime() {
        return mTime;
    }

    public String getCrimeDate() {
        return new SimpleDateFormat("yyyy, MMM dd").format(mDate);
    }

    public String getCrimeTime() {
        return new SimpleDateFormat("hh:mm").format(mTime);
    }

    public void setTime(Date timeOfDay) {
        mTime = timeOfDay;
    }

    public boolean isSolved() {
        return mSolved;
    }

    public void setSolved(boolean solved) {
        mSolved = solved;
    }

    public String getSuspect() {
        return mSuspect;
    }

    public void setSuspect(String suspect) {
        mSuspect = suspect;
    }

    public String getPhotoFilename() {
        return "IMG_" + getId().toString() + ".jpg";
    }
}
