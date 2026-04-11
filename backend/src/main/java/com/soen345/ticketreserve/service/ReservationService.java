package com.soen345.ticketreserve.service;

import com.soen345.ticketreserve.dto.ReservationRequest;
import com.soen345.ticketreserve.dto.ReservationResponse;
import com.soen345.ticketreserve.exception.BadRequestException;
import com.soen345.ticketreserve.model.Reservation;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.repository.ReservationRepository;
import com.soen345.ticketreserve.repository.UserRepository;
import com.soen345.ticketreserve.repository.EventRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReservationService {

    private final ReservationRepository reservationRepository;
    private final EmailService emailService;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;

    public ReservationService(ReservationRepository reservationRepository, EmailService emailService, UserRepository userRepository, EventRepository eventRepository) {
        this.reservationRepository = reservationRepository;
        this.emailService = emailService;
        this.userRepository = userRepository;
        this.eventRepository = eventRepository;
    }

        public ReservationResponse createReservation(ReservationRequest request) {
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("User not found with ID: " + request.getUserId()));
        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new IllegalArgumentException("Event not found with ID: " + request.getEventId()));
        if (request.getQuantity() <= 0) {
            throw new BadRequestException("Quantity must be at least 1");
        }

        int reservedSpots = reservationRepository.sumQuantityByEventId(event.getEventId());
        int remainingSpots = Math.max(event.getEventCapacity() - reservedSpots, 0);
        if (request.getQuantity() > remainingSpots) {
            throw new BadRequestException("Only " + remainingSpots + " spots are available for this event.");
        }

        Reservation reservation = new Reservation(
            user,
            event,
            request.getQuantity()
        );

        Reservation savedReservation = reservationRepository.save(reservation);

        emailService.sendReservationConfirmation(
            user.getEmail(),
            event.getTitle(),
            savedReservation.getQuantity(),
            savedReservation.getId()
        );

        return new ReservationResponse(
            savedReservation.getId(),
            "Reservation created successfully and email confirmation sent.",
            user.getEmail(),
            event.getTitle(),
            savedReservation.getQuantity()
        );
    }

    public List<Event> getEventsForUser(Long userId) {
        return reservationRepository.findByUser_Id(userId)
                .stream()
                .map(Reservation::getEvent)
                .toList();
    }
}
