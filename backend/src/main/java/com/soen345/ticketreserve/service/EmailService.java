package com.soen345.ticketreserve.service;

public interface EmailService {
    void sendReservationConfirmation(String to, String eventName, int quantity, Long reservationId);
}