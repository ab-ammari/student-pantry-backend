package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.cop1.studentpantrybackend.enums.EventStatus;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EventDTO {
    private Long id;

    @NotBlank(message = "Le nom de l'événement est obligatoire")
    @Size(min = 3, max = 100, message = "Le nom doit être entre 3 et 100 caractères")
    private String name;

    @Size(max = 1000, message = "La description ne peut pas dépasser 1000 caractères")
    private String description;

    @NotBlank(message = "Le lieu est obligatoire")
    private String location;

    @NotNull(message = "La date de l'événement est obligatoire")
    @FutureOrPresent(message = "La date doit être aujourd'hui ou dans le futur")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime eventDate;

    private EventStatus status;

    private Long createdById;
    private String createdByName;  // Nom complet du créateur (non stocké en base)

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Pour les listes imbriquées (optionnelles)
    private Set<TimeSlotDTO> timeSlots = new HashSet<>();

    // Champs calculés pour les statistiques
    private Integer totalCapacity;
    private Integer totalReservations;
    private Integer totalVolunteerShifts;
    private Integer filledVolunteerShifts;
}
