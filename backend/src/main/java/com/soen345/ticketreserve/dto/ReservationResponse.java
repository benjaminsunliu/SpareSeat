package com.soen345.ticketreserve.dto;

public class ReservationResponse {

    private Long reservationId;
    private String message;
    private String customerEmail;
    private String eventName;
    private int quantity;

    public ReservationResponse() {
    }

    public ReservationResponse(Long reservationId, String message, String customerEmail, String eventName, int quantity) {
        this.reservationId = reservationId;
        this.message = message;
        this.customerEmail = customerEmail;
        this.eventName = eventName;
        this.quantity = quantity;
    }

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

    public void setReservationId(Long reservationId) {
        this.reservationId = reservationId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setCustomerEmail(String customerEmail) {
        this.customerEmail = customerEmail;
    }

    public void setEventName(String eventName) {
        this.eventName = eventName;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}