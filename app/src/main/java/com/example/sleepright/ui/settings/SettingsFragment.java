package com.example.sleepright.ui.settings;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.SwitchPreference;

import com.example.sleepright.R;

import java.util.Calendar;

public class SettingsFragment extends PreferenceFragmentCompat implements Preference.OnPreferenceChangeListener {
    private SharedPreferences prefs;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
//        prefs = getContext().getSharedPreferences(".ui.settings.SettingsActivity", Context.MODE_PRIVATE);
        prefs = PreferenceManager.getDefaultSharedPreferences(getContext());
        SharedPreferences.Editor myEditor = prefs.edit();

        createNotificationChannel();
        ListPreference sleepIncomePref = findPreference("ideal_sleep_income");
        SwitchPreference notifications = findPreference("switch_bedtime_notification");
        Preference bedtimePref = findPreference("preferred_bedtime");

        sleepIncomePref.setOnPreferenceChangeListener(this);
        notifications.setOnPreferenceChangeListener(this);

        int hour = prefs.getInt("bedtimeHour", 10);
        String min = prefs.getString("bedtimeMin", "00");
        String am_pm = prefs.getString("bedtimeAMPM", "PM");
        bedtimePref.setSummary(hour + ":" + min + " " + am_pm);
        bedtimePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                int hour = prefs.getInt("bedtimeHour", 10);
                String min = prefs.getString("bedtimeMin", "00");

                TimePickerDialog bedtimePicker;
                bedtimePicker = new TimePickerDialog(getContext(), new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        String am_pm = "";
                        int selectedHour;
                        int selectedMin;
                        Calendar bedtime = Calendar.getInstance();
                        bedtime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        bedtime.set(Calendar.MINUTE, minute);

                        selectedHour = bedtime.get(Calendar.HOUR_OF_DAY);
                        if (bedtime.get(Calendar.AM_PM) == Calendar.AM) {
                            am_pm = "AM";
                            if (selectedHour == 0) {
                                selectedHour = 12;
                            }
                        } else if (bedtime.get(Calendar.AM_PM) == Calendar.PM) {
                            am_pm = "PM";
                            if (selectedHour > 12) {
                                selectedHour = bedtime.get(Calendar.HOUR_OF_DAY) - 12;
                            }
                        }

                        selectedMin = bedtime.get(Calendar.MINUTE);
                        String formattedMin;
                        if (selectedMin < 10) {
                            formattedMin = "0" + String.valueOf(selectedMin);
                        }
                        else {
                            formattedMin = String.valueOf(selectedMin);
                        }

                        bedtimePref.setSummary(selectedHour + ":" + formattedMin + " " + am_pm);
//                        bedtimePref.setDefaultValue(selectedHour + ":" + formattedMin + " " + am_pm);
                        myEditor.putInt("bedtimeHour", selectedHour);
                        myEditor.putString("bedtimeMin", formattedMin);
                        myEditor.putString("bedtimeAMPM", am_pm);
                        myEditor.commit();
                    }
                }, hour, Integer.parseInt(min), false);
                bedtimePicker.setTitle("Set bedtime");
                bedtimePicker.show();
                return true;
            }
        });


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        if (preference instanceof ListPreference && preference.getKey().equals("ideal_sleep_income")) {
            Toast toast = Toast.makeText(getContext(), "Ideal Sleep Income", Toast.LENGTH_SHORT);
            toast.show();
        } else if (preference instanceof SwitchPreference && preference.getKey().equals("switch_bedtime_notification")) {
            if (((SwitchPreference) preference).isEnabled()) {
                // notifications
                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "sleepright")
                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
                        .setContentTitle("SleepRight Notification")
                        .setContentText("Blank")
                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);

                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

            }
        } else {
            Toast toast = Toast.makeText(getContext(), "Other Preference", Toast.LENGTH_SHORT);
            toast.show();
        }
        return true;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "bedtimeChannel";
            String description = "Channel for bedtime notifications";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel("sleepright", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getContext().getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}