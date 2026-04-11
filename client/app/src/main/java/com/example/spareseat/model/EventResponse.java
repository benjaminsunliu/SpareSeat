package com.example.spareseat.model;

import java.io.Serializable;

public class EventResponse implements Serializable {
    private static final long serialVersionUID = 1L;

    private Long eventId;
    private Long organizerId;
    private String title;
    private String description;
    private String date;
    private String location;
    private int eventCapacity;
    private int remainingSpots;
    private String category;

    public EventResponse() {
    }

    public EventResponse(Long eventId, Long organizerId, String title, String description,
                         String date, String location, int eventCapacity, int remainingSpots,
                         String category) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.eventCapacity = eventCapacity;
        this.remainingSpots = remainingSpots;
        this.category = category;
    }

    public Long getEventId() { return eventId; }
    public Long getOrganizerId() { return organizerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public int getEventCapacity() { return eventCapacity; }
    public int getRemainingSpots() { return remainingSpots; }
    public String getCategory() { return category; }
}
