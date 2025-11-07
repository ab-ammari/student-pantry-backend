package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.*;
import fr.cop1.studentpantrybackend.entities.*;
import fr.cop1.studentpantrybackend.entities.Notification;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.enums.NotificationStatus;
import fr.cop1.studentpantrybackend.enums.NotificationType;
import fr.cop1.studentpantrybackend.enums.RegistrationStatus;
import fr.cop1.studentpantrybackend.enums.ReservationStatus;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.mappers.*;
import fr.cop1.studentpantrybackend.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EventRepository eventRepository;
    private final ReservationRepository reservationRepository;
    private final VolunteerRegistrationRepository volunteerRegistrationRepository;
    private final NotificationMapper notificationMapper;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public NotificationDTO findById(Long id) throws ResourceNotFoundException {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée avec l'id: " + id));
        return notificationMapper.toDTO(notification);
    }

    @Override
    public List<NotificationDTO> findAll() {
        return notificationRepository.findAll().stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<NotificationDTO> findAll(Pageable pageable) {
        return notificationRepository.findAll(pageable)
                .map(notificationMapper::toDTO);
    }

    @Override
    public NotificationDTO save(NotificationDTO notificationDTO) throws ResourceNotFoundException {
        // Vérifier si l'utilisateur existe
        if (notificationDTO.getUserId() != null) {
            userRepository.findById(notificationDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + notificationDTO.getUserId()));
        }

        // Convertir DTO en entité
        Notification notification = notificationMapper.toEntity(notificationDTO);

        // Définir le statut initial si non spécifié
        if (notification.getStatus() == null) {
            notification.setStatus(NotificationStatus.SENT);
        }

        // Définir l'horodatage
        if (notification.getSentAt() == null) {
            notification.setSentAt(LocalDateTime.now());
        }

        // Enregistrer la notification
        Notification savedNotification = notificationRepository.save(notification);

        return notificationMapper.toDTO(savedNotification);
    }

    @Override
    public NotificationDTO update(Long id, NotificationDTO notificationDTO) throws ResourceNotFoundException {
        Notification existingNotification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée avec l'id: " + id));

        // Mettre à jour avec les données du DTO
        notificationMapper.updateEntityFromDTO(notificationDTO, existingNotification);

        // Enregistrer les modifications
        Notification updatedNotification = notificationRepository.save(existingNotification);

        return notificationMapper.toDTO(updatedNotification);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée avec l'id: " + id));

        notificationRepository.delete(notification);
    }

    @Override
    public List<NotificationDTO> findByUserId(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return notificationRepository.findByUserOrderBySentAtDesc(user).stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<NotificationDTO> findUnreadByUserId(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return notificationRepository.findByUserAndStatusOrderBySentAtDesc(user, NotificationStatus.SENT).stream()
                .map(notificationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Long countUnreadByUserId(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return notificationRepository.countByUserAndStatus(user, NotificationStatus.SENT);
    }

    @Override
    public NotificationDTO markAsRead(Long id) throws ResourceNotFoundException {
        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Notification non trouvée avec l'id: " + id));

        // Marquer comme lue
        notification.markAsRead();

        // Enregistrer les modifications
        Notification updatedNotification = notificationRepository.save(notification);

        return notificationMapper.toDTO(updatedNotification);
    }

    @Override
    public void markAllAsReadForUser(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        notificationRepository.markAllAsRead(user);
    }

    @Override
    public NotificationDTO sendNotification(Long userId, NotificationType type, String content) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        // Créer la notification
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setType(type);
        notification.setStatus(NotificationStatus.SENT);
        notification.setContent(content);
        notification.setSentAt(LocalDateTime.now());

        // Enregistrer la notification
        Notification savedNotification = notificationRepository.save(notification);

        return notificationMapper.toDTO(savedNotification);
    }

    @Override
    public void notifyEventCancellation(Long eventId) throws ResourceNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + eventId));

        // Récupérer toutes les réservations pour cet événement
        List<Reservation> reservations = reservationRepository.findByEventId(eventId);

        // Récupérer toutes les inscriptions bénévoles pour cet événement
        List<VolunteerRegistration> volunteerRegistrations = volunteerRegistrationRepository.findByEventId(eventId);

        // Envoyer des notifications aux participants
        for (Reservation reservation : reservations) {
            if (reservation.getStatus() == ReservationStatus.COMFIRMED) {
                sendNotification(
                        reservation.getUser().getId(),
                        NotificationType.RESERVATION_CANCELLATION,
                        "L'événement " + event.getName() + " prévu le " +
                                event.getEventDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                                " a été annulé."
                );
            }
        }

        // Envoyer des notifications aux bénévoles
        for (VolunteerRegistration registration : volunteerRegistrations) {
            if (registration.getStatus() == RegistrationStatus.CONFIRMED) {
                sendNotification(
                        registration.getUser().getId(),
                        NotificationType.RESERVATION_CANCELLATION,
                        "L'événement bénévole " + event.getName() + " prévu le " +
                                event.getEventDate().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")) +
                                " a été annulé."
                );
            }
        }
    }

    @Override
    public void sendReservationReminders(Long eventId) throws ResourceNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + eventId));

        // Récupérer toutes les réservations confirmées pour cet événement
        List<Reservation> reservations = reservationRepository.findByEventId(eventId).stream()
                .filter(r -> r.getStatus() == ReservationStatus.COMFIRMED)
                .collect(Collectors.toList());

        // Envoyer des rappels aux participants
        for (Reservation reservation : reservations) {
            sendNotification(
                    reservation.getUser().getId(),
                    NotificationType.RESERVATION_REMINDER,
                    "Rappel : Votre réservation pour l'événement " + event.getName() + " est demain, le " +
                            reservation.getTimeSlot().getStartTime().format(DATE_TIME_FORMATTER) +
                            ". N'oubliez pas votre carte étudiante."
            );
        }
    }

    @Override
    public void sendVolunteerReminders(Long eventId) throws ResourceNotFoundException {
        Event event = eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + eventId));

        // Récupérer toutes les inscriptions bénévoles confirmées pour cet événement
        List<VolunteerRegistration> volunteerRegistrations = volunteerRegistrationRepository.findByEventId(eventId).stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED)
                .collect(Collectors.toList());

        // Envoyer des rappels aux bénévoles
        for (VolunteerRegistration registration : volunteerRegistrations) {
            sendNotification(
                    registration.getUser().getId(),
                    NotificationType.VOLUNTEER_REMINDER,
                    "Rappel : Vous êtes inscrit comme bénévole pour l'événement " + event.getName() +
                            " demain, le " + registration.getVolunteerShift().getTimeSlot().getStartTime().format(DATE_TIME_FORMATTER) +
                            ". Votre rôle : " + registration.getVolunteerShift().getRoleType() + "."
            );
        }
    }

    @Override
    public void sendAccountApprovalNotification(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        sendNotification(
                user.getId(),
                NotificationType.ACCOUNT_APPROVED,
                "Votre compte a été approuvé ! Vous pouvez maintenant utiliser pleinement les fonctionnalités de l'application."
        );
    }

    @Override
    public void sendAccountRejectionNotification(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        sendNotification(
                user.getId(),
                NotificationType.ACCOUNT_REJECTED,
                "Votre demande de compte a été rejetée. Veuillez contacter l'administrateur pour plus d'informations."
        );
    }

    @Override
    public Long countByStatus(NotificationStatus status) {
        return (long) notificationRepository.findByStatus(status).size();
    }

    @Override
    public Long countByType(NotificationType type) {
        return (long) notificationRepository.findByType(type).size();
    }
}
