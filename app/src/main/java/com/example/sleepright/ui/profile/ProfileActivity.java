package com.example.sleepright.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import androidx.fragment.app.Fragment;

import com.example.sleepright.MainActivity;
import com.example.sleepright.R;
import com.example.sleepright.SignupActivity;
import com.example.sleepright.ui.login.LoginActivity;

import io.realm.mongodb.User;

public class ProfileActivity extends AppCompatActivity {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private ProfileViewModel profileViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_profile);

        final Button changeEmailButton = findViewById(R.id.button_change_email);
        final Button changePasswordButton = findViewById(R.id.button_change_password);
        final Button deleteAccountButton = findViewById(R.id.button_delete_account);
        final Button logOutButton = findViewById(R.id.button_log_out);

        changeEmailButton.setEnabled(true);
        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // Log.d(LOG_TAG, "email button clicked.");
                startActivity(new Intent(ProfileActivity.this, ChangeEmailActivity.class));
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( ProfileActivity.this, ChangePasswordActivity.class));
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TO-DO
            }
        });

        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TO-DO
            }
        });
    }

    public void ChangeFragment(View v) {
        Fragment fragment;

        if(v == findViewById(R.id.button_change_email)) {
            fragment = new ChangeEmailFragment();
            FragmentManager fm = getSupportFragmentManager();
            FragmentTransaction ft = fm.beginTransaction();
            ft.replace(R.id.fragment_place, fragment);
            ft.commit();
        }
    }

}
