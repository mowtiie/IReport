package com.mowtiie.ireport.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mowtiie.ireport.adapters.GenericAdapter;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityWorkerHomeBinding;
import com.mowtiie.ireport.databinding.ItemRowBinding;

import java.util.ArrayList;

public class WorkerHomeActivity extends AppCompatActivity {

    private ActivityWorkerHomeBinding binding;
    private GenericAdapter<String[]> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityWorkerHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupList();
        bindListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadTasks();
    }

    private void setupList() {
        adapter = new GenericAdapter<>(new ArrayList<>(), this::bindTaskRow);
        binding.rvTasks.setLayoutManager(new LinearLayoutManager(this));
        binding.rvTasks.setAdapter(adapter);
        binding.viewEmpty.tvEmptyMessage.setText("No tasks assigned to you");
    }

    private void bindListeners() {
        binding.toolbar.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, ProfileActivity.class));
            return true;
        });
        binding.chipGroupStatus.setOnCheckedStateChangeListener((group, checkedIds) -> loadTasks());
    }

    private void loadTasks() {
        ArrayList<String[]> tasks = Data.loadAssignedTickets(this, selectedStatus());
        adapter.setItems(tasks);
        binding.viewEmpty.getRoot().setVisibility(tasks.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private String selectedStatus() {
        int checkedId = binding.chipGroupStatus.getCheckedChipId();
        if (checkedId == binding.chipSubmitted.getId()) {
            return Data.STATUS_SUBMITTED;
        }
        if (checkedId == binding.chipUnderReview.getId()) {
            return Data.STATUS_UNDER_REVIEW;
        }
        if (checkedId == binding.chipInProgress.getId()) {
            return Data.STATUS_IN_PROGRESS;
        }
        if (checkedId == binding.chipResolved.getId()) {
            return Data.STATUS_RESOLVED;
        }
        return null;
    }

    private void bindTaskRow(ItemRowBinding row, String[] task) {
        row.tvTitle.setText(task[Data.TICKET_TITLE]);
        row.tvSubtitle.setText(Data.categoryLabel(task[Data.TICKET_CATEGORY]) + " · "
                + Data.urgencyLabel(task[Data.TICKET_URGENCY]) + " urgency");
        row.tvMeta.setText("#" + task[Data.TICKET_ID] + " · " + task[Data.TICKET_LOCATION]);
        Data.applyStatusChip(row.chipStatus, task[Data.TICKET_STATUS]);
        row.chipStatus.setVisibility(View.VISIBLE);
        row.getRoot().setOnClickListener(v -> openTask(task[Data.TICKET_ID]));
    }

    private void openTask(String ticketId) {
        Intent intent = new Intent(this, WorkerTaskActivity.class);
        intent.putExtra(Data.EXTRA_TICKET_ID, ticketId);
        startActivity(intent);
    }
}
