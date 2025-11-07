package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.EventDTO;
import fr.cop1.studentpantrybackend.dtos.TimeSlotDTO;
import fr.cop1.studentpantrybackend.entities.Event;
import fr.cop1.studentpantrybackend.entities.TimeSlot;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.enums.EventStatus;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.mappers.EventMapper;
import fr.cop1.studentpantrybackend.mappers.TimeSlotMapper;
import fr.cop1.studentpantrybackend.repositories.EventRepository;
import fr.cop1.studentpantrybackend.repositories.TimeSlotRepository;
import fr.cop1.studentpantrybackend.repositories.UserRepository;

import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventRepository eventRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final UserRepository userRepository;
    private final EventMapper eventMapper;
    private final TimeSlotMapper timeSlotMapper;
    private final NotificationService notificationService;

    @Override
    public EventDTO findById(Long id) throws ResourceNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + id));
        return eventMapper.toDTO(event);
    }

    @Override
    public List<EventDTO> findAll() {
        return eventRepository.findAll().stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<EventDTO> findAll(Pageable pageable) {
        return eventRepository.findAll(pageable)
                .map(eventMapper::toDTO);
    }

    @Override
    public EventDTO save(EventDTO eventDTO) {
        // Valider les données
        validateEvent(eventDTO);

        // Convertir DTO en entité
        Event event = eventMapper.toEntity(eventDTO);

        // Définir l'état initial si non spécifié
        if (event.getStatus() == null) {
            event.setStatus(EventStatus.DRAFT);
        }

        // Définir les horodatages
        LocalDateTime now = LocalDateTime.now();
        event.setCreatedAt(now);
        event.setUpdatedAt(now);

        // Enregistrer l'événement
        Event savedEvent = eventRepository.save(event);

        return eventMapper.toDTO(savedEvent);
    }

    @Override
    public EventDTO update(Long id, EventDTO eventDTO) throws ResourceNotFoundException {
        Event existingEvent = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + id));

        // Vérifier si l'événement peut être modifié
        if (existingEvent.getStatus() == EventStatus.COMPLETED) {
            throw new ValidationException("Impossible de modifier un événement terminé");
        }

        // Valider les données
        validateEvent(eventDTO);

        // Mettre à jour l'événement avec les données du DTO
        eventMapper.updateEntityFromDTO(eventDTO, existingEvent);

        // Mettre à jour l'horodatage
        existingEvent.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        Event updatedEvent = eventRepository.save(existingEvent);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + id));

        // Vérifier si l'événement peut être supprimé
        if (event.getStatus() == EventStatus.PUBLISHED || event.getStatus() == EventStatus.COMPLETED) {
            throw new ValidationException("Impossible de supprimer un événement publié ou terminé");
        }

        eventRepository.delete(event);
    }

    @Override
    public List<EventDTO> findByStatus(EventStatus status) {
        return eventRepository.findByStatus(status).stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEventDateAfter(today).stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findPastEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEventDateBefore(today).stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsBetweenDates(LocalDate startDate, LocalDate endDate) {
        return eventRepository.findByEventDateBetween(startDate, endDate).stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> findEventsByCreator(Long userId) throws ResourceNotFoundException {
        User creator = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return eventRepository.findByCreatedBy(creator).stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<EventDTO> searchEvents(String keyword) {
        return eventRepository.findByNameContainingIgnoreCase(keyword).stream()
                .map(eventMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public EventDTO publishEvent(Long id) throws ResourceNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + id));

        // Vérifier si l'événement peut être publié
        if (event.getStatus() != EventStatus.DRAFT) {
            throw new ValidationException("Seuls les événements en brouillon peuvent être publiés");
        }

        // Vérifier si l'événement a des créneaux
        if (event.getTimeSlots() == null || event.getTimeSlots().isEmpty()) {
            throw new ValidationException("L'événement doit avoir au moins un créneau horaire pour être publié");
        }

        // Mettre à jour le statut
        event.setStatus(EventStatus.PUBLISHED);
        event.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        Event updatedEvent = eventRepository.save(event);

        // Envoyer des notifications aux utilisateurs concernés
        notifyUsersAboutEvent(updatedEvent);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    public EventDTO cancelEvent(Long id) throws ResourceNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + id));

        // Vérifier si l'événement peut être annulé
        if (event.getStatus() == EventStatus.COMPLETED) {
            throw new ValidationException("Impossible d'annuler un événement terminé");
        }

        // Mettre à jour le statut
        event.setStatus(EventStatus.CANCELLED);
        event.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        Event updatedEvent = eventRepository.save(event);

        // Notifier les utilisateurs de l'annulation
        notificationService.notifyEventCancellation(id);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    public EventDTO completeEvent(Long id) throws ResourceNotFoundException {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + id));

        // Vérifier si l'événement peut être marqué comme terminé
        if (event.getStatus() != EventStatus.PUBLISHED) {
            throw new ValidationException("Seuls les événements publiés peuvent être marqués comme terminés");
        }

        // Vérifier si l'événement a eu lieu
        if (event.getEventDate().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Impossible de terminer un événement qui n'a pas encore eu lieu");
        }

        // Mettre à jour le statut
        event.setStatus(EventStatus.COMPLETED);
        event.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        Event updatedEvent = eventRepository.save(event);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    public EventDTO addTimeSlot(Long eventId, TimeSlotDTO timeSlotDTO) throws ResourceNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + eventId));

        // Vérifier si l'événement peut être modifié
        if (event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.CANCELLED) {
            throw new ValidationException("Impossible de modifier les créneaux d'un événement terminé ou annulé");
        }

        // Convertir DTO en entité
        TimeSlot timeSlot = timeSlotMapper.toEntity(timeSlotDTO);

        // Associer le créneau à l'événement
        timeSlot.setEvent(event);
        timeSlot.setCreatedAt(LocalDateTime.now());
        timeSlot.setUpdatedAt(LocalDateTime.now());

        // Définir le nombre de places disponibles initial
        if (timeSlot.getAvailableSpots() == null) {
            timeSlot.setAvailableSpots(timeSlot.getMaxCapacity());
        }

        // Enregistrer le créneau
        TimeSlot savedTimeSlot = timeSlotRepository.save(timeSlot);

        // Mettre à jour l'événement
        event.getTimeSlots().add(savedTimeSlot);
        event.setUpdatedAt(LocalDateTime.now());
        Event updatedEvent = eventRepository.save(event);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    public EventDTO updateTimeSlot(Long eventId, Long timeSlotId, TimeSlotDTO timeSlotDTO) throws ResourceNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + eventId));

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        // Vérifier si le créneau appartient à l'événement
        if (!timeSlot.getEvent().getId().equals(eventId)) {
            throw new ValidationException("Le créneau n'appartient pas à cet événement");
        }

        // Vérifier si l'événement peut être modifié
        if (event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.CANCELLED) {
            throw new ValidationException("Impossible de modifier les créneaux d'un événement terminé ou annulé");
        }

        // Mettre à jour le créneau avec les données du DTO
        timeSlotMapper.updateEntityFromDTO(timeSlotDTO, timeSlot);

        // Mettre à jour l'horodatage
        timeSlot.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        timeSlotRepository.save(timeSlot);

        // Mettre à jour l'événement
        event.setUpdatedAt(LocalDateTime.now());
        Event updatedEvent = eventRepository.save(event);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    public EventDTO removeTimeSlot(Long eventId, Long timeSlotId) throws ResourceNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + eventId));

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        // Vérifier si le créneau appartient à l'événement
        if (!timeSlot.getEvent().getId().equals(eventId)) {
            throw new ValidationException("Le créneau n'appartient pas à cet événement");
        }

        // Vérifier si l'événement peut être modifié
        if (event.getStatus() == EventStatus.COMPLETED || event.getStatus() == EventStatus.CANCELLED) {
            throw new ValidationException("Impossible de modifier les créneaux d'un événement terminé ou annulé");
        }

        // Vérifier si le créneau a des réservations
        if (timeSlot.getReservations() != null && !timeSlot.getReservations().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un créneau qui a des réservations");
        }

        // Supprimer le créneau de l'événement
        event.getTimeSlots().remove(timeSlot);

        // Mettre à jour l'événement
        event.setUpdatedAt(LocalDateTime.now());
        Event updatedEvent = eventRepository.save(event);

        // Supprimer le créneau
        timeSlotRepository.delete(timeSlot);

        return eventMapper.toDTO(updatedEvent);
    }

    @Override
    public Long countEventsByStatus(EventStatus status) {
        return eventRepository.findByStatus(status).stream().count();
    }

    @Override
    public Long countUpcomingEvents() {
        LocalDate today = LocalDate.now();
        return eventRepository.findByEventDateAfter(today).stream().count();
    }

    // Méthodes utilitaires

    private void validateEvent(EventDTO eventDTO) {
        if (eventDTO.getEventDate() != null && eventDTO.getEventDate().isBefore(LocalDateTime.now())) {
            throw new ValidationException("La date de l'événement ne peut pas être dans le passé");
        }
    }

    private void notifyUsersAboutEvent(Event event) {
        // Ici, vous pourriez implémenter la logique pour envoyer des notifications
        // aux utilisateurs qui pourraient être intéressés par l'événement
        // Par exemple, les étudiants d'une école spécifique
    }
}
