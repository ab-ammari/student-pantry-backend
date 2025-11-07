package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.EventDTO;
import fr.cop1.studentpantrybackend.dtos.TimeSlotDTO;
import fr.cop1.studentpantrybackend.enums.EventStatus;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface EventService {
    // Opérations CRUD de base
    EventDTO findById(Long id) throws ResourceNotFoundException;
    List<EventDTO> findAll();
    Page<EventDTO> findAll(Pageable pageable);
    EventDTO save(EventDTO eventDTO);
    EventDTO update(Long id, EventDTO eventDTO) throws ResourceNotFoundException;
    void delete(Long id) throws ResourceNotFoundException;

    // Méthodes spécifiques
    List<EventDTO> findByStatus(EventStatus status);
    List<EventDTO> findUpcomingEvents();
    List<EventDTO> findPastEvents();
    List<EventDTO> findEventsBetweenDates(LocalDate startDate, LocalDate endDate);
    List<EventDTO> findEventsByCreator(Long userId) throws ResourceNotFoundException;
    List<EventDTO> searchEvents(String keyword);

    // Méthodes de gestion du cycle de vie
    EventDTO publishEvent(Long id) throws ResourceNotFoundException;
    EventDTO cancelEvent(Long id) throws ResourceNotFoundException;
    EventDTO completeEvent(Long id) throws ResourceNotFoundException;

    // Méthodes de gestion des créneaux
    EventDTO addTimeSlot(Long eventId, TimeSlotDTO timeSlotDTO) throws ResourceNotFoundException;
    EventDTO updateTimeSlot(Long eventId, Long timeSlotId, TimeSlotDTO timeSlotDTO) throws ResourceNotFoundException;
    EventDTO removeTimeSlot(Long eventId, Long timeSlotId) throws ResourceNotFoundException;

    // Statistiques
    Long countEventsByStatus(EventStatus status);
    Long countUpcomingEvents();
}
