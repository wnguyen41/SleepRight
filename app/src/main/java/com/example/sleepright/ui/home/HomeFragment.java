package com.example.sleepright.ui.home;

import android.os.Bundle;
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

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

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