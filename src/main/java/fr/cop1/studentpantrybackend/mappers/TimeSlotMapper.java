package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.TimeSlotDTO;
import fr.cop1.studentpantrybackend.entities.Event;
import fr.cop1.studentpantrybackend.entities.TimeSlot;
import fr.cop1.studentpantrybackend.repositories.EventRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class TimeSlotMapper {

    private final EventRepository eventRepository;

    public TimeSlotMapper(EventRepository eventRepository) {
        this.eventRepository = eventRepository;
    }

    public TimeSlotDTO toDTO(TimeSlot timeSlot) {
        if (timeSlot == null) {
            return null;
        }

        TimeSlotDTO dto = TimeSlotDTO.builder()
                .id(timeSlot.getId())
                .startTime(timeSlot.getStartTime())
                .endTime(timeSlot.getEndTime())
                .maxCapacity(timeSlot.getMaxCapacity())
                .availableSpots(timeSlot.getAvailableSpots())
                .createdAt(timeSlot.getCreatedAt())
                .updatedAt(timeSlot.getUpdatedAt())
                .build();

        // Ajouter des informations sur l'événement si disponible
        if (timeSlot.getEvent() != null) {
            dto.setEventId(timeSlot.getEvent().getId());
            dto.setEventName(timeSlot.getEvent().getName());
        }

        return dto;
    }

    // Surcharge pour inclure les réservations et les shifts si nécessaire
    public TimeSlotDTO toDTOWithRelations(TimeSlot timeSlot, boolean includeReservations, boolean includeVolunteerShifts) {
        TimeSlotDTO dto = toDTO(timeSlot);

        if (dto == null) {
            return null;
        }

        // À implémenter une fois que les mappers pour Reservation et VolunteerShift sont créés
        // Cette méthode sera complétée plus tard pour éviter les dépendances circulaires

        return dto;
    }

    public TimeSlot toEntity(TimeSlotDTO dto) {
        if (dto == null) {
            return null;
        }

        TimeSlot timeSlot = new TimeSlot();
        timeSlot.setId(dto.getId());
        timeSlot.setStartTime(dto.getStartTime());
        timeSlot.setEndTime(dto.getEndTime());
        timeSlot.setMaxCapacity(dto.getMaxCapacity());
        timeSlot.setAvailableSpots(dto.getAvailableSpots());

        // Récupérer l'événement si l'ID est fourni
        if (dto.getEventId() != null) {
            Optional<Event> event = eventRepository.findById(dto.getEventId());
            event.ifPresent(timeSlot::setEvent);
        }

        return timeSlot;
    }

    public void updateEntityFromDTO(TimeSlotDTO dto, TimeSlot timeSlot) {
        if (dto == null || timeSlot == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getStartTime() != null) timeSlot.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) timeSlot.setEndTime(dto.getEndTime());
        if (dto.getMaxCapacity() != null) timeSlot.setMaxCapacity(dto.getMaxCapacity());
        if (dto.getAvailableSpots() != null) timeSlot.setAvailableSpots(dto.getAvailableSpots());

        // Mettre à jour l'événement si l'ID est fourni
        if (dto.getEventId() != null) {
            Optional<Event> event = eventRepository.findById(dto.getEventId());
            event.ifPresent(timeSlot::setEvent);
        }
    }
}

