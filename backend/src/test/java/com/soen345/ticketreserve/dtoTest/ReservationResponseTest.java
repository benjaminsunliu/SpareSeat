package com.soen345.ticketreserve.dtoTest;

import com.soen345.ticketreserve.dto.ReservationResponse;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationResponseTest {

    @Test
    void shouldCreateReservationResponseAndUseGettersSetters() {
        ReservationResponse response = new ReservationResponse();

        response.setReservationId(1L);
        response.setMessage("Reservation created successfully and email confirmation sent.");
        response.setCustomerEmail("test@example.com");
        response.setEventName("Movie Night");
        response.setQuantity(2);

        assertEquals(1L, response.getReservationId());
        assertEquals("Reservation created successfully and email confirmation sent.", response.getMessage());
        assertEquals("test@example.com", response.getCustomerEmail());
        assertEquals("Movie Night", response.getEventName());
        assertEquals(2, response.getQuantity());
    }

    @Test
    void shouldCreateReservationResponseWithConstructor() {
        ReservationResponse response = new ReservationResponse(
                1L,
                "Reservation created successfully and email confirmation sent.",
                "test@example.com",
                "Movie Night",
                2
        );

        assertEquals(1L, response.getReservationId());
        assertEquals("Reservation created successfully and email confirmation sent.", response.getMessage());
        assertEquals("test@example.com", response.getCustomerEmail());
        assertEquals("Movie Night", response.getEventName());
        assertEquals(2, response.getQuantity());
    }
}