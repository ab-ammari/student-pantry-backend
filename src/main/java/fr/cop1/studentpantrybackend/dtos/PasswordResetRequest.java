package fr.cop1.studentpantrybackend.dtos;


import lombok.Data;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * DTO pour une demande de réinitialisation de mot de passe
 */
@Data
public class PasswordResetRequest {

    @NotBlank(message = "L'email est obligatoire")
    @Email(message = "Format d'email invalide")
    private String email;
}