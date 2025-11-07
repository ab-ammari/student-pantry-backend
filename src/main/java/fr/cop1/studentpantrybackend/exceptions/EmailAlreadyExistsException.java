package fr.cop1.studentpantrybackend.exceptions;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public class EmailAlreadyExistsException extends Exception {
    public EmailAlreadyExistsException(@NotBlank(message = "Email est obligatoire") @Email(message = "Format d'email invalide") String s) {
    }
}
