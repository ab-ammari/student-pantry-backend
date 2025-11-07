package fr.cop1.studentpantrybackend.services;

import fr.cop1.studentpantrybackend.dtos.*;
import fr.cop1.studentpantrybackend.entities.BasketType;
import fr.cop1.studentpantrybackend.entities.Inventory;
import fr.cop1.studentpantrybackend.exceptions.ResourceNotFoundException;
import fr.cop1.studentpantrybackend.mappers.*;
import fr.cop1.studentpantrybackend.repositories.*;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class InventoryServiceImpl implements InventoryService {

    private final InventoryRepository inventoryRepository;
    private final BasketTypeRepository basketTypeRepository;
    private final InventoryMapper inventoryMapper;

    private static final Integer DEFAULT_LOW_STOCK_THRESHOLD = 10;

    @Override
    public InventoryDTO findById(Long id) throws ResourceNotFoundException {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id: " + id));
        return inventoryMapper.toDTO(inventory);
    }

    @Override
    public List<InventoryDTO> findAll() {
        return inventoryRepository.findAll().stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public Page<InventoryDTO> findAll(Pageable pageable) {
        return inventoryRepository.findAll(pageable)
                .map(inventoryMapper::toDTO);
    }

    @Override
    public InventoryDTO save(InventoryDTO inventoryDTO) throws ResourceNotFoundException {
        // Vérifier si le type de panier existe
        if (inventoryDTO.getBasketTypeId() != null) {
            basketTypeRepository.findById(inventoryDTO.getBasketTypeId())
                    .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + inventoryDTO.getBasketTypeId()));
        }

        // Convertir DTO en entité
        Inventory inventory = inventoryMapper.toEntity(inventoryDTO);

        // Définir les horodatages
        LocalDateTime now = LocalDateTime.now();
        inventory.setCreatedAt(now);
        inventory.setUpdatedAt(now);

        // Enregistrer le produit
        Inventory savedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toDTO(savedInventory);
    }

    @Override
    public InventoryDTO update(Long id, InventoryDTO inventoryDTO) throws ResourceNotFoundException {
        Inventory existingInventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id: " + id));

        // Mettre à jour avec les données du DTO
        inventoryMapper.updateEntityFromDTO(inventoryDTO, existingInventory);

        // Mettre à jour l'horodatage
        existingInventory.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        Inventory updatedInventory = inventoryRepository.save(existingInventory);

        return inventoryMapper.toDTO(updatedInventory);
    }

    @Override
    public void delete(Long id) throws ResourceNotFoundException {
        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id: " + id));

        inventoryRepository.delete(inventory);
    }

    @Override
    public List<InventoryDTO> findByBasketTypeId(Long basketTypeId) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(basketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + basketTypeId));

        return inventoryRepository.findByBasketType(basketType).stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> searchProducts(String keyword) {
        return inventoryRepository.findByProductNameContainingIgnoreCase(keyword).stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> findExpiredProducts() {
        return inventoryRepository.findExpiredItems().stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> findProductsExpiringBetween(LocalDate startDate, LocalDate endDate) {
        return inventoryRepository.findByExpirationDateBetween(startDate, endDate).stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public List<InventoryDTO> findLowStockProducts(Integer threshold) {
        int actualThreshold = threshold != null ? threshold : DEFAULT_LOW_STOCK_THRESHOLD;

        return inventoryRepository.findLowStockItems(actualThreshold).stream()
                .map(inventoryMapper::toDTO)
                .collect(Collectors.toList());
    }

    @Override
    public InventoryDTO addStock(Long id, Integer quantity) throws ResourceNotFoundException {
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("La quantité à ajouter doit être positive");
        }

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id: " + id));

        // Ajouter le stock
        inventory.addStock(quantity);

        // Mettre à jour l'horodatage
        inventory.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        Inventory updatedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toDTO(updatedInventory);
    }

    @Override
    public InventoryDTO removeStock(Long id, Integer quantity) throws ResourceNotFoundException {
        if (quantity == null || quantity <= 0) {
            throw new ValidationException("La quantité à retirer doit être positive");
        }

        Inventory inventory = inventoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produit non trouvé avec l'id: " + id));

        // Vérifier s'il y a assez de stock
        if (inventory.getQuantity() < quantity) {
            throw new ValidationException("Stock insuffisant: " + inventory.getQuantity() + " disponible, " + quantity + " demandé");
        }

        // Retirer le stock
        inventory.removeStock(quantity);

        // Mettre à jour l'horodatage
        inventory.setUpdatedAt(LocalDateTime.now());

        // Enregistrer les modifications
        Inventory updatedInventory = inventoryRepository.save(inventory);

        return inventoryMapper.toDTO(updatedInventory);
    }

    @Override
    public void decrementStockForBasketType(Long basketTypeId) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(basketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + basketTypeId));

        // Récupérer tous les produits pour ce type de panier
        List<Inventory> products = inventoryRepository.findByBasketType(basketType);

        // Décrémenter le stock de chaque produit
        for (Inventory product : products) {
            if (product.getQuantity() > 0) {
                product.removeStock(1);
                product.setUpdatedAt(LocalDateTime.now());
                inventoryRepository.save(product);
            }
        }
    }

    @Override
    public Integer getTotalStockForBasketType(Long basketTypeId) throws ResourceNotFoundException {
        BasketType basketType = basketTypeRepository.findById(basketTypeId)
                .orElseThrow(() -> new ResourceNotFoundException("Type de panier non trouvé avec l'id: " + basketTypeId));

        Integer totalStock = inventoryRepository.getTotalStockForBasketType(basketType);

        return totalStock != null ? totalStock : 0;
    }

    @Override
    public Map<String, Integer> getStockLevelsByBasketType() {
        Map<String, Integer> stockLevels = new HashMap<>();

        List<BasketType> basketTypes = basketTypeRepository.findAll();

        for (BasketType basketType : basketTypes) {
            Integer totalStock = inventoryRepository.getTotalStockForBasketType(basketType);
            stockLevels.put(basketType.getName(), totalStock != null ? totalStock : 0);
        }

        return stockLevels;
    }

    @Override
    public Map<String, Long> getExpiringProductCounts(int daysThreshold) {
        Map<String, Long> expiringCounts = new HashMap<>();

        LocalDate thresholdDate = LocalDate.now().plusDays(daysThreshold);
        List<Inventory> expiringProducts = inventoryRepository.findByExpirationDateBetween(LocalDate.now(), thresholdDate);

        // Regrouper par type de panier
        Map<BasketType, List<Inventory>> groupedByBasketType = expiringProducts.stream()
                .collect(Collectors.groupingBy(Inventory::getBasketType));

        for (Map.Entry<BasketType, List<Inventory>> entry : groupedByBasketType.entrySet()) {
            expiringCounts.put(entry.getKey().getName(), (long) entry.getValue().size());
        }

        return expiringCounts;
    }
}