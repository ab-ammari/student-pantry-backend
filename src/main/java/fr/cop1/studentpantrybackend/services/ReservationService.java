package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.ReservationDTO;
import fr.cop1.studentpantrybackend.enums.ReservationStatus;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface ReservationService {
    // Opérations CRUD de base
    ReservationDTO findById(Long id) throws ResourceNotFoundException;
    List<ReservationDTO> findAll();
    Page<ReservationDTO> findAll(Pageable pageable);
    ReservationDTO save(ReservationDTO reservationDTO) throws ResourceNotFoundException;
    ReservationDTO update(Long id, ReservationDTO reservationDTO) throws ResourceNotFoundException;
    void delete(Long id) throws ResourceNotFoundException;

    // Méthodes spécifiques
    List<ReservationDTO> findByUserId(Long userId) throws ResourceNotFoundException;
    List<ReservationDTO> findByTimeSlotId(Long timeSlotId) throws ResourceNotFoundException;
    List<ReservationDTO> findByEventId(Long eventId);
    List<ReservationDTO> findByBasketTypeId(Long basketTypeId) throws ResourceNotFoundException;
    List<ReservationDTO> findByStatus(ReservationStatus status);
    List<ReservationDTO> findRecentReservations(int limit);

    // Méthodes de gestion du cycle de vie
    ReservationDTO createReservation(Long userId, Long timeSlotId, Long basketTypeId, String notes) throws ResourceNotFoundException;
    ReservationDTO cancelReservation(Long id, String reason) throws ResourceNotFoundException;
    ReservationDTO checkInReservation(Long id) throws ResourceNotFoundException;
    ReservationDTO markAsNoShow(Long id) throws ResourceNotFoundException;

    // Vérifications de disponibilité
    boolean isTimeSlotAvailable(Long timeSlotId) throws ResourceNotFoundException;
    boolean canUserReserve(Long userId, Long timeSlotId) throws ResourceNotFoundException;

    // Gestion des stocks
    void updateInventoryAfterCheckIn(Long reservationId) throws ResourceNotFoundException;

    // Statistiques
    Long countReservationsByStatus(ReservationStatus status);
    Long countReservationsByTimeSlot(Long timeSlotId) throws ResourceNotFoundException;
    Long countActiveReservationsByUser(Long userId) throws ResourceNotFoundException;
    Map<String, Long> getReservationStatsByBasketType();
    Map<String, Long> getReservationStatsByTimeSlot(Long eventId);
}