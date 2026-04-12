package com.example.spareseat;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.spareseat.api.ApiClient;
import com.example.spareseat.model.ReservationResponse;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyReservationsActivity extends AppCompatActivity {

    private RecyclerView rvReservations;
    private ReservationAdapter reservationAdapter;
    private TextView tvReservationCount;
    private TextView tvEmpty;
    private LinearLayout llEmpty;
    private ProgressBar progressBar;
    private SwipeRefreshLayout swipeRefreshReservations;
    private BottomNavigationView bottomNavigationView;

    private final List<ReservationResponse> reservations = new ArrayList<>();
    private final Set<Long> cancellingReservationIds = new HashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_reservations);

        TextView tvSubtitle = findViewById(R.id.tvSubtitle);
        String name = SessionManager.getUserName(this);
        if (!TextUtils.isEmpty(name)) {
            tvSubtitle.setText(name + ", you can manage your reservations here.");
        }

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            SessionManager.clear(this);
            Intent intent = new Intent(this, WelcomeActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });

        rvReservations = findViewById(R.id.rvReservations);
        tvReservationCount = findViewById(R.id.tvReservationCount);
        tvEmpty = findViewById(R.id.tvEmpty);
        llEmpty = findViewById(R.id.llEmpty);
        progressBar = findViewById(R.id.progressBar);
        swipeRefreshReservations = findViewById(R.id.swipeRefreshReservations);
        bottomNavigationView = findViewById(R.id.bottomNavigation);

        swipeRefreshReservations.setColorSchemeColors(getColor(R.color.brand_lime));
        swipeRefreshReservations.setOnRefreshListener(() -> fetchReservations(false, true));
        CustomerNavigationHelper.setup(this, bottomNavigationView, R.id.navReservations);

        rvReservations.setLayoutManager(new LinearLayoutManager(this));
        reservationAdapter = new ReservationAdapter(reservations, cancellingReservationIds, this::confirmCancellation);
        rvReservations.setAdapter(reservationAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        CustomerNavigationHelper.syncSelection(bottomNavigationView, R.id.navReservations);
        fetchReservations(reservations.isEmpty(), false);
    }

    private void fetchReservations(boolean showFullScreenLoading, boolean showRefreshIndicator) {
        long userId = SessionManager.getUserId(this);
        if (userId == -1L) {
            showEmptyState("Please sign in again to see your reservations.");
            return;
        }

        if (showFullScreenLoading) {
            progressBar.setVisibility(View.VISIBLE);
            rvReservations.setVisibility(View.GONE);
            llEmpty.setVisibility(View.GONE);
        } else if (showRefreshIndicator && !swipeRefreshReservations.isRefreshing()) {
            swipeRefreshReservations.setRefreshing(true);
        }

        ApiClient.getService().getReservationsForUser(userId).enqueue(new Callback<List<ReservationResponse>>() {
            @Override
            public void onResponse(Call<List<ReservationResponse>> call,
                                   Response<List<ReservationResponse>> response) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshReservations.setRefreshing(false);
                if (response.isSuccessful() && response.body() != null) {
                    reservations.clear();
                    reservations.addAll(response.body());
                    cancellingReservationIds.clear();
                    reservationAdapter.notifyDataSetChanged();
                    updateListVisibility();
                } else if (reservations.isEmpty()) {
                    showEmptyState("We couldn't load your reservations right now.");
                } else {
                    Toast.makeText(MyReservationsActivity.this,
                            "Could not refresh reservations right now.",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<ReservationResponse>> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                swipeRefreshReservations.setRefreshing(false);
                if (reservations.isEmpty()) {
                    showEmptyState("Unable to connect. Check your internet connection.");
                } else {
                    Toast.makeText(MyReservationsActivity.this,
                            "Refresh failed. Check your connection.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void confirmCancellation(ReservationResponse reservation) {
        Long reservationId = reservation.getReservationId();
        if (reservationId == null || cancellingReservationIds.contains(reservationId)) {
            return;
        }

        String eventName = !TextUtils.isEmpty(reservation.getEventName())
                ? reservation.getEventName()
                : "this event";

        new AlertDialog.Builder(this)
                .setTitle("Cancel reservation?")
                .setMessage("You'll give up your reserved spot" +
                        (reservation.getQuantity() > 1 ? "s" : "") +
                        " for " + eventName + ".")
                .setNegativeButton("Keep", null)
                .setPositiveButton("Cancel reservation", (dialog, which) -> cancelReservation(reservation))
                .show();
    }

    private void cancelReservation(ReservationResponse reservation) {
        Long reservationId = reservation.getReservationId();
        long userId = SessionManager.getUserId(this);
        if (reservationId == null || userId == -1L) {
            Toast.makeText(this, "Please sign in again to cancel this reservation.", Toast.LENGTH_SHORT).show();
            return;
        }

        cancellingReservationIds.add(reservationId);
        reservationAdapter.notifyDataSetChanged();

        ApiClient.getService().cancelReservation(reservationId, userId).enqueue(new Callback<ReservationResponse>() {
            @Override
            public void onResponse(Call<ReservationResponse> call, Response<ReservationResponse> response) {
                cancellingReservationIds.remove(reservationId);
                if (response.isSuccessful()) {
                    removeReservation(reservationId);
                    reservationAdapter.notifyDataSetChanged();
                    updateListVisibility();

                    String message = response.body() != null && !TextUtils.isEmpty(response.body().getMessage())
                            ? response.body().getMessage()
                            : "Reservation canceled successfully.";
                    Toast.makeText(MyReservationsActivity.this, message, Toast.LENGTH_SHORT).show();
                } else {
                    reservationAdapter.notifyDataSetChanged();
                    Toast.makeText(MyReservationsActivity.this,
                            parseErrorBody(response, "We couldn't cancel that reservation."),
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ReservationResponse> call, Throwable t) {
                cancellingReservationIds.remove(reservationId);
                reservationAdapter.notifyDataSetChanged();
                Toast.makeText(MyReservationsActivity.this,
                        "Unable to connect. Please check your internet connection.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removeReservation(Long reservationId) {
        for (int i = 0; i < reservations.size(); i++) {
            if (reservationId.equals(reservations.get(i).getReservationId())) {
                reservations.remove(i);
                return;
            }
        }
    }

    private void updateListVisibility() {
        int reservationCount = reservations.size();
        tvReservationCount.setText(reservationCount == 0
                ? "No reservations yet"
                : reservationCount + (reservationCount == 1 ? " reservation" : " reservations"));

        boolean isEmpty = reservations.isEmpty();
        rvReservations.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
        llEmpty.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        if (isEmpty) {
            tvEmpty.setText("You haven't reserved a spot yet. Browse events and save a seat.");
        }
    }

    private void showEmptyState(String message) {
        tvReservationCount.setText("My reservations");
        tvEmpty.setText(message);
        progressBar.setVisibility(View.GONE);
        rvReservations.setVisibility(View.GONE);
        llEmpty.setVisibility(View.VISIBLE);
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
}
