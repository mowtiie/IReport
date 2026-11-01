package com.mowtiie.ireport.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityResetPasswordBinding;

public class ResetPasswordActivity extends AppCompatActivity {

    private ActivityResetPasswordBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityResetPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnResetPassword.setOnClickListener(v -> attemptReset());
    }

    private void attemptReset() {
        binding.tilIdentifier.setError(null);
        binding.tilNewPassword.setError(null);
        String identifier = binding.etIdentifier.getText().toString().trim();
        String newPassword = binding.etNewPassword.getText().toString();

        if (!validateFields(identifier, newPassword)) {
            return;
        }
        if (!Data.resetPassword(this, identifier, newPassword)) {
            binding.tilIdentifier.setError("No account found for this email or mobile number");
            return;
        }
        Toast.makeText(this, "Password updated. Please log in.", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateFields(String identifier, String newPassword) {
        boolean valid = true;
        if (!Data.isValidEmail(identifier) && !Data.isValidMobile(identifier)) {
            binding.tilIdentifier.setError("Enter a valid email or an 11-digit mobile number starting with 09");
            valid = false;
        }
        if (!Data.isValidPassword(newPassword)) {
            binding.tilNewPassword.setError("Use at least " + Data.MIN_PASSWORD_LENGTH + " characters");
            valid = false;
        }
        return valid;
    }
}
