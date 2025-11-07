package fr.cop1.studentpantrybackend.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.enums.UserStatus;
import jakarta.validation.constraints.*;

import lombok.*;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserDTO {
    private Long id;

    @NotBlank(message = "Email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;

    // Password uniquement pour la création/mise à jour
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Size(min = 6, message = "Le mot de passe doit contenir au moins 6 caractères")
    private String password;

    @NotBlank(message = "Prénom est obligatoire")
    @Size(min = 2, max = 50, message = "Le prénom doit être entre 2 et 50 caractères")
    private String firstName;

    @NotBlank(message = "Nom est obligatoire")
    @Size(min = 2, max = 50, message = "Le nom doit être entre 2 et 50 caractères")
    private String lastName;

    @Pattern(regexp = "^(\\+[0-9]{1,3})?[0-9]{9,15}$", message = "Format de téléphone invalide")
    private String phone;

    private UserRole role;
    private UserStatus status;
    private String school;
    private Boolean studentIdVerified;
    private LocalDateTime createdAt;
    private LocalDateTime lastLogin;

    // Champs calculés
    @JsonIgnore
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // Pour les statistiques (peut être omis dans les réponses normales)
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer reservationCount;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private Integer volunteerShiftsCompleted;
}