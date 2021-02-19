package com.example.sleepright;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sleepright.ui.login.LoginActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPasswordActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);
    }

    public void goToSignin(View view) {
        startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
    }

    public void resetPassword(View view) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        EditText emailAddress = (EditText) findViewById(R.id.editText_email);
        String emailString = emailAddress.getText().toString().trim();

        if(emailString.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            emailAddress.setError("Valid email required");
            emailAddress.requestFocus();
            return;
        }

        auth.sendPasswordResetEmail(emailString)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(ForgotPasswordActivity.this, "Email sent to " + emailString, Toast.LENGTH_LONG).show();
                            startActivity(new Intent(ForgotPasswordActivity.this, LoginActivity.class));
                        }
                        else
                        {
                            Toast.makeText(ForgotPasswordActivity.this, emailString + " does not exist.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}