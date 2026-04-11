package com.soen345.ticketreserve.dtoTest;

import com.soen345.ticketreserve.dto.ReservationRequest;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ReservationRequestTest {

    @Test
    void shouldCreateReservationRequestAndUseGettersSetters() {
        ReservationRequest request = new ReservationRequest();

        request.setUserId(1L);
        request.setEventId(2L);
        request.setQuantity(2);

        assertEquals(1L, request.getUserId());
        assertEquals(2L, request.getEventId());
        assertEquals(2, request.getQuantity());
    }

    @Test
    void shouldCreateReservationRequestWithConstructor() {
        ReservationRequest request = new ReservationRequest(1L, 2L, 2);

        assertEquals(1L, request.getUserId());
        assertEquals(2L, request.getEventId());
        assertEquals(2, request.getQuantity());
    }
}