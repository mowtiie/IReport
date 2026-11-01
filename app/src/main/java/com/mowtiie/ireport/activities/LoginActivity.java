package com.mowtiie.ireport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityLoginBinding;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindListeners();
    }

    private void bindListeners() {
        binding.btnLogin.setOnClickListener(v -> attemptLogin());
        binding.btnSocialSignIn.setOnClickListener(v -> signInWithSocialAccount());
        binding.btnResetPassword.setOnClickListener(v -> startActivity(new Intent(this, ResetPasswordActivity.class)));
        binding.btnSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void attemptLogin() {
        binding.tilIdentifier.setError(null);
        binding.tilPassword.setError(null);
        String identifier = binding.etIdentifier.getText().toString().trim();
        String password = binding.etPassword.getText().toString();

        if (!validateFields(identifier, password)) {
            return;
        }
        if (!Data.login(this, identifier, password)) {
            binding.tilPassword.setError("Incorrect email, mobile number or password");
            return;
        }
        Data.openHome(this);
    }

    private boolean validateFields(String identifier, String password) {
        boolean valid = true;
        if (!Data.isValidEmail(identifier) && !Data.isValidMobile(identifier)) {
            binding.tilIdentifier.setError("Enter a valid email or an 11-digit mobile number starting with 09");
            valid = false;
        }
        if (password.isEmpty()) {
            binding.tilPassword.setError("Enter your password");
            valid = false;
        }
        return valid;
    }

    private void signInWithSocialAccount() {
        Data.login(this, Data.DEMO_RESIDENT_EMAIL, Data.DEMO_PASSWORD);
        Toast.makeText(this, "Simulated sign-in", Toast.LENGTH_SHORT).show();
        Data.openHome(this);
    }
}
