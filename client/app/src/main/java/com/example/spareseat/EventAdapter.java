package com.example.spareseat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spareseat.model.EventResponse;

import java.util.List;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {
    interface OnEventClickListener {
        void onEventClick(EventResponse event);
    }

    private final List<EventResponse> events;
    private final OnEventClickListener listener;

    public EventAdapter(List<EventResponse> events, OnEventClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_card, parent, false);
        return new EventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EventViewHolder holder, int position) {
        EventResponse event = events.get(position);

        holder.tvTitle.setText(event.getTitle() != null ? event.getTitle() : "Untitled");

        String cat = event.getCategory() != null && !event.getCategory().isEmpty()
                ? event.getCategory() : "General";
        holder.tvCategory.setText(cat);

        holder.tvDate.setText(event.getDate() != null ? event.getDate() : "TBD");
        holder.tvLocation.setText(event.getLocation() != null ? event.getLocation() : "TBD");

        holder.tvDescription.setText(event.getDescription() != null && !event.getDescription().isEmpty()
                ? event.getDescription() : "No description available.");

        int remainingSpots = event.getRemainingSpots();
        holder.tvCapacity.setText(remainingSpots > 0
                ? remainingSpots + " spots available"
                : "Sold out");

        holder.itemView.setOnClickListener(v -> listener.onEventClick(event));
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class EventViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvCategory, tvDate, tvLocation, tvDescription, tvCapacity;

        EventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle       = itemView.findViewById(R.id.tvTitle);
            tvCategory    = itemView.findViewById(R.id.tvCategory);
            tvDate        = itemView.findViewById(R.id.tvDate);
            tvLocation    = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCapacity    = itemView.findViewById(R.id.tvCapacity);
        }
    }
}
