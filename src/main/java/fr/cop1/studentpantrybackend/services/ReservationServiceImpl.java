package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.ReservationDTO;
import fr.cop1.studentpantrybackend.entities.BasketType;
import fr.cop1.studentpantrybackend.entities.Reservation;
import fr.cop1.studentpantrybackend.entities.TimeSlot;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.enums.NotificationType;
import fr.cop1.studentpantrybackend.enums.ReservationStatus;
import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.mappers.ReservationMapper;
import fr.cop1.studentpantrybackend.repositories.*;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ReservationServiceImpl implements ReservationService {
    private final ReservationRepository reservationRepository;
    private final UserRepository userRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BasketTypeRepository basketTypeRepository;
    private final ReservationMapper reservationMapper;
    private final NotificationService notificationService;
    private final InventoryService inventoryService;

    @Override
    public ReservationDTO findById(Long id) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + id));
        return reservationMapper.toDTO(reservation);
    }

    @Override
    public List<ReservationDTO> findAll() {
        return reservationRepository.findAll().stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<ReservationDTO> findAll(Pageable pageable) {
        return reservationRepository.findAll(pageable)
                .map(reservationMapper::toDTO);
    }

    @Override
    public ReservationDTO save(ReservationDTO reservationDTO) throws ResourceNotFoundException {
        // Vérifier les données
        validateReservation(reservationDTO);

        // Convertir DTO en entité
        Reservation reservation = reservationMapper.toEntity(reservationDTO);

        // Définir le statut initial si non spécifié
        if (reservation.getStatus() == null) {
            reservation.setStatus(ReservationStatus.COMFIRMED);
        }

        // Définir les horodatages
        LocalDateTime now = LocalDateTime.now();
        reservation.setCreatedAt(now);
        reservation.setUpdatedAt(now);

        // Mettre à jour les places disponibles dans le créneau
        updateTimeSlotAvailability(reservation.getTimeSlot(), 1);

        // Enregistrer la réservation
        Reservation savedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO update(Long id, ReservationDTO reservationDTO) throws ResourceNotFoundException {
        Reservation existingReservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + id));

        // Vérifier si la réservation peut être modifiée
        if (existingReservation.getStatus() == ReservationStatus.CHECKED_IN) {
            throw new ValidationException("Impossible de modifier une réservation déjà utilisée");
        }

        // Vérifier les données
        validateReservation(reservationDTO);

        // Vérifier si le créneau est modifié
        boolean timeSlotChanged = reservationDTO.getTimeSlotId() != null &&
                !existingReservation.getTimeSlot().getId().equals(reservationDTO.getTimeSlotId());

        if (timeSlotChanged) {
            // Restaurer une place dans l'ancien créneau
            updateTimeSlotAvailability(existingReservation.getTimeSlot(), -1);

            // Vérifier la disponibilité du nouveau créneau
            TimeSlot newTimeSlot = timeSlotRepository.findById(reservationDTO.getTimeSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + reservationDTO.getTimeSlotId()));

            if (newTimeSlot.getAvailableSpots() <= 0) {
                throw new ValidationException("Plus de place disponible dans ce créneau");
            }

            // Réserver une place dans le nouveau créneau
            updateTimeSlotAvailability(newTimeSlot, 1);
        }

        // Mettre à jour la réservation avec les données du DTO
        reservationMapper.updateEntityFromDTO(reservationDTO, existingReservation);

        // Mettre à jour l'horodatage
        existingReservation.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        Reservation updatedReservation = reservationRepository.save(existingReservation);

        return reservationMapper.toDTO(updatedReservation);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + id));

        // Vérifier si la réservation peut être supprimée
        if (reservation.getStatus() == ReservationStatus.CHECKED_IN) {
            throw new ValidationException("Impossible de supprimer une réservation déjà utilisée");
        }

        // Restaurer une place dans le créneau si la réservation était confirmée
        if (reservation.getStatus() == ReservationStatus.COMFIRMED) {
            updateTimeSlotAvailability(reservation.getTimeSlot(), -1);
        }

        reservationRepository.delete(reservation);
    }

    @Override
    public List<ReservationDTO> findByUserId(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return reservationRepository.findByUser(user).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> findByTimeSlotId(Long timeSlotId) throws ResourceNotFoundException {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        return reservationRepository.findByTimeSlot(timeSlot).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> findByEventId(Long eventId) {
        return reservationRepository.findByEventId(eventId).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> findByBasketTypeId(Long basketTypeId) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(basketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + basketTypeId));

        return reservationRepository.findByBasketType(basketType).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> findByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status).stream()
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<ReservationDTO> findRecentReservations(int limit) {
        LocalDateTime recent = LocalDateTime.now().minusDays(7);
        return reservationRepository.findByCreatedAtAfterOrderByCreatedAtDesc(recent).stream()
                .limit(limit)
                .map(reservationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ReservationDTO createReservation(Long userId, Long timeSlotId, Long basketTypeId, String notes) throws ResourceNotFoundException {
        // Récupérer les entités
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        BasketType basketType = basketTypeRepository.findById(basketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + basketTypeId));

        // Vérifier si l'utilisateur peut réserver
        if (user.getRole() != UserRole.STUDENT) {
            throw new ValidationException("Seuls les étudiants peuvent effectuer des réservations");
        }

        if (!user.isStudentIdVerified()) {
            throw new ValidationException("Votre identité étudiante doit être vérifiée avant de réserver");
        }

        // Vérifier s'il y a des places disponibles
        if (timeSlot.getAvailableSpots() <= 0) {
            throw new ValidationException("Plus de place disponible dans ce créneau");
        }

        // Vérifier si l'utilisateur n'a pas déjà réservé ce créneau
        if (reservationRepository.findByUserAndTimeSlot(user, timeSlot).isPresent()) {
            throw new ValidationException("Vous avez déjà une réservation pour ce créneau");
        }

        // Créer la réservation
        Reservation reservation = new Reservation();
        reservation.setUser(user);
        reservation.setTimeSlot(timeSlot);
        reservation.setBasketType(basketType);
        reservation.setNotes(notes);
        reservation.setStatus(ReservationStatus.COMFIRMED);
        reservation.setCreatedAt(LocalDateTime.now());
        reservation.setUpdatedAt(LocalDateTime.now());

        // Mettre à jour les places disponibles
        updateTimeSlotAvailability(timeSlot, 1);

        // Enregistrer la réservation
        Reservation savedReservation = reservationRepository.save(reservation);

        // Envoyer une notification de confirmation
        notificationService.sendNotification(
                user.getId(),
                NotificationType.RESERVATION_CONFIRMATION,
                "Votre réservation pour l'événement " + timeSlot.getEvent().getName() +
                        " le " + timeSlot.getStartTime() + " a été confirmée.");

        return reservationMapper.toDTO(savedReservation);
    }

    @Override
    public ReservationDTO cancelReservation(Long id, String reason) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + id));

        // Vérifier si la réservation peut être annulée
        if (reservation.getStatus() != ReservationStatus.COMFIRMED) {
            throw new ValidationException("Seules les réservations confirmées peuvent être annulées");
        }

        // Vérifier si l'événement a déjà commencé
        if (reservation.getTimeSlot().getStartTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Impossible d'annuler une réservation pour un événement déjà commencé");
        }

        // Mettre à jour la réservation
        reservation.setStatus(ReservationStatus.CANCELLED);
        reservation.setNotes(reason);
        reservation.setUpdatedAt(LocalDateTime.now());

        // Restaurer une place dans le créneau
        updateTimeSlotAvailability(reservation.getTimeSlot(), -1);

        // Enregistrer les modifications
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Envoyer une notification d'annulation
        notificationService.sendNotification(
                reservation.getUser().getId(),
                NotificationType.RESERVATION_CANCELLATION,
                "Votre réservation pour l'événement " + reservation.getTimeSlot().getEvent().getName() +
                        " le " + reservation.getTimeSlot().getStartTime() + " a été annulée.");

        return reservationMapper.toDTO(updatedReservation);
    }

    @Override
    public ReservationDTO checkInReservation(Long id) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + id));

        // Vérifier si la réservation peut être utilisée
        if (reservation.getStatus() != ReservationStatus.COMFIRMED) {
            throw new ValidationException("Seules les réservations confirmées peuvent être utilisées");
        }

        // Marquer la réservation comme utilisée
        reservation.checkIn();

        // Enregistrer les modifications
        Reservation updatedReservation = reservationRepository.save(reservation);

        // Mettre à jour l'inventaire
        updateInventoryAfterCheckIn(id);

        return reservationMapper.toDTO(updatedReservation);
    }

    @Override
    public ReservationDTO markAsNoShow(Long id) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + id));

        // Vérifier si la réservation peut être marquée comme no-show
        if (reservation.getStatus() != ReservationStatus.COMFIRMED) {
            throw new ValidationException("Seules les réservations confirmées peuvent être marquées comme non présentées");
        }

        // Vérifier si l'événement est passé
        if (reservation.getTimeSlot().getEndTime().isAfter(LocalDateTime.now())) {
            throw new ValidationException("Impossible de marquer comme non présentée une réservation pour un événement non terminé");
        }

        // Marquer la réservation comme no-show
        reservation.markAsNoShow();

        // Enregistrer les modifications
        Reservation updatedReservation = reservationRepository.save(reservation);

        return reservationMapper.toDTO(updatedReservation);
    }

    @Override
    public boolean isTimeSlotAvailable(Long timeSlotId) throws ResourceNotFoundException {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        return timeSlot.getAvailableSpots() > 0;
    }

    @Override
    public boolean canUserReserve(Long userId, Long timeSlotId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        // Vérifier si l'utilisateur est un étudiant
        if (user.getRole() != UserRole.STUDENT) {
            return false;
        }

        // Vérifier si l'identité étudiante est vérifiée
        if (!user.isStudentIdVerified()) {
            return false;
        }

        // Vérifier s'il y a des places disponibles
        if (timeSlot.getAvailableSpots() <= 0) {
            return false;
        }

        // Vérifier si l'utilisateur n'a pas déjà réservé ce créneau
        return reservationRepository.findByUserAndTimeSlot(user, timeSlot).isEmpty();
    }

    @Override
    public void updateInventoryAfterCheckIn(Long reservationId) throws ResourceNotFoundException {
        Reservation reservation = reservationRepository.findById(reservationId)
                .orElseThrow(() -> new ResourceNotFoundException("Réservation non trouvée avec l'id: " + reservationId));

        // Décrémenter les stocks pour le type de panier
        inventoryService.decrementStockForBasketType(reservation.getBasketType().getId());
    }

    @Override
    public Long countReservationsByStatus(ReservationStatus status) {
        return (long) reservationRepository.findByStatus(status).size();
    }

    @Override
    public Long countReservationsByTimeSlot(Long timeSlotId) throws ResourceNotFoundException {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        return (long) reservationRepository.findByTimeSlot(timeSlot).size();
    }

    @Override
    public Long countActiveReservationsByUser(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return (long) reservationRepository.findByUserAndStatus(user, ReservationStatus.COMFIRMED).size();
    }

    @Override
    public Map<String, Long> getReservationStatsByBasketType() {
        Map<String, Long> stats = new HashMap<>();

        List<BasketType> basketTypes = basketTypeRepository.findAll();
        for (BasketType basketType : basketTypes) {
            Long count = (long) reservationRepository.findByBasketType(basketType).size();
            stats.put(basketType.getName(), count);
        }

        return stats;
    }

    @Override
    public Map<String, Long> getReservationStatsByTimeSlot(Long eventId) {
        Map<String, Long> stats = new HashMap<>();

        List<TimeSlot> timeSlots = timeSlotRepository.findByEventId(eventId);
        for (TimeSlot timeSlot : timeSlots) {
            Long count = (long) reservationRepository.findByTimeSlot(timeSlot).size();
            stats.put(timeSlot.getStartTime().toString(), count);
        }

        return stats;
    }

    // Méthodes utilitaires

    private void validateReservation(ReservationDTO reservationDTO) throws ResourceNotFoundException {
        // Vérifier si les entités référencées existent
        if (reservationDTO.getUserId() != null) {
            userRepository.findById(reservationDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + reservationDTO.getUserId()));
        }

        if (reservationDTO.getTimeSlotId() != null) {
            timeSlotRepository.findById(reservationDTO.getTimeSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + reservationDTO.getTimeSlotId()));
        }

        if (reservationDTO.getBasketTypeId() != null) {
            basketTypeRepository.findById(reservationDTO.getBasketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + reservationDTO.getBasketTypeId()));
        }
    }

    private void updateTimeSlotAvailability(TimeSlot timeSlot, int increment) {
        if (increment > 0) {
            // Réduire le nombre de places disponibles
            if (timeSlot.getAvailableSpots() <= 0) {
                throw new ValidationException("Plus de place disponible dans ce créneau");
            }
            timeSlot.setAvailableSpots(timeSlot.getAvailableSpots() - increment);
        } else {
            // Augmenter le nombre de places disponibles
            timeSlot.setAvailableSpots(timeSlot.getAvailableSpots() - increment); // increment est négatif
        }

        timeSlotRepository.save(timeSlot);
    }
}