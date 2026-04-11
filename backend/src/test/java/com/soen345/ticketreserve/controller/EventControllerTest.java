package com.soen345.ticketreserve.controller;

import com.soen345.ticketreserve.exception.BadRequestException;
import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.service.EventService;
import com.soen345.ticketreserve.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EventController.class)
class EventControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EventService eventService;

        @MockitoBean
        private UserService userService;

    @Test
    void shouldReturnAllEvents() throws Exception {
        Event eventOne = new Event();
        eventOne.setEventId(1L);
        eventOne.setTitle("Spring Meetup");
        eventOne.setDescription("Community event");
        eventOne.setEventDate(LocalDate.of(2026, 4, 10));
        eventOne.setLocation("Montreal");
        eventOne.setEventCapacity(120);
        eventOne.setCategory("Tech");

        Event eventTwo = new Event();
        eventTwo.setEventId(2L);
        eventTwo.setTitle("Summer Concert");
        eventTwo.setDescription("Live music");
        eventTwo.setEventDate(LocalDate.of(2026, 8, 21));
        eventTwo.setLocation("Toronto");
        eventTwo.setEventCapacity(350);
        eventTwo.setCategory("Music");

        when(eventService.getAllEvents()).thenReturn(List.of(eventOne, eventTwo));
        when(eventService.getRemainingSpots(eventOne)).thenReturn(95);
        when(eventService.getRemainingSpots(eventTwo)).thenReturn(320);

        mockMvc.perform(get("/api/events"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].title").value("Spring Meetup"))
                .andExpect(jsonPath("$[0].date").value("2026-04-10"))
                .andExpect(jsonPath("$[0].remainingSpots").value(95))
                .andExpect(jsonPath("$[0].category").value("Tech"))
                .andExpect(jsonPath("$[1].eventId").value(2))
                .andExpect(jsonPath("$[1].title").value("Summer Concert"))
                .andExpect(jsonPath("$[1].date").value("2026-08-21"))
                .andExpect(jsonPath("$[1].remainingSpots").value(320))
                .andExpect(jsonPath("$[1].category").value("Music"));
    }

    @Test
    void shouldReturnSingleEventById() throws Exception {
        User organizer = new User();
        organizer.setId(4L);

        Event event = new Event();
        event.setEventId(9L);
        event.setOrganizer(organizer);
        event.setTitle("Design Jam");
        event.setDescription("Rapid prototyping session");
        event.setEventDate(LocalDate.of(2026, 9, 12));
        event.setLocation("Montreal");
        event.setEventCapacity(40);
        event.setCategory("Workshop");

        when(eventService.getEventById(9L)).thenReturn(event);
        when(eventService.getRemainingSpots(event)).thenReturn(27);

        mockMvc.perform(get("/api/events/9"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(9))
                .andExpect(jsonPath("$.organizerId").value(4))
                .andExpect(jsonPath("$.title").value("Design Jam"))
                .andExpect(jsonPath("$.remainingSpots").value(27));
    }

    @Test
    void shouldReturnBadRequestWhenEventByIdNotFound() throws Exception {
        when(eventService.getEventById(404L))
                .thenThrow(new BadRequestException("Event not found with id: 404"));

        mockMvc.perform(get("/api/events/404"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Event not found with id: 404"));
    }

    @Test
    void shouldCreateEvent() throws Exception {
        Event created = new Event();
        created.setEventId(1L);
        created.setTitle("Spring Meetup");
        created.setDescription("Community event");
        created.setEventDate(LocalDate.of(2026, 4, 10));
        created.setLocation("Montreal");
        created.setCategory("General");
        created.setEventCapacity(120);

        User organizer = new User();
        organizer.setId(1L);

        when(userService.getUserById(1L)).thenReturn(organizer);
        when(eventService.createEvent(any(Event.class))).thenReturn(created);
        when(eventService.getRemainingSpots(created)).thenReturn(120);

        mockMvc.perform(post("/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "organizerId": 1,
                  "title": "Spring Meetup",
                  "description": "Community event",
                  "date": "2026-04-10",
                  "location": "Montreal",
                  "category": "General",
                  "eventCapacity": 120
                }
                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.eventId").value(1))
                .andExpect(jsonPath("$.title").value("Spring Meetup"))
                .andExpect(jsonPath("$.description").value("Community event"))
                .andExpect(jsonPath("$.date").value("2026-04-10"))
                .andExpect(jsonPath("$.location").value("Montreal"))
                .andExpect(jsonPath("$.category").value("General"))
                .andExpect(jsonPath("$.remainingSpots").value(120))
                .andExpect(jsonPath("$.eventCapacity").value(120));
    }

    @Test
    void shouldReturnBadRequestWhenCreateFails() throws Exception {
                User organizer = new User();
                organizer.setId(1L);

                when(userService.getUserById(1L)).thenReturn(organizer);
        when(eventService.createEvent(any(Event.class)))
                .thenThrow(new BadRequestException("Event title is required"));

        mockMvc.perform(post("/api/events/create")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                                                                        "organizerId": 1,
                  "title": "",
                  "description": "Community event",
                  "date": "2026-04-10",
                  "location": "Montreal",
                  "eventCapacity": 120
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Event title is required"));
    }

    @Test
    void shouldDeleteEvent() throws Exception {
        mockMvc.perform(delete("/api/events/delete/1"))
                .andExpect(status().isNoContent());

        verify(eventService).deleteEvent(1L);
    }

    @Test
    void shouldReturnBadRequestWhenDeleteFails() throws Exception {
        doThrow(new BadRequestException("Event not found with id: 999"))
                .when(eventService).deleteEvent(999L);

        mockMvc.perform(delete("/api/events/delete/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Event not found with id: 999"));
    }

    @Test
    void shouldReturnEventsByOrganizer() throws Exception {
        User organizer = new User();
        organizer.setId(7L);

        Event e1 = new Event();
        e1.setEventId(1L);
        e1.setTitle("Host Gala");
        e1.setDescription("Gala night");
        e1.setEventDate(LocalDate.of(2026, 5, 20));
        e1.setLocation("Montreal");
        e1.setEventCapacity(100);
        e1.setCategory("Social");
        e1.setOrganizer(organizer);

        Event e2 = new Event();
        e2.setEventId(2L);
        e2.setTitle("Host Workshop");
        e2.setDescription("Workshop event");
        e2.setEventDate(LocalDate.of(2026, 6, 10));
        e2.setLocation("Quebec");
        e2.setEventCapacity(30);
        e2.setCategory("Tech");
        e2.setOrganizer(organizer);

        when(eventService.getEventsByOrganizerId(7L)).thenReturn(List.of(e1, e2));
        when(eventService.getRemainingSpots(e1)).thenReturn(80);
        when(eventService.getRemainingSpots(e2)).thenReturn(12);

        mockMvc.perform(get("/api/events/organizer/7"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventId").value(1))
                .andExpect(jsonPath("$[0].title").value("Host Gala"))
                .andExpect(jsonPath("$[0].organizerId").value(7))
                .andExpect(jsonPath("$[0].remainingSpots").value(80))
                .andExpect(jsonPath("$[1].eventId").value(2))
                .andExpect(jsonPath("$[1].title").value("Host Workshop"))
                .andExpect(jsonPath("$[1].organizerId").value(7))
                .andExpect(jsonPath("$[1].remainingSpots").value(12));
    }

    @Test
    void shouldReturnEmptyListForOrganizerWithNoEvents() throws Exception {
        when(eventService.getEventsByOrganizerId(99L)).thenReturn(List.of());

        mockMvc.perform(get("/api/events/organizer/99"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void shouldUpdateEvent() throws Exception {
        Event updated = new Event();
        updated.setEventId(3L);
        updated.setTitle("Updated Meetup");
        updated.setDescription("Updated desc");
        updated.setEventDate(LocalDate.of(2026, 7, 15));
        updated.setLocation("Ottawa");
        updated.setCategory("General");
        updated.setEventCapacity(80);

        when(eventService.updateEvent(any(Long.class), any(Event.class))).thenReturn(updated);
        when(eventService.getRemainingSpots(updated)).thenReturn(80);

        mockMvc.perform(put("/api/events/update/3")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "title": "Updated Meetup",
                  "description": "Updated desc",
                  "date": "2026-07-15",
                  "location": "Ottawa",
                  "category": "General",
                  "eventCapacity": 80
                }
                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.eventId").value(3))
                .andExpect(jsonPath("$.title").value("Updated Meetup"))
                .andExpect(jsonPath("$.location").value("Ottawa"))
                .andExpect(jsonPath("$.remainingSpots").value(80))
                .andExpect(jsonPath("$.eventCapacity").value(80));
    }

    @Test
    void shouldReturnBadRequestWhenUpdateFails() throws Exception {
        when(eventService.updateEvent(any(Long.class), any(Event.class)))
                .thenThrow(new BadRequestException("Event not found with id: 5"));

        mockMvc.perform(put("/api/events/update/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                {
                  "title": "Whatever",
                  "date": "2026-07-15",
                  "location": "Montreal",
                  "eventCapacity": 50
                }
                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Event not found with id: 5"));
    }
}
