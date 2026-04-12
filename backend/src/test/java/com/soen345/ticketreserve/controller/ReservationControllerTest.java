package com.soen345.ticketreserve.controller;

import com.soen345.ticketreserve.dto.ReservationResponse;
import com.soen345.ticketreserve.dto.ReservationRequest;
import com.soen345.ticketreserve.service.ReservationService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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

    @Test
    void shouldReturnReservationsForUser() throws Exception {
        List<ReservationResponse> reservations = List.of(
                new ReservationResponse(4L, null, "test@example.com", "Movie Night", 2,
                        9L, LocalDate.of(2026, 5, 1), "Montreal", "ACTIVE"),
                new ReservationResponse(5L, null, "test@example.com", "Tech Talk", 1,
                        10L, LocalDate.of(2026, 5, 10), "Toronto", "CANCELLED")
        );

        when(reservationService.getReservationsForUser(1L)).thenReturn(reservations);

        mockMvc.perform(get("/api/reservations/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].reservationId").value(4))
                .andExpect(jsonPath("$[0].eventName").value("Movie Night"))
                .andExpect(jsonPath("$[0].eventDate").value("2026-05-01"))
                .andExpect(jsonPath("$[0].eventLocation").value("Montreal"))
                .andExpect(jsonPath("$[0].eventStatus").value("ACTIVE"))
                .andExpect(jsonPath("$[1].reservationId").value(5));

        verify(reservationService).getReservationsForUser(1L);
    }

    @Test
    void shouldCancelReservation() throws Exception {
        ReservationResponse response = new ReservationResponse(
                4L,
                "Reservation canceled successfully.",
                "test@example.com",
                "Movie Night",
                2,
                9L,
                LocalDate.of(2026, 5, 1),
                "Montreal",
                "CANCELLED"
        );

        when(reservationService.cancelReservation(1L, 4L)).thenReturn(response);

        mockMvc.perform(delete("/api/reservations/4")
                        .param("userId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(4))
                .andExpect(jsonPath("$.message").value("Reservation canceled successfully."));

        verify(reservationService).cancelReservation(1L, 4L);
    }
}
