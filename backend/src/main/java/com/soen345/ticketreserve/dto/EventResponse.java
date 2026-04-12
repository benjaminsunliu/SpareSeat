package com.soen345.ticketreserve.dto;

import java.time.LocalDate;

public class EventResponse {
    private Long eventId;
    private Long organizerId;
    private String title;
    private String description;
    private LocalDate date;
    private String location;
    private int eventCapacity;
    private int remainingSpots;
    private String category;
    private String status;

    public EventResponse() {}

    public EventResponse(Long eventId, Long organizerId, String title, String description, LocalDate date,
                         String location, int eventCapacity, int remainingSpots, String category, String status) {
        this.eventId = eventId;
        this.organizerId = organizerId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.eventCapacity = eventCapacity;
        this.remainingSpots = remainingSpots;
        this.category = category;
        this.status = status;
    }

    public Long getEventId() {
        return eventId;
    }

    public Long getOrganizerId() {
        return organizerId;
    }

    public void setOrganizerId(Long organizerId) {
        this.organizerId = organizerId;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public LocalDate getDate() {
        return date;
    }

    public String getLocation() {
        return location;
    }

    public int getEventCapacity() {
        return eventCapacity;
    }

    public int getRemainingSpots() {
        return remainingSpots;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public void setRemainingSpots(int remainingSpots) {
        this.remainingSpots = remainingSpots;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
