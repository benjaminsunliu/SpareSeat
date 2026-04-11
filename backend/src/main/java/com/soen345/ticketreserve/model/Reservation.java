package com.soen345.ticketreserve.model;

import jakarta.persistence.*;

@Entity
@Table(name = "reservations")
public class Reservation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;

    @ManyToOne(optional = false)
    @JoinColumn(name = "event_id", referencedColumnName = "eventId")
    private Event event;
    private int quantity;

    public Reservation() {
    }

    public Reservation(User user, Event event, int quantity) {
        this.user = user;
        this.event = event;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }


    public User getUser() {
        return user;
    }

    public Event getEvent() {
        return event;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setId(Long id) {
        this.id = id;
    }


    public void setUser(User user) {
        this.user = user;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
}