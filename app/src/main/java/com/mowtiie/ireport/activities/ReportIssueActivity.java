package com.mowtiie.ireport.activities;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityReportIssueBinding;

public class ReportIssueActivity extends AppCompatActivity {

    private static final int MIN_DESCRIPTION_LENGTH = 10;

    private ActivityReportIssueBinding binding;
    private String selectedCategory;
    private String selectedUrgency;
    private String selectedMedia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityReportIssueBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setupDropdowns();
        bindListeners();
    }

    private void setupDropdowns() {
        String[] categoryLabels = new String[Data.CATEGORIES.length];
        for (int index = 0; index < categoryLabels.length; index++) {
            categoryLabels[index] = Data.categoryLabel(Data.CATEGORIES[index]);
        }
        String[] urgencyLabels = new String[Data.URGENCIES.length];
        for (int index = 0; index < urgencyLabels.length; index++) {
            urgencyLabels[index] = Data.urgencyLabel(Data.URGENCIES[index]);
        }
        binding.ddCategory.setAdapter(newDropdownAdapter(categoryLabels));
        binding.ddUrgency.setAdapter(newDropdownAdapter(urgencyLabels));
        binding.ddBarangay.setAdapter(newDropdownAdapter(Data.BARANGAYS));
        binding.ddCategory.setOnItemClickListener((parent, view, position, id) -> selectedCategory = Data.CATEGORIES[position]);
        binding.ddUrgency.setOnItemClickListener((parent, view, position, id) -> selectedUrgency = Data.URGENCIES[position]);
    }

    private ArrayAdapter<String> newDropdownAdapter(String[] items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }

    private void bindListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnAttachMedia.setOnClickListener(v -> showMediaPicker());
        binding.btnSubmit.setOnClickListener(v -> attemptSubmit());
    }

    private void showMediaPicker() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Attach photo or video")
                .setItems(Data.SAMPLE_MEDIA, (dialog, which) -> {
                    selectedMedia = Data.SAMPLE_MEDIA[which];
                    binding.tvMediaName.setText(selectedMedia);
                })
                .show();
    }

    private void attemptSubmit() {
        binding.tilCategory.setError(null);
        binding.tilTitle.setError(null);
        binding.tilDescription.setError(null);
        binding.tilUrgency.setError(null);
        binding.tilBarangay.setError(null);
        binding.tilStreet.setError(null);
        String title = binding.etTitle.getText().toString().trim();
        String description = binding.etDescription.getText().toString().trim();
        String barangay = binding.ddBarangay.getText().toString();
        String street = binding.etStreet.getText().toString().trim();

        if (!validateFields(title, description, barangay, street)) {
            return;
        }
        String location = barangay + Data.LOCATION_SEPARATOR + street;
        Data.createTicket(this, selectedCategory, title, description, selectedUrgency, location, selectedMedia);
        Toast.makeText(this, "Report submitted", Toast.LENGTH_SHORT).show();
        finish();
    }

    private boolean validateFields(String title, String description, String barangay, String street) {
        boolean valid = true;
        if (selectedCategory == null) {
            binding.tilCategory.setError("Choose a category");
            valid = false;
        }
        if (title.isEmpty()) {
            binding.tilTitle.setError("Enter a short title");
            valid = false;
        }
        if (description.length() < MIN_DESCRIPTION_LENGTH) {
            binding.tilDescription.setError("Describe the issue in at least " + MIN_DESCRIPTION_LENGTH + " characters");
            valid = false;
        }
        if (selectedUrgency == null) {
            binding.tilUrgency.setError("Choose an urgency");
            valid = false;
        }
        if (barangay.isEmpty()) {
            binding.tilBarangay.setError("Choose a barangay");
            valid = false;
        }
        if (street.isEmpty()) {
            binding.tilStreet.setError("Enter a street or landmark");
            valid = false;
        }
        return valid;
    }
}
