package fr.cop1.studentpantrybackend.entities;

import fr.cop1.studentpantrybackend.enums.ExperienceLevel;
import fr.cop1.studentpantrybackend.enums.RegistrationStatus;
import fr.cop1.studentpantrybackend.enums.RoleType;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "volunteer_shifts")
@Data
@NoArgsConstructor
public class VolunteerShift {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "role_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private RoleType roleType;

    @Column(name = "required_volunteers", nullable = false)
    private int requiredVolunteers = 1;

    @Column(name = "min_experience_level")
    @Enumerated(EnumType.STRING)
    private ExperienceLevel minExperienceLevel;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "start_time")
    private LocalTime startTime;

    @Column(name = "end_time")
    private LocalTime endTime;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relations
    @OneToMany(mappedBy = "volunteerShift", cascade = CascadeType.ALL)
    private Set<VolunteerRegistration> registrations = new HashSet<>();
    // Méthodes utilitaires
    public void addRegistration(VolunteerRegistration registration) {
        registrations.add(registration);
        registration.setVolunteerShift(this);
    }

    public void removeRegistration(VolunteerRegistration registration) {
        registrations.remove(registration);
        registration.setVolunteerShift(null);
    }

    // Obtenir le nombre de places restantes pour les bénévoles
    public int getAvailableVolunteerSpots() {
        long confirmedRegistrations = registrations.stream()
                .filter(r -> r.getStatus() == RegistrationStatus.CONFIRMED
                        || r.getStatus() == RegistrationStatus.COMPLETED)
                .count();

        return Math.max(0, this.requiredVolunteers - (int)confirmedRegistrations);
    }
}
