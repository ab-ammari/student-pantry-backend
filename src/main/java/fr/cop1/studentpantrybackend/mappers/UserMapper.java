package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.UserDTO;
import fr.cop1.studentpantrybackend.entities.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {

    //private final PasswordEncoder passwordEncoder;

//    public UserMapper(PasswordEncoder passwordEncoder) {
//        this.passwordEncoder = passwordEncoder;
//    }

    public UserDTO toDTO(User user) {
        if (user == null) {
            return null;
        }

        return UserDTO.builder()
                .id(user.getId())
                .email(user.getEmail())
                .firstName(user.getFirstName())
                .lastName(user.getLastName())
                .phone(user.getPhone())
                .role(user.getRole())
                .status(user.getStatus())
                .school(user.getSchool())
                .createdAt(user.getCreatedAt())
                .lastLogin(user.getLastLogin())
                .build();
    }

    public User toEntity(UserDTO dto) {
        if (dto == null) {
            return null;
        }

        User user = new User();
        user.setId(dto.getId());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getFirstName());
        user.setLastName(dto.getLastName());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole());
        user.setStatus(dto.getStatus());
        user.setSchool(dto.getSchool());
        user.setStudentIdVerified(dto.getStudentIdVerified());

        // Hasher le mot de passe uniquement s'il est fourni (création/modification)
//        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
//            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
//        }

        // we store password as plain text for now (to be fixed later)
        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
            user.setPasswordHash(dto.getPassword());
        }

        return user;
    }

    /**
     * Met à jour une entité existante avec les données d'un DTO
     * sans modifier les champs sensibles comme le mot de passe si non fourni
     */
    public void updateEntityFromDTO(UserDTO dto, User user) {
        if (dto == null || user == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getEmail() != null) user.setEmail(dto.getEmail());
        if (dto.getFirstName() != null) user.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) user.setLastName(dto.getLastName());
        if (dto.getPhone() != null) user.setPhone(dto.getPhone());
        if (dto.getRole() != null) user.setRole(dto.getRole());
        if (dto.getStatus() != null) user.setStatus(dto.getStatus());
        if (dto.getSchool() != null) user.setSchool(dto.getSchool());
        if (dto.getStudentIdVerified() != null) user.setStudentIdVerified(dto.getStudentIdVerified());

        // Mettre à jour le mot de passe uniquement s'il est fourni
//        if (dto.getPassword() != null && !dto.getPassword().isEmpty()) {
//            user.setPasswordHash(passwordEncoder.encode(dto.getPassword()));
//        }
    }
}
