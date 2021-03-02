package com.example.sleepright.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sleepright.MainActivity;
import com.example.sleepright.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class ChangeEmailActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_change_email);

        final EditText newEmailET = findViewById(R.id.et_new_email);
        final EditText passwordET = findViewById(R.id.et_password_for_email_change);
        final Button cancelButton = findViewById(R.id.btn_cancel_change_email);
        final Button saveEmailButton = findViewById(R.id.button_save_email);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        saveEmailButton.setEnabled(true);
        saveEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Log.d(LOG_TAG, "save email button clicked");
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();

                if (passwordET.getText().toString().isEmpty()) {
                    passwordET.setError("Incorrect password.");
                    passwordET.requestFocus();
                    return;
                }

                if (newEmailET.getText().toString().isEmpty()) {
                    newEmailET.setError("Invalid email.");
                    newEmailET.requestFocus();
                    return;
                }

                if (user != null && user.getEmail() != null) {
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), passwordET.getText().toString());

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        String newEmail = newEmailET.getText().toString();

                                        user.updateEmail(newEmail).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    FirebaseDatabase.getInstance().getReference("Users").child(uid).child("username").setValue(newEmail);

                                                    Toast toast = Toast.makeText(getApplicationContext(), "User email address updated.", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                    onBackPressed();
                                                } else {
//                                                    Toast toast = Toast.makeText(getApplicationContext(), "User email address not updated.", Toast.LENGTH_SHORT);
//                                                    toast.show();
                                                    newEmailET.setError("Invalid email.");
                                                    newEmailET.requestFocus();
                                                }
                                            }
                                        });

                                    } else {
//                                        Toast toast = Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT);
//                                        toast.show();
                                        passwordET.setError("Incorrect password.");
                                        passwordET.requestFocus();
                                    }
                                }
                            });
                } else if (user == null) {
                    Log.d(LOG_TAG, "User does not exist; Change Email Failed.");
                    Toast toast2 = Toast.makeText(getApplicationContext(), "User does not exist.", Toast.LENGTH_SHORT);
                    toast2.show();
                }
            }
        });
    }

}

