package fr.cop1.studentpantrybackend.web.controllers;

import fr.cop1.studentpantrybackend.dtos.UserDTO;
import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.exceptions.EmailAlreadyExistsException;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.services.UserService;
import fr.cop1.studentpantrybackend.web.ApiConstants;
import fr.cop1.studentpantrybackend.web.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des utilisateurs
 */
@RestController
@RequestMapping(ApiConstants.USERS_ENDPOINT)
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
public class UserController {

    private final UserService userService;

    /**
     * Récupérer tous les utilisateurs (avec pagination)
     */
    @GetMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<Page<UserDTO>>> getAllUsers(
            @RequestParam(value = ApiConstants.PAGE_PARAM, defaultValue = "0") int page,
            @RequestParam(value = ApiConstants.SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = ApiConstants.SORT_PARAM, defaultValue = "id") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(sortBy));
        Page<UserDTO> users = userService.findAll(pageable);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalPages", users.getTotalPages());
        meta.put("totalElements", users.getTotalElements());
        meta.put("size", users.getSize());
        meta.put("page", users.getNumber());

        return ResponseEntity.ok(ApiResponse.success("Liste des utilisateurs récupérée", users, meta));
    }

    /**
     * Récupérer un utilisateur par son ID
     */
    @GetMapping(ApiConstants.USER_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#id)")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> getUserById(@PathVariable Long id) throws ResourceNotFoundException {
        UserDTO user = userService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(user));
    }

    /**
     * Récupérer les utilisateurs par rôle
     */
    @GetMapping("/role/{role}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<List<UserDTO>>> getUsersByRole(@PathVariable UserRole role) {
        List<UserDTO> users = userService.findByRole(role);
        return ResponseEntity.ok(ApiResponse.success("Utilisateurs avec le rôle " + role, users));
    }

    /**
     * Créer un nouvel utilisateur (admin uniquement)
     */
    @PostMapping
    //@PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> createUser(@Valid @RequestBody UserDTO userDTO) throws EmailAlreadyExistsException {
        UserDTO createdUser = userService.save(userDTO);
        return new ResponseEntity<>(ApiResponse.success("Utilisateur créé avec succès", createdUser), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour un utilisateur existant
     */
    @PutMapping(ApiConstants.USER_ID_PATH)
    //@PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> updateUser(@PathVariable Long id, @Valid @RequestBody UserDTO userDTO) throws EmailAlreadyExistsException, ResourceNotFoundException {
        UserDTO updatedUser = userService.update(id, userDTO);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur mis à jour avec succès", updatedUser));
    }

    /**
     * Supprimer un utilisateur
     */
    @DeleteMapping(ApiConstants.USER_ID_PATH)
    //@PreAuthorize("hasRole('ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<Void>> deleteUser(@PathVariable Long id) throws ResourceNotFoundException {
        userService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé avec succès", null));
    }

    /**
     * Approuver un utilisateur
     */
    @PutMapping("/{id}/approve")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> approveUser(@PathVariable Long id) throws ResourceNotFoundException {
        UserDTO approvedUser = userService.approveUser(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur approuvé", approvedUser));
    }

    /**
     * Rejeter un utilisateur
     */
    @PutMapping("/{id}/reject")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> rejectUser(@PathVariable Long id) throws ResourceNotFoundException {
        UserDTO rejectedUser = userService.rejectUser(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur rejeté", rejectedUser));
    }

    /**
     * Bloquer un utilisateur
     */
    @PutMapping("/{id}/block")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> blockUser(@PathVariable Long id) throws ResourceNotFoundException {
        UserDTO blockedUser = userService.blockUser(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur bloqué", blockedUser));
    }

    /**
     * Activer un utilisateur
     */
    @PutMapping("/{id}/activate")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> activateUser(@PathVariable Long id) throws ResourceNotFoundException {
        UserDTO activatedUser = userService.activateUser(id);
        return ResponseEntity.ok(ApiResponse.success("Utilisateur activé", activatedUser));
    }

    /**
     * Vérifier l'identité étudiante d'un utilisateur
     */
    @PutMapping("/{id}/verify-student")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<UserDTO>> verifyStudentId(@PathVariable Long id) throws ResourceNotFoundException {
        UserDTO verifiedUser = userService.verifyStudentId(id);
        return ResponseEntity.ok(ApiResponse.success("Identité étudiante vérifiée", verifiedUser));
    }

    /**
     * Changer le mot de passe d'un utilisateur
     */
    @PutMapping("/{id}/change-password")
    //@PreAuthorize("hasRole('ADMIN') or @userSecurity.isCurrentUser(#id)")
    @CrossOrigin(origins = "http://localhost:4200", allowCredentials = "true")
    public ResponseEntity<ApiResponse<Void>> changePassword(
            @PathVariable Long id,
            @RequestParam String oldPassword,
            @RequestParam String newPassword) {

        userService.changePassword(id, oldPassword, newPassword);
        return ResponseEntity.ok(ApiResponse.success("Mot de passe changé avec succès", null));
    }
}
