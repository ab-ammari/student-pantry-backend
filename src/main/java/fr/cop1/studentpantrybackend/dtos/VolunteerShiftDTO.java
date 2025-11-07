package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.*;
import fr.cop1.studentpantrybackend.enums.ExperienceLevel;
import fr.cop1.studentpantrybackend.enums.RoleType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerShiftDTO {
    private Long id;

    @NotNull(message = "L'ID du créneau est obligatoire")
    private Long timeSlotId;

    // Informations supplémentaires sur le créneau (non stockées en base)
    private LocalDateTime slotStartTime;
    private LocalDateTime slotEndTime;
    private String eventName;

    @NotNull(message = "Le type de rôle est obligatoire")
    private RoleType roleType;

    @NotNull(message = "Le nombre de bénévoles requis est obligatoire")
    @Min(value = 1, message = "Au moins un bénévole est requis")
    private Integer requiredVolunteers;

    private ExperienceLevel minExperienceLevel;

    @Size(max = 500, message = "La description ne peut pas dépasser 500 caractères")
    private String description;

    // Horaires spécifiques au poste bénévole (peuvent différer du créneau)
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Liste des inscriptions (optionnelle)
    private List<VolunteerRegistrationDTO> registrations = new ArrayList<>();

    // Champs calculés pour l'UI
    private Integer availableSpots;
    private Boolean isFull;
}
