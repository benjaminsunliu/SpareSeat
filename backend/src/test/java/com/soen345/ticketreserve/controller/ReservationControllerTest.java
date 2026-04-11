package com.soen345.ticketreserve.controller;

import com.soen345.ticketreserve.dto.ReservationResponse;
import com.soen345.ticketreserve.dto.ReservationRequest;
import com.soen345.ticketreserve.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ReservationControllerTest {

    private MockMvc mockMvc;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationService = mock(ReservationService.class);
        ReservationController reservationController = new ReservationController(reservationService);
        mockMvc = MockMvcBuilders.standaloneSetup(reservationController).build();
    }

    @Test
    void shouldCreateReservation() throws Exception {
        String requestJson = """
                {
                  "userId": 1,
                  "eventId": 2,
                  "quantity": 2
                }
                """;

        ReservationResponse response = new ReservationResponse(
                1L,
                "Reservation created successfully and email confirmation sent.",
                "test@example.com",
                "Movie Night",
                2
        );

        when(reservationService.createReservation(any())).thenReturn(response);

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(1))
                .andExpect(jsonPath("$.customerEmail").value("test@example.com"))
                .andExpect(jsonPath("$.eventName").value("Movie Night"))
                .andExpect(jsonPath("$.quantity").value(2));

        verify(reservationService).createReservation(argThat((ReservationRequest request) ->
            request.getUserId().equals(1L)
                && request.getEventId().equals(2L)
                && request.getQuantity() == 2
        ));
    }
}