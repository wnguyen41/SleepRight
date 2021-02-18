package com.example.sleepright.ui.profile;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sleepright.MainActivity;
import com.example.sleepright.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class ChangePasswordFragment extends Fragment {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
//    private AlertDialog.Builder dialogBuilder;
//    private AlertDialog dialog;

    public static ChangePasswordFragment newInstance() {
        return new ChangePasswordFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View root = inflater.inflate(R.layout.fragment_change_password, container, false);
//        dialogBuilder = new AlertDialog.Builder(this.getContext());
//        dialogBuilder.setView(root);
//        dialog = dialogBuilder.create();

        final EditText currentPasswordET = root.findViewById(R.id.edittxt_current_password);
        final EditText newPasswordET = root.findViewById(R.id.edittxt_new_password);
        final EditText confirmNewPasswordET = root.findViewById(R.id.edittxt_confirm_password);
        final Button savePasswordButton = root.findViewById(R.id.button_change_password);
//        Log.d(LOG_TAG, "in change password");
        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(LOG_TAG, "clicked");
                String currentPassword = currentPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();
                String confirmNewPassword = confirmNewPasswordET.getText().toString();


                if(newPassword.equals(confirmNewPassword)) {
                    updatePassword(currentPassword, newPassword);
                }
                else {
                    Toast toast = Toast.makeText(getContext(), "Passwords don't match. Try again.", Toast.LENGTH_SHORT);
                    toast.show();
                }

            }
        });


        return root;
    }

    public void updatePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        AuthCredential credential = EmailAuthProvider
                .getCredential(user.getEmail(), currentPassword);

        user.reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            user.updatePassword(newPassword)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                Log.d(LOG_TAG, "User password updated.");
                                                Toast toast = Toast.makeText(getContext(), "Password Updated!", Toast.LENGTH_SHORT);
                                                toast.show();
                                            }
                                            else {
                                                Log.d(LOG_TAG, "Error password not updated.");
                                            }
                                        }
                                    });
                        }
                        else {
                            Log.d(LOG_TAG, "Authentication Failed.");
                            Toast toast = Toast.makeText(getContext(), "Authentication Failed.", Toast.LENGTH_SHORT);
                            toast.show();
                        }
                    }
                });
    }


}