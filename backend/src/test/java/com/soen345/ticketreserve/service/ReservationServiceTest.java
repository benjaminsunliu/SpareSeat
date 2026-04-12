package com.soen345.ticketreserve.service;

import com.soen345.ticketreserve.dto.ReservationRequest;
import com.soen345.ticketreserve.dto.ReservationResponse;
import com.soen345.ticketreserve.exception.BadRequestException;
import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.model.Reservation;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.repository.EventRepository;
import com.soen345.ticketreserve.repository.ReservationRepository;
import com.soen345.ticketreserve.repository.UserRepository;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class ReservationServiceTest {

    private ReservationRepository reservationRepository;
    private EmailService emailService;
    private UserRepository userRepository;
    private EventRepository eventRepository;
    private ReservationService reservationService;

    @BeforeEach
    void setUp() {
        reservationRepository = mock(ReservationRepository.class);
        emailService = mock(EmailService.class);
        userRepository = mock(UserRepository.class);
        eventRepository = mock(EventRepository.class);
        reservationService = new ReservationService(reservationRepository, emailService, userRepository, eventRepository);
    }

        @Test
    void shouldCreateReservationAndSendEmail() {
        Long userId = 10L;
        Long eventId = 20L;
        ReservationRequest request = new ReservationRequest(
            userId,
            eventId,
            2
        );

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Event event = new Event();
        event.setEventId(eventId);
        event.setTitle("Movie Night");
        event.setEventCapacity(10);
        event.setEventDate(LocalDate.of(2026, 5, 1));
        event.setLocation("Montreal");
        event.setStatus(EventService.STATUS_ACTIVE);

        Reservation savedReservation = new Reservation(
            user,
            event,
            2
        );
        savedReservation.setId(1L);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(event));
        when(reservationRepository.sumQuantityByEventId(eventId)).thenReturn(0);
        when(reservationRepository.save(any(Reservation.class))).thenReturn(savedReservation);

        ReservationResponse response = reservationService.createReservation(request);

        assertNotNull(response);
        assertEquals(1L, response.getReservationId());
        assertEquals("test@example.com", response.getCustomerEmail());
        assertEquals("Movie Night", response.getEventName());
        assertEquals(2, response.getQuantity());
        assertEquals(eventId, response.getEventId());
        assertEquals(LocalDate.of(2026, 5, 1), response.getEventDate());
        assertEquals("Montreal", response.getEventLocation());
        assertEquals(EventService.STATUS_ACTIVE, response.getEventStatus());

        verify(userRepository, times(1)).findById(userId);
        verify(eventRepository, times(1)).findById(eventId);
        verify(reservationRepository, times(1)).sumQuantityByEventId(eventId);
        verify(reservationRepository, times(1)).save(any(Reservation.class));
        verify(emailService, times(1)).sendReservationConfirmation(
            "test@example.com",
            "Movie Night",
            2,
            1L
        );
        }

    @Test
    void shouldReturnAllReservationsAssociatedWithUser() {
        Long userId = 10L;

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Event event1 = new Event();
        event1.setEventId(1L);
        event1.setTitle("Movie Night");
        event1.setEventDate(LocalDate.of(2026, 5, 20));
        event1.setLocation("Montreal");
        event1.setStatus(EventService.STATUS_CANCELLED);

        Event event2 = new Event();
        event2.setEventId(2L);
        event2.setTitle("Tech Talk");
        event2.setEventDate(LocalDate.of(2026, 5, 10));
        event2.setLocation("Toronto");
        event2.setStatus(EventService.STATUS_ACTIVE);

        Reservation reservation1 = new Reservation(user, event1, 1);
        reservation1.setId(1L);
        Reservation reservation2 = new Reservation(user, event2, 1);
        reservation2.setId(2L);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(reservationRepository.findByUser_Id(userId)).thenReturn(List.of(reservation1, reservation2));

        List<ReservationResponse> reservations = reservationService.getReservationsForUser(userId);

        assertEquals(2, reservations.size());
        assertEquals("Tech Talk", reservations.get(0).getEventName());
        assertEquals("Movie Night", reservations.get(1).getEventName());
        assertEquals("Toronto", reservations.get(0).getEventLocation());
        assertEquals(EventService.STATUS_CANCELLED, reservations.get(1).getEventStatus());
        verify(userRepository, times(1)).findById(userId);
        verify(reservationRepository, times(1)).findByUser_Id(userId);
    }

    @Test
    void shouldRejectReservationWhenQuantityExceedsRemainingSpots() {
        Long userId = 10L;
        Long eventId = 20L;
        ReservationRequest request = new ReservationRequest(userId, eventId, 3);

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Event event = new Event();
        event.setEventId(eventId);
        event.setTitle("Movie Night");
        event.setEventCapacity(5);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(event));
        when(reservationRepository.sumQuantityByEventId(eventId)).thenReturn(4);

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> reservationService.createReservation(request));
        assertEquals("Only 1 spots are available for this event.", error.getMessage());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldRejectReservationWhenEventIsCancelled() {
        Long userId = 10L;
        Long eventId = 20L;
        ReservationRequest request = new ReservationRequest(userId, eventId, 1);

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Event event = new Event();
        event.setEventId(eventId);
        event.setTitle("Movie Night");
        event.setStatus(EventService.STATUS_CANCELLED);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(eventRepository.findById(eventId)).thenReturn(java.util.Optional.of(event));

        BadRequestException error = assertThrows(BadRequestException.class,
                () -> reservationService.createReservation(request));
        assertEquals("This event has been cancelled.", error.getMessage());
        verify(reservationRepository, never()).save(any(Reservation.class));
    }

    @Test
    void shouldCancelReservationForUser() {
        Long userId = 10L;
        Long reservationId = 99L;

        User user = new User();
        user.setId(userId);
        user.setEmail("test@example.com");

        Event event = new Event();
        event.setEventId(20L);
        event.setTitle("Movie Night");
        event.setEventDate(LocalDate.of(2026, 5, 1));
        event.setLocation("Montreal");
        event.setStatus(EventService.STATUS_CANCELLED);

        Reservation reservation = new Reservation(user, event, 2);
        reservation.setId(reservationId);

        when(userRepository.findById(userId)).thenReturn(java.util.Optional.of(user));
        when(reservationRepository.findByIdAndUser_Id(reservationId, userId))
                .thenReturn(java.util.Optional.of(reservation));

        ReservationResponse response = reservationService.cancelReservation(userId, reservationId);

        assertEquals(reservationId, response.getReservationId());
        assertEquals("Reservation canceled successfully.", response.getMessage());
        assertEquals("Movie Night", response.getEventName());
        assertEquals(EventService.STATUS_CANCELLED, response.getEventStatus());
        verify(reservationRepository, times(1)).delete(reservation);
    }
}
