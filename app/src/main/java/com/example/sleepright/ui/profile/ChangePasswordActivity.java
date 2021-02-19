package com.example.sleepright.ui.profile;

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

public class ChangePasswordActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_change_password);

//        dialogBuilder = new AlertDialog.Builder(this.getContext());
//        dialogBuilder.setView(root);
//        dialog = dialogBuilder.create();

        final EditText currentPasswordET = findViewById(R.id.edittxt_current_password);
        final EditText newPasswordET = findViewById(R.id.edittxt_new_password);
        final EditText confirmNewPasswordET = findViewById(R.id.edittxt_confirm_password);
        final Button savePasswordButton = findViewById(R.id.btn_save_password);

        savePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentPassword = currentPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();
                String confirmNewPassword = confirmNewPasswordET.getText().toString();



                if(newPassword.equals(confirmNewPassword)) {
                    updatePassword(currentPassword, newPassword);
//                    toast = Toast.makeText(getApplicationContext(), "Passwords match!", Toast.LENGTH_SHORT);
                }
                else {
                    Toast toast = Toast.makeText(getApplicationContext(), "Passwords don't match. Try again.", Toast.LENGTH_SHORT);
                    toast.show();
                }


            }
        });
    }

    public void updatePassword(String currentPassword, String newPassword) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null && user.getEmail() != null) {
            AuthCredential credential = EmailAuthProvider
                    .getCredential(user.getEmail(), currentPassword);

            user.reauthenticate(credential)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                user.updatePassword(newPassword)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Log.d(LOG_TAG, "User password updated.");
                                                    Toast toast = Toast.makeText(getApplicationContext(), "Password Updated!", Toast.LENGTH_SHORT);
                                                    toast.show();
                                                } else {
                                                    Log.d(LOG_TAG, "Error password not updated.");
                                                }
                                            }
                                        });
                            } else {
                                Log.d(LOG_TAG, "Authentication Failed.");
                                Toast toast = Toast.makeText(getApplicationContext(), "Authentication Failed.", Toast.LENGTH_SHORT);
                                toast.show();
                            }
                        }
                    });
        }
        else {
            Toast toast = Toast.makeText(getApplicationContext(), "User does not exist.", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

}
