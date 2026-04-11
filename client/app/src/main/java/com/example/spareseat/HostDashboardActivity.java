package com.example.spareseat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spareseat.api.ApiClient;
import com.example.spareseat.model.EventCreateRequest;
import com.example.spareseat.model.EventResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HostDashboardActivity extends AppCompatActivity {

    private RecyclerView rvHostEvents;
    private HostEventAdapter adapter;
    private TextView tvEventCount, tvEmpty;
    private LinearLayout llEmpty;
    private ProgressBar progressBar;

    private final List<EventResponse> events = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_host_dashboard);

        TextView tvGreeting = findViewById(R.id.tvGreeting);
        Button btnLogout = findViewById(R.id.btnLogout);
        Button btnCreateEvent = findViewById(R.id.btnCreateEvent);
        tvEventCount = findViewById(R.id.tvEventCount);
        rvHostEvents = findViewById(R.id.rvHostEvents);
        llEmpty = findViewById(R.id.llEmpty);
        tvEmpty = findViewById(R.id.tvEmpty);
        progressBar = findViewById(R.id.progressBar);

        String name = SessionManager.getUserName(this);
        tvGreeting.setText("Hi, " + (name.isEmpty() ? "there" : name) + "!");

        btnLogout.setOnClickListener(v -> {
            SessionManager.clear(this);
            startActivity(new Intent(this, WelcomeActivity.class));
            finish();
        });

        btnCreateEvent.setOnClickListener(v -> showEventForm(null));

        adapter = new HostEventAdapter(events, new HostEventAdapter.MenuClickListener() {
            @Override
            public void onEdit(EventResponse event) {
                showEventForm(event);
            }

            @Override
            public void onDelete(EventResponse event) {
                confirmDelete(event);
            }
        });

        rvHostEvents.setLayoutManager(new LinearLayoutManager(this));
        rvHostEvents.setAdapter(adapter);

        fetchMyEvents();
    }

    // ── FETCH ────────────────────────────────────────────────────────────────

    private void fetchMyEvents() {
        long userId = SessionManager.getUserId(this);
        if (userId == -1L) {
            showEmpty("Could not identify user. Please log in again.");
            return;
        }

        progressBar.setVisibility(View.VISIBLE);
        rvHostEvents.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);

        ApiClient.getService().getEventsByOrganizer(userId).enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    events.clear();
                    events.addAll(response.body());
                    adapter.notifyDataSetChanged();
                    updateListVisibility();
                } else {
                    showEmpty("Failed to load events.");
                }
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showEmpty("Unable to connect. Please check your internet connection.");
            }
        });
    }

    // ── CREATE / EDIT FORM ───────────────────────────────────────────────────

    private void showEventForm(EventResponse existing) {
        View formView = LayoutInflater.from(this).inflate(R.layout.dialog_event_form, null);

        TextInputLayout tilTitle    = formView.findViewById(R.id.tilTitle);
        TextInputLayout tilDate     = formView.findViewById(R.id.tilDate);
        TextInputLayout tilLocation = formView.findViewById(R.id.tilLocation);
        TextInputLayout tilCapacity = formView.findViewById(R.id.tilCapacity);

        TextInputEditText etTitle       = formView.findViewById(R.id.etTitle);
        TextInputEditText etDescription = formView.findViewById(R.id.etDescription);
        TextInputEditText etDate        = formView.findViewById(R.id.etDate);
        TextInputEditText etLocation    = formView.findViewById(R.id.etLocation);
        TextInputEditText etCategory    = formView.findViewById(R.id.etCategory);
        TextInputEditText etCapacity    = formView.findViewById(R.id.etCapacity);

        if (existing != null) {
            etTitle.setText(existing.getTitle());
            etDescription.setText(existing.getDescription());
            etDate.setText(existing.getDate());
            etLocation.setText(existing.getLocation());
            etCategory.setText(existing.getCategory());
            etCapacity.setText(existing.getEventCapacity() > 0
                    ? String.valueOf(existing.getEventCapacity()) : "");
        }

        String dialogTitle = existing == null ? "Create Event" : "Edit Event";

        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(dialogTitle)
                .setView(formView)
                .setPositiveButton(existing == null ? "Create" : "Save", null)
                .setNegativeButton("Cancel", null)
                .create();

        dialog.setOnShowListener(d -> {
            Button btnSave = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            btnSave.setOnClickListener(v -> {
                tilTitle.setError(null);
                tilDate.setError(null);
                tilLocation.setError(null);
                tilCapacity.setError(null);

                String title    = text(etTitle);
                String desc     = text(etDescription);
                String date     = text(etDate);
                String location = text(etLocation);
                String category = text(etCategory);
                String capStr   = text(etCapacity);

                boolean valid = true;
                if (TextUtils.isEmpty(title)) {
                    tilTitle.setError("Required");
                    valid = false;
                }
                if (!isValidDateFormat(date)) {
                    tilDate.setError("Required — use YYYY-MM-DD");
                    valid = false;
                }
                if (TextUtils.isEmpty(location)) {
                    tilLocation.setError("Required");
                    valid = false;
                }
                int capacity = 0;
                if (TextUtils.isEmpty(capStr)) {
                    tilCapacity.setError("Required");
                    valid = false;
                } else {
                    try {
                        capacity = Integer.parseInt(capStr);
                        if (capacity <= 0) {
                            tilCapacity.setError("Must be greater than 0");
                            valid = false;
                        }
                    } catch (NumberFormatException e) {
                        tilCapacity.setError("Enter a valid number");
                        valid = false;
                    }
                }

                if (!valid) return;

                btnSave.setEnabled(false);
                long organizerId = SessionManager.getUserId(this);
                EventCreateRequest request = new EventCreateRequest(
                        organizerId, title, desc, date, location, category, capacity);

                if (existing == null) {
                    submitCreate(request, dialog, btnSave);
                } else {
                    submitUpdate(existing.getEventId(), request, dialog, btnSave);
                }
            });
        });

        dialog.show();
    }

    // ── CREATE ───────────────────────────────────────────────────────────────

    private void submitCreate(EventCreateRequest request, AlertDialog dialog, Button btnSave) {
        ApiClient.getService().createEvent(request).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    events.add(0, response.body());
                    adapter.notifyItemInserted(0);
                    rvHostEvents.scrollToPosition(0);
                    updateListVisibility();
                    dialog.dismiss();
                    Toast.makeText(HostDashboardActivity.this, "Event created!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HostDashboardActivity.this, "Failed to create event.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(HostDashboardActivity.this, "Connection error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── UPDATE ───────────────────────────────────────────────────────────────

    private void submitUpdate(Long eventId, EventCreateRequest request, AlertDialog dialog, Button btnSave) {
        ApiClient.getService().updateEvent(eventId, request).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                btnSave.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    int index = indexById(eventId);
                    if (index != -1) {
                        events.set(index, response.body());
                        adapter.notifyItemChanged(index);
                    }
                    dialog.dismiss();
                    Toast.makeText(HostDashboardActivity.this, "Event updated!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HostDashboardActivity.this, "Failed to update event.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                btnSave.setEnabled(true);
                Toast.makeText(HostDashboardActivity.this, "Connection error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── DELETE ───────────────────────────────────────────────────────────────

    private void confirmDelete(EventResponse event) {
        new AlertDialog.Builder(this)
                .setTitle("Delete Event")
                .setMessage("Delete \"" + event.getTitle() + "\"? This cannot be undone.")
                .setPositiveButton("Delete", (d, w) -> submitDelete(event))
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void submitDelete(EventResponse event) {
        ApiClient.getService().deleteEvent(event.getEventId()).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {
                if (response.isSuccessful()) {
                    int index = indexById(event.getEventId());
                    if (index != -1) {
                        events.remove(index);
                        adapter.notifyItemRemoved(index);
                        updateListVisibility();
                    }
                    Toast.makeText(HostDashboardActivity.this, "Event deleted.", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(HostDashboardActivity.this, "Failed to delete event.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {
                Toast.makeText(HostDashboardActivity.this, "Connection error.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // ── HELPERS ──────────────────────────────────────────────────────────────

    private int indexById(Long eventId) {
        for (int i = 0; i < events.size(); i++) {
            if (eventId.equals(events.get(i).getEventId())) return i;
        }
        return -1;
    }

    private void updateListVisibility() {
        if (events.isEmpty()) {
            showEmpty("No events yet. Create your first one!");
        } else {
            tvEventCount.setText("My Events (" + events.size() + ")");
            rvHostEvents.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    private void showEmpty(String message) {
        tvEmpty.setText(message);
        llEmpty.setVisibility(View.VISIBLE);
        rvHostEvents.setVisibility(View.GONE);
    }

    public static boolean isValidDateFormat(String date) {
        return date != null && !date.isEmpty() && date.matches("\\d{4}-\\d{2}-\\d{2}");
    }

    private static String text(TextInputEditText et) {
        return et.getText() != null ? et.getText().toString().trim() : "";
    }
}
