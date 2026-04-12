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
import org.springframework.stereotype.Service;

import java.util.Comparator;
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
        User user = findUser(request.getUserId());
        Event event = findEvent(request.getEventId());
        if (EventService.STATUS_CANCELLED.equalsIgnoreCase(event.getStatus())) {
            throw new BadRequestException("This event has been cancelled.");
        }
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

        return toReservationResponse(savedReservation, "Reservation created successfully and email confirmation sent.");
    }

    public List<ReservationResponse> getReservationsForUser(Long userId) {
        findUser(userId);

        return reservationRepository.findByUser_Id(userId)
                .stream()
                .sorted(Comparator
                        .comparing((Reservation reservation) -> reservation.getEvent().getEventDate(),
                                Comparator.nullsLast(Comparator.naturalOrder()))
                        .thenComparing(Reservation::getId, Comparator.nullsLast(Comparator.reverseOrder())))
                .map(reservation -> toReservationResponse(reservation, null))
                .toList();
    }

    public ReservationResponse cancelReservation(Long userId, Long reservationId) {
        findUser(userId);

        Reservation reservation = reservationRepository.findByIdAndUser_Id(reservationId, userId)
                .orElseThrow(() -> new BadRequestException("Reservation not found for this user."));

        ReservationResponse response = toReservationResponse(reservation, "Reservation canceled successfully.");
        reservationRepository.delete(reservation);
        return response;
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new BadRequestException("User not found with ID: " + userId));
    }

    private Event findEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BadRequestException("Event not found with ID: " + eventId));
    }

    private ReservationResponse toReservationResponse(Reservation reservation, String message) {
        Event event = reservation.getEvent();
        User user = reservation.getUser();
        return new ReservationResponse(
                reservation.getId(),
                message,
                user.getEmail(),
                event.getTitle(),
                reservation.getQuantity(),
                event.getEventId(),
                event.getEventDate(),
                event.getLocation(),
                event.getStatus() == null || event.getStatus().trim().isEmpty()
                        ? EventService.STATUS_ACTIVE
                        : event.getStatus()
        );
    }
}
