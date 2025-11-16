package fr.cop1.studentpantrybackend.web.controllers;


import fr.cop1.studentpantrybackend.dtos.LoginRequest;
import fr.cop1.studentpantrybackend.dtos.LoginResponse;
import fr.cop1.studentpantrybackend.dtos.PasswordResetRequest;
import fr.cop1.studentpantrybackend.dtos.UserDTO;
import fr.cop1.studentpantrybackend.exceptions.EmailAlreadyExistsException;
import fr.cop1.studentpantrybackend.exceptions.InvalidCredentialsException;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.services.UserService;
import fr.cop1.studentpantrybackend.web.ApiConstants;
import fr.cop1.studentpantrybackend.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.Map;

/**
 * Contrôleur REST pour l'authentification
 */
@RestController
@RequestMapping(ApiConstants.AUTH_ENDPOINT)
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class AuthController {

    private final UserService userService;

    /**
     * Authentifier un utilisateur
     */
    @PostMapping(ApiConstants.LOGIN_PATH)
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginRequest) throws InvalidCredentialsException, ResourceNotFoundException {
        // Authentifier l'utilisateur
        UserDTO authenticatedUser = userService.login(
                loginRequest.getEmail(),
                loginRequest.getPassword());

        // Générer un token JWT (à implémenter avec Spring Security)
        String token = generateJwtToken(authenticatedUser);

        // Préparer la réponse
        LoginResponse response = new LoginResponse();
        response.setToken(token);
        response.setUser(authenticatedUser);

        return ResponseEntity.ok(ApiResponse.success("Authentification réussie", response));
    }

    /**
     * Enregistrer un nouvel utilisateur
     */
    @PostMapping(ApiConstants.REGISTER_PATH)
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> register(@Valid @RequestBody UserDTO userDTO) throws EmailAlreadyExistsException {
        UserDTO registeredUser = userService.register(userDTO);
        return new ResponseEntity<>(ApiResponse.success("Inscription réussie", registeredUser), HttpStatus.CREATED);
    }

    /**
     * Réinitialiser le mot de passe d'un utilisateur
     */
    @PostMapping(ApiConstants.RESET_PASSWORD_PATH)
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody PasswordResetRequest resetRequest) {
        userService.resetPassword(resetRequest.getEmail());
        return ResponseEntity.ok(ApiResponse.success("Un email de réinitialisation a été envoyé", null));
    }

    // Méthode temporaire pour générer un token JWT (à remplacer par une implémentation réelle)
    private String generateJwtToken(UserDTO user) {
        // Dans une implémentation réelle, vous utiliseriez une bibliothèque comme jjwt
        // et définiriez des claims, une signature, etc.

        // Ceci est juste un exemple temporaire
        return "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ";
    }
}