package com.soen345.ticketreserve.repository;

import com.soen345.ticketreserve.model.Reservation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReservationRepository extends JpaRepository<Reservation, Long> {
	List<Reservation> findByUser_Id(Long userId);

    @Query("select coalesce(sum(r.quantity), 0) from Reservation r where r.event.eventId = :eventId")
    int sumQuantityByEventId(@Param("eventId") Long eventId);
}
