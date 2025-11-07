package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.entities.VolunteerAvailability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalTime;
import java.util.List;

@Repository
public interface VolunteerAvailabilityRepository extends JpaRepository<VolunteerAvailability, Long> {
    List<VolunteerAvailability> findByUser(User user);
    List<VolunteerAvailability> findByUserAndIsActive(User user, Boolean isActive);
    List<VolunteerAvailability> findByDayOfWeek(Integer dayOfWeek);
    List<VolunteerAvailability> findByDayOfWeekAndIsActive(Integer dayOfWeek, Boolean isActive);

    // Recherche de disponibilités pour un créneau horaire spécifique
    @Query("SELECT va FROM VolunteerAvailability va WHERE va.isActive = true AND va.dayOfWeek = ?1 AND " +
            "va.startTime <= ?2 AND va.endTime >= ?3")
    List<VolunteerAvailability> findAvailabilitiesForTimeSlot(Integer dayOfWeek, LocalTime startTime, LocalTime endTime);

    // Requête pour trouver les bénévoles disponibles à un moment précis
    @Query("SELECT va.user FROM VolunteerAvailability va WHERE va.isActive = true AND va.dayOfWeek = ?1 AND " +
            "va.startTime <= ?2 AND va.endTime >= ?2 GROUP BY va.user")
    List<User> findAvailableVolunteers(Integer dayOfWeek, LocalTime time);
}