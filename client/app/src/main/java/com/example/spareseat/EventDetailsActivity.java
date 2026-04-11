package com.example.spareseat;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.spareseat.api.ApiClient;
import com.example.spareseat.model.EventResponse;
import com.example.spareseat.model.ReservationRequest;
import com.example.spareseat.model.ReservationResponse;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EventDetailsActivity extends AppCompatActivity {
    public static final String EXTRA_EVENT = "extra_event";

    private EventResponse event;
    private TextView tvTitle;
    private TextView tvCategory;
    private TextView tvDate;
    private TextView tvLocation;
    private TextView tvDescription;
    private TextView tvCapacity;
    private TextView tvReservationStatus;
    private LinearLayout llReservationSection;
    private SwipeRefreshLayout swipeRefreshEventDetails;
    private TextInputLayout tilQuantity;
    private TextInputEditText etQuantity;
    private Button btnReserve;
    private boolean isHostUser;

    public static Intent createIntent(Context context, EventResponse event) {
        Intent intent = new Intent(context, EventDetailsActivity.class);
        intent.putExtra(EXTRA_EVENT, event);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        bindViews();

        ImageButton btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        event = readEventExtra();
        if (event == null) {
            showMissingEventState();
            return;
        }

        swipeRefreshEventDetails.setColorSchemeColors(Color.parseColor("#89F336"));
        swipeRefreshEventDetails.setOnRefreshListener(() -> refreshEvent(false));

        populateEvent();

        isHostUser = "HOST".equals(SessionManager.getUserRole(this));
        if (isHostUser) {
            llReservationSection.setVisibility(View.GONE);
            return;
        }

        btnReserve.setOnClickListener(v -> submitReservation());
    }

    private void bindViews() {
        tvTitle = findViewById(R.id.tvEventTitle);
        tvCategory = findViewById(R.id.tvEventCategory);
        tvDate = findViewById(R.id.tvEventDate);
        tvLocation = findViewById(R.id.tvEventLocation);
        tvDescription = findViewById(R.id.tvEventDescription);
        tvCapacity = findViewById(R.id.tvEventCapacity);
        tvReservationStatus = findViewById(R.id.tvReservationStatus);
        llReservationSection = findViewById(R.id.llReservationSection);
        swipeRefreshEventDetails = findViewById(R.id.swipeRefreshEventDetails);
        tilQuantity = findViewById(R.id.tilQuantity);
        etQuantity = findViewById(R.id.etQuantity);
        btnReserve = findViewById(R.id.btnReserve);
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshEvent(false);
    }

    @SuppressWarnings("deprecation")
    private EventResponse readEventExtra() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            return getIntent().getSerializableExtra(EXTRA_EVENT, EventResponse.class);
        }
        return (EventResponse) getIntent().getSerializableExtra(EXTRA_EVENT);
    }

    private void populateEvent() {
        tvTitle.setText(valueOrFallback(event.getTitle(), "Untitled Event"));

        String category = !TextUtils.isEmpty(event.getCategory()) ? event.getCategory() : "General";
        tvCategory.setText(category);

        tvDate.setText(valueOrFallback(event.getDate(), "Date to be announced"));
        tvLocation.setText(valueOrFallback(event.getLocation(), "Location to be announced"));
        tvDescription.setText(valueOrFallback(event.getDescription(), "No description available."));

        updateCapacityLabel();
    }

    private void showMissingEventState() {
        tvTitle.setText("Event details unavailable");
        tvCategory.setText("General");
        tvDate.setText("Date to be announced");
        tvLocation.setText("Location to be announced");
        tvDescription.setText("We couldn't load this event. Please go back and try again.");
        tvCapacity.setText("Capacity unavailable");
        llReservationSection.setVisibility(View.GONE);
    }

    private void submitReservation() {
        tilQuantity.setError(null);
        hideStatus();

        long userId = SessionManager.getUserId(this);
        if (userId == -1L) {
            showStatus("Please sign in again before reserving a spot.", false);
            return;
        }
        if (event.getEventId() == null) {
            showStatus("This event can't be reserved right now. Please try another one.", false);
            return;
        }

        String quantityValue = etQuantity.getText() != null
                ? etQuantity.getText().toString().trim() : "";
        if (TextUtils.isEmpty(quantityValue)) {
            tilQuantity.setError("Enter how many spots you want");
            return;
        }

        int quantity;
        try {
            quantity = Integer.parseInt(quantityValue);
        } catch (NumberFormatException e) {
            tilQuantity.setError("Enter a valid number");
            return;
        }

        if (quantity <= 0) {
            tilQuantity.setError("Quantity must be at least 1");
            return;
        }

        int remainingSpots = event.getRemainingSpots();
        if (quantity > remainingSpots) {
            tilQuantity.setError("Only " + remainingSpots + " spots are available");
            return;
        }

        btnReserve.setEnabled(false);
        btnReserve.setText("Signing Up...");

        ReservationRequest request = new ReservationRequest(userId, event.getEventId(), quantity);
        ApiClient.getService().createReservation(request).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                resetReserveButton();
                if (response.isSuccessful() && response.body() != null) {
                    ReservationResponse reservation = response.body();
                    String message = !TextUtils.isEmpty(reservation.getMessage())
                            ? reservation.getMessage()
                            : "You're signed up for " + valueOrFallback(event.getTitle(), "this event") + ".";
                    etQuantity.setText("1");
                    showStatus(message, true);
                    refreshEvent(false);
                    Toast.makeText(EventDetailsActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    showStatus(parseErrorBody(response, "We couldn't complete your signup."), false);
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                resetReserveButton();
                showStatus("Unable to connect. Please check your internet connection.", false);
            }
        });
    }

    private void resetReserveButton() {
        btnReserve.setEnabled(true);
        btnReserve.setText("Sign Up For Event");
    }

    private void showStatus(String message, boolean isSuccess) {
        tvReservationStatus.setVisibility(View.VISIBLE);
        tvReservationStatus.setText(message);
        tvReservationStatus.setTextColor(Color.parseColor(isSuccess ? "#247A00" : "#B3261E"));
    }

    private void hideStatus() {
        tvReservationStatus.setVisibility(View.GONE);
        tvReservationStatus.setText("");
    }

    private String parseErrorBody(Response<?> response, String fallback) {
        try {
            if (response.errorBody() == null) {
                return fallback;
            }
            String raw = response.errorBody().string();
            if (TextUtils.isEmpty(raw)) {
                return fallback;
            }
            JSONObject json = new JSONObject(raw);
            if (json.has("message")) {
                return json.getString("message");
            }
            if (json.has("error")) {
                return json.getString("error");
            }
            return fallback;
        } catch (Exception e) {
            return fallback;
        }
    }

    private String valueOrFallback(String value, String fallback) {
        return TextUtils.isEmpty(value) ? fallback : value;
    }

    private void refreshEvent(boolean showRefreshIndicator) {
        if (event == null || event.getEventId() == null) {
            return;
        }
        if (showRefreshIndicator && !swipeRefreshEventDetails.isRefreshing()) {
            swipeRefreshEventDetails.setRefreshing(true);
        }

        ApiClient.getService().getEventById(event.getEventId()).enqueue(new Callback<EventResponse>() {
            @Override
            public void onResponse(Call<EventResponse> call, Response<EventResponse> response) {
                swipeRefreshEventDetails.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    event = response.body();
                    populateEvent();
                } else if (showRefreshIndicator) {
                    Toast.makeText(EventDetailsActivity.this,
                            "Could not refresh event details right now.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<EventResponse> call, Throwable t) {
                swipeRefreshEventDetails.setRefreshing(false);
                if (showRefreshIndicator) {
                    Toast.makeText(EventDetailsActivity.this,
                            "Refresh failed. Check your connection.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void updateCapacityLabel() {
        int remainingSpots = event.getRemainingSpots();
        tvCapacity.setText(remainingSpots > 0
                ? remainingSpots + " spots available"
                : "Sold out");
        btnReserve.setEnabled(remainingSpots > 0);
        if (remainingSpots <= 0) {
            btnReserve.setText("Event Sold Out");
        } else {
            btnReserve.setText("Sign Up For Event");
        }
    }
}
