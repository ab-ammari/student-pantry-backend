package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.cop1.studentpantrybackend.enums.RegistrationStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerRegistrationDTO {
    private Long id;

    @NotNull(message = "L'ID du poste bénévole est obligatoire")
    private Long volunteerShiftId;

    // Informations supplémentaires sur le poste (non stockées en base)
    private String roleType;
    private LocalDateTime eventDate;
    private String eventName;

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;
    private String userName; // Prénom et nom du bénévole (non stocké en base)

    private RegistrationStatus status;

    private Boolean isTeamLeader;

    @Size(max = 500, message = "Les notes ne peuvent pas dépasser 500 caractères")
    private String notes;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime checkedInAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Champs calculés pour l'UI
    private Boolean isCheckedIn;

}