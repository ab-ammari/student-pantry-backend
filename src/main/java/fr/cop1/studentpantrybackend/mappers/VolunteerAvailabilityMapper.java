package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.VolunteerAvailabilityDTO;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.entities.VolunteerAvailability;
import fr.cop1.studentpantrybackend.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VolunteerAvailabilityMapper {

    private final UserRepository userRepository;

    public VolunteerAvailabilityMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public VolunteerAvailabilityDTO toDTO(VolunteerAvailability availability) {
        if (availability == null) {
            return null;
        }

        VolunteerAvailabilityDTO dto = VolunteerAvailabilityDTO.builder()
                .id(availability.getId())
                .dayOfWeek(availability.getDayOfWeek())
                .startTime(availability.getStartTime())
                .endTime(availability.getEndTime())
                .isActive(availability.getIsActive())
                .createdAt(availability.getCreatedAt())
                .build();

        // Ajouter les informations de l'utilisateur
        if (availability.getUser() != null) {
            dto.setUserId(availability.getUser().getId());
            dto.setUserName(availability.getUser().getFirstName() + " " + availability.getUser().getLastName());
        }

        return dto;
    }

    public VolunteerAvailability toEntity(VolunteerAvailabilityDTO dto) {
        if (dto == null) {
            return null;
        }

        VolunteerAvailability availability = new VolunteerAvailability();
        availability.setId(dto.getId());
        availability.setDayOfWeek(dto.getDayOfWeek());
        availability.setStartTime(dto.getStartTime());
        availability.setEndTime(dto.getEndTime());
        availability.setIsActive(dto.getIsActive());

        // Récupérer l'utilisateur
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(availability::setUser);
        }

        return availability;
    }

    public void updateEntityFromDTO(VolunteerAvailabilityDTO dto, VolunteerAvailability availability) {
        if (dto == null || availability == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getDayOfWeek() != null) availability.setDayOfWeek(dto.getDayOfWeek());
        if (dto.getStartTime() != null) availability.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) availability.setEndTime(dto.getEndTime());
        if (dto.getIsActive() != null) availability.setIsActive(dto.getIsActive());

        // Mettre à jour l'utilisateur
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(availability::setUser);
        }
    }
}