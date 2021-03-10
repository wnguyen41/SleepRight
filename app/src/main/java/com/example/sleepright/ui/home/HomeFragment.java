package com.example.sleepright.ui.home;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sleepright.ListAdapter;
import com.example.sleepright.R;
import com.example.sleepright.SleepSession;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.w3c.dom.Text;

import java.lang.reflect.Array;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private TextView recommendedSleep;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_home, container, false);

        RecyclerView recyclerView = (RecyclerView) root.findViewById(R.id.recyclerView);

        ArrayList<SleepSession> sessions = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();

        FirebaseDatabase.getInstance().getReference().child("SleepSessions").addListenerForSingleValueEvent(new ValueEventListener() {
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
                ListAdapter listAdapter = new ListAdapter(sessions);
                recyclerView.setAdapter(listAdapter);
                RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
                recyclerView.setLayoutManager(layoutManager);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
/*
        ListAdapter listAdapter = new ListAdapter(sessions);
        recyclerView.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);
*/

        recommendedSleep = root.findViewById(R.id.sleep_recommendation);

        ArrayList<SleepSession> usefulSessions = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference().child("SleepSessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = 0;
                snapshot.getValue();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    SleepSession session = snap.getValue(SleepSession.class);
                    System.out.println("User: " + uid);
                    if (count < 7 && session.userID.equals(uid) && session.getSessionRating() > 3) {
                        System.out.println("Data: " + session.getUserID());
                        System.out.println("Session Rating: " + session.sessionRating);
                        usefulSessions.add(session);
                        count++;
                        System.out.println("Size of Sessions List: " + usefulSessions.size());
                    }
                    if (count == 7) {break; }
                }
                //Average out hours slept for high rated sleep times and try to not use poorly slept times
                //Collect all the start and end times to get a recommended time slot
                float hours = (float) 0.0;
                float averageStarting = 0;
                float averageEnding = 0;
                ArrayList<Date> startTimes;
                ArrayList<Date> endTimes;
                if (!usefulSessions.isEmpty()) {
                    int listSize = usefulSessions.size();
                    for (int i = 0; i < listSize; i++)
                    {
                        DateFormat goodStartHour = new SimpleDateFormat("hh");
                        String convertStartHour = goodStartHour.format(usefulSessions.get(i).getStartTime());
                        float start = Float.parseFloat(convertStartHour);
                        DateFormat goodEndHour = new SimpleDateFormat("hh");
                        String convertEndHour = goodEndHour.format(usefulSessions.get(i).getEndTime());
                        float end = Float.parseFloat(convertEndHour);
                        System.out.println("Start: " + Float.toString(start));
                        System.out.println("End: " + Float.toString(end));

                        averageStarting += start;
                        averageEnding += end;

                        long difference = usefulSessions.get(i).getEndTime().getTime() - usefulSessions.get(i).getStartTime().getTime();
                        float convertDiff = TimeUnit.MILLISECONDS.toHours(difference);
                        hours += convertDiff;
                        System.out.println("Difference: " + Float.toString(hours));
                    }
                    hours /= listSize;
                    averageStarting /= listSize;
                    averageEnding /= listSize;

                    //formatting strings and numbers
                    int avgHours = (int) hours;
                    int startHour = (int) averageStarting;
                    String startString = Integer.toString(startHour);
                    if (startHour > 12){
                        startString = Integer.toString(startHour-12);
                        startString += ":00 PM";
                    }
                    else
                        startString += ":00 AM";

                    int endHour = (int) averageEnding;

                    int diff = startHour + avgHours;
                    String diffString = Integer.toString(diff);
                    if (diff > 25){
                        diffString = Integer.toString(diff-24);
                        diffString += ":00 AM";
                    }
                    else if (diff > 12) {
                        diffString = Integer.toString(diff-12);
                        diffString += ":00 PM";
                    }
                    else
                        diffString += ":00 AM";

                    String recommendation = startString + " until " + diffString + " for about " + Integer.toString(avgHours) + " hours!";
                    recommendedSleep.setText(recommendation);

                    // add recommendation to SharedPreferences
                    SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
                    SharedPreferences.Editor prefsEditor = prefs.edit();
                    prefsEditor.putString("recommendation", recommendation);
                    prefsEditor.putInt("recommendationStartHour", startHour);
                    prefsEditor.apply();
                    prefsEditor.commit();
                }
                else
                {
                    recommendedSleep.setText("No current recommendation for now");
                    //recommendedSleep.setGravity(Gravity.CENTER_HORIZONTAL);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return root;
//        homeViewModel =
//                new ViewModelProvider(this).get(HomeViewModel.class);
//        View root = inflater.inflate(R.layout.fragment_home, container, false);
//        final TextView textView = root.findViewById(R.id.text_home);
//        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });
//        return root;
    }
/*
    public String recommendedTime(){
        String recommendation = "";

        ArrayList<SleepSession> sessions = new ArrayList<>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        FirebaseDatabase.getInstance().getReference().child("SleepSessions").addListenerForSingleValueEvent(new ValueEventListener() {
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
                //Average out hours slept for high rated sleep times and try to not use poorly slept times
                if (!sessions.isEmpty()) {
                    int listSize = sessions.size();
                    for (int i = 0; i < listSize; i++)
                    {
                        if (sessions.get(i).getSessionRating() > 3)
                        {

                        }
                    }

                    SleepSession current = sessions.get(position);
                    Date startDate = current.getStartTime();
                    Date endDate = current.getEndTime();
                    DateFormat day = new SimpleDateFormat("MMM dd");
                    DateFormat time = new SimpleDateFormat("hh:mm aa");
                    float rating = current.getSessionRating();

                    long difference = endDate.getTime() - startDate.getTime();
                    float slept = TimeUnit.MILLISECONDS.toHours(difference);
                    String timeSlept = Float.toString(slept);

                    String date = day.format(startDate);
                    String minutes = time.format(endDate);
                }
                else
                {
                    recommendation = "";
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return recommendation;
    }
*/
}