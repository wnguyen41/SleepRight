package com.example.sleepright.ui.home;

import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.sleepright.ListAdapter;
import com.example.sleepright.MainActivity;
import com.example.sleepright.R;
import com.example.sleepright.SleepReceiver;
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
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
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
                        DateFormat goodStartHour = new SimpleDateFormat("HH");
                        String convertStartHour = goodStartHour.format(usefulSessions.get(i).getStartTime());
                        float start = Float.parseFloat(convertStartHour);
                        DateFormat goodEndHour = new SimpleDateFormat("HH");
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

                    // setting alarm one hour before recommendation
                    Calendar updateTime = Calendar.getInstance();
                    updateTime.set(Calendar.HOUR_OF_DAY, startHour-1);
                    updateTime.set(Calendar.MINUTE, 0);
                    updateTime.set(Calendar.SECOND, 0);
                    if(prefs.getBoolean("notificationPreference", false) && !prefs.getBoolean("firstTime", false)) {
                        Log.i(LOG_TAG, "First time: false");
                        prefsEditor.putBoolean("firstTime", true);
                        prefsEditor.apply();
                        prefsEditor.commit();
                        Intent intent = new Intent(getContext(), SleepReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                        //                        Toast.makeText(getContext(), updateTime.getTimeInMillis() + " " + startHour, Toast.LENGTH_LONG).show();
                    }
                    else if (prefs.getBoolean("notificationPreference", false) && prefs.getInt("recommendationStartHour", 0) != startHour){
                        Log.i(LOG_TAG, "Updated. First time: false");
                        // cancel previous notification
                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());
                        notificationManager.cancelAll();
                        Intent intent = new Intent(getContext(), SleepReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);

                        // cancel other alarm
                        alarmManager.cancel(pendingIntent);
                        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
                    }
                    prefsEditor.putString("recommendation", recommendation);
                    prefsEditor.putInt("recommendationStartHour", startHour);
                    prefsEditor.apply();
                    prefsEditor.commit();
                    Log.i(LOG_TAG, "Update time: " + updateTime.getTime());
                    Log.i(LOG_TAG, "Recommend start time: " + prefs.getInt("recommendationStartHour", 0));

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

}