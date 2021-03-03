package com.example.sleepright;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class SleepSession {
    //public Calendar startTime,endTime;
    public Date startTime, endTime;
    public String userID;
    public float sessionRating;

    public SleepSession(){

    }

    public SleepSession(String userID, Date startTime, Date endTime, float sessionRating)
    {
        this.userID = userID;
        this.startTime = startTime;
        this.endTime = endTime;
        this.sessionRating = sessionRating;
    }

    public String getUserID() {
        return userID;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getEndTime() {
        return endTime;
    }

    public float getSessionRating() {
        return sessionRating;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void setStartTime(Date start) {
        this.startTime = start;
    }

    public void setEndTime(Date end) {
        this.endTime = end;
    }

    public void setSessionRating(float rating) {
        this.sessionRating = rating;
    }

    public ArrayList<SleepSession> querySessions () {
        ArrayList<SleepSession> sessions = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseDatabase.getInstance().getReference().child("SleepSessions").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                snapshot.getValue();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    SleepSession session = snap.getValue(SleepSession.class);
                    System.out.println("User: " + uid);
                    if (count < 7 && session.userID.equals(uid)) {
                        System.out.println("Data: " + session.getUserID());
                        System.out.println("Session Rating: " + session.sessionRating);
                        sessions.add(session);
                        count++;
                        System.out.println("Size of Sessions List: " + sessions.size());
                    }
                    if (count == 7) {break; }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return sessions;
    }
}
