package com.example.sleepright;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.preference.PreferenceManager;


import java.util.Calendar;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        createNotificationChannel();

        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_profile,R.id.navigation_home, R.id.navigation_settings)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(navView, navController);

        // setting alarm one hour before recommendation
        // interval day: 1000*60*60*24
        // interval 15 min: 900000 (for testing)
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int startHour = prefs.getInt("recommendationStartHour", 0);
        if(prefs.getBoolean("notificationPreference", false) && !prefs.getBoolean("firstTime", false)) {
            SharedPreferences.Editor prefsEditor = prefs.edit();
            prefsEditor.putBoolean("firstTime", true);
            prefsEditor.apply();
            prefsEditor.commit();

            Calendar updateTime = Calendar.getInstance();
            updateTime.set(Calendar.HOUR_OF_DAY, startHour-1);
            updateTime.set(Calendar.MINUTE, 0);
            Intent intent = new Intent(this, SleepReceiver.class);
            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
            alarmManager.setInexactRepeating(AlarmManager.RTC_WAKEUP, updateTime.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    //                        Toast.makeText(getContext(), updateTime.getTimeInMillis() + " " + startHour, Toast.LENGTH_LONG).show();
        }

//        // RecyclerView code starts here
//        ListFragment fragment = new ListFragment();
//        FragmentManager fragmentManager = getFragmentManager();
//        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.add(R.id.recyclerView,fragment);
//        fragmentTransaction.commit();

    }

    public void popup(View view) {
        Intent intent = new Intent (this, AddSleep.class);
        startActivity(intent);
    }

    public void closePopup(View view) {
        this.finish();
    }

//    private void createNotificationChannel() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            CharSequence name = "bedtimeChannel";
//            String description = "Channel for bedtime notifications";
//            int importance = NotificationManager.IMPORTANCE_DEFAULT;
//            NotificationChannel channel = new NotificationChannel("notifySleep", name, importance);
//            channel.setDescription(description);
//
//            NotificationManager notificationManager = getSystemService(NotificationManager.class);
//            notificationManager.createNotificationChannel(channel);
//        }
//    }


}