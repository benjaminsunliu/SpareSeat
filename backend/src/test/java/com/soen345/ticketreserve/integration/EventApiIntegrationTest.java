package com.soen345.ticketreserve.integration;

import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.service.EventService;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class EventApiIntegrationTest extends IntegrationTestSupport {

    @Test
    void shouldCreateCancelAndHideCancelledEventFromBrowseList() throws Exception {
        User organizer = createUser("Host User", "host@example.com", "5145550001", "Password1", "HOST");

        mockMvc.perform(post("/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "organizerId": %d,
                                  "title": "Jazz Night",
                                  "description": "Live music downtown",
                                  "date": "2026-05-16",
                                  "location": "Montreal",
                                  "category": "Music",
                                  "eventCapacity": 30
                                }
                                """.formatted(organizer.getId())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Jazz Night"))
                .andExpect(jsonPath("$.status").value(EventService.STATUS_ACTIVE))
                .andExpect(jsonPath("$.remainingSpots").value(30));

        Event createdEvent = eventRepository.findByTitle("Jazz Night").orElseThrow();

        mockMvc.perform(put("/api/events/cancel/{id}", createdEvent.getEventId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(createdEvent.getEventId()))
                .andExpect(jsonPath("$.status").value(EventService.STATUS_CANCELLED));

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        mockMvc.perform(get("/api/events/organizer/{organizerId}", organizer.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].eventId").value(createdEvent.getEventId()))
                .andExpect(jsonPath("$[0].status").value(EventService.STATUS_CANCELLED));
    }

    @Test
    void shouldDeleteEventAndItsReservations() throws Exception {
        User organizer = createUser("Host User", "host@example.com", "5145550001", "Password1", "HOST");
        User customer = createUser("Customer User", "customer@example.com", "5145550002", "Password1", "CUSTOMER");
        Event event = createEvent(
                organizer,
                "Movie Night",
                LocalDate.of(2026, 6, 1),
                "Montreal",
                "Movies",
                12,
                "Outdoor screening",
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
                .andExpect(jsonPath("$.eventName").value("Movie Night"))
                .andExpect(jsonPath("$.quantity").value(2));

        assertEquals(1, reservationRepository.count());

        mockMvc.perform(delete("/api/events/delete/{id}", event.getEventId()))
                .andExpect(status().isNoContent());

        assertFalse(eventRepository.existsById(event.getEventId()));
        assertEquals(0, reservationRepository.count());
        assertTrue(userRepository.existsById(organizer.getId()));
        assertTrue(userRepository.existsById(customer.getId()));
    }
}
