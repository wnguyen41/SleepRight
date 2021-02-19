package com.example.sleepright.ui.profile;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


import com.example.sleepright.MainActivity;
import com.example.sleepright.R;


public class ChangeEmailFragment extends Fragment {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.d(LOG_TAG, "ChangeEmailFragment");
        return inflater.inflate(R.layout.fragment_change_email, container, false);
    }
}