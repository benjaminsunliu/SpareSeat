package com.example.spareseat.model;

public class EventResponse {
    private Long eventId;
    private Long organizerId;
    private String title;
    private String description;
    private String date;
    private String location;
    private int eventCapacity;
    private String category;

    public Long getEventId() { return eventId; }
    public Long getOrganizerId() { return organizerId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getDate() { return date; }
    public String getLocation() { return location; }
    public int getEventCapacity() { return eventCapacity; }
    public String getCategory() { return category; }
}
