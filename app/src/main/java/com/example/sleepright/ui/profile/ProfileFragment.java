package com.example.sleepright.ui.profile;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.sleepright.R;

import org.w3c.dom.Text;

public class ProfileFragment extends Fragment {

    private ProfileViewModel mViewModel;
    private View root;
    private AlertDialog.Builder dialogBuilder;
    private AlertDialog dialog;


    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        mViewModel =
                new ViewModelProvider(this).get(ProfileViewModel.class);
        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        final TextView textView = root.findViewById(R.id.text_profile);
        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                //textView.setText(s);
            }
        });

        final Button changeEmailButton = root.findViewById(R.id.button_change_email);
        final Button changePasswordButton = root.findViewById(R.id.button_change_password);
        final Button deleteAccountButton = root.findViewById(R.id.button_delete_account);
//        final Button logOutButton = root.findViewById(R.id.button_log_out);


        changeEmailButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log.d(LOG_TAG, "email button clicked.");
                Intent intent = new Intent(root.getContext(), ChangeEmailActivity.class);
                startActivity(intent);
            }
        });

        changePasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent( root.getContext(), ChangePasswordActivity.class));
            }
        });

        deleteAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createConfirmAccountDeletionDialog();
            }
        });

//        logOutButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//            }
//        });

        return root;

//        root = inflater.inflate(R.layout.fragment_profile, container, false);
//        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        // TODO: Use the ViewModel
//        final TextView textView = root.findViewById(R.id.text_home);
//        mViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                textView.setText(s);
//            }
//        });

    }


    public void createConfirmAccountDeletionDialog() {
        dialogBuilder = new AlertDialog.Builder(this.getContext());
        final View contactPopupView = getLayoutInflater().inflate(R.layout.fragment_delete_account, null);

        TextView popup_Title = contactPopupView.findViewById(R.id.confirm_account_deletion);
        TextView popup_Subtext = contactPopupView.findViewById(R.id.account_deletion_info);
        Button cancel_button = contactPopupView.findViewById(R.id.button_cancel);
        Button delete_button = contactPopupView.findViewById(R.id.button_delete);

        dialogBuilder.setView(contactPopupView);
        dialog = dialogBuilder.create();
        dialog.show();

        cancel_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        delete_button.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: delete account
            }
        });
    }

}