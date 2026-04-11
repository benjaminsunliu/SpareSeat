package com.soen345.ticketreserve.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class GmailEmailService implements EmailService {

    private final JavaMailSender mailSender;

    public GmailEmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendReservationConfirmation(String to, String eventName, int quantity, Long reservationId) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(to);
        message.setSubject("Reservation Confirmation");
        message.setText(
                "Hello,\n\n" +
                "Your reservation is confirmed.\n\n" +
                "Event: " + eventName + "\n" +
                "Quantity: " + quantity + "\n" +
                "Reservation ID: " + reservationId + "\n\n" +
                "Thank you."
        );

        mailSender.send(message);
    }
}