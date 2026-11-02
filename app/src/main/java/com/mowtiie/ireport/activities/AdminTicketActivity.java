package com.mowtiie.ireport.activities;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.snackbar.Snackbar;
import com.mowtiie.ireport.adapters.GenericAdapter;
import com.mowtiie.ireport.data.Data;
import com.mowtiie.ireport.databinding.ActivityAdminTicketBinding;
import com.mowtiie.ireport.databinding.ItemRowBinding;
import com.mowtiie.ireport.databinding.SheetAddNoteBinding;

import java.util.ArrayList;

public class AdminTicketActivity extends AppCompatActivity {

    private final ActivityResultLauncher<PickVisualMediaRequest> proofPicker =
            registerForActivityResult(new ActivityResultContracts.PickVisualMedia(), this::onProofPicked);

    private ActivityAdminTicketBinding binding;
    private GenericAdapter<String[]> notesAdapter;
    private ArrayList<String[]> workers;
    private String ticketId;
    private String selectedDepartment;
    private String selectedWorkerId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        binding = ActivityAdminTicketBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        ticketId = getIntent().getStringExtra(Data.EXTRA_TICKET_ID);
        setupDropdowns();
        setupNotesList();
        bindListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        showTicket(Data.loadTicket(this, ticketId));
        loadNotes();
    }

    private void setupDropdowns() {
        String[] statusLabels = new String[Data.STATUS_FLOW.length];
        for (int index = 0; index < statusLabels.length; index++) {
            statusLabels[index] = Data.statusLabel(Data.STATUS_FLOW[index]);
        }
        workers = Data.loadWorkers(this);
        String[] workerNames = new String[workers.size()];
        for (int index = 0; index < workerNames.length; index++) {
            workerNames[index] = workers.get(index)[Data.USER_ROW_NAME];
        }
        String[] departments = {Data.DEPT_PUBLIC_WORKS, Data.DEPT_SANITATION_OFFICE, Data.DEPT_ELECTRICAL_TEAM};
        binding.ddStatus.setAdapter(newDropdownAdapter(statusLabels));
        binding.ddDepartment.setAdapter(newDropdownAdapter(departments));
        binding.ddWorker.setAdapter(newDropdownAdapter(workerNames));
        binding.ddStatus.setOnItemClickListener((parent, view, position, id) -> updateStatus(Data.STATUS_FLOW[position]));
        binding.ddDepartment.setOnItemClickListener((parent, view, position, id) -> selectedDepartment = departments[position]);
        binding.ddWorker.setOnItemClickListener((parent, view, position, id) -> selectedWorkerId = workers.get(position)[Data.USER_ROW_ID]);
    }

    private ArrayAdapter<String> newDropdownAdapter(String[] items) {
        return new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
    }

    private void setupNotesList() {
        notesAdapter = new GenericAdapter<>(new ArrayList<>(), this::bindNoteRow);
        binding.rvNotes.setLayoutManager(new LinearLayoutManager(this));
        binding.rvNotes.setNestedScrollingEnabled(false);
        binding.rvNotes.setAdapter(notesAdapter);
        binding.viewEmpty.tvEmptyMessage.setText("No notes yet");
    }

    private void bindListeners() {
        binding.toolbar.setNavigationOnClickListener(v -> finish());
        binding.btnAssign.setOnClickListener(v -> attemptAssign());
        binding.btnAttachProof.setOnClickListener(v -> proofPicker.launch(Data.imageRequest()));
        binding.btnAddNote.setOnClickListener(v -> showAddNoteSheet());
    }

    private void showTicket(String[] ticket) {
        binding.tvTitle.setText(ticket[Data.TICKET_TITLE]);
        Data.applyStatusChip(binding.chipStatus, ticket[Data.TICKET_STATUS]);
        binding.tvCategory.setText(Data.categoryLabel(ticket[Data.TICKET_CATEGORY]) + " · "
                + Data.urgencyLabel(ticket[Data.TICKET_URGENCY]) + " urgency");
        binding.tvLocation.setText(ticket[Data.TICKET_LOCATION] + " · " + ticket[Data.TICKET_CREATED_AT]);
        binding.tvReporter.setText("Reported by " + Data.loadUserName(this, ticket[Data.TICKET_USER_ID]));
        binding.tvMediaName.setText("Attached media: " + (ticket[Data.TICKET_MEDIA] == null ? "none" : ticket[Data.TICKET_MEDIA]));
        binding.tvDescription.setText(ticket[Data.TICKET_DESCRIPTION]);
        binding.ddStatus.setText(Data.statusLabel(ticket[Data.TICKET_STATUS]), false);
        showAssignment(ticket);
        showResolution(ticket[Data.TICKET_RESOLUTION_MEDIA]);
    }

    private void showAssignment(String[] ticket) {
        selectedDepartment = ticket[Data.TICKET_ASSIGNED_DEPT];
        selectedWorkerId = ticket[Data.TICKET_ASSIGNED_TO];
        binding.ddDepartment.setText(selectedDepartment, false);
        binding.ddWorker.setText(selectedWorkerId == null ? null : Data.loadUserName(this, selectedWorkerId), false);
    }

    private void showResolution(String resolutionMedia) {
        boolean hasProof = resolutionMedia != null;
        binding.ivResolution.setVisibility(hasProof ? View.VISIBLE : View.GONE);
        if (!hasProof) {
            binding.tvResolutionName.setText("No after photo attached");
            return;
        }
        Data.showMedia(this, binding.ivResolution, resolutionMedia);
        binding.tvResolutionName.setText(resolutionMedia);
    }

    private void loadNotes() {
        ArrayList<String[]> notes = Data.loadNotes(this, ticketId);
        notesAdapter.setItems(notes);
        binding.viewEmpty.getRoot().setVisibility(notes.isEmpty() ? View.VISIBLE : View.GONE);
    }

    private void bindNoteRow(ItemRowBinding row, String[] note) {
        row.tvTitle.setText(note[Data.NOTE_TEXT]);
        row.tvSubtitle.setVisibility(View.GONE);
        row.tvMeta.setText(note[Data.NOTE_CREATED_AT]);
        row.chipStatus.setVisibility(View.GONE);
        row.btnAction.setVisibility(View.GONE);
    }

    private void updateStatus(String status) {
        Data.changeTicketStatus(this, ticketId, status);
        showTicket(Data.loadTicket(this, ticketId));
        Snackbar.make(binding.getRoot(), "Status updated", Snackbar.LENGTH_SHORT).show();
    }

    private void attemptAssign() {
        binding.tilWorker.setError(null);
        if (selectedDepartment == null && selectedWorkerId == null) {
            binding.tilWorker.setError("Choose a department or a field worker");
            return;
        }
        Data.assignTicket(this, ticketId, selectedWorkerId, selectedDepartment);
        showTicket(Data.loadTicket(this, ticketId));
        Snackbar.make(binding.getRoot(), "Ticket assigned", Snackbar.LENGTH_SHORT).show();
    }

    private void onProofPicked(Uri uri) {
        if (uri == null) {
            return;
        }
        String mediaName = Data.saveMedia(this, uri);
        if (mediaName == null) {
            Snackbar.make(binding.getRoot(), "Could not attach that file", Snackbar.LENGTH_SHORT).show();
            return;
        }
        Data.setResolutionMedia(this, ticketId, mediaName);
        showTicket(Data.loadTicket(this, ticketId));
        Snackbar.make(binding.getRoot(), "After photo attached", Snackbar.LENGTH_SHORT).show();
    }

    private void showAddNoteSheet() {
        BottomSheetDialog sheetDialog = new BottomSheetDialog(this);
        SheetAddNoteBinding sheet = SheetAddNoteBinding.inflate(getLayoutInflater());
        sheetDialog.setContentView(sheet.getRoot());
        sheet.btnSaveNote.setOnClickListener(v -> {
            sheet.tilNote.setError(null);
            String note = sheet.etNote.getText().toString().trim();
            if (note.isEmpty()) {
                sheet.tilNote.setError("Enter a note");
                return;
            }
            Data.addNote(this, ticketId, note);
            sheetDialog.dismiss();
            loadNotes();
            Snackbar.make(binding.getRoot(), "Note added", Snackbar.LENGTH_SHORT).show();
        });
        sheetDialog.show();
    }
}
