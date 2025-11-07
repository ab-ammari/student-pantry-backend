package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.UserDTO;
import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.enums.UserStatus;
import fr.cop1.studentpantrybackend.exceptions.EmailAlreadyExistsException;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserService {

    // Opérations CRUD de base
    UserDTO findById(Long id) throws ResourceNotFoundException;
    List<UserDTO> findAll();
    Page<UserDTO> findAll(Pageable pageable);
    UserDTO save(UserDTO userDTO) throws EmailAlreadyExistsException;
    UserDTO update(Long id, UserDTO userDTO) throws ResourceNotFoundException, EmailAlreadyExistsException;
    void delete(Long id) throws ResourceNotFoundException;

    // Méthodes spécifiques
    UserDTO findByEmail(String email) throws ResourceNotFoundException;
    List<UserDTO> findByRole(UserRole role);
    List<UserDTO> findByStatus(UserStatus status);
    List<UserDTO> findStudentsBySchool(String school);

    // Méthodes de gestion
    UserDTO register(UserDTO userDTO) throws EmailAlreadyExistsException;
    UserDTO approveUser(Long id) throws ResourceNotFoundException;
    UserDTO rejectUser(Long id) throws ResourceNotFoundException;
    UserDTO blockUser(Long id) throws ResourceNotFoundException;
    UserDTO activateUser(Long id) throws ResourceNotFoundException;
    UserDTO verifyStudentId(Long id) throws ResourceNotFoundException;

    // Méthodes d'authentification
    UserDTO login(String email, String password);
    void updateLastLogin(Long id) throws ResourceNotFoundException;
    void changePassword(Long id, String oldPassword, String newPassword);
    void resetPassword(String email);

    // Statistiques
    Long countByRole(UserRole role);
    Long countActiveUsers();
    Long countPendingUsers();
}
