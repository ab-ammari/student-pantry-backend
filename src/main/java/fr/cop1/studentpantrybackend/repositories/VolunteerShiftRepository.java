package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.TimeSlot;
import fr.cop1.studentpantrybackend.entities.VolunteerShift;
import fr.cop1.studentpantrybackend.enums.ExperienceLevel;
import fr.cop1.studentpantrybackend.enums.RoleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VolunteerShiftRepository extends JpaRepository<VolunteerShift, Long> {
    List<VolunteerShift> findByTimeSlot(TimeSlot timeSlot);
    List<VolunteerShift> findByTimeSlotId(Long timeSlotId);
    List<VolunteerShift> findByRoleType(RoleType roleType);
    List<VolunteerShift> findByMinExperienceLevel(ExperienceLevel experienceLevel);

    // Requête pour trouver les postes non pourvus
    @Query("SELECT v FROM VolunteerShift v WHERE v.requiredVolunteers > " +
            "(SELECT COUNT(r) FROM VolunteerRegistration r WHERE r.volunteerShift = v AND r.status = 'CONFIRMED')")
    List<VolunteerShift> findUnfilledShifts();

    // Requête pour trouver les shifts par événement
    @Query("SELECT v FROM VolunteerShift v WHERE v.timeSlot.event.id = ?1")
    List<VolunteerShift> findByEventId(Long eventId);

    // Recherche de shifts en fonction du niveau d'expérience et du type de rôle
    List<VolunteerShift> findByRoleTypeAndMinExperienceLevel(RoleType roleType, ExperienceLevel minExperienceLevel);
}
