package fr.cop1.studentpantrybackend.mappers;

import fr.cop1.studentpantrybackend.dtos.BasketTypeDTO;
import fr.cop1.studentpantrybackend.entities.BasketType;
import org.springframework.stereotype.Component;

@Component
public class BasketTypeMapper {

    private final InventoryMapper inventoryMapper;

    public BasketTypeMapper(InventoryMapper inventoryMapper) {
        this.inventoryMapper = inventoryMapper;
    }

    public BasketTypeDTO toDTO(BasketType basketType) {
        if (basketType == null) {
            return null;
        }

        BasketTypeDTO dto = BasketTypeDTO.builder()
                .id(basketType.getId())
                .name(basketType.getName())
                .description(basketType.getDescription())
                .isActive(basketType.isActive())
                .createdAt(basketType.getCreatedAt())
                .build();

        return dto;
    }

    // Surcharge pour inclure les inventaires si nécessaire
    public BasketTypeDTO toDTOWithInventory(BasketType basketType) {
        BasketTypeDTO dto = toDTO(basketType);

        if (dto == null || basketType.getInventoryItems() == null) {
            return dto;
        }

        // Ajouter les éléments d'inventaire
        basketType.getInventoryItems().forEach(item ->
                dto.getInventoryItems().add(inventoryMapper.toDTO(item))
        );

        return dto;
    }

    public BasketType toEntity(BasketTypeDTO dto) {
        if (dto == null) {
            return null;
        }

        BasketType basketType = new BasketType();
        basketType.setId(dto.getId());
        basketType.setName(dto.getName());
        basketType.setDescription(dto.getDescription());
        basketType.setActive(dto.getIsActive());
        return basketType;
    }

    public void updateEntityFromDTO(BasketTypeDTO dto, BasketType basketType) {
        if (dto == null || basketType == null) {
            return;
        }

        // Ne pas modifier l'ID
        if (dto.getName() != null) basketType.setName(dto.getName());
        if (dto.getDescription() != null) basketType.setDescription(dto.getDescription());
        if (dto.getIsActive() != null) basketType.setActive(dto.getIsActive());
    }
}