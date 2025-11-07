package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.Event;
import fr.cop1.studentpantrybackend.entities.TimeSlot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TimeSlotRepository extends JpaRepository<TimeSlot, Long> {
    List<TimeSlot> findByEvent(Event event);
    List<TimeSlot> findByEventId(Long eventId);
    List<TimeSlot> findByStartTimeAfter(LocalDateTime dateTime);
    List<TimeSlot> findByStartTimeBefore(LocalDateTime dateTime);
    List<TimeSlot> findByStartTimeBetween(LocalDateTime startDateTime, LocalDateTime endDateTime);

    // search for time slots of a given event that have more than a specified number of available spots
    List<TimeSlot> findByEventAndAvailableSpotsGreaterThan(Event event, int minAvailableSpots);

    // find the most popular time slots for a given event based on the number of reservations made
    @Query("SELECT t FROM TimeSlot t WHERE t.event.id = ?1 ORDER BY (t.maxCapacity - t.availableSpots) DESC")
    List<TimeSlot> findMostPopularTimeSlotsByEventId(Long eventId);

}
