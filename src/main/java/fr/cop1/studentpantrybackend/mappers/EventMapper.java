package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.EventDTO;
import fr.cop1.studentpantrybackend.entities.Event;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class EventMapper {

    private final TimeSlotMapper timeSlotMapper;
    private final UserRepository userRepository;

    public EventMapper(TimeSlotMapper timeSlotMapper, UserRepository userRepository) {
        this.timeSlotMapper = timeSlotMapper;
        this.userRepository = userRepository;
    }

    public EventDTO toDTO(Event event) {
        if (event == null) {
            return null;
        }

        EventDTO dto = EventDTO.builder()
                .id(event.getId())
                .name(event.getName())
                .description(event.getDescription())
                .location(event.getLocation())
                .eventDate(event.getEventDate())
                .status(event.getStatus())
                .createdAt(event.getCreatedAt())
                .updatedAt(event.getUpdatedAt())
                .build();

        // Ajouter des informations sur le créateur si disponible
        if (event.getCreatedBy() != null) {
            dto.setCreatedById(event.getCreatedBy().getId());
            dto.setCreatedByName(event.getCreatedBy().getFirstName() + " " + event.getCreatedBy().getLastName());
        }

        // Ajouter optionnellement les créneaux si nécessaire
        if (event.getTimeSlots() != null && !event.getTimeSlots().isEmpty()) {
            dto.setTimeSlots(event.getTimeSlots().stream()
                    .map(timeSlotMapper::toDTO)
                    .collect(Collectors.toSet()));
        }

        return dto;
    }

    public Event toEntity(EventDTO dto) {
        if (dto == null) {
            return null;
        }

        Event event = new Event();
        event.setId(dto.getId());
        event.setName(dto.getName());
        event.setDescription(dto.getDescription());
        event.setLocation(dto.getLocation());
        event.setEventDate(dto.getEventDate());
        event.setStatus(dto.getStatus());

        // Récupérer le créateur si l'ID est fourni
        if (dto.getCreatedById() != null) {
            Optional<User> creator = userRepository.findById(dto.getCreatedById());
            creator.ifPresent(event::setCreatedBy);
        }

        return event;
    }

    public void updateEntityFromDTO(EventDTO dto, Event event) {
        if (dto == null || event == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getName() != null) event.setName(dto.getName());
        if (dto.getDescription() != null) event.setDescription(dto.getDescription());
        if (dto.getLocation() != null) event.setLocation(dto.getLocation());
        if (dto.getEventDate() != null) event.setEventDate(dto.getEventDate());
        if (dto.getStatus() != null) event.setStatus(dto.getStatus());

        // Mettre à jour le créateur si l'ID est fourni
        if (dto.getCreatedById() != null) {
            Optional<User> creator = userRepository.findById(dto.getCreatedById());
            creator.ifPresent(event::setCreatedBy);
        }
    }
}

