package com.example.spareseat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spareseat.model.ReservationResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;
import java.util.Set;

public class ReservationAdapter extends RecyclerView.Adapter<ReservationAdapter.ReservationViewHolder> {
    private static final String STATUS_CANCELLED = "CANCELLED";

    interface OnCancelClickListener {
        void onCancelClick(ReservationResponse reservation);
    }

    private final List<ReservationResponse> reservations;
    private final Set<Long> cancellingReservationIds;
    private final OnCancelClickListener listener;

    public ReservationAdapter(List<ReservationResponse> reservations,
                              Set<Long> cancellingReservationIds,
                              OnCancelClickListener listener) {
        this.reservations = reservations;
        this.cancellingReservationIds = cancellingReservationIds;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ReservationViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation_card, parent, false);
        return new ReservationViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReservationViewHolder holder, int position) {
        ReservationResponse reservation = reservations.get(position);
        holder.tvTitle.setText(valueOrFallback(reservation.getEventName(), "Reserved Event"));
        holder.tvDate.setText(valueOrFallback(reservation.getEventDate(), "Date to be announced"));
        holder.tvLocation.setText(valueOrFallback(reservation.getEventLocation(), "Location to be announced"));
        holder.tvQuantity.setText(reservation.getQuantity() + (reservation.getQuantity() == 1
                ? " spot reserved"
                : " spots reserved"));

        Long reservationId = reservation.getReservationId();
        holder.tvReservationMeta.setText(reservationId != null
                ? "Reservation #" + reservationId
                : "Reservation");
        boolean isCancelledEvent = STATUS_CANCELLED.equalsIgnoreCase(reservation.getEventStatus());
        holder.tvEventStatus.setVisibility(isCancelledEvent ? View.VISIBLE : View.GONE);
        if (isCancelledEvent) {
            holder.tvEventStatus.setText("Cancelled");
        }

        boolean isCancelling = reservationId != null && cancellingReservationIds.contains(reservationId);
        holder.btnCancel.setEnabled(!isCancelling);
        holder.btnCancel.setText(holder.itemView.getContext().getString(
                isCancelling ? R.string.cancelling_reservation : R.string.cancel_reservation
        ));
        holder.btnCancel.setOnClickListener(v -> listener.onCancelClick(reservation));
    }

    @Override
    public int getItemCount() {
        return reservations.size();
    }

    private String valueOrFallback(String value, String fallback) {
        return value == null || value.trim().isEmpty() ? fallback : value;
    }

    static class ReservationViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle;
        final TextView tvDate;
        final TextView tvLocation;
        final TextView tvQuantity;
        final TextView tvReservationMeta;
        final TextView tvEventStatus;
        final MaterialButton btnCancel;

        ReservationViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvReservationTitle);
            tvDate = itemView.findViewById(R.id.tvReservationDate);
            tvLocation = itemView.findViewById(R.id.tvReservationLocation);
            tvQuantity = itemView.findViewById(R.id.tvReservationQuantity);
            tvReservationMeta = itemView.findViewById(R.id.tvReservationMeta);
            tvEventStatus = itemView.findViewById(R.id.tvReservationEventStatus);
            btnCancel = itemView.findViewById(R.id.btnCancelReservation);
        }
    }
}
