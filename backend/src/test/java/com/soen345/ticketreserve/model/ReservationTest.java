package com.soen345.ticketreserve.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationTest {

    @Test
    void shouldCreateReservationAndUseGettersSetters() {
        Reservation reservation = new Reservation();
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Event event = new Event();
        event.setEventId(2L);
        event.setTitle("Movie Night");

        reservation.setId(1L);
        reservation.setUser(user);
        reservation.setEvent(event);
        reservation.setQuantity(2);

        assertEquals(1L, reservation.getId());
        assertEquals("test@example.com", reservation.getUser().getEmail());
        assertEquals("Movie Night", reservation.getEvent().getTitle());
        assertEquals(2, reservation.getQuantity());
    }

    @Test
    void shouldCreateReservationWithConstructor() {
        User user = new User();
        user.setId(1L);
        user.setEmail("test@example.com");

        Event event = new Event();
        event.setEventId(2L);
        event.setTitle("Movie Night");

        Reservation reservation = new Reservation(user, event, 2);

        assertEquals("test@example.com", reservation.getUser().getEmail());
        assertEquals("Movie Night", reservation.getEvent().getTitle());
        assertEquals(2, reservation.getQuantity());
    }
}