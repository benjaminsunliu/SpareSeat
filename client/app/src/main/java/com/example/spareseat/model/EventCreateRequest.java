package com.example.spareseat.model;

public class EventCreateRequest {
    private Long organizerId;
    private String title;
    private String description;
    private String date;
    private String location;
    private String category;
    private int eventCapacity;

    public EventCreateRequest(Long organizerId, String title, String description,
                              String date, String location, String category, int eventCapacity) {
        this.organizerId   = organizerId;
        this.title         = title;
        this.description   = description;
        this.date          = date;
        this.location      = location;
        this.category      = category;
        this.eventCapacity = eventCapacity;
    }

    public Long getOrganizerId()    { return organizerId; }
    public String getTitle()        { return title; }
    public String getDescription()  { return description; }
    public String getDate()         { return date; }
    public String getLocation()     { return location; }
    public String getCategory()     { return category; }
    public int getEventCapacity()   { return eventCapacity; }
}
