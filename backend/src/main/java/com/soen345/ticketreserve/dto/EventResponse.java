package com.soen345.ticketreserve.dto;

import java.time.LocalDate;

public class EventResponse {
    private Long eventId;
    private String title;
    private String description;
    private LocalDate date;
    private String location;
    private int eventCapacity;
    private String category;

    public EventResponse() {}

    public EventResponse(Long eventId, String title, String description, LocalDate date, String location, int eventCapacity, String category) {
        this.eventId = eventId;
        this.title = title;
        this.description = description;
        this.date = date;
        this.location = location;
        this.eventCapacity = eventCapacity;
        this.category = category;
    }

    public Long getEventId() {
        return eventId;
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

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}
