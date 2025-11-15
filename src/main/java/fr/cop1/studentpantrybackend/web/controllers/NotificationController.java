package fr.cop1.studentpantrybackend.web.controllers;


import fr.cop1.studentpantrybackend.dtos.NotificationDTO;
import fr.cop1.studentpantrybackend.enums.NotificationStatus;
import fr.cop1.studentpantrybackend.enums.NotificationType;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.services.NotificationService;
import fr.cop1.studentpantrybackend.web.ApiConstants;
import fr.cop1.studentpantrybackend.web.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contrôleur REST pour la gestion des notifications
 */
@RestController
@RequestMapping(ApiConstants.NOTIFICATIONS_ENDPOINT)
@RequiredArgsConstructor
@Validated
public class NotificationController {

    private final NotificationService notificationService;

    /**
     * Récupérer toutes les notifications (avec pagination)
     */
    @GetMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Page<NotificationDTO>>> getAllNotifications(
            @RequestParam(value = ApiConstants.PAGE_PARAM, defaultValue = "0") int page,
            @RequestParam(value = ApiConstants.SIZE_PARAM, defaultValue = "20") int size,
            @RequestParam(value = ApiConstants.SORT_PARAM, defaultValue = "sentAt") String sortBy) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, sortBy));
        Page<NotificationDTO> notifications = notificationService.findAll(pageable);

        Map<String, Object> meta = new HashMap<>();
        meta.put("totalPages", notifications.getTotalPages());
        meta.put("totalElements", notifications.getTotalElements());
        meta.put("size", notifications.getSize());
        meta.put("page", notifications.getNumber());

        return ResponseEntity.ok(ApiResponse.success("Liste des notifications récupérée", notifications, meta));
    }

    /**
     * Récupérer une notification par son ID
     */
    @GetMapping(ApiConstants.NOTIFICATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @notificationSecurity.isRecipient(#id)")
    public ResponseEntity<ApiResponse<NotificationDTO>> getNotificationById(@PathVariable Long id) throws ResourceNotFoundException {
        NotificationDTO notification = notificationService.findById(id);
        return ResponseEntity.ok(ApiResponse.success(notification));
    }

    /**
     * Récupérer les notifications d'un utilisateur
     */
    @GetMapping("/user/{userId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getNotificationsByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        List<NotificationDTO> notifications = notificationService.findByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Notifications de l'utilisateur", notifications));
    }

    /**
     * Récupérer les notifications non lues d'un utilisateur
     */
    @GetMapping("/unread/user/{userId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<List<NotificationDTO>>> getUnreadNotificationsByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        List<NotificationDTO> notifications = notificationService.findUnreadByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Notifications non lues de l'utilisateur", notifications));
    }

    /**
     * Compter les notifications non lues d'un utilisateur
     */
    @GetMapping("/unread/count/{userId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<Long>> countUnreadNotificationsByUserId(@PathVariable Long userId) throws ResourceNotFoundException {
        Long count = notificationService.countUnreadByUserId(userId);
        return ResponseEntity.ok(ApiResponse.success("Nombre de notifications non lues", count));
    }

    /**
     * Créer une nouvelle notification
     */
    @PostMapping
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDTO>> createNotification(@Valid @RequestBody NotificationDTO notificationDTO) throws ResourceNotFoundException {
        NotificationDTO createdNotification = notificationService.save(notificationDTO);
        return new ResponseEntity<>(ApiResponse.success("Notification créée avec succès", createdNotification), HttpStatus.CREATED);
    }

    /**
     * Envoyer une notification à un utilisateur
     */
    @PostMapping("/send")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDTO>> sendNotification(
            @RequestParam Long userId,
            @RequestParam NotificationType type,
            @RequestParam String content) throws ResourceNotFoundException {

        NotificationDTO sentNotification = notificationService.sendNotification(userId, type, content);
        return new ResponseEntity<>(ApiResponse.success("Notification envoyée avec succès", sentNotification), HttpStatus.CREATED);
    }

    /**
     * Mettre à jour une notification existante
     */
    @PutMapping(ApiConstants.NOTIFICATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<NotificationDTO>> updateNotification(
            @PathVariable Long id,
            @Valid @RequestBody NotificationDTO notificationDTO) throws ResourceNotFoundException {

        NotificationDTO updatedNotification = notificationService.update(id, notificationDTO);
        return ResponseEntity.ok(ApiResponse.success("Notification mise à jour avec succès", updatedNotification));
    }

    /**
     * Supprimer une notification
     */
    @DeleteMapping(ApiConstants.NOTIFICATION_ID_PATH)
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @notificationSecurity.isRecipient(#id)")
    public ResponseEntity<ApiResponse<Void>> deleteNotification(@PathVariable Long id) throws ResourceNotFoundException {
        notificationService.delete(id);
        return ResponseEntity.ok(ApiResponse.success("Notification supprimée avec succès", null));
    }

    /**
     * Marquer une notification comme lue
     */
    @PutMapping("/{id}/mark-read")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @notificationSecurity.isRecipient(#id)")
    public ResponseEntity<ApiResponse<NotificationDTO>> markNotificationAsRead(@PathVariable Long id) throws ResourceNotFoundException {
        NotificationDTO readNotification = notificationService.markAsRead(id);
        return ResponseEntity.ok(ApiResponse.success("Notification marquée comme lue", readNotification));
    }

    /**
     * Marquer toutes les notifications d'un utilisateur comme lues
     */
    @PutMapping("/mark-all-read/{userId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN') or @userSecurity.isCurrentUser(#userId)")
    public ResponseEntity<ApiResponse<Void>> markAllNotificationsAsRead(@PathVariable Long userId) throws ResourceNotFoundException {
        notificationService.markAllAsReadForUser(userId);
        return ResponseEntity.ok(ApiResponse.success("Toutes les notifications marquées comme lues", null));
    }

    /**
     * Envoyer des rappels pour un événement
     */
    @PostMapping("/send-reminders/event/{eventId}")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Void>> sendEventReminders(@PathVariable Long eventId) throws ResourceNotFoundException {
        // Envoyer des rappels aux participants
        notificationService.sendReservationReminders(eventId);

        // Envoyer des rappels aux bénévoles
        notificationService.sendVolunteerReminders(eventId);

        return ResponseEntity.ok(ApiResponse.success("Rappels envoyés pour l'événement", null));
    }

    /**
     * Obtenir des statistiques sur les notifications
     */
    @GetMapping("/stats")
    //@PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<ApiResponse<Map<String, Object>>> getNotificationStats() {
        Map<String, Object> stats = new HashMap<>();

        // Nombre de notifications par statut
        stats.put("pending", notificationService.countByStatus(NotificationStatus.PENDING));
        stats.put("sent", notificationService.countByStatus(NotificationStatus.SENT));
        stats.put("read", notificationService.countByStatus(NotificationStatus.READ));
        stats.put("failed", notificationService.countByStatus(NotificationStatus.FAILED));

        return ResponseEntity.ok(ApiResponse.success("Statistiques des notifications", stats));
    }
}