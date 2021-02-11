package com.example.sleepright;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sleepright.ui.login.LoginActivity;

import com.mongodb.ConnectionString;
import com.mongodb.client.MongoClient;

import com.mongodb.MongoClientSettings;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

public class SignupActivity extends AppCompatActivity {

    String email, password;
    EditText emailInput;
    EditText passInput;

    ConnectionString connString = new ConnectionString(
            "mongodb+srv://<sleepright>:<password>@<cluster-address>/Cluster0?w=majority"
    );

    MongoClientSettings settings = MongoClientSettings.builder()
            .applyConnectionString(connString)
            .retryWrites(true)
            .build();
    MongoClient mongoClient = MongoClients.create(settings);
    MongoDatabase database = mongoClient.getDatabase("users");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        emailInput = (EditText) findViewById(R.id.textfield_email3);
        passInput = (EditText) findViewById(R.id.textfield_password3);
        final TextView signinTextView = findViewById(R.id.signin_text);

        signinTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = emailInput.getText().toString();
                password = passInput.getText().toString();
                startActivity(new Intent(SignupActivity.this, LoginActivity.class));
            }
        });
    }
}