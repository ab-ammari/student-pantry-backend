package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.cop1.studentpantrybackend.enums.ReservationStatus;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationDTO {
    private Long id;

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;
    private String userName; // Prénom et nom de l'utilisateur (non stocké en base)

    @NotNull(message = "L'ID du créneau est obligatoire")
    private Long timeSlotId;

    // Informations supplémentaires sur le créneau (non stockées en base)
    private LocalDateTime slotStartTime;
    private LocalDateTime slotEndTime;
    private String eventName;

    @NotNull(message = "L'ID du type de panier est obligatoire")
    private Long basketTypeId;
    private String basketTypeName; // Nom du type de panier (non stocké en base)

    private ReservationStatus status;

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

    public Boolean getIsCheckedIn() {
        return checkedInAt != null;
    }
}