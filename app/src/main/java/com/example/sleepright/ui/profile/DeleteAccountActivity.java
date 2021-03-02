package com.example.sleepright.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.sleepright.MainActivity;
import com.example.sleepright.R;
import com.example.sleepright.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class DeleteAccountActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_delete_account);

        final TextView title = findViewById(R.id.confirm_account_deletion);
        final TextView info1 = findViewById(R.id.account_deletion_info);
        final TextView info2 = findViewById(R.id.account_deletion_info2);
        final Button cancelButton = findViewById(R.id.button_cancel_deletion);
        final Button deleteButton = findViewById(R.id.button_delete);
        final EditText passwordET = findViewById(R.id.et_delete_account_password);

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                String uid = user.getUid();

                if (passwordET.getText().toString().isEmpty()) {
                    passwordET.setError("Incorrect password.");
                    passwordET.requestFocus();
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
                                        FirebaseDatabase.getInstance().getReference("Users").child(uid).removeValue();
                                        user.delete()
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
//                                                            dialog.dismiss();
                                                            Log.d(LOG_TAG, "User account deleted: " + uid);
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Account Deleted.", Toast.LENGTH_SHORT);
                                                            toast.show();
                                                            onBackPressed();
                                                            startActivity(new Intent(DeleteAccountActivity.this, LoginActivity.class));
                                                        } else {
                                                            Log.d(LOG_TAG, "User account could not be deleted.");
                                                            Toast toast = Toast.makeText(getApplicationContext(), "Account could not be deleted.", Toast.LENGTH_SHORT);
                                                            toast.show();
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
                    Toast toast = Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

}
