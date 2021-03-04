package com.example.sleepright.ui.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sleepright.MainActivity;
import com.example.sleepright.R;
import com.example.sleepright.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private View root;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        root = inflater.inflate(R.layout.fragment_profile, container, false);
//        final TextView textView = root.findViewById(R.id.text_profile);
//        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                //textView.setText(s);
//            }
//        });

        final Button changeEmailButton = root.findViewById(R.id.button_change_email);
        final Button changePasswordButton = root.findViewById(R.id.button_change_password);
        final Button deleteAccountButton = root.findViewById(R.id.button_delete_account);
        final Button logOutButton = root.findViewById(R.id.button_log_out);
        final Button linkGoogleFit = root.findViewById(R.id.button_link_google_fit);


        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(root.getContext(), ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(root.getContext(), ChangePasswordActivity.class));
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(root.getContext(), DeleteAccountActivity.class));
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast toast = Toast.makeText(getContext(), "Logged out!", Toast.LENGTH_SHORT);
                toast.show();
                startActivity(new Intent(root.getContext(), LoginActivity.class));
            }
        });

        linkGoogleFit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Proceed with loging into google and pulling information from google fit
                Toast toast = Toast.makeText(getContext(), "Linking Google Fit!", Toast.LENGTH_SHORT);
                toast.show();
//                startActivity(new Intent(root.getContext(), LoginActivity.class));
            }
        });

        return root;

//        root = inflater.inflate(R.layout.fragment_profile, container, false);
//        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
//        final TextView textView = root.findViewById(R.id.text_home);
//        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

    }
}