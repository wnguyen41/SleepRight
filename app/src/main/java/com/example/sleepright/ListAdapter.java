package com.example.sleepright;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ListAdapter extends RecyclerView.Adapter {

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
        return 7;
    }

    private class ListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView day_month;
        private TextView hour_min;
        private RatingBar rating_bar;

        public ListViewHolder(View itemView){
            super(itemView);
            day_month = (TextView) itemView.findViewById(R.id.textView_day_month);
            hour_min = (TextView) itemView.findViewById(R.id.textView_hour_min);
            rating_bar = (RatingBar) itemView.findViewById(R.id.ratingBar);


            itemView.setOnClickListener(this);
        }

        public void bindView(int position){
            //TODO: set textview and ratingbar to data from our database here
            day_month.setText("Testing_Day_Month");
            hour_min.setText("Testing_Hour_Min");
            rating_bar.setRating((float)3.5);
        }


        @Override
        public void onClick(View v) {
            // TODO: onclick should go to fragment that allows you to edit the rating. Passes data from the itemView to the fragment.
        }
    }
}
