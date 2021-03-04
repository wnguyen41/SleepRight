package com.example.sleepright;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

public class ActivityChangeSleep extends AppCompatActivity {

    private SimpleDateFormat simpleDateFormat;
    private EditText start_date_time_in;
    private EditText end_date_time_in;
    private RatingBar ratingBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_sleep);

        end_date_time_in = findViewById(R.id.end_date_time_in);
        start_date_time_in = findViewById(R.id.start_date_time_in);
        simpleDateFormat = new SimpleDateFormat("EEE MMM d, h:mm a");
        ratingBar = findViewById(R.id.rating_bar);

        Intent intent = getIntent();
    }

    public void closeAddSleep(View view) {
        finish();
    }

    public void submit(View v) throws ParseException {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();

            // get the SleepSession's data
            Calendar startTimeCalender = Calendar.getInstance();
            startTimeCalender.setTime(simpleDateFormat.parse(start_date_time_in.getText().toString()));// all done
            Calendar endTimeCalender = Calendar.getInstance();
            endTimeCalender.setTime(simpleDateFormat.parse(end_date_time_in.getText().toString()));
            float rating = ratingBar.getRating();

            // create SleepSession object
            SleepSession newSleepSession = new SleepSession(uid,startTimeCalender.getTime(),endTimeCalender.getTime(),rating);

            // Read data and search for object and edit it
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("SleepSessions");
            //DatabaseReference newSession = db.push();
            //newSession.setValue(newSleepSession);

            ArrayList<SleepSession> sessions = new ArrayList<>();
            int position = getIntent().getIntExtra("ADAPTER_POSITION", -1);
            FirebaseDatabase.getInstance().getReference().child("SleepSessions").addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    int count = -1;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        SleepSession session = snap.getValue(SleepSession.class);
                        System.out.println("User: " + uid);
                        System.out.println("This is the inherited position: " + position);
                        if (session.userID.equals(uid)) {
                            System.out.println("Data: " + session.getUserID());
                            System.out.println("Session Rating: " + session.sessionRating);
                            sessions.add(session);
                            count++;
                            System.out.println("Size of Sessions List in submit: " + sessions.size());
                        }
                        if (count == position){
                            //change value of sleep session in database
                            session.setStartTime(startTimeCalender.getTime());
                            session.setEndTime(endTimeCalender.getTime());
                            session.setSessionRating(rating);
                            db.child(snap.getKey()).setValue(session);
                            System.out.println("This is the key that we changed: " + snap.getKey());
                        }
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });


            Toast.makeText(ActivityChangeSleep.this, "Submit successful.", Toast.LENGTH_LONG).show();
        }
        else{
            Toast.makeText(ActivityChangeSleep.this, "Submit unsuccessful.", Toast.LENGTH_LONG).show();
        }
//        FirebaseDatabase database = FirebaseDatabase.getInstance();
//        DatabaseReference myRef = database.getReference("message");
//
//        myRef.setValue("Hello, World!");
    }

    //    public void showTimePickerDialog(View v) {
//        DialogFragment newFragment = new TimePickerFragment();
//        newFragment.show(getSupportFragmentManager(), "timePicker");
//    }
//
//    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        // Do something with the time chosen by the user
//        TextView endTime = (TextView)findViewById(R.id.addsleepend);
//        endTime.setText(hourOfDay+":"+minute);
//
//    }
    public void startDateTimeDialog(View v){
        showDateTimeDialog(start_date_time_in);
    }
    public void endDateTimeDialog(View v){
        showDateTimeDialog(end_date_time_in);
    }

    private void showDateTimeDialog(final EditText date_time_in)
    {
        final Calendar c = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                c.set(Calendar.YEAR,year);
                c.set(Calendar.MONTH,month);
                c.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        c.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        c.set(Calendar.MINUTE,minute);

                        date_time_in.setText(simpleDateFormat.format(c.getTime()));
                    }
                };
                new TimePickerDialog(ActivityChangeSleep.this, timeSetListener, c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE),
                        DateFormat.is24HourFormat(ActivityChangeSleep.this)).show();
            }
        };
        new DatePickerDialog(ActivityChangeSleep.this, dateSetListener, c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
    }

    public void deleteRating(View view) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("SleepSessions");
        ArrayList<SleepSession> sessions = new ArrayList<>();
        int position = getIntent().getIntExtra("ADAPTER_POSITION", -1);
        FirebaseDatabase.getInstance().getReference().child("SleepSessions").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int count = -1;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    SleepSession session = snap.getValue(SleepSession.class);
                    System.out.println("User: " + uid);
                    System.out.println("This is the inherited position: " + position);
                    if (session.userID.equals(uid)) {
                        System.out.println("Data: " + session.getUserID());
                        System.out.println("Session Rating: " + session.sessionRating);
                        sessions.add(session);
                        count++;
                        System.out.println("Size of Sessions List in submit: " + sessions.size());
                    }
                    if (count == position){
                        //remove value at database
                        //db.child(snap.getKey()).setValue(session);
                        db.child(snap.getKey()).removeValue();
                        System.out.println("This is the key that we deleted: " + snap.getKey());
                        Toast.makeText(ActivityChangeSleep.this, "Session deleted, you may close this window.", Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
