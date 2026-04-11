package com.soen345.ticketreserve.controller;

import com.soen345.ticketreserve.dto.EventResponse;
import com.soen345.ticketreserve.dto.EventCreationRequest;
import com.soen345.ticketreserve.model.Event;
import com.soen345.ticketreserve.model.User;
import com.soen345.ticketreserve.service.EventService;
import com.soen345.ticketreserve.service.UserService;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    private final EventService eventService;
    private final UserService userService;

    public EventController(EventService eventService, UserService userService) {
        this.eventService = eventService;
        this.userService = userService;
    }

    @GetMapping
    public ResponseEntity<List<EventResponse>> getAllEvents() {
        List<EventResponse> responses = eventService.getAllEvents()
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/organizer/{organizerId}")
    public ResponseEntity<List<EventResponse>> getEventsByOrganizer(@PathVariable Long organizerId) {
        List<EventResponse> responses = eventService.getEventsByOrganizerId(organizerId)
                .stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(responses);
    }

    @PostMapping("/create")
    public ResponseEntity<EventResponse> createEvent(@RequestBody EventCreationRequest request) {
        User organizer = userService.getUserById(request.getOrganizerId());
        Event event = new Event();
        event.setOrganizer(organizer);
        event.setTitle(request.getTitle());
        event.setDescription(request.getDescription());
        event.setEventDate(request.getDate());
        event.setLocation(request.getLocation());
        event.setCategory(request.getCategory());
        event.setEventCapacity(request.getEventCapacity());

        Event createdEvent = eventService.createEvent(event);

        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(createdEvent));
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<EventResponse> updateEvent(@PathVariable Long id, @RequestBody EventCreationRequest request) {
        Event updates = new Event();
        updates.setTitle(request.getTitle());
        updates.setDescription(request.getDescription());
        updates.setEventDate(request.getDate());
        updates.setLocation(request.getLocation());
        updates.setCategory(request.getCategory());
        updates.setEventCapacity(request.getEventCapacity());

        Event updatedEvent = eventService.updateEvent(id, updates);
        return ResponseEntity.ok(toResponse(updatedEvent));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable("id") Long id) {
        eventService.deleteEvent(id);
        return ResponseEntity.noContent().build();
    }

    private EventResponse toResponse(Event event) {
        return new EventResponse(
                event.getEventId(),
                event.getOrganizer() != null ? event.getOrganizer().getId() : null,
                event.getTitle(),
                event.getDescription(),
                event.getEventDate(),
                event.getLocation(),
                event.getEventCapacity(),
                event.getCategory()
        );
    }
}
