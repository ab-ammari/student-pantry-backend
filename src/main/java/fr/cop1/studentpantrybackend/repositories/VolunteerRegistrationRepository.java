package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.entities.VolunteerRegistration;
import fr.cop1.studentpantrybackend.entities.VolunteerShift;
import fr.cop1.studentpantrybackend.enums.RegistrationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRegistrationRepository extends JpaRepository<VolunteerRegistration, Long> {
    List<VolunteerRegistration> findByUser(User user);
    List<VolunteerRegistration> findByVolunteerShift(VolunteerShift volunteerShift);
    List<VolunteerRegistration> findByStatus(RegistrationStatus status);
    List<VolunteerRegistration> findByUserAndStatus(User user, RegistrationStatus status);
    Optional<VolunteerRegistration> findByUserAndVolunteerShift(User user, VolunteerShift volunteerShift);
    List<VolunteerRegistration> findByIsTeamLeader(Boolean isTeamLeader);

    // Recherche des inscriptions par événement
    @Query("SELECT vr FROM VolunteerRegistration vr WHERE vr.volunteerShift.timeSlot.event.id = ?1")
    List<VolunteerRegistration> findByEventId(Long eventId);

    // Statistiques
    @Query("SELECT COUNT(vr) FROM VolunteerRegistration vr WHERE vr.user = ?1 AND vr.status = 'COMPLETED'")
    Long countCompletedShiftsByUser(User user);

    // Recherche des bénévoles les plus actifs
    @Query("SELECT vr.user, COUNT(vr) FROM VolunteerRegistration vr WHERE vr.status = 'COMPLETED' " +
            "GROUP BY vr.user ORDER BY COUNT(vr) DESC")
    List<Object[]> findMostActiveVolunteers();

    // Requête pour les inscriptions récentes
    List<VolunteerRegistration> findByCreatedAtAfterOrderByCreatedAtDesc(LocalDateTime dateTime);
}