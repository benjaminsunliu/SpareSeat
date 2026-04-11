package com.soen345.ticketreserve.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public class ReservationRequest {

    private Long userId;

    private Long eventId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;

    public ReservationRequest() {
    }

    public ReservationRequest(Long userId, Long eventId, int quantity) {
        this.userId = userId;
        this.eventId = eventId;
        this.quantity = quantity;
    }


    public Long getUserId() {
        return userId;
    }


    public Long getEventId() {
        return eventId;
    }

    public int getQuantity() {
        return quantity;
    }


    public void setUserId(Long userId) {
        this.userId = userId;
    }


    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}