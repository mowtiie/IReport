package com.mowtiie.ireport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.mowtiie.ireport.adapters.GenericAdapter;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityAdminHomeBinding;
import com.mowtiie.ireport.databinding.ItemRowBinding;

import java.util.ArrayList;

public class AdminHomeActivity extends AppCompatActivity {

    private ActivityAdminHomeBinding binding;
    private GenericAdapter<String[]> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupList();
        bindListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showCounts();
        loadTickets();
    }

    private void setupList() {
        adapter = new GenericAdapter<>(new ArrayList<>(), this::bindTicketRow);
        binding.rvTickets.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTickets.setAdapter(adapter);
        binding.viewEmpty.tvEmptyMessage.setText("No tickets match these filters");
    }

    private void bindListeners() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        });
        ChipGroup.OnCheckedStateChangeListener reloadOnChange = (group, checkedIds) -> loadTickets();
        binding.chipGroupCategory.setOnCheckedStateChangeListener(reloadOnChange);
        binding.chipGroupStatus.setOnCheckedStateChangeListener(reloadOnChange);
        binding.chipGroupUrgency.setOnCheckedStateChangeListener(reloadOnChange);
        binding.chipGroupSort.setOnCheckedStateChangeListener(reloadOnChange);
    }

    private void showCounts() {
        StringBuilder counts = new StringBuilder();
        for (String status : Data.STATUS_FLOW) {
            if (counts.length() > 0) {
                counts.append(" · ");
            }
            counts.append(Data.statusLabel(status)).append(": ").append(Data.countTickets(this, status));
        }
        binding.tvCounts.setText(counts);
    }

    private void loadTickets() {
        String category = selectedValue(binding.chipGroupCategory, Data.CATEGORIES,
                binding.chipRoads, binding.chipSanitation, binding.chipLighting);
        String status = selectedValue(binding.chipGroupStatus, Data.STATUS_FLOW,
                binding.chipSubmitted, binding.chipUnderReview, binding.chipInProgress, binding.chipResolved);
        String urgency = selectedValue(binding.chipGroupUrgency, Data.URGENCIES,
                binding.chipLow, binding.chipMedium, binding.chipHigh);
        boolean sortByUrgency = binding.chipGroupSort.getCheckedChipId() == binding.chipMostUrgent.getId();

        ArrayList<String[]> tickets = Data.loadAllTickets(this, category, status, urgency, sortByUrgency);
        adapter.setItems(tickets);
        binding.viewEmpty.getRoot().setVisibility(tickets.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String selectedValue(ChipGroup group, String[] values, Chip... chips) {
        int checkedId = group.getCheckedChipId();
        for (int index = 0; index < chips.length; index++) {
            if (chips[index].getId() == checkedId) {
                return values[index];
            }
        }
        return null;
    }

    private void bindTicketRow(ItemRowBinding row, String[] ticket) {
        row.tvTitle.setText(ticket[Data.TICKET_TITLE]);
        row.tvSubtitle.setText(Data.categoryLabel(ticket[Data.TICKET_CATEGORY]) + " · "
                + Data.urgencyLabel(ticket[Data.TICKET_URGENCY]) + " urgency");
        row.tvMeta.setText("#" + ticket[Data.TICKET_ID] + " · " + ticket[Data.TICKET_LOCATION]
                + " · " + ticket[Data.TICKET_CREATED_AT]);
        Data.applyStatusChip(row.chipStatus, ticket[Data.TICKET_STATUS]);
        row.chipStatus.setVisibility(View.VISIBLE);
        row.getRoot().setOnClickListener(v -> openTicket(ticket[Data.TICKET_ID]));
    }

    private void openTicket(String ticketId) {
        Intent intent = new Intent(this, AdminTicketActivity.class);
        intent.putExtra(Data.EXTRA_TICKET_ID, ticketId);
        startActivity(intent);
    }
}
