package com.soen345.ticketreserve.service;

import com.soen345.ticketreserve.dto.ReservationRequest;
import com.soen345.ticketreserve.dto.ReservationResponse;
import com.soen345.ticketreserve.exception.BadRequestException;
import com.soen345.ticketreserve.model.Reservation;
import com.soen345.ticketreserve.repository.ReservationRepository;
import com.soen345.ticketreserve.repository.UserRepository;
import com.soen345.ticketreserve.repository.EventRepository;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.model.Event;
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
    void shouldReturnAllEventsAssociatedWithUser() {
        Long userId = 10L;

        User user = new User();
        user.setId(userId);

        Event event1 = new Event();
        event1.setEventId(1L);
        event1.setTitle("Movie Night");

        Event event2 = new Event();
        event2.setEventId(2L);
        event2.setTitle("Tech Talk");

        Reservation reservation1 = new Reservation(user, event1, 1);
        Reservation reservation2 = new Reservation(user, event2, 1);

        when(reservationRepository.findByUser_Id(userId)).thenReturn(List.of(reservation1, reservation2));

        List<Event> events = reservationService.getEventsForUser(userId);

        assertEquals(2, events.size());
        assertEquals("Movie Night", events.get(0).getTitle());
        assertEquals("Tech Talk", events.get(1).getTitle());
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
}
