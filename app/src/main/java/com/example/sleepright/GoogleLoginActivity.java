package com.example.sleepright;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class GoogleLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private GoogleSignInClient mGoogleSignInClient;
    private TextView mStatus, mImportCount, mLoggedAs;
    private Button mSignOut, mGoogleSignIn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_login);

        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mStatus = (TextView) findViewById(R.id.textView_status);
        mImportCount = (TextView) findViewById(R.id.textView_import_count);
        mLoggedAs = (TextView) findViewById(R.id.textView_logged_in_as);

        mSignOut = (Button) findViewById(R.id.button_sign_out);
        mGoogleSignIn = (Button) findViewById(R.id.sign_in_button);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);
        findViewById(R.id.button_import).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sign_in_button:
                signIn();
                break;
            case R.id.button_cancel:
                cancel();
                break;
            case R.id.button_import:
                //importData();
                break;
            case R.id.button_sign_out:
                signOut();
                break;
            // ...
        }
    }

    private void signOut() {
        mGoogleSignInClient.signOut()
                .addOnCompleteListener(this, task -> {
                    mLoggedAs.setText("Logged in as:");
                    mSignOut.setEnabled(false);
                    mGoogleSignIn.setEnabled(true);
                });
    }

    private void cancel() {
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        updateUI(account);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == 1) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);
        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);

            // Signed in successfully, show authenticated UI.
            updateUI(account); // TODO: Sucessfully logged in
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Toast toast = Toast.makeText(this, "Sign in result failed: " + e.getStatusCode(), Toast.LENGTH_SHORT);
            toast.show();
//            updateUI(null);
        }
    }

    //Change UI according to user data.
    public void updateUI(GoogleSignInAccount account){
        // TODO: This is temporary
        if(account != null){
            mLoggedAs.setText("Logged in as: " + account.getGivenName());
            mGoogleSignIn.setEnabled(false);
            mSignOut.setEnabled(true);
            Toast.makeText(this,"Google Sign In Successful",Toast.LENGTH_LONG).show();

        }else {
            mGoogleSignIn.setEnabled(true);
            mSignOut.setEnabled(false);
            Toast.makeText(this,"GoogleSignIn: Null",Toast.LENGTH_LONG).show();
        }

    }

}