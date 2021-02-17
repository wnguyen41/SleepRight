package com.example.sleepright;

import java.util.Calendar;

public class SleepSession {
    public Calendar startTime,endTime;
    public String userID;
    public float sessionRating;

    public SleepSession(){

    }

    public SleepSession(String userID, Calendar startTime, Calendar endTime, float sessionRating)
    {
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionRating = sessionRating;
    }

}
