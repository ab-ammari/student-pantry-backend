package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.NotificationDTO;
import fr.cop1.studentpantrybackend.entities.Notification;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class NotificationMapper {

    private final UserRepository userRepository;

    public NotificationMapper(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public NotificationDTO toDTO(Notification notification) {
        if (notification == null) {
            return null;
        }

        NotificationDTO dto = NotificationDTO.builder()
                .id(notification.getId())
                .type(notification.getType())
                .status(notification.getStatus())
                .content(notification.getContent())
                .sentAt(notification.getSentAt())
                .readAt(notification.getReadAt())
                .build();

        // Ajouter les informations de l'utilisateur
        if (notification.getUser() != null) {
            dto.setUserId(notification.getUser().getId());
            dto.setUserName(notification.getUser().getFirstName() + " " + notification.getUser().getLastName());
        }

        return dto;
    }

    public Notification toEntity(NotificationDTO dto) {
        if (dto == null) {
            return null;
        }

        Notification notification = new Notification();
        notification.setId(dto.getId());
        notification.setType(dto.getType());
        notification.setStatus(dto.getStatus());
        notification.setContent(dto.getContent());
        notification.setSentAt(dto.getSentAt());
        notification.setReadAt(dto.getReadAt());

        // Récupérer l'utilisateur
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(notification::setUser);
        }

        return notification;
    }

    public void updateEntityFromDTO(NotificationDTO dto, Notification notification) {
        if (dto == null || notification == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getType() != null) notification.setType(dto.getType());
        if (dto.getStatus() != null) notification.setStatus(dto.getStatus());
        if (dto.getContent() != null) notification.setContent(dto.getContent());
        if (dto.getSentAt() != null) notification.setSentAt(dto.getSentAt());
        if (dto.getReadAt() != null) notification.setReadAt(dto.getReadAt());

        // Mettre à jour l'utilisateur
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(notification::setUser);
        }
    }
}