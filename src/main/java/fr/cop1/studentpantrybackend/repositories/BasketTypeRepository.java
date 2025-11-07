package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.BasketType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BasketTypeRepository extends JpaRepository<BasketType, Long> {
    List<BasketType> findByIsActive(Boolean isActive);
    BasketType findByName(String name);
    List<BasketType> findByNameContainingIgnoreCase(String keyword);
}
