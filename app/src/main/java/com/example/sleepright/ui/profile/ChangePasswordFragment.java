package com.example.sleepright.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sleepright.MainActivity;
import com.example.sleepright.R;



public class ChangePasswordFragment extends Fragment {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
//    private AlertDialog.Builder dialogBuilder;
//    private AlertDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(LOG_TAG, "ChangePasswordFragment");
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_change_password, container, false);

    }


}