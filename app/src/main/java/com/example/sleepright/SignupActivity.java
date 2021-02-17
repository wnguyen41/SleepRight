package com.example.sleepright;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sleepright.ui.login.LoginActivity;

import com.example.sleepright.ui.login.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class SignupActivity extends AppCompatActivity implements View.OnClickListener {

    //String email, password;
    private EditText emailInput, passInput;
    private FirebaseAuth mAuth;
    private TextView registerUser, signinTextView, signinPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        mAuth = FirebaseAuth.getInstance();

        signinTextView = findViewById(R.id.signin_text);
        Button button = (Button) findViewById(R.id.button_sign_in3);

        registerUser = (Button) findViewById(R.id.button_sign_in3);
        button.setEnabled(true);

        signinPage = (TextView) findViewById(R.id.signin_text);
        signinPage.setEnabled(true);

        signinPage.setOnClickListener(this);
        button.setOnClickListener(this);

        emailInput = (EditText) findViewById(R.id.textfield_email3);
        passInput = (EditText) findViewById(R.id.textfield_password3);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signin_text:
                startActivity(new Intent(this, LoginActivity.class));
                break;
            case R.id.button_sign_in3:
                register();
                break;
        }
    }

    private void register() {
        String email = emailInput.getText().toString().trim();
        String password = passInput.getText().toString().trim();
        if(email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailInput.setError("Valid email required");
            emailInput.requestFocus();
            return;
        }

        if(password.isEmpty()) {
            passInput.setError("Password is required");
            passInput.requestFocus();
            return;
        }

        if(password.length() < 5) {
            passInput.setError("At least 5 characters");
            passInput.requestFocus();
            return;
        }

        //loadingProgressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    User user = new User(email, password);

                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(SignupActivity.this, "User has been registered!", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                            }
                            else {
                                Toast.makeText(SignupActivity.this, "Register failed.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }else {
                    Toast.makeText(SignupActivity.this, "User has been registered!", Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}