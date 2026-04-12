package com.example.spareseat;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.spareseat.model.EventResponse;
import com.google.android.material.button.MaterialButton;

import java.util.List;

public class HostEventAdapter extends RecyclerView.Adapter<HostEventAdapter.HostEventViewHolder> {
    private static final String STATUS_CANCELLED = "CANCELLED";

    interface MenuClickListener {
        void onEdit(EventResponse event);
        void onDelete(EventResponse event);
        void onCancel(EventResponse event);
    }

    private final List<EventResponse> events;
    private final MenuClickListener listener;

    public HostEventAdapter(List<EventResponse> events, MenuClickListener listener) {
        this.events = events;
        this.listener = listener;
    }

    @NonNull
    @Override
    public HostEventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_host_event_card, parent, false);
        return new HostEventViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HostEventViewHolder holder, int position) {
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
        boolean isCancelled = STATUS_CANCELLED.equalsIgnoreCase(event.getStatus());
        if (isCancelled) {
            holder.tvCapacity.setText("Status: Cancelled");
            holder.tvCapacity.setTextColor(holder.itemView.getContext().getColor(R.color.danger));
        } else {
            holder.tvCapacity.setText(remainingSpots > 0
                    ? remainingSpots + " spots available"
                    : "Sold out");
            holder.tvCapacity.setTextColor(holder.itemView.getContext().getColor(
                    remainingSpots > 0 ? R.color.available_green : R.color.text_muted
            ));
        }

        holder.btnCancelEvent.setEnabled(!isCancelled);
        holder.btnCancelEvent.setText(isCancelled ? "Cancelled" : "Cancel Event");
        holder.btnCancelEvent.setOnClickListener(v -> listener.onCancel(event));

        holder.btnMenu.setOnClickListener(v -> {
            PopupMenu popup = new PopupMenu(v.getContext(), v);
            popup.getMenu().add(0, 0, 0, "Edit");
            popup.getMenu().add(0, 1, 1, "Delete");
            popup.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == 0) {
                    listener.onEdit(event);
                } else {
                    listener.onDelete(event);
                }
                return true;
            });
            popup.show();
        });
    }

    @Override
    public int getItemCount() {
        return events.size();
    }

    static class HostEventViewHolder extends RecyclerView.ViewHolder {
        final TextView tvTitle, tvCategory, tvDate, tvLocation, tvDescription, tvCapacity;
        final ImageButton btnMenu;
        final MaterialButton btnCancelEvent;

        HostEventViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle       = itemView.findViewById(R.id.tvTitle);
            tvCategory    = itemView.findViewById(R.id.tvCategory);
            tvDate        = itemView.findViewById(R.id.tvDate);
            tvLocation    = itemView.findViewById(R.id.tvLocation);
            tvDescription = itemView.findViewById(R.id.tvDescription);
            tvCapacity    = itemView.findViewById(R.id.tvCapacity);
            btnMenu       = itemView.findViewById(R.id.btnMenu);
            btnCancelEvent = itemView.findViewById(R.id.btnCancelEvent);
        }
    }
}
