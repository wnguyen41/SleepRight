package com.example.sleepright.ui.login;

import android.app.Activity;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sleepright.MainActivity;
import com.example.sleepright.R;
import com.example.sleepright.SignupActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;


public class LoginActivity extends AppCompatActivity {

    private LoginViewModel loginViewModel;
    private TextView signup;
    private EditText email, password;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

//        Button button = (Button) findViewById(R.id.button_sign_in);
//
//        button.setOnClickListener(this);
//        button.setEnabled(true);

        signup = (TextView) findViewById(R.id.signup_text);
//        signup.setOnClickListener(signin());
//        signup.setEnabled(true);

        email = (EditText) findViewById(R.id.textfield_email);
        password = (EditText) findViewById(R.id.textfield_password);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        mAuth = FirebaseAuth.getInstance();
    }

    public void goToSignup(View v) {
        startActivity(new Intent(this, SignupActivity.class));
    }

    public void signin(View v) {
        String emailString = email.getText().toString().trim();
        String pass = password.getText().toString().trim();

        if(emailString.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(emailString).matches()) {
            email.setError("Valid email required");
            email.requestFocus();
            return;
        }

        if(pass.isEmpty()) {
            password.setError("Password is required");
            password.requestFocus();
            return;
        }

        if(pass.length() < 6) {
            password.setError("At least 6 characters");
            password.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(emailString,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText(LoginActivity.this, "Login successful.", Toast.LENGTH_LONG).show();
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                }else{
                    Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }

}