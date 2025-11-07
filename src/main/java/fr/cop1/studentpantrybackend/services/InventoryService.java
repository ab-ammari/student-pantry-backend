package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.*;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface InventoryService {

    // Opérations CRUD de base
    InventoryDTO findById(Long id) throws ResourceNotFoundException;
    List<InventoryDTO> findAll();
    Page<InventoryDTO> findAll(Pageable pageable);
    InventoryDTO save(InventoryDTO inventoryDTO) throws ResourceNotFoundException;
    InventoryDTO update(Long id, InventoryDTO inventoryDTO) throws ResourceNotFoundException;
    void delete(Long id) throws ResourceNotFoundException;

    // Méthodes spécifiques
    List<InventoryDTO> findByBasketTypeId(Long basketTypeId) throws ResourceNotFoundException;
    List<InventoryDTO> searchProducts(String keyword);
    List<InventoryDTO> findExpiredProducts();
    List<InventoryDTO> findProductsExpiringBetween(LocalDate startDate, LocalDate endDate);
    List<InventoryDTO> findLowStockProducts(Integer threshold);

    // Méthodes de gestion des stocks
    InventoryDTO addStock(Long id, Integer quantity) throws ResourceNotFoundException;
    InventoryDTO removeStock(Long id, Integer quantity) throws ResourceNotFoundException;
    void decrementStockForBasketType(Long basketTypeId) throws ResourceNotFoundException;

    // Statistiques
    Integer getTotalStockForBasketType(Long basketTypeId) throws ResourceNotFoundException;
    Map<String, Integer> getStockLevelsByBasketType();
    Map<String, Long> getExpiringProductCounts(int daysThreshold);
}