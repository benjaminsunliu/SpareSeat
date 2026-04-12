package com.example.spareseat.model;

public class ReservationResponse {
    private Long reservationId;
    private String message;
    private String customerEmail;
    private String eventName;
    private int quantity;
    private Long eventId;
    private String eventDate;
    private String eventLocation;
    private String eventStatus;

    public Long getReservationId() {
        return reservationId;
    }

    public String getMessage() {
        return message;
    }

    public String getCustomerEmail() {
        return customerEmail;
    }

    public String getEventName() {
        return eventName;
    }

    public int getQuantity() {
        return quantity;
    }

    public Long getEventId() {
        return eventId;
    }

    public String getEventDate() {
        return eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventStatus() {
        return eventStatus;
    }
}
