package com.example.sleepright;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Build;
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
import com.google.android.gms.auth.api.signin.GoogleSignInOptionsExtension;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

public class GoogleLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "debug";
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount = null;
    private TextView mStatus, mImportCount, mLoggedAs;
    private Button mSignOut, mGoogleSignIn;
    private ArrayList<SleepSession> mSessionList;

    private final FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
            .build();

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
                fitSignIn();
                break;
            case R.id.button_sign_out:
                signOut();
                break;
            // ...
        }
    }

    private void fitSignIn() {
        if (oAuthPermissionsApproved()) {
            importData();
        } else {
            GoogleSignIn.requestPermissions(this, 1,
                    getGoogleAccount(), fitnessOptions);
        }
    }

    private boolean oAuthPermissionsApproved() {
        return GoogleSignIn.hasPermissions(getGoogleAccount(), fitnessOptions);
    }

    private GoogleSignInAccount getGoogleAccount() {
        return GoogleSignIn.getAccountForExtension(
                this, fitnessOptions);
    }




    private void importData(){

        // Setting a start and end date using a range of 1 week before this moment.
        ZonedDateTime endTime = LocalDateTime.now().atZone(ZoneId.systemDefault());
        ZonedDateTime startTime = endTime.minusMonths(3);
        Log.i(TAG, "Range Start: " + startTime.toString());
        Log.i(TAG, "Range End: " + endTime.toString());

        String[] SLEEP_STAGE_NAMES = {
                "Unused",
                "Awake (during sleep)",
                "Sleep",
                "Out-of-bed",
                "Light sleep",
                "Deep sleep",
                "REM sleep"
        };

//        GoogleSignInOptionsExtension fitnessOptions =
//                FitnessOptions.builder()
//                        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
//                        .build();

        SessionReadRequest request = new SessionReadRequest.Builder()
                .readSessionsFromAllApps()
                // By default, only activity sessions are included, so it is necessary to explicitly
                // request sleep sessions. This will cause activity sessions to be *excluded*.
                .includeSleepSessions()
                // Sleep segment data is required for details of the fine-granularity sleep, if it is present.
                .read(DataType.TYPE_SLEEP_SEGMENT)
                .setTimeInterval(startTime.toEpochSecond(), endTime.toEpochSecond(), TimeUnit.SECONDS)
                .build();

        Fitness.getSessionsClient(this, getGoogleAccount())
                .readSession(request)
                .addOnSuccessListener(response -> {
                            List<Session> sessions = response.getSessions();
                            Log.i(TAG, "Number of returned sessions is: " + sessions.size());
                            for (Session session : sessions) {
                                long sessionStart = session.getStartTime(TimeUnit.SECONDS);
                                long sessionEnd = session.getEndTime(TimeUnit.SECONDS);
                                Log.i(TAG, "\t* Sleep between " + sessionStart + " and " + sessionEnd);

                                // If the sleep session has finer granularity sub-components, extract them:
                                List<DataSet> dataSets = response.getDataSet(session);
                                for (DataSet dataSet : dataSets) {
                                    for (DataPoint point : dataSet.getDataPoints()) {
                                        int sleepStageVal = point.getValue(Field.FIELD_SLEEP_SEGMENT_TYPE).asInt();
                                        String sleepStage = SLEEP_STAGE_NAMES[sleepStageVal];
                                        long segmentStart = point.getStartTime(TimeUnit.SECONDS);
                                        long segmentEnd = point.getEndTime(TimeUnit.SECONDS);
                                        Log.i(TAG, "\t* Type " + sleepStage + " between " + segmentStart + " and " + segmentEnd);
                                    }
                                }

                            }
                        })
                .addOnFailureListener(response -> {
                    Log.i(TAG, "Sessions request failed. " + response.getMessage());
                });

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
            mAccount = account;
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