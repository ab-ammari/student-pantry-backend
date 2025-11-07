package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeSlotDTO {
    private Long id;

    private Long eventId;
    private String eventName;  // Information supplémentaire sur l'événement

    @NotNull(message = "L'heure de début est obligatoire")
    @FutureOrPresent(message = "L'heure de début doit être dans le futur")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime startTime;

    @NotNull(message = "L'heure de fin est obligatoire")
    @FutureOrPresent(message = "L'heure de fin doit être dans le futur")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime endTime;

    @NotNull(message = "La capacité maximale est obligatoire")
    @Min(value = 1, message = "La capacité doit être d'au moins 1")
    private Integer maxCapacity;

    private Integer availableSpots;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime updatedAt;

    // Pour les listes imbriquées (optionnelles, selon les besoins de l'API)
    private List<ReservationDTO> reservations = new ArrayList<>();
    private List<VolunteerShiftDTO> volunteerShifts = new ArrayList<>();

    // Champs calculés pour l'UI
    private Boolean isFull;
    private Integer reservationCount;
    private Integer volunteerCount;

    // Méthode utilitaire
    public Boolean getIsFull() {
        return availableSpots != null && availableSpots <= 0;
    }

    public Integer getReservationCount() {
        return maxCapacity - (availableSpots != null ? availableSpots : 0);
    }
}