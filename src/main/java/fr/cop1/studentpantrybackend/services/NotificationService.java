package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.NotificationDTO;
import fr.cop1.studentpantrybackend.enums.NotificationStatus;
import fr.cop1.studentpantrybackend.enums.NotificationType;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface NotificationService {

    // Opérations CRUD de base
    NotificationDTO findById(Long id) throws ResourceNotFoundException;
    List<NotificationDTO> findAll();
    Page<NotificationDTO> findAll(Pageable pageable);
    NotificationDTO save(NotificationDTO notificationDTO) throws ResourceNotFoundException;
    NotificationDTO update(Long id, NotificationDTO notificationDTO) throws ResourceNotFoundException;
    void delete(Long id) throws ResourceNotFoundException;

    // Méthodes spécifiques
    List<NotificationDTO> findByUserId(Long userId) throws ResourceNotFoundException;
    List<NotificationDTO> findUnreadByUserId(Long userId) throws ResourceNotFoundException;
    Long countUnreadByUserId(Long userId) throws ResourceNotFoundException;

    // Méthodes de gestion du cycle de vie
    NotificationDTO markAsRead(Long id) throws ResourceNotFoundException;
    void markAllAsReadForUser(Long userId) throws ResourceNotFoundException;

    // Méthodes d'envoi de notifications
    NotificationDTO sendNotification(Long userId, NotificationType type, String content) throws ResourceNotFoundException;

    // Notifications spécifiques (méthodes d'aide)
    void notifyEventCancellation(Long eventId) throws ResourceNotFoundException;
    void sendReservationReminders(Long eventId) throws ResourceNotFoundException;
    void sendVolunteerReminders(Long eventId) throws ResourceNotFoundException;
    void sendAccountApprovalNotification(Long userId) throws ResourceNotFoundException;
    void sendAccountRejectionNotification(Long userId) throws ResourceNotFoundException;

    // Statistiques
    Long countByStatus(NotificationStatus status);
    Long countByType(NotificationType type);
}
