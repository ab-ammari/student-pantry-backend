package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.InventoryDTO;
import fr.cop1.studentpantrybackend.entities.BasketType;
import fr.cop1.studentpantrybackend.entities.Inventory;
import fr.cop1.studentpantrybackend.repositories.BasketTypeRepository;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class InventoryMapper {

    private final BasketTypeRepository basketTypeRepository;

    public InventoryMapper(BasketTypeRepository basketTypeRepository) {
        this.basketTypeRepository = basketTypeRepository;
    }

    public InventoryDTO toDTO(Inventory inventory) {
        if (inventory == null) {
            return null;
        }

        InventoryDTO dto = InventoryDTO.builder()
                .id(inventory.getId())
                .productName(inventory.getProductName())
                .quantity(inventory.getQuantity())
                .expirationDate(inventory.getExpirationDate())
                .createdAt(inventory.getCreatedAt())
                .updatedAt(inventory.getUpdatedAt())
                .build();

        // Ajouter les informations du type de panier
        if (inventory.getBasketType() != null) {
            dto.setBasketTypeId(inventory.getBasketType().getId());
            dto.setBasketTypeName(inventory.getBasketType().getName());
        }

        return dto;
    }

    public Inventory toEntity(InventoryDTO dto) {
        if (dto == null) {
            return null;
        }

        Inventory inventory = new Inventory();
        inventory.setId(dto.getId());
        inventory.setProductName(dto.getProductName());
        inventory.setQuantity(dto.getQuantity());
        inventory.setExpirationDate(dto.getExpirationDate());

        // Récupérer le type de panier
        if (dto.getBasketTypeId() != null) {
            Optional<BasketType> basketType = basketTypeRepository.findById(dto.getBasketTypeId());
            basketType.ifPresent(inventory::setBasketType);
        }

        return inventory;
    }

    public void updateEntityFromDTO(InventoryDTO dto, Inventory inventory) {
        if (dto == null || inventory == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getProductName() != null) inventory.setProductName(dto.getProductName());
        if (dto.getQuantity() != null) inventory.setQuantity(dto.getQuantity());
        if (dto.getExpirationDate() != null) inventory.setExpirationDate(dto.getExpirationDate());

        // Mettre à jour le type de panier
        if (dto.getBasketTypeId() != null) {
            Optional<BasketType> basketType = basketTypeRepository.findById(dto.getBasketTypeId());
            basketType.ifPresent(inventory::setBasketType);
        }
    }
}
