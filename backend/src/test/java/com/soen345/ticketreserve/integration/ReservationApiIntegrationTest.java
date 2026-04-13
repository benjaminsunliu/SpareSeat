package com.soen345.ticketreserve.integration;

import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class ReservationApiIntegrationTest extends IntegrationTestSupport {

    @Test
    void shouldCreateListAndCancelReservation() throws Exception {
        User organizer = createUser("Host User", "host@example.com", "5145550001", "Password1", "HOST");
        User customer = createUser("Customer User", "customer@example.com", "5145550002", "Password1", "CUSTOMER");
        Event event = createEvent(
                organizer,
                "Tech Talk",
                LocalDate.of(2026, 5, 20),
                "Toronto",
                "Tech",
                10,
                "Build systems at scale",
                EventService.STATUS_ACTIVE
        );

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": %d,
                                  "eventId": %d,
                                  "quantity": 2
                                }
                                """.formatted(customer.getId(), event.getEventId())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.customerEmail").value("customer@example.com"))
                .andExpect(jsonPath("$.eventName").value("Tech Talk"))
                .andExpect(jsonPath("$.quantity").value(2))
                .andExpect(jsonPath("$.eventStatus").value(EventService.STATUS_ACTIVE));

        Long reservationId = reservationRepository.findAll().get(0).getId();
        verify(emailService).sendReservationConfirmation("customer@example.com", "Tech Talk", 2, reservationId);

        mockMvc.perform(get("/api/events/{id}", event.getEventId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingSpots").value(8));

        mockMvc.perform(get("/api/reservations/user/{userId}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].eventName").value("Tech Talk"))
                .andExpect(jsonPath("$[0].eventDate").value("2026-05-20"));

        mockMvc.perform(delete("/api/reservations/{reservationId}", reservationId)
                        .param("userId", customer.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.reservationId").value(reservationId))
                .andExpect(jsonPath("$.message").value("Reservation canceled successfully."));

        assertEquals(0, reservationRepository.count());

        mockMvc.perform(get("/api/reservations/user/{userId}", customer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/events/{id}", event.getEventId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.remainingSpots").value(10));
    }

    @Test
    void shouldRejectReservationWhenCapacityWouldBeExceeded() throws Exception {
        User organizer = createUser("Host User", "host@example.com", "5145550001", "Password1", "HOST");
        User firstCustomer = createUser("Customer One", "one@example.com", "5145550002", "Password1", "CUSTOMER");
        User secondCustomer = createUser("Customer Two", "two@example.com", "5145550003", "Password1", "CUSTOMER");
        Event event = createEvent(
                organizer,
                "Workshop",
                LocalDate.of(2026, 7, 10),
                "Montreal",
                "Learning",
                3,
                "Hands-on session",
                EventService.STATUS_ACTIVE
        );

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": %d,
                                  "eventId": %d,
                                  "quantity": 2
                                }
                                """.formatted(firstCustomer.getId(), event.getEventId())))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/reservations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "userId": %d,
                                  "eventId": %d,
                                  "quantity": 2
                                }
                                """.formatted(secondCustomer.getId(), event.getEventId())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Only 1 spots are available for this event."));
    }
}
