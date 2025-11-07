package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.VolunteerShiftDTO;
import fr.cop1.studentpantrybackend.entities.TimeSlot;
import fr.cop1.studentpantrybackend.entities.VolunteerShift;
import fr.cop1.studentpantrybackend.repositories.TimeSlotRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class VolunteerShiftMapper {

    private final TimeSlotRepository timeSlotRepository;

    public VolunteerShiftMapper(TimeSlotRepository timeSlotRepository) {
        this.timeSlotRepository = timeSlotRepository;
    }

    public VolunteerShiftDTO toDTO(VolunteerShift volunteerShift) {
        if (volunteerShift == null) {
            return null;
        }

        VolunteerShiftDTO dto = VolunteerShiftDTO.builder()
                .id(volunteerShift.getId())
                .roleType(volunteerShift.getRoleType())
                .requiredVolunteers(volunteerShift.getRequiredVolunteers())
                .minExperienceLevel(volunteerShift.getMinExperienceLevel())
                .description(volunteerShift.getDescription())
                .startTime(volunteerShift.getStartTime())
                .endTime(volunteerShift.getEndTime())
                .createdAt(volunteerShift.getCreatedAt())
                .build();

        // Ajouter les informations du créneau
        if (volunteerShift.getTimeSlot() != null) {
            dto.setTimeSlotId(volunteerShift.getTimeSlot().getId());
            dto.setSlotStartTime(volunteerShift.getTimeSlot().getStartTime());
            dto.setSlotEndTime(volunteerShift.getTimeSlot().getEndTime());

            // Ajouter le nom de l'événement si disponible
            if (volunteerShift.getTimeSlot().getEvent() != null) {
                dto.setEventName(volunteerShift.getTimeSlot().getEvent().getName());
            }
        }

        return dto;
    }

    // Surcharge pour inclure les inscriptions si nécessaire
    public VolunteerShiftDTO toDTOWithRegistrations(VolunteerShift volunteerShift,
                                                    VolunteerRegistrationMapper registrationMapper) {
        VolunteerShiftDTO dto = toDTO(volunteerShift);

        if (dto == null || volunteerShift.getRegistrations() == null) {
            return dto;
        }

        // Ajouter les inscriptions
        volunteerShift.getRegistrations().forEach(registration ->
                dto.getRegistrations().add(registrationMapper.toDTO(registration))
        );

        return dto;
    }

    public VolunteerShift toEntity(VolunteerShiftDTO dto) {
        if (dto == null) {
            return null;
        }

        VolunteerShift volunteerShift = new VolunteerShift();
        volunteerShift.setId(dto.getId());
        volunteerShift.setRoleType(dto.getRoleType());
        volunteerShift.setRequiredVolunteers(dto.getRequiredVolunteers());
        volunteerShift.setMinExperienceLevel(dto.getMinExperienceLevel());
        volunteerShift.setDescription(dto.getDescription());
        volunteerShift.setStartTime(dto.getStartTime());
        volunteerShift.setEndTime(dto.getEndTime());

        // Récupérer le créneau
        if (dto.getTimeSlotId() != null) {
            Optional<TimeSlot> timeSlot = timeSlotRepository.findById(dto.getTimeSlotId());
            timeSlot.ifPresent(volunteerShift::setTimeSlot);
        }

        return volunteerShift;
    }

    public void updateEntityFromDTO(VolunteerShiftDTO dto, VolunteerShift volunteerShift) {
        if (dto == null || volunteerShift == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getRoleType() != null) volunteerShift.setRoleType(dto.getRoleType());
        if (dto.getRequiredVolunteers() != null) volunteerShift.setRequiredVolunteers(dto.getRequiredVolunteers());
        if (dto.getMinExperienceLevel() != null) volunteerShift.setMinExperienceLevel(dto.getMinExperienceLevel());
        if (dto.getDescription() != null) volunteerShift.setDescription(dto.getDescription());
        if (dto.getStartTime() != null) volunteerShift.setStartTime(dto.getStartTime());
        if (dto.getEndTime() != null) volunteerShift.setEndTime(dto.getEndTime());

        // Mettre à jour le créneau
        if (dto.getTimeSlotId() != null) {
            Optional<TimeSlot> timeSlot = timeSlotRepository.findById(dto.getTimeSlotId());
            timeSlot.ifPresent(volunteerShift::setTimeSlot);
        }
    }
}
