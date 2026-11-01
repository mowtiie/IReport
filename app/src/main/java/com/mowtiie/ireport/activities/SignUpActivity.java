package com.mowtiie.ireport.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivitySignUpBinding;

public class SignUpActivity extends AppCompatActivity {

    private ActivitySignUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupToolbar();
        binding.btnSignUp.setOnClickListener(v -> attemptSignUp());
    }

    private void setupToolbar() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void attemptSignUp() {
        binding.tilName.setError(null);
        binding.tilMobile.setError(null);
        binding.tilEmail.setError(null);
        binding.tilPassword.setError(null);
        String name = binding.etName.getText().toString().trim();
        String mobile = binding.etMobile.getText().toString().trim();
        String email = binding.etEmail.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (!validateFields(name, mobile, email, password)) {
            return;
        }
        String role = Data.ROLES[binding.tabRole.getSelectedTabPosition()];
        Data.signUp(this, role, name, mobile, email, password);
        Data.login(this, email, password);
        Toast.makeText(this, "Account created", Toast.LENGTH_SHORT).show();
        Data.openHome(this);
    }

    private boolean validateFields(String name, String mobile, String email, String password) {
        boolean valid = true;
        if (name.isEmpty()) {
            binding.tilName.setError("Enter your full name");
            valid = false;
        }
        if (!Data.isValidMobile(mobile)) {
            binding.tilMobile.setError("Enter 11 digits starting with 09");
            valid = false;
        } else if (Data.isUserFieldTaken(this, Data.COLUMN_PHONE, mobile, Data.NO_USER_ID)) {
            binding.tilMobile.setError("This mobile number is already registered");
            valid = false;
        }
        if (!Data.isValidEmail(email)) {
            binding.tilEmail.setError("Enter a valid email address");
            valid = false;
        } else if (Data.isUserFieldTaken(this, Data.COLUMN_EMAIL, email, Data.NO_USER_ID)) {
            binding.tilEmail.setError("This email is already registered");
            valid = false;
        }
        if (!Data.isValidPassword(password)) {
            binding.tilPassword.setError("Use at least " + Data.MIN_PASSWORD_LENGTH + " characters");
            valid = false;
        }
        return valid;
    }
}
