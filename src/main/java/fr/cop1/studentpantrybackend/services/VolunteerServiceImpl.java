package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.*;
import fr.cop1.studentpantrybackend.entities.*;
import fr.cop1.studentpantrybackend.enums.NotificationType;
import fr.cop1.studentpantrybackend.enums.RegistrationStatus;
import fr.cop1.studentpantrybackend.enums.RoleType;
import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.exceptions.*;
import fr.cop1.studentpantrybackend.mappers.*;
import fr.cop1.studentpantrybackend.repositories.*;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class VolunteerServiceImpl  implements VolunteerService {

    private final VolunteerShiftRepository shiftRepository;
    private final VolunteerRegistrationRepository registrationRepository;
    private final VolunteerAvailabilityRepository availabilityRepository;
    private final UserRepository userRepository;
    private final TimeSlotRepository timeSlotRepository;
    private final EventRepository eventRepository;

    private final VolunteerShiftMapper shiftMapper;
    private final VolunteerRegistrationMapper registrationMapper;
    private final VolunteerAvailabilityMapper availabilityMapper;

    private final NotificationService notificationService;

    // Méthodes pour la gestion des postes bénévoles (shifts)

    @Override
    public VolunteerShiftDTO findShiftById(Long id) throws ResourceNotFoundException {
        VolunteerShift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poste bénévole non trouvé avec l'id: " + id));
        return shiftMapper.toDTO(shift);
    }

    @Override
    public List<VolunteerShiftDTO> findAllShifts() {
        return shiftRepository.findAll().stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VolunteerShiftDTO> findAllShifts(Pageable pageable) {
        return shiftRepository.findAll(pageable)
                .map(shiftMapper::toDTO);
    }

    @Override
    public VolunteerShiftDTO saveShift(VolunteerShiftDTO shiftDTO) throws ResourceNotFoundException {
        // Vérifier si le créneau existe
        TimeSlot timeSlot = null;
        if (shiftDTO.getTimeSlotId() != null) {
            timeSlot = timeSlotRepository.findById(shiftDTO.getTimeSlotId())
                    .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + shiftDTO.getTimeSlotId()));
        }

        // Convertir DTO en entité
        VolunteerShift shift = shiftMapper.toEntity(shiftDTO);

        // Définir l'horodatage
        shift.setCreatedAt(LocalDateTime.now());

        // Enregistrer le poste
        VolunteerShift savedShift = shiftRepository.save(shift);

        return shiftMapper.toDTO(savedShift);
    }

    @Override
    public VolunteerShiftDTO updateShift(Long id, VolunteerShiftDTO shiftDTO) throws ResourceNotFoundException {
        VolunteerShift existingShift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poste bénévole non trouvé avec l'id: " + id));

        // Mettre à jour avec les données du DTO
        shiftMapper.updateEntityFromDTO(shiftDTO, existingShift);

        // Enregistrer les modifications
        VolunteerShift updatedShift = shiftRepository.save(existingShift);

        return shiftMapper.toDTO(updatedShift);
    }

    @Override
    public void deleteShift(Long id) throws ResourceNotFoundException {
        VolunteerShift shift = shiftRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Poste bénévole non trouvé avec l'id: " + id));

        // Vérifier s'il y a des inscriptions
        if (shift.getRegistrations() != null && !shift.getRegistrations().isEmpty()) {
            throw new ValidationException("Impossible de supprimer un poste qui a des inscriptions");
        }

        shiftRepository.delete(shift);
    }

    @Override
    public List<VolunteerShiftDTO> findShiftsByTimeSlotId(Long timeSlotId) throws ResourceNotFoundException {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        return shiftRepository.findByTimeSlot(timeSlot).stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolunteerShiftDTO> findShiftsByEventId(Long eventId) {
        return shiftRepository.findByEventId(eventId).stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolunteerShiftDTO> findShiftsByRoleType(RoleType roleType) {
        return shiftRepository.findByRoleType(roleType).stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolunteerShiftDTO> findUnfilledShifts() {
        return shiftRepository.findUnfilledShifts().stream()
                .map(shiftMapper::toDTO)
                .collect(Collectors.toList());
    }

    // Méthodes pour la gestion des inscriptions bénévoles

    @Override
    public VolunteerRegistrationDTO findRegistrationById(Long id) throws ResourceNotFoundException {
        VolunteerRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription bénévole non trouvée avec l'id: " + id));
        return registrationMapper.toDTO(registration);
    }

    @Override
    public List<VolunteerRegistrationDTO> findAllRegistrations() {
        return registrationRepository.findAll().stream()
                .map(registrationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<VolunteerRegistrationDTO> findAllRegistrations(Pageable pageable) {
        return registrationRepository.findAll(pageable)
                .map(registrationMapper::toDTO);
    }

    @Override
    public VolunteerRegistrationDTO saveRegistration(VolunteerRegistrationDTO registrationDTO) throws ResourceNotFoundException {
        // Vérifier si les entités référencées existent
        VolunteerShift shift = null;
        User user = null;

        if (registrationDTO.getVolunteerShiftId() != null) {
            shift = shiftRepository.findById(registrationDTO.getVolunteerShiftId())
                    .orElseThrow(() -> new ResourceNotFoundException("Poste bénévole non trouvé avec l'id: " + registrationDTO.getVolunteerShiftId()));
        }

        if (registrationDTO.getUserId() != null) {
            user = userRepository.findById(registrationDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + registrationDTO.getUserId()));
        }

        // Vérifier s'il y a déjà une inscription pour cet utilisateur et ce poste
        if (user != null && shift != null) {
            Optional<VolunteerRegistration> existingRegistration = registrationRepository.findByUserAndVolunteerShift(user, shift);
            if (existingRegistration.isPresent()) {
                throw new ValidationException("L'utilisateur est déjà inscrit à ce poste");
            }
        }

        // Convertir DTO en entité
        VolunteerRegistration registration = registrationMapper.toEntity(registrationDTO);

        // Définir le statut initial si non spécifié
        if (registration.getStatus() == null) {
            registration.setStatus(RegistrationStatus.CONFIRMED);
        }

        // Définir les horodatages
        LocalDateTime now = LocalDateTime.now();
        registration.setCreatedAt(now);
        registration.setUpdatedAt(now);

        // Enregistrer l'inscription
        VolunteerRegistration savedRegistration = registrationRepository.save(registration);

        return registrationMapper.toDTO(savedRegistration);
    }

    @Override
    public VolunteerRegistrationDTO updateRegistration(Long id, VolunteerRegistrationDTO registrationDTO) throws ResourceNotFoundException {
        VolunteerRegistration existingRegistration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription bénévole non trouvée avec l'id: " + id));

        // Vérifier si l'inscription peut être modifiée
        if (existingRegistration.getStatus() == RegistrationStatus.COMPLETED) {
            throw new ValidationException("Impossible de modifier une inscription terminée");
        }

        // Mettre à jour avec les données du DTO
        registrationMapper.updateEntityFromDTO(registrationDTO, existingRegistration);

        // Mettre à jour l'horodatage
        existingRegistration.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        VolunteerRegistration updatedRegistration = registrationRepository.save(existingRegistration);

        return registrationMapper.toDTO(updatedRegistration);
    }

    @Override
    public void deleteRegistration(Long id) throws ResourceNotFoundException {
        VolunteerRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription bénévole non trouvée avec l'id: " + id));

        // Vérifier si l'inscription peut être supprimée
        if (registration.getStatus() == RegistrationStatus.COMPLETED) {
            throw new ValidationException("Impossible de supprimer une inscription terminée");
        }

        registrationRepository.delete(registration);
    }

    @Override
    public List<VolunteerRegistrationDTO> findRegistrationsByUserId(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return registrationRepository.findByUser(user).stream()
                .map(registrationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolunteerRegistrationDTO> findRegistrationsByShiftId(Long shiftId) throws ResourceNotFoundException {
        VolunteerShift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Poste bénévole non trouvé avec l'id: " + shiftId));

        return registrationRepository.findByVolunteerShift(shift).stream()
                .map(registrationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolunteerRegistrationDTO> findRegistrationsByEventId(Long eventId) {
        return registrationRepository.findByEventId(eventId).stream()
                .map(registrationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolunteerRegistrationDTO> findRegistrationsByStatus(RegistrationStatus status) {
        return registrationRepository.findByStatus(status).stream()
                .map(registrationMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VolunteerRegistrationDTO registerVolunteer(Long userId, Long shiftId, Boolean isTeamLeader, String notes) throws ResourceNotFoundException {
        // Récupérer les entités
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        VolunteerShift shift = shiftRepository.findById(shiftId)
                .orElseThrow(() -> new ResourceNotFoundException("Poste bénévole non trouvé avec l'id: " + shiftId));

        // Vérifier si l'utilisateur peut s'inscrire
        if (user.getRole() != UserRole.VOLUNTEER && user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.MANAGER) {
            throw new ValidationException("Seuls les bénévoles, managers et administrateurs peuvent s'inscrire");
        }

        // Vérifier s'il y a des places disponibles
        if (shift.getAvailableVolunteerSpots() <= 0) {
            throw new ValidationException("Plus de place disponible pour ce poste");
        }

        // Vérifier si l'utilisateur n'est pas déjà inscrit
        if (registrationRepository.findByUserAndVolunteerShift(user, shift).isPresent()) {
            throw new ValidationException("Vous êtes déjà inscrit à ce poste");
        }

        // Créer l'inscription
        VolunteerRegistration registration = new VolunteerRegistration();
        registration.setUser(user);
        registration.setVolunteerShift(shift);
        registration.setStatus(RegistrationStatus.CONFIRMED);
        registration.setIsTeamLeader(isTeamLeader != null ? isTeamLeader : false);
        registration.setNotes(notes);
        registration.setCreatedAt(LocalDateTime.now());
        registration.setUpdatedAt(LocalDateTime.now());

        // Enregistrer l'inscription
        VolunteerRegistration savedRegistration = registrationRepository.save(registration);

        // Envoyer une notification de confirmation
        notificationService.sendNotification(
                user.getId(),
                NotificationType.VOLUNTEER_CONFIRMATION,
                "Votre inscription bénévole pour l'événement " + shift.getTimeSlot().getEvent().getName() +
                        " le " + shift.getTimeSlot().getStartTime() + " a été confirmée.");

        return registrationMapper.toDTO(savedRegistration);
    }

    @Override
    public VolunteerRegistrationDTO cancelRegistration(Long id, String reason) throws ResourceNotFoundException {
        VolunteerRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription bénévole non trouvée avec l'id: " + id));

        // Vérifier si l'inscription peut être annulée
        if (registration.getStatus() != RegistrationStatus.CONFIRMED) {
            throw new ValidationException("Seules les inscriptions confirmées peuvent être annulées");
        }

        // Vérifier si l'événement a déjà commencé
        if (registration.getVolunteerShift().getTimeSlot().getStartTime().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Impossible d'annuler une inscription pour un événement déjà commencé");
        }

        // Mettre à jour l'inscription
        registration.setStatus(RegistrationStatus.CANCELLED);
        registration.setNotes(reason);
        registration.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        VolunteerRegistration updatedRegistration = registrationRepository.save(registration);

        return registrationMapper.toDTO(updatedRegistration);
    }

    @Override
    public VolunteerRegistrationDTO checkInRegistration(Long id) throws ResourceNotFoundException {
        VolunteerRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription bénévole non trouvée avec l'id: " + id));

        // Vérifier si l'inscription peut être utilisée
        if (registration.getStatus() != RegistrationStatus.CONFIRMED) {
            throw new ValidationException("Seules les inscriptions confirmées peuvent être utilisées");
        }

        // Marquer l'inscription comme utilisée
        registration.checkIn();

        // Enregistrer les modifications
        VolunteerRegistration updatedRegistration = registrationRepository.save(registration);

        return registrationMapper.toDTO(updatedRegistration);
    }

    @Override
    public VolunteerRegistrationDTO completeRegistration(Long id) throws ResourceNotFoundException {
        VolunteerRegistration registration = registrationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Inscription bénévole non trouvée avec l'id: " + id));

        // Vérifier si l'inscription peut être marquée comme terminée
        if (registration.getStatus() != RegistrationStatus.CONFIRMED) {
            throw new ValidationException("Seules les inscriptions confirmées peuvent être marquées comme terminées");
        }

        // Vérifier si le bénévole s'est présenté
        if (registration.getCheckedInAt() == null) {
            throw new ValidationException("Le bénévole doit d'abord être enregistré comme présent");
        }

        // Marquer l'inscription comme terminée
        registration.complete();

        // Enregistrer les modifications
        VolunteerRegistration updatedRegistration = registrationRepository.save(registration);

        return registrationMapper.toDTO(updatedRegistration);
    }

    // Méthodes pour la gestion des disponibilités récurrentes

    @Override
    public VolunteerAvailabilityDTO findAvailabilityById(Long id) throws ResourceNotFoundException {
        VolunteerAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'id: " + id));
        return availabilityMapper.toDTO(availability);
    }

    @Override
    public List<VolunteerAvailabilityDTO> findAllAvailabilities() {
        return availabilityRepository.findAll().stream()
                .map(availabilityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<VolunteerAvailabilityDTO> findAvailabilitiesByUserId(Long userId) throws ResourceNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + userId));

        return availabilityRepository.findByUser(user).stream()
                .map(availabilityMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public VolunteerAvailabilityDTO saveAvailability(VolunteerAvailabilityDTO availabilityDTO) throws ResourceNotFoundException {
        // Vérifier si l'utilisateur existe
        User user = null;
        if (availabilityDTO.getUserId() != null) {
            user = userRepository.findById(availabilityDTO.getUserId())
                    .orElseThrow(() -> new ResourceNotFoundException("Utilisateur non trouvé avec l'id: " + availabilityDTO.getUserId()));
        }

        // Vérifier si l'utilisateur est un bénévole
        if (user != null && user.getRole() != UserRole.VOLUNTEER && user.getRole() != UserRole.ADMIN && user.getRole() != UserRole.MANAGER) {
            throw new ValidationException("Seuls les bénévoles, managers et administrateurs peuvent définir des disponibilités");
        }

        // Vérifier la validité des heures
        if (availabilityDTO.getStartTime() != null && availabilityDTO.getEndTime() != null &&
                availabilityDTO.getStartTime().isAfter(availabilityDTO.getEndTime())) {
            throw new ValidationException("L'heure de début doit être avant l'heure de fin");
        }

        // Convertir DTO en entité
        VolunteerAvailability availability = availabilityMapper.toEntity(availabilityDTO);

        // Définir l'état initial si non spécifié
        if (availability.getIsActive() == null) {
            availability.setIsActive(true);
        }

        // Définir l'horodatage
        availability.setCreatedAt(LocalDateTime.now());

        // Enregistrer la disponibilité
        VolunteerAvailability savedAvailability = availabilityRepository.save(availability);

        return availabilityMapper.toDTO(savedAvailability);
    }

    @Override
    public VolunteerAvailabilityDTO updateAvailability(Long id, VolunteerAvailabilityDTO availabilityDTO) throws ResourceNotFoundException {
        VolunteerAvailability existingAvailability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'id: " + id));

        // Vérifier la validité des heures
        if (availabilityDTO.getStartTime() != null && availabilityDTO.getEndTime() != null &&
                availabilityDTO.getStartTime().isAfter(availabilityDTO.getEndTime())) {
            throw new ValidationException("L'heure de début doit être avant l'heure de fin");
        }

        // Mettre à jour avec les données du DTO
        availabilityMapper.updateEntityFromDTO(availabilityDTO, existingAvailability);

        // Enregistrer les modifications
        VolunteerAvailability updatedAvailability = availabilityRepository.save(existingAvailability);

        return availabilityMapper.toDTO(updatedAvailability);
    }

    @Override
    public void deleteAvailability(Long id) throws ResourceNotFoundException {
        VolunteerAvailability availability = availabilityRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Disponibilité non trouvée avec l'id: " + id));

        availabilityRepository.delete(availability);
    }

    @Override
    public List<Long> findAvailableVolunteersForTimeSlot(Long timeSlotId) throws ResourceNotFoundException {
        TimeSlot timeSlot = timeSlotRepository.findById(timeSlotId)
                .orElseThrow(() -> new ResourceNotFoundException("Créneau non trouvé avec l'id: " + timeSlotId));

        // Récupérer le jour de la semaine (1-7 où 1=Lundi)
        DayOfWeek dayOfWeek = timeSlot.getStartTime().getDayOfWeek();
        int dayValue = dayOfWeek.getValue(); // 1=Lundi, 7=Dimanche

        // Récupérer l'heure de début et de fin
        LocalTime startTime = timeSlot.getStartTime().toLocalTime();
        LocalTime endTime = timeSlot.getEndTime().toLocalTime();

        // Trouver les bénévoles disponibles pour ce créneau
        List<User> availableVolunteers = availabilityRepository.findAvailableVolunteers(dayValue, startTime);

        // Retourner la liste des IDs des bénévoles disponibles
        return availableVolunteers.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Override
    public Long countVolunteersByEvent(Long eventId) throws ResourceNotFoundException {
        // Vérifier si l'événement existe
        eventRepository.findById(eventId)
                .orElseThrow(() -> new ResourceNotFoundException("Événement non trouvé avec l'id: " + eventId));

        // Trouver toutes les inscriptions pour cet événement
        List<VolunteerRegistration> registrations = registrationRepository.findByEventId(eventId);

        // Compter le nombre de bénévoles uniques
        Set<Long> uniqueVolunteers = new HashSet<>();
        for (VolunteerRegistration registration : registrations) {
            if (registration.getStatus() != RegistrationStatus.CANCELLED) {
                uniqueVolunteers.add(registration.getUser().getId());
            }
        }

        return (long) uniqueVolunteers.size();
    }

    @Override
    public Long countRegistrationsByStatus(RegistrationStatus status) {
        return (long) registrationRepository.findByStatus(status).size();
    }

    @Override
    public Map<String, Long> getRegistrationStatsByRoleType() {
        Map<String, Long> stats = new HashMap<>();

        for (RoleType roleType : RoleType.values()) {
            List<VolunteerShift> shifts = shiftRepository.findByRoleType(roleType);

            long registrationCount = 0;
            for (VolunteerShift shift : shifts) {
                List<VolunteerRegistration> registrations = registrationRepository.findByVolunteerShift(shift);
                registrationCount += registrations.size();
            }

            stats.put(roleType.toString(), registrationCount);
        }

        return stats;
    }

    @Override
    public List<Object[]> findMostActiveVolunteers(int limit) {
        List<Object[]> mostActive = registrationRepository.findMostActiveVolunteers();

        // Limiter le nombre de résultats
        if (mostActive.size() > limit) {
            mostActive = mostActive.subList(0, limit);
        }

        return mostActive;
    }
}