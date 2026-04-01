package com.example.spareseat;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spareseat.api.ApiClient;
import com.example.spareseat.api.ApiService;
import com.example.spareseat.model.EventResponse;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoggedInActivity extends AppCompatActivity {

    private RecyclerView rvEvents;
    private EventAdapter eventAdapter;
    private TextView tvEventCount, tvEmpty;
    private LinearLayout llEmpty;
    private ProgressBar progressBar;
    private AutoCompleteTextView actvLocation, actvCategory, actvDate;

    private final List<EventResponse> allEvents = new ArrayList<>();
    private final List<EventResponse> filteredEvents = new ArrayList<>();

    private String selectedLocation = "All";
    private String selectedCategory = "All";
    private String selectedDate = "All";
    private String searchQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        // Greeting
        String name = SessionManager.getUserName(this);
        TextView tvGreeting = findViewById(R.id.tvGreeting);
        if (name != null && !name.isEmpty()) {
            tvGreeting.setText("Hi, " + name + "!");
        }

        // Logout
        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SessionManager.clear(this);
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        // Views
        rvEvents     = findViewById(R.id.rvEvents);
        tvEventCount = findViewById(R.id.tvEventCount);
        llEmpty      = findViewById(R.id.llEmpty);
        tvEmpty      = findViewById(R.id.tvEmpty);
        progressBar  = findViewById(R.id.progressBar);
        actvLocation = findViewById(R.id.actvLocation);
        actvCategory = findViewById(R.id.actvCategory);
        actvDate     = findViewById(R.id.actvDate);

        // RecyclerView
        rvEvents.setLayoutManager(new LinearLayoutManager(this));
        eventAdapter = new EventAdapter(filteredEvents);
        rvEvents.setAdapter(eventAdapter);

        // Search
        TextInputEditText etSearch = findViewById(R.id.etSearch);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                searchQuery = s.toString().toLowerCase().trim();
                applyFilters();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Filter dropdowns
        actvLocation.setOnItemClickListener((parent, view, position, id) -> {
            selectedLocation = parent.getItemAtPosition(position).toString();
            applyFilters();
        });
        actvCategory.setOnItemClickListener((parent, view, position, id) -> {
            selectedCategory = parent.getItemAtPosition(position).toString();
            applyFilters();
        });
        actvDate.setOnItemClickListener((parent, view, position, id) -> {
            selectedDate = parent.getItemAtPosition(position).toString();
            applyFilters();
        });

        fetchEvents();
    }

    private void fetchEvents() {
        progressBar.setVisibility(View.VISIBLE);
        rvEvents.setVisibility(View.GONE);
        llEmpty.setVisibility(View.GONE);

        ApiService apiService = ApiClient.getService();
        apiService.getAllEvents().enqueue(new Callback<List<EventResponse>>() {
            @Override
            public void onResponse(Call<List<EventResponse>> call, Response<List<EventResponse>> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    allEvents.clear();
                    allEvents.addAll(response.body());
                    populateFilterDropdowns();
                    applyFilters();
                } else {
                    showEmptyState("Could not load events. Try again later.");
                }
            }

            @Override
            public void onFailure(Call<List<EventResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                showEmptyState("Network error. Check your connection.");
            }
        });
    }

    private void populateFilterDropdowns() {
        Set<String> locations  = new LinkedHashSet<>();
        Set<String> categories = new LinkedHashSet<>();
        locations.add("All");
        categories.add("All");

        for (EventResponse e : allEvents) {
            if (e.getLocation() != null && !e.getLocation().isEmpty())
                locations.add(e.getLocation());
            String cat = (e.getCategory() != null && !e.getCategory().isEmpty())
                    ? e.getCategory() : "General";
            categories.add(cat);
        }

        actvLocation.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(locations)));
        actvCategory.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line, new ArrayList<>(categories)));
        actvDate.setAdapter(new ArrayAdapter<>(this,
                android.R.layout.simple_dropdown_item_1line,
                Arrays.asList("All", "Today", "This Week", "This Month")));
    }

    private void applyFilters() {
        filteredEvents.clear();

        for (EventResponse e : allEvents) {
            String cat = (e.getCategory() != null && !e.getCategory().isEmpty())
                    ? e.getCategory() : "General";

            boolean locMatch  = selectedLocation.equals("All")  || selectedLocation.equals(e.getLocation());
            boolean catMatch  = selectedCategory.equals("All")  || selectedCategory.equals(cat);
            boolean dateMatch = matchesDateFilter(selectedDate, e.getDate());
            boolean search    = searchQuery.isEmpty()
                    || (e.getTitle() != null       && e.getTitle().toLowerCase().contains(searchQuery))
                    || (e.getDescription() != null && e.getDescription().toLowerCase().contains(searchQuery))
                    || (e.getLocation() != null    && e.getLocation().toLowerCase().contains(searchQuery));

            if (locMatch && catMatch && dateMatch && search) {
                filteredEvents.add(e);
            }
        }

        eventAdapter.notifyDataSetChanged();

        int count = filteredEvents.size();
        tvEventCount.setText(count + " event" + (count != 1 ? "s" : "") + " found");

        if (count == 0) {
            showEmptyState("No events match your filters.");
        } else {
            rvEvents.setVisibility(View.VISIBLE);
            llEmpty.setVisibility(View.GONE);
        }
    }

    static boolean matchesDateFilter(String selectedDate, String eventDateStr) {
        if (selectedDate == null || selectedDate.equals("All")) return true;
        if (eventDateStr == null || eventDateStr.isEmpty()) return false;
        try {
            Date eventDate = new SimpleDateFormat("yyyy-MM-dd", Locale.US).parse(eventDateStr);
            Calendar event = Calendar.getInstance();
            event.setTime(eventDate);
            Calendar now = Calendar.getInstance();
            switch (selectedDate) {
                case "Today":
                    return event.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && event.get(Calendar.DAY_OF_YEAR) == now.get(Calendar.DAY_OF_YEAR);
                case "This Week":
                    return event.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && event.get(Calendar.WEEK_OF_YEAR) == now.get(Calendar.WEEK_OF_YEAR);
                case "This Month":
                    return event.get(Calendar.YEAR) == now.get(Calendar.YEAR)
                        && event.get(Calendar.MONTH) == now.get(Calendar.MONTH);
                default:
                    return true;
            }
        } catch (ParseException e) {
            return false;
        }
    }

    private void showEmptyState(String message) {
        rvEvents.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
        tvEmpty.setText(message);
    }
}
