package com.example.sleepright;

import android.content.Context;
import android.content.Intent;
import android.se.omapi.Session;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;

public class ListAdapter extends RecyclerView.Adapter {
    public ArrayList<SleepSession> sessions;

    public ListAdapter(ArrayList<SleepSession> inputSessions){
        sessions = inputSessions;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new ListViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((ListViewHolder) holder).bindView(position);
    }

    @Override
    public int getItemCount() {
        System.out.println("Sessions count: " + sessions.size());
        //return sessions.size();
        return sessions.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView day_month;
        private TextView hour_min;
        private RatingBar rating_bar;

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = user.getUid();
        DatabaseReference db = FirebaseDatabase.getInstance().getReference("SleepSessions");

        public ListViewHolder(View itemView){
            super(itemView);
            day_month = (TextView) itemView.findViewById(R.id.textView_day_month);
            hour_min = (TextView) itemView.findViewById(R.id.textView_hour_min);
            rating_bar = (RatingBar) itemView.findViewById(R.id.ratingBar);
            itemView.setOnClickListener(this);
        }

        public void bindView(int position){
            //TODO: set textview and ratingbar to data from our database here
            System.out.println(position + " position");
            if (!sessions.isEmpty()) {
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

                day_month.setText(date);
                hour_min.setText(timeSlept + " h");
                rating_bar.setRating(rating);
            }
        }


        @Override
        public void onClick(View v) {
            // TODO: onclick should go to fragment that allows you to edit the rating. Passes data from the itemView to the fragment.
            Intent intent = new Intent (v.getContext(), ActivityChangeSleep.class);
            int pos = getAdapterPosition();
            intent.putExtra("ADAPTER_POSITION", pos);
            v.getContext().startActivity(intent);
        }
    }
}
