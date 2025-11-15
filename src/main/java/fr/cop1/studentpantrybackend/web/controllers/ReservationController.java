package fr.cop1.studentpantrybackend.web.controllers;

import fr.cop1.studentpantrybackend.dtos.ReservationDTO;
import fr.cop1.studentpantrybackend.enums.ReservationStatus;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.services.ReservationService;
import fr.cop1.studentpantrybackend.web.ApiConstants;
import fr.cop1.studentpantrybackend.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des réservations
 */
@RestController
@RequestMapping(ApiConstants.RESERVATIONS_ENDPOINT)
@RequiredArgsConstructor
@Validated
public class ReservationController {

    private final ReservationService reservationService;

    /**
     * Récupérer toutes les réservations (avec pagination)
     */
    @GetMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<ReservationDTO>>> getAllReservations(
            @RequestParam(value = ApiConstants.PAGE_PARAM, defaultValue = "0") int page,
            @RequestParam(value = ApiConstants.SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = ApiConstants.SORT_PARAM, defaultValue = "createdAt") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<ReservationDTO> reservations = reservationService.findAll(pageable);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalPages", reservations.getTotalPages());
        meta.put("totalElements", reservations.getTotalElements());
        meta.put("size", reservations.getSize());
        meta.put("page", reservations.getNumber());

        return ResponseEntity.ok(ApiResponse.success("Liste des réservations récupérée", reservations, meta));
    }

    /**
     * Récupérer une réservation par son ID
     */
    @GetMapping(ApiConstants.RESERVATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @reservationSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<ReservationDTO>> getReservationById(@PathVariable Long id) throws ResourceNotFoundException {
        ReservationDTO reservation = reservationService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(reservation));
    }

    /**
     * Récupérer les réservations d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getReservationsByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        List<ReservationDTO> reservations = reservationService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Réservations de l'utilisateur", reservations));
    }

    /**
     * Récupérer les réservations d'un créneau horaire
     */
    @GetMapping("/timeslot/{timeSlotId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getReservationsByTimeSlotId(@PathVariable Long timeSlotId) throws ResourceNotFoundException {
        List<ReservationDTO> reservations = reservationService.findByTimeSlotId(timeSlotId);
        return ResponseEntity.ok(ApiResponse.success("Réservations du créneau horaire", reservations));
    }

    /**
     * Récupérer les réservations d'un événement
     */
    @GetMapping("/event/{eventId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getReservationsByEventId(@PathVariable Long eventId) {
        List<ReservationDTO> reservations = reservationService.findByEventId(eventId);
        return ResponseEntity.ok(ApiResponse.success("Réservations de l'événement", reservations));
    }

    /**
     * Récupérer les réservations par statut
     */
    @GetMapping("/status/{status}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getReservationsByStatus(@PathVariable ReservationStatus status) {
        List<ReservationDTO> reservations = reservationService.findByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Réservations avec le statut " + status, reservations));
    }

    /**
     * Récupérer les réservations récentes
     */
    @GetMapping("/recent")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<List<ReservationDTO>>> getRecentReservations(
            @RequestParam(defaultValue = "10") int limit) {

        List<ReservationDTO> reservations = reservationService.findRecentReservations(limit);
        return ResponseEntity.ok(ApiResponse.success("Réservations récentes", reservations));
    }

