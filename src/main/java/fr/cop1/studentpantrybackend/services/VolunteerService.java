package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.VolunteerAvailabilityDTO;
import fr.cop1.studentpantrybackend.dtos.VolunteerRegistrationDTO;
import fr.cop1.studentpantrybackend.dtos.VolunteerShiftDTO;
import fr.cop1.studentpantrybackend.enums.RegistrationStatus;
import fr.cop1.studentpantrybackend.enums.RoleType;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface VolunteerService {
    // Gestion des postes bénévoles (shifts)
    VolunteerShiftDTO findShiftById(Long id) throws ResourceNotFoundException;
    List<VolunteerShiftDTO> findAllShifts();
    Page<VolunteerShiftDTO> findAllShifts(Pageable pageable);
    VolunteerShiftDTO saveShift(VolunteerShiftDTO shiftDTO) throws ResourceNotFoundException;
    VolunteerShiftDTO updateShift(Long id, VolunteerShiftDTO shiftDTO) throws ResourceNotFoundException;
    void deleteShift(Long id) throws ResourceNotFoundException;

    List<VolunteerShiftDTO> findShiftsByTimeSlotId(Long timeSlotId) throws ResourceNotFoundException;
    List<VolunteerShiftDTO> findShiftsByEventId(Long eventId);
    List<VolunteerShiftDTO> findShiftsByRoleType(RoleType roleType);
    List<VolunteerShiftDTO> findUnfilledShifts();

    // Gestion des inscriptions bénévoles
    VolunteerRegistrationDTO findRegistrationById(Long id) throws ResourceNotFoundException;
    List<VolunteerRegistrationDTO> findAllRegistrations();
    Page<VolunteerRegistrationDTO> findAllRegistrations(Pageable pageable);
    VolunteerRegistrationDTO saveRegistration(VolunteerRegistrationDTO registrationDTO) throws ResourceNotFoundException;
    VolunteerRegistrationDTO updateRegistration(Long id, VolunteerRegistrationDTO registrationDTO) throws ResourceNotFoundException;
    void deleteRegistration(Long id) throws ResourceNotFoundException;

    List<VolunteerRegistrationDTO> findRegistrationsByUserId(Long userId) throws ResourceNotFoundException;
    List<VolunteerRegistrationDTO> findRegistrationsByShiftId(Long shiftId) throws ResourceNotFoundException;
    List<VolunteerRegistrationDTO> findRegistrationsByEventId(Long eventId);
    List<VolunteerRegistrationDTO> findRegistrationsByStatus(RegistrationStatus status);

    // Méthodes de gestion du cycle de vie
    VolunteerRegistrationDTO registerVolunteer(Long userId, Long shiftId, Boolean isTeamLeader, String notes) throws ResourceNotFoundException;
    VolunteerRegistrationDTO cancelRegistration(Long id, String reason) throws ResourceNotFoundException;
    VolunteerRegistrationDTO checkInRegistration(Long id) throws ResourceNotFoundException;
    VolunteerRegistrationDTO completeRegistration(Long id) throws ResourceNotFoundException;

    // Gestion des disponibilités récurrentes
    VolunteerAvailabilityDTO findAvailabilityById(Long id) throws ResourceNotFoundException;
    List<VolunteerAvailabilityDTO> findAllAvailabilities();
    List<VolunteerAvailabilityDTO> findAvailabilitiesByUserId(Long userId) throws ResourceNotFoundException;
    VolunteerAvailabilityDTO saveAvailability(VolunteerAvailabilityDTO availabilityDTO) throws ResourceNotFoundException;
    VolunteerAvailabilityDTO updateAvailability(Long id, VolunteerAvailabilityDTO availabilityDTO) throws ResourceNotFoundException;
    void deleteAvailability(Long id) throws ResourceNotFoundException;

    // Recherche de bénévoles disponibles
    List<Long> findAvailableVolunteersForTimeSlot(Long timeSlotId) throws ResourceNotFoundException;

    // Statistiques
    Long countVolunteersByEvent(Long eventId) throws ResourceNotFoundException;
    Long countRegistrationsByStatus(RegistrationStatus status);
    Map<String, Long> getRegistrationStatsByRoleType();
    List<Object[]> findMostActiveVolunteers(int limit);
}