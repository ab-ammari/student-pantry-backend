package fr.cop1.studentpantrybackend.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "basket_types")
@Data
@NoArgsConstructor @Getter @Setter
public class BasketType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "is_active")
    private boolean isActive = true;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Relations
    @OneToMany(mappedBy = "basketType")
    private Set<Reservation> reservations = new HashSet<>();

    @OneToMany(mappedBy = "basketType")
    private Set<Inventory> inventoryItems = new HashSet<>();
}
