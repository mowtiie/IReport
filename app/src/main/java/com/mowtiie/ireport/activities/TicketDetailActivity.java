package com.mowtiie.ireport.activities;

import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.color.MaterialColors;
import com.google.android.material.textview.MaterialTextView;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityTicketDetailBinding;

import java.util.Arrays;

public class TicketDetailActivity extends AppCompatActivity {

    private static final String STEP_DONE = "✓  ";
    private static final String STEP_CURRENT = "●  ";
    private static final String STEP_PENDING = "○  ";

    private ActivityTicketDetailBinding binding;
    private String ticketId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityTicketDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ticketId = getIntent().getStringExtra(Data.EXTRA_TICKET_ID);
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTicket(Data.loadTicket(this, ticketId));
    }

    private void showTicket(String[] ticket) {
        binding.tvTitle.setText(ticket[Data.TICKET_TITLE]);
        Data.applyStatusChip(binding.chipStatus, ticket[Data.TICKET_STATUS]);
        binding.tvCategory.setText(Data.categoryLabel(ticket[Data.TICKET_CATEGORY]) + " · "
                + Data.urgencyLabel(ticket[Data.TICKET_URGENCY]) + " urgency");
        binding.tvLocation.setText(ticket[Data.TICKET_LOCATION] + " · " + ticket[Data.TICKET_CREATED_AT]);
        binding.tvDescription.setText(ticket[Data.TICKET_DESCRIPTION]);
        showAssignment(ticket[Data.TICKET_ASSIGNED_DEPT]);
        showTimeline(ticket[Data.TICKET_STATUS]);
        showMedia(ticket[Data.TICKET_MEDIA]);
        showResolution(ticket[Data.TICKET_RESOLUTION_MEDIA]);
    }

    private void showAssignment(String department) {
        binding.tvAssignment.setVisibility(department == null ? View.GONE : View.VISIBLE);
        binding.tvAssignment.setText("Assigned to: " + department);
    }

    private void showTimeline(String currentStatus) {
        MaterialTextView[] steps = {
                binding.tvStepSubmitted, binding.tvStepUnderReview,
                binding.tvStepInProgress, binding.tvStepResolved
        };
        int currentIndex = Arrays.asList(Data.STATUS_FLOW).indexOf(currentStatus);
        for (int index = 0; index < steps.length; index++) {
            styleTimelineStep(steps[index], Data.STATUS_FLOW[index], index, currentIndex);
        }
    }

    private void styleTimelineStep(MaterialTextView step, String status, int index, int currentIndex) {
        boolean isCurrent = index == currentIndex;
        String marker = index < currentIndex ? STEP_DONE : isCurrent ? STEP_CURRENT : STEP_PENDING;
        int colorAttr = isCurrent
                ? androidx.appcompat.R.attr.colorPrimary
                : com.google.android.material.R.attr.colorOnSurfaceVariant;
        step.setText(marker + Data.statusLabel(status));
        step.setTextColor(MaterialColors.getColor(step, colorAttr));
        step.setTypeface(null, isCurrent ? Typeface.BOLD : Typeface.NORMAL);
    }

    private void showMedia(String media) {
        binding.llMedia.setVisibility(media == null ? View.GONE : View.VISIBLE);
        if (media == null) {
            return;
        }
        Data.showMedia(this, binding.ivMedia, media);
        binding.tvMediaName.setText(media);
    }

    private void showResolution(String resolutionMedia) {
        binding.llResolution.setVisibility(resolutionMedia == null ? View.GONE : View.VISIBLE);
        if (resolutionMedia == null) {
            return;
        }
        Data.showMedia(this, binding.ivResolution, resolutionMedia);
        binding.tvResolutionName.setText(resolutionMedia);
    }
}
