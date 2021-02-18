package com.example.sleepright.ui.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sleepright.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;


public class ChangeEmailFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_change_email, container, false);

        final EditText newEmailET = root.findViewById(R.id.et_new_email);
        final EditText passwordET = root.findViewById(R.id.et_password_for_email_change);
        final Button saveEmailButton = root.findViewById(R.id.button_save_email);

        saveEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast toast = Toast.makeText(getContext(), "Change Email Button Clicked", Toast.LENGTH_SHORT);
                toast.show();
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                AuthCredential credential = EmailAuthProvider
                        .getCredential(user.getEmail(), passwordET.getText().toString());

                user.reauthenticate(credential)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if(task.isSuccessful()) {
                                    user.updateEmail(newEmailET.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Toast toast = Toast.makeText(getContext(), "User email address updated.", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                            else {
                                                Toast toast = Toast.makeText(getContext(), "User email address not updated.", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                        }
                                    });

                                }
                                else {
                                    Toast toast = Toast.makeText(getContext(), "Authentication Failed.", Toast.LENGTH_SHORT);
                                    toast.show();
                                }
                            }
                        });
            }
        });


        return root;
    }
}