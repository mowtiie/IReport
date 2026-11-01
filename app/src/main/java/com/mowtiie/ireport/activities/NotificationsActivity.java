package com.mowtiie.ireport.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.mowtiie.ireport.adapters.GenericAdapter;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityNotificationsBinding;
import com.mowtiie.ireport.databinding.ItemRowBinding;

import java.util.ArrayList;

public class NotificationsActivity extends AppCompatActivity {

    private static final String UNREAD_FLAG = "0";

    private ActivityNotificationsBinding binding;
    private GenericAdapter<String[]> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityNotificationsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        adapter = new GenericAdapter<>(new ArrayList<>(), this::bindNotificationRow);
        binding.rvNotifications.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotifications.setAdapter(adapter);
        binding.viewEmpty.tvEmptyMessage.setText("No notifications yet");
    }

    @Override
    protected void onResume() {
        super.onResume();
        ArrayList<String[]> notifications = Data.loadNotifications(this);
        adapter.setItems(notifications);
        binding.viewEmpty.getRoot().setVisibility(notifications.isEmpty() ? View.VISIBLE : View.GONE);
        Data.markNotificationsRead(this);
    }

    private void bindNotificationRow(ItemRowBinding row, String[] notification) {
        boolean isUnread = UNREAD_FLAG.equals(notification[Data.NOTIFICATION_IS_READ]);
        row.tvTitle.setText(notification[Data.NOTIFICATION_MESSAGE]);
        row.tvTitle.setTypeface(null, isUnread ? Typeface.BOLD : Typeface.NORMAL);
        row.tvSubtitle.setVisibility(View.GONE);
        row.tvMeta.setText(notification[Data.NOTIFICATION_CREATED_AT]);
        row.chipStatus.setVisibility(View.GONE);
        row.btnAction.setVisibility(View.GONE);
        row.getRoot().setOnClickListener(v -> openTicketDetail(notification[Data.NOTIFICATION_TICKET_ID]));
    }

    private void openTicketDetail(String ticketId) {
        Intent intent = new Intent(this, TicketDetailActivity.class);
        intent.putExtra(Data.EXTRA_TICKET_ID, ticketId);
        startActivity(intent);
    }
}
