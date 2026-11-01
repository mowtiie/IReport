package com.mowtiie.ireport.activities;

import android.os.Bundle;
import android.view.View;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.tabs.TabLayout;
import com.mowtiie.ireport.adapters.GenericAdapter;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityCommunityFeedBinding;
import com.mowtiie.ireport.databinding.ItemRowBinding;

import java.util.ArrayList;

public class CommunityFeedActivity extends AppCompatActivity {

    private static final int TAB_NEARBY = 0;

    private ActivityCommunityFeedBinding binding;
    private GenericAdapter<String[]> nearbyAdapter;
    private GenericAdapter<String[]> announcementAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityCommunityFeedBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupList();
        bindListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadFeed();
    }

    private void setupList() {
        nearbyAdapter = new GenericAdapter<>(new ArrayList<>(), this::bindNearbyRow);
        announcementAdapter = new GenericAdapter<>(new ArrayList<>(), this::bindAnnouncementRow);
        binding.rvFeed.setLayoutManager(new LinearLayoutManager(this));
    }

    private void bindListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.tabFeed.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                loadFeed();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
            }
        });
    }

    private void loadFeed() {
        if (binding.tabFeed.getSelectedTabPosition() == TAB_NEARBY) {
            loadNearbyIssues();
            return;
        }
        loadAnnouncements();
    }

    private void loadNearbyIssues() {
        String barangay = Data.findHomeBarangay(this);
        ArrayList<String[]> tickets = Data.loadNearbyTickets(this, barangay);
        binding.tvFeedHeader.setText("Issues reported by neighbors in " + barangay);
        binding.viewEmpty.tvEmptyMessage.setText("No nearby issues");
        binding.rvFeed.setAdapter(nearbyAdapter);
        nearbyAdapter.setItems(tickets);
        binding.viewEmpty.getRoot().setVisibility(tickets.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void loadAnnouncements() {
        ArrayList<String[]> announcements = Data.loadAnnouncements(this);
        binding.tvFeedHeader.setText("Announcements from the barangay office");
        binding.viewEmpty.tvEmptyMessage.setText("No announcements");
        binding.rvFeed.setAdapter(announcementAdapter);
        announcementAdapter.setItems(announcements);
        binding.viewEmpty.getRoot().setVisibility(announcements.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void bindNearbyRow(ItemRowBinding row, String[] ticket) {
        String ticketId = ticket[Data.TICKET_ID];
        row.tvTitle.setText(ticket[Data.TICKET_TITLE]);
        row.tvSubtitle.setText(Data.categoryLabel(ticket[Data.TICKET_CATEGORY]) + " · "
                + Data.urgencyLabel(ticket[Data.TICKET_URGENCY]) + " urgency");
        row.tvSubtitle.setVisibility(View.VISIBLE);
        row.tvMeta.setText(ticket[Data.TICKET_LOCATION] + " · " + ticket[Data.TICKET_CREATED_AT]);
        Data.applyStatusChip(row.chipStatus, ticket[Data.TICKET_STATUS]);
        row.chipStatus.setVisibility(View.VISIBLE);
        String upvoteLabel = Data.hasUpvoted(this, ticketId) ? "Upvoted" : "Upvote";
        row.btnAction.setText(upvoteLabel + " (" + ticket[Data.TICKET_UPVOTES] + ")");
        row.btnAction.setVisibility(View.VISIBLE);
        row.btnAction.setOnClickListener(v -> toggleUpvote(ticketId));
    }

    private void bindAnnouncementRow(ItemRowBinding row, String[] announcement) {
        row.tvTitle.setText(announcement[Data.ANNOUNCEMENT_TITLE]);
        row.tvSubtitle.setText(announcement[Data.ANNOUNCEMENT_BODY]);
        row.tvSubtitle.setVisibility(View.VISIBLE);
        row.tvMeta.setText(announcement[Data.ANNOUNCEMENT_CREATED_AT]);
        row.chipStatus.setVisibility(View.GONE);
        row.btnAction.setVisibility(View.GONE);
    }

    private void toggleUpvote(String ticketId) {
        Data.toggleUpvote(this, ticketId);
        loadNearbyIssues();
    }
}
