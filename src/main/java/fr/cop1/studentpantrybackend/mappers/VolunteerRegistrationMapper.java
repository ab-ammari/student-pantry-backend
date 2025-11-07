package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.VolunteerRegistrationDTO;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.entities.VolunteerRegistration;
import fr.cop1.studentpantrybackend.entities.VolunteerShift;
import fr.cop1.studentpantrybackend.repositories.UserRepository;
import fr.cop1.studentpantrybackend.repositories.VolunteerShiftRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VolunteerRegistrationMapper {

    private final UserRepository userRepository;
    private final VolunteerShiftRepository volunteerShiftRepository;

    public VolunteerRegistrationMapper(UserRepository userRepository,
                                       VolunteerShiftRepository volunteerShiftRepository) {
        this.userRepository = userRepository;
        this.volunteerShiftRepository = volunteerShiftRepository;
    }

    public VolunteerRegistrationDTO toDTO(VolunteerRegistration registration) {
        if (registration == null) {
            return null;
        }

        VolunteerRegistrationDTO dto = VolunteerRegistrationDTO.builder()
                .id(registration.getId())
                .status(registration.getStatus())
                .isTeamLeader(registration.getIsTeamLeader())
                .notes(registration.getNotes())
                .checkedInAt(registration.getCheckedInAt())
                .createdAt(registration.getCreatedAt())
                .updatedAt(registration.getUpdatedAt())
                .build();

        // Ajouter les informations de l'utilisateur
        if (registration.getUser() != null) {
            dto.setUserId(registration.getUser().getId());
            dto.setUserName(registration.getUser().getFirstName() + " " + registration.getUser().getLastName());
        }

        // Ajouter les informations du poste bénévole
        if (registration.getVolunteerShift() != null) {
            dto.setVolunteerShiftId(registration.getVolunteerShift().getId());

            if (registration.getVolunteerShift().getRoleType() != null) {
                dto.setRoleType(registration.getVolunteerShift().getRoleType().toString());
            }

            // Ajouter les informations de l'événement si disponible
            if (registration.getVolunteerShift().getTimeSlot() != null) {
                if (registration.getVolunteerShift().getTimeSlot().getEvent() != null) {
                    dto.setEventName(registration.getVolunteerShift().getTimeSlot().getEvent().getName());

                    if (registration.getVolunteerShift().getTimeSlot().getEvent().getEventDate() != null) {
                        dto.setEventDate(registration.getVolunteerShift().getTimeSlot().getStartTime());
                    }
                }
            }
        }

        return dto;
    }

    public VolunteerRegistration toEntity(VolunteerRegistrationDTO dto) {
        if (dto == null) {
            return null;
        }

        VolunteerRegistration registration = new VolunteerRegistration();
        registration.setId(dto.getId());
        registration.setStatus(dto.getStatus());
        registration.setIsTeamLeader(dto.getIsTeamLeader());
        registration.setNotes(dto.getNotes());
        registration.setCheckedInAt(dto.getCheckedInAt());

        // Récupérer l'utilisateur
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(registration::setUser);
        }

        // Récupérer le poste bénévole
        if (dto.getVolunteerShiftId() != null) {
            Optional<VolunteerShift> shift = volunteerShiftRepository.findById(dto.getVolunteerShiftId());
            shift.ifPresent(registration::setVolunteerShift);
        }

        return registration;
    }

    public void updateEntityFromDTO(VolunteerRegistrationDTO dto, VolunteerRegistration registration) {
        if (dto == null || registration == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getStatus() != null) registration.setStatus(dto.getStatus());
        if (dto.getIsTeamLeader() != null) registration.setIsTeamLeader(dto.getIsTeamLeader());
        if (dto.getNotes() != null) registration.setNotes(dto.getNotes());
        if (dto.getCheckedInAt() != null) registration.setCheckedInAt(dto.getCheckedInAt());

        // Mettre à jour l'utilisateur
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(registration::setUser);
        }

        // Mettre à jour le poste bénévole
        if (dto.getVolunteerShiftId() != null) {
            Optional<VolunteerShift> shift = volunteerShiftRepository.findById(dto.getVolunteerShiftId());
            shift.ifPresent(registration::setVolunteerShift);
        }
    }
}