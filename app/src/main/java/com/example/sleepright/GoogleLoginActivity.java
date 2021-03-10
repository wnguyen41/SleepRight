package com.example.sleepright;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
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
import com.google.android.gms.common.api.Result;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessActivities;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.FitnessStatusCodes;
import com.google.android.gms.fitness.data.Bucket;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataSet;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.data.Session;
import com.google.android.gms.fitness.data.Value;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.request.SessionReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.fitness.result.DataReadResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.sql.Time;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static java.text.DateFormat.getDateInstance;

public class GoogleLoginActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "debug";
    private static final int MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION = 1002;
    private static final int MY_ACTIVITYS_AUTH_REQUEST_CODE = 1003;
    private GoogleSignInClient mGoogleSignInClient;
    private GoogleSignInAccount mAccount = null;
    private TextView mStatus, mImportCount, mLoggedAs;
    private Button mSignOut, mGoogleSignIn;
    private ArrayList<SleepSession> mSessionList;
    private RecyclerView mRecyclerView_import;

    private final FitnessOptions fitnessOptions = FitnessOptions.builder()
            .addDataType(DataType.TYPE_SLEEP_SEGMENT)
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

        mRecyclerView_import = (RecyclerView) findViewById(R.id.recyclerView_import_list);

        findViewById(R.id.sign_in_button).setOnClickListener(this);
        findViewById(R.id.button_cancel).setOnClickListener(this);
        findViewById(R.id.button_import).setOnClickListener(this);
        findViewById(R.id.button_sign_out).setOnClickListener(this);

        mSessionList = new ArrayList<>();
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
            Log.i(TAG,"User has permissions.");
            importData();
        } else {
            Log.i(TAG,"User does not have permissions.");
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
        mSessionList.clear();

        // Setting a start and end date using a range of 1 week before this moment.
        Calendar cal = Calendar.getInstance();
        Date now = new Date();
        cal.setTime(now);
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -8);
        long startTime = cal.getTimeInMillis();

        java.text.DateFormat dateFormat = getDateInstance();
        Log.i(TAG, "Range Start: " + dateFormat.format(startTime));
        Log.i(TAG, "Range End: " + dateFormat.format(endTime));



//        GoogleSignInOptionsExtension fitnessOptions =
//                FitnessOptions.builder()
//                        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
//                        .build();

        DataReadRequest request = new DataReadRequest.Builder()
                .read(DataType.TYPE_SLEEP_SEGMENT)
                // The data request can specify multiple data types to return, effectively
                // combining multiple data queries into one call.
                // In this example, it's very unlikely that the request is for several hundred
                // datapoints each consisting of a few steps and a timestamp.  The more likely
                // scenario is wanting to see how many steps were walked per day, for 7 days.
//                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT)
                // Analogous to a "Group By" in SQL, defines how data should be aggregated.
                // bucketByTime allows for a time span, whereas bucketBySession would allow
                // bucketing by "sessions", which would need to be defined in code.
//                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(this, getGoogleAccount())
                .readData(request)
                .addOnSuccessListener(response -> {
                    mStatus.setText("Success");
                    mStatus.setTextColor(Color.parseColor("#00cc00"));
                    if (response.getBuckets().size() > 0) {
                        Log.i(TAG, "Number of returned buckets of DataSets is: "
                                + response.getBuckets().size());
                        for (Bucket bucket : response.getBuckets()) {
                            List<DataSet> dataSets = bucket.getDataSets();
                            for (DataSet dataSet : dataSets) {
                                dumpDataSet(dataSet);
                            }
                        }
                    } else if (response.getDataSets().size() > 0) {
                        Log.i(TAG, "Number of returned DataSets is: "
                                + response.getDataSets().size());
                        for (DataSet dataSet : response.getDataSets()) {
                            dumpDataSet(dataSet);
                        }
                    }
                        })
                .addOnFailureListener(response -> {
                    mStatus.setText("Failed");
                    mStatus.setTextColor(Color.parseColor("#ff3333"));
                    Log.i(TAG, "Sessions request failed. " + response.getMessage());
                });

    }

    private void dumpDataSet(DataSet dataSet) {

        String[] SLEEP_STAGE_NAMES = {
                "Unused",
                "Awake (during sleep)",
                "Sleep",
                "Out-of-bed",
                "Light sleep",
                "Deep sleep",
                "REM sleep"
        };

        Log.i(TAG, "Data returned for Data type: "+dataSet.getDataType().getName()+"");

        SimpleDateFormat dateFormat = new SimpleDateFormat("EEE MMM d, h:mm a");
        SimpleDateFormat hourMinFormat = new SimpleDateFormat("h:mm");

        long sleepHours = 0;

        for (DataPoint dp : dataSet.getDataPoints()) {
            //Log.i(TAG, dp.getOriginalDataSource().getStreamIdentifier().toString());
            Log.i(TAG, "Data point:");
            Log.i(TAG, "\tType: " + dp.getDataType().getName());
            Log.i(TAG, "\tStart: " + dateFormat.format(dp.getStartTime(TimeUnit.MILLISECONDS)));
            Log.i(TAG, "\tEnd: " + dateFormat.format(dp.getEndTime(TimeUnit.MILLISECONDS)));

            Duration dur = Duration.between(
                    Instant.ofEpochMilli(dp.getStartTime(TimeUnit.MILLISECONDS)),
                    Instant.ofEpochMilli(dp.getEndTime(TimeUnit.MILLISECONDS)));

            for(Field field : dp.getDataType().getFields()) {
                if(field.getName().contains("sleep")){
                    Log.i(TAG, "\tField: " + field.getName() +
                            " Value: " + SLEEP_STAGE_NAMES[dp.getValue(field).asInt()]);
                    long totalSec = dur.getSeconds();
                    long hours = totalSec / 3600;
                    long mins = totalSec % 3600 / 60;
                    Log.i(TAG, "\tField: Sleep duration: "+hours+":"+mins);
                }
                Log.i(TAG, "\tField: " + field.getName() +
                        " Value: " + dp.getValue(field));
            }
            Date startDate = new Date(dp.getStartTime(TimeUnit.MILLISECONDS));
            Date endDate = new Date(dp.getEndTime(TimeUnit.MILLISECONDS));
            SleepSession googleSession = new SleepSession(FirebaseAuth.getInstance().getCurrentUser().getUid(), startDate, endDate, 0);
            DatabaseReference db = FirebaseDatabase.getInstance().getReference("SleepSessions");
            DatabaseReference newSession = db.push();
            newSession.setValue(googleSession);

            mSessionList.add(googleSession);
        }

        displayList();



    }

    private void displayList() {
        ListAdapter listAdapter = new ListAdapter(mSessionList);
        mRecyclerView_import.setAdapter(listAdapter);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView_import.setLayoutManager(layoutManager);

        mImportCount.setText("Count: " + mSessionList.size());

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
        GoogleSignInAccount account = GoogleSignIn.getAccountForExtension(this, fitnessOptions);
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACTIVITY_RECOGNITION)
                != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACTIVITY_RECOGNITION},
                    MY_PERMISSIONS_REQUEST_ACTIVITY_RECOGNITION);

        }

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