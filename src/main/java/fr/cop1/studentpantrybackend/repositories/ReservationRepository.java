package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.BasketType;
import fr.cop1.studentpantrybackend.entities.Reservation;
import fr.cop1.studentpantrybackend.entities.TimeSlot;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.enums.ReservationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface ReservationRepository extends JpaRepository<Reservation, Long> {
    List<Reservation> findByUser(User user);
    List<Reservation> findByTimeSlot(TimeSlot timeSlot);
    List<Reservation> findByBasketType(BasketType basketType);
    List<Reservation> findByStatus(ReservationStatus status);
    List<Reservation> findByUserAndStatus(User user, ReservationStatus status);
    Optional<Reservation> findByUserAndTimeSlot(User user, TimeSlot timeSlot);

    // Statistics
    Long countByTimeSlotAndStatus(TimeSlot timeSlot, ReservationStatus status);

    // find all reservations for a given event id
    @Query("SELECT r FROM Reservation r WHERE r.timeSlot.event.id = ?1")
    List<Reservation> findByEventId(Long eventId);

    // recent reservations
    List<Reservation> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime dateTime);

    // find users with more than a specified number of NO_SHOW reservations
    @Query("SELECT r.user, COUNT(r) FROM Reservation r WHERE r.status = 'NO_SHOW' GROUP BY r.user HAVING COUNT(r) > ?1")
    List<Object[]> findFrequentNoShows(Long minNoShows);
}
