package com.mowtiie.ireport.activities;

import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityProfileBinding;

public class ProfileActivity extends AppCompatActivity {

    private ActivityProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        showUser();
        bindListeners();
    }

    private void showUser() {
        binding.tvRole.setText(Data.roleLabel(Data.USER_ROLE));
        binding.etName.setText(Data.USER_NAME);
        binding.etMobile.setText(Data.USER_PHONE);
        binding.etEmail.setText(Data.USER_EMAIL);
        binding.swNotifications.setChecked(Data.USER_NOTIFY_ENABLED);
    }

    private void bindListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.swNotifications.setOnCheckedChangeListener(
                (button, isChecked) -> Data.setNotifyEnabled(this, isChecked));
        binding.btnSave.setOnClickListener(v -> attemptSave());
        binding.btnLogout.setOnClickListener(v -> Data.logout(this));
    }

    private void attemptSave() {
        binding.tilName.setError(null);
        binding.tilMobile.setError(null);
        binding.tilEmail.setError(null);
        String name = binding.etName.getText().toString().trim();
        String mobile = binding.etMobile.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();

        if (!validateFields(name, mobile, email)) {
            return;
        }
        Data.updateProfile(this, name, mobile, email);
        Snackbar.make(binding.getRoot(), "Profile saved", Snackbar.LENGTH_SHORT).show();
    }

    private boolean validateFields(String name, String mobile, String email) {
        boolean valid = true;
        if (name.isEmpty()) {
            binding.tilName.setError("Enter your full name");
            valid = false;
        }
        if (!Data.isValidMobile(mobile)) {
            binding.tilMobile.setError("Enter 11 digits starting with 09");
            valid = false;
        } else if (Data.isUserFieldTaken(this, Data.COLUMN_PHONE, mobile, Data.USER_ID)) {
            binding.tilMobile.setError("This mobile number is already registered");
            valid = false;
        }
        if (!Data.isValidEmail(email)) {
            binding.tilEmail.setError("Enter a valid email address");
            valid = false;
        } else if (Data.isUserFieldTaken(this, Data.COLUMN_EMAIL, email, Data.USER_ID)) {
            binding.tilEmail.setError("This email is already registered");
            valid = false;
        }
        return valid;
    }
}
