package com.mowtiie.ireport.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityWorkerTaskBinding;

public class WorkerTaskActivity extends AppCompatActivity {

    private ActivityWorkerTaskBinding binding;
    private String ticketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityWorkerTaskBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ticketId = getIntent().getStringExtra(Data.EXTRA_TICKET_ID);
        bindListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTask();
    }

    private void bindListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.etAfterPhoto.setOnClickListener(v -> showAfterPhotoPicker());
        binding.btnInProgress.setOnClickListener(v -> markInProgress());
        binding.btnResolved.setOnClickListener(v -> attemptMarkResolved());
    }

    private void showTask() {
        String[] task = Data.loadTicket(this, ticketId);
        String status = task[Data.TICKET_STATUS];
        binding.tvTitle.setText(task[Data.TICKET_TITLE]);
        Data.applyStatusChip(binding.chipStatus, status);
        binding.tvCategory.setText(Data.categoryLabel(task[Data.TICKET_CATEGORY]) + " · "
                + Data.urgencyLabel(task[Data.TICKET_URGENCY]) + " urgency");
        binding.tvLocation.setText(task[Data.TICKET_LOCATION] + " · " + task[Data.TICKET_CREATED_AT]);
        binding.tvReporter.setText("Reported by " + Data.loadUserName(this, task[Data.TICKET_USER_ID]));
        binding.tvDescription.setText(task[Data.TICKET_DESCRIPTION]);
        showReportedMedia(task[Data.TICKET_MEDIA]);
        showAfterPhoto(task[Data.TICKET_RESOLUTION_MEDIA]);
        binding.btnInProgress.setEnabled(!Data.STATUS_IN_PROGRESS.equals(status) && !Data.STATUS_RESOLVED.equals(status));
        binding.btnResolved.setEnabled(!Data.STATUS_RESOLVED.equals(status));
    }

    private void showReportedMedia(String media) {
        binding.llMedia.setVisibility(media == null ? View.GONE : View.VISIBLE);
        if (media == null) {
            return;
        }
        binding.ivMedia.setImageResource(Data.drawableFor(this, media));
        binding.tvMediaName.setText(media);
    }

    private void showAfterPhoto(String afterPhoto) {
        binding.etAfterPhoto.setText(afterPhoto);
        binding.ivAfterPhoto.setVisibility(afterPhoto == null ? View.GONE : View.VISIBLE);
        if (afterPhoto != null) {
            binding.ivAfterPhoto.setImageResource(Data.drawableFor(this, afterPhoto));
        }
    }

    private void showAfterPhotoPicker() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Attach after photo")
                .setItems(Data.SAMPLE_AFTER_MEDIA, (dialog, which) -> {
                    binding.tilAfterPhoto.setError(null);
                    Data.setResolutionMedia(this, ticketId, Data.SAMPLE_AFTER_MEDIA[which]);
                    showTask();
                })
                .show();
    }

    private void markInProgress() {
        Data.changeTicketStatus(this, ticketId, Data.STATUS_IN_PROGRESS);
        showTask();
        Snackbar.make(binding.getRoot(), "Task marked as in progress", Snackbar.LENGTH_SHORT).show();
    }

    private void attemptMarkResolved() {
        binding.tilAfterPhoto.setError(null);
        if (Data.loadTicket(this, ticketId)[Data.TICKET_RESOLUTION_MEDIA] == null) {
            binding.tilAfterPhoto.setError("Attach an after photo before marking this task as resolved");
            return;
        }
        Data.changeTicketStatus(this, ticketId, Data.STATUS_RESOLVED);
        showTask();
        Snackbar.make(binding.getRoot(), "Task marked as resolved", Snackbar.LENGTH_SHORT).show();
    }
}
