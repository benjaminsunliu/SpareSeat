package com.example.spareseat.model;

public class ReservationRequest {
    private final long userId;
    private final long eventId;
    private final int quantity;

    public ReservationRequest(long userId, long eventId, int quantity) {
        this.userId = userId;
        this.eventId = eventId;
        this.quantity = quantity;
    }

    public long getUserId() {
        return userId;
    }

    public long getEventId() {
        return eventId;
    }

    public int getQuantity() {
        return quantity;
    }
}
