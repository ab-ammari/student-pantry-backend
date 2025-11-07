package fr.cop1.studentpantrybackend.entities;

import fr.cop1.studentpantrybackend.enums.UserRole;
import fr.cop1.studentpantrybackend.enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Data
@NoArgsConstructor @AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name="first_name", nullable = false)
    private String firstName;

    @Column(name="last_name", nullable = false)
    private String lastName;

    private String phone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserRole role;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    private String school;

    @Column(name = "student_id_verified")
    private boolean studentIdVerified = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name ="last_login")
    private LocalDateTime lastLogin;

    // Relations
    @OneToMany(mappedBy = "user")
    private Set<Reservation> reservations = new HashSet<>();


    @OneToMany(mappedBy = "user")
    private Set<VolunteerRegistration> volunteerRegistrations = new HashSet<>();

    @OneToMany(mappedBy = "user")
    private Set<VolunteerAvailability> volunteerAvailabilities = new HashSet<>();

    @OneToMany(mappedBy = "createdBy")
    private Set<Event> eventsCreated = new HashSet<>();
}
