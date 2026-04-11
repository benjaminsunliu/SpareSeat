package com.soen345.ticketreserve.service;

import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.repository.ReservationRepository;
import com.soen345.ticketreserve.repository.EventRepository;
import com.soen345.ticketreserve.exception.BadRequestException;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class EventService {
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;

    public EventService(EventRepository eventRepository, ReservationRepository reservationRepository) {
        this.eventRepository = eventRepository;
        this.reservationRepository = reservationRepository;
    }

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }

    public List<Event> getEventsByOrganizerId(Long organizerId) {
        return eventRepository.findByOrganizer_Id(organizerId);
    }

    public Event getEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new BadRequestException("Event not found with id: " + eventId));
    }

    public Event createEvent(Event event) {

        if(event.getOrganizer() == null) {
            throw new BadRequestException("Event organizer is required");
        }
        if (event.getTitle() == null || event.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Event title is required");
        }
        if (event.getEventDate() == null) {
            throw new BadRequestException("Event date is required");
        }
        if (event.getLocation() == null || event.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Event location is required");
        }
        if (event.getCategory() == null || event.getCategory().trim().isEmpty()) {
            event.setCategory("General");
        }
        if (event.getEventCapacity() <= 0) {
            throw new BadRequestException("Event capacity must be greater than 0");
        }
        return eventRepository.save(event);
    }

    public Event updateEvent(Long eventId, Event updates) {
        Event existing = getEventById(eventId);
        if (updates.getTitle() == null || updates.getTitle().trim().isEmpty()) {
            throw new BadRequestException("Event title is required");
        }
        if (updates.getEventDate() == null) {
            throw new BadRequestException("Event date is required");
        }
        if (updates.getLocation() == null || updates.getLocation().trim().isEmpty()) {
            throw new BadRequestException("Event location is required");
        }
        if (updates.getEventCapacity() <= 0) {
            throw new BadRequestException("Event capacity must be greater than 0");
        }
        existing.setTitle(updates.getTitle());
        existing.setDescription(updates.getDescription());
        existing.setEventDate(updates.getEventDate());
        existing.setLocation(updates.getLocation());
        existing.setCategory(updates.getCategory() == null || updates.getCategory().trim().isEmpty() ? "General" : updates.getCategory());
        existing.setEventCapacity(updates.getEventCapacity());
        return eventRepository.save(existing);
    }

    public void deleteEvent(Long eventId) {
        if (!eventRepository.existsById(eventId)) {
            throw new BadRequestException("Event not found with id: " + eventId);
        }
        eventRepository.deleteById(eventId);
    }

    public int getRemainingSpots(Event event) {
        if (event == null || event.getEventId() == null) {
            return 0;
        }
        int reservedSpots = reservationRepository.sumQuantityByEventId(event.getEventId());
        return Math.max(event.getEventCapacity() - reservedSpots, 0);
    }
}
