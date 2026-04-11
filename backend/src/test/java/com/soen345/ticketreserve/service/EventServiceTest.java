package com.soen345.ticketreserve.service;

import com.soen345.ticketreserve.exception.BadRequestException;
import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.repository.EventRepository;
import com.soen345.ticketreserve.repository.ReservationRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.LocalDate;
import java.util.Optional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class EventServiceTest {

    User testOrganizer = new User();

    @Mock
    private EventRepository eventRepository;
    @Mock
    private ReservationRepository reservationRepository;

    private EventService eventService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        eventService = new EventService(eventRepository, reservationRepository);
    }

    @Test
    void shouldCreateEventWhenValid() {
        Event event = validEvent();

        when(eventRepository.save(any(Event.class))).thenReturn(event);

        Event result = eventService.createEvent(event);

        assertEquals("Spring Meetup", result.getTitle());
        verify(eventRepository).save(event);
    }

    @Test
    void shouldThrowErrorWhenTitleMissing() {
        Event event = validEvent();
        event.setTitle("  ");

        assertThrows(BadRequestException.class, () -> eventService.createEvent(event));
    }

    @Test
    void shouldThrowErrorWhenDateMissing() {
        Event event = validEvent();
        event.setEventDate(null);

        assertThrows(BadRequestException.class, () -> eventService.createEvent(event));
    }

    @Test
    void shouldThrowErrorWhenLocationMissing() {
        Event event = validEvent();
        event.setLocation("");

        assertThrows(BadRequestException.class, () -> eventService.createEvent(event));
    }

    @Test
    void shouldDefaultCategoryWhenCategoryMissing() {
        Event event = validEvent();
        event.setCategory(null);

        when(eventRepository.save(any(Event.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Event result = eventService.createEvent(event);

        assertEquals("General", result.getCategory());
        verify(eventRepository).save(event);
    }

    @Test
    void shouldGetEventByIdWhenFound() {
        Event event = validEvent();
        event.setEventId(10L);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(event));

        Event result = eventService.getEventById(10L);

        assertEquals(10L, result.getEventId());
        verify(eventRepository).findById(10L);
    }

    @Test
    void shouldThrowErrorWhenEventNotFoundById() {
        when(eventRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> eventService.getEventById(99L));
    }

    @Test
    void shouldDeleteEventWhenExists() {
        when(eventRepository.existsById(5L)).thenReturn(true);

        eventService.deleteEvent(5L);

        verify(eventRepository).deleteById(5L);
    }

    @Test
    void shouldThrowErrorWhenDeleteEventNotFound() {
        when(eventRepository.existsById(6L)).thenReturn(false);

        assertThrows(BadRequestException.class, () -> eventService.deleteEvent(6L));
    }

    @Test
    void testReturnAllEvents() {
        Event event1 = validEvent();
        event1.setEventId(1L);
        Event event2 = validEvent();
        event2.setEventId(2L);

        when(eventRepository.findAll()).thenReturn(java.util.List.of(event1, event2));

        java.util.List<Event> result = eventService.getAllEvents();

        assertEquals(2, result.size());
        verify(eventRepository).findAll();
    }

    @Test
    void shouldThrowErrorOrganizerMissing() {
        Event event = validEvent();
        event.setOrganizer(null);

        assertThrows(BadRequestException.class, () -> eventService.createEvent(event));
    }

    @Test
    void shouldThrowErrorWhenEventSizeIsNegative() {
        Event event = validEvent();
        event.setEventCapacity(-10);

        assertThrows(BadRequestException.class, () -> eventService.createEvent(event));
    }

    @Test
    void shouldReturnEventsByOrganizerId() {
        Event e1 = validEvent();
        e1.setEventId(1L);
        Event e2 = validEvent();
        e2.setEventId(2L);

        when(eventRepository.findByOrganizer_Id(1L)).thenReturn(List.of(e1, e2));

        List<Event> result = eventService.getEventsByOrganizerId(1L);

        assertEquals(2, result.size());
        verify(eventRepository).findByOrganizer_Id(1L);
    }

    @Test
    void shouldReturnEmptyListWhenOrganizerHasNoEvents() {
        when(eventRepository.findByOrganizer_Id(99L)).thenReturn(List.of());

        List<Event> result = eventService.getEventsByOrganizerId(99L);

        assertEquals(0, result.size());
        verify(eventRepository).findByOrganizer_Id(99L);
    }

    @Test
    void shouldUpdateEventWhenValid() {
        Event existing = validEvent();
        existing.setEventId(10L);

        Event updates = new Event();
        updates.setTitle("Updated Title");
        updates.setDescription("Updated desc");
        updates.setEventDate(LocalDate.of(2026, 9, 1));
        updates.setLocation("Toronto");
        updates.setCategory("Tech");
        updates.setEventCapacity(200);

        when(eventRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event result = eventService.updateEvent(10L, updates);

        assertEquals("Updated Title", result.getTitle());
        assertEquals("Toronto", result.getLocation());
        assertEquals(200, result.getEventCapacity());
        verify(eventRepository).save(existing);
    }

    @Test
    void shouldThrowErrorWhenUpdateTitleMissing() {
        Event existing = validEvent();
        existing.setEventId(10L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existing));

        Event updates = validEvent();
        updates.setTitle("");

        assertThrows(BadRequestException.class, () -> eventService.updateEvent(10L, updates));
    }

    @Test
    void shouldThrowErrorWhenUpdateDateMissing() {
        Event existing = validEvent();
        existing.setEventId(10L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existing));

        Event updates = validEvent();
        updates.setEventDate(null);

        assertThrows(BadRequestException.class, () -> eventService.updateEvent(10L, updates));
    }

    @Test
    void shouldThrowErrorWhenUpdateLocationMissing() {
        Event existing = validEvent();
        existing.setEventId(10L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existing));

        Event updates = validEvent();
        updates.setLocation("  ");

        assertThrows(BadRequestException.class, () -> eventService.updateEvent(10L, updates));
    }

    @Test
    void shouldThrowErrorWhenUpdateCapacityIsZero() {
        Event existing = validEvent();
        existing.setEventId(10L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existing));

        Event updates = validEvent();
        updates.setEventCapacity(0);

        assertThrows(BadRequestException.class, () -> eventService.updateEvent(10L, updates));
    }

    @Test
    void shouldThrowErrorWhenUpdatingNonexistentEvent() {
        when(eventRepository.findById(404L)).thenReturn(Optional.empty());

        assertThrows(BadRequestException.class, () -> eventService.updateEvent(404L, validEvent()));
    }

    @Test
    void shouldDefaultCategoryToGeneralOnUpdate() {
        Event existing = validEvent();
        existing.setEventId(10L);
        when(eventRepository.findById(10L)).thenReturn(Optional.of(existing));
        when(eventRepository.save(any(Event.class))).thenAnswer(inv -> inv.getArgument(0));

        Event updates = validEvent();
        updates.setCategory(null);

        Event result = eventService.updateEvent(10L, updates);

        assertEquals("General", result.getCategory());
    }

    @Test
    void shouldReturnRemainingSpotsAfterReservations() {
        Event event = validEvent();
        event.setEventId(15L);
        event.setEventCapacity(120);

        when(reservationRepository.sumQuantityByEventId(15L)).thenReturn(45);

        int remaining = eventService.getRemainingSpots(event);

        assertEquals(75, remaining);
        verify(reservationRepository).sumQuantityByEventId(15L);
    }

    private Event validEvent() {
        Event event = new Event();
        event.setOrganizer(testOrganizer);
        event.setTitle("Spring Meetup");
        event.setDescription("Community event");
        event.setEventDate(LocalDate.of(2026, 4, 10));
        event.setLocation("Montreal");
        event.setCategory("General");
        event.setEventCapacity(120);
        return event;
    }
}
