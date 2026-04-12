package com.soen345.ticketreserve.dto;

import java.time.LocalDate;

public class ReservationResponse {

    private Long reservationId;
    private String message;
    private String customerEmail;
    private String eventName;
    private int quantity;
    private Long eventId;
    private LocalDate eventDate;
    private String eventLocation;
    private String eventStatus;

    public ReservationResponse() {
    }

    public ReservationResponse(Long reservationId, String message, String customerEmail, String eventName, int quantity) {
        this.reservationId = reservationId;
        this.message = message;
        this.customerEmail = customerEmail;
        this.eventName = eventName;
        this.quantity = quantity;
    }

    public ReservationResponse(Long reservationId, String message, String customerEmail, String eventName,
                               int quantity, Long eventId, LocalDate eventDate, String eventLocation,
                               String eventStatus) {
        this.reservationId = reservationId;
        this.message = message;
        this.customerEmail = customerEmail;
        this.eventName = eventName;
        this.quantity = quantity;
        this.eventId = eventId;
        this.eventDate = eventDate;
        this.eventLocation = eventLocation;
        this.eventStatus = eventStatus;
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

    public Long getEventId() {
        return eventId;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public String getEventLocation() {
        return eventLocation;
    }

    public String getEventStatus() {
        return eventStatus;
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

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public void setEventStatus(String eventStatus) {
        this.eventStatus = eventStatus;
    }
}
