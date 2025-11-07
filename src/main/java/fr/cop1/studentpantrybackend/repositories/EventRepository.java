package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.Event;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.enums.EventStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface EventRepository extends JpaRepository<Event, Long> {
    List<Event> findByStatus(EventStatus status);
    List<Event> findByEventDateAfter(LocalDate date);
    List<Event> findByEventDateBefore(LocalDate date);
    List<Event> findByEventDateBetween(LocalDate startDate, LocalDate endDate);
    List<Event> findByCreatedBy(User user);
    List<Event> findByNameContainingIgnoreCase(String keyword);

    // search for published and upcoming events
    List<Event> findByStatusAndEventDateAfterOrderByEventDateAsc(EventStatus status, LocalDate date);
}
