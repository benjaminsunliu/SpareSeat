package com.soen345.ticketreserve.dtoTest;

import com.soen345.ticketreserve.dto.EventResponse;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class EventResponseTest {

    @Test
    void testSettersAndGetters() {
        EventResponse eventResponse = new EventResponse();
        eventResponse.setEventId(1L);
        eventResponse.setTitle("Spring Meetup");
        eventResponse.setDescription("A meetup to discuss Spring Framework");
        eventResponse.setDate(LocalDate.of(2024, 10, 15));
        eventResponse.setLocation("Montreal");
        eventResponse.setEventCapacity(100);
        eventResponse.setRemainingSpots(72);
        eventResponse.setCategory("Tech");
        eventResponse.setStatus("ACTIVE");

        assertEquals(1L, eventResponse.getEventId());
        assertEquals("Spring Meetup", eventResponse.getTitle());
        assertEquals("A meetup to discuss Spring Framework", eventResponse.getDescription());
        assertEquals(LocalDate.of(2024, 10, 15), eventResponse.getDate());
        assertEquals("Montreal", eventResponse.getLocation());
        assertEquals(100, eventResponse.getEventCapacity());
        assertEquals(72, eventResponse.getRemainingSpots());
        assertEquals("Tech", eventResponse.getCategory());
        assertEquals("ACTIVE", eventResponse.getStatus());
    }

    @Test
    void testAllArgsConstructor() {
        EventResponse r = new EventResponse(
                5L, 10L, "Concert", "Live music",
                LocalDate.of(2026, 6, 15),
                "Toronto", 200, 165, "Music", "ACTIVE");

        assertEquals(5L, r.getEventId());
        assertEquals(10L, r.getOrganizerId());
        assertEquals("Concert", r.getTitle());
        assertEquals("Live music", r.getDescription());
        assertEquals(LocalDate.of(2026, 6, 15), r.getDate());
        assertEquals("Toronto", r.getLocation());
        assertEquals(200, r.getEventCapacity());
        assertEquals(165, r.getRemainingSpots());
        assertEquals("Music", r.getCategory());
        assertEquals("ACTIVE", r.getStatus());
    }

    @Test
    void testNoArgConstructorDefaultsToNull() {
        EventResponse r = new EventResponse();
        assertNull(r.getEventId());
        assertNull(r.getTitle());
        assertNull(r.getCategory());
    }

    @Test
    void testSetCategoryOverwritesPreviousValue() {
        EventResponse r = new EventResponse();
        r.setCategory("Sports");
        assertEquals("Sports", r.getCategory());
        r.setCategory("Music");
        assertEquals("Music", r.getCategory());
    }

    @Test
    void testOrganizerIdSetterAndGetter() {
        EventResponse r = new EventResponse();
        assertNull(r.getOrganizerId());
        r.setOrganizerId(42L);
        assertEquals(42L, r.getOrganizerId());
    }

    @Test
    void testOrganizerIdCanBeNull() {
        EventResponse r = new EventResponse(1L, null, "Title", "Desc",
                LocalDate.of(2026, 1, 1), "Montreal", 50, 50, "General", "ACTIVE");
        assertNull(r.getOrganizerId());
    }
}
