package com.soen345.ticketreserve.service;

import org.junit.jupiter.api.Test;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;

class GmailEmailServiceTest {

    @Test
    void shouldSendReservationConfirmationWithoutThrowing() {
        JavaMailSender mailSender = mock(JavaMailSender.class);
        GmailEmailService emailService = new GmailEmailService(mailSender);

        assertDoesNotThrow(() ->
                emailService.sendReservationConfirmation(
                        "test@example.com",
                        "Movie Night",
                        2,
                        1L
                )
        );
    }
}