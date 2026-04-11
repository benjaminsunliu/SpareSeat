package com.example.spareseat.model;

public class ReservationResponse {
    private Long reservationId;
    private String message;
    private String customerEmail;
    private String eventName;
    private int quantity;

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
}
