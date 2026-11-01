package com.mowtiie.ireport.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityResidentHomeBinding;

public class ResidentHomeActivity extends AppCompatActivity {

    private ActivityResidentHomeBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityResidentHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        bindListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        binding.tvWelcome.setText("Welcome, " + Data.USER_NAME);
        binding.tvRole.setText(Data.roleLabel(Data.USER_ROLE));
    }

    private void bindListeners() {
        binding.btnReportIssue.setOnClickListener(v -> open(ReportIssueActivity.class));
        binding.btnMyReports.setOnClickListener(v -> open(MyReportsActivity.class));
        binding.btnCommunityFeed.setOnClickListener(v -> open(CommunityFeedActivity.class));
        binding.btnNotifications.setOnClickListener(v -> open(NotificationsActivity.class));
        binding.btnProfile.setOnClickListener(v -> open(ProfileActivity.class));
    }

    private void open(Class<?> screen) {
        startActivity(new Intent(this, screen));
    }
}
