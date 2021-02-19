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


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private LoginViewModel loginViewModel;
    private TextView signup;
    private EditText email, password;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button button = (Button) findViewById(R.id.button_sign_in);

        button.setOnClickListener(this);
        button.setEnabled(true);

        signup = (TextView) findViewById(R.id.signup_text);
        signup.setOnClickListener(this);
        signup.setEnabled(true);

        email = (EditText) findViewById(R.id.textfield_email);
        password = (EditText) findViewById(R.id.textfield_password);
        progressBar = (ProgressBar) findViewById(R.id.loading);
        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.signup_text:
                startActivity(new Intent(this, SignupActivity.class));
                break;
            case R.id.button_sign_in:
                signin();
                break;
        }
    }

    private void signin() {
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
/*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = new ViewModelProvider(this, new LoginViewModelFactory())
                .get(LoginViewModel.class);

        final EditText usernameEditText = findViewById(R.id.textfield_email);
        final EditText passwordEditText = findViewById(R.id.textfield_password);
        final Button loginButton = findViewById(R.id.button_sign_in);
        final ProgressBar loadingProgressBar = findViewById(R.id.loading);
        final TextView signupTextView = findViewById(R.id.signup_text);

        loginViewModel.getLoginFormState().observe(this, new Observer<LoginFormState>() {
            @Override
            public void onChanged(@Nullable LoginFormState loginFormState) {
                if (loginFormState == null) {
                    return;
                }
                loginButton.setEnabled(loginFormState.isDataValid());
                if (loginFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(loginFormState.getUsernameError()));
                }
                if (loginFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(loginFormState.getPasswordError()));
                }
            }
        });

        loginViewModel.getLoginResult().observe(this, new Observer<LoginResult>() {
            @Override
            public void onChanged(@Nullable LoginResult loginResult) {
                if (loginResult == null) {
                    return;
                }
                loadingProgressBar.setVisibility(View.GONE);
                if (loginResult.getError() != null) {
                    showLoginFailed(loginResult.getError());
                }
                if (loginResult.getSuccess() != null) {

                    updateUiWithUser(loginResult.getSuccess());
                }
                setResult(Activity.RESULT_OK);

                //Complete and destroy login activity once successful
                finish();
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                loginViewModel.loginDataChanged(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    loginViewModel.login(usernameEditText.getText().toString(),
                            passwordEditText.getText().toString());
                }
                return false;
            }
        });

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadingProgressBar.setVisibility(View.VISIBLE);
                loginViewModel.login(usernameEditText.getText().toString(),
                        passwordEditText.getText().toString());
            }
        });

        signupTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, SignupActivity.class));
            }
        });
    }

    private void updateUiWithUser(LoggedInUserView model) {
        String welcome = getString(R.string.welcome) + model.getDisplayName();
        // TODO : initiate successful logged in experience
//        Successful login allows use to got to MainActivity page
        startActivity(new Intent(LoginActivity.this, MainActivity.class));
        Toast.makeText(getApplicationContext(), welcome, Toast.LENGTH_LONG).show();
    }

    private void showLoginFailed(@StringRes Integer errorString) {
        Toast.makeText(getApplicationContext(), errorString, Toast.LENGTH_SHORT).show();
    }*/
}