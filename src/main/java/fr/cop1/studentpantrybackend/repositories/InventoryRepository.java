package fr.cop1.studentpantrybackend.repositories;

import fr.cop1.studentpantrybackend.entities.BasketType;
import fr.cop1.studentpantrybackend.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
    List<Inventory> findByBasketType(BasketType basketType);
    List<Inventory> findByProductNameContainingIgnoreCase(String keyword);
    List<Inventory> findByExpirationDateBefore(LocalDate date);
    List<Inventory> findByExpirationDateBetween(LocalDate startDate, LocalDate endDate);

    // Recherche des produits avec stock faible
    @Query("SELECT i FROM Inventory i WHERE i.quantity < ?1")
    List<Inventory> findLowStockItems(Integer threshold);

    // Recherche des produits expirés
    @Query("SELECT i FROM Inventory i WHERE i.expirationDate < CURRENT_DATE")
    List<Inventory> findExpiredItems();

    // Obtenir le stock total pour un type de panier
    @Query("SELECT SUM(i.quantity) FROM Inventory i WHERE i.basketType = ?1")
    Integer getTotalStockForBasketType(BasketType basketType);
}