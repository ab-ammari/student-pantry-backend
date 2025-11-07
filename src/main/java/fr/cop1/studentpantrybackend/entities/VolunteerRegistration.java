package fr.cop1.studentpantrybackend.entities;

import fr.cop1.studentpantrybackend.enums.RegistrationStatus;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "volunteer_registrations", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"volunteer_shift_id", "user_id"})
})
@Data
@NoArgsConstructor
public class VolunteerRegistration {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "volunteer_shift_id", nullable = false)
    private VolunteerShift volunteerShift;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private RegistrationStatus status = RegistrationStatus.CONFIRMED;

    @Column(name = "is_team_leader")
    private Boolean isTeamLeader = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "checked_in_at")
    private LocalDateTime checkedInAt;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt = LocalDateTime.now();

    // Méthodes utilitaires
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public void checkIn() {
        this.checkedInAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    public void complete() {
        this.status = RegistrationStatus.COMPLETED;
        this.updatedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = RegistrationStatus.CANCELLED;
        this.updatedAt = LocalDateTime.now();
    }
}
