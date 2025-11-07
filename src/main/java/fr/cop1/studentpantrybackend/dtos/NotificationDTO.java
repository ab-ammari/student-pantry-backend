package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonFormat;
import fr.cop1.studentpantrybackend.enums.NotificationStatus;
import fr.cop1.studentpantrybackend.enums.NotificationType;
import jakarta.validation.constraints.*;
import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationDTO {
    private Long id;

    @NotNull(message = "L'ID utilisateur est obligatoire")
    private Long userId;
    private String userName; // Prénom et nom de l'utilisateur (non stocké en base)

    @NotNull(message = "Le type de notification est obligatoire")
    private NotificationType type;

    private NotificationStatus status;

    @NotBlank(message = "Le contenu est obligatoire")
    @Size(min = 1, max = 1000, message = "Le contenu doit être entre 1 et 1000 caractères")
    private String content;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime sentAt;

    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime readAt;

    // Champs calculés pour l'UI
    private Boolean isRead;

    public Boolean getIsRead() {
        return status == NotificationStatus.READ || readAt != null;
    }
}