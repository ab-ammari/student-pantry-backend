package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.UserDTO;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.enums.UserStatus;
import fr.cop1.studentpantrybackend.exceptions.EmailAlreadyExistsException;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.mappers.UserMapper;
import fr.cop1.studentpantrybackend.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class UserServiceImpl implements  UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final NotificationService notificationService;

    @Override
    public UserDTO findById(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> findAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<UserDTO> findAll(Pageable pageable) {
        return userRepository.findAll(pageable)
                .map(userMapper::toDTO);
    }

    @Override
    public UserDTO save(UserDTO userDTO) throws EmailAlreadyExistsException {
        // Vérifier si l'email est déjà utilisé
        if (userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("L'email est déjà utilisé: " + userDTO.getEmail());
        }

        // Convertir DTO en entité
        User user = userMapper.toEntity(userDTO);

        // Définir l'état initial si non spécifié
        if (user.getStatus() == null) {
            user.setStatus(UserStatus.PENDING);
        }

        // Définir la date de création
        user.setCreatedAt(LocalDateTime.now());

        // Enregistrer l'utilisateur
        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO update(Long id, UserDTO userDTO) throws ResourceNotFoundException, EmailAlreadyExistsException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        // Vérifier si l'email est modifié et s'il est déjà utilisé par un autre utilisateur
        if (!existingUser.getEmail().equals(userDTO.getEmail()) &&
                userRepository.existsByEmail(userDTO.getEmail())) {
            throw new EmailAlreadyExistsException("L'email est déjà utilisé: " + userDTO.getEmail());
        }

        // Mettre à jour l'utilisateur avec les données du DTO
        userMapper.updateEntityFromDTO(userDTO, existingUser);

        // Enregistrer les modifications
        User updatedUser = userRepository.save(existingUser);

        return userMapper.toDTO(updatedUser);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        // Option 1: Suppression physique
        userRepository.delete(user);

        // Option 2: Suppression logique (si préféré)
        // user.setStatus(UserStatus.DELETED);
        // userRepository.save(user);
    }

    @Override
    public UserDTO findByEmail(String email) throws ResourceNotFoundException {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));
        return userMapper.toDTO(user);
    }

    @Override
    public List<UserDTO> findByRole(UserRole role) {
        return userRepository.findByRole(role).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findByStatus(UserStatus status) {
        return userRepository.findByStatus(status).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserDTO> findStudentsBySchool(String school) {
        return userRepository.findByRoleAndSchool(UserRole.STUDENT, school).stream()
                .map(userMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public UserDTO register(UserDTO userDTO) throws EmailAlreadyExistsException {
        // Définir le statut initial
        userDTO.setStatus(UserStatus.PENDING);

        // Enregistrer l'utilisateur
        UserDTO savedUser = this.save(userDTO);

        // Envoyer une notification de confirmation d'inscription
        // notificationService.sendAccountRegistrationConfirmation(savedUser.getId());

        return savedUser;
    }

    @Override
    public UserDTO approveUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        // Envoyer une notification d'approbation
        notificationService.sendAccountApprovalNotification(id);

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO rejectUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        user.setStatus(UserStatus.REJECTED);
        User savedUser = userRepository.save(user);

        // Envoyer une notification de rejet
        notificationService.sendAccountRejectionNotification(id);

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO blockUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        user.setStatus(UserStatus.BLOCKED);
        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO activateUser(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO verifyStudentId(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        if (user.getRole() != UserRole.STUDENT) {
            throw new IllegalStateException("Seuls les comptes étudiants peuvent être vérifiés");
        }

        user.setStudentIdVerified(true);
        User savedUser = userRepository.save(user);

        return userMapper.toDTO(savedUser);
    }

    @Override
    public UserDTO login(String email, String password) throws ResourceNotFoundException {
        Optional<User> userOpt = userRepository.findByEmail(email);

        if (userOpt.isEmpty() || !passwordEncoder.matches(password, userOpt.get().getPasswordHash())) {
            throw new InvalidCredentialsException("Email ou mot de passe incorrect");
        }

        User user = userOpt.get();

        // Vérifier si le compte est actif
        if (user.getStatus() != UserStatus.ACTIVE) {
            throw new InvalidCredentialsException("Compte non actif. Statut: " + user.getStatus());
        }

        // Mettre à jour la dernière connexion
        updateLastLogin(user.getId());

        return userMapper.toDTO(user);
    }

    @Override
    public void updateLastLogin(Long id) throws ResourceNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        user.setLastLogin(LocalDateTime.now());
        userRepository.save(user);
    }

    @Override
    public void changePassword(Long id, String oldPassword, String newPassword) {

    }

    @Override
    public void resetPassword(String email) {

    }

    /*@Override
    public void changePassword(Long id, String oldPassword, String newPassword) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + id));

        // Vérifier l'ancien mot de passe
        if (!passwordEncoder.matches(oldPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Ancien mot de passe incorrect");
        }

        // Mettre à jour avec le nouveau mot de passe
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }*/

    /*@Override
    public void resetPassword(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'email: " + email));

        // Logique pour générer un mot de passe temporaire ou un token de réinitialisation
        // Puis envoi d'un email ou d'une notification
        // ...

        // Exemple simplifié:
        String temporaryPassword = generateRandomPassword();
        user.setPasswordHash(passwordEncoder.encode(temporaryPassword));
        userRepository.save(user);

        // Envoyer le mot de passe temporaire par email
        // emailService.sendPasswordResetEmail(user.getEmail(), temporaryPassword);
    }*/

    @Override
    public Long countByRole(UserRole role) {
        return userRepository.findByRole(role).stream().count();
    }

    @Override
    public Long countActiveUsers() {
        return userRepository.findByStatus(UserStatus.ACTIVE).stream().count();
    }

    @Override
    public Long countPendingUsers() {
        return userRepository.findByStatus(UserStatus.PENDING).stream().count();
    }

    // Méthode utilitaire pour générer un mot de passe aléatoire
    private String generateRandomPassword() {
        // Implémentation simple pour l'exemple
        return "temp" + (int) (Math.random() * 10000);
    }
}
