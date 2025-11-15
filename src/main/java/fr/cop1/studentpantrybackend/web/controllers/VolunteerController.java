package fr.cop1.studentpantrybackend.web.controllers;

import fr.cop1.studentpantrybackend.dtos.*;
import fr.cop1.studentpantrybackend.enums.RegistrationStatus;
import fr.cop1.studentpantrybackend.enums.RoleType;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.services.*;
import fr.cop1.studentpantrybackend.web.*;
import jakarta.validation.Valid;
import lombok.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.*;
import org.springframework.web.bind.annotation.*;

import java.util.*;

/**
 * Contrôleur REST pour la gestion des bénévoles
 */
@RestController
@RequiredArgsConstructor
@Validated
public class VolunteerController {

    private final VolunteerService volunteerService;

    /**
     * ===== GESTION DES POSTES BÉNÉVOLES =====
     */

    /**
     * Récupérer tous les postes bénévoles (avec pagination)
     */
    @GetMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT)
    public ResponseEntity<ApiResponse<Page<VolunteerShiftDTO>>> getAllShifts(
            @RequestParam(value = ApiConstants.PAGE_PARAM, defaultValue = "0") int page,
            @RequestParam(value = ApiConstants.SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = ApiConstants.SORT_PARAM, defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<VolunteerShiftDTO> shifts = volunteerService.findAllShifts(pageable);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalPages", shifts.getTotalPages());
        meta.put("totalElements", shifts.getTotalElements());
        meta.put("size", shifts.getSize());
        meta.put("page", shifts.getNumber());

        return ResponseEntity.ok(ApiResponse.success("Liste des postes bénévoles récupérée", shifts, meta));
    }

    /**
     * Récupérer un poste bénévole par son ID
     */
    @GetMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT + ApiConstants.VOLUNTEER_SHIFT_ID_PATH)
    public ResponseEntity<ApiResponse<VolunteerShiftDTO>> getShiftById(@PathVariable Long id) throws ResourceNotFoundException {
        VolunteerShiftDTO shift = volunteerService.findShiftById(id);
        return ResponseEntity.ok(ApiResponse.success(shift));
    }

    /**
     * Récupérer les postes bénévoles par créneau horaire
     */
    @GetMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT + "/timeslot/{timeSlotId}")
    public ResponseEntity<ApiResponse<List<VolunteerShiftDTO>>> getShiftsByTimeSlotId(@PathVariable Long timeSlotId) throws ResourceNotFoundException {
        List<VolunteerShiftDTO> shifts = volunteerService.findShiftsByTimeSlotId(timeSlotId);
        return ResponseEntity.ok(ApiResponse.success("Postes bénévoles du créneau horaire", shifts));
    }

    /**
     * Récupérer les postes bénévoles par événement
     */
    @GetMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT + "/event/{eventId}")
    public ResponseEntity<ApiResponse<List<VolunteerShiftDTO>>> getShiftsByEventId(@PathVariable Long eventId) {
        List<VolunteerShiftDTO> shifts = volunteerService.findShiftsByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Postes bénévoles de l'événement", shifts));
    }

    /**
     * Récupérer les postes bénévoles par type de rôle
     */
    @GetMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT + "/role/{roleType}")
    public ResponseEntity<ApiResponse<List<VolunteerShiftDTO>>> getShiftsByRoleType(@PathVariable RoleType roleType) {
        List<VolunteerShiftDTO> shifts = volunteerService.findShiftsByRoleType(roleType);
        return ResponseEntity.ok(ApiResponse.success("Postes bénévoles pour le rôle " + roleType, shifts));
    }

    /**
     * Récupérer les postes bénévoles non pourvus
     */
    @GetMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT + "/unfilled")
    public ResponseEntity<ApiResponse<List<VolunteerShiftDTO>>> getUnfilledShifts() {
        List<VolunteerShiftDTO> shifts = volunteerService.findUnfilledShifts();
        return ResponseEntity.ok(ApiResponse.success("Postes bénévoles non pourvus", shifts));
    }

    /**
     * Créer un nouveau poste bénévole
     */
    @PostMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<VolunteerShiftDTO>> createShift(@Valid @RequestBody VolunteerShiftDTO shiftDTO) throws ResourceNotFoundException {
        VolunteerShiftDTO createdShift = volunteerService.saveShift(shiftDTO);
        return new ResponseEntity<>(ApiResponse.success("Poste bénévole créé avec succès", createdShift), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour un poste bénévole existant
     */
    @PutMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT + ApiConstants.VOLUNTEER_SHIFT_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<VolunteerShiftDTO>> updateShift(
            @PathVariable Long id,
            @Valid @RequestBody VolunteerShiftDTO shiftDTO) throws ResourceNotFoundException {

        VolunteerShiftDTO updatedShift = volunteerService.updateShift(id, shiftDTO);
        return ResponseEntity.ok(ApiResponse.success("Poste bénévole mis à jour avec succès", updatedShift));
    }

    /**
     * Supprimer un poste bénévole
     */
    @DeleteMapping(ApiConstants.VOLUNTEER_SHIFTS_ENDPOINT + ApiConstants.VOLUNTEER_SHIFT_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteShift(@PathVariable Long id) throws ResourceNotFoundException {
        volunteerService.deleteShift(id);
        return ResponseEntity.ok(ApiResponse.success("Poste bénévole supprimé avec succès", null));
    }

    /**
     * ===== GESTION DES INSCRIPTIONS BÉNÉVOLES =====
     */

    /**
     * Récupérer toutes les inscriptions bénévoles (avec pagination)
     */
    @GetMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<VolunteerRegistrationDTO>>> getAllRegistrations(
            @RequestParam(value = ApiConstants.PAGE_PARAM, defaultValue = "0") int page,
            @RequestParam(value = ApiConstants.SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = ApiConstants.SORT_PARAM, defaultValue = "createdAt") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<VolunteerRegistrationDTO> registrations = volunteerService.findAllRegistrations(pageable);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalPages", registrations.getTotalPages());
        meta.put("totalElements", registrations.getTotalElements());
        meta.put("size", registrations.getSize());
        meta.put("page", registrations.getNumber());

        return ResponseEntity.ok(ApiResponse.success("Liste des inscriptions bénévoles récupérée", registrations, meta));
    }

    /**
     * Récupérer une inscription bénévole par son ID
     */
    @GetMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + ApiConstants.VOLUNTEER_REGISTRATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @volunteerSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<VolunteerRegistrationDTO>> getRegistrationById(@PathVariable Long id) throws ResourceNotFoundException {
        VolunteerRegistrationDTO registration = volunteerService.findRegistrationById(id);
        return ResponseEntity.ok(ApiResponse.success(registration));
    }

    /**
     * Récupérer les inscriptions bénévoles d'un utilisateur
     */
    @GetMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + "/user/{userId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<List<VolunteerRegistrationDTO>>> getRegistrationsByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        List<VolunteerRegistrationDTO> registrations = volunteerService.findRegistrationsByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Inscriptions bénévoles de l'utilisateur", registrations));
    }

    /**
     * Récupérer les inscriptions bénévoles d'un poste
     */
    @GetMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + "/shift/{shiftId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<VolunteerRegistrationDTO>>> getRegistrationsByShiftId(@PathVariable Long shiftId) throws ResourceNotFoundException {
        List<VolunteerRegistrationDTO> registrations = volunteerService.findRegistrationsByShiftId(shiftId);
        return ResponseEntity.ok(ApiResponse.success("Inscriptions bénévoles du poste", registrations));
    }

    /**
     * Récupérer les inscriptions bénévoles d'un événement
     */
    @GetMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + "/event/{eventId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<VolunteerRegistrationDTO>>> getRegistrationsByEventId(@PathVariable Long eventId) {
        List<VolunteerRegistrationDTO> registrations = volunteerService.findRegistrationsByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Inscriptions bénévoles de l'événement", registrations));
    }

    /**
     * Créer une nouvelle inscription bénévole
     */
    @PostMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT)
    //@PreAuthorize("hasAnyRole('VOLUNTEER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<VolunteerRegistrationDTO>> createRegistration(@Valid @RequestBody VolunteerRegistrationDTO registrationDTO) throws ResourceNotFoundException {
        VolunteerRegistrationDTO createdRegistration = volunteerService.saveRegistration(registrationDTO);
        return new ResponseEntity<>(ApiResponse.success("Inscription bénévole créée avec succès", createdRegistration), HttpStatus.CREATED);
    }

    /**
     * Créer une inscription bénévole (endpoint simplifié)
     */
    @PostMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + "/create")
    //@PreAuthorize("hasAnyRole('VOLUNTEER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<VolunteerRegistrationDTO>> createSimpleRegistration(
            @RequestParam Long userId,
            @RequestParam Long shiftId,
            @RequestParam(required = false) Boolean isTeamLeader,
            @RequestParam(required = false) String notes) throws ResourceNotFoundException {

        VolunteerRegistrationDTO createdRegistration = volunteerService.registerVolunteer(userId, shiftId, isTeamLeader, notes);
        return new ResponseEntity<>(ApiResponse.success("Inscription bénévole créée avec succès", createdRegistration), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour une inscription bénévole existante
     */
    @PutMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + ApiConstants.VOLUNTEER_REGISTRATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @volunteerSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<VolunteerRegistrationDTO>> updateRegistration(
            @PathVariable Long id,
            @Valid @RequestBody VolunteerRegistrationDTO registrationDTO) throws ResourceNotFoundException {

        VolunteerRegistrationDTO updatedRegistration = volunteerService.updateRegistration(id, registrationDTO);
        return ResponseEntity.ok(ApiResponse.success("Inscription bénévole mise à jour avec succès", updatedRegistration));
    }

    /**
     * Supprimer une inscription bénévole
     */
    @DeleteMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + ApiConstants.VOLUNTEER_REGISTRATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @volunteerSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<Void>> deleteRegistration(@PathVariable Long id) throws ResourceNotFoundException {
        volunteerService.deleteRegistration(id);
        return ResponseEntity.ok(ApiResponse.success("Inscription bénévole supprimée avec succès", null));
    }

    /**
     * Annuler une inscription bénévole
     */
    @PutMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + "/{id}/cancel")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @volunteerSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<VolunteerRegistrationDTO>> cancelRegistration(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) throws ResourceNotFoundException {

        VolunteerRegistrationDTO canceledRegistration = volunteerService.cancelRegistration(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Inscription bénévole annulée", canceledRegistration));
    }

    /**
     * Check-in d'une inscription bénévole
     */
    @PutMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + "/{id}/check-in")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<VolunteerRegistrationDTO>> checkInRegistration(@PathVariable Long id) throws ResourceNotFoundException {
        VolunteerRegistrationDTO checkedInRegistration = volunteerService.checkInRegistration(id);
        return ResponseEntity.ok(ApiResponse.success("Check-in effectué", checkedInRegistration));
    }

    /**
     * Marquer une inscription bénévole comme terminée
     */
    @PutMapping(ApiConstants.VOLUNTEER_REGISTRATIONS_ENDPOINT + "/{id}/complete")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<VolunteerRegistrationDTO>> completeRegistration(@PathVariable Long id) throws ResourceNotFoundException {
        VolunteerRegistrationDTO completedRegistration = volunteerService.completeRegistration(id);
        return ResponseEntity.ok(ApiResponse.success("Inscription bénévole marquée comme terminée", completedRegistration));
    }

    /**
     * ===== GESTION DES DISPONIBILITÉS BÉNÉVOLES =====
     */

    /**
     * Récupérer toutes les disponibilités bénévoles
     */
    @GetMapping(ApiConstants.VOLUNTEER_AVAILABILITIES_ENDPOINT)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<VolunteerAvailabilityDTO>>> getAllAvailabilities() {
        List<VolunteerAvailabilityDTO> availabilities = volunteerService.findAllAvailabilities();
        return ResponseEntity.ok(ApiResponse.success("Liste des disponibilités bénévoles récupérée", availabilities));
    }

    /**
     * Récupérer une disponibilité bénévole par son ID
     */
    @GetMapping(ApiConstants.VOLUNTEER_AVAILABILITIES_ENDPOINT + ApiConstants.VOLUNTEER_AVAILABILITY_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @volunteerSecurity.isAvailabilityOwner(#id)")
    public ResponseEntity<ApiResponse<VolunteerAvailabilityDTO>> getAvailabilityById(@PathVariable Long id) throws ResourceNotFoundException {
        VolunteerAvailabilityDTO availability = volunteerService.findAvailabilityById(id);
        return ResponseEntity.ok(ApiResponse.success(availability));
    }

    /**
     * Récupérer les disponibilités bénévoles d'un utilisateur
     */
    @GetMapping(ApiConstants.VOLUNTEER_AVAILABILITIES_ENDPOINT + "/user/{userId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<List<VolunteerAvailabilityDTO>>> getAvailabilitiesByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        List<VolunteerAvailabilityDTO> availabilities = volunteerService.findAvailabilitiesByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Disponibilités bénévoles de l'utilisateur", availabilities));
    }

    /**
     * Créer une nouvelle disponibilité bénévole
     */
    @PostMapping(ApiConstants.VOLUNTEER_AVAILABILITIES_ENDPOINT)
    //@PreAuthorize("hasAnyRole('VOLUNTEER', 'MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<VolunteerAvailabilityDTO>> createAvailability(@Valid @RequestBody VolunteerAvailabilityDTO availabilityDTO) throws ResourceNotFoundException {
        VolunteerAvailabilityDTO createdAvailability = volunteerService.saveAvailability(availabilityDTO);
        return new ResponseEntity<>(ApiResponse.success("Disponibilité bénévole créée avec succès", createdAvailability), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour une disponibilité bénévole existante
     */
    @PutMapping(ApiConstants.VOLUNTEER_AVAILABILITIES_ENDPOINT + ApiConstants.VOLUNTEER_AVAILABILITY_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @volunteerSecurity.isAvailabilityOwner(#id)")
    public ResponseEntity<ApiResponse<VolunteerAvailabilityDTO>> updateAvailability(
            @PathVariable Long id,
            @Valid @RequestBody VolunteerAvailabilityDTO availabilityDTO) throws ResourceNotFoundException {

        VolunteerAvailabilityDTO updatedAvailability = volunteerService.updateAvailability(id, availabilityDTO);
        return ResponseEntity.ok(ApiResponse.success("Disponibilité bénévole mise à jour avec succès", updatedAvailability));
    }

    /**
     * Supprimer une disponibilité bénévole
     */
    @DeleteMapping(ApiConstants.VOLUNTEER_AVAILABILITIES_ENDPOINT + ApiConstants.VOLUNTEER_AVAILABILITY_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @volunteerSecurity.isAvailabilityOwner(#id)")
    public ResponseEntity<ApiResponse<Void>> deleteAvailability(@PathVariable Long id) throws ResourceNotFoundException {
        volunteerService.deleteAvailability(id);
        return ResponseEntity.ok(ApiResponse.success("Disponibilité bénévole supprimée avec succès", null));
    }

    /**
     * Rechercher des bénévoles disponibles pour un créneau
     */
    @GetMapping(ApiConstants.VOLUNTEER_ENDPOINT + "/available-for-timeslot/{timeSlotId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<Long>>> findAvailableVolunteersForTimeSlot(@PathVariable Long timeSlotId) throws ResourceNotFoundException {
        List<Long> availableVolunteers = volunteerService.findAvailableVolunteersForTimeSlot(timeSlotId);
        return ResponseEntity.ok(ApiResponse.success("Bénévoles disponibles pour ce créneau", availableVolunteers));
    }

    /**
     * Obtenir des statistiques sur les bénévoles
     */
    @GetMapping(ApiConstants.VOLUNTEER_ENDPOINT + "/stats")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVolunteerStats() {
        Map<String, Object> stats = new HashMap<>();

        // Nombre d'inscriptions par statut
        stats.put("confirmed", volunteerService.countRegistrationsByStatus(RegistrationStatus.CONFIRMED));
        stats.put("cancelled", volunteerService.countRegistrationsByStatus(RegistrationStatus.CANCELLED));
        stats.put("completed", volunteerService.countRegistrationsByStatus(RegistrationStatus.COMPLETED));

        // Statistiques par type de rôle
        stats.put("byRoleType", volunteerService.getRegistrationStatsByRoleType());

        // Bénévoles les plus actifs
        stats.put("mostActive", volunteerService.findMostActiveVolunteers(5));

        return ResponseEntity.ok(ApiResponse.success("Statistiques des bénévoles", stats));
    }

    /**
     * Obtenir des statistiques sur les bénévoles d'un événement
     */
    @GetMapping(ApiConstants.VOLUNTEER_ENDPOINT + "/stats/event/{eventId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getVolunteerStatsByEvent(@PathVariable Long eventId) throws ResourceNotFoundException {
        Map<String, Object> stats = new HashMap<>();

        // Nombre de bénévoles pour cet événement
        stats.put("totalVolunteers", volunteerService.countVolunteersByEvent(eventId));

        return ResponseEntity.ok(ApiResponse.success("Statistiques des bénévoles pour l'événement", stats));
    }
}