    /**
     * Créer une nouvelle réservation
     */
    @PostMapping
    //@PreAuthorize("hasAnyRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservationDTO>> createReservation(@Valid @RequestBody ReservationDTO reservationDTO) throws ResourceNotFoundException {
        ReservationDTO createdReservation = reservationService.save(reservationDTO);
        return new ResponseEntity<>(ApiResponse.success("Réservation créée avec succès", createdReservation), HttpStatus.CREATED);
    }

    /**
     * Créer une réservation (endpoint simplifié)
     */
    @PostMapping("/create")
    //@PreAuthorize("hasAnyRole('STUDENT') or hasRole('ADMIN')")
    public ResponseEntity<ApiResponse<ReservationDTO>> createSimpleReservation(
            @RequestParam Long userId,
            @RequestParam Long timeSlotId,
            @RequestParam Long basketTypeId,
            @RequestParam(required = false) String notes) throws ResourceNotFoundException {

        ReservationDTO createdReservation = reservationService.createReservation(userId, timeSlotId, basketTypeId, notes);
        return new ResponseEntity<>(ApiResponse.success("Réservation créée avec succès", createdReservation), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour une réservation existante
     */
    @PutMapping(ApiConstants.RESERVATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @reservationSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<ReservationDTO>> updateReservation(
            @PathVariable Long id,
            @Valid @RequestBody ReservationDTO reservationDTO) throws ResourceNotFoundException {

        ReservationDTO updatedReservation = reservationService.update(id, reservationDTO);
        return ResponseEntity.ok(ApiResponse.success("Réservation mise à jour avec succès", updatedReservation));
    }

    /**
     * Supprimer une réservation
     */
    @DeleteMapping(ApiConstants.RESERVATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @reservationSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<Void>> deleteReservation(@PathVariable Long id) throws ResourceNotFoundException {
        reservationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Réservation supprimée avec succès", null));
    }

    /**
     * Annuler une réservation
     */
    @PutMapping("/{id}/cancel")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @reservationSecurity.isOwner(#id)")
    public ResponseEntity<ApiResponse<ReservationDTO>> cancelReservation(
            @PathVariable Long id,
            @RequestParam(required = false) String reason) throws ResourceNotFoundException {

        ReservationDTO canceledReservation = reservationService.cancelReservation(id, reason);
        return ResponseEntity.ok(ApiResponse.success("Réservation annulée", canceledReservation));
    }

    /**
     * Check-in d'une réservation
     */
    @PutMapping("/{id}/check-in")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<ReservationDTO>> checkInReservation(@PathVariable Long id) throws ResourceNotFoundException {
        ReservationDTO checkedInReservation = reservationService.checkInReservation(id);
        return ResponseEntity.ok(ApiResponse.success("Check-in effectué", checkedInReservation));
    }

    /**
     * Marquer une réservation comme non présentée
     */
    @PutMapping("/{id}/no-show")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN', 'VOLUNTEER')")
    public ResponseEntity<ApiResponse<ReservationDTO>> markAsNoShow(@PathVariable Long id) throws ResourceNotFoundException {
        ReservationDTO noShowReservation = reservationService.markAsNoShow(id);
        return ResponseEntity.ok(ApiResponse.success("Réservation marquée comme non présentée", noShowReservation));
    }

    /**
     * Vérifier si un créneau est disponible
     */
    @GetMapping("/check-availability")
    public ResponseEntity<ApiResponse<Boolean>> isTimeSlotAvailable(@RequestParam Long timeSlotId) throws ResourceNotFoundException {
        boolean isAvailable = reservationService.isTimeSlotAvailable(timeSlotId);
        String message = isAvailable ? "Créneau disponible" : "Créneau complet";
        return ResponseEntity.ok(ApiResponse.success(message, isAvailable));
    }

    /**
     * Vérifier si un utilisateur peut réserver un créneau
     */
    @GetMapping("/can-reserve")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<Boolean>> canUserReserve(
            @RequestParam Long userId,
            @RequestParam Long timeSlotId) throws ResourceNotFoundException {

        boolean canReserve = reservationService.canUserReserve(userId, timeSlotId);
        String message = canReserve ? "L'utilisateur peut réserver ce créneau" : "L'utilisateur ne peut pas réserver ce créneau";
        return ResponseEntity.ok(ApiResponse.success(message, canReserve));
    }

    /**
     * Obtenir des statistiques sur les réservations
     */
    @GetMapping("/stats")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReservationStats() {
        Map<String, Object> stats = new HashMap<>();

        // Nombre de réservations par statut
        stats.put("confirmed", reservationService.countReservationsByStatus(ReservationStatus.COMFIRMED));
        stats.put("cancelled", reservationService.countReservationsByStatus(ReservationStatus.CANCELLED));
        stats.put("checkedIn", reservationService.countReservationsByStatus(ReservationStatus.CHECKED_IN));
        stats.put("noShow", reservationService.countReservationsByStatus(ReservationStatus.NO_SHOW));

        // Statistiques par type de panier
        stats.put("byBasketType", reservationService.getReservationStatsByBasketType());

        return ResponseEntity.ok(ApiResponse.success("Statistiques des réservations", stats));
    }

    /**
     * Obtenir des statistiques sur les réservations d'un événement
     */
    @GetMapping("/stats/event/{eventId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getReservationStatsByEvent(@PathVariable Long eventId) {
        Map<String, Object> stats = new HashMap<>();

        // Statistiques par créneau horaire
        stats.put("byTimeSlot", reservationService.getReservationStatsByTimeSlot(eventId));

        return ResponseEntity.ok(ApiResponse.success("Statistiques des réservations pour l'événement", stats));
    }
}
