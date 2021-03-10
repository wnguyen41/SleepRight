package com.example.sleepright;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.PreferenceManager;

import com.example.sleepright.R;

import java.util.Calendar;

public class SleepReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        createNotificationChannel(context);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        String recommendation = prefs.getString("recommendation", "");
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notifySleep")
                .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                .setContentTitle("SleepRight Sleep Recommendation")
                .setContentText(recommendation)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setAutoCancel(true);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(200, builder.build());
    }

    private void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "bedtimeChannel";
            String description = "Channel for bedtime notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("notifySleep", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }
}
