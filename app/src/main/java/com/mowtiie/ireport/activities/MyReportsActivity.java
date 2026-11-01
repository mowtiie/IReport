package com.mowtiie.ireport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mowtiie.ireport.R;
import com.mowtiie.ireport.adapters.GenericAdapter;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityMyReportsBinding;
import com.mowtiie.ireport.databinding.ItemRowBinding;

import java.util.ArrayList;

public class MyReportsActivity extends AppCompatActivity {

    private ActivityMyReportsBinding binding;
    private GenericAdapter<String[]> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityMyReportsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupList();
        bindListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTickets();
    }

    private void setupList() {
        adapter = new GenericAdapter<>(new ArrayList<>(), this::bindTicketRow);
        binding.rvReports.setLayoutManager(new LinearLayoutManager(this));
        binding.rvReports.setAdapter(adapter);
        binding.viewEmpty.tvEmptyMessage.setText("No reports found");
    }

    private void bindListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> loadTickets());
    }

    private void loadTickets() {
        ArrayList<String[]> tickets = Data.loadUserTickets(this, selectedStatus());
        adapter.setItems(tickets);
        binding.viewEmpty.getRoot().setVisibility(tickets.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String selectedStatus() {
        int checkedId = binding.chipGroupStatus.getCheckedChipId();
        if (checkedId == R.id.chipSubmitted) {
            return Data.STATUS_SUBMITTED;
        }
        if (checkedId == R.id.chipUnderReview) {
            return Data.STATUS_UNDER_REVIEW;
        }
        if (checkedId == R.id.chipInProgress) {
            return Data.STATUS_IN_PROGRESS;
        }
        if (checkedId == R.id.chipResolved) {
            return Data.STATUS_RESOLVED;
        }
        return null;
    }

    private void bindTicketRow(ItemRowBinding row, String[] ticket) {
        row.tvTitle.setText(ticket[Data.TICKET_TITLE]);
        row.tvSubtitle.setText(Data.categoryLabel(ticket[Data.TICKET_CATEGORY]) + " · "
                + Data.urgencyLabel(ticket[Data.TICKET_URGENCY]) + " urgency");
        row.tvMeta.setText("#" + ticket[Data.TICKET_ID] + " · " + ticket[Data.TICKET_CREATED_AT]);
        Data.applyStatusChip(row.chipStatus, ticket[Data.TICKET_STATUS]);
        row.chipStatus.setVisibility(View.VISIBLE);
        row.getRoot().setOnClickListener(v -> openTicketDetail(ticket[Data.TICKET_ID]));
    }

    private void openTicketDetail(String ticketId) {
        Intent intent = new Intent(this, TicketDetailActivity.class);
        intent.putExtra(Data.EXTRA_TICKET_ID, ticketId);
        startActivity(intent);
    }
}
