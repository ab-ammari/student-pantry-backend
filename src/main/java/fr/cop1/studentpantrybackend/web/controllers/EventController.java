package fr.cop1.studentpantrybackend.web.controllers;

import fr.cop1.studentpantrybackend.dtos.EventDTO;
import fr.cop1.studentpantrybackend.dtos.TimeSlotDTO;
import fr.cop1.studentpantrybackend.enums.EventStatus;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.services.EventService;
import fr.cop1.studentpantrybackend.web.ApiConstants;
import fr.cop1.studentpantrybackend.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des événements
 */
@RestController
@RequestMapping(ApiConstants.EVENTS_ENDPOINT)
@RequiredArgsConstructor
@Validated
public class EventController {

    private final EventService eventService;

    /**
     * Récupérer tous les événements (avec pagination)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<Page<EventDTO>>> getAllEvents(
            @RequestParam(value = ApiConstants.PAGE_PARAM, defaultValue = "0") int page,
            @RequestParam(value = ApiConstants.SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = ApiConstants.SORT_PARAM, defaultValue = "eventDate") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<EventDTO> events = eventService.findAll(pageable);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalPages", events.getTotalPages());
        meta.put("totalElements", events.getTotalElements());
        meta.put("size", events.getSize());
        meta.put("page", events.getNumber());

        return ResponseEntity.ok(ApiResponse.success("Liste des événements récupérée", events, meta));
    }

    /**
     * Récupérer un événement par son ID
     */
    @GetMapping(ApiConstants.EVENT_ID_PATH)
    public ResponseEntity<ApiResponse<EventDTO>> getEventById(@PathVariable Long id) throws ResourceNotFoundException {
        EventDTO event = eventService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(event));
    }

    /**
     * Récupérer les événements par statut
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsByStatus(@PathVariable EventStatus status) {
        List<EventDTO> events = eventService.findByStatus(status);
        return ResponseEntity.ok(ApiResponse.success("Événements avec le statut " + status, events));
    }

    /**
     * Récupérer les événements à venir
     */
    @GetMapping("/upcoming")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getUpcomingEvents() {
        List<EventDTO> events = eventService.findUpcomingEvents();
        return ResponseEntity.ok(ApiResponse.success("Événements à venir", events));
    }

    /**
     * Récupérer les événements passés
     */
    @GetMapping("/past")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getPastEvents() {
        List<EventDTO> events = eventService.findPastEvents();
        return ResponseEntity.ok(ApiResponse.success("Événements passés", events));
    }

    /**
     * Récupérer les événements entre deux dates
     */
    @GetMapping("/between")
    public ResponseEntity<ApiResponse<List<EventDTO>>> getEventsBetweenDates(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        List<EventDTO> events = eventService.findEventsBetweenDates(startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("Événements entre " + startDate + " et " + endDate, events));
    }

    /**
     * Rechercher des événements par mot-clé
     */
    @GetMapping("/search")
    public ResponseEntity<ApiResponse<List<EventDTO>>> searchEvents(@RequestParam String keyword) {
        List<EventDTO> events = eventService.searchEvents(keyword);
        return ResponseEntity.ok(ApiResponse.success("Résultats de recherche pour: " + keyword, events));
    }

    /**
     * Créer un nouvel événement
     */
    @PostMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> createEvent(@Valid @RequestBody EventDTO eventDTO) {
        EventDTO createdEvent = eventService.save(eventDTO);
        return new ResponseEntity<>(ApiResponse.success("Événement créé avec succès", createdEvent), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour un événement existant
     */
    @PutMapping(ApiConstants.EVENT_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> updateEvent(@PathVariable Long id, @Valid @RequestBody EventDTO eventDTO) throws ResourceNotFoundException {
        EventDTO updatedEvent = eventService.update(id, eventDTO);
        return ResponseEntity.ok(ApiResponse.success("Événement mis à jour avec succès", updatedEvent));
    }

    /**
     * Supprimer un événement
     */
    @DeleteMapping(ApiConstants.EVENT_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> deleteEvent(@PathVariable Long id) throws ResourceNotFoundException {
        eventService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Événement supprimé avec succès", null));
    }

    /**
     * Publier un événement
     */
    @PutMapping("/{id}/publish")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> publishEvent(@PathVariable Long id) throws ResourceNotFoundException {
        EventDTO publishedEvent = eventService.publishEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Événement publié", publishedEvent));
    }

    /**
     * Annuler un événement
     */
    @PutMapping("/{id}/cancel")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> cancelEvent(@PathVariable Long id) throws ResourceNotFoundException {
        EventDTO canceledEvent = eventService.cancelEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Événement annulé", canceledEvent));
    }

    /**
     * Marquer un événement comme terminé
     */
    @PutMapping("/{id}/complete")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> completeEvent(@PathVariable Long id) throws ResourceNotFoundException {
        EventDTO completedEvent = eventService.completeEvent(id);
        return ResponseEntity.ok(ApiResponse.success("Événement marqué comme terminé", completedEvent));
    }

    /**
     * Ajouter un créneau horaire à un événement
     */
    @PostMapping(ApiConstants.EVENT_TIMESLOTS_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> addTimeSlotToEvent(
            @PathVariable Long id,
            @Valid @RequestBody TimeSlotDTO timeSlotDTO) throws ResourceNotFoundException {

        EventDTO updatedEvent = eventService.addTimeSlot(id, timeSlotDTO);
        return ResponseEntity.ok(ApiResponse.success("Créneau horaire ajouté", updatedEvent));
    }

    /**
     * Mettre à jour un créneau horaire
     */
    @PutMapping(ApiConstants.EVENT_TIMESLOTS_PATH + ApiConstants.TIMESLOT_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> updateTimeSlot(
            @PathVariable Long id,
            @PathVariable Long timeslotId,
            @Valid @RequestBody TimeSlotDTO timeSlotDTO) throws ResourceNotFoundException {

        EventDTO updatedEvent = eventService.updateTimeSlot(id, timeslotId, timeSlotDTO);
        return ResponseEntity.ok(ApiResponse.success("Créneau horaire mis à jour", updatedEvent));
    }

    /**
     * Supprimer un créneau horaire
     */
    @DeleteMapping(ApiConstants.EVENT_TIMESLOTS_PATH + ApiConstants.TIMESLOT_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<EventDTO>> removeTimeSlot(
            @PathVariable Long id,
            @PathVariable Long timeslotId) throws ResourceNotFoundException {

        EventDTO updatedEvent = eventService.removeTimeSlot(id, timeslotId);
        return ResponseEntity.ok(ApiResponse.success("Créneau horaire supprimé", updatedEvent));
    }

    /**
     * Obtenir des statistiques sur les événements
     */
    @GetMapping("/stats")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getEventStats() {
        Map<String, Object> stats = new HashMap<>();

        // Nombre d'événements par statut
        stats.put("draft", eventService.countEventsByStatus(EventStatus.DRAFT));
        stats.put("published", eventService.countEventsByStatus(EventStatus.PUBLISHED));
        stats.put("completed", eventService.countEventsByStatus(EventStatus.COMPLETED));
        stats.put("cancelled", eventService.countEventsByStatus(EventStatus.CANCELLED));

        // Nombre d'événements à venir
        stats.put("upcoming", eventService.countUpcomingEvents());

        return ResponseEntity.ok(ApiResponse.success("Statistiques des événements", stats));
    }
}
