package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VolunteerAvailabilityDTO {
    private Long id;

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;
    private String userName; // Prénom et nom du bénévole (non stocké en base)

    @NotNull(message = "Le jour de la semaine est obligatoire")
    @Min(value = 1, message = "Le jour doit être entre 1 (Lundi) et 7 (Dimanche)")
    @Max(value = 7, message = "Le jour doit être entre 1 (Lundi) et 7 (Dimanche)")
    private Integer dayOfWeek;

    @NotNull(message = "L'heure de début est obligatoire")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime startTime;

    @NotNull(message = "L'heure de fin est obligatoire")
    @JsonFormat(pattern = "HH:mm:ss")
    private LocalTime endTime;

    private Boolean isActive;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    // Champs calculés pour l'UI
    private String dayName;

    public String getDayName() {
        switch (dayOfWeek) {
            case 1: return "Lundi";
            case 2: return "Mardi";
            case 3: return "Mercredi";
            case 4: return "Jeudi";
            case 5: return "Vendredi";
            case 6: return "Samedi";
            case 7: return "Dimanche";
            default: return "Inconnu";
        }
    }
}
