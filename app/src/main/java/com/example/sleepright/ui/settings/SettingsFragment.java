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
import androidx.preference.CheckBoxPreference;
import androidx.preference.EditTextPreference;
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

        EditTextPreference agePref = findPreference("pref_age");
        EditTextPreference weightPref = findPreference("pref_weight");
        ListPreference sleepIncomePref = findPreference("ideal_sleep_income");
        SwitchPreference notifications = findPreference("switch_bedtime_notification");
        Preference wakeupTimePref = findPreference("target_wakeup_time");
//        CheckBoxPreference googleFitPref = findPreference("google_fit");
//        CheckBoxPreference googleCalendarPref = findPreference("google_calendar");

        agePref.setOnPreferenceChangeListener(this);
        agePref.setSummary(prefs.getInt("userAge", 0) + " years old");
        weightPref.setOnPreferenceChangeListener(this);
        weightPref.setSummary(prefs.getInt("userWeight", 0) + " lb");
        sleepIncomePref.setOnPreferenceChangeListener(this);
        notifications.setOnPreferenceChangeListener(this);


        int hour = prefs.getInt("wakeupHour", 10);
        String min = prefs.getString("wakeupMin", "00");
        String am_pm = prefs.getString("wakeupAMPM", "PM");
        wakeupTimePref.setSummary(hour + ":" + min + " " + am_pm);
        wakeupTimePref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

            @Override
            public boolean onPreferenceClick(Preference preference) {
                int hour = prefs.getInt("wakeupHour", 10);
                String min = prefs.getString("wakeupMin", "00");

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
                        } else {
                            formattedMin = String.valueOf(selectedMin);
                        }

                        wakeupTimePref.setSummary(selectedHour + ":" + formattedMin + " " + am_pm);
//                        wakeupTimePref.setDefaultValue(selectedHour + ":" + formattedMin + " " + am_pm);
                        myEditor.putInt("wakeupHour", selectedHour);
                        myEditor.putString("wakeupMin", formattedMin);
                        myEditor.putString("wakeupAMPM", am_pm);
                        myEditor.apply();
                        myEditor.commit();
                    }
                }, hour, Integer.parseInt(min), false);
                bedtimePicker.setTitle("Set wakeup time");
                bedtimePicker.show();
                return true;
            }
        });


    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String stringValue = newValue.toString();
        SharedPreferences.Editor myEditor = prefs.edit();

        if (preference instanceof ListPreference && preference.getKey().equals("ideal_sleep_income")) {
//            Toast toast = Toast.makeText(getContext(), "Ideal Sleep Income", Toast.LENGTH_SHORT);
//            toast.show();
        } else if (preference instanceof SwitchPreference && preference.getKey().equals("switch_bedtime_notification")) {
            if (((SwitchPreference) preference).isEnabled()) {
                // notifications
//                NotificationCompat.Builder builder = new NotificationCompat.Builder(getContext(), "sleepright")
//                        .setSmallIcon(R.drawable.ic_baseline_notifications_24)
//                        .setContentTitle("SleepRight Notification")
//                        .setContentText("Blank")
//                        .setPriority(NotificationCompat.PRIORITY_DEFAULT);
//
//                NotificationManagerCompat notificationManager = NotificationManagerCompat.from(getContext());

            }
        } else if (preference instanceof EditTextPreference) {
            try {
                if (preference.getKey().equals("pref_age")) {
                    String ageInput = newValue.toString();
                    if (!ageInput.isEmpty()) {
                        myEditor.putInt("userAge", Integer.parseInt(ageInput));
                        preference.setSummary(ageInput + " years old");
                    }

                } else if (preference.getKey().equals("pref_weight")) {
                    String weightInput = newValue.toString();
                    if (!weightInput.isEmpty()) {
                        myEditor.putInt("userWeight", Integer.parseInt(weightInput));
                        preference.setSummary(weightInput + " lb");
                    }
                }
                myEditor.apply();
                myEditor.commit();
            } catch (NumberFormatException nfe) {
                Toast toast = Toast.makeText(getContext(), "Please input only numbers", Toast.LENGTH_SHORT);
                toast.show();
            }
        } else {
//            Toast toast = Toast.makeText(getContext(), "Other Preference", Toast.LENGTH_SHORT);
//            toast.show();
        }
        return true;
    }


}