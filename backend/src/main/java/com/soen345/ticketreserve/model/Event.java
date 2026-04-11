package com.soen345.ticketreserve.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long eventId;

    @ManyToOne
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private LocalDate eventDate;

    @Column(nullable = false)
    private String location;

    private String category;

    private int eventCapacity;

    private String description;

    private String status;


    // Constructors

    public Event() {}

    public Event(String title, LocalDate eventDate, String location, String category, int eventCapacity, String description, String status) {
        this.title = title;
        this.eventDate = eventDate;
        this.location = location;
        this.category = category;
        this.eventCapacity = eventCapacity;
        this.description = description;
        this.status = status;
    }

    // Getters and Setters

    public Long getEventId() {
        return eventId;
    }

    public User getOrganizer() {
        return organizer;
    }

    public String getTitle() {
        return title;
    }

    public LocalDate getEventDate() {
        return eventDate;
    }

    public String getLocation() {
        return location;
    }

    public String getCategory() {
        return category;
    }

    public int getEventCapacity() {
        return eventCapacity;
    }

    public String getDescription() {
        return description;
    }

    public String getStatus() {
        return status;
    }

    public void setEventId(Long eventId) {
        this.eventId = eventId;
    }

    public void setOrganizer(User organizer) {
        this.organizer = organizer;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setEventDate(LocalDate eventDate) {
        this.eventDate = eventDate;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setEventCapacity(int eventCapacity) {
        this.eventCapacity = eventCapacity;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
