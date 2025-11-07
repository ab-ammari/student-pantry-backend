package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.ReservationDTO;
import fr.cop1.studentpantrybackend.entities.BasketType;
import fr.cop1.studentpantrybackend.entities.Reservation;
import fr.cop1.studentpantrybackend.entities.TimeSlot;
import fr.cop1.studentpantrybackend.entities.User;
import fr.cop1.studentpantrybackend.repositories.BasketTypeRepository;
import fr.cop1.studentpantrybackend.repositories.TimeSlotRepository;
import fr.cop1.studentpantrybackend.repositories.UserRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class ReservationMapper {

    private final UserRepository userRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final BasketTypeRepository basketTypeRepository;

    public ReservationMapper(UserRepository userRepository, TimeSlotRepository timeSlotRepository,
                             BasketTypeRepository basketTypeRepository) {
        this.userRepository = userRepository;
        this.timeSlotRepository = timeSlotRepository;
        this.basketTypeRepository = basketTypeRepository;
    }

    public ReservationDTO toDTO(Reservation reservation) {
        if (reservation == null) {
            return null;
        }

        ReservationDTO dto = ReservationDTO.builder()
                .id(reservation.getId())
                .status(reservation.getStatus())
                .notes(reservation.getNotes())
                .checkedInAt(reservation.getCheckedInAt())
                .createdAt(reservation.getCreatedAt())
                .updatedAt(reservation.getUpdatedAt())
                .build();

        // Ajouter les informations de l'utilisateur
        if (reservation.getUser() != null) {
            dto.setUserId(reservation.getUser().getId());
            dto.setUserName(reservation.getUser().getFirstName() + " " + reservation.getUser().getLastName());
        }

        // Ajouter les informations du créneau
        if (reservation.getTimeSlot() != null) {
            dto.setTimeSlotId(reservation.getTimeSlot().getId());
            dto.setSlotStartTime(reservation.getTimeSlot().getStartTime());
            dto.setSlotEndTime(reservation.getTimeSlot().getEndTime());

            // Ajouter le nom de l'événement si disponible
            if (reservation.getTimeSlot().getEvent() != null) {
                dto.setEventName(reservation.getTimeSlot().getEvent().getName());
            }
        }

        // Ajouter les informations du type de panier
        if (reservation.getBasketType() != null) {
            dto.setBasketTypeId(reservation.getBasketType().getId());
            dto.setBasketTypeName(reservation.getBasketType().getName());
        }

        return dto;
    }

    public Reservation toEntity(ReservationDTO dto) {
        if (dto == null) {
            return null;
        }

        Reservation reservation = new Reservation();
        reservation.setId(dto.getId());
        reservation.setStatus(dto.getStatus());
        reservation.setNotes(dto.getNotes());
        reservation.setCheckedInAt(dto.getCheckedInAt());

        // Récupérer l'utilisateur
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(reservation::setUser);
        }

        // Récupérer le créneau
        if (dto.getTimeSlotId() != null) {
            Optional<TimeSlot> timeSlot = timeSlotRepository.findById(dto.getTimeSlotId());
            timeSlot.ifPresent(reservation::setTimeSlot);
        }

        // Récupérer le type de panier
        if (dto.getBasketTypeId() != null) {
            Optional<BasketType> basketType = basketTypeRepository.findById(dto.getBasketTypeId());
            basketType.ifPresent(reservation::setBasketType);
        }

        return reservation;
    }

    public void updateEntityFromDTO(ReservationDTO dto, Reservation reservation) {
        if (dto == null || reservation == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getStatus() != null) reservation.setStatus(dto.getStatus());
        if (dto.getNotes() != null) reservation.setNotes(dto.getNotes());
        if (dto.getCheckedInAt() != null) reservation.setCheckedInAt(dto.getCheckedInAt());

        // Mettre à jour l'utilisateur
        if (dto.getUserId() != null) {
            Optional<User> user = userRepository.findById(dto.getUserId());
            user.ifPresent(reservation::setUser);
        }

        // Mettre à jour le créneau
        if (dto.getTimeSlotId() != null) {
            Optional<TimeSlot> timeSlot = timeSlotRepository.findById(dto.getTimeSlotId());
            timeSlot.ifPresent(reservation::setTimeSlot);
        }

        // Mettre à jour le type de panier
        if (dto.getBasketTypeId() != null) {
            Optional<BasketType> basketType = basketTypeRepository.findById(dto.getBasketTypeId());
            basketType.ifPresent(reservation::setBasketType);
        }
    }
}
