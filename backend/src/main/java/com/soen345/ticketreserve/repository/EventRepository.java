package com.soen345.ticketreserve.repository;

import com.soen345.ticketreserve.model.Event;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long> {

        List<Event> findByOrganizer_Id(Long organizerId);

        Optional<Event> findByTitle(String title);

        Optional<Event> findByCategory(String category);

        Optional<Event> findByLocation(String location);

        Optional<Event> findByEventDate(LocalDate eventDate);

}